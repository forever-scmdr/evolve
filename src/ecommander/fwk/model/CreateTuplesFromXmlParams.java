package ecommander.fwk.model;

import ecommander.fwk.ItemEventCommandFactory;
import ecommander.fwk.integration.CatalogConst;
import ecommander.model.Item;
import ecommander.model.datatypes.TupleDataType;
import ecommander.persistence.commandunits.DBPersistenceCommandUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.PersistenceCommandUnit;
import ecommander.persistence.mappers.ItemMapper;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

public class CreateTuplesFromXmlParams implements ItemEventCommandFactory, CatalogConst {

    public static class CreateTuples extends DBPersistenceCommandUnit {

        public CreateTuples(Item product) {
            this.product = product;
        }

        private Item product;

        @Override
        public void execute() throws Exception {
            if (product.getParameterByName(PARAMS_XML_PARAM).hasChanged()) {
                product.clearValue(PARAM_VALS_PARAM);
                String xml = "<params>" + product.getStringValue(PARAMS_XML_PARAM) + "</params>";
                Document paramsTree = Jsoup.parse(xml, "localhost", Parser.xmlParser());
                Elements paramEls = paramsTree.select(PARAMETER + ", " + PARAM);
                for (Element paramEl : paramEls) {
                    Elements nameElements = paramEl.getElementsByTag(NAME);
                    if (!nameElements.isEmpty()) {
                        Element nameEl = nameElements.first();
                        String name = StringUtils.trim(nameEl != null ? nameEl.ownText() : null);
                        if (StringUtils.isNotBlank(name)) {
                            Elements values = paramEl.getElementsByTag(VALUE);
                            for (Element value : values) {
                                product.setValue(PARAM_VALS_PARAM, TupleDataType.newTuple(name, StringUtils.trim(value.ownText())));
                            }
                        }
                    }
                }
                executeCommand(SaveItemDBUnit.get(product).noTriggerExtra().noFulltextIndex());
            }
        }
    }

    @Override
    public PersistenceCommandUnit createCommand(Item item) throws Exception {
        return new CreateTuples(item);
    }
}
