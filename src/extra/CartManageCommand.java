package extra;

import ecommander.fwk.BasicCartManageCommand;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.pages.ResultPE;
import extra._generated.ItemNames;

import java.util.HashSet;

/**
 * Корзина
 * Created by E on 6/3/2018.
 */
public class CartManageCommand extends BasicCartManageCommand {

	public static final HashSet<String> MANDATORY_PHYS = new HashSet<>();
	public static final HashSet<String> MANDATORY_JUR = new HashSet<>();
	static {
		MANDATORY_PHYS.add(ItemNames.user_phys_.NAME);
		//MANDATORY_PHYS.add(ItemNames.user_phys_.ADDRESS);
		//MANDATORY_PHYS.add(ItemNames.user_phys_.EMAIL);
		MANDATORY_PHYS.add(ItemNames.user_phys_.PHONE);
		//MANDATORY_PHYS.add(ItemNames.user_phys_.SHIP_TYPE);

		//MANDATORY_JUR.add(ItemNames.user_jur_.ACCOUNT);
		//MANDATORY_JUR.add(ItemNames.user_jur_.ADDRESS);
		//MANDATORY_JUR.add(ItemNames.user_jur_.BANK);
		//MANDATORY_JUR.add(ItemNames.user_jur_.BANK_ADDRESS);
		//MANDATORY_JUR.add(ItemNames.user_jur_.BANK_CODE);
		MANDATORY_JUR.add(ItemNames.user_jur_.CONTACT_NAME);
		//MANDATORY_JUR.add(ItemNames.user_jur_.CONTACT_PHONE);
		MANDATORY_JUR.add(ItemNames.user_jur_.PHONE);
		//MANDATORY_JUR.add(ItemNames.user_jur_.DIRECTOR);
		//MANDATORY_JUR.add(ItemNames.user_jur_.EMAIL);
		MANDATORY_JUR.add(ItemNames.user_jur_.ORGANIZATION);
		//MANDATORY_JUR.add(ItemNames.user_jur_.SHIP_TYPE);
		//MANDATORY_JUR.add(ItemNames.user_jur_.UNP);
	}

	public ResultPE addCustomToCart() throws Exception{
		String code = getVarSingleValue("prod");
		String id = getVarSingleValue("id");
		ensureCart();
		Item bought = getSessionMapper().getSingleItemByParamValue(BOUGHT_ITEM, CODE_PARAM, code);
		if(bought == null){
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
		}else{
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
		Item form = getItemForm().getItemSingleTransient();
		boolean isPhys = form.getTypeId() == ItemTypeRegistry.getItemType(ItemNames.USER_PHYS).getTypeId();
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
}
