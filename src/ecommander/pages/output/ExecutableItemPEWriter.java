package ecommander.pages.output;

import ecommander.controllers.AppContext;
import ecommander.fwk.Strings;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.Item;
import ecommander.model.ItemType;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.ParameterDescription;
import ecommander.model.datatypes.DataType;
import ecommander.pages.ExecutableItemPE;
import ecommander.pages.PageElement;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.text.StringEscapeUtils;

import java.util.ArrayList;

import static ecommander.admin.MainAdminPageCreator.*;

public class ExecutableItemPEWriter implements PageElementWriter {

	public static final String ITEM_COUNT_ELEMENT_SUFFIX = "_count";
	public static final String ID_ATTRIBUTE = "id";
	public static final String TYPE_ATTRIBUTE = "type";
	public static final String PATH_ATTRIBUTE = "path";
	public static final String KEY_ATTRIBUTE = "key";
	
	public static final String ADMIN_PARAMETERS_ATTRIBUTE = "adm-params";
	public static final String ADMIN_FULL_ATTRIBUTE = "adm-full";
	public static final String ADMIN_DELETE_ATTRIBUTE = "adm-delete";
	public static final String ADMIN_SIBLING_ATTRIBUTE = "adm-sibling";

	public static final String OWN_EXTRAS_ELEMENT = "item_own_extras";
	/**
	 * Вывести один найденный айтем (и запустить вывод сабайтемов этого айтема)
	 * В этом методе происходит вызов next итератора. Сам метод возвращает результат этого вызова next
	 * @param itemToWrite
	 * @param xml
	 * @param isVisualEditing
	 * @throws Exception
	 * @return
	 */
	private boolean writeItem(ExecutableItemPE itemToWrite, XmlDocumentBuilder xml, boolean isVisualEditing) throws Exception {
		ExecutableItemPE.ParentRelatedFoundIterator iter = itemToWrite.getParentRelatedFoundItemIterator();
		Item item = iter.getCurrentItem();
		// Создать тэг для айтема и добавить его в структуру
		String tagName;
		if (!StringUtils.isBlank(itemToWrite.getTag())) {
			tagName = itemToWrite.getTag();
		} else {
			if (!item.getItemType().isUserDefined()) {
				tagName = item.getTypeName();
			} else {
				tagName = findTag(item.getTypeName());
			}
		}
		// <item id="123" path="sitefiles/1/20/255/4055" updated="190456373"> (ID айтема, путь к файлам айтема)
		xml.startElement(tagName, TYPE_ATTRIBUTE, item.getTypeName(), ID_ATTRIBUTE, item.getId(), PATH_ATTRIBUTE,
				AppContext.getFilesUrlPath(item.isFileProtected()) + item.getRelativeFilesPath(), KEY_ATTRIBUTE, item.getKeyUnique());
		// Если включен режим визуального редактирования
		if (isVisualEditing)
			addConentUpdateAttrs(xml, item);
		// Параметры айтема
		// Если айтем содержит XML параметры, то им надо убрать эскейпинг
		if (item.getItemType().hasXML()) {
			//xml.addElements(StringEscapeUtils.unescapeXml(item.outputValues()));
			String values = item.outputValues();
			for (ParameterDescription paramDescr : item.getItemType().getParameterList()) {
				if (paramDescr.getType() == DataType.Type.XML) {
					String start = "<" + paramDescr.getName() + ">";
					String end = "</" + paramDescr.getName() + ">";
					String xmlVal = StringUtils.substringBetween(values, start, end);
					xmlVal = StringEscapeUtils.unescapeXml(xmlVal);
					values = StringUtils.substringBefore(values, start) + start + xmlVal + end + StringUtils.substringAfter(values, end);
				}
			}
			xml.addElements(values);
		} else {
			xml.addElements(item.outputValues());
		}
		// Вывести поля extra (нужны в частности при полнотекстовом поиске для подсветки результатов
		if (item.hasExtras()) {
			xml.startElement(OWN_EXTRAS_ELEMENT);
			for (String key : item.getExtraKeys()) {
				xml.startElement(Strings.createXmlElementName(key)).addElements(item.getStringExtra(key)).endElement();
			}
			xml.endElement();
		}
		// Вывести остальные вложенные элементы айтема и вложенные сабайтемы
		for (PageElement element : itemToWrite.getAllNested()) {
			PageElementWriterRegistry.getWriter(element).write(element, xml);
		}
		// Проверить, есть ли вложенные айтемы
		boolean hasNext = iter.next();
		//while (hasNext && iter.getCurrentNestedLevel() > nestedLevel) {
		while (hasNext && (iter.getCurrentItem().getContextParentId() == item.getId())) {
			hasNext = writeItem(itemToWrite, xml, isVisualEditing);
		}
		// </item>
		xml.endElement();
		return hasNext;
	}
	/**
	 * Вывести список айтемов
	 */
	public void write(PageElement elementToWrite, XmlDocumentBuilder xml) throws Exception {
		ExecutableItemPE executableItem = (ExecutableItemPE)elementToWrite;
		if (executableItem.isVirtual())
			return;
		// Если есть кеш - просто вывести его содержимое как элементы XML и завершить выполнение метода
		if (executableItem.hasCacheContents()) {
			xml.addElements(executableItem.getCachedContents());
			return;
		}
		// Если нет кеша - произвести стандартный вывод элемента
		// Создается элемент <item_count>10</item_count>
		String tagName = StringUtils.isBlank(executableItem.getTag()) ? executableItem.getItemName() : executableItem.getTag();
		tagName += ITEM_COUNT_ELEMENT_SUFFIX;
		String quantity = Integer.toString(executableItem.getParentRelatedFoundItemIterator().getTotalQuantity());
		xml.startElement(tagName).addText(quantity).endElement();
		// Выводится фильтр, если он есть
		if (executableItem.hasFilter()) {
			new FilterPEWriter().write(executableItem, xml);
		}
		// Выводятся все найденные айтемы
		if (executableItem.getParentRelatedFoundItemIterator().next()) {
			boolean hasNext;
			do {
				hasNext = writeItem(executableItem, xml, executableItem.getSessionContext().isContentUpdateMode());
			} while (hasNext);
		}
	}
	/**
	 * Определить тэг для айтема
	 * Требуется найти первый не определенный пользователем родительский тип айтема для данного (пользовательского) типа айтема
	 * @param childTypeName
	 * @return
	 */
	private String findTag(String childTypeName) {
		ArrayList<String> predecessorNames = ItemTypeRegistry.getDirectParents(childTypeName);
		int i = 0;
		while (true) {
			String predecessorName = predecessorNames.get(i);
			ItemType itemDesc = ItemTypeRegistry.getItemType(predecessorName);
			if (!itemDesc.isUserDefined()) {
				return itemDesc.getName();
			} else {
				predecessorNames.addAll(ItemTypeRegistry.getDirectParents(itemDesc.getName()));
			}
			i++;
			if (i >= predecessorNames.size()) break;
		}
		return "undefiled";
	}
	/**
	 * Добавить тэги для визуального редактирования
	 * @param itemTag
	 * @param item
	 */
	private void addConentUpdateAttrs(XmlDocumentBuilder itemTag, Item item) {
		String paramsUrl = createAdminUrl(GET_VIEW_ACTION, VIEW_TYPE_INPUT, PARAMS_VIEW_TYPE, ITEM_ID_INPUT, item.getId());
		String allUrl = createAdminUrl(SET_ITEM_ACTION, ITEM_ID_INPUT, item.getId(), ITEM_TYPE_INPUT, item.getTypeId());
		String deleteUrl = createAdminUrl(DELETE_ITEM_ACTION, ITEM_ID_INPUT, item.getId(), PARENT_ID_INPUT, item.getContextParentId());
		String newSiblingUrl = createAdminUrl(CREATE_ITEM_ACTION, ITEM_TYPE_INPUT, item.getTypeId(), PARENT_ID_INPUT,
				item.getContextParentId());
		itemTag.insertAttributes(ADMIN_PARAMETERS_ATTRIBUTE, paramsUrl, ADMIN_FULL_ATTRIBUTE, allUrl, ADMIN_DELETE_ATTRIBUTE, deleteUrl,
				ADMIN_SIBLING_ATTRIBUTE, newSiblingUrl);
	}

//	public static void main(String[] args) throws XMLStreamException {
//        XMLOutputFactory xof = XMLOutputFactory.newInstance();
//        XMLStreamWriter xtw = null;
//        xtw = xof.createXMLStreamWriter(System.out);
//        xtw.writeStartDocument("utf-8", "1.0");
//        xtw.writeStartElement("item");
//        //xtw.writeCharacters("<param name=\"mega\">cool</param>");
//        xtw.writeCData("<param name=\"mega\">cool</param>");
//        xtw.writeEndElement();
//        xtw.flush();
//	}
}