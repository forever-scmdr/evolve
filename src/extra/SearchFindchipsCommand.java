package extra;

import ecommander.controllers.AppContext;
import ecommander.fwk.*;
import ecommander.model.Compare;
import ecommander.model.Item;
import ecommander.model.datatypes.DecimalDataType;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.itemquery.fulltext.FulltextQueryCreatorRegistry;
import ecommander.special.portal.outer.ProxyRequestDispatcher;
import ecommander.special.portal.outer.Request;
import extra._generated.ItemNames;
import extra._generated.Product;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.jetbrains.annotations.Nullable;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;


/**
 * Формирует следующую структуру из https://www.findchips.com
 *

 <result>
 	<query>HMC0603JT100M</query>
 	<distributor name="Farnell">
		<product id="708-HMC0603JT100M">
			<code>708-HMC0603JT100M</code>
			<name>HMC0603JT100M</name>
			<vendor>SEI Stackpole Electronics Inc</vendor>
 			<qty>40512</qty>
			<currency_id>USD</currency_id>
			<pricebreak>3</pricebreak>
			<step>500</step>
			<min_qty>500</min_qty>
 			<description>Thick Film Resistors - SMD 100MOhms 0603 0.1W 5% High Value</description>
			<next_delivery>32 Weeks, 1 Days</next_delivery>
 			<container>Reel</container>
			<prices>
				<break qty="1">
					<price_original>0.06</price_original>
					<price_USD>0.06</price_USD>
					<price_BYN>0.17</price_BYN>
					<price>4.63</price>
					<price_RUB>4.63</price_RUB>
					<price_EUR>0.05</price_EUR>
				</break>
				<break qty="1000">
					<price_original>0.04</price_original>
					<price_USD>0.04</price_USD>
					<price_BYN>0.13</price_BYN>
					<price>3.45</price>
					<price_RUB>3.45</price_RUB>
					<price_EUR>0.04</price_EUR>
				</break>
				<break qty="5000">
					<price_original>0.04</price_original>
					<price_USD>0.04</price_USD>
					<price_BYN>0.12</price_BYN>
					<price>3.23</price>
					<price_RUB>3.23</price_RUB>
					<price_EUR>0.04</price_EUR>
 				</break>
			 </prices>
 		</product>
 		<product>
 			...
 		</product>
 			...
 	</distributor>
 </result>



 *
 *
 */
public class SearchFindchipsCommand extends Command implements ItemNames {

	private static final String SERVER_PARAM = "server";
	private static final String LOCAL_PARAM = "local";
	private static final String NEW_PARAM = "new";
	private static final String QUERY_PARAM = "q";
	protected static final String PRICE_PREFIX = "price_";
	private final String CACHE_DIR = "files/search";

	protected Input inp;

	public SearchFindchipsCommand(Command outer) {
		super(outer);
	}

	protected class Input {
		protected List<Object> servers;
		protected String curCode;
		protected String query;
		protected BigDecimal fromFilterDecimal;
		protected BigDecimal toFilterDecimal;
		protected boolean hasShipDateFilter;
		protected boolean hasVendorFilter;
		protected boolean hasDistributorFilter;
		protected boolean hasFromFilter;
		protected boolean hasToFilter;
		protected HashSet<Object> shipDateFilter;
		protected HashSet<Object> vendorFilter;
		protected HashSet<Object> distributorFilter;
		protected HashSet<Object> dstrSet;
		protected BigDecimal globalMaxPrice;
		protected BigDecimal globalMinPrice;

