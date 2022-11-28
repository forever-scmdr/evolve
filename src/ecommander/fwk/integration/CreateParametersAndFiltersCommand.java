package ecommander.fwk.integration;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.Pair;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.Strings;
import ecommander.model.*;
import ecommander.model.datatypes.DataType;
import ecommander.model.filter.CriteriaDef;
import ecommander.model.filter.FilterDefinition;
import ecommander.model.filter.InputDef;
import ecommander.pages.Command;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.commandunits.SaveNewItemTypeDBUnit;
import ecommander.persistence.common.SynchronousTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.*;

/**
 * Created by E on 17/5/2018.
 */
public class CreateParametersAndFiltersCommand extends IntegrateBase implements CatalogConst {

	public static final String ELEMENT_CLASSES_PROCESS_TIMER_NAME = "classes_element";
	public static final String DB_CLASSES_TIMER_NAME = "classes_DB";
	public static final String ELEMENT_PRODUCT_PROCESS_TIMER_NAME = "product_element";
	public static final String DB_PRODUCT_TIMER_NAME = "product_DB";
	public static final String MODEL_RELOAD_TIMER_NAME = "model_reload";

	private static final String DIGITS = "0123456789.,";

	private SynchronousTransaction transaction = new SynchronousTransaction(User.getDefaultUser());

	/**
	 * Типы и названия параметров
	 * Тип параметра может быть одним из трех
	 * integer
	 * double
	 * string
	 * @author E
	 *
	 */
	protected static class Params {
		private final String className;
		private final String classCaption;
		protected LinkedHashMap<String, DataType.Type> paramTypes = new LinkedHashMap<>();
		protected LinkedHashMap<String, Pair<String, Boolean>> paramCaptions = new LinkedHashMap<>();
		protected HashMap<String, String> paramUnits = new HashMap<>();
		protected HashSet<String> notInFilter = new HashSet<>();
		protected static final NumberFormat eng_format = NumberFormat.getInstance(new Locale("en"));
		protected static final NumberFormat ru_format = NumberFormat.getInstance(new Locale("ru"));

		protected Params(String caption, String className) {
			this.classCaption = caption;
			this.className = className;
		}

		protected void addParameter(String name, String value, boolean isMultiple) {
			String paramName = Strings.createXmlElementName(name);
			if (!paramTypes.containsKey(paramName)) {
				paramTypes.put(paramName, DataType.Type.INTEGER);
				paramCaptions.put(paramName, new Pair<>(name, isMultiple));
			} else if (isMultiple) {
				Pair<String, Boolean> cap = paramCaptions.get(paramName);
				if (cap != null && !cap.getRight()) {
					paramCaptions.put(paramName, new Pair<>(cap.getLeft(), true));
				}
			}
			DataType.Type currentType = paramTypes.get(paramName);
			Pair<DataType.Type, String> test = testValueHasUnit(value);
			if (currentType.equals(DataType.Type.INTEGER) && test.getLeft() != DataType.Type.INTEGER) {
				paramTypes.put(paramName, test.getLeft());
			} else if (currentType.equals(DataType.Type.DOUBLE) && test.getLeft() == DataType.Type.STRING) {
				paramTypes.put(paramName, DataType.Type.STRING);
			}
			if (StringUtils.isNotBlank(test.getRight()) && test.getLeft() != DataType.Type.STRING) {
				String newUnit = test.getRight();
				String oldUnit = paramUnits.get(paramName);
				if (oldUnit == null) {
					paramUnits.put(paramName, test.getRight());
				}
				// Если разные размерности (единицы измерения) в одном параметре - хранить как строку
				else if (StringUtils.isNotBlank(oldUnit) && !StringUtils.equalsIgnoreCase(newUnit, oldUnit)) {
					paramTypes.put(paramName, DataType.Type.STRING);
				}
			}
			if (paramTypes.get(paramName) == DataType.Type.STRING) {
				paramUnits.remove(paramName);
			}
		}

		protected void addNotInFilter(String name) {
			String paramName = Strings.createXmlElementName(name);
			notInFilter.add(paramName);
		}

		private static boolean testDouble(String value) {
			ParsePosition pp = new ParsePosition(0);
			ru_format.parse(value, pp);
			if (pp.getIndex() != value.length()) {
				pp = new ParsePosition(0);
				eng_format.parse(value, pp);
				if (pp.getIndex() != value.length())
					return false;
			}
			return true;
		}

