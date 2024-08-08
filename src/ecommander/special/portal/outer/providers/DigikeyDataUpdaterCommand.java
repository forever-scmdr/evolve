package ecommander.special.portal.outer.providers;

import ecommander.controllers.AppContext;
import ecommander.fwk.EcommanderException;
import ecommander.fwk.JsoupUtils;
import ecommander.fwk.Pair;
import ecommander.fwk.ServerLogger;
import ecommander.model.Item;
import ecommander.model.datatypes.TupleDataType;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
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
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.concurrent.ConcurrentHashMap;

/**
 * TODO 2. Все-таки сделать список того, что уже загружено, чтобы сохранялась информация о том, что было в начале (начальные урлы, а не замененные)
 *
 *
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
    private static final UpdatingCounter counter = new UpdatingCounter();

    private volatile Item prod;

    @Override
    public ResultPE execute() throws Exception {
        Item loadedProd = getSingleLoadedItem("prod");
        if (loadedProd == null)
            return null;
        // Эта строчка нужна чтобы не менять в разных потоках айтем, который был загружен для показа на странице
        // и подвергается всей соответствующей логике уже после (или параллельно) выполнения этой команды
        prod = ItemQuery.loadById(loadedProd.getId());
        synchronized (counter) {
            if (counter.isUpdating(prod))
                return null;
            counter.startUpdating(prod);
        }
        DateTime defaultTime = DateTime.now().minusYears(5);
        DateTime lastChecked = new DateTime(prod.getLongValue(product_.LAST_UPDATE_CHECKED, defaultTime.getMillis()), DateTimeZone.UTC);
        // Проверка даты последнего обновления (закомментировать для тестов)
        if (lastChecked.isAfter(DateTime.now().minusDays(CHECK_DAYS_INTERVAL))) {
            counter.endUpdating(prod);
            return null;
        }
        // pdf файлы, которые хранятся в товаре
        String url = prod.getStringValue(product_.URL);
        if (StringUtils.isBlank(url)) {
            return null;
        }
        if (prod.isValueEmpty(product_.TO_DOWNLOAD)) {
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
                            if (StringUtils.isNotBlank(pdfUrl) && !StringUtils.startsWith(pdfUrl, IMG_DIRECTORY)) {
                                prod.setValue(product_.TO_DOWNLOAD, TupleDataType.newTuple(PDF_LABEL, pdfUrl));
                            }
                        }
                    }
                }
            }
            // загрузка html для картинки
            getAndSaveHtmlData();
        } else {
            getAndSaveFiles(true);
            counter.endUpdating(prod); // в этом месте, а не в методе, т.к. метод также вызывается из getAndSaveHtmlData()
        }
        return null;
    }



    /**
     * Загрузить с digikey.com html страницу товара.
     * Разбор страницы запускается асинхронно после успешного скачивания этой страницы.
     * Если разбор прошел успешно, из этого асинхронного обработчика вызывается также и процесс
     * скачивания файлов, который тоже работает асинхронно (потому что для одного товара может
     * качаться много файлов)
     */
    private void getAndSaveHtmlData() {
        HtmlResultHandler htmlHandler = new HtmlResultHandler();
        GeneralProxyRequestProcessor.submitAsync(htmlHandler, prod.getStringValue(product_.URL), "text/html");
    }

    private void getAndSaveFiles(boolean async) {
        ArrayList<Pair<String, String>> toDownload = prod.getTupleValues(product_.TO_DOWNLOAD);
        for (Pair<String, String> toDownloadLine : toDownload) {
            boolean isPdf = !StringUtils.equalsIgnoreCase(toDownloadLine.getLeft(), IMAGE_LABEL);
            FileResultHandler fileHandler = new FileResultHandler(isPdf);
            String responseMimeType = isPdf ? "application/pdf" : "image/jpeg";
            // Сначала проверить наличие файлов
            String filePath = AppContext.getRealPath(createFileNameUrlPathFromQueryUrl(prod, toDownloadLine.getRight(), isPdf));
            if (!Files.exists(Paths.get(filePath))) {
                counter.startUpdating(prod);
                if (async) {
                    GeneralProxyRequestProcessor.submitAsync(fileHandler, toDownloadLine.getRight(), responseMimeType);
                } else {
                    try {
                        GeneralProxyRequestProcessor.submitSyncAsAsync(fileHandler, toDownloadLine.getRight(), responseMimeType);
                    } catch (EcommanderException e) {
                        ServerLogger.error("Error while auto downloading files", e);
                        // продолжить выполнение для других файлов
                        counter.endUpdating(prod);
                    }
                }
            }
        }
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
    private class HtmlResultHandler implements GeneralProxyRequestProcessor.ResultHandler {

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
                                DelayedTransaction.executeSingle(getInitiator(), SaveItemDBUnit.get(prod));
                            } catch (Exception e) {
                                ServerLogger.error("Error while saving product for image download", e);
                            }
                        }
                        // TODO можно обновлять время последней проверки так, чтобы повторная проверка нужна была бы через 10 минут, например
                        if (prod.isValueNotEmpty(product_.TO_DOWNLOAD)) {
                            getAndSaveFiles(false);
                        }
                    }
                }
            }
            synchronized (counter) {
                counter.endUpdating(prod);
            }
        }
    }

    /**
     * Обработчик ответа сервера на запрос файлов с digikey
     */
    private class FileResultHandler implements GeneralProxyRequestProcessor.ResultHandler {
        private boolean isPdf;

        private FileResultHandler(boolean isPdf) {
            this.isPdf = isPdf;
        }
        @Override
        public void handleResult(Result result) {
            if (result.isSuccess()) {
                for (Request.Query query : result.getRequest().getAllQueries()) {
                    if (query.isFinishedAndSuccess()) {
                        if (query.getResult() != null && query.getResult().length > 0) {
                            String fileUrl = createFileNameUrlPathFromQueryUrl(prod, query.getQueryString(), isPdf);
                            String filePath = AppContext.getRealPath(fileUrl);
                            // сохраняется pdf файл
                            if (isPdf) {
                                try {
                                    if (isPdf(query.getResult())) {
                                        FileUtils.writeByteArrayToFile(new File(filePath), query.getResult());
                                        String docsXml = prod.getStringValue(product_.DOCUMENTS_XML);
                                        if (prod.isValueNotEmpty(product_.DOCUMENTS_XML_MOD)) {
                                            docsXml = prod.getStringValue(product_.DOCUMENTS_XML_MOD);
                                        }
                                        docsXml = StringUtils.replace(docsXml, query.getQueryString(), fileUrl);
                                        prod.setValue(product_.DOCUMENTS_XML_MOD, docsXml);
                                    }
                                    Object toDelete = TupleDataType.newTuple(PDF_LABEL, query.getQueryString());
                                    prod.setValue(product_.LAST_UPDATE_CHECKED, DateTime.now(DateTimeZone.UTC).getMillis());
                                    prod.removeEqualValue(product_.TO_DOWNLOAD, toDelete);
                                    DelayedTransaction.executeSingle(getInitiator(), SaveItemDBUnit.get(prod));
                                } catch (Exception e) {
                                    ServerLogger.error("Error while saving pdf for product", e);
                                }
                            }
                            // сохраняется картинка
                            else {
                                Object toDelete = TupleDataType.newTuple(IMAGE_LABEL, query.getQueryString());
                                try {
                                    FileUtils.writeByteArrayToFile(new File(filePath), query.getResult());
                                    prod.setValue(product_.MAIN_PIC_URL, fileUrl);
                                    prod.setValue(product_.LAST_UPDATE_CHECKED, DateTime.now(DateTimeZone.UTC).getMillis());
                                    prod.removeEqualValue(product_.TO_DOWNLOAD, toDelete);
                                    DelayedTransaction.executeSingle(getInitiator(), SaveItemDBUnit.get(prod));
                                } catch (Exception e) {
                                    ServerLogger.error("Error while saving image for product", e);
                                }
                            }
                        }
                    }
                }
            }
            counter.endUpdating(prod);
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

    /**
     * Сделать имя локального файла относительно корневой директории урлов (base) из урла запроса для файла
     * @param prod
     * @param url
     * @param isPdf
     * @return
     */
    private static String createFileNameUrlPathFromQueryUrl(Item prod, String url, boolean isPdf) {
        String fileName = isPdf ? StringUtils.substringAfterLast(url, "/") : prod.getKeyUnique() + ".jpg";
        fileName = StringUtils.substringBefore(fileName, "?");
        if (isPdf && !StringUtils.endsWithIgnoreCase(fileName, "pdf")) {
            fileName += ".pdf";
        }
        return IMG_DIRECTORY + "/" + prod.getRelativeFilesPath() + fileName;
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
            return updating.get(itemId) != null && updating.get(itemId) > 0;
        }

        public boolean isUpdating(Item item) {
            return isUpdating(item.getId());
        }
    }
}
