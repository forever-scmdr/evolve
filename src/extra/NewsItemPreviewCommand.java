package extra;

import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.itemquery.ItemQuery;

import java.util.List;

public class NewsItemPreviewCommand extends Command {

	private static final Byte[] ASSOC_IDS = new Byte[]{ItemTypeRegistry.getPrimaryAssocId(), ItemTypeRegistry.getAssocId("news")};

	@Override
	public ResultPE execute() throws Exception {
		long id = Long.parseLong(getVarSingleValue("id"));
		XmlDocumentBuilder content = loadItemContent(id);
		ResultPE result = getResult("success");
		result.setValue(content.toString());
		return result;
	}

	private XmlDocumentBuilder loadItemContent(long id) throws Exception {
		Item item = ItemQuery.loadById(id);
		if(item == null){return XmlDocumentBuilder.newDocPart();}
		XmlDocumentBuilder doc = XmlDocumentBuilder.newDocPart();
		startItem(item, doc);

		List<Item> children = ItemQuery.loadByParentId(item.getId(), ASSOC_IDS);
		for(Item c : children){
			loadItemContent(c, doc);
		}
		doc.endElement();
		return doc;
	}

	private void loadItemContent(Item item, XmlDocumentBuilder doc) throws Exception{
		startItem(item, doc);
		List<Item> children = ItemQuery.loadByParentId(item.getId(), ASSOC_IDS);
		for(Item c : children){
			loadItemContent(c, doc);
		}
		doc.endElement();
	}

	private void startItem(Item item, XmlDocumentBuilder doc){
		long id = item.getId();
		String itemType = item.getTypeName();
		String key = item.getKeyUnique();
		String path = "files/" + item.getRelativeFilesPath();

		doc.startElement(itemType, "id", id, "path", path, "key", key);
		doc.addElements(item.outputValues());
	}
}