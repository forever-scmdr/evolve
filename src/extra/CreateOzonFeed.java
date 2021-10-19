package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Item;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class CreateOzonFeed extends IntegrateBase implements CatalogConst {

    private static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("dd-MM-YYYY_HH-mm-ss");
    private static final String OUTPUT_FOLDER = AppContext.getFilesDirPath(false);
    private static final String REGEX = "ozon_feed_\\d{2}-\\d{2}-\\d{4}_\\d{2}-\\d{2}-\\d{2}\\.xml";
    private XmlDocumentBuilder doc = XmlDocumentBuilder.newDoc();

    @Override
    protected boolean makePreparations() throws Exception {
       return true;
    }

    @Override
    protected void integrate() throws Exception {
        setOperation("Deleting old Ozon Feed");
        Files.walk(Paths.get(AppContext.getFilesDirPath(false))).filter(f->f.getFileName().toString().matches(REGEX)).forEach(path -> {
            try {
                Files.delete(path);
            } catch (IOException e) {
                info.addError(ExceptionUtils.getStackTrace(e),-1,-1);
            }
        });
        //Files.deleteIfExists(OUTPUT_FILE);
        info.setProcessed(0);
        setOperation("Creating feed");
        doc.startElement("yml_catalog").startElement("shop").startElement("offers");
        ItemQuery q = new ItemQuery(PRODUCT_ITEM);
        int page = 1;
        int limit = 1000;
        q.setLimit(limit,page);
        List<Item> products;
        while ((products = q.loadItems()).size() > 0){
            for(Item product : products){
                createProductXml(product);
            }
            page++;
            q.setLimit(limit,page);
        }
        doc.endElement().endElement().endElement();

        String fileName = String.format("ozon_feed_%s.yml", DATE_FORMAT.format(new Date()));
        Path outputFile = Paths.get(OUTPUT_FOLDER, fileName);
        Files.write(outputFile, doc.toString().getBytes(StandardCharsets.UTF_8));
        pushLog(getUrlBase()+"/files/"+fileName);
        setOperation("Complete");
    }

    private void createProductXml(Item product) {
        String code = product.getStringValue(CODE_PARAM);
        if(StringUtils.startsWith(code, "gift-")) return;
        doc.startElement("offer", "id", code);
        doc.addElement("price", product.getValue(PRICE_PARAM));
        doc.addElement("oldprice", product.getValue(PRICE_OLD_PARAM,0));
        doc.addElement("premium_price",0);
        doc.startElement("outlets");
        doc.addEmptyElement("outlet", "instock", (int)product.getDoubleValue(QTY_PARAM,0));
        doc.endElement();
        doc.endElement();
        info.increaseProcessed();
    }
}
