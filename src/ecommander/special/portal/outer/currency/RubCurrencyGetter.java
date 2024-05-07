package ecommander.special.portal.outer.currency;

import ecommander.fwk.JsoupUtils;
import ecommander.fwk.OkWebClient;
import ecommander.model.datatypes.DecimalDataType;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.math.BigDecimal;
import java.util.HashMap;

public class RubCurrencyGetter extends CurrencyRatesGetter{
    public static final String RUB = "RUB";
    public static final String CENTROBANK_URL = "https://www.cbr-xml-daily.ru/daily_utf8.xml";

    @Override
    public String getBankRatesRemote() throws Exception {
        return OkWebClient.getInstance().getString(CENTROBANK_URL);
    }

    @Override
    public String getBaseCurrencyCode() {
        return RUB;
    }

    @Override
    public HashMap<String, BigDecimal> getRates(String xml) {
        HashMap<String, BigDecimal> rates = new HashMap<>();
        if (StringUtils.isBlank(xml))
            return rates;
        Document doc = JsoupUtils.parseXml(xml);
        for (Element valute : doc.select("Valute")) {
            String code = JsoupUtils.getTagFirstValue(valute, "CharCode");
            String totalRateStr = JsoupUtils.getTagFirstValue(valute, "VunitRate");
            BigDecimal totalRate = DecimalDataType.parse(totalRateStr, 4);
            rates.put(code, totalRate);
        }
        return rates;
    }
}
