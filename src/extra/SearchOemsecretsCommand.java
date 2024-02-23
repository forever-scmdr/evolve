package extra;

import ecommander.fwk.XmlDocumentBuilder;
import ecommander.pages.ResultPE;

public class SearchOemsecretsCommand extends SearchFindchipsCommand {
    @Override
    protected ResultPE getFromServer(XmlDocumentBuilder xml, CurrencyRates rates) throws Exception {
        return super.getFromServer(xml, rates);
    }
}
