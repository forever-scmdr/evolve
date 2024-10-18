package ecommander.special.portal.outer.parsing;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ecommander.controllers.AppContext;
import ecommander.fwk.*;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.User;
import ecommander.model.datatypes.TupleDataType;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.special.portal.outer.providers.DigikeyDataUpdaterCommand;
import extra._generated.ItemNames;
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

public class ImportProductsFromDigikeyPageFiles extends IntegrateBase implements ItemNames {

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

    private void importSourceDirectory(Path path) {
        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
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
                    Files.delete(entry); // удалить файл, чтобы не рассматривать его повторно

                    info.increaseProcessed();
                }
            }
        } catch (Exception e) {
            ServerLogger.error("Error while importing source html file", e);
            info.pushLog("Error while importing source html file", e);
        }
    }


    private void importProduct(String html) throws Exception {
        Document pageDoc = Jsoup.parse(html);
        String url = pageDoc.getElementsByTag("body").first().attr("source");
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
        JsonObject data = null; // объект data - содержит все необходимые данные
        JsonObject productOverview = null; // объект productOverview - основные параметры продукта
        JsonObject productAttributes = null; // объект productAttributes - список технических характеристик
        JsonArray breadcrumb = null; // объект breadcrumb - путь разделов к товару
        try {
            data = props.getAsJsonObject("pageProps").getAsJsonObject("envelope").getAsJsonObject("data");
            productOverview = data.getAsJsonObject("productOverview");
            breadcrumb = data.getAsJsonArray("breadcrumb");
            productAttributes = data.getAsJsonObject("productAttributes");
        } catch (NullPointerException npe) {
            throw new MessageError("JSON structure is not as expected");
        }
        if (productOverview == null || breadcrumb == null) {
            throw new MessageError("JSON structure is not as expected");
        }

        // Обработка пути к товару (создание или загрузка разделов каталога)
        Item section = ensureSection(breadcrumb, 0, null);

        // Создание (или загрузка) товара
        String code = productOverview.get("manufacturerProductNumber").getAsString();
        Item product = ItemQuery.loadSingleItemByParamValue(PRODUCT, product_.NAME, code);
        if (product == null) {
            product = Item.newChildItem(ItemTypeRegistry.getItemType(PRODUCT), section);
            product.setValue(product_.NAME, code);
            executeAndCommitCommandUnits(SaveItemDBUnit.get(product));
        }

        // Простые параметры товара (берутся из productOverview)
        product.setValue(product_.CODE, code);
        product.setValue(product_.URL, url);
        product.setValue(product_.VENDOR, productOverview.get("manufacturer").getAsString());
        product.setValue(product_.NAME_EXTRA, productOverview.get("description").getAsString());
        product.setValue(product_.DESCRIPTION, productOverview.get("detailedDescription").getAsString());
        product.setValue(product_.OFFER_ID, productOverview.get("rolledUpProductNumber").getAsString());
        product.setValue(product_.TYPE, section.getStringValue(section_.NAME));
        product.setValue(product_.STATUS, "v2"); // это чтобы можно было отличать вновь импортированные товары от ранее импортированных (в IntegrateParsedDigikey)

        // Заполнение технических характеристик
        XmlDocumentBuilder paramsXml = XmlDocumentBuilder.newDocPart();
        if (productAttributes != null) {
            JsonArray attributes = productAttributes.getAsJsonArray("attributes");
            if (attributes != null) {
                for (JsonElement attribute : attributes) {
                    JsonObject attr = attribute.getAsJsonObject();
                    try {
                        String label = attr.get("label").getAsString();
                        paramsXml.startElement("param");
                        paramsXml.addElement("name", StringUtils.normalizeSpace(label));
                        JsonArray values = attr.getAsJsonArray("values");
                        for (JsonElement value : values) {
                            JsonObject val = value.getAsJsonObject();
                            paramsXml.addElement("value", val.get("value").getAsString());
                        }
                        paramsXml.endElement(); // param
                    } catch (Exception e) {
                        continue;
                    }
                }
            }
        }
        product.setValue(product_.PARAMS_XML, paramsXml.getXmlStringSB().toString());

        // Environmental and Export Classifications
        XmlDocumentBuilder envXml = XmlDocumentBuilder.newDocPart();
        JsonObject environmental = data.getAsJsonObject("environmental");
        if (environmental != null) {
            try {
                JsonArray dataRows = environmental.getAsJsonArray("dataRows");
                if (dataRows != null) {
                    for (JsonElement row : dataRows) {
                        JsonArray dataCells = row.getAsJsonObject().getAsJsonArray("dataCells");
                        if (dataCells.size() != 2) {
                            continue;
                        }
                        String name = dataCells.get(0).getAsJsonObject().getAsJsonObject("data")
                                .getAsJsonObject("value").get("value").getAsString();
                        String value = dataCells.get(1).getAsJsonObject().getAsJsonObject("data")
                                .getAsJsonObject("value").get("value").getAsString();
                        envXml.startElement("param");
                        envXml.addElement("name", StringUtils.normalizeSpace(name));
                        envXml.addElement("value", StringUtils.normalizeSpace(value));
                        envXml.endElement(); // param
                    }
                }
            } catch (Exception e) {
                // ничего не делать, просто пропустить
            }
        }
        product.setValue(product_.ENVIRONMENTAL_XML, envXml.getXmlStringSB().toString());


        // Additional Resources
        XmlDocumentBuilder extraXml = XmlDocumentBuilder.newDocPart();
        JsonObject additionalResources = data.getAsJsonObject("additionalResources");
        if (additionalResources != null) {
            try {
                JsonArray dataRows = additionalResources.getAsJsonArray("dataRows");
                if (dataRows != null) {
                    for (JsonElement row : dataRows) {
                        JsonArray dataCells = row.getAsJsonObject().getAsJsonArray("dataCells");
                        if (dataCells.size() != 2) {
                            continue;
                        }
                        extraXml.startElement("param");
                        String name = dataCells.get(0).getAsJsonObject().getAsJsonObject("data")
                                .getAsJsonObject("value").get("value").getAsString();
                        extraXml.addElement("name", StringUtils.normalizeSpace(name));
                        JsonObject valueObj = dataCells.get(1).getAsJsonObject().getAsJsonObject("data").getAsJsonObject("value");
                        JsonArray manyVals = valueObj.getAsJsonArray("values");
                        if (manyVals != null) {
                            for (JsonElement val : manyVals) {
                                extraXml.addElement("value", StringUtils.normalizeSpace(val.getAsJsonObject().get("value").getAsString()));
                            }
                        } else {
                            extraXml.addElement("value", StringUtils.normalizeSpace(valueObj.get("value").getAsString()));
                        }
                        extraXml.endElement(); // param
                    }
                }
            } catch (Exception e) {
                // ничего не делать, просто пропустить
            }
        }
        product.setValue(product_.ADDITIONAL_XML, extraXml.getXmlStringSB().toString());

        // Documents and Media
        XmlDocumentBuilder docsXml = XmlDocumentBuilder.newDocPart();
        JsonObject otherDocsAndMedia = data.getAsJsonObject("otherDocsAndMedia");
        if (otherDocsAndMedia != null) {
            try {
                JsonArray dataRows = additionalResources.getAsJsonArray("dataRows");
                if (dataRows != null) {
                    for (JsonElement row : dataRows) {
                        JsonArray dataCells = row.getAsJsonObject().getAsJsonArray("dataCells");
                        if (dataCells.size() != 2) {
                            continue;
                        }
                        docsXml.startElement("param");
                        String name = dataCells.get(0).getAsJsonObject().getAsJsonObject("data")
                                .getAsJsonObject("value").get("value").getAsString();
                        docsXml.addElement("name", StringUtils.normalizeSpace(name));
                        JsonObject valueObj = dataCells.get(1).getAsJsonObject().getAsJsonObject("data").getAsJsonObject("value");
                        docsXml.startElement("value");
                        docsXml.startElement("a", "href", valueObj.get("url").getAsString())
                                .addText(valueObj.get("label").getAsString()).endElement();
                        docsXml.endElement(); // value
                        docsXml.endElement(); // param
                    }
                }
            } catch (Exception e) {
                // ничего не делать, просто пропустить
            }
        }
        product.setValue(product_.DOCUMENTS_XML, docsXml.getXmlStringSB().toString());


        // Главная картинка
        JsonArray carouselMedia = data.getAsJsonArray("carouselMedia");
        if (carouselMedia != null && carouselMedia.size() > 0) {
            JsonObject first = carouselMedia.get(0).getAsJsonObject();
            String mainPicUrl = StringUtils.normalizeSpace(first.get("displayUrl").getAsString());
            product.setValue(product_.TO_DOWNLOAD, TupleDataType.newTuple(DigikeyDataUpdaterCommand.IMAGE_LABEL, mainPicUrl));
        }

        // Главный даташит
        JsonElement mainDatasheet = productOverview.get("datasheetUrl");
        if (mainDatasheet != null && StringUtils.isNotBlank(mainDatasheet.getAsString())) {
            product.setValue(product_.TO_DOWNLOAD, TupleDataType.newTuple(DigikeyDataUpdaterCommand.PDF_LABEL, mainDatasheet.getAsString()));
        }

        // Сохранение товара
        executeAndCommitCommandUnits(SaveItemDBUnit.get(product).ignoreFileErrors().noFulltextIndex());
    }

    /**
     * Создать или загрузить все необходимые разделы для сохранения товара
     * @param crumbs
     * @param idx
     * @param parent
     * @return
     * @throws Exception
     */
    private Item ensureSection(JsonArray crumbs, int idx, Item parent) throws Exception {
        if (idx >= crumbs.size() - 1)
            return parent;
        JsonObject crumb = crumbs.get(idx).getAsJsonObject();
        if (idx == 0) {
            parent = ItemUtils.ensureSingleRootAnonymousItem(CATALOG, User.getDefaultUser());
            return ensureSection(crumbs, idx + 1, parent);
        }
        String sectionName = crumb.get("label").getAsString();
        Item section = new ItemQuery(SECTION).setParentId(parent.getId(), false)
                .addParameterEqualsCriteria(section_.NAME, sectionName).loadFirstItem();
        if (section == null) {
            section = ItemUtils.ensureSingleChild(SECTION, getInitiator(), parent);
            section.setValue(section_.NAME, sectionName);
            executeAndCommitCommandUnits(SaveItemDBUnit.get(section));
        }
        if (idx == crumbs.size() - 2)
            return section;
        return ensureSection(crumbs, idx + 1, section);
    }

}
