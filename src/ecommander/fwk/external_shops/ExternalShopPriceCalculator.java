package ecommander.fwk.external_shops;

import ecommander.model.Item;
import ecommander.model.datatypes.DecimalDataType;
import org.apache.commons.lang3.StringUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class ExternalShopPriceCalculator {
	public static BigDecimal convertToByn(String price, Item currency, Item catalog){
		if (StringUtils.isBlank(price)) return BigDecimal.ZERO;
		BigDecimal originalPrice = DecimalDataType.parse(price, DecimalDataType.CURRENCY_PRECISE);
		return convertToByn(originalPrice, currency, catalog);
	}
	public static BigDecimal convertToByn(BigDecimal price, Item currency, Item catalog){
		if (price.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
		BigDecimal scale = new BigDecimal(currency.getIntValue("scale", 1));
		BigDecimal currencyQ = new BigDecimal(1 + currency.getDoubleValue("q", 0));
		BigDecimal currencyRatio = new BigDecimal(currency.getDoubleValue("ratio")).multiply(currencyQ);

		BigDecimal c1 = catalog.getDecimalValue("c1");
		BigDecimal c2 = catalog.getDecimalValue("c2");
		BigDecimal c3 = catalog.getDecimalValue("c3");
		BigDecimal c4 = catalog.getDecimalValue("c4");

		BigDecimal bynPrice = price.divide(c1, RoundingMode.HALF_EVEN).multiply(currencyRatio).multiply(c2).multiply(c3).multiply(c4).divide(scale);
		bynPrice.setScale(DecimalDataType.CURRENCY, RoundingMode.HALF_EVEN);
		return bynPrice;
	}
}