		@Nullable
		private ResultPE init() throws EcommanderException {
			this.servers = getVarValues(SERVER_PARAM);
			this.query = getVarSingleValue(QUERY_PARAM);
			// Запрос может поступать напрямую через переменную q, а может через страничный айтем товара "prod" (его название)
			if (StringUtils.isBlank(query)) {
				Item prod = getSingleLoadedItem("prod");
				if (prod != null)
					query = prod.getStringValue("name");
			}
			if (StringUtils.isBlank(query)) {
				return getResult("illegal_argument").setValue("Неверный формат запроса");
			}

			// Фильтр
			this.curCode = getVarSingleValueDefault("cur", "RUB");
			String fromFilter = getVarSingleValue("from");
			String toFilter = getVarSingleValue("to");
			this.shipDateFilter = new HashSet<>(getVarValues("ship_date"));
			this.vendorFilter = new HashSet<>(getVarValues("vendor"));
			this.distributorFilter = new HashSet<>(getVarValues("distributor"));
			String dstr = getVarSingleValue("dstr");
			String[] dstrArray = StringUtils.split(dstr, ",");
			this.dstrSet = new HashSet<>();
			if (dstrArray != null) {
				for (String s : dstrArray) {
					if (StringUtils.isNotBlank(s))
						this.dstrSet.add(StringUtils.trim(s));
				}
			}
			this.fromFilterDecimal = DecimalDataType.parse(fromFilter, 4);
			fromFilterDecimal = fromFilterDecimal == null ? BigDecimal.ONE.negate() : fromFilterDecimal;
			this.toFilterDecimal = DecimalDataType.parse(toFilter, 4);
			toFilterDecimal = toFilterDecimal == null ? BigDecimal.valueOf(Double.MAX_VALUE) : toFilterDecimal;
			this.hasShipDateFilter = shipDateFilter.size() > 0;
			this.hasVendorFilter = vendorFilter.size() > 0;
			this.hasDistributorFilter = distributorFilter.size() > 0;
			this.hasFromFilter = StringUtils.isNotBlank(fromFilter);
			this.hasToFilter = StringUtils.isNotBlank(toFilter);
			this.globalMaxPrice = BigDecimal.ONE.negate();
			this.globalMinPrice = BigDecimal.valueOf(Double.MAX_VALUE);
			return null;
		}
	}


