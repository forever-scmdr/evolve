package extra;

import ecommander.controllers.AppContext;
import ecommander.controllers.PageController;
import ecommander.fwk.*;
import ecommander.fwk.integration.ExcelTemplateProcessor;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.model.datatypes.DateDataType;
import ecommander.model.datatypes.DecimalDataType;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.LinkPE;
import ecommander.pages.ResultPE;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import extra._generated.Price_catalog;
import extra._generated.User_phys;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.Multipart;
import javax.mail.internet.MimeBodyPart;
import javax.mail.util.ByteArrayDataSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;

/**
 * Корзина
 * Created by E on 6/3/2018.
 */
public class CartManageCommand extends BasicCartManageCommand implements ItemNames {

	public static final String PLAIN_SECTION = "plain_section";
	public static final String PRICE_CATALOG = "price_catalog";
	private static final String DEFAULT = "default";
	private static final String JSON_ORDERS_DIR = "WEB-INF/json_orders";

	private static class PriceCatalog {
		private String name;
		private BigDecimal defaultQuotient;
		private ArrayList<Pair<BigDecimal, BigDecimal>> qtyQuotients = new ArrayList<>();
		private boolean quotientForPack;

		private PriceCatalog(String name, BigDecimal defaultQuotient, boolean quotientForPack) {
			this.name = name;
			this.defaultQuotient = defaultQuotient;
			this.quotientForPack = quotientForPack;
			if (defaultQuotient == null || defaultQuotient.equals(BigDecimal.ZERO)) {
				this.defaultQuotient = BigDecimal.ONE;
			}
		}

		private void prepare() {
			qtyQuotients.sort((first, second) -> first.getLeft().subtract(second.getLeft()).intValue());
		}

		private void addQuotient(Pair<String, String> pair) {
			qtyQuotients.add(new Pair<>(DecimalDataType.parse(pair.getLeft(), 2), DecimalDataType.parse(pair.getRight(), 2)));
		}
	}


	// Раздел => Базовый коэффициент, Коэффициенты от суммы
	private HashMap<String, PriceCatalog> priceIntervals = null;


	public static final HashSet<String> MANDATORY_PHYS = new HashSet<>();
	public static final HashSet<String> MANDATORY_JUR = new HashSet<>();
	static {
		MANDATORY_PHYS.add(user_phys_.NAME);
		//MANDATORY_PHYS.add(ItemNames.user_phys_.ADDRESS);
		//MANDATORY_PHYS.add(ItemNames.user_phys_.EMAIL);
		MANDATORY_PHYS.add(user_phys_.PHONE);
		//MANDATORY_PHYS.add(ItemNames.user_phys_.SHIP_TYPE);

		//MANDATORY_JUR.add(ItemNames.user_jur_.ACCOUNT);
		//MANDATORY_JUR.add(ItemNames.user_jur_.ADDRESS);
		//MANDATORY_JUR.add(ItemNames.user_jur_.BANK);
		//MANDATORY_JUR.add(ItemNames.user_jur_.BANK_ADDRESS);
		//MANDATORY_JUR.add(ItemNames.user_jur_.BANK_CODE);
		MANDATORY_JUR.add(user_jur_.CONTACT_NAME);
		//MANDATORY_JUR.add(ItemNames.user_jur_.CONTACT_PHONE);
		MANDATORY_JUR.add(user_jur_.PHONE);
		//MANDATORY_JUR.add(ItemNames.user_jur_.DIRECTOR);
		//MANDATORY_JUR.add(ItemNames.user_jur_.EMAIL);
		MANDATORY_JUR.add(user_jur_.ORGANIZATION);
		//MANDATORY_JUR.add(ItemNames.user_jur_.SHIP_TYPE);
		//MANDATORY_JUR.add(ItemNames.user_jur_.UNP);
	}

