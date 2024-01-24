package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.CharSeparatedTxtTableData;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.ItemUtils;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.fwk.integration.CatalogConst;
import ecommander.fwk.integration.CreateParametersAndFiltersCommand;
import ecommander.model.*;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.charset.Charset;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.TreeSet;

public class IntegrateTxt extends IntegrateBase implements CatalogConst {

    private Item section;

    @Override
    protected boolean makePreparations() throws Exception {
        Item catalog = ItemUtils.ensureSingleRootItem(CATALOG_ITEM, getInitiator(), UserGroupRegistry.getDefaultGroup(), User.ANONYMOUS_ID);
        String sectionName = getVarSingleValueDefault("section", "Гидроцилиндры");
        section = ItemQuery.loadSingleItemByParamValue(SECTION_ITEM, NAME_PARAM, sectionName);
        if (section == null) {
            section = Item.newChildItem(ItemTypeRegistry.getItemType(SECTION_ITEM), catalog);
            section.setValue(NAME_PARAM, sectionName);
            executeAndCommitCommandUnits(SaveItemDBUnit.get(section));
        }
        return true;
    }

    @Override
    protected void integrate() throws Exception {
        getInfo().setOperation("Поиск файлов интеграции");
        getInfo().pushLog("Поиск файлов интеграции");
        File dir = new File(AppContext.getRealPath("integrate"));
        Collection<File> files = FileUtils.listFiles(dir, new String[]{"txt"}, true);
        if (files.size() == 0) {
            getInfo().addError("Не найдены .txt файлы в директории интеграции", dir.getAbsolutePath());
            return;
        }
        getInfo().pushLog("Найдено {} файлов", files.size());
        getInfo().setProcessed(0);
        getInfo().setOperation("Создание товаров");
        final ItemType productType = ItemTypeRegistry.getItemType(PRODUCT_ITEM);
        final ItemType paramsXmlType = ItemTypeRegistry.getItemType(PARAMS_XML_ITEM);
        for (File file : files) {
            getInfo().pushLog("Обработка {}", file.getName());
            try (CharSeparatedTxtTableData data = new CharSeparatedTxtTableData(file.toPath(), Charset.forName("Cp1251"))) {
                final LinkedHashSet<String> headers = data.getHeaders();
                data.iterate(src -> {
                    String id = src.getValue("Обозначение");
                    if (StringUtils.isBlank(id))
                        return;
                    Item product = ItemQuery.loadSingleItemByParamValue(PRODUCT_ITEM, CODE_PARAM, id);
                    if (product == null) {
                        product = Item.newChildItem(productType, section);
                        product.setValue(CODE_PARAM, id);
                    }
                    product.setValue(NAME_PARAM, id);
                    XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();
                    for (String header : headers) {
                        if (StringUtils.equalsIgnoreCase(header, "Обозначение"))
                            continue;
                        String value = src.getValue(header);
                        if (StringUtils.isNotBlank(value)) {
                            xml.startElement("parameter");
                            xml.addElement("name", StringUtils.capitalize(header)).addElement("value", value);
                            xml.endElement();
                        }
                    }
                    product.setValue(LABEL_PARAM, src.getValue("Применяемость"));
                    String tech = src.getValue("Технические особенности");
                    if (StringUtils.isNotBlank(tech)) {
                        product.setValue(TAG_PARAM, "Телескопические");
                    } else {
                        String dd = src.getValue("D-d");
                        String d = StringUtils.substringBefore(dd, "-");
                        if (StringUtils.isNotBlank(d)) {
                            product.setValue(TAG_PARAM, "D=" + d);
                        }
                    }
                    executeAndCommitCommandUnits(SaveItemDBUnit.get(product));
                    Item paramsXml = Item.newChildItem(paramsXmlType, product);
                    paramsXml.setValue(XML_PARAM, xml.toString());
                    executeAndCommitCommandUnits(SaveItemDBUnit.get(paramsXml));
                    getInfo().increaseProcessed();
                });
            }
        }
        getInfo().pushLog("Создание товаров завершено");
        CreateParametersAndFiltersCommand filtersCreateCommand = new CreateParametersAndFiltersCommand(this);
        executeOtherIntegration(filtersCreateCommand);
    }

    @Override
    protected void terminate() throws Exception {

    }
}
