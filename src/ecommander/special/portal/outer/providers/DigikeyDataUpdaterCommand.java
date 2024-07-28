package ecommander.special.portal.outer.providers;

import ecommander.controllers.AppContext;
import ecommander.fwk.JsoupUtils;
import ecommander.fwk.Pair;
import ecommander.fwk.ServerLogger;
import ecommander.model.Item;
import ecommander.model.User;
import ecommander.model.datatypes.TupleDataType;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.special.portal.outer.GeneralProxyRequestProcessor;
import ecommander.special.portal.outer.Request;
import ecommander.special.portal.outer.Result;
import extra._generated.ItemNames;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Скачивает данные товара с digikey
 * В товаре хранится урл, с которого он брался на digikey. По этому урлу скачивается html страница с данными товара
 * Ее можно в принципе брать для обновления информации по самому товару, но пока скачивается только картинка и pdf
 *
 * параметр - загруженный страничный айтем с id prod
 */
public class DigikeyDataUpdaterCommand extends Command implements ItemNames {
    public static final int CHECK_DAYS_INTERVAL = 180;
    public static final String IMAGE_LABEL = "image";
    public static final String PDF_LABEL = "pdf";
    public static final String IMG_DIRECTORY = "imgdata";
    private static volatile UpdatingCounter counter;
    static {
        counter = new UpdatingCounter();
    }

    @Override
    public ResultPE execute() throws Exception {
        Item prod = getSingleLoadedItem("prod");
        if (prod == null)
            return null;
        DateTime defaultTime = DateTime.now().minusYears(5);
        DateTime lastChecked = new DateTime(prod.getLongValue(product_.LAST_UPDATE_CHECKED, defaultTime.getMillis()), DateTimeZone.UTC);
        if (lastChecked.isAfter(DateTime.now().minusDays(CHECK_DAYS_INTERVAL)))
            return null;
        if (counter.isUpdating(prod.getId()))
            return null;
        String url = prod.getStringValue(product_.URL);
        if (StringUtils.isBlank(url)) {
            return null;
        }
        if (prod.isValueEmpty(product_.TO_DOWNLOAD)) {
            // pdf файлы, которые хранятся в товаре
            counter.startUpdating(prod);
            String xml = StringEscapeUtils.unescapeXml(prod.getStringValue(product_.DOCUMENTS_XML));
            if (StringUtils.isNotBlank(xml)) {
                Document docsDoc = JsoupUtils.parseXml("<root>" + xml + "</root>");
                Elements els = docsDoc.select("param");
                for (Element el : els) {
                    String name = JsoupUtils.getTagFirstValue(el, "name");
                    if (StringUtils.containsIgnoreCase(name, "datasheet")) {
                        Elements links = el.select("a");
                        for (Element link : links) {
                            String pdfUrl = link.attr("href");
                            if (StringUtils.isNotBlank(pdfUrl)) {
                                prod.setValue(product_.TO_DOWNLOAD, TupleDataType.newTuple(PDF_LABEL, pdfUrl));
                            }
                        }
                    }
                }
            }
            // загрузка html для картинки
            getAndSaveHtmlData(prod);
        } else {
            getAndSaveFiles(prod);
        }
        return null;
    }



    /**
     * Загрузить с digikey.com html страницу товара.
     * Разбор страницы запускается асинхронно после успешного скачивания этой страницы.
     * Если разбор прошел успешно, из этого асинхронного обработчика вызывается также и процесс
     * скачивания файлов, который тоже работает асинхронно (потому что для одного товара может
     * качаться много файлов)
     * @param prod
     */
    private void getAndSaveHtmlData(Item prod) {
        HtmlResultHandler htmlHandler = new HtmlResultHandler(prod, getInitiator());
        GeneralProxyRequestProcessor.submitAsync(htmlHandler, prod.getStringValue(product_.URL));
    }

    private void getAndSaveFiles(Item prod) {
        ArrayList<Pair<String, String>> toDownload = prod.getTupleValues(product_.TO_DOWNLOAD);
        for (Pair<String, String> toDownloadLine : toDownload) {
            boolean isPdf = !StringUtils.equalsIgnoreCase(toDownloadLine.getLeft(), IMAGE_LABEL);
            FileResultHandler fileHandler = new FileResultHandler(prod, getInitiator(), isPdf);
            GeneralProxyRequestProcessor.submitAsync(fileHandler, toDownloadLine.getRight());
        }
    }

    /**
     * Исправить урл
     * @param url
     * @return
     */
    private static String formatUrl(String url) {
        if (!StringUtils.startsWith(url, "http")) {
            return "https:" + url;
        }
        return url;
    }

    /**
     * Урлы, которые действительно надо скачивать
     * @param prod
     * @return
     */
    private HashSet<Pair<String, String>> getUrlsToActuallyDownlad(Item prod) {
        HashSet<Pair<String, String>> toDownload = new HashSet<>(prod.getTupleValues(product_.TO_DOWNLOAD));
        toDownload.removeAll(prod.getTupleValues(product_.DOWNLOADED));
        return toDownload;
    }

    /**
     * Найти в списке урлов урл для картинки
     * @param allUrls
     * @return
     */
    private Pair<String, String> getImageUrlToDownlaod(Collection<Pair<String, String>> allUrls) {
        for (Pair<String, String> url : allUrls) {
            if (StringUtils.equals(url.getLeft(), IMAGE_LABEL)) {
                return url;
            }
        }
        return null;
    }

