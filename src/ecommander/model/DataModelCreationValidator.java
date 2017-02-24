package ecommander.model;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Stack;

import org.apache.commons.lang3.StringUtils;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import ecommander.fwk.ServerLogger;
import ecommander.fwk.Strings;
import ecommander.fwk.ModelValidator;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

public class DataModelCreationValidator extends ModelValidator implements DataModelXmlElementNames {

	private static class Element {
		protected int lineNumber;
		protected Element(int lineNumber) {
			this.lineNumber = lineNumber;
		}
	}
	
	private static class ChildContainer extends Element {
		protected ArrayList<Child> children;
		protected ChildContainer(int lineNumber) {
			super(lineNumber);
			this.children = new ArrayList<Child>();
		}
	}

	private static class Assoc extends ChildContainer {
		public Assoc(int lineNumber, String name, String caption, String description, boolean isTransitive) {
			super(lineNumber);
			this.name = name;
			this.caption = caption;
			this.description = description;
			this.isTransitive = isTransitive;
		}
		public final String name;
		public final String caption;
		public final String description;
		public final boolean isTransitive;
	}

	private static class Item extends ChildContainer {
		
		private Item(int lineNumber, String name, String caption, String key, boolean isKeyUnique, String defaultPage, boolean isVirtual) {
			super(lineNumber);
			this.name = name;
			this.caption = caption;
			this.key = key;
			this.isVirtual = isVirtual;
			this.baseItems = new ArrayList<String>();
			this.parameters = new ArrayList<Parameter>();
			this.isKeyUnique = isKeyUnique;
			this.defaultPage = defaultPage;
		}
		public final String name;
		@SuppressWarnings("unused")
		public final String caption;
		public final String key;
		public final boolean isKeyUnique;
		public final String defaultPage;
		public final boolean isVirtual;
		public final ArrayList<String> baseItems;
		public final ArrayList<Parameter> parameters;
	}
	
	private static class Child extends Element {
		private Child(int lineNumber, String name, String parentName, String assoc, boolean isSingle, boolean isVirtual) {
			super(lineNumber);
			this.name = name;
			this.isSingle = isSingle;
			this.isVirtual = isVirtual;
			this.parentName = parentName;
			this.assoc = assoc;
		}
		public final String name;
		public final String parentName;
		public final String assoc;
		public final boolean isSingle;
		@SuppressWarnings("unused")
		public final boolean isVirtual;
	}

	private static class BaseItemParam extends Element {
		private final String type;
		private final String item;
		private final String assoc;
		private final String parameter;
		private final boolean isTransitive;
		public BaseItemParam(int lineNumber, String type, String item, String assoc, String parameter, boolean isTransitive) {
			super(lineNumber);
			this.type = type;
			this.item = item;
			this.assoc = assoc;
			this.parameter = parameter;
			this.isTransitive = isTransitive;
		}
	}
	
	private static class Parameter extends Element {
		private Parameter(int lineNumber, String name, String type, boolean isMultiple, String caption, String function) {
			super(lineNumber);
			this.name = name;
			this.type = type;
			this.isMultiple = isMultiple;
			this.caption = caption;
			this.function = function;
		}
		public final String name;
		public final String function;
		@SuppressWarnings("unused")
		public final String type;
		@SuppressWarnings("unused")
		public final boolean isMultiple;
		@SuppressWarnings("unused")
		public final String caption;
		public final ArrayList<BaseItemParam> baseItemParams = new ArrayList<>(3);
	}