	@Override
	public ResultPE execute() throws Exception {
		this.inp = new Input();
		ResultPE errorResult = inp.init();
		if (errorResult != null) {
			return errorResult;
		}
		boolean localSearch = StringUtils.equalsAnyIgnoreCase(getVarSingleValue(LOCAL_PARAM), "yes", "true");
		// Надо ли делать новый запрос или взять кеш
		boolean fromCache = !StringUtils.equalsAnyIgnoreCase(getVarSingleValueDefault(NEW_PARAM, "true"), "yes", "true");
		String cacheFileName = Strings.getFileName(inp.query);
		File cacheDir = new File(AppContext.getRealPath(CACHE_DIR));
		File cacheFile = new File(AppContext.getRealPath(CACHE_DIR + '/' + cacheFileName + ".xml"));

		// Определение, надо ли загружать из кеша
		boolean loadFromCache = fromCache && cacheFile.exists();

		// Загрузка результата из кеша и его модификация
		if (loadFromCache) {
			String xml = FileUtils.readFileToString(cacheFile, StandardCharsets.UTF_8);
			if (inp.hasVendorFilter || inp.hasShipDateFilter || inp.hasFromFilter || inp.hasToFilter || inp.hasDistributorFilter) {
				Document doc = JsoupUtils.parseXml(xml);
				Elements distributors = doc.getElementsByTag("distributor");
				if (inp.hasDistributorFilter) {
					for (Element distributor : distributors) {
						String distrName = distributor.attr("name");
						if (!inp.distributorFilter.contains(distrName)) {
							distributor.remove();
						}
					}
				}
				if (inp.hasVendorFilter || inp.hasShipDateFilter || inp.hasFromFilter || inp.hasToFilter) {
					Elements products = doc.getElementsByTag("product");
					for (Element product : products) {
						if (inp.hasShipDateFilter) {
							String shipDate = JsoupUtils.getSelectorFirstValue(product, "next_delivery");
							if (!inp.shipDateFilter.contains(shipDate)) {
								product.remove();
								continue;
							}
						}
						if (inp.hasVendorFilter) {
							String vendor = JsoupUtils.getSelectorFirstValue(product, "vendor");
							if (!inp.vendorFilter.contains(vendor)) {
								product.remove();
								continue;
							}
						}
						if (inp.hasFromFilter) {
							String maxPriceStr = JsoupUtils.getSelectorFirstValue(product, "max_price");
							BigDecimal maxPrice = DecimalDataType.parse(maxPriceStr, 4);
							if (inp.fromFilterDecimal.compareTo(maxPrice) > 0) {
								product.remove();
								continue;
							}
						}
						if (inp.hasToFilter) {
							String minPriceStr = JsoupUtils.getSelectorFirstValue(product, "min_price");
							BigDecimal minPrice = DecimalDataType.parse(minPriceStr, 4);
							if (inp.toFilterDecimal.compareTo(minPrice) < 0) {
								product.remove();
							}
						}
					}
				}
				return getResult("product_list").setValue(JsoupUtils.outputXmlDoc(doc));
			}
			return getResult("product_list").setValue(xml);
		}

		// Получение ответа сервера
		// В случае если надо получать результат с сервера
		else {
			CurrencyRates rates = new CurrencyRates();
			XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();
			xml.startElement("result");
			BigDecimal globalMaxPrice = BigDecimal.ONE.negate();
			BigDecimal globalMinPrice = BigDecimal.valueOf(Double.MAX_VALUE);

			// Загрузка с сервера
			ResultPE result = getFromServer(xml, rates);
			if (result != null) {
				return result;
			}

			// Поиск в локальной базе
			if (localSearch) {
				// Загрузка курсов валют
				String priceParamName = PRICE_PREFIX + (StringUtils.isBlank(inp.curCode) ? rates.getDefaultCurrency() : inp.curCode);
				BigDecimal extraQuotient = BigDecimal.ONE; // дополнительный коэффициент для цены
				Item localCatalog = ItemUtils.ensureSingleRootAnonymousItem(PLAIN_CATALOG, getInitiator());
				ItemQuery localQuery = new ItemQuery(PRODUCT).setParentId(localCatalog.getId(), true)
						.setFulltextCriteria(FulltextQueryCreatorRegistry.DEFAULT, inp.query, 50, null, Compare.SOME)
						.setLimit(50).addSorting(product_.SECTION_NAME, "ASC");
				List<Item> prods = localQuery.loadItems();
				final String NONE_DISTR = "~~@~NONE~@~~";
				String currentDistributor = NONE_DISTR;

				for (Item prod : prods) {
					Product p = Product.get(prod);
					String distributor = p.getDefault_section_name("partnumber.ru");
					// Если поменялось название поставщика в отсортированном списке - создать новый тэг
					if (!StringUtils.equals(distributor, currentDistributor)) {
						if (!StringUtils.equals(NONE_DISTR, currentDistributor)) {
							xml.endElement(); // предыдущий поставщик (если нужно закрывать)
						}
						xml.startElement("distributor", "name", distributor);
						currentDistributor = distributor;
						Item catalogSettings = new ItemQuery(PRICE_CATALOG).addParameterEqualsCriteria(price_catalog_.NAME, distributor).loadFirstItem();
						extraQuotient = BigDecimal.ONE; // дополнительный коэффициент для цены
						if (catalogSettings != null) {
							extraQuotient = catalogSettings.getDecimalValue(price_catalog_.QUOTIENT, BigDecimal.ONE);
						}
					}
					xml.startElement("product", "id", p.get_code(), "key", p.getKeyUnique());
					xml.addElement("code", Strings.translit(p.get_code()));
					xml.addElement("name", p.get_name());
					xml.addElement("vendor", p.get_vendor());
					xml.addElement("qty", p.get_qty());
					xml.addElement("step", p.get_step());
					xml.addElement("description", p.get_name_extra());
					xml.addElement("min_qty", p.get_min_qty());
					xml.addElement("next_delivery", p.get_next_delivery());
					xml.addElement("category_id", distributor);
					xml.addElement("pricebreak", "1");
					xml.addElement("currency_id", "RUB");
					xml.startElement("prices");

					xml.startElement("break", "qty", "1");
					BigDecimal priceOriginal = p.getDecimalValue(priceParamName, BigDecimal.ZERO);
					BigDecimal maxPrice = BigDecimal.ONE.negate();
					BigDecimal minPrice = BigDecimal.valueOf(Double.MAX_VALUE);
					xml.addElement("price_original", priceOriginal);
					// Применить коэффициент
					priceOriginal = priceOriginal.multiply(extraQuotient).setScale(4, RoundingMode.UP);
					HashMap<String, BigDecimal> allPricesDecimal = rates.setAllPricesXML(xml, priceOriginal, inp.curCode);
					BigDecimal currentPrice = allPricesDecimal.get(priceParamName);
					if (currentPrice != null) {
						maxPrice = currentPrice;
						minPrice = currentPrice;
						globalMaxPrice = globalMaxPrice.max(currentPrice);
						globalMinPrice = globalMinPrice.min(currentPrice);
					}
					xml.endElement(); // break
					xml.endElement(); // prices

					// Добавление элементов с максимальной и минимальной ценой (чтобы сохранилась в кеше)
					xml.addElement("max_price", maxPrice);
					xml.addElement("min_price", minPrice);
					// Если девайс не подходит по фильтрам - добавить тэг <invalid>invalid</invalid>
					boolean isInvalid
							= (inp.hasShipDateFilter && !inp.shipDateFilter.contains(p.get_next_delivery()))
							|| (inp.hasVendorFilter && !inp.vendorFilter.contains(p.get_vendor()))
							|| (inp.hasDistributorFilter && !inp.distributorFilter.contains("partnumber.ru"))
							|| (inp.hasFromFilter && maxPrice.compareTo(inp.fromFilterDecimal) < 0)
							|| (inp.hasToFilter && minPrice.compareTo(inp.toFilterDecimal) > 0);
					if (isInvalid) {
						xml.addElement("invalid", "invalid");
					}
					xml.endElement(); // product
				}
				xml.endElement(); // distributor
			}

			xml.addElement("max_price", globalMaxPrice);
			xml.addElement("min_price", globalMinPrice);

			xml.endElement(); // result
			// Сохранение кеша
			if (!cacheDir.exists()) {
				cacheDir.mkdirs();
			}
			FileUtils.write(cacheFile, xml.getXmlStringSB(), StandardCharsets.UTF_8);

			return getResult("product_list").setValue(xml.toString());
		}
	}

