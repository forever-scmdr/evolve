package ecommander.special.portal.outer.providers;

import ecommander.controllers.AppContext;
import ecommander.fwk.*;
import ecommander.fwk.Timer;
import ecommander.model.Compare;
import ecommander.model.Item;
import ecommander.model.User;
import ecommander.model.datatypes.DecimalDataType;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.itemquery.fulltext.FulltextQueryCreatorRegistry;
import ecommander.special.portal.outer.Request;
import ecommander.special.portal.outer.Result;
import extra._generated.ItemNames;
import extra._generated.Product;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RegExUtils;
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
import java.util.*;

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
        boolean isBom = input.getQueries().size() > 1;
        // Если запрос BOM - удалить все запросы с нулевым количеством
        if (isBom) {
            HashSet<String> qNames = new HashSet<>(input.getQueries().keySet());
            for (String qName : qNames) {
                if (input.getQueries().get(qName) == null || input.getQueries().get(qName) <= 0)
                    input.getQueries().remove(qName);
            }
        }

        // Результат выполнения всего запроса
        LinkedHashMap<String, String> queryXmls = new LinkedHashMap<>();
        // Заполнить пустыми значениями, чтобы сохранился правильный порядок (как юзер писал)
        for (String query : input.getQueries().keySet()) {
            queryXmls.put(query, "");
        }
        XmlDocumentBuilder totalXml = XmlDocumentBuilder.newDocPart();

        // Сначала посмотреть, что можно прочитать из кеша. То, что есть в кеше позже не запрашивать
        LinkedHashMap<String, Integer> queries = new LinkedHashMap<>(input.getQueries());
        for (String query : queries.keySet()) {
            // Подготовка данных о кеше
            Timer.getTimer().start("API # find_files");
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
            Timer.getTimer().stop("API # find_files");

            // Если запрос можно прочитать из кеша
            if (canUseCache) {
                // чтение из файла кеша
                try {
                    String cache = readCacheFile(cacheFile);
                    XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();
                    xml.startElement("query", "id", createQueryUniqueId(query), "q", query,
                            "qty", input.getQueries().get(query), "cache", HOURS_CACHE_SAVED);
                    xml.addElements(cache);
                    Timer.getTimer().start("API # load_local");
                    addLocalProducts(query, xml, isBom); // также добавить локальные результаты
                    Timer.getTimer().stop("API # load_local");
                    xml.endElement();
                    queryXmls.put(query, xml.getXmlStringSB().toString());
                    // удалить подзапрос из общего запроса, чтобы не тратить время на его выполнение на сервере
                    input.getQueries().remove(query);
                } catch (Exception e) {
                    totalXml.addElement("error", ExceptionUtils.getStackTrace(e), "type", "reading_cache");
                }
            }
        }

        // загрузка с сервера (если еще остались необработанные запросы)
        if (input.getQueries().size() > 0) {
            ProviderGetter getter = PROVIDER_GETTERS.get(input.getRemote());
            if (getter != null) {
                Timer.getTimer().start("API # remote_load");
                Result result = getter.getData(input);
                Timer.getTimer().stop("API # remote_load");
                // Если результат получен
                if (result.isSuccess()) {
                    Timer.getTimer().start("API # store_cache");
                    for (Request.Query query : result.getRequest().getAllQueries()) {
                        // Сформировать кеши для полного соответсвия и всех результатов.
                        // Если не было полного соответствия - сохраняется пустой файл
                        Pair<String, String> xmlsSorted = separateExactMatchesXml(query.getProcessedResult().getXmlStringSB().toString());
                        // сохранить общий кеш (все результаты)
                        String allFileName = createCacheFileName(query.query);
                        File allCacheFile = new File(AppContext.getRealPath(CACHE_DIR + '/' + allFileName + ".xml"));
                        FileUtils.write(allCacheFile, xmlsSorted.getRight(), StandardCharsets.UTF_8);
                        // сохранить кеш полного совпадения
                        String exactFileName = createExactCacheFileName(query.query);
                        File exactCacheFile = new File(AppContext.getRealPath(CACHE_DIR + '/' + exactFileName + ".xml"));
                        FileUtils.write(exactCacheFile, xmlsSorted.getLeft(), StandardCharsets.UTF_8);
                        // Сохранить результат запроса в общем списке всех запросов
                        XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();
                        xml.startElement("query", "id", createQueryUniqueId(query.query), "q", query.query,
                                "qty", input.getQueries().get(query.query));
                        if (isBom) {
                            xml.addElements(xmlsSorted.getLeft());
                        } else {
                            xml.addElements(xmlsSorted.getRight());
                        }
                        addLocalProducts(query.query, xml, isBom); // также добавить локальные результаты
                        xml.endElement();
                        queryXmls.put(query.query, xml.getXmlStringSB().toString());
                        // удалить подзапрос, чтобы можно было посмотреть, остались ли необработанные
                        input.getQueries().remove(query.query);
                    }
                    Timer.getTimer().stop("API # store_cache");
                }
            }
        }

        // Применить фильтры
        for (String query : queryXmls.keySet()) {
            String filtered = applyFilters(queryXmls.get(query), queries.get(query));
            queryXmls.put(query, filtered);
        }

        // Для всех оставшихся необработанными запросов добавить сообщение об ошибке
        for (String unprocessed : input.getQueries().keySet()) {
            XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();
            xml.startElement("query", "id", createQueryUniqueId(unprocessed), "q", unprocessed,
                    "qty", input.getQueries().get(unprocessed));
            xml.addElement("error", "товары не доступны по непонятным причинам", "type", "general");
            xml.endElement();
            queryXmls.put(unprocessed, xml.getXmlStringSB().toString());
        }

        // возврат результата
        for (String query : queryXmls.keySet()) {
            totalXml.addElements(queryXmls.get(query));
        }
        return totalXml;
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
    private XmlDocumentBuilder performLocalSearch(String query, boolean isExact) throws Exception {
        BigDecimal extraQuotient = BigDecimal.ONE; // дополнительный коэффициент для цены
        Item localCatalog = ItemUtils.ensureSingleRootAnonymousItem(ItemNames.PLAIN_CATALOG, User.getDefaultUser());
        String searchType = isExact ? FulltextQueryCreatorRegistry.EQUAL : FulltextQueryCreatorRegistry.DEFAULT;
        ItemQuery localQuery = new ItemQuery(ItemNames.PRODUCT).setParentId(localCatalog.getId(), true)
                .setFulltextCriteria(searchType, query, 50, null, Compare.EVERY)
                .setLimit(50).addSorting(ItemNames.product_.SECTION_NAME, "ASC");
        List<Item> prods = localQuery.loadItems();

        XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();

        for (Item prod : prods) {
            Product p = Product.get(prod);
            String distributor = p.getDefault_section_name("partnumber.ru");
            // Если поменялось название поставщика в отсортированном списке - создать новый тэг
            extraQuotient = input.getDistributorQuotient(distributor); // TODO сделать загрузку коэффициентов
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
        return xml;
    }

    /**
     * Поиск в локальном каталоге и добавление в общий результат поиска (общий XML)
     * Происходит только в случае, если это надо делать (передана соответсвующее значение переменной)
     * @param query
     * @param xml
     */
    private void addLocalProducts(String query, XmlDocumentBuilder xml, boolean isExact) {
        if (performLocalSearch) {
            try {
                XmlDocumentBuilder localQueryXml = performLocalSearch(query, isExact);
                xml.addElements(localQueryXml.getXmlStringSB());
            } catch (Exception e) {
                xml.addElement("error", ExceptionUtils.getStackTrace(e), "type", "local_search");
            }
        }
    }


    /**
     * Применить заданные фильтры и сортировку
     * @param xml
     * @param queryQuantity
     * @return
     */
    public String applyFilters(String xml, Integer queryQuantity) {
        boolean hasQualityFilter = input.hasVendorFilter() || input.hasShipDateFilter() || input.hasFromFilter()
                || input.hasToFilter() || input.hasDistributorFilter();
        final int requestedQuantity = queryQuantity == null ? 1 : queryQuantity;
        boolean hasQuantityFilter = requestedQuantity > 1;
        boolean hasSorting = input.getSort() != OuterInputData.Sort.price;
        if (!hasQualityFilter && !hasQuantityFilter && !hasSorting)
            return xml;
        Document doc = JsoupUtils.parseXml(xml);

        // *****************************
        // Фильтрация
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
                    BigDecimal qtyPrice = getPriceForQty(product, requestedQuantity, input.getPriceParamName());
                    if (!input.fromPriceFilterMatches(qtyPrice)) {
                        product.remove();
                        continue;
                    }
                }
                if (input.hasToFilter()) {
                    BigDecimal qtyPrice = getPriceForQty(product, requestedQuantity, input.getPriceParamName());
                    if (!input.toPriceFilterMatches(qtyPrice)) {
                        product.remove();
                    }
                }
            }
        }

        // *****************************
        // Сортировка
        if (hasQuantityFilter || hasSorting) {
            //int firstProductIndex = doc.select("product").first().siblingIndex();
            ArrayList<Element> prods = new ArrayList<>(doc.select("product"));
            ArrayList<Element> sortedProds = new ArrayList<>();

            // Сортировка по цене
            if (input.getSort() == OuterInputData.Sort.price && hasQuantityFilter) {
                // Предварительно отсортировать список (чтобы не сортировать потом)
                prods.sort((o1, o2) -> compareXmlJsoupProducts(o1, o2, requestedQuantity, input.getPriceParamName()));
                // По одному записать все товары с минимальной ценой для оставшегося количества
                // (уменьшая постоянно количество на величину имеющегося фактически на складе для данного товара)
                int qtyToOrder = requestedQuantity;
                while (qtyToOrder > 0 && prods.size() > 0) {
                    final int RQTY = qtyToOrder;
                    Optional<Element> cheapestOpt = prods.stream().min((o1, o2) -> compareXmlJsoupProducts(o1, o2, RQTY, input.getPriceParamName()));
                    Element cheapest = cheapestOpt.get();
                    int qtyToOffer = qtyToOffer(cheapest, qtyToOrder);
                    BigDecimal price = getPriceForQty(cheapest, qtyToOffer, input.getPriceParamName());
                    cheapest.select("prices").attr("price", price.toPlainString()).attr("qty", qtyToOffer + "");
                    sortedProds.add(cheapest);
                    qtyToOrder -= qtyToOffer; // уменьшить требуемое количество, т.к. часть его уже удовлетворена
                    prods.remove(cheapest); // удалить из рассмотрения уже рассмотренный товар
                }
                // Добавить в общий отсортированный список оставшиеся товары из первого отсортированного списка
                for (Element prod : prods) {
                    if (qtyToOffer(prod, requestedQuantity) > 0)
                        sortedProds.add(prod);
                }
            }

            // Сортировка по дате поставки
            else if (input.getSort() == OuterInputData.Sort.date) {
                prods.sort((o1, o2) -> {
                    String shipTimeStr1 = RegExUtils.replaceAll(JsoupUtils.getTagFirstValue(o1, "next_delivery"), "\\D+", "");
                    String shipTimeStr2 = RegExUtils.replaceAll(JsoupUtils.getTagFirstValue(o2, "next_delivery"), "\\D+", "");
                    int shipTime1 = NumberUtils.toInt(shipTimeStr1, 1000);
                    int shipTime2 = NumberUtils.toInt(shipTimeStr2, 1000);
                    if (shipTime1 != shipTime2) {
                        return shipTime1 - shipTime2;
                    } else {
                        BigDecimal price1 = DecimalDataType.parse(o1.select("prices").first().attr("default_qty_price"), 4);
                        BigDecimal price2 = DecimalDataType.parse(o2.select("prices").first().attr("default_qty_price"), 4);
                        price1 = price1 == null ? BigDecimal.valueOf(Integer.MAX_VALUE) : price1;
                        price2 = price2 == null ? BigDecimal.valueOf(Integer.MAX_VALUE) : price2;
                        return price1.compareTo(price2);
                    }
                });
                sortedProds = prods;
            }

            doc.select("product").remove();
            doc.select("query").first().insertChildren(0, sortedProds);
        }

        // *****************************
        // Распределение количества по первым нескольким предложениям (продуктам)
        if (requestedQuantity > 0) {
            int desiredQty = requestedQuantity;
            for (Element product : doc.select("product")) {
                int qtyToOffer = qtyToOffer(product, desiredQty);
                product.attr("request_qty", qtyToOffer + "");
                if (qtyToOffer < desiredQty) {
                    desiredQty -= qtyToOffer;
                } else {
                    break;
                }
            }
        }

        return JsoupUtils.outputXmlNoPrettyPrint(doc);
    }

    /**
     * Получить цену товара, которая соответствует его количеству
     * @param product
     * @param productQty
     * @return
     */
    private static BigDecimal getPriceForQty(Element product, int productQty, String priceParamName) {
        Elements breaks = product.getElementsByTag("break");
        Element currentBreak = breaks.first();
        for (Element aBreak : breaks) {
            int breakQty = NumberUtils.toInt(aBreak.attr("qty"), 1);
            if (breakQty > productQty)
                break;
            currentBreak = aBreak;
        }
        String priceStr = JsoupUtils.getTagFirstValue(currentBreak, priceParamName);
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
     * Left - полное совпадение, которое отсортировано по цене
     * Rigth - все девайсы, в которых полное совпадение также отсортировано
     * @param allResultDoc
     * @return - <Exact Sotred, All (ecaxt and inexact, exact also sorted)>
     * в случае отсутствия полных совпадений возвращается пустой документ. Он тоже кешируется
     */
    private Pair<String, String> separateExactMatchesXml(String allResultDoc) {
        Document doc = JsoupUtils.parseXml(allResultDoc);
        ArrayList<Element> inexactProds = doc.select("product[query_exact_match=false]");
        doc.select("product[query_exact_match=false]").remove();
        Element firstProductEl = doc.select("product").first();
        boolean hasExactResult = firstProductEl != null;
        if (hasExactResult) {
            int firstProductIndex = firstProductEl.siblingIndex();
            ArrayList<Element> exactProds = new ArrayList<>(doc.select("product"));
            exactProds.sort((o1, o2) -> compareXmlJsoupProducts(o1, o2, 1, input.getPriceParamName()));
            doc.select("product").remove();
            doc.insertChildren(firstProductIndex, exactProds);
        }
        String resultExact = hasExactResult ? JsoupUtils.outputXmlNoPrettyPrint(doc) : "";

        // Добавляем все остальные результаты
        doc.appendChildren(inexactProds);
        boolean hasAllResult = doc.select("product").size() > 0;
        String resultAll = hasAllResult ? JsoupUtils.outputXmlNoPrettyPrint(doc) : "";
        return new Pair<>(resultExact, resultAll);
    }

    private static String createQueryUniqueId(String query) {
        return "q" + query.hashCode() + RandomStringUtils.randomAlphanumeric(5);
    }

    /**
     * Сравнивает два товара в XML, который был разобран с помощью JSoup
     * Сравнивается цена
     * @param o1
     * @param o2
     * @param desiredQty - количество для заказа, если нет количества, передавать 1
     * @param priceParamName
     * @return
     */
    private static int compareXmlJsoupProducts(Element o1, Element o2, int desiredQty, String priceParamName) {
        desiredQty = Math.max(desiredQty, 1);
        int qtyToOffer1 = qtyToOffer(o1, desiredQty);
        int qtyToOffer2 = qtyToOffer(o2, desiredQty);
        if (qtyToOffer1 == 0 && qtyToOffer2 == 0) {
            return 0;
        }
        if (qtyToOffer1 == 0)
            return 1;
        if (qtyToOffer2 == 0)
            return -1;
        BigDecimal price1 = getPriceForQty(o1, qtyToOffer1, priceParamName);
        BigDecimal price2 = getPriceForQty(o2, qtyToOffer2, priceParamName);
        price1 = price1 == null ? BigDecimal.valueOf(Integer.MAX_VALUE) : price1;
        price2 = price2 == null ? BigDecimal.valueOf(Integer.MAX_VALUE) : price2;
        BigDecimal equivalentPrice1 = price1;
        BigDecimal equivalentPrice2 = price2;
        if (qtyToOffer1 > desiredQty) {
            equivalentPrice1 = price1.multiply(BigDecimal.valueOf(qtyToOffer1)).divide(BigDecimal.valueOf(desiredQty), RoundingMode.HALF_EVEN);
        }
        if (qtyToOffer2 > desiredQty) {
            equivalentPrice2 = price2.multiply(BigDecimal.valueOf(qtyToOffer2)).divide(BigDecimal.valueOf(desiredQty), RoundingMode.HALF_EVEN);
        }
        int compare = equivalentPrice1.compareTo(equivalentPrice2);
        if (compare == 0) {
            return qtyToOffer2 - qtyToOffer1;
        }
        return compare;
    }

    /**
     * Вернуть количество, максимально близкое из возможных (в большую сторону) к тому, которое хочет заказать пользователь
     * @param prod
     * @param desiredQty
     * @return
     */
    private static int qtyToOffer(Element prod, int desiredQty) {
        int minQty = NumberUtils.toInt(JsoupUtils.getTagFirstValue(prod, "min_qty"), 1);
        int step = NumberUtils.toInt(JsoupUtils.getTagFirstValue(prod, "step"), 1);
        int qtyInStore = NumberUtils.toInt(JsoupUtils.getTagFirstValue(prod, "qty"), 0);
        int qtyToOffer = Math.max(minQty, desiredQty);
        qtyToOffer = Math.min(qtyToOffer, qtyInStore);
        int numSteps = Math.floorDiv(qtyToOffer, step);
        if (numSteps * step < qtyToOffer)
            numSteps++;
        qtyToOffer = numSteps * step;
        if (qtyToOffer > qtyInStore)
            qtyToOffer = (numSteps - 1) * step;
        return qtyToOffer;
    }
}
