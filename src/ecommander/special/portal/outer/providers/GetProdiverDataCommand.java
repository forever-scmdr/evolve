package ecommander.special.portal.outer.providers;

import ecommander.fwk.XmlDocumentBuilder;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;

public class GetProdiverDataCommand extends Command implements ItemNames {
    private static final String LOCAL_PARAM = "local";
    private static final String NEW_PARAM = "new";


    protected UserInput inp; // данные, полученные от пользователя и другие внешние данные

    @Override
    public ResultPE execute() throws Exception {
        inp = new UserInput(this);
        if (inp.getErrorResult() != null) {
            return inp.getErrorResult();
        }

        // Надо ли производить локальный поиск (поиск по локальной собственной базе)
        boolean localSearch = StringUtils.equalsAnyIgnoreCase(getVarSingleValue(LOCAL_PARAM), "yes", "true");

        // Надо ли делать новый запрос или взять кеш
        boolean forceRefreshCache = StringUtils.equalsAnyIgnoreCase(getVarSingleValueDefault(NEW_PARAM, "true"), "yes", "true");

        inp.getRates(); // загрузка курсов валют

        XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();
        xml.startElement("result");
        for (String query : inp.getQueries().keySet()) {
            xml.addElement("query", query, "qty", inp.getQueries().get(query));
            QueryDataGetter queryGetter = new QueryDataGetter(query, inp, localSearch, forceRefreshCache);
            ProviderGetter.Result result = queryGetter.appendQueryData(xml);
            if (!result.isSuccess()) {
                xml.addElement("error", result.getErrorMessage());
            }
        }

        xml.addElement("max_price", inp.getGlobalMaxPrice());
        xml.addElement("min_price", inp.getGlobalMinPrice());

        xml.endElement(); // result
        return getResult("product_list").setValue(xml.toString());
    }

}
