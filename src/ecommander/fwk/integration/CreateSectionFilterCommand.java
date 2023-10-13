package ecommander.fwk.integration;

import ecommander.controllers.AppContext;
import ecommander.fwk.Pair;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.Strings;
import ecommander.model.*;
import ecommander.model.datatypes.DataType;
import ecommander.model.filter.CriteriaDef;
import ecommander.model.filter.FilterDefinition;
import ecommander.model.filter.InputDef;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.commandunits.SaveNewItemTypeDBUnit;
import ecommander.persistence.common.SynchronousTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import kotlin.Triple;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.math.NumberUtils;
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
 * Команда для сознания фильтра, подразумевающая что вся информация хранится в одном айтеме товара (параметры xml и
 */
public class CreateSectionFilterCommand extends Command implements CatalogConst {

	public static final String ELEMENT_CLASSES_PROCESS_TIMER_NAME = "classes_element";
	public static final String ELEMENT_PRODUCT_PROCESS_TIMER_NAME = "product_element";

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
		// <Normalized Value (just numbers, Real Value(with unit, commas, etc.)>
		// То, что выводится в select и его реальное значение. Реальное значение может быть с единицей измерения и т.п.,
		// но его надо хранить т.к. именно такое значение хранится в БД и с ним надо производить сравнение
		protected HashMap<String, Pair<String, String>> uniqueParamValues = new LinkedHashMap<>();
		protected HashSet<String> notInFilter = new HashSet<>();
		protected static final NumberFormat eng_format = NumberFormat.getInstance(new Locale("en"));
		protected static final NumberFormat ru_format = NumberFormat.getInstance(new Locale("ru"));

		protected Params(String caption, String className) {
			this.classCaption = caption;
			this.className = className;
		}

		protected void addParameter(String name, String value, boolean isMultiple) {
			value = StringUtils.normalizeSpace(value);
			String paramName = StringUtils.normalizeSpace(name);
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
			Triple<DataType.Type, String, String> test = testValueHasUnit(value);
			if (currentType.equals(DataType.Type.INTEGER) && test.getFirst() != DataType.Type.INTEGER) {
				paramTypes.put(paramName, test.getFirst());
			} else if (currentType.equals(DataType.Type.DOUBLE) && test.getFirst() == DataType.Type.STRING) {
				paramTypes.put(paramName, DataType.Type.STRING);
			}
			if (StringUtils.isNotBlank(test.getSecond()) && test.getFirst() != DataType.Type.STRING) {
				String newUnit = test.getSecond();
				String oldUnit = paramUnits.get(paramName);
				if (oldUnit == null) {
					paramUnits.put(paramName, test.getSecond());
				}
				// Если разные размерности (единицы измерения) в одном параметре - хранить как строку
				else if (StringUtils.isNotBlank(oldUnit) && !StringUtils.equalsIgnoreCase(newUnit, oldUnit)) {
					paramTypes.put(paramName, DataType.Type.STRING);
				}
			}
			if (paramTypes.get(paramName) == DataType.Type.STRING) {
				paramUnits.remove(paramName);
			}
			uniqueParamValues.put(paramName, new Pair<>(test.getThird(), value));
		}

		protected void addNotInFilter(String name) {
			String paramName = Strings.createXmlElementName(name);
			notInFilter.add(paramName);
		}

		private static Pair<Boolean, Number> testDouble(String value) {
			ParsePosition pp = new ParsePosition(0);
			Number parsed = ru_format.parse(value, pp);
			if (pp.getIndex() != value.length()) {
				pp = new ParsePosition(0);
				parsed = eng_format.parse(value, pp);
				if (pp.getIndex() != value.length())
					return new Pair<>(Boolean.FALSE, null);
			}
			return new Pair<>(Boolean.FALSE, parsed);
		}

		/**
		 * Проверить (определить) значение параметра с учетом единицы измерения
		 * @param value
		 * @return Тип, единица измерения, заголовок
		 */
		protected static Triple<DataType.Type, String, String> testValueHasUnit(String value) {
			try {
				Number parsed = Integer.parseInt(value);
				if (value.matches("0\\d+")) {
					return new Triple<>(DataType.Type.STRING, null, value);
				}
				return new Triple<>(DataType.Type.INTEGER, null, parsed.toString());
			} catch (NumberFormatException nfe1) {
				Pair<Boolean, Number> doubleTest = testDouble(value);
				if (doubleTest.getLeft()) {
					return new Triple<>(DataType.Type.DOUBLE, null, doubleTest.getRight().toString());
				} else {
					//fix bug with % material
					if (value.matches("\\d+([\\.,]\\d+)?%\\D+")) {
						return new Triple<>(DataType.Type.STRING, null, value);
					}
					if (value.matches("^-?[0-9]+[\\.,]?[0-9]*\\s*\\D+$")) {
						int unitStart = 0;
						for (; unitStart < value.length() && StringUtils.contains(DIGITS, value.charAt(unitStart)); unitStart++) { /* */ }
						String numStr = value.substring(0, unitStart).trim();
						String unit = value.substring(unitStart).trim();
						try {
							Number parsed = Integer.parseInt(numStr);
							return new Triple<>(DataType.Type.INTEGER, unit, parsed.toString());
						} catch (NumberFormatException nfe2) {
							doubleTest = testDouble(numStr);
							if (doubleTest.getLeft()) {
								return new Triple<>(DataType.Type.DOUBLE, unit, doubleTest.getRight().toString());
							} else {
								return new Triple<>(DataType.Type.STRING, null, value);
							}
						}
					} else {
						return new Triple<>(DataType.Type.STRING, null, value);
					}
				}
			}
		}
	}


	public CreateSectionFilterCommand(Command outer) {
		super(outer);
	}

	public CreateSectionFilterCommand() {
	}

	@Override
	public ResultPE execute() throws Exception {
		String justOneSection = getVarSingleValue("section");
		List<Item> sections = null;
		if (StringUtils.isNotBlank(justOneSection)) {
			long longId = NumberUtils.toLong(justOneSection, -1);
			Item section = null;
			if (longId > 0)
				section = ItemQuery.loadById(longId);
			if (section == null)
				section = ItemQuery.loadSingleItemByUniqueKey(justOneSection);
			if (section == null)
				section = ItemQuery.loadSingleItemByParamValue(SECTION_ITEM, NAME_PARAM, justOneSection);
			sections = new ArrayList<>();
			if (section != null) {
				sections.add(section);
			}
		} else {
			sections = new ItemQuery(SECTION_ITEM).loadItems();
		}
		if (sections.size() > 0)
			doCreate(sections);
		return null;
	}

	protected void doCreate(List<Item> sections) throws Exception {
		ArrayList<String> assocNames = new ArrayList<>();
		assocNames.add(ItemTypeRegistry.getPrimaryAssoc().getName());
		List<Object> extraAssocNames = getVarValues("assoc");
		for (Object extraAssocName : extraAssocNames) {
			Assoc extraAssoc = ItemTypeRegistry.getAssoc((String) extraAssocName);
			if (extraAssoc != null)
				assocNames.add(extraAssoc.getName());
		}
		for (Item section : sections) {
			List<Item> products = new ItemQuery(PRODUCT_ITEM).setParentId(section.getId(), false, assocNames.toArray(new String[0])).setLimit(1).loadItems();
			if (products.size() > 0) {
				Item first = products.get(0);
				// Анализ параметров продуктов
				Params params = new Params(section.getStringValue(NAME_PARAM), first.getTypeName());
				// params_xml айтем не используется, загружаются просто сами товары, которые уже сами содержат параметр params_xml
				ItemQuery paramsXmlQuery = new ItemQuery(PRODUCT_ITEM).setParentId(section.getId(), true, assocNames.toArray(new String[0]))
						.setIdSequential(0).setLimit(50);
				List<Item> paramsXmlItems = paramsXmlQuery.loadItems();
				long lastParamsXmlId = 0;
				while (paramsXmlItems.size() > 0) {
					for (Item product : paramsXmlItems) {
						lastParamsXmlId = product.getId();
						if (StringUtils.isNotBlank(product.getStringValue(PARAMS_XML_PARAM))) {
							String xml = "<params>" + product.getStringValue(PARAMS_XML_PARAM) + "</params>";
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
					paramsXmlItems = paramsXmlQuery.setIdSequential(lastParamsXmlId).loadItems();
				}

				// Создать фильтр и установить его в айтем
				FilterDefinition filter = FilterDefinition.create("");
				filter.setRoot(first.getTypeName());
				for (String paramName : params.paramTypes.keySet()) {
					if (params.notInFilter.contains(paramName))
						continue;
					String caption = params.paramCaptions.get(paramName).getLeft();
					String unit = params.paramUnits.get(paramName);
					InputDef input = new InputDef("droplist", caption, unit, "");
					filter.addPart(input);
					input.addPart(new CriteriaDef("=", PARAM_VALS_PARAM, params.paramTypes.get(paramName), ""));
				}
				section.setValue(PARAMS_FILTER_PARAM, filter.generateXML());
				transaction.executeCommandUnit(SaveItemDBUnit.get(section).noTriggerExtra().noFulltextIndex());
				transactionExecute();
				//executeAndCommitCommandUnits(SaveItemDBUnit.get(section).noTriggerExtra().noFulltextIndex());
			} else {
				section.clearValue(PARAMS_FILTER_PARAM);
				transaction.executeCommandUnit(SaveItemDBUnit.get(section).noTriggerExtra().noFulltextIndex());
				transactionExecute();
				//executeAndCommitCommandUnits(SaveItemDBUnit.get(section).noTriggerExtra().noFulltextIndex());
			}
		}
		transaction.commit();
	}


	private Boolean hasCode = null;
	private Boolean hasName = null;
	private Boolean hasCategoryId = null;

	/**
	 * Создать уникальное название для класса
	 * @param section
	 * @return
	 */
	private String createClassName(Item section) {
		if (hasCode == null) {
			hasCode = section.getItemType().hasParameter("code");
			hasName = section.getItemType().hasParameter("name");
			hasCategoryId = section.getItemType().hasParameter("category_id");
		}
		if (hasCode && section.isValueNotEmpty("code")) {
			return Strings.createXmlElementName(StringUtils.lowerCase("p" + section.getStringValue("code")));
		}
		if (hasName && section.isValueNotEmpty("name")) {
			if (!hasCategoryId || section.isValueEmpty("category_id")) {
				// в этом случае использовать ID айтема, т.к. могут быть разделы с одинаковым названием
				return Strings.createXmlElementName(StringUtils.lowerCase("pid_" + section.getId()));
			}
			return Strings.createXmlElementName(StringUtils.lowerCase("p" + section.getStringValue("name")))
					+ "_" + section.getStringValue("category_id");
		}
		return Strings.createXmlElementName(StringUtils.lowerCase("p" + section.getKey()));
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
