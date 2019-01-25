package ecommander.fwk.integration;

import ecommander.fwk.ItemEventCommandFactory;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.Item;
import ecommander.model.ParameterDescription;
import ecommander.persistence.commandunits.DBPersistenceCommandUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.PersistenceCommandUnit;
import ecommander.persistence.itemquery.ItemQuery;

/**
 * Created by E on 25/1/2019.
 */
public class UpdateProductParamsXml implements ItemEventCommandFactory {

	private static final String PRODUCT = "described_product";
	private static final String PARAMS_XML = "params_xml";

	private static final String PARAMETER = "parameter";
	private static final String NAME = "name";
	private static final String VALUE = "value";

	private static class Command extends DBPersistenceCommandUnit {

		private Item paramsItem;

		public Command(Item paramsItem) {
			this.paramsItem = paramsItem;
		}

		@Override
		public void execute() throws Exception {
			Item product = new ItemQuery(PRODUCT).setChildId(paramsItem.getId(), false).loadFirstItem();
			if (product != null) {
				XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();
				for (ParameterDescription param : paramsItem.getItemType().getParameterList()) {
					if (paramsItem.isValueNotEmpty(param.getName())) {
						xml.startElement(PARAMETER).startElement(NAME).addText(param.getCaption()).endElement();
						for (String value : paramsItem.outputValues(param.getName())) {
							xml.startElement(VALUE).addText(value).endElement();
						}
						xml.endElement();
					}
				}
				product.setValue(PARAMS_XML, xml.toString());
				executeCommandInherited(SaveItemDBUnit.get(product).noFulltextIndex().noTriggerExtra());
			}
		}
	}

	@Override
	public PersistenceCommandUnit createCommand(Item item) throws Exception {
		return new Command(item);
	}
}
