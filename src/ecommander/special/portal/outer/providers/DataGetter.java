package ecommander.special.portal.outer.providers;

import com.sun.org.apache.xpath.internal.operations.Number;
import ecommander.controllers.AppContext;
import ecommander.fwk.*;
import ecommander.model.Compare;
import ecommander.model.Item;
import ecommander.model.User;
import ecommander.model.datatypes.DecimalDataType;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.itemquery.fulltext.FulltextQueryCreatorRegistry;
import ecommander.special.portal.outer.Request;
import extra._generated.ItemNames;
import extra._generated.Product;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

/**
 * Получает данные по всем запросам пользователя или от сервера или из кеша.
 * Также выполняет поиск по этим запросам по локальному каталогу.
 */
public class DataGetter {

    private final String CACHE_DIR = "files/search";

    private static int HOURS_CACHE_SAVED = 24;

    private static final HashMap<String, ProviderGetter> PROVIDER_GETTERS = new HashMap<>();
    static {
        PROVIDER_GETTERS.put(Providers.FINDCHIPS, new FindchipsGetter());
        PROVIDER_GETTERS.put(Providers.OEMSECRETS, new OemsecretsGetter());
    }

    private boolean performLocalSearch; // выполнять ли локальный поиск по этому запросу (по локальному каталогу)
    private boolean forceRefreshCache;  // выполнять ли новый запрос к удаленному серверу даже при наличии кеша
    private OuterInputData input;    // данные, полученные от пользователя (фильтры и т.п.)

    public DataGetter(OuterInputData input, boolean performLocalSearch, boolean forceRefreshCache) {
        this.input = input;
        this.performLocalSearch = performLocalSearch;
        this.forceRefreshCache = forceRefreshCache;
    }

    /**
     * Выполняет все действия и возвращает результирующий XML документ
     * @return
     * @throws Exception
     */
    public XmlDocumentBuilder getQueryData() throws Exception {
        // Результат выполнения всего запроса
        XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();

        // Сначала посмотреть, что можно прочитать из кеша. То, что есть в кеше позже не запрашивать
        ArrayList<String> queries = new ArrayList<>(input.getQueries().keySet());
        boolean isBom = input.getQueries().size() > 1;
        for (String query : queries) {
            // Подготовка данных о кеше
            String cacheFileName = isBom ? createExactCacheFileName(query) : createCacheFileName(query);
            File cacheDir = new File(AppContext.getRealPath(CACHE_DIR));
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            File cacheFile = new File(AppContext.getRealPath(CACHE_DIR + '/' + cacheFileName + ".xml"));
            boolean cacheExists = cacheFile.exists();

            // Просрочен ли кеш
            boolean canUseCache = !forceRefreshCache && cacheExists;
            if (canUseCache) {
                DateTime cacheCreated = new DateTime(cacheFile.lastModified(), DateTimeZone.UTC);
                DateTime now = DateTime.now(DateTimeZone.UTC);
                canUseCache = now.isBefore(cacheCreated.plusHours(HOURS_CACHE_SAVED));
            }

            // Если запрос можно прочитать из кеша
            if (canUseCache) {
                // чтение из файла кеша
                try {
                    String cache = readCacheFile(cacheFile);
                    xml.startElement("query", "q", query, "qty", input.getQueries().get(query), "cache", HOURS_CACHE_SAVED);
                    xml.addElements(cache);
                    xml.endElement();
                    // удалить подзапрос из общего запроса, чтобы не тратить время на его выполнение на сервере
                    input.getQueries().remove(query);
                } catch (Exception e) {
                    xml.addElement("error", ExceptionUtils.getStackTrace(e), "type", "reading_cache");
                }
            }
        }

        // загрузка с сервера (если еще остались необработанные запросы)
        if (input.getQueries().size() > 0) {
            ProviderGetter getter = PROVIDER_GETTERS.get(input.getRemote());
            if (getter != null && input.getQueries().size() > 0) {
                ProviderGetter.Result result = getter.getData(input);
                // Если результат получен
                if (result.isSuccess()) {
                    for (Request.Query query : result.getRequest().getAllQueries()) {
                        // сохранить кеш (все результаты)
                        String cacheFileName = createCacheFileName(query.query);
                        File cacheFile = new File(AppContext.getRealPath(CACHE_DIR + '/' + cacheFileName + ".xml"));
                        FileUtils.write(cacheFile, query.getProcessedResult().getXmlStringSB(), StandardCharsets.UTF_8);
                        // сохранить кеш (полное соответствие). Если не было полного соответствия - сохраняется пустой файл
                        String exactXml = createJustExactMatchesXml(query.getProcessedResult().getXmlStringSB().toString());
                        cacheFileName = createExactCacheFileName(query.query);
                        cacheFile = new File(AppContext.getRealPath(CACHE_DIR + '/' + cacheFileName + ".xml"));
                        FileUtils.write(cacheFile, exactXml, StandardCharsets.UTF_8);
                        // дописать в итоговый документ
                        xml.startElement("query", "q", query.query, "qty", input.getQueries().get(query.query));
                        xml.addElements(query.getProcessedResult().getXmlStringSB());
                        xml.endElement();
                    }
                }
            }
        }

        // Поиск в локальном каталоге
        if (performLocalSearch) {
            xml.startElement("local");
            for (String query : input.getQueries().keySet()) {
                try {
                    XmlDocumentBuilder localQueryXml = performLocalSearch(query);
                    xml.addElements(localQueryXml.getXmlStringSB());
                } catch (Exception e) {
                   xml.addElement("error", ExceptionUtils.getStackTrace(e), "type", "local_search");
                }
            }
            xml.endElement(); // local
        }

        // возврат результата
        return xml;
    }


