package ecommander.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ecommander.common.ServerLogger;
import ecommander.common.Strings;
import ecommander.controllers.AppContext;
import ecommander.controllers.parsing.ModelValidator;

public class DataModelCreationValidator extends ModelValidator {

	private static class Element {
		protected int lineNumber;
		protected Element(int lineNumber) {
			this.lineNumber = lineNumber;
		}
	}
	
	private static class SubitemContainer extends Element {
		protected ArrayList<Subitem> subitems;
		protected SubitemContainer(int lineNumber) {
			super(lineNumber);
			this.subitems = new ArrayList<Subitem>();			
		}
	}
	
	private static class Item extends SubitemContainer {
		
		private Item(String name, String caption, String key, int lineNumber, boolean isVirtual) {
			super(lineNumber);
			this.name = name;
			this.caption = caption;
			this.key = key;
			this.isVirtual = isVirtual;
			this.predecessors = new ArrayList<String>();
			this.parameters = new ArrayList<Parameter>();
		}
		public final String name;
		@SuppressWarnings("unused")
		public final String caption;
		public final String key;
		public final boolean isVirtual;
		public final ArrayList<String> predecessors;
		public final ArrayList<Parameter> parameters;
	}
	
	private static class Subitem extends Element {
		private Subitem(String name, String parentName, boolean isSingle, boolean isVirtual, int lineNumber) {
			super(lineNumber);
			this.name = name;
			this.isSingle = isSingle;
			this.isVirtual = isVirtual;
			this.parentName = parentName;
		}
		public final String name;
		public final String parentName;
		public final boolean isSingle;
		@SuppressWarnings("unused")
		public final boolean isVirtual;
	}
	
	private static class Parameter extends Element {
		private Parameter(String name, String type, boolean isSingle, String caption, int lineNumber) {
			super(lineNumber);
			this.name = name;
			this.type = type;
			this.isSingle = isSingle;
			this.caption = caption;
		}
		public final String name;
		@SuppressWarnings("unused")
		public final String type;
		@SuppressWarnings("unused")
		public final boolean isSingle;
		@SuppressWarnings("unused")
		public final String caption;
	}
	
	private static class Root extends SubitemContainer {
		private Root(String group, int lineNumber) {
			super(lineNumber);
			this.group = group;
		}
		private String group;
	}
	/**
	 * Элементы
	 */
	private static final String ITEMS_ELEMENT = "items";
	private static final String ITEM_ELEMENT = "item";
	private static final String ROOT_ELEMENT = "root";
	private static final String NAME_ATTRIBUTE = "name";
	private static final String PARAMETER_ELEMENT = "parameter";
	private static final String SUBITEM_ELEMENT = "subitem";
	/**
	 * Атрибуты
	 */
	private static final String TYPE_ATTRIBUTE = "type";
	private static final String QUANTIFIER_ATTRIBUTE = "quantifier";
	private static final String CAPTION_ATTRIBUTE = "caption";
//	private static final String DESCRIPTION_ATTRIBUTE = "description";
//	private static final String DOMAIN_ATTRIBUTE = "domain";
//	private static final String FORMAT_ATTRIBUTE = "format";
	private static final String GROUP_ATTRIBUTE = "group";
	private static final String KEY_ATTRIBUTE = "key";
	private static final String EXTENDS_ATTRIBUTE = "extends";
	private static final String VIRTUAL_ATTRIBUTE = "virtual";

	/**
	 * Значения
	 */
	private static final String SINGLE_VALUE = "single";
	private static final String MULTIPLE_VALUE = "multiple";
	
	private static final String TRUE_VALUE = "true";	
	
	private class DataModelHandler extends DefaultHandler {

		private Locator locator;
		private int errorCounter = 0;
		private boolean criticalError = false; // ошибка, после которой дальнейший анализ не возможен
		
		private Stack<Element> stack = new Stack<Element>();
		