	public ResultPE addCustomToCart() throws Exception{
		String code = getVarSingleValue("prod");
		String id = getVarSingleValue("id");
		ensureCart();
		Item bought = getSessionMapper().getSingleItemByParamValue(BOUGHT_ITEM, CODE_PARAM, code);
		if (bought == null) {
			bought = getSessionMapper().createSessionItem(BOUGHT_ITEM, cart.getId());
			bought.setValue(CODE_PARAM, code);
			bought.setValue(QTY_PARAM, 1d);
			getSessionMapper().saveTemporaryItem(bought);

			Item product = getSessionMapper().createSessionItem(PRODUCT_ITEM, bought.getId());
			product.setValue(CODE_PARAM, code);
			product.setValue(NAME_PARAM, code);
			product.setExtra("id", id);

			product.setValue(QTY_PARAM, 10000000d);
			getSessionMapper().saveTemporaryItem(product);
			setBoughtQtys(product, bought, 1d);
		} else {
			//bought.setValue(QTY_PARAM, bought.getDoubleValue(QTY_PARAM) + 1);
			Item product = getSessionMapper().getSingleItemByName(PRODUCT_ITEM, bought.getId());
			setBoughtQtys(product, bought, bought.getDoubleValue(QTY_PARAM) + 1);
		}

		recalculateCart();
		return getResult("ajax");
	}

	@Override
	public ResultPE recalculate() throws Exception {
		super.recalculate();
		return getResult("ajax");
	}

	@Override
	protected boolean validate() throws Exception {
		String manPhysStr = getVarSingleValueDefault("mandatory_phys", null);
		String manJurStr = getVarSingleValueDefault("mandatory_jur", null);
		if (StringUtils.isNotBlank(manPhysStr)) {
			String[] paramNames = StringUtils.split(manPhysStr, ",; ");
			synchronized (MANDATORY_PHYS) {
				MANDATORY_PHYS.clear();
				for (String paramName : paramNames) {
					MANDATORY_PHYS.add(StringUtils.trim(paramName));
				}
			}
		}
		if (StringUtils.isNotBlank(manJurStr)) {
			String[] paramNames = StringUtils.split(manJurStr, ",; ");
			synchronized (MANDATORY_JUR) {
				MANDATORY_JUR.clear();
				for (String paramName : paramNames) {
					MANDATORY_JUR.add(StringUtils.trim(paramName));
				}
			}
		}
		Item form = getItemForm().getItemSingleTransient();
		boolean isPhys = form.getTypeId() == ItemTypeRegistry.getItemType(USER_PHYS).getTypeId();
		boolean hasError = false;
		if (isPhys) {
			for (String mandatory : MANDATORY_PHYS) {
				if (form.isValueEmpty(mandatory)) {
					getItemForm().setValidationError(form.getId(), mandatory, "Не заполнен параметр");
					hasError = true;
				}
			}
			removeSessionForm("customer_jur");
			saveSessionForm("customer_phys");
		} else {
			for (String mandatory : MANDATORY_JUR) {
				if (form.isValueEmpty(mandatory)) {
					getItemForm().setValidationError(form.getId(), mandatory, "Не заполнен параметр");
					hasError = true;
				}
			}
			removeSessionForm("customer_phys");
			saveSessionForm("customer_jur");
		}
		return !hasError;
	}


	@Override
	protected void extraProductLoading(Item product) throws Exception {
		Item section = new ItemQuery(PLAIN_SECTION).setChildId(product.getId(), false).loadFirstItem();
		if (section != null) {
			section.setContextParentId(ItemTypeRegistry.getPrimaryAssoc(), product.getId());
			getSessionMapper().saveTemporaryItem(section);
		}
	}


	@Override
	protected BigDecimal getProductPriceForQty(Item product, String priceParam, double qty) throws Exception {
		// Для товаров, которые были загружены из БД (реальные товары из каталога)
		if (product.getId() >= 0) {
			Item section = getSessionMapper().getSingleItemByName(PLAIN_SECTION, product.getId());
			if (section != null) {
				loadPriceIntervals();
				String secName = section.getStringValue(NAME_PARAM);
				PriceCatalog quotients = priceIntervals.get(secName);
				if (quotients != null) {
					BigDecimal quotient = quotients.defaultQuotient;
					for (Pair<BigDecimal, BigDecimal> qtyQuotient : quotients.qtyQuotients) {
						double qtyLimit = qtyQuotient.getLeft().doubleValue();
						if (quotients.quotientForPack)
							qtyLimit *= product.getDoubleValue("step", 1.0);
						if (qtyLimit > qty) {
							break;
						}
						quotient = qtyQuotient.getRight();
					}
					return product.getDecimalValue(priceParam, BigDecimal.ZERO).multiply(quotient);
				}
			}
		}
		// Для сторонних товаров, которых нет в БД
		else {
			String outerXML = product.getStringValue(EXTRA_XML_PARAM);
			if (StringUtils.isNotBlank(outerXML)) {
				Document parsed = JsoupUtils.parseXml(outerXML);
				Element prices = parsed.getElementsByTag(SearchApiCommand.PRICES_TAG).first();
				HashMap<BigDecimal, BigDecimal> intervals = new HashMap<>();
				if (prices != null) {
					Elements breaks = parsed.getElementsByTag(SearchApiCommand.BREAK_TAG);
					for (Element aBreak : breaks) {
						BigDecimal breakQty = DecimalDataType.parse(aBreak.attr(SearchApiCommand.QTY_ATTR), 2);
						if (breakQty == null)
							breakQty = BigDecimal.ONE;
						BigDecimal breakPrice = DecimalDataType.parse(JsoupUtils.getTagFirstValue(aBreak, SearchApiCommand.PRICE_TAG), 2);
						intervals.put(breakQty, breakPrice);
					}
					ArrayList<BigDecimal> qtysOrdered = new ArrayList<>(intervals.keySet());
					Collections.sort(qtysOrdered);
					qtysOrdered.add(BigDecimal.valueOf(Long.MAX_VALUE)); // чтобы гарантированно не выйти за предел
					BigDecimal decimalQty = new BigDecimal(qty);
					for (int i = 0; i < qtysOrdered.size(); i++) {
						if (qtysOrdered.get(i).compareTo(decimalQty) > 0) {
							if (i > 0)
								return intervals.get(qtysOrdered.get(i - 1));
							return intervals.get(qtysOrdered.get(i));
						}
					}
				}
			}
		}
		return product.getDecimalValue(priceParam, BigDecimal.ZERO);
	}

	/**
	 * Загрузить интервалы цен и коэфиициенты для этих интервалов
	 */
	private void loadPriceIntervals() {
		if (priceIntervals == null) {
			priceIntervals = new HashMap<>();
			try {
				List<Item> priceCatalogs = new ItemQuery(PRICE_CATALOG).loadItems();
				for (Item priceCatalog : priceCatalogs) {
					Price_catalog cat = Price_catalog.get(priceCatalog);
					PriceCatalog intervals = new PriceCatalog(cat.get_name(), cat.getDefault_quotient(BigDecimal.ONE), !"количество".equalsIgnoreCase(cat.get_qty_quotient_policy()));
					for (Pair<String, String> quotients : cat.getTupleValues(Price_catalog.QTY_QUOTIENT)) {
						intervals.addQuotient(quotients);
					}
					intervals.prepare();
					priceIntervals.put(cat.get_name(), intervals);
				}
			} catch (Exception e) {
				ServerLogger.error("Unable to load price intervals", e);
			}
		}
	}


/*
	@Override
	protected boolean recalculateCart(String...priceParamName) throws Exception {
//		if (discounts == null) {
//			Item common = ItemUtils.ensureSingleRootAnonymousItem(ItemNames.COMMON, User.getDefaultUser());
//			discounts = Discounts.get(ItemUtils.ensureSingleItem(ItemNames.DISCOUNTS,
//					User.getDefaultUser(), common.getId(), common.getOwnerGroupId(), common.getOwnerUserId()));
//		}
		double discount = 0.0d;
		User_jur user = User_jur.get(new ItemQuery(ItemNames.USER_JUR).setUser(getInitiator()).loadFirstItem());
		if (user != null) {
			discount += user.getDefault_discount(0.0d);
		}
		boolean success = super.recalculateCart(user != null ? ItemNames.product_.PRICE_OPT : PRICE_PARAM);
		BigDecimal originalSum = cart.getDecimalValue(SUM_PARAM, new BigDecimal(0));
//		if (originalSum.compareTo(discounts.get_sum_more()) >= 0) {
//			discount += discounts.get_sum_discount();
//		}
		MultipleHttpPostForm userForm = getSessionForm("customer_jur");
		if (userForm == null)
			userForm = getSessionForm("customer_phys");
		if (userForm != null) {
			Item uf = userForm.getItemSingleTransient();
			if (uf != null) {
				String shipType = uf.getStringValue("ship_type");
				if (StringUtils.containsIgnoreCase(shipType, "самовывоз")) {
					//discount += discounts.get_self_delivery();
				} else if (StringUtils.containsIgnoreCase(shipType, "автолайт")) {
					//discount -= discounts.get_autolight();
				} else if (StringUtils.containsIgnoreCase(shipType, "доставка")) {
					//discount -= discounts.get_delivery();
				}
				if (StringUtils.containsIgnoreCase(uf.getStringValue("pay_type"), "предоплата")) {
					//discount += discounts.get_pay_first();
				}
			}
		}
		//BigDecimal discountedSum = originalSum.multiply(new BigDecimal((100 - discount) / 100));
		//discountedSum = discountedSum.setScale(2, BigDecimal.ROUND_HALF_EVEN);
		//cart.setValue(ItemNames.cart_.SUM_DISCOUNT, discountedSum);
		// Сумма прописью
		//BigDecimal rub = discountedSum.setScale(0, BigDecimal.ROUND_FLOOR);
		//BigDecimal kop = discountedSum.subtract(rub).multiply(new BigDecimal(100)).setScale(0, BigDecimal.ROUND_CEILING);
		//String sumText = Strings.numberToRusWords(rub.doubleValue()) + " "
		//		+ Strings.numberEnding(rub.doubleValue(), "белорусский рубль", "белорусских рубля", "белорусских рублей")
		//		+ " " + kop + " "
		//		+ Strings.numberEnding(kop.doubleValue(), "копейка", "копейки", "копеек");
		//String dateStr = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(new Locale("ru")).format(LocalDate.now());
		//cart.setValueUI(ItemNames.cart_.EXTRA_SUM_STR, sumText);
		//cart.setValueUI(ItemNames.cart_.EXTRA_DATE_STR, dateStr);
		getSessionMapper().saveTemporaryItem(cart);
		return success;
	}

	public ResultPE update() throws Exception {
		Item form = getItemForm().getItemSingleTransient();
		boolean isPhys = form.getTypeId() == ItemTypeRegistry.getItemType(ItemNames.USER_PHYS).getTypeId();
		if (isPhys) {
			removeSessionForm("customer_jur");
			saveSessionForm("customer_phys");
		} else {
			removeSessionForm("customer_phys");
			saveSessionForm("customer_jur");
		}
		recalculateCart(isPhys ? PRICE_PARAM : ItemNames.product_.PRICE);
		return getResult("proceed");
	}

	@Override
	protected boolean addExtraEmailBodyPart(boolean isCustomerEmail, Multipart mp) {
		try {
			LinkPE pdfLink = LinkPE.newDirectLink("link", "order_pdf", false);
			ExecutablePagePE pdfTemplate = getExecutablePage(pdfLink.serialize());
			ByteArrayOutputStream pdfHtmlBytes = new ByteArrayOutputStream();
			PageController.newSimple().executePage(pdfTemplate, pdfHtmlBytes);

			File dir = new File(AppContext.getFilesDirPath(false) + "pdf");
			dir.mkdir();
			File output = new File(AppContext.getFilesDirPath(false) + "pdf/" + String.valueOf(cart.getStringValue(ItemNames.cart_.ORDER_NUM)) + ".pdf");
			if (!output.exists()) {
				String content = new String(pdfHtmlBytes.toByteArray(), Strings.SYSTEM_ENCODING);

				ITextRenderer renderer = new ITextRenderer();
				String fontPath = AppContext.getContextPath() + "ARIALUNI.TTF";
				renderer.getFontResolver().addFont(fontPath, BaseFont.IDENTITY_H, BaseFont.NOT_EMBEDDED);
				renderer.setDocumentFromString(content);
				renderer.layout();
				FileOutputStream fos = new FileOutputStream(output);
				renderer.createPDF(fos);
				fos.close();
			}
			FileInputStream fis = new FileInputStream(output);
			DataSource dataSource = new ByteArrayDataSource(fis, "application/pdf");
			MimeBodyPart filePart = new MimeBodyPart();
			filePart.setDataHandler(new DataHandler(dataSource));
			filePart.setFileName(output.getName());
			mp.addBodyPart(filePart);
			return true;
		} catch (Exception e) {
			ServerLogger.error("Email PDF generation error", e);
			return false;
		}
	}
	*/

