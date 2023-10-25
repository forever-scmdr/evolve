package ecommander.fwk.integration;

import ecommander.controllers.AppContext;
import ecommander.fwk.*;
import ecommander.model.*;
import ecommander.model.datatypes.DataType;
import ecommander.model.datatypes.DoubleDataType;
import ecommander.model.datatypes.TupleDataType;
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
import java.util.stream.Stream;

/**
 * Команда для сознания фильтра, подразумевающая, что вся информация хранится в одном айтеме товара
 * (параметры xml и один строковый параметр с множественными значениями)
 * Параметры команды (переменные страницы) - возможны два режима работы
 * 	- section - список id или названий или урлов разделов для создания фильтров
 * 	- page_sec - страничный ID одного текущего раздела на странице раздела. Для создания фильтров на лету (лейзи)
 */
public class CreateSectionFilterCommand extends Command implements CatalogConst {

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
		protected HashMap<String, String> paramUnits = new HashMap<>();
		// <Real Value(with unit, commas, etc.) => Normalized Value (just numbers) >
		// То, что выводится в select и его реальное значение. Реальное значение может быть с единицей измерения и т.п.,
		// но его надо хранить т.к. именно такое значение хранится в БД и с ним надо производить сравнение
		protected HashMap<String, HashMap<String, String>> uniqueParamValues = new LinkedHashMap<>();
		protected HashSet<String> notInFilter = new HashSet<>();
		protected static final NumberFormat eng_format = NumberFormat.getInstance(new Locale("en"));
		protected static final NumberFormat ru_format = NumberFormat.getInstance(new Locale("ru"));

		protected Params(String caption, String className) {
			this.classCaption = caption;
			this.className = className;
		}

