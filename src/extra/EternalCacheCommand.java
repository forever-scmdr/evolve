package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.pages.output.ExecutableItemPEWriter;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;

public class EternalCacheCommand extends Command{
    private static final Path FOLDER = Paths.get(AppContext.getContextPath(), "eternal_cache");
    private static final String PROD_ID = "prod";
    private static final String PARENT_SEC_ID = "parent";
    private static final String PARENT_SEC_TAG = "product_section";
    private static final String PRODUCT_TAG = "product";
    private static final String PARAMS_ID = "params";


    public static boolean cacheExists(String key) {
        return Files.isRegularFile(FOLDER.resolve(key + ".txt"));
    }

    @Override
    public ResultPE execute() throws Exception {
        Item product = getSingleLoadedItem(PROD_ID);
        if (product != null) {
            if (!cacheExists(product.getKeyUnique())) {
                saveToCache(product);
            }
            setPageVariable("name", product.getStringValue(ItemNames.product_.NAME,"-none-"));
            setPageVariable("vendor_code", product.getStringValue(ItemNames.product_.VENDOR_CODE,"-none-"));
            for (String v : product.getStringValues(ItemNames.product_.ASSOC_CODE)) {
                setPageVariable("assoc_code", v);
            }
            return null;
        } else {
            String productCache = new String(Files.readAllBytes(FOLDER.resolve(getVarSingleValue(PROD_ID) + ".txt")), StandardCharsets.UTF_8);
            if (StringUtils.isNotBlank(productCache)) {
                try {
                    Document doc = Jsoup.parseBodyFragment(productCache);
                    String name = doc.select("product > name").text();
                    String vendorCode = doc.select("product > vendor_code").text();

                    if(StringUtils.isNotBlank(name)) {
                        setPageVariable("name", name);
                    }else{
                        setPageVariable("name", "-none-");
                    }
                    if(StringUtils.isNotBlank(vendorCode)) {
                        setPageVariable("vendor_code", vendorCode);
                    }else{
                        setPageVariable("vendor_code", "-none-");
                    }

                    Elements ass = doc.select("product > assoc_code");
                    for (Element e : ass) {
                        String v = e.ownText();
                        setPageVariable("assoc_code", v);
                    }
                } catch (Exception e) {
                    ServerLogger.error(e);
                    setPageVariable("assoc_code", "-none-");
                    setPageVariable("vendor_code", "-none-");

                }
            }
            ResultPE res = getResult("loaded");
            res.setValue(productCache);
            return res;
        }
    }


    private void saveToCache(Item product) throws IOException {
        product.clearValue(ItemNames.product_.PRICE);
        product.clearValue(ItemNames.product_.QTY);
        product.setValue(ItemNames.product_.AVAILABLE, (byte) 0);
        Collection<Item> parentSecs = getLoadedChildItems(PARENT_SEC_ID, product.getId()).values();
        Collection<Item> params = getLoadedChildItems(PARAMS_ID, product.getId()).values();

        XmlDocumentBuilder cacheXml = XmlDocumentBuilder.newDocPart();
        startItem(product, PRODUCT_TAG, cacheXml);
        cacheXml.addElements(product.outputValues());
        for (Item param : params) {
            startItem(param, "params", cacheXml);
            cacheXml.addElements(param.outputValues());
            cacheXml.endElement();
        }
        for (Item parent : parentSecs) {
            parent.clearValue("params_filter");
            startItem(parent, PARENT_SEC_TAG, cacheXml);
            cacheXml.addElements(parent.outputValues());
            cacheXml.endElement();
        }
        cacheXml.endElement();
        if (!Files.exists(FOLDER)) {
            Files.createDirectories(FOLDER);
        }
        Files.write(FOLDER.resolve(product.getKeyUnique() + ".txt"), cacheXml.toString().getBytes(StandardCharsets.UTF_8));
    }

    private void startItem(Item item, String tag, XmlDocumentBuilder doc) {
        doc.startElement(tag,
                ExecutableItemPEWriter.ID_ATTRIBUTE, item.getId()
                , ExecutableItemPEWriter.TYPE_ATTRIBUTE, item.getTypeName()
                , ExecutableItemPEWriter.KEY_ATTRIBUTE, item.getKeyUnique()
                , ExecutableItemPEWriter.PATH_ATTRIBUTE, "files/" + item.getRelativeFilesPath());
    }
}