	@Override
	protected boolean addExtraEmailBodyPart(boolean isCustomerEmail, Multipart mp) throws Exception {
		// ПРикрепляет к письму все файловые extra параметры, которые были назедны в запросе
		if (!isCustomerEmail) {
			Item form = getItemForm().getItemSingleTransient();
			for (String extraKey : form.getExtraKeys()) {
				ArrayList<Object> values = form.getListExtra(extraKey);
				if (values.size() > 0 && values.get(0) instanceof FileItem) {
					for (Object value : values) {
						FileItem file = (FileItem) value;
						DataSource dataSource = new ByteArrayDataSource(file.getInputStream(), file.getContentType());
						MimeBodyPart filePart = new MimeBodyPart();
						filePart.setDataHandler(new DataHandler(dataSource));
						filePart.setFileName(file.getName());
						mp.addBodyPart(filePart);
					}
				}
			}
		}
		return true;
	}

	@Override
	protected boolean addExtraEmailBillPart(String orderNum, Multipart mp) throws Exception {
		Item catalogMeta = ItemQuery.loadSingleItemByName(CATALOG_META);
		if (catalogMeta == null)
			return false;
		File template = catalogMeta.getFileValue(catalog_meta_.EXCEL_ORDER_TEMPLATE, AppContext.getFilesDirPath(false));
		if (template != null && template.exists()) {
			try {
				Item customer = getItemForm().getItemSingleTransient();
				boolean isPhys = customer.getTypeId() == ItemTypeRegistry.getItemType(ItemNames.USER_PHYS).getTypeId();
				ExcelTemplateProcessor proc = new ExcelTemplateProcessor(template);
				proc.replace("$doc_num", "C-" + orderNum);
				proc.replace("$order_num", orderNum);
				proc.replace("$order_date", DateDataType.outputDate(System.currentTimeMillis(), DateDataType.DAY_FORMATTER));
				proc.replace("$customer_name", isPhys ? customer.outputValue(user_phys_.NAME) : customer.outputValue(user_jur_.ORGANIZATION));
				proc.replace("$customer_address", isPhys ? customer.outputValue(user_phys_.ADDRESS) : customer.outputValue(user_jur_.ADDRESS));
				ArrayList<Item> boughts = getSessionMapper().getItemsByName(BOUGHT_ITEM, cart.getId());
				for (int i = 0; i < boughts.size() - 1; i++) {
					proc.duplicateRowContaining("$prod_name");
				}
				BigDecimal nds = BigDecimal.valueOf((double) 20);
				BigDecimal ndsPercents = nds.add(BigDecimal.valueOf((double) 100));
				BigDecimal ndsQuotient = BigDecimal.valueOf((double) 100).divide(ndsPercents, 6, RoundingMode.HALF_EVEN);
				for (Item bought : boughts) {
					Item product = getSessionMapper().getSingleItemByName(PRODUCT_ITEM, bought.getId());
					BigDecimal sum = bought.getDecimalValue(bought_.SUM).setScale(2, RoundingMode.CEILING);
					BigDecimal noNdsSum = sum.multiply(ndsQuotient).setScale(2, RoundingMode.CEILING);
					BigDecimal justNdsSum = sum.subtract(noNdsSum).setScale(2, RoundingMode.CEILING);
					BigDecimal noNdsPrice = noNdsSum.divide(BigDecimal.valueOf(bought.getDoubleValue(bought_.QTY)), 6, RoundingMode.HALF_EVEN)
							.setScale(2, RoundingMode.CEILING);
					proc.replace("$prod_name", bought.outputValue(bought_.NAME));
					proc.replace("$prod_unit", product.getStringValue(described_product_.UNIT, "шт."));
					proc.replace("$prod_qty", bought.outputValue(bought_.QTY));
					proc.replace("$prod_price", noNdsPrice.toPlainString());
					proc.replace("$prod_sum", noNdsSum.toPlainString());
					proc.replace("$prod_sum_nds", justNdsSum.toPlainString());
					proc.replace("$prod_sum_full", sum.toPlainString());
					proc.replace("$prod_dlv", product.getStringValue(described_product_.NEXT_DELIVERY, "7"));
				}
				BigDecimal cartSum = cart.getDecimalValue(cart_.SUM).setScale(2, RoundingMode.CEILING);;
				BigDecimal roubles = cartSum.setScale(0, RoundingMode.DOWN);
				BigDecimal cents = cartSum.subtract(roubles).multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.CEILING);
				BigDecimal sumNoNds = cartSum.multiply(ndsQuotient).setScale(2, RoundingMode.CEILING);;
				BigDecimal justNds = cartSum.subtract(sumNoNds);
				BigDecimal roublesJustNds = justNds.setScale(0, RoundingMode.DOWN);
				BigDecimal centsJustNds = justNds.subtract(roublesJustNds).multiply(BigDecimal.valueOf(100)).setScale(0, RoundingMode.CEILING);;
				proc.replace("$cart_sum", sumNoNds.toPlainString());
				proc.replace("$cart_sum_nds", justNds.toPlainString());
				proc.replace("$cart_sum_full", cartSum.toPlainString());
				String justNdsRubString = Strings.numberToRusWords(roublesJustNds.longValue()) + " "
						+ Strings.numberEnding(roublesJustNds.longValue(), "рубль", "рубля", "рублей");
				String justNdsCentString = Strings.numberToRusWords(centsJustNds.longValue()) + " "
						+ Strings.numberEnding(centsJustNds.longValue(), "копейка", "копейки", "копеек");
				String rubString = Strings.numberToRusWords(roubles.longValue()) + " "
						+ Strings.numberEnding(roubles.longValue(), "рубль", "рубля", "рублей");
				String centString = Strings.numberToRusWords(cents.longValue()) + " "
						+ Strings.numberEnding(cents.longValue(), "копейка", "копейки", "копеек");
				boolean hasNdsCents = centsJustNds.compareTo(BigDecimal.valueOf(1)) >= 0;
				boolean hasSumCents = cents.compareTo(BigDecimal.valueOf(1)) >= 0;
				proc.replace("$just_nds_words", justNdsRubString + (hasNdsCents ? " " + justNdsCentString : ""));
				proc.replace("$sum_words", rubString + (hasSumCents ? " " + centString : ""));

				File xlsFile = new File(AppContext.getFilesDirPath(false) + "bill_" + orderNum + proc.getExtension());
				proc.writeExcelFile(xlsFile);
				DataSource xlsxSource = new FileDataSource(xlsFile); // , "application/vnd.ms-excel"
				MimeBodyPart xlsxPart = new MimeBodyPart();
				xlsxPart.setDataHandler(new DataHandler(xlsxSource));
				xlsxPart.setFileName(xlsFile.getName());
				mp.addBodyPart(xlsxPart);
			} catch (Exception e) {
				ServerLogger.error("Can not use customer bill template ", e);
				return false;
			}
		}
		return true;
	}


	@Override
	protected boolean postProcessCart(String orderNum) throws Exception {
		String fileName = getVarSingleValueDefault("order_dir", JSON_ORDERS_DIR);
		String path = AppContext.getRealPath(fileName);
		File dir = new File(path);
		if (!dir.exists()) {
			dir.mkdirs();
		}
		//Item user = getSessionMapper().getSingleRootItemByName(USER);
		Item customer = getItemForm().getItemSingleTransient();
		LinkPE orderFileLink = LinkPE.newDirectLink("link", "order_file", false);
		orderFileLink.addStaticVariable("order_num", orderNum);
		orderFileLink.addStaticVariable("deivery", customer.getStringValue(User_phys.SHIP_TYPE));
		orderFileLink.addStaticVariable("payment", customer.getStringValue(User_phys.PAY_TYPE));
		//orderFileLink.addStaticVariable("currency", currency);

		ExecutablePagePE orderFileTemplate = getExecutablePage(orderFileLink.serialize());
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		PageController.newSimple().executePage(orderFileTemplate, out);
		File file = new File(path + "/" + orderNum + ".json");
		FileUtils.writeByteArrayToFile(file, out.toByteArray());

		return true;
	}

}