	private static class Root extends ChildContainer {
		private Root(int lineNumber) {
			super(lineNumber);
		}
	}

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
			if (ASSOC.equalsIgnoreCase(qName)) {
				String name = attributes.getValue(NAME);
				String caption = attributes.getValue(CAPTION);
				String description = attributes.getValue(DESCRIPTION);
				boolean isTransitive = StringUtils.endsWithIgnoreCase(attributes.getValue(TRANSITIVE), TRUE_VALUE);
				if (StringUtils.isBlank(name)) {
					addError("Name not set. Assoc 'name' attribute must not be empty.", locator.getLineNumber());
					name = "error" + (errorCounter++);
				}
				if (StringUtils.isBlank(caption))
					addError("Caption not set. Assoc 'caption' attribute must not be empty.", locator.getLineNumber());
				if (items.containsKey(name)) {
					addError("Duplicate item name '" + name + "'. All items must have unique names", locator.getLineNumber());
					criticalError = true;
				}
				Assoc assoc = new Assoc(locator.getLineNumber(), name, caption, description, isTransitive);
				String strExtends = attributes.getValue(SUPER);
				// Сохранение
				stack.push(assoc);
				assocs.put(name, assoc);
			}
			else if (ITEM.equalsIgnoreCase(qName)) {
				String name = attributes.getValue(NAME);
				String caption = attributes.getValue(CAPTION);
				String key = attributes.getValue(KEY);
				String defaultPage = attributes.getValue(DEFAULT_PAGE);
				boolean isVirtual = StringUtils.endsWithIgnoreCase(attributes.getValue(VIRTUAL), TRUE_VALUE);
				boolean isKeyUnique = StringUtils.endsWithIgnoreCase(attributes.getValue(KEY_UNIQUE), TRUE_VALUE);
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
				Item item = new Item(locator.getLineNumber(), name, caption, key, isKeyUnique, defaultPage, isVirtual);
				String strExtends = attributes.getValue(SUPER);
				if (strExtends != null) {
					String[] parents = StringUtils.split(strExtends, ItemType.COMMON_DELIMITER);
					for (String parent : parents) {
						if (!StringUtils.isBlank(parent) && !parent.equals(ItemType.ITEM_SELF))
							item.baseItems.add(parent);
					}
				}
				// Сохранение
				stack.push(item);
				items.put(name, item);
			}
			else if (CHILD.equalsIgnoreCase(qName)) {
				Element parent = stack.peek();
				if (!(parent instanceof ChildContainer)) {
					addError("Subitem element is in wrong place. 'subitem' must be a child of 'item' or 'root'", locator.getLineNumber());
					return;
				}
				String childName = attributes.getValue(NAME);
				if (StringUtils.isBlank(childName)) {
					addError("Name not set. Child 'name' attribute must not be empty.", locator.getLineNumber());
					return;
				}
				if (((ChildContainer)parent).children.contains(childName)) {
					addError("Duplicate child name: " + childName, locator.getLineNumber());
					return;
				}
				String assoc = attributes.getValue(ASSOC);
				boolean isSingle = StringUtils.equalsIgnoreCase(attributes.getValue(SINGLE), TRUE_VALUE);
				boolean isVirtual = StringUtils.equalsIgnoreCase(attributes.getValue(VIRTUAL), TRUE_VALUE);
				String parentName = "";
				if (parent instanceof Item) {
					parentName = ((Item)parent).name;
				} else if (parent instanceof Root) {
					parentName = "root";
				}
				Child child = new Child(locator.getLineNumber(), childName, parentName, assoc, isSingle, isVirtual);
				// Сохранение
				stack.push(child);
				((ChildContainer)parent).children.add(child);
				children.add(child);
			}
			else if (PARAMETER.equalsIgnoreCase(qName)) {
				Element parent = stack.peek();
				if (!(parent instanceof Item)) {
					addError("Parameter element is in wrong place. 'parameter' must be a child of 'item'", locator.getLineNumber());
					return;
				}
				String name = attributes.getValue(NAME);
				if (StringUtils.isBlank(name)) {
					addError("Name not set. Parameter 'name' attribute must not be empty.", locator.getLineNumber());
					return;
				}
				if (((Item)parent).parameters.contains(name)) {
					addError("Duplicate parameter name: " + name, locator.getLineNumber());
					return;
				}
				String caption = attributes.getValue(CAPTION);
				if (StringUtils.isBlank(caption) && !((Item)parent).isVirtual)
					addError("Caption not set. Parameter 'caption' attribute must not be empty.", locator.getLineNumber());
				boolean isMultiple = StringUtils.equalsIgnoreCase(attributes.getValue(MULTIPLE), TRUE_VALUE);
				String type = attributes.getValue(TYPE);
				if (!DataTypeRegistry.isTypeNameValid(type))
					addError("Parameter type is incorrect.", locator.getLineNumber());
				String function = attributes.getValue(FUNCTION);
				Parameter parameter = new Parameter(locator.getLineNumber(), name, type, isMultiple, caption, function);
				// Сохранение
				stack.push(parameter);
				((Item)parent).parameters.add(parameter);
			}
			else if (BASE_CHILD.equalsIgnoreCase(qName) || BASE_PARENT.equalsIgnoreCase(qName)) {
				Element parent = stack.peek();
				if (!(parent instanceof Parameter)) {
					addError(qName + " element is in wrong place. It must be a child of 'parameter'", locator.getLineNumber());
					return;
				}
				String item = attributes.getValue(ITEM);
				if (StringUtils.isBlank(item)) {
					addError("Base item not set. Base-parent/child 'item' attribute must not be empty.", locator.getLineNumber());
					return;
				}
				String parameter = attributes.getValue(PARAMETER);
				if (StringUtils.isBlank(parameter)) {
					addError("Base item parameter not set. Base-parent/child 'parameter' attribute must not be empty.", locator.getLineNumber());
					return;
				}
				String assoc = attributes.getValue(ASSOC);
				boolean isTransitive = StringUtils.equalsIgnoreCase(attributes.getValue(TRANSITIVE), TRUE_VALUE);
				BaseItemParam base = new BaseItemParam(locator.getLineNumber(), qName, item, assoc, parameter, isTransitive);
				// Сохранение
				stack.push(base);
				((Parameter)parent).baseItemParams.add(base);
			}
			else if (ROOT.equalsIgnoreCase(qName)) {
				if (stack.size() > 0) {
					addError("Root element is in wrong place. 'root' most not be a child of any other node", locator.getLineNumber());
					return;
				}
				Root root = new Root(locator.getLineNumber());
				// Сохранение
				stack.push(root);
				if (root != null) {
					addError("There must be only one 'root' element", locator.getLineNumber());
					return;
				}
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

	private ArrayList<String> modelFiles;
	private HashMap<String, Assoc> assocs;
	private HashMap<String, Item> items;
	private ArrayList<Child> children;
	private Root roots;
	
	public DataModelCreationValidator(ArrayList<String> modelFiles) {
		items = new HashMap<String, Item>();
		children = new ArrayList<Child>();
		this.modelFiles = modelFiles;
	}


	@Override
	public void validate() {
		// Создать парсер
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factory.newSAXParser();
			for (String modelFile : modelFiles) {
				parser.parse(modelFile, new DataModelHandler());			
			}
		} catch (Exception se) {
			ServerLogger.error("model.xml validation failed", se);
			addError(se.getMessage(), 0);
			return;
		}
		// Проверка extends (есть ли такие айтемы)
		ArrayList<String[]> parentChildPairs = new ArrayList<String[]>();
		boolean criticalError = false;
		for (Item item : items.values()) {
			for (String parentName : item.baseItems) {
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
		for (Child child : children) {
			Item item = items.get(child.name);
			if (item == null) {
				criticalError = true;
				addError("'" + child.parentName + "' item tries to subitem nonexisting item '" + child.name + "'", child.lineNumber);
			}
		}
		if (criticalError)
			return;
		// Проверка key (есть ли key у айтемов, которые являются множественными сабайтемами)
		for (Child child : children) {
			if (!child.isSingle) {
				Item item = items.get(child.name);
				if (item.isVirtual)
					continue;
				boolean hasKey = false;
				for (String predName : hierarchy.getItemPredecessorsExt(child.name)) {
					Item pred = items.get(predName);
					if (!StringUtils.isBlank(pred.key)) {
						hasKey = true;
						break;
					}
				}
				if (!hasKey)
					addError("Multiple subitem '" + child.name + "' has no key. Multiple subitem items must have key parameters",
							child.lineNumber);
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

	private void checkSubitems(ChildContainer item, HashSet<String> accessibleItems, HashSet<String> checkedItems, TypeHierarchyRegistry hierarchy) {
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
		for (Child child : item.children) {
			Set<String> succNames = hierarchy.getItemExtenders(child.name);
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
