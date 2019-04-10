package extra;

import ecommander.fwk.MysqlConnector;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.ItemMapper;
import org.apache.commons.lang3.StringUtils;

import java.sql.Connection;
import java.util.ArrayList;

/**
 * Created by E on 9/4/2019.
 */
public class FixProducer extends Command {

	public static final String PRODUCT = "product";
	public static final String NAME = "name";
	public static final String PARAMS_XML = "params_xml";
	public static final String XML = "xml";

	@Override
	public ResultPE execute() throws Exception {
		long lastId = 0;
		ItemType paramsXmlType = ItemTypeRegistry.getItemType(PARAMS_XML);
		try (Connection conn = MysqlConnector.getConnection()) {
			ArrayList<Item> products;
			int modified = 0;
			do {
				products = ItemMapper.loadByName(PRODUCT, 100, lastId, conn);
				for (Item product : products) {
					String name = StringUtils.trim(product.getStringValue(NAME));
					if (StringUtils.endsWithIgnoreCase(name, "ND")) {
						Item paramsXml = new ItemQuery(PARAMS_XML).setParentId(product.getId(), false).loadFirstItem();
						if (paramsXml != null
								&& !StringUtils.containsIgnoreCase(paramsXml.getStringValue(XML), "Производитель")
								&& StringUtils.isNotBlank(product.getStringValue("vendor"))) {
							XmlDocumentBuilder xmlBuilder = XmlDocumentBuilder.newDocFull(paramsXml.getStringValue(XML));
							xmlBuilder
									.startElement("parameter")
									.startElement("name").addText("Производитель").endElement()
									.startElement("value").addText(product.getStringValue("vendor")).endElement()
									.endElement();
							paramsXml.setValue(XML, xmlBuilder.toString());
							executeAndCommitCommandUnits(SaveItemDBUnit.get(paramsXml));
						}
					}
					lastId = product.getId();
				}
				modified += 100;
				ServerLogger.warn("\n\n\n\n\n\t\t\tMODIFIED - " + modified + "\n\n\n\n\n\n");
			} while (products.size() > 0);
		}

		return null;
	}
}
