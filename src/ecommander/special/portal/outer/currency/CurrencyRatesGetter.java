package ecommander.special.portal.outer.currency;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class CurrencyRatesGetter {
    public abstract String getBankRatesRemote() throws Exception;
    public abstract String getBaseCurrencyCode();
    public abstract HashMap<String, BigDecimal> getRates(String xml);
}
