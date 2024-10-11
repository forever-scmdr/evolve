package ecommander.special.portal.outer.parsing;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ecommander.controllers.AppContext;
import ecommander.fwk.EcommanderException;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.MessageError;
import ecommander.fwk.ServerLogger;
import ecommander.model.Item;
import lunacrawler.fwk.Crawler;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.FileInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

public class ImportProductsFromDigikeyPageFiles extends IntegrateBase {

    public static final String RESULT_DIR_PROP = "parsing.result_dir"; // директория, в которой лежат файлы со стилями
    private static final String SRC_DIR_NAME = "_src/";
    private static final String IMPORTED_DIR_NAME = "_imported/";

    private String FILES_ROOT_DIR;
    private String SRC_DIR;
    private String IMPORTED_DIR;

    @Override
    protected boolean makePreparations() throws Exception {
        FILES_ROOT_DIR = AppContext.getRealPath(AppContext.getProperty(RESULT_DIR_PROP, null));
        FILES_ROOT_DIR = StringUtils.appendIfMissing(FILES_ROOT_DIR, "/", "/");
        SRC_DIR = FILES_ROOT_DIR + SRC_DIR_NAME;
        IMPORTED_DIR = FILES_ROOT_DIR + IMPORTED_DIR_NAME;
        return true;
    }

    @Override
    protected void integrate() throws Exception {
        importSourceDirectory(Paths.get(SRC_DIR));
    }

    @Override
    protected void terminate() throws Exception {

    }

    private void importSourceDirectory(Path dir) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(dir)) {
            // Для каждого файла из временной директории
            for (Path entry : stream) {
                if (Files.isDirectory(entry)) {
                    importSourceDirectory(entry);
                } else {
                    String html = null;

                    try (FileInputStream fis = new FileInputStream(entry.toFile());
                         ZipInputStream zis = new ZipInputStream(fis)) {
                        ZipEntry zipEntry = zis.getNextEntry();
                        if (zipEntry != null) {
                            html = IOUtils.toString(zis, StandardCharsets.UTF_8);
                            zis.closeEntry();
                        }
                    }
                    // Возможно файл не архивирован
                    catch (Exception e) {
                        html = FileUtils.readFileToString(entry.toFile(), StandardCharsets.UTF_8);
                    }
                    if (StringUtils.isBlank(html)) {
                        try {
                            html = FileUtils.readFileToString(entry.toFile(), StandardCharsets.UTF_8);
                        } catch (IOException e) {
                            html = null;
                        }
                    }
                    if (StringUtils.isBlank(html)) {
                        info.pushLog("URL file {} has not been imported correctly. Archiving error", entry.toString());
                        continue;
                    }

                    // сам импорт данных
                    importProduct(html);

                    // копирование файла в директорию отработанных
                    String divisionDirName = IMPORTED_DIR + Crawler.getUrlDirName(entry.getFileName().toString());
                    Files.createDirectories(Paths.get(divisionDirName));
                    Path importedFile = Paths.get(divisionDirName + entry.getFileName().toString());
                    Files.copy(entry, importedFile);

                    info.increaseProcessed();
                }
            }
        } catch (Exception e) {
            ServerLogger.error("Error while importing source html file", e);
            info.pushLog("Error while importing source html file", e);
        }
    }


    private void importProduct(String html) throws EcommanderException {
        Document pageDoc = Jsoup.parse(html);
        //String url = pageDoc.getElementsByTag("body").first().attr("source");
        Elements sectionLinks = pageDoc.select("script[id=__NEXT_DATA__]");
        if (sectionLinks.size() == 0)
            return;
        String script = sectionLinks.html();
        JsonElement json = JsonParser.parseString(script);
        if (json == null) {
            throw new MessageError("No json found");
        }
        JsonObject props = json.getAsJsonObject().getAsJsonObject("props");
        if (props == null) {
            throw new MessageError("No 'props' element found in json");
        }
        JsonObject data = null;
        JsonObject productOverview = null;
        JsonArray breadcrumb = null;
        try {
            data = props.getAsJsonObject("pageProps").getAsJsonObject("envelope").getAsJsonObject("data");
            productOverview = data.getAsJsonObject("productOverview");
            breadcrumb = data.getAsJsonArray("breadcrumb");
        } catch (NullPointerException npe) {
            throw new MessageError("JSON structure is not as expected");
        }
        if (productOverview == null || breadcrumb == null) {
            throw new MessageError("JSON structure is not as expected");
        }

        // Обработка пути к товару (создание или загрузка разделов каталога)
        Item parentSection = null;
        for (int i = breadcrumb.size() - 2; i > 0; i++) {
            String sectionName = breadcrumb.get(i).getAsJsonObject().get("label").getAsString();
            parentSection =
        }

    }


    private static Item ensureSection(JsonArray crumbs, int idx) {

    }

}