		@Override
		public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
			if (criticalError) {
				return;
			}
			if (ITEM_ELEMENT.equalsIgnoreCase(qName)) {
				String name = attributes.getValue(NAME_ATTRIBUTE);
				String caption = attributes.getValue(CAPTION_ATTRIBUTE);
				String key = attributes.getValue(KEY_ATTRIBUTE);
				boolean isVirtual = TRUE_VALUE.equalsIgnoreCase(attributes.getValue(VIRTUAL_ATTRIBUTE));
				if (StringUtils.isBlank(name)) {
					addError("Name not set. Item 'name' attribute must not be empty.", locator.getLineNumber());
					name = "error" + (errorCounter++);
				}
				if (StringUtils.isBlank(caption) && !isVirtual)
					addError("Caption not set. Item 'caption' attribute must not be empty.", locator.getLineNumber());
				if (items.containsKey(name)) {
					addError("Duplicate item name '" + name + "'. All items must have unique names", locator.getLineNumber());
					criticalError = true;
				}
				Item item = new Item(name, caption, key, locator.getLineNumber(), isVirtual);
				String strExtends = attributes.getValue(EXTENDS_ATTRIBUTE);
				if (strExtends != null) {
					String[] parents = StringUtils.split(strExtends, ItemType.COMMON_DELIMITER);
					for (String parent : parents) {
						if (!StringUtils.isBlank(parent) && !parent.equals(ItemType.ITEM_SELF))
							item.predecessors.add(parent);
					}
				}
				// Сохранение
				stack.push(item);
				items.put(name, item);
			}
			else if (SUBITEM_ELEMENT.equalsIgnoreCase(qName)) {
				Element parent = stack.peek();
				if (!(parent instanceof SubitemContainer)) {
					addError("Subitem element is in wrong place. 'subitem' most be a child of 'item'", locator.getLineNumber());
					return;
				}
				String childName = attributes.getValue(NAME_ATTRIBUTE);
				if (StringUtils.isBlank(childName)) {
					addError("Name not set. Subitem 'name' attribute must not be empty.", locator.getLineNumber());
					return;
				}
				if (((SubitemContainer)parent).subitems.contains(childName)) {
					addError("Duplicate subitem name: " + childName, locator.getLineNumber());
					return;
				}
				String quantifierStr = attributes.getValue(QUANTIFIER_ATTRIBUTE);
				if (quantifierStr == null) quantifierStr = Strings.EMPTY;
				if (!quantifierStr.equalsIgnoreCase(SINGLE_VALUE) && !quantifierStr.equalsIgnoreCase(MULTIPLE_VALUE))
					addError("Subitem quantifier not set. Subitem 'quantifier' attribute must not be empty.", locator.getLineNumber());
				String virtualityStr = attributes.getValue(VIRTUAL_ATTRIBUTE);
				if (virtualityStr == null) virtualityStr = Strings.EMPTY;
				boolean isSingle = !quantifierStr.equalsIgnoreCase(MULTIPLE_VALUE);
				boolean isVirtual = virtualityStr.equalsIgnoreCase(TRUE_VALUE);
				String parentName = "";
				if (parent instanceof Item) {
					parentName = ((Item)parent).name;
				} else if (parent instanceof Root) {
					parentName = ((Root)parent).group;
				}
				Subitem subitem = new Subitem(childName, parentName, isSingle, isVirtual, locator.getLineNumber());
				// Сохранение
				stack.push(subitem);
				((SubitemContainer)parent).subitems.add(subitem);
				subitems.add(subitem);
			}
			else if (PARAMETER_ELEMENT.equalsIgnoreCase(qName)) {
				Element parent = stack.peek();
				if (!(parent instanceof Item)) {
					addError("Parameter element is in wrong place. 'parameter' most be a child of 'item'", locator.getLineNumber());
					return;
				}
				String name = attributes.getValue(NAME_ATTRIBUTE);
				if (StringUtils.isBlank(name)) {
					addError("Name not set. Subitem 'name' attribute must not be empty.", locator.getLineNumber());
					return;
				}
				if (((Item)parent).parameters.contains(name)) {
					addError("Duplicate parameter name: " + name, locator.getLineNumber());
					return;
				}
				String caption = attributes.getValue(CAPTION_ATTRIBUTE);
				if (StringUtils.isBlank(caption) && !((Item)parent).isVirtual)
					addError("Caption not set. Parameter 'caption' attribute must not be empty.", locator.getLineNumber());
				String quantifierStr = attributes.getValue(QUANTIFIER_ATTRIBUTE);
				if (quantifierStr == null) quantifierStr = Strings.EMPTY;
//				if (!quantifierStr.equalsIgnoreCase(SINGLE_VALUE) && !quantifierStr.equalsIgnoreCase(MULTIPLE_VALUE))
//					addError("Parameter quantifier not set. Parameter 'quantifier' attribute must not be empty.", locator.getLineNumber());
				String type = attributes.getValue(TYPE_ATTRIBUTE);
				if (!DataTypeRegistry.isTypeNameValid(type))
					addError("Parameter type is incorrect.", locator.getLineNumber());
				boolean isSingle = !quantifierStr.equalsIgnoreCase(MULTIPLE_VALUE);
				
				Parameter parameter = new Parameter(name, type, isSingle, caption, locator.getLineNumber());
				// Сохранение
				stack.push(parameter);
				((Item)parent).parameters.add(parameter);
			}
			else if (ROOT_ELEMENT.equalsIgnoreCase(qName)) {
				if (stack.size() > 0) {
					addError("Root element is in wrong place. 'root' most not be a child of any other node", locator.getLineNumber());
					return;
				}
				String group = attributes.getValue(GROUP_ATTRIBUTE);
				if (StringUtils.isBlank(group)) {
					addError("Group not set. Root 'group' attribute must not be empty.", locator.getLineNumber());
					return;
				}
				Root root = new Root(group, locator.getLineNumber());
				// Сохранение
				stack.push(root);
				roots.add(root);
			}
			else if (!ITEMS_ELEMENT.equalsIgnoreCase(qName)) {
				addError("Invalid '" + qName + "' element", locator.getLineNumber());
			}
		}
		
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (criticalError)
				return;
			if (ROOT_ELEMENT.equalsIgnoreCase(qName) || 
					PARAMETER_ELEMENT.equalsIgnoreCase(qName) || 
					SUBITEM_ELEMENT.equalsIgnoreCase(qName) || 
					ITEM_ELEMENT.equalsIgnoreCase(qName))
				stack.pop();
		}

		@Override
		public void setDocumentLocator(Locator locator) {
			this.locator = locator;
		}

	
	}
	
	private HashMap<String, Item> items;
	private ArrayList<Subitem> subitems;
	private ArrayList<Root> roots;
	
	public DataModelCreationValidator() {
		items = new HashMap<String, Item>();
		subitems = new ArrayList<Subitem>();
		roots = new ArrayList<Root>();
	}

	@Override
	public void validate() {
		// Прасить документ
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factory.newSAXParser();
			ArrayList<File> modelFiles = findModelFiles(new File(AppContext.getMainModelPath()), null);
			for (File modelFile : modelFiles) {
				parser.parse(modelFile, new DataModelHandler());			
			}
			File userModelFile = new File(AppContext.getUserModelPath());
			if (userModelFile.exists())
				parser.parse(AppContext.getUserModelPath(), new DataModelHandler());
		} catch (Exception se) {
			ServerLogger.error("model.xml validation failed", se);
			addError(se.getMessage(), 0);
			return;
		}
		// Проверка extends (есть ли такие айтемы)
		ArrayList<String[]> parentChildPairs = new ArrayList<String[]>();
		boolean criticalError = false;
		for (Item item : items.values()) {
			for (String parentName : item.predecessors) {
				if (!items.containsKey(parentName)) {
					addError("Item '" + item.name + "' tries to extend non existent item '" + parentName + "'", item.lineNumber);
					criticalError = true;
				}
				parentChildPairs.add(new String[] {parentName, item.name});
			}
		}
		if (criticalError)
			return;
		// Загрузка матрицы наследования
		TypeHierarchyRegistry hierarchy = ItemTypeRegistry.createHierarchy(parentChildPairs, true);
		// Проверка key (есть ли параметры, перечисленные в key)
		for (Item item : items.values()) {
			// Проверка key (есть ли параметры, перечисленные в key)
			if (!StringUtils.isBlank(item.key)) {
				String[] paramNames = StringUtils.split(item.key, ItemType.COMMON_DELIMITER);
				for (String paramName : paramNames) {
					boolean hasParam = false;
					for (String predName : hierarchy.getItemPredecessorsExt(item.name)) {
						Item pred = items.get(predName);
						for (Parameter param : pred.parameters) {
							if (param.name.equals(paramName)) {
								hasParam = true;
								break;
							}
						}
						if (hasParam) break;
					}
					if (!hasParam)
						addError("Item '" + item.name + "' have no '" + paramName + "' parameter, that is used in item's key", item.lineNumber);
				}
			}
		}
		// Проверка сабайтемов (есть ли такие айтемы)
		for (Subitem subitem : subitems) {
			Item item = items.get(subitem.name);
			if (item == null) {
				criticalError = true;
				addError("'" + subitem.parentName + "' item tries to subitem nonexisting item '" + subitem.name + "'", subitem.lineNumber);
			}
		}
		if (criticalError)
			return;
		// Проверка key (есть ли key у айтемов, которые являются множественными сабайтемами)
		for (Subitem subitem : subitems) {
			if (!subitem.isSingle) {
				Item item = items.get(subitem.name);
				if (item.isVirtual)
					continue;
				boolean hasKey = false;
				for (String predName : hierarchy.getItemPredecessorsExt(subitem.name)) {
					Item pred = items.get(predName);
					if (!StringUtils.isBlank(pred.key)) {
						hasKey = true;
						break;
					}
				}
				if (!hasKey)
					addError("Multiple subitem '" + subitem.name + "' has no key. Multiple subitem items must have key parameters",
							subitem.lineNumber);
			}
		}
		// Проверка, достижим ли айтем из корня
		HashSet<String> accessibleItems = new HashSet<String>();
		HashSet<String> checkedItems = new HashSet<String>();
		for (Root root : roots) {
			checkSubitems(root, accessibleItems, checkedItems, hierarchy);
		}
		if (!accessibleItems.containsAll(items.keySet())) {
			HashSet<String> allItems = new HashSet<String>(items.keySet());
			allItems.removeAll(accessibleItems);
			for (String inaccessibleItem : allItems) {
				Item item = items.get(inaccessibleItem);
				// Проверить всех предшественников по иерархии (если доступен предшественник, то доступен и наследник)
				boolean inaccessible = true;
				Set<String> inaccPreds = hierarchy.getItemPredecessors(inaccessibleItem);
				for (String pred : inaccPreds) {
					if (!allItems.contains(pred)) {
						inaccessible = false;
						break;
					}
				}
				if (inaccessible && !item.isVirtual)
					addError("Item '" + item.name + "' is not used in data structure. It must be either removed or linked as subitem",
							item.lineNumber);
			}
		}
	}

	private void checkSubitems(SubitemContainer item, HashSet<String> accessibleItems, HashSet<String> checkedItems, TypeHierarchyRegistry hierarchy) {
		if (item instanceof Item) {
			if (checkedItems.contains(((Item)item).name))
				return;
			checkedItems.add(((Item)item).name);
			Set<String> branch = hierarchy.getItemPredecessors(((Item)item).name);
			branch.addAll(hierarchy.getItemExtenders(((Item)item).name));
			accessibleItems.addAll(branch);
			for (String itemName : branch) {
				checkSubitems(items.get(itemName), accessibleItems, checkedItems, hierarchy);
			}
		}
		for (Subitem subitem : item.subitems) {
			Set<String> succNames = hierarchy.getItemExtenders(subitem.name);
			accessibleItems.addAll(succNames);
			for (String succName : succNames) {
				checkSubitems(items.get(succName), accessibleItems, checkedItems, hierarchy);
			}
		}
	}
	
	/**
	 * Находит все файлы pages.xml
	 * @param startFile
	 * @param files
	 * @return
	 */
	private static ArrayList<File> findModelFiles(File startFile, ArrayList<File> files) {
		if (files == null) {
			files = new ArrayList<File>();
			if (startFile.isFile())
				files.add(startFile);
			else
				return findModelFiles(startFile, files);
		} else {
			if (startFile.isDirectory()) {
				File[] filesList = startFile.listFiles();
				for (File file : filesList) {
					if (file.isFile())
						files.add(file);
					else if (file.isDirectory())
						findModelFiles(file, files);
				}
			}
		}
		return files;
	}
}