    /**
     * Прочитать данные из файла кеша
     * (при этом учесть фильтры пользователя)
     * @param cacheFile
     * @return
     */
    private String readCacheFile(File cacheFile) throws IOException {
        String xml = FileUtils.readFileToString(cacheFile, StandardCharsets.UTF_8);
        return xml;
    }

    /**
     * Выполнить поиск в локальном каталоге
     * (также учесть фильтры пользователя)
     * @return
     */
    private XmlDocumentBuilder performLocalSearch(String query) throws Exception {
        BigDecimal extraQuotient = BigDecimal.ONE; // дополнительный коэффициент для цены
        Item localCatalog = ItemUtils.ensureSingleRootAnonymousItem(ItemNames.PLAIN_CATALOG, User.getDefaultUser());
        ItemQuery localQuery = new ItemQuery(ItemNames.PRODUCT).setParentId(localCatalog.getId(), true)
                .setFulltextCriteria(FulltextQueryCreatorRegistry.DEFAULT, query, 50, null, Compare.SOME)
                .setLimit(50).addSorting(ItemNames.product_.SECTION_NAME, "ASC");
        List<Item> prods = localQuery.loadItems();
        final String NONE_DISTR = "~~@~~NONE~~@~~";
        String currentDistributor = NONE_DISTR;

        XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();
        int qty = input.getQueries().get(query);
        // <query> - открывающий
        xml.startElement("query", "q", query, "qty", qty, "local", "partnumber.ru");

        for (Item prod : prods) {
            Product p = Product.get(prod);
            String distributor = p.getDefault_section_name("partnumber.ru");
            // Если поменялось название поставщика в отсортированном списке - создать новый тэг
            if (!StringUtils.equals(distributor, currentDistributor)) {
                xml.startElement("distributor", "name", distributor);
                currentDistributor = distributor;
                Item catalogSettings = new ItemQuery(ItemNames.PRICE_CATALOG)
                        .addParameterEqualsCriteria(ItemNames.price_catalog_.NAME, distributor).loadFirstItem();
                extraQuotient = BigDecimal.ONE; // дополнительный коэффициент для цены
                if (catalogSettings != null) {
                    extraQuotient = catalogSettings.getDecimalValue(ItemNames.price_catalog_.QUOTIENT, BigDecimal.ONE);
                }
            }
            xml.startElement("product", "id", p.get_code(), "key", p.getKeyUnique());
            xml.addElement("code", Strings.translit(p.get_code()));
            xml.addElement("name", p.get_name());
            xml.addElement("vendor", p.get_vendor());
            xml.addElement("qty", p.get_qty());
            xml.addElement("step", p.get_step());
            xml.addElement("description", p.get_name_extra());
            xml.addElement("min_qty", p.get_min_qty());
            xml.addElement("next_delivery", p.get_next_delivery());
            xml.addElement("category_id", distributor);
            xml.addElement("pricebreak", "1");
            xml.addElement("currency_id", "RUB");
            xml.startElement("prices");

            xml.startElement("break", "qty", "1");
            BigDecimal priceOriginal = p.getDecimalValue(input.getPriceParamName(), BigDecimal.ZERO);
            xml.addElement("price_original", priceOriginal);
            // Применить коэффициент
            priceOriginal = priceOriginal.multiply(extraQuotient).setScale(4, RoundingMode.UP);
            input.getRates().setAllPricesXML(xml, priceOriginal, input.getCurCode());

            xml.endElement(); // break
            xml.endElement(); // prices
            xml.endElement(); // product
        }
        xml.endElement(); // distributor
        xml.endElement(); // query
        return xml;
    }


