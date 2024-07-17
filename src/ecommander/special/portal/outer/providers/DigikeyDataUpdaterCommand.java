package ecommander.special.portal.outer.providers;

import ecommander.controllers.AppContext;
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
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

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
    public static final String IMG_DIRECTORY = "imgdata";
    private static volatile Set<Long> updatingProdIds;
    static {
        updatingProdIds = ConcurrentHashMap.newKeySet();
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
        if (updatingProdIds.contains(prod.getId()))
            return null;
        updatingProdIds.add(prod.getId()); // не забыть потом удалять
        String url = prod.getStringValue(product_.URL);
        if (StringUtils.isBlank(url)) {
            updatingProdIds.remove(prod.getId());
            return null;
        }
        if (StringUtils.isBlank(getImageListedToDownload(prod))) {
            getAndSaveHtmlData(prod);
        } else {
            getAndSaveFiles(prod);
        }
        return null;
    }

    private void getAndSaveHtmlData(Item prod) {
        HtmlResultHandler htmlHandler = new HtmlResultHandler(prod, getInitiator());
        GeneralProxyRequestProcessor.submitAsync(htmlHandler, prod.getStringValue(product_.URL));
    }

    private void getAndSaveFiles(Item prod) {
        FileResultHandler fileHandler = new FileResultHandler(prod, getInitiator());
        GeneralProxyRequestProcessor.submitAsync(fileHandler, getImageListedToDownload(prod));
    }

    /**
     * Получить урл для скачки, который содержит главную картинку
     * @param prod
     * @return
     */
    private static String getImageListedToDownload(Item prod) {
        ArrayList<Pair<String, String>> toDownload = prod.getTupleValues(product_.TO_DOWNLOAD);
        for (Pair<String, String> toDownloadLine : toDownload) {
            if (StringUtils.equalsIgnoreCase(toDownloadLine.getLeft(), IMAGE_LABEL))
                return toDownloadLine.getRight();
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
                            prod.clearValue(product_.TO_DOWNLOAD);
                            prod.setValue(product_.TO_DOWNLOAD, TupleDataType.newTuple(IMAGE_LABEL, imageUrl));
                            try {
                                DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(prod));
                                FileResultHandler fileHandler = new FileResultHandler(prod, initiator);
                                GeneralProxyRequestProcessor.submitSyncAsAsync(fileHandler, prod.getStringValue(imageUrl));
                                updatingProdIds.remove(prod.getId());
                                return;
                            } catch (Exception e) {
                                ServerLogger.error("Error while saving product for image download", e);
                            }
                        }
                    }
                }
            }
            updatingProdIds.remove(prod.getId());
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

        private FileResultHandler(Item prod, User initiator) {
            this.prod = prod;
            this.initiator = initiator;
        }
        @Override
        public void handleResult(Result result) {
            if (result.isSuccess()) {
                for (Request.Query query : result.getRequest().getAllQueries()) {
                    if (query.isFinishedAndSuccess()) {
                        if (query.getResult() != null && query.getResult().length > 0) {
                            String fileName = StringUtils.substringAfterLast(query.getQueryString(), "/");
                            String filePath = AppContext.getRealPath(IMG_DIRECTORY + "/" + fileName);
                            try {
                                FileUtils.writeByteArrayToFile(new File(filePath), query.getResult());
                                prod.setValue(product_.MAIN_PIC_URL, filePath);
                                prod.setValue(product_.LAST_UPDATE_CHECKED, DateTime.now(DateTimeZone.UTC));
                                prod.clearValue(product_.TO_DOWNLOAD);
                                DelayedTransaction.executeSingle(initiator, SaveItemDBUnit.get(prod));
                            } catch (Exception e) {
                                ServerLogger.error("Error while saving image for product", e);
                            }
                        }
                    }
                }
            }
            updatingProdIds.remove(prod.getId());
        }
    }
}