    /**
     * Обработчик ответа сервера на запрос страницы html страницы с digikey
     */
    private static class HtmlResultHandler implements GeneralProxyRequestProcessor.ResultHandler {
        private Item prod;
        private User initiator;

        private HtmlResultHandler(Item prod, User initiator) {
            this.prod = prod;
            this.initiator = initiator;
        }
        @Override
        public void handleResult(Result result) {
            if (result.isSuccess()) {
                Request.Query query = result.getRequest().getSingleQuery();
                if (query.isFinishedAndSuccess()) {
                    Document doc = Jsoup.parse(query.getResultString(StandardCharsets.UTF_8), "https://www.digikey.com");
                    Element meta = doc.select("meta[property=og:image]").first();
                    if (meta != null) {
                        String imageUrl = meta.attr("content");
                        if (StringUtils.isNotBlank(imageUrl)) {
                            prod.setValue(product_.TO_DOWNLOAD, TupleDataType.newTuple(IMAGE_LABEL, imageUrl));
                            try {
                                // сохранение товара
                                DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(prod));
                                FileResultHandler fileHandler = new FileResultHandler(prod, initiator);
                                GeneralProxyRequestProcessor.submitSyncAsAsync(fileHandler, prod.getStringValue(imageUrl));
                                return;
                            } catch (Exception e) {
                                ServerLogger.error("Error while saving product for image download", e);
                            }
                        }
                    }
                }
            }
            counter.endUpdating(prod);
            // TODO можно обновлять время последней проверки так, чтобы повторная проверка нужна была бы через 10 минут, например
            return;
        }
    }

    /**
     * Обработчик ответа сервера на запрос файлов с digikey
     */
    private static class FileResultHandler implements GeneralProxyRequestProcessor.ResultHandler {
        private Item prod;
        private User initiator;
        private boolean isPdf;

        private FileResultHandler(Item prod, User initiator, boolean isPdf) {
            this.prod = prod;
            this.initiator = initiator;
            this.isPdf = isPdf;
        }
        @Override
        public void handleResult(Result result) {
            if (result.isSuccess()) {
                for (Request.Query query : result.getRequest().getAllQueries()) {
                    if (query.isFinishedAndSuccess()) {
                        if (query.getResult() != null && query.getResult().length > 0) {
                            DateTime now = DateTime.now(DateTimeZone.UTC);
                            String dirName = now.getDayOfMonth() + "" + now.getMonthOfYear() + "" + now.getYear();
                            String fileName = StringUtils.substringAfterLast(query.getQueryString(), "/");
                            if (isPdf && !StringUtils.endsWithIgnoreCase(fileName, "pdf")) {
                                fileName += ".pdf";
                            }
                            String filePath = AppContext.getRealPath(IMG_DIRECTORY + "/" + dirName + "/" + fileName);
                            // сохраняется pdf файл
                            if (isPdf) {
                                try {
                                    if (isPdf(query.getResult())) {
                                        FileUtils.writeByteArrayToFile(new File(filePath), query.getResult());
                                        Object toDelete = TupleDataType.newTuple(PDF_LABEL, query.getQueryString());
                                        String docsXml = prod.getStringValue(product_.DOCUMENTS_XML);
                                        docsXml = StringUtils.replace(docsXml, query.getQueryString(), filePath);
                                        prod.setValue(product_.DOCUMENTS_XML, docsXml);
                                        prod.setValue(product_.LAST_UPDATE_CHECKED, DateTime.now(DateTimeZone.UTC));
                                        prod.removeEqualValue(product_.TO_DOWNLOAD, toDelete);
                                        DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(prod));
                                    }
                                } catch (Exception e) {
                                    ServerLogger.error("Error while saving pdf for product", e);
                                }
                            }
                            // сохраняется картинка
                            else {
                                Object toDelete = TupleDataType.newTuple(IMAGE_LABEL, query.getQueryString());
                                try {
                                    FileUtils.writeByteArrayToFile(new File(filePath), query.getResult());
                                    prod.setValue(product_.MAIN_PIC_URL, filePath);
                                    prod.setValue(product_.LAST_UPDATE_CHECKED, DateTime.now(DateTimeZone.UTC));
                                    prod.removeEqualValue(product_.TO_DOWNLOAD, toDelete);
                                    DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(prod));
                                } catch (Exception e) {
                                    ServerLogger.error("Error while saving image for product", e);
                                }
                            }
                        }
                    }
                }
            }
            updatingProdIds.remove(prod.getId());
        }
    }

    private static boolean isPdf(byte[] bytes) {
        return bytes != null && bytes.length > 4
                && bytes[0] == 0x25 // %
                && bytes[1] == 0x50 // P
                && bytes[2] == 0x44 // D
                && bytes[3] == 0x46 // F
                && bytes[4] == 0x2D; // -
    }

    private static class UpdatingCounter {
        private volatile ConcurrentHashMap<Long, Integer> updating = new ConcurrentHashMap<>();

        public void startUpdating(Long itemId) {
            updating.compute(itemId, (key, count) -> count == null ? 1 : count++);
        }

        public void startUpdating(Item item) {
            startUpdating(item.getId());
        }

        public void endUpdating(Long itemId) {
            updating.compute(itemId, (key, count) -> (count == null || count <= 1) ? null : count--);
            if (updating.containsKey(itemId) && updating.get(itemId) == null) {
                updating.remove(itemId);
            }
        }

        public void endUpdating(Item item) {
            endUpdating(item.getId());
        }

        public boolean isUpdating(Long itemId) {
            return updating.get(itemId) == null;
        }

        public boolean isUpdating(Item item) {
            return isUpdating(item.getId());
        }
    }
}