		protected static Pair<DataType.Type, String> testValueHasUnit(String value) {
			try {
				Integer.parseInt(value);
				if (value.matches("0\\d+")) {
					return new Pair<>(DataType.Type.STRING, null);
				}
				return new Pair<>(DataType.Type.INTEGER, null);
			} catch (NumberFormatException nfe1) {
				if (testDouble(value)) {
					return new Pair<>(DataType.Type.DOUBLE, null);
				} else {
					//fix bug with % material
					if(value.matches("\\d+([\\.,]\\d+)?%\\D+")){
						return new Pair<>(DataType.Type.STRING, null);
					}
					if (value.matches("^-?[0-9]+[\\.,]?[0-9]*\\s*\\D+$")) {
						int unitStart = 0;
						for (; unitStart < value.length() && StringUtils.contains(DIGITS, value.charAt(unitStart)); unitStart++) { /* */ }
						String numStr = value.substring(0, unitStart).trim();
						String unit = value.substring(unitStart).trim();
						try {
							Integer.parseInt(numStr);
							return new Pair<>(DataType.Type.INTEGER, unit);
						} catch (NumberFormatException nfe2) {
							if (testDouble(numStr)) {
								return new Pair<>(DataType.Type.DOUBLE, unit);
							} else {
								return new Pair<>(DataType.Type.STRING, null);
							}
						}
					} else {
						return new Pair<>(DataType.Type.STRING, null);
					}
				}
			}
		}
	}


	public CreateParametersAndFiltersCommand(Command outer) {
		super(outer);
	}

	public CreateParametersAndFiltersCommand() {
	}

	@Override
	protected boolean makePreparations() throws Exception {
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		List<Item> sections = new ItemQuery(SECTION_ITEM).loadItems();
		doCreate(sections);
	}

