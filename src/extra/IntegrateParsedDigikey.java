package extra;

import ecommander.fwk.*;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.User;
import ecommander.model.UserGroupRegistry;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import ecommander.persistence.common.DelayedTransaction;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import lunacrawler.fwk.ParsedInfoProvider;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.map.CaseInsensitiveMap;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;


public class IntegrateParsedDigikey extends IntegrateBase implements ItemNames {

	private static final String INTEGRATE_DIR = "integrate";
	public static final String SECTION_ELEMENT = "section";
	public static final String PRODUCT_ELEMENT = "product";
	public static final String ID_ATTR = "id";

	private ParsedInfoProvider infoProvider;
	private HashMap<String, Item> sections = new HashMap<>();
	private String ROOT_ID = "/en/products";

	private static Map<String, String> digikeyParameterNames = new CaseInsensitiveMap<>();
	static {
		digikeyParameterNames.put("Manufacturer", product_.VENDOR);
		digikeyParameterNames.put("Manufacturer Product Number", product_.VENDOR_CODE);
		digikeyParameterNames.put("Description", product_.NAME_EXTRA);
		digikeyParameterNames.put("Digi-Key Part Number", product_.CODE);
		digikeyParameterNames.put("Second Category", product_.SECTION_NAME);
	}


	@Override
	protected boolean makePreparations() throws Exception {
		infoProvider = new ParsedInfoProvider(true);
		Item catalog = ItemUtils.ensureSingleRootItem(CATALOG, getInitiator(), User.NO_GROUP_ID, User.ANONYMOUS_ID);
		sections.put(ROOT_ID, catalog);
		return infoProvider.isValid();
	}


	@Override
	protected void integrate() throws Exception {
		info.setToProcess(0);
		info.setProcessed(0);
		info.limitLog(300);
		try {
			XmlDataSource bigTree = infoProvider.getBigTree();
			DelayedTransaction transaction = new DelayedTransaction(getInitiator());
			HashSet<String> elementNames = new HashSet<>();
			CollectionUtils.addAll(elementNames, PRODUCT_ELEMENT, SECTION_ELEMENT);
			while (true) {
				String code = null;
				String name = null;
				try {
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
						info.addError("Документ для товара '" + code + "' содержит ошибки", code);
						continue;
					}
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
							transaction.addCommandUnit(SaveItemDBUnit.get(section).noFulltextIndex());
							transaction.execute();
						}
						sections.put(code, section);
					} else if (StringUtils.equalsIgnoreCase(node.getTagName(), PRODUCT_ELEMENT)) {
						Item product = ItemQuery.loadSingleItemByParamValue(PRODUCT, product_.URL, code);
						if (product != null) {
							transaction.addCommandUnit(ItemStatusDBUnit.delete(product).noFulltextIndex());
							transaction.execute();
						}
						// Создание айтема продукта
						Item parentSection = sections.get(parentId);
						if (parentSection == null) {
							info.addError("Раздел для товара '" + code + "' не найден", code);
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
										product.setValue(product_.DESC_VALS, StringUtils.normalizeSpace(value.text()));
									}
								}
							} else {
								break;
							}
						}
						// Заполнение технических характеристик
						product.setValue(product_.PARAMS_XML, doc.getRoot().select("attributes > value").html());
						transaction.addCommandUnit(SaveItemDBUnit.new_(product, parentSection).noFulltextIndex());
						// Создание вложенных айтемов (доп. страницы описания товара)
						// Даташиты
						Element docsEl = doc.getRoot().select("docs > value").first();
						if (docsEl != null) {
							Elements params = docsEl.getElementsByTag("param");
							for (Element param : params) {
								if (StringUtils.equalsIgnoreCase(JsoupUtils.getTagFirstValue(param, "name"), "Datasheets")) {
									Item ds = Item.newChildItem(ItemTypeRegistry.getItemType(ItemNames.PRODUCT_EXTRA_XML), product);
									ds.setValue(product_extra_xml_.NAME, "Datasheets");
									ds.setValue(product_extra_xml_.XML, param.outerHtml());
									transaction.addCommandUnit(SaveItemDBUnit.new_(ds, product));
									break;
								}
							}
							// Другие документы (в т.ч. и даташиты)
							Item docs = Item.newChildItem(ItemTypeRegistry.getItemType(ItemNames.PRODUCT_EXTRA_XML), product);
							docs.setValue(product_extra_xml_.NAME, "Documents and Media");
							docs.setValue(product_extra_xml_.XML, docsEl.outerHtml());
							transaction.addCommandUnit(SaveItemDBUnit.new_(docs, product));
						}
						// Environmental
						Element envEl = doc.getRoot().select("environmental > value").first();
						if (envEl != null) {
							Item env = Item.newChildItem(ItemTypeRegistry.getItemType(ItemNames.PRODUCT_EXTRA_XML), product);
							env.setValue(product_extra_xml_.NAME, "Environmental and Export Classifications");
							env.setValue(product_extra_xml_.XML, envEl.outerHtml());
							transaction.addCommandUnit(SaveItemDBUnit.new_(env, product));
						}
						// Additional
						Element extraEl = doc.getRoot().select("additional > value").first();
						if (extraEl != null) {
							Item extra = Item.newChildItem(ItemTypeRegistry.getItemType(ItemNames.PRODUCT_EXTRA_XML), product);
							extra.setValue(product_extra_xml_.NAME, "Additional Resources");
							extra.setValue(product_extra_xml_.XML, extraEl.outerHtml());
							transaction.addCommandUnit(SaveItemDBUnit.new_(extra, product));
						}
						info.increaseProcessed();
					}
				} catch (UserNotAllowedException e) {
					ServerLogger.error("User not allowed", e);
					info.addError("User not allowed", "catalog");
					return;
				} catch (Exception e) {
					ServerLogger.error("Product save error", e);
					info.addError("Product save error, ID = " + code + ", name = " + name, "catalog");
				}
				if (transaction.getCommandCount() >= 100) {
					transaction.execute();
				}
			}
			transaction.execute();
		} catch (Exception e) {
			ServerLogger.error("Product save error", e);
			info.addError("MEGA ERROR", "MEGA ERROR");
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
