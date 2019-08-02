package ecommander.fwk.integration;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.MysqlConnector;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.Item;
import ecommander.model.datatypes.DateDataType;
import ecommander.pages.LinkPE;
import ecommander.persistence.mappers.ItemMapper;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.Collection;

public class CreateSitemapCommand extends IntegrateBase implements CatalogConst {
    public static final String SITEMAP = "sitemap";
    public static final String XML = ".xml";
    public static final String PRODUCT_PAGE = "product";
    public static DateTimeFormatter SITEMAP_FORMATTER = DateTimeFormat.forPattern("yyyy-MM-dd").withZoneUTC();

    private XmlDocumentBuilder currentMap = null;
    private XmlDocumentBuilder indexFile = XmlDocumentBuilder.newDoc();
    private int mapCount = 0;

    @Override
    protected boolean makePreparations() throws Exception {
        // Удалить все файлы сайтмапа
        Collection<File> files = FileUtils.listFiles(new File(AppContext.getContextPath()), new String[]{"xml"}, false);
        boolean success = true;
        for (File file : files) {
            if (StringUtils.startsWithIgnoreCase(file.getName(), SITEMAP))
                success &= FileUtils.deleteQuietly(file);
        }
        return success;
    }

    @Override
    protected void integrate() throws Exception {
        indexFile.startElement("sitemapindex", "xmlns", "http://www.sitemaps.org/schemas/sitemap/0.9");
        ArrayList<Item> prods;
        long startFrom = 0;
        int urlsInFileCount = 0;
        info.setProcessed(0);
        do {
            try (Connection conn = MysqlConnector.getConnection()) {
                prods = ItemMapper.loadByName(PRODUCT_ITEM, 50, startFrom, conn);
            }
            if (urlsInFileCount == 0 || urlsInFileCount >= 49999 || (urlsInFileCount > 0 && prods.size() == 0)) {
                newMapFile();
                urlsInFileCount = 0;
            }
            for (Item prod : prods) {
                startFrom = prod.getId();
                LinkPE link = LinkPE.newExclusiveLink(PRODUCT_PAGE, "prod", prod.getKeyUnique(), false);
                currentMap.startElement("url");
                currentMap.startElement("loc").addText(getUrlBase() + link.serialize()).endElement();
                currentMap.startElement("lastmod").addText(DateDataType.outputDate(prod.getTimeUpdated(), SITEMAP_FORMATTER)).endElement();
                currentMap.startElement("changefreq").addText("weekly").endElement();
                currentMap.endElement();
                urlsInFileCount++;
                if (urlsInFileCount >= 49999) {
                    newMapFile();
                    urlsInFileCount = 0;
                }
                info.increaseProcessed();
            }
        } while (prods.size() > 0);
        indexFile.endElement();// sitemapindex
        FileUtils.write(new File(AppContext.getRealPath(SITEMAP + XML)), indexFile.toString(), StandardCharsets.UTF_8);
    }

    private void newMapFile() throws IOException {
        if (currentMap != null) {
            currentMap.endElement();
            String fileName = SITEMAP + (mapCount++) + XML;
            FileUtils.write(new File(AppContext.getRealPath(fileName)), currentMap.toString(), StandardCharsets.UTF_8);
            indexFile.startElement("sitemap");
            indexFile.startElement("loc").addText(getUrlBase() + '/' + fileName).endElement();
            indexFile.startElement("lastmod").addText(DateDataType.outputDate(System.currentTimeMillis(), SITEMAP_FORMATTER)).endElement();
            indexFile.endElement();
        }
        currentMap = XmlDocumentBuilder.newDoc();
        currentMap.startElement("urlset", "xmlns", "http://www.sitemaps.org/schemas/sitemap/0.9");
    }


    @Override
    protected void terminate() throws Exception {

    }
}