	protected void doCreate(List<Item> sections) throws Exception {
		info.setOperation("Создание классов и фильтров");
		info.setToProcess(sections.size());
		info.setProcessed(0);
		ArrayList<String> assocNames = new ArrayList<>();
		assocNames.add(ItemTypeRegistry.getPrimaryAssoc().getName());
		List<Object> extraAssocNames = getVarValues("assoc");
		for (Object extraAssocName : extraAssocNames) {
			Assoc extraAssoc = ItemTypeRegistry.getAssoc((String) extraAssocName);
			if (extraAssoc != null)
				assocNames.add(extraAssoc.getName());
		}
		for (Item section : sections) {
			info.getTimer().start(ELEMENT_CLASSES_PROCESS_TIMER_NAME);
			info.getTimer().start(DB_CLASSES_TIMER_NAME);
			List<Item> products = new ItemQuery(PRODUCT_ITEM).setParentId(section.getId(), false, assocNames.toArray(new String[0])).loadItems();
			info.getTimer().stop(DB_CLASSES_TIMER_NAME);
			if (products.size() > 0) {

				// Загрузить и добавить все строковые товары
				/*
				if (ItemTypeRegistry.getItemType(LINE_PRODUCT_ITEM) != null)
					products.addAll(new ItemQuery(LINE_PRODUCT_ITEM).setParentId(section.getId(), true).loadItems());
				*/

				// Анализ параметров продуктов
				String className = createClassName(section);
				Params params = new Params(section.getStringValue(NAME_PARAM), className);
				info.getTimer().start(DB_CLASSES_TIMER_NAME);
				List<Item> paramsXmlItems = new ItemQuery(PARAMS_XML_ITEM).setParentId(section.getId(), true, assocNames.toArray(new String[0])).loadItems();
				info.getTimer().stop(DB_CLASSES_TIMER_NAME);
				for (Item paramsXmlItem : paramsXmlItems) {
					if (StringUtils.isNotBlank(paramsXmlItem.getStringValue(XML_PARAM))) {
						String xml = "<params>" + paramsXmlItem.getStringValue(XML_PARAM) + "</params>";
						Document paramsTree = Jsoup.parse(xml, "localhost", Parser.xmlParser());
						Elements paramEls = paramsTree.getElementsByTag(PARAMETER);
						for (Element paramEl : paramEls) {
							Elements nameElements = paramEl.getElementsByTag(NAME);
							if (!nameElements.isEmpty()) {
								String caption = StringUtils.trim(nameElements.first().ownText());
								if (StringUtils.isNotBlank(caption)) {
									caption = caption.replaceAll("\\s+", " ");
									Elements values = paramEl.getElementsByTag(VALUE);
									for (Element value : values) {
										params.addParameter(caption, StringUtils.trim(value.ownText()), values.size() > 1);
									}
								}
							}
						}
					}
				}

				// Создание фильтра
				String classCaption = section.getStringValue(NAME_PARAM);
				// Создать фильтр и установить его в айтем
				FilterDefinition filter = FilterDefinition.create("");
				filter.setRoot(className);
				for (String paramName : params.paramTypes.keySet()) {
					if (params.notInFilter.contains(paramName))
						continue;
					String caption = params.paramCaptions.get(paramName).getLeft();
					String unit = params.paramUnits.get(paramName);
					InputDef input = new InputDef("droplist", caption, unit, "");
					filter.addPart(input);
					input.addPart(new CriteriaDef("=", paramName, params.paramTypes.get(paramName), ""));
				}
				section.setValue(PARAMS_FILTER_PARAM, filter.generateXML());
				info.getTimer().start(DB_CLASSES_TIMER_NAME);
				transaction.executeCommandUnit(SaveItemDBUnit.get(section).noTriggerExtra().noFulltextIndex());
				transactionExecute();
				//executeAndCommitCommandUnits(SaveItemDBUnit.get(section).noTriggerExtra().noFulltextIndex());
				info.getTimer().stop(DB_CLASSES_TIMER_NAME);

				// Создать класс для продуктов из этого раздела
				ItemType newClass = new ItemType(className, 0, classCaption, "", "",
						PARAMS_ITEM, null, false, true, false, false);
				for (String paramName : params.paramTypes.keySet()) {
					if(StringUtils.isBlank(paramName))
						continue;
					String type = params.paramTypes.get(paramName).toString();
					String caption = params.paramCaptions.get(paramName).getLeft();
					boolean isMultiple = params.paramCaptions.get(paramName).getRight();
					String unit = params.paramTypes.get(paramName) != DataType.Type.STRING ? params.paramUnits.get(paramName) : null;
					newClass.putParameter(new ParameterDescription(paramName, 0, type, isMultiple, 0,
							"", caption, unit, "", false, false, null, null));
				}
				info.getTimer().start(DB_CLASSES_TIMER_NAME);
				transaction.executeCommandUnit(new SaveNewItemTypeDBUnit(newClass));
				transactionExecute();
				//executeAndCommitCommandUnits(new SaveNewItemTypeDBUnit(newClass));
				info.getTimer().stop(DB_CLASSES_TIMER_NAME);
				pushLog("class \""+newClass.getCaption()+"\" добавлен");
			} else {
				section.clearValue(PARAMS_FILTER_PARAM);
				info.getTimer().start(DB_CLASSES_TIMER_NAME);
				transaction.executeCommandUnit(SaveItemDBUnit.get(section).noTriggerExtra().noFulltextIndex());
				transactionExecute();
				//executeAndCommitCommandUnits(SaveItemDBUnit.get(section).noTriggerExtra().noFulltextIndex());
				info.getTimer().stop(DB_CLASSES_TIMER_NAME);
			}
			info.increaseProcessed();
			info.getTimer().stop(ELEMENT_CLASSES_PROCESS_TIMER_NAME);
		}
		transaction.commit();

		try {
			pushLog("Попытка обновить информационную модель");
			DataModelBuilder.newForceUpdate().tryLockAndReloadModel();
			pushLog("Информационную модель обновлена");
			if (SystemUtils.IS_OS_LINUX){
				pushLog("Попытка смены прав доступа к модели");
				Path ecXml = Paths.get(AppContext.getContextPath(),"WEB-INF", "ec_xml");
				Runtime.getRuntime().exec(new String[]{"chmod", "775", "-R", ecXml.toAbsolutePath().toString()});
				pushLog("Прав доступа к модели успешно изменены");
			}
		} catch (Exception e) {
			ServerLogger.error("Unable to reload new model", e);
			info.addError("Невозможно создать новую модель данных", e.getLocalizedMessage());
			info.setOperation("Фатальная ошибка");
			return;
		}

		info.setOperation("Удаление старых параметров");
		info.setToProcess(sections.size());
		info.setProcessed(0);
		for (Item section : sections) {
			List<Item> oldParams = new ItemQuery(PARAMS_ITEM).setParentId(section.getId(), true, assocNames.toArray(new String[0])).loadItems();
			for (Item oldParam : oldParams) {
				transaction.executeCommandUnit(ItemStatusDBUnit.delete(oldParam).noFulltextIndex().noTriggerExtra());
				transactionExecute();
			}
			info.increaseProcessed();
		}

		info.setOperation("Заполнение параметров товаров");
		info.setToProcess(sections.size());
		info.setProcessed(0);
		for (Item section : sections) {
			String className = createClassName(section);
			ItemType paramDesc = ItemTypeRegistry.getItemType(StringUtils.lowerCase(className));
			info.getTimer().start(DB_PRODUCT_TIMER_NAME);
			List<Item> products = new ItemQuery(PRODUCT_ITEM).setParentId(section.getId(), false).loadItems();
			info.getTimer().stop(DB_PRODUCT_TIMER_NAME);
			if (products.size() > 0) {

				// Загрузить и добавить все строковые товары
				/*
				if (ItemTypeRegistry.getItemType(LINE_PRODUCT_ITEM) != null)
					products.addAll(new ItemQuery(LINE_PRODUCT_ITEM).setParentId(section.getId(), true).loadItems());
				 */

				for (Item product : products) {
					Item paramsXml = new ItemQuery(PARAMS_XML_ITEM).setParentId(product.getId(), false).loadFirstItem();
					if (paramsXml != null) {
						String xml = "<params>" + paramsXml.getStringValue(XML_PARAM) + "</params>";
						Document paramsTree = Jsoup.parse(xml, "localhost", Parser.xmlParser());
						Elements paramEls = paramsTree.getElementsByTag("parameter");
						Item params = Item.newChildItem(paramDesc, product);
						for (Element paramEl : paramEls) {
							Elements nameElements = paramEl.getElementsByTag(NAME);
							if (!nameElements.isEmpty()) {
								String name = StringUtils.normalizeSpace(nameElements.first().ownText());
								name = Strings.createXmlElementName(name);
								if (paramDesc.hasParameter(name)) {
									Elements values = paramEl.getElementsByTag("value");
									for (Element valueEl : values) {
										String value = StringUtils.normalizeSpace(valueEl.ownText());
										if(paramDesc.getParameter(name).getDataType().getType() != DataType.Type.STRING) {
											Pair<DataType.Type, String> valuePair = Params.testValueHasUnit(value);
											if (StringUtils.isNotBlank(valuePair.getRight())) {
												value = value.split("\\s*[^0-9\\.,]")[0];
											}
										}
										params.setValueUI(name, value);
									}
								} else {
									info.pushLog("No parameter {} in section {}", name, section.getStringValue("name"));
								}
							}
						}
						info.getTimer().start(DB_PRODUCT_TIMER_NAME);
						transaction.executeCommandUnit(SaveItemDBUnit.get(params).noFulltextIndex().noTriggerExtra());
						transactionExecute();
						//executeAndCommitCommandUnits(SaveItemDBUnit.get(params).noFulltextIndex().noTriggerExtra());
						info.getTimer().stop(DB_PRODUCT_TIMER_NAME);
					}
				}
			}
			info.increaseProcessed();
		}
		transaction.commit();
	}



