package ecommander.model;

import ecommander.fwk.ModelValidator;
import ecommander.fwk.ServerLogger;
import ecommander.pages.ValidationResults;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.*;

/**
 * Проверка файла model.xml
 * TODO <enhance/> Сделать проверку доступности сабайетма с учетом ассоциации (сейчас ассоциация не учитывается)
 * TODO <enhance/> Сделать проверку доступности базовых айтемов для вычистяемых парамтеров с учетом ассоциации
 */
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
		public final boolean isVirtual;
	}

	private static class BaseItemParam extends Element {
		private final ComputedDescription.Type type;
		private final String item;
		private final String assoc;
		private final String parameter;
		private final boolean isTransitive;
		public BaseItemParam(int lineNumber, ComputedDescription.Type type, String item, String assoc, String parameter, boolean isTransitive) {
			super(lineNumber);
			this.type = type;
			this.item = item;
			this.assoc = assoc;
			this.parameter = parameter;
			this.isTransitive = isTransitive;
		}
	}
	
	private static class Parameter extends Element {
		private Parameter(int lineNumber, String name, String type, boolean isMultiple, String caption, ComputedDescription.Func function) {
			super(lineNumber);
			this.name = name;
			this.type = type;
			this.isMultiple = isMultiple;
			this.caption = caption;
			this.function = function;
		}
		public final String name;
		public final ComputedDescription.Func function;
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
		
		private Stack<Element> stack = new Stack<>();
		
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
				byte id = NumberUtils.toByte(attributes.getValue(AG_ID), (byte) 0);
				if (StringUtils.isBlank(name)) {
					addError("Name not set. Assoc 'name' attribute must not be empty.", locator.getLineNumber());
					name = "error" + (errorCounter++);
				}
				if (StringUtils.isBlank(caption))
					addError("Caption not set. Assoc 'caption' attribute must not be empty.", locator.getLineNumber());
				if (assocs.containsKey(name)) {
					addError("Duplicate assoc name '" + name + "'. All assocs must have unique names", locator.getLineNumber());
					criticalError = true;
				}
				if (id != (byte)0) {
					if (assocIds.contains(id)) {
						addError("Duplicate assoc ID '" + id + "'. All assocs must have unique IDs", locator.getLineNumber());
					}
					assocIds.add(id);
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
				int id = NumberUtils.toInt(attributes.getValue(AG_ID), 0);
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
				if (id != 0) {
					if (itemIds.contains(id)) {
						addError("Duplicate item ID '" + id + "'. All items must have unique IDs", locator.getLineNumber());
					}
					itemIds.add(id);
				}
				Item item = new Item(locator.getLineNumber(), name, caption, key, isKeyUnique, defaultPage, isVirtual);
				String strExtends = attributes.getValue(SUPER);
				if (strExtends != null) {
					String[] parents = StringUtils.split(strExtends, ItemType.COMMON_DELIMITER);
					for (String parent : parents) {
						if (!StringUtils.isBlank(parent) && !parent.equals(ItemType.ITEM_SELF_PARAMS))
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
				String itemName = attributes.getValue(ITEM);
				if (StringUtils.isBlank(itemName)) {
					addError("Name not set. Child 'name' attribute must not be empty.", locator.getLineNumber());
					return;
				}
				if (((ChildContainer)parent).children.contains(itemName)) {
					addError("Duplicate child name: " + itemName, locator.getLineNumber());
					return;
				}
				String assoc = attributes.getValue(ASSOC);
				boolean isSingle = StringUtils.equalsIgnoreCase(attributes.getValue(SINGLE), TRUE_VALUE);
				boolean isVirtual = StringUtils.equalsIgnoreCase(attributes.getValue(VIRTUAL), TRUE_VALUE);
				int id = NumberUtils.toInt(attributes.getValue(AG_ID), 0);
				if (id != 0) {
					if (paramIds.contains(id)) {
						addError("Duplicate parameter ID '" + id + "'. All parameters must have unique IDs", locator.getLineNumber());
					}
					paramIds.add(id);
				}
				String parentName = "";
				if (parent instanceof Item) {
					parentName = ((Item)parent).name;
				} else if (parent instanceof Root) {
					parentName = "root";
				}
				Child child = new Child(locator.getLineNumber(), itemName, parentName, assoc, isSingle, isVirtual);
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
				String functionStr = attributes.getValue(FUNCTION);
				ComputedDescription.Func func = null;
				if (StringUtils.isNotBlank(functionStr) && (func = ComputedDescription.Func.get(functionStr)) == null)
					addError("There is no '" + functionStr + "' predefined function", locator.getLineNumber());
				Parameter parameter = new Parameter(locator.getLineNumber(), name, type, isMultiple, caption, func);
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
				if (StringUtils.isBlank(assoc))
					assoc = AssocRegistry.PRIMARY_NAME;
				boolean isTransitive = StringUtils.equalsIgnoreCase(attributes.getValue(TRANSITIVE), TRUE_VALUE);
				ComputedDescription.Type type = ComputedDescription.Type.get(qName);
				BaseItemParam base = new BaseItemParam(locator.getLineNumber(), type, item, assoc, parameter, isTransitive);
				// Сохранение
				stack.push(base);
				((Parameter)parent).baseItemParams.add(base);
			}
			else if (ROOT.equalsIgnoreCase(qName)) {
				if (stack.size() > 0) {
					addError("Root element is in wrong place. 'root' most not be a child of any other node", locator.getLineNumber());
					return;
				}
				if (root != null) {
					addError("There must be only one 'root' element", locator.getLineNumber());
					return;
				}
				root = new Root(locator.getLineNumber());
				// Сохранение
				stack.push(root);
			}
			else if (USER_GROUP.equalsIgnoreCase(qName)) {
				if (stack.size() > 0) {
					addError("User-group element is in wrong place. 'user-group' most not be a child of any other node", locator.getLineNumber());
					return;
				}
				// Сохранение
				stack.push(new Root(locator.getLineNumber()));
			}
			else if (VALUE.equalsIgnoreCase(qName)) {
				Element parent = stack.peek();
				if (!(parent instanceof Parameter)) {
					addError("Value element is in wrong place. 'value' must be a child of 'parameter'", locator.getLineNumber());
					return;
				}
			}
			else if (!MODEL.equalsIgnoreCase(qName) && ItemType.Event.get(qName) == null) {
				addError("Invalid '" + qName + "' element", locator.getLineNumber());
			}
		}
		
		@Override
		public void endElement(String uri, String localName, String qName) throws SAXException {
			if (criticalError)
				return;
			if (ROOT.equalsIgnoreCase(qName) ||
					ASSOC.equalsIgnoreCase(qName) ||
					ITEM.equalsIgnoreCase(qName) ||
					CHILD.equalsIgnoreCase(qName) ||
					PARAMETER.equalsIgnoreCase(qName) ||
					BASE_CHILD.equalsIgnoreCase(qName) ||
					USER_GROUP.equalsIgnoreCase(qName))
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
	private HashSet<Integer> itemIds = new HashSet<>();
	private HashSet<Integer> paramIds = new HashSet<>();
	private HashSet<Byte> assocIds = new HashSet<>();
	private Root root;
	
	public DataModelCreationValidator(ArrayList<String> modelFiles) {
		items = new HashMap<String, Item>();
		children = new ArrayList<Child>();
		assocs = new HashMap<>();
		ecommander.model.Assoc pa = AssocRegistry.PRIMARY;
		assocs.put(AssocRegistry.PRIMARY_NAME,
				new Assoc(0, pa.getName(), pa.getCaption(), pa.getDescription(), pa.isTransitive()));
		this.modelFiles = modelFiles;
	}


	@Override
	public void validate() {
		// Создать парсер
		SAXParserFactory factory = SAXParserFactory.newInstance();
		try {
			SAXParser parser = factory.newSAXParser();
			for (String modelFile : modelFiles) {
				if(StringUtils.isNotBlank(modelFile)) {
					parser.parse(new InputSource(new StringReader(modelFile)), new DataModelHandler());
				}
			}
		} catch (Exception se) {
			System.out.println(se.getMessage());
			ServerLogger.error("model.xml validation failed", se);
			addError(se.getMessage(), 0);
			return;
		}
		// Проверка extends (есть ли такие айтемы)
		ArrayList<String[]> parentChildPairs = new ArrayList<>();
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
		HierarchyRegistry hierarchy = ItemTypeRegistry.createHierarchy(parentChildPairs, new ArrayList<String[]>(), true);
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
		// Проверка сабайтемов (есть ли такие айтемы и ассоциации)
		for (Child child : children) {
			if (items.get(child.name) == null) {
				criticalError = true;
				addError("'" + child.parentName + "' item uses undefined item '" + child.name + "' as a child", child.lineNumber);
			}
			if (StringUtils.isNotBlank(child.assoc) && assocs.get(child.assoc) == null) {
				addError("'" + child.parentName + "' item tries to use undefiled assoc '" + child.assoc + "'", child.lineNumber);
			}
		}
		if (criticalError)
			return;
		// Проверка key (есть ли key у айтемов, которые являются множественными сабайтемами)
		for (Child child : children) {
			if (!child.isSingle) {
				Item item = items.get(child.name);
				if (child.isVirtual || item.isVirtual)
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
					addError("Multiple child '" + child.name + "' has no key. Multiple child items must have key parameters",
							child.lineNumber);
			}
		}

		// Проверка базовых параметров
		for (Item item : items.values()) {
			for (Parameter parameter : item.parameters) {
				// Если задана функция, то параметр вычисляемый
				if (parameter.function != null) {
					if (parameter.baseItemParams.size() == 0) {
						addError("Computed parameter '" + parameter.name + "' has no base base-child or base-parent",
								parameter.lineNumber);
						continue;
					}
					for (BaseItemParam base : parameter.baseItemParams) {
						Item baseItem = items.get(base.item);
						Assoc assoc = assocs.get(base.assoc);
						if (assoc == null) {
							addError(base.type + " references undefiled assoc '" + base.assoc + "'", base.lineNumber);
							continue;
						}
						if (baseItem == null) {
							addError(base.type + " references undefiled item '" + base.item + "'", base.lineNumber);
							continue;
						}
						// Проверка параметра
						boolean hasParam = false;
						for (String predName : hierarchy.getItemPredecessorsExt(base.item)) {
							Item pred = items.get(predName);
							for (Parameter param : pred.parameters) {
								if (param.name.equals(base.parameter)) {
									hasParam = true;
									break;
								}
							}
							if (hasParam) break;
						}
						if (!hasParam) {
							addError("Item '" + base.item + "' have no '" + base.parameter + "' parameter referenced by " + base.type, item.lineNumber);
							continue;
						}
						// Проверка доступность айтема через ассоциацию
						if (base.type == ComputedDescription.Type.child) {
							if (StringUtils.isNotBlank(base.assoc) &&
									!StringUtils.equalsIgnoreCase(AssocRegistry.PRIMARY_NAME, base.assoc)) {
								boolean hasAssoc = false;
								for (Child child : item.children) {
									if (StringUtils.equalsIgnoreCase(child.assoc, base.assoc)) {
										hasAssoc = true;
										break;
									}
								}
								if (!hasAssoc) {
									addError("Item '" + item.name + "' have no '" + base.assoc + "' assoc referenced by " + base.type, item.lineNumber);
									continue;
								}
							} else {
								boolean hasChild = false;
								for (Child child : item.children) {
									if (StringUtils.equalsIgnoreCase(child.name, base.item)) {
										hasChild = true;
										break;
									}
								}
								if (!hasChild) {
									addError("Item '" + item.name + "' have no default-assoc child '"
											+ base.item + "' referenced by " + base.type, item.lineNumber);
									continue;
								}
							}
						} else if (base.type == ComputedDescription.Type.parent) {
							boolean isChildHasParam = false;
							for (Item container : items.values()) {
								for (Child child : container.children) {
									if (StringUtils.equalsIgnoreCase(child.name, base.item) && StringUtils.equalsIgnoreCase(child.assoc, base.assoc)) {
										for (Parameter contParam : container.parameters) {
											if (StringUtils.equalsIgnoreCase(contParam.name, base.parameter)) {
												isChildHasParam = true;
												break;
											}
										}
									}
									if (isChildHasParam) break;
								}
								if (isChildHasParam) break;
							}
							if (!isChildHasParam) {
								addError("There is no suitable parent '" + base.item + "' with parameter '"
										+ base.parameter + "' associated by '" + base.assoc + "' referenced by "
										+ base.type, item.lineNumber);
								continue;
							}
						}
					}
				}
			}
		}
		// Проверка, достижим ли айтем из корня
		HashSet<String> accessibleItems = new HashSet<>();
		HashSet<String> checkedItems = new HashSet<>();
		if (root == null) {
			addError("There is no root defined", 0);
			return;
		}
		checkSubitems(root, accessibleItems, checkedItems, hierarchy);
		if (!accessibleItems.containsAll(items.keySet())) {
			HashSet<String> allItems = new HashSet<>(items.keySet());
			allItems.removeAll(accessibleItems);
			for (String inaccessibleItem : allItems) {
				Item item = items.get(inaccessibleItem);
				// Проверить всех предшественников по иерархии (если доступен предшественник, то доступен и наследник)
				boolean inaccessible = true;
				Set<String> inaccPreds = hierarchy.getPredecessors(inaccessibleItem);
				for (String pred : inaccPreds) {
					if (!allItems.contains(pred)) {
						inaccessible = false;
						break;
					}
				}
				if (inaccessible && !item.isVirtual)
					addError("Item '" + item.name + "' is not used in data structure. It must be either removed or linked as a child",
							item.lineNumber);
			}
		}
	}

	private void checkSubitems(ChildContainer item, HashSet<String> accessibleItems, HashSet<String> checkedItems, HierarchyRegistry hierarchy) {
		if (item instanceof Item) {
			if (checkedItems.contains(((Item)item).name))
				return;
			checkedItems.add(((Item)item).name);
			Set<String> branch = hierarchy.getPredecessors(((Item)item).name);
			branch.addAll(hierarchy.getExtenders(((Item)item).name));
			accessibleItems.addAll(branch);
			for (String itemName : branch) {
				checkSubitems(items.get(itemName), accessibleItems, checkedItems, hierarchy);
			}
		}
		for (Child child : item.children) {
			Set<String> succNames = hierarchy.getExtenders(child.name);
			accessibleItems.addAll(succNames);
			for (String succName : succNames) {
				checkSubitems(items.get(succName), accessibleItems, checkedItems, hierarchy);
			}
		}
	}

	public static void main(String[] args) throws IOException {
		File file = new File("F:/PROJECTS/evolve/web/WEB-INF/ec_xml/model_test.xml");
		ServerLogger.init("F:/log4j.properties", "F:/log.txt");
		ArrayList<String> files = new ArrayList<>();
		files.add(FileUtils.readFileToString(file, "UTF-8"));
		DataModelCreationValidator validator = new DataModelCreationValidator(files);
		validator.validate();
		ValidationResults res = validator.getResults();
		for (ValidationResults.LineMessage lineMessage : res.getLineErrors()) {
			System.out.println(lineMessage);
		}
		for (ValidationResults.StructureMessage structureMessage : res.getStructureErrors()) {
			System.out.println(structureMessage);
		}
		if (res.isSuccessful())
			System.out.println("SUCCESS");
	}
}