		protected void addParameter(String name, String value) {
			value = StringUtils.normalizeSpace(value);
			String paramName = StringUtils.normalizeSpace(name);
			if (!paramTypes.containsKey(paramName)) {
				paramTypes.put(paramName, DataType.Type.INTEGER);
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
			if (!uniqueParamValues.containsKey(paramName)) {
				uniqueParamValues.put(paramName, new HashMap<>());
			}
			uniqueParamValues.get(paramName).put(TupleDataType.outputTuple(TupleDataType.newTuple(name, value), null), test.getThird());
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
			return new Pair<>(Boolean.TRUE, parsed);
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


	private static class StringOrNumberComparator implements Comparator<String> {

		private DataType.Type type;
		private HashMap<String, String> normalizedValues;

		public StringOrNumberComparator(DataType.Type type, HashMap<String, String> normalizedValues) {
			this.type = type;
			this.normalizedValues = normalizedValues;
		}

		@Override
		public int compare(String o1, String o2) {
			if (type == DataType.Type.STRING)
				return o1.compareTo(o2);
			String normalizedO1 = normalizedValues.get(o1);
			String normalizedO2 = normalizedValues.get(o2);
			Double do1 = DoubleDataType.parse(normalizedO1);
			Double do2 = DoubleDataType.parse(normalizedO2);
			do1 = do1 == null ? Double.MAX_VALUE : do1;
			do2 = do2 == null ? Double.MAX_VALUE : do2;
			return do1.compareTo(do2);
		}
	}


	public CreateSectionFilterCommand(Command outer) {
		super(outer);
	}

	public CreateSectionFilterCommand() {
	}

	@Override
	public ResultPE execute() throws Exception {
		// Список разделов по ID, названию или урлу
		List<Object> secIds = getVarValues("section");
		List<Item> sections = new ArrayList<>();
		for (Object secId : secIds) {
			String secIdStr = (String) secId;
			if (StringUtils.isNotBlank(secIdStr)) {
				long longId = NumberUtils.toLong(secIdStr, -1);
				Item section = null;
				if (longId > 0)
					section = ItemQuery.loadById(longId);
				if (section == null)
					section = ItemQuery.loadSingleItemByUniqueKey(secIdStr);
				if (section == null)
					section = ItemQuery.loadSingleItemByParamValue(SECTION_ITEM, NAME_PARAM, secIdStr);
				if (section != null) {
					sections.add(section);
				}
			}
		}
		// Страничный ID айтема раздела
		String pageSecId = getVarSingleValue("page_sec");
		if (StringUtils.isNotBlank(pageSecId)) {
			Item section = getSingleLoadedItem(pageSecId);
			if (section != null) {
				sections.add(section);
			}
		}
		if (secIds.size() == 0 && StringUtils.isBlank(pageSecId))
			sections = new ItemQuery(SECTION_ITEM).loadItems();
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
			// Пропустить разделы, в которых уже есть фильтр. Чтобы пересоздать фильтр, нужно очистить этот параметр в
			// айтеме раздела (можно в CMS)
			if (section.isValueNotEmpty(XML_FILTER_PARAM))
				continue;
			List<Item> products = new ItemQuery(PRODUCT_ITEM).setParentId(section.getId(), false, assocNames.toArray(new String[0])).setLimit(1).loadItems();
			if (products.size() > 0) {
				Item first = products.get(0);
				// Анализ параметров продуктов
				Params params = new Params(section.getStringValue(NAME_PARAM), first.getTypeName());

				// params_xml айтем не используется, загружаются просто сами товары, которые уже сами содержат параметр params_xml
				ItemQuery productQuery = new ItemQuery(PRODUCT_ITEM).setParentId(section.getId(), false, assocNames.toArray(new String[0]))
						.setIdSequential(0).setLimit(50);
				List<Item> productItems = productQuery.loadItems();
				long lastProductId = 0;
				while (productItems.size() > 0) {
					for (Item product : productItems) {
						lastProductId = product.getId();
						// Копирование XML параметров из вложенного айтема в сам продукт, удаление старого айтема params_xml
						if (StringUtils.isBlank(product.getStringValue(PARAMS_XML_PARAM))) {
							Item paramsXmlItem = new ItemQuery(PARAMS_XML_ITEM).setParentId(product.getId(), false).loadFirstItem();
							if (paramsXmlItem != null && StringUtils.isNotBlank(paramsXmlItem.getStringValue(XML_PARAM))) {
								product.setValue(PARAMS_XML_PARAM, paramsXmlItem.getStringValue(XML_PARAM));
								transaction.executeCommandUnit(SaveItemDBUnit.get(product).noFulltextIndex());
								transaction.executeCommandUnit(ItemStatusDBUnit.delete(paramsXmlItem));
								// удалить также и айтем params
								Item paramsItem = new ItemQuery(PARAMS_ITEM).setParentId(product.getId(), false).loadFirstItem();
								if (paramsItem != null)
									transaction.executeCommandUnit(ItemStatusDBUnit.delete(paramsItem));
								transaction.commit();
							}
						}
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
											params.addParameter(caption, StringUtils.trim(value.ownText()));
										}
									}
								}
							}
						}
					}
					productItems = productQuery.setIdSequential(lastProductId).loadItems();
				}

				// Создать фильтр и установить его в айтем
				XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();
				xml.startElement("filter");
				for (String paramName : params.paramTypes.keySet()) {
					if (params.notInFilter.contains(paramName) || !params.uniqueParamValues.containsKey(paramName))
						continue;
					DataType.Type type = params.paramTypes.get(paramName);
					HashMap<String, String> uniqueVals = params.uniqueParamValues.get(paramName);
					Stream<String> sortedVals = uniqueVals.keySet().stream().sorted(new StringOrNumberComparator(type, uniqueVals));
					String unit = params.paramUnits.get(paramName);
					xml.startElement("param", "type", type);
					xml.addElement("name", paramName);
					if (StringUtils.isNotBlank(unit))
						xml.addElement("unit", unit);
					xml.startElement("values");
					sortedVals.forEach(s -> xml.startElement("value").addElement("val", s).addElement("cap", uniqueVals.get(s)).endElement());
					xml.endElement().endElement(); // </values></param>
				}
				xml.endElement(); // </filter>
				// установка параметра фильтра в айтем section
				section.setValue(XML_FILTER_PARAM, xml.toString());

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


	public static void main(String[] args) {
		System.out.println("0.5 - 5 Нм".matches("^-?[0-9]+[\\.,]?[0-9]*\\s+[^-\\s]+$"));
		System.out.println("45,5cm".split("\\s*[^0-9\\.,]")[0]);
	}

	private void transactionExecute() throws Exception {
		if (transaction.getUncommitedCount() >= 400)
			transaction.commit();
	}
}