	/**
	 * Загрузить данные с сервера и оформить в виде XML
	 * Возвращает резльутат ResultPE в случае ошибки
	 * @param xml
	 * @return
	 */
	protected ResultPE getFromServer(XmlDocumentBuilder xml, CurrencyRates rates) throws Exception {
		String html = null;
		String proxy = getVarSingleValue("proxy");
		// Поиск по всем удаленным серверам (обычно один)
		for (Object server : inp.servers) {
			try {
				String query = StringUtils.normalizeSpace(inp.query);
				if (StringUtils.isNotBlank(query)) {
					query = URLEncoder.encode(query, Strings.SYSTEM_ENCODING);
					if (StringUtils.isNotBlank((String) server)) {
						Request request = ProxyRequestDispatcher.submitRequest("findchips", query);
						request.awaitExecution();
						Request.Query response = request.getAllQueries().iterator().next();
						html = response.getResult();
						/*
						String requestUrl = server + query;
						if (StringUtils.isNotBlank(proxy) && StringUtils.startsWith(proxy, "http")) {
							String proxyUrl = proxy + "?url=" + requestUrl;
							html = OkWebClient.getInstance().getString(proxyUrl);
						} else {
							html = OkWebClient.getInstance().getString(requestUrl);
						}
						 */
					}
					// если задан параметр server - значит надо подулючаться удаленному серверу
					else {
						return getResult("illegal_argument").setValue("Не указан сервер");
					}
				} else {
					return getResult("illegal_argument").setValue("Неверный формат запроса");
				}
			} catch (Exception e) {
				return getResult("error").setValue(ExceptionUtils.getStackTrace(e));
			}

			// Парсинг и создание XML
			Document doc = Jsoup.parse(html);
			Elements distributors = doc.select("div.distributor-results");
			if (distributors.size() == 0)
				return getResult("error").setValue("Не найден элемент дистрибьютора");
			xml.addElement("query", inp.query);

			for (Element distributorEl : distributors) {
				String distributor = distributorEl.attr("data-distributor_name");
				if (inp.dstrSet.size() > 0 && !inp.dstrSet.contains(distributor)) {
					continue;
				}
				// Загрузка курсов валют
				String priceParamName = PRICE_PREFIX + (StringUtils.isBlank(inp.curCode) ? rates.getDefaultCurrency() : inp.curCode);
				Item catalogSettings = new ItemQuery(PRICE_CATALOG).addParameterEqualsCriteria(price_catalog_.NAME, distributor).loadFirstItem();
				BigDecimal extraQuotient = BigDecimal.ONE; // дополнительный коэффициент для цены
				if (catalogSettings != null)
					extraQuotient = catalogSettings.getDecimalValue(price_catalog_.QUOTIENT, BigDecimal.ONE);

				xml.startElement("distributor", "name", distributor);
				Elements lines = distributorEl.select("tr.row");
				for (Element line : lines) {
					String name = JsoupUtils.getSelectorFirstValue(line, "td:eq(0) a");
					String code = JsoupUtils.getSelectorFirstValue(line, "span.additional-value");
					if (StringUtils.isBlank(code)) {
						code = name + "_" + Integer.toHexString(distributor.hashCode());
					}
					String key = Strings.translit(name + " " + code);
					String vendor = JsoupUtils.getSelectorFirstValue(line, "td:eq(1)");
					String description = JsoupUtils.getSelectorFirstValue(line, "td:eq(2) span:eq(0)");
					String minQtyStr = JsoupUtils.getSelectorFirstValue(line, "td:eq(2) span[data-title='Min Qty']");
					String leadTime = JsoupUtils.getSelectorFirstValue(line, "td:eq(2) span[data-title='Lead time']");
					String stepStr = minQtyStr;
					String container = JsoupUtils.getSelectorFirstValue(line, "td:eq(2) span[data-title='Container']");
					String qtyStr = JsoupUtils.getSelectorFirstValue(line, "td:eq(3)");
					xml.startElement("product", "id", code, "key", key);
					xml.addElement("code", code);
					xml.addElement("name", name);
					xml.addElement("vendor", vendor);
					xml.addElement("qty", qtyStr);
					xml.addElement("step", stepStr);
					xml.addElement("description", description);
					xml.addElement("min_qty", minQtyStr);
					xml.addElement("next_delivery", leadTime);
					xml.addElement("container", container);
					xml.addElement("category_id", distributor);
					Elements priceLis = line.select("td.td-price li[class]");
					xml.addElement("pricebreak", priceLis.size());
					boolean noCurrency = true;
					boolean noBreaks = true;
					BigDecimal maxPrice = BigDecimal.ONE.negate();
					BigDecimal minPrice = BigDecimal.valueOf(Double.MAX_VALUE);
					for (Element priceLi : priceLis) {
						String breakQtyStr = JsoupUtils.getSelectorFirstValue(priceLi, "span.label");
						Element data = priceLi.select("span.value").first();
						if (data == null)
							continue;
						String priceStr = data.attr("data-baseprice");
						String currencyCode = data.attr("data-basecurrency");
						if (noCurrency) {
							xml.addElement("currency_id", currencyCode);
							noCurrency = false;
						}
						if (noBreaks) {
							xml.startElement("prices");
							noBreaks = false;
						}
						xml.startElement("break", "qty", breakQtyStr);
						BigDecimal priceOriginal = DecimalDataType.parse(priceStr, 4);
						if (priceOriginal != null) {
							xml.addElement("price_original", priceOriginal);
							// Применить коэффициент
							priceOriginal = priceOriginal.multiply(extraQuotient).setScale(4, RoundingMode.UP);
							HashMap<String, BigDecimal> allPricesDecimal = rates.setAllPricesXML(xml, priceOriginal, currencyCode);
							BigDecimal currentPrice = allPricesDecimal.get(priceParamName);
							if (currentPrice != null) {
								maxPrice = maxPrice.max(currentPrice);
								minPrice = minPrice.min(currentPrice);
								inp.globalMaxPrice = inp.globalMaxPrice.max(currentPrice);
								inp.globalMinPrice = inp.globalMinPrice.min(currentPrice);
							}
						}
						xml.endElement(); // break
					}
					if (!noBreaks) {
						xml.endElement(); // prices
					}
					// Добавление элементов с максимальной и минимальной ценой (чтобы сохранилась в кеше)
					xml.addElement("max_price", maxPrice);
					xml.addElement("min_price", minPrice);
					// Если девайс не подходит по фильтрам - добавить тэг <invalid>invalid</invalid>
					boolean isInvalid
							= (inp.hasShipDateFilter && !inp.shipDateFilter.contains(leadTime))
							|| (inp.hasVendorFilter && !inp.vendorFilter.contains(vendor))
							|| (inp.hasDistributorFilter && !inp.distributorFilter.contains(distributor))
							|| (inp.hasFromFilter && maxPrice.compareTo(inp.fromFilterDecimal) < 0)
							|| (inp.hasToFilter && minPrice.compareTo(inp.toFilterDecimal) > 0);
					if (isInvalid) {
						xml.addElement("invalid", "invalid");
					}
					xml.endElement(); // product
				}
				xml.endElement(); // distributor
			}
		}
		return null;
	}
}
