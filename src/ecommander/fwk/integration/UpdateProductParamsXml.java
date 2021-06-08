package ecommander.fwk.integration;

import ecommander.fwk.ItemEventCommandFactory;
import ecommander.fwk.JsoupUtils;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.ParameterDescription;
import ecommander.persistence.commandunits.DBPersistenceCommandUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.PersistenceCommandUnit;
import ecommander.persistence.itemquery.ItemQuery;
import org.jsoup.nodes.Document;

/**
 * Created by E on 25/1/2019.
 */
public class UpdateProductParamsXml implements ItemEventCommandFactory {

	private static final String PRODUCT = "described_product";
	private static final String PARAMS_XML = "params_xml";
	private static final String XML = "xml";

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
				Item paramsXml = new ItemQuery(PARAMS_XML).setParentId(product.getId(), false).loadFirstItem();
				boolean hasNoXml = paramsXml == null || paramsXml.isValueEmpty(XML);
				// попытка разобрать имеющийся xml параметров
				if (!hasNoXml) {
					try {
						Document doc = JsoupUtils.parseXml(paramsXml.getStringValue(XML));
						for (ParameterDescription param : paramsItem.getItemType().getParameterList()) {
							/*
							if (paramsItem.isValueNotEmpty(param.getName())) {
								xml.startElement(PARAMETER).startElement(NAME).addText(param.getCaption()).endElement();
								for (String value : paramsItem.outputValues(param.getName())) {
									xml.startElement(VALUE).addText(value).endElement();
								}
								xml.endElement();
							}
							*/
						}
					} catch (Exception e) {
						hasNoXml = true;
					}
				}
				// создание нового xml параметров, если разбор старого по какой-то причине не возможен
				if (hasNoXml) {
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
					if (paramsXml == null) {
						paramsXml = Item.newChildItem(ItemTypeRegistry.getItemType(PARAMS_XML), product);
					}
					paramsXml.setValue(XML, xml.toString());
				}
				executeCommandInherited(SaveItemDBUnit.get(paramsXml).noFulltextIndex().noTriggerExtra());
			}
		}
	}

	@Override
	public PersistenceCommandUnit createCommand(Item item) throws Exception {
		return new Command(item);
	}
}
