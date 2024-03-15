package ecommander.special.portal.outer.providers;

import ecommander.controllers.AppContext;
import ecommander.fwk.ItemUtils;
import ecommander.fwk.JsoupUtils;
import ecommander.fwk.Strings;
import ecommander.fwk.XmlDocumentBuilder;
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
    private UserInput input;    // данные, полученные от пользователя (фильтры и т.п.)

    public DataGetter(UserInput input, boolean performLocalSearch, boolean forceRefreshCache) {
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
        for (String query : input.getQueries().keySet()) {
            // Подготовка данных о кеше
            String cacheFileName = createCacheFileName(query);
            File cacheDir = new File(AppContext.getRealPath(CACHE_DIR));
            if (!cacheDir.exists()) {
                cacheDir.mkdirs();
            }
            File cacheFile = new File(AppContext.getRealPath(CACHE_DIR + '/' + cacheFileName + ".xml"));
            boolean cacheExists = cacheFile.exists();

            // Просрочен ли кеш
            boolean cacheNeedsRefresh = forceRefreshCache || !cacheExists;
            if (!cacheNeedsRefresh) {
                DateTime cacheCreated = new DateTime(cacheFile.lastModified(), DateTimeZone.UTC);
                DateTime now = DateTime.now(DateTimeZone.UTC);
                cacheNeedsRefresh = now.isAfter(cacheCreated.plusHours(HOURS_CACHE_SAVED));
            }

            // Если запрос можно прочитать из кеша
            if (!cacheNeedsRefresh) {
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

        // загрузка с сервера
        ProviderGetter getter = PROVIDER_GETTERS.get(input.getRemote());
        if (getter != null && input.getQueries().size() > 0) {
            ProviderGetter.Result result = getter.getData(input, input.getRates());
            // Если результат получен
            if (result.isSuccess()) {
                for (Request.Query query : result.getRequest().getAllQueries()) {
                    String cacheFileName = createCacheFileName(query.query);
                    File cacheFile = new File(AppContext.getRealPath(CACHE_DIR + '/' + cacheFileName + ".xml"));
                    // сохранить кеш
                    FileUtils.write(cacheFile, query.getProcessedResult().getXmlStringSB(), StandardCharsets.UTF_8);
                    // дописать в итоговый документ
                    xml.addElements(query.getProcessedResult().getXmlStringSB());
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
        if (input.hasVendorFilter() || input.hasShipDateFilter() || input.hasFromFilter() || input.hasToFilter() || input.hasDistributorFilter()) {
            Document doc = JsoupUtils.parseXml(xml);
            Elements distributors = doc.getElementsByTag("distributor");
            if (input.hasDistributorFilter()) {
                for (Element distributor : distributors) {
                    String distrName = distributor.attr("name");
                    if (!input.distributorFilterMatches(distrName)) {
                        distributor.remove();
                    }
                }
            }
            if (input.hasVendorFilter() || input.hasShipDateFilter() || input.hasFromFilter() || input.hasToFilter()) {
                Elements products = doc.getElementsByTag("product");
                for (Element product : products) {
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
                        String maxPriceStr = JsoupUtils.getSelectorFirstValue(product, "max_price");
                        BigDecimal maxPrice = DecimalDataType.parse(maxPriceStr, 4);
                        if (!input.fromPriceFilterMatches(maxPrice)) {
                            product.remove();
                            continue;
                        }
                    }
                    if (input.hasToFilter()) {
                        String minPriceStr = JsoupUtils.getSelectorFirstValue(product, "min_price");
                        BigDecimal minPrice = DecimalDataType.parse(minPriceStr, 4);
                        if (!input.toPriceFilterMatches(minPrice)) {
                            product.remove();
                        }
                    }
                }
            }
            return JsoupUtils.outputXmlDoc(doc);
        }
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
        final String NONE_DISTR = "~~@~NONE~@~~";
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
            BigDecimal maxPrice = BigDecimal.ONE.negate();
            BigDecimal minPrice = BigDecimal.valueOf(Double.MAX_VALUE);
            xml.addElement("price_original", priceOriginal);
            // Применить коэффициент
            priceOriginal = priceOriginal.multiply(extraQuotient).setScale(4, RoundingMode.UP);
            HashMap<String, BigDecimal> allPricesDecimal = input.getRates().setAllPricesXML(xml, priceOriginal, input.getCurCode());
            BigDecimal currentPrice = allPricesDecimal.get(input.getPriceParamName());
            if (currentPrice != null) {
                maxPrice = currentPrice;
                minPrice = currentPrice;
                input.setGlobalMaxPrice(input.getGlobalMaxPrice().max(currentPrice));
                input.setGlobalMinPrice(input.getGlobalMinPrice().min(currentPrice));
            }
            xml.endElement(); // break
            xml.endElement(); // prices

            // Добавление элементов с максимальной и минимальной ценой (чтобы сохранилась в кеше)
            xml.addElement("max_price", maxPrice);
            xml.addElement("min_price", minPrice);
            // Если девайс не подходит по фильтрам - добавить тэг <invalid>invalid</invalid>
            boolean isInvalid
                    = !input.shipDateFilterMatches(p.get_next_delivery())
                    || !input.vendorFilterMatches(p.get_vendor())
                    || !input.distributorFilterMatches("partnumber.ru")
                    || !input.fromPriceFilterMatches(maxPrice)
                    || !input.toPriceFilterMatches(minPrice);
            if (isInvalid) {
                xml.addElement("invalid", "invalid");
            }
            xml.endElement(); // product
        }
        xml.endElement(); // distributor
        xml.endElement(); // query
        return xml;
    }

    /**
     * Название файла для кеша одного запроса
     * @param query
     * @return
     */
    private String createCacheFileName(String query) {
        return Strings.getFileName(input.getRemote() + "__" + query);
    }
}