    public String applyFilters(String xml, String query) {
        Integer quantity = input.getQueries().get(query);
        boolean hasQualityFilter = input.hasVendorFilter() || input.hasShipDateFilter() || input.hasFromFilter()
                || input.hasToFilter() || input.hasDistributorFilter();
        boolean hasQuantityFilter = quantity != null && quantity > 1;
        if (!hasQualityFilter && !hasQuantityFilter)
            return xml;
        Document doc = JsoupUtils.parseXml(xml);
        int requestedQuantity = quantity == null ? 1 : quantity;
        if (hasQualityFilter) {
            Elements products = doc.getElementsByTag("product");
            for (Element product : products) {
                if (input.hasDistributorFilter()) {
                    String distributor = JsoupUtils.getSelectorFirstValue(product, "category_id");
                    if (!input.distributorFilterMatches(distributor)) {
                        product.remove();
                        continue;
                    }
                }
                if (input.hasShipDateFilter()) {
                    String shipDate = JsoupUtils.getSelectorFirstValue(product, "next_delivery");
                    if (!input.shipDateFilterMatches(shipDate)) {
                        product.remove();
                        continue;
                    }
                }
                if (input.hasVendorFilter()) {
                    String vendor = JsoupUtils.getSelectorFirstValue(product, "vendor");
                    if (!input.vendorFilterMatches(vendor)) {
                        product.remove();
                        continue;
                    }
                }
                if (input.hasFromFilter()) {
                    BigDecimal qtyPrice = getPriceForQty(product, requestedQuantity);
                    if (!input.fromPriceFilterMatches(qtyPrice)) {
                        product.remove();
                        continue;
                    }
                }
                if (input.hasToFilter()) {
                    BigDecimal qtyPrice = getPriceForQty(product, requestedQuantity);
                    if (!input.toPriceFilterMatches(qtyPrice)) {
                        product.remove();
                    }
                }
            }
        }
        if (hasQuantityFilter) {
            int firstProductIndex = doc.select("product").first().siblingIndex();
            ArrayList<Element> prods = new ArrayList<>(doc.select("product"));
            // для каждого товара найти цену, которая соответствует количеству, и записать ее в тэг prices
            for (Element prod : prods) {
                BigDecimal price = getPriceForQty(prod, requestedQuantity);
                prod.select("prices").attr("price", price.toPlainString());
            }
            prods.sort((o1, o2) -> {
                BigDecimal price1 = DecimalDataType.parse(o1.select("prices").first().attr("price"), 4);
                BigDecimal price2 = DecimalDataType.parse(o2.select("prices").first().attr("price"), 4);
                price1 = price1 == null ? BigDecimal.valueOf(Integer.MAX_VALUE) : price1;
                price2 = price2 == null ? BigDecimal.valueOf(Integer.MAX_VALUE) : price2;
                return price1.compareTo(price2);
            });
            doc.select("product").remove();
            doc.insertChildren(firstProductIndex, prods);
        }
        return JsoupUtils.outputXmlNoPrettyPrint(doc);
    }

    /**
     * Получить цену товара, которая соответствует его количеству
     * @param product
     * @param productQty
     * @return
     */
    private BigDecimal getPriceForQty(Element product, int productQty) {
        Elements breaks = product.getElementsByTag("break");
        Element currentBreak = breaks.first();
        for (Element aBreak : breaks) {
            currentBreak = aBreak;
            int breakQty = NumberUtils.toInt(aBreak.attr("qty"), 1);
            if (breakQty > productQty)
                break;
        }
        String priceStr = JsoupUtils.getTagFirstValue(currentBreak, input.getPriceParamName());
        return DecimalDataType.parse(priceStr, 4);
    }

    /**
     * Название файла для кеша одного запроса
     * @param query
     * @return
     */
    private String createCacheFileName(String query) {
        return Strings.getFileName(input.getRemote() + "__" + query);
    }

    /**
     * Название файла для кеша одного запроса и результатов с полным соответствием
     * @param query
     * @return
     */
    private String createExactCacheFileName(String query) {
        return Strings.getFileName(input.getRemote() + "__" + query + "_ex");
    }

    /**
     * Оставить в результате только полное соответствие запросу
     * Также отсортировать по цене для одного элемента (для случая, когда заказывается 1 девайс, т.е.
     * по цене первого ценового интервала)
     * @param allResultDoc
     * @return - в случае отсутствия полных совпадений возвращается пустой документ. Он тоже кешируется
     */
    private String createJustExactMatchesXml(String allResultDoc) {
        Document doc = JsoupUtils.parseXml(allResultDoc);
        doc.select("product[query_exact_match=false]").remove();
        int firstProductIndex = doc.select("product").first().siblingIndex();
        ArrayList<Element> prods = new ArrayList<>(doc.select("product"));
        prods.sort((o1, o2) -> {
            BigDecimal price1 = DecimalDataType.parse(o1.select("break").first().select("price").first().ownText(), 4);
            BigDecimal price2 = DecimalDataType.parse(o2.select("break").first().select("price").first().ownText(), 4);
            price1 = price1 == null ? BigDecimal.valueOf(Integer.MAX_VALUE) : price1;
            price2 = price2 == null ? BigDecimal.valueOf(Integer.MAX_VALUE) : price2;
            return price1.compareTo(price2);
        });
        doc.select("product").remove();
        doc.insertChildren(firstProductIndex, prods);
        boolean hasResult = doc.select("product").size() > 0;
        return hasResult ? JsoupUtils.outputXmlNoPrettyPrint(doc) : "";
    }
}
