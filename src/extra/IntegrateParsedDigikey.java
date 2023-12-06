package extra;

import ecommander.fwk.*;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.User;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.common.SynchronousTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import lunacrawler.fwk.ParsedInfoProvider;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;


public class IntegrateParsedDigikey extends IntegrateBase implements ItemNames {

	private static final String INTEGRATE_DIR = "integrate";
	public static final String SECTION_ELEMENT = "section";
	public static final String PRODUCT_ELEMENT = "product";
	public static final String ID_ATTR = "id";

	private ParsedInfoProvider infoProvider;
	private HashMap<String, Item> sections = new HashMap<>();
	private String ROOT_ID = "/en/products";

	private int currentIndexInSection = 0;

	private static Map<String, String> digikeyParameterNames = new CaseInsensitiveMap<>();
	static {
		digikeyParameterNames.put("Manufacturer", product_.VENDOR);
		digikeyParameterNames.put("Manufacturer Product Number", product_.NAME);
		digikeyParameterNames.put("Description", product_.NAME_EXTRA);
		digikeyParameterNames.put("Digi-Key Part Number", product_.OFFER_ID);
		digikeyParameterNames.put("Second Category", product_.TYPE);
	}


	@Override
	protected boolean makePreparations() throws Exception {
		infoProvider = new ParsedInfoProvider(true, info.getTimer());
		Item catalog = ItemQuery.loadSingleItemByName(CATALOG);
		if (catalog == null) {
			catalog = ItemUtils.ensureSingleRootItem(CATALOG, getInitiator(), User.NO_GROUP_ID, User.ANONYMOUS_ID);
		}
		sections.put(ROOT_ID, catalog);
		return infoProvider.isValid();
	}


	@Override
	protected void integrate() throws Exception {
		info.setToProcess(0);
		info.setProcessed(0);
		info.limitLog(20);
		try {
			XmlDataSource bigTree = infoProvider.getBigTree();
			SynchronousTransaction transaction = new SynchronousTransaction(getInitiator(), info.getTimer());
			HashSet<String> elementNames = new HashSet<>();
			CollectionUtils.addAll(elementNames, PRODUCT_ELEMENT, SECTION_ELEMENT);
			while (true) {
				info.getTimer().start("main cycle");
				String code = null;
				String name = null;
				try {
					info.getTimer().start("files outer");
					XmlDataSource.Node node = bigTree.findNextNode(elementNames);
					if (node == null)
						break;
					code = node.attr(ID_ATTR);
					if (StringUtils.isBlank(code))
						continue;
					ParsedInfoProvider.InfoAccessor doc;
					try {
						doc = infoProvider.getAccessor(code);
					} catch (Exception e) {
						ServerLogger.error("Error parsing product xml file", e);
						info.pushError("Документ для товара '" + code + "' содержит ошибки", code);
						continue;
					}
					info.getTimer().stop("files outer");
					String parentId = doc.getFirst("h_parent").attr("parent");
					if (StringUtils.equalsIgnoreCase(node.getTagName(), SECTION_ELEMENT)) {
						name = doc.getNodeText("name");
						Item section = ItemQuery.loadSingleItemByParamValue(SECTION, section_.NAME, name);
						if (section == null) {
							Item parent = sections.get(parentId);
							if (parent == null)
								parent = sections.get(ROOT_ID);
							section = Item.newChildItem(ItemTypeRegistry.getItemType(SECTION), parent);
							section.setValue(section_.NAME, name);
							transaction.executeCommandUnit(SaveItemDBUnit.get(section).noFulltextIndex().noTriggerExtra());
							transaction.commit();
						}
						sections.put(code, section);
						currentIndexInSection = 0;
					} else if (StringUtils.equalsIgnoreCase(node.getTagName(), PRODUCT_ELEMENT)) {
						Item product = ItemQuery.loadSingleItemByParamValue(PRODUCT, product_.URL, code);
						if (product != null) {
							transaction.executeCommandUnit(ItemStatusDBUnit.delete(product).noFulltextIndex().noTriggerExtra());
							transaction.commit();
						}
						// Создание айтема продукта
						Item parentSection = sections.get(parentId);
						if (parentSection == null) {
							info.pushError("Раздел для товара '" + code + "' не найден", code);
							info.increaseProcessed();
							continue;
						}
						product = Item.newChildItem(ItemTypeRegistry.getItemType(PRODUCT), parentSection);
						// Заполнение основных параметров
						for (int i = 1; i < 30; i++) {
							Element param = doc.getFirst("parameter_" + i);
							if (param != null) {
								String paramName = JsoupUtils.getTagFirstValue(param,"name");
								if (digikeyParameterNames.containsKey(paramName)) {
									for (Element value : param.getElementsByTag("value")) {
										product.setValue(digikeyParameterNames.get(paramName), StringUtils.normalizeSpace(value.text()));
									}
								} else {
									for (Element value : param.getElementsByTag("value")) {
										Pair<String, String> pair = new Pair<>(paramName, StringUtils.normalizeSpace(value.text()));
										product.setValue(product_.DESC_VALS, pair);
									}
								}
							} else {
								break;
							}
						}
						product.setValue(product_.CODE, product.getStringValue(product_.NAME));
						product.setValue(product_.URL, code);
						// Заполнение технических характеристик
						currentIndexInSection++;
						product.setValue(product_.PARAMS_XML, doc.getRoot().select("attributes > value").html());
						// Даташиты
						Element docsEl = doc.getRoot().select("docs > value").first();
						// Другие документы (в т.ч. и даташиты)
						if (docsEl != null) {
							product.setValue(product_.DOCUMENTS_XML, docsEl.outerHtml());
						}
						// Environmental
						Element envEl = doc.getRoot().select("environmental > value").first();
						if (envEl != null) {
							product.setValue(product_.ENVIRONMENTAL_XML, envEl.outerHtml());
						}
						// Additional
						Element extraEl = doc.getRoot().select("additional > value").first();
						if (extraEl != null) {
							product.setValue(product_.ADDITIONAL_XML, extraEl.outerHtml());
						}
						transaction.executeCommandUnit(SaveItemDBUnit.new_(product, parentSection, currentIndexInSection * 64).noFulltextIndex());
						info.increaseProcessed();
					}
				} catch (UserNotAllowedException e) {
					ServerLogger.error("User not allowed", e);
					info.pushError("User not allowed", "catalog");
					return;
				} catch (Exception e) {
					ServerLogger.error("Product save error", e);
					info.pushError("Product save error, ID = " + code + ", name = " + name, "catalog");
				}
				if (transaction.getUncommitedCount() >= 100) {
					transaction.commit();
				}
				info.getTimer().stop("main cycle");
			}
			transaction.commit();
		} catch (Exception e) {
			ServerLogger.error("Product save error", e);
			info.pushError("MEGA ERROR", "MEGA ERROR");
		} finally {
			infoProvider.getBigTree().finishDocument();
		}
		// save file
		//FileUtils.write(new File(csvFile, "result.csv"), csv, StandardCharsets.UTF_8);
	}

	@Override
	protected void terminate() throws Exception {

	}
}