	/**
	 * Создать уникальное название для класса
	 * @param section
	 * @return
	 */
	public static String createClassName(Item section) {
		if (section.isValueNotEmpty("code")) {
			return Strings.createXmlElementName(StringUtils.lowerCase("p" + section.getStringValue("code")));
		}
		if (section.isValueNotEmpty("name")) {
			if (section.isValueEmpty("category_id")) {
				// в этом случае использовать ID айтема, т.к. могут быть разделы с одинаковым названием
				return Strings.createXmlElementName(StringUtils.lowerCase("pid_" + section.getId()));
			}
			return Strings.createXmlElementName(StringUtils.lowerCase("p" + section.getStringValue("name"))
					+ "_" + section.getStringValue("category_id"));
		}
		return Strings.createXmlElementName(StringUtils.lowerCase("p" + section.getKey()));
	}


	/**
	 * Для вызова из внешней команды
	 * @param info
	 * @throws Exception
	 */
	public void doCreateParametersAndFilters(Info info) throws Exception {
		this.info = info;
		integrate();
	}

	@Override
	protected void terminate() throws Exception {

	}

	public static void main(String[] args) {
		System.out.println("0.5 - 5 Нм".matches("^-?[0-9]+[\\.,]?[0-9]*\\s+[^-\\s]+$"));
		System.out.println("45,5cm".split("\\s*[^0-9\\.,]")[0]);
	}

	private void transactionExecute() throws Exception {
		if (transaction.getUncommitedCount() >= 400)
			transaction.commit();
	}
}
