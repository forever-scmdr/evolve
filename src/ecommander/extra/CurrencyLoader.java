package ecommander.extra;



import org.apache.http.client.fluent.Content;
import org.apache.http.client.fluent.Request;

import ecommander.pages.elements.Command;
import ecommander.pages.elements.ResultPE;

public class CurrencyLoader extends Command {
	
	@Override
	public ResultPE execute() throws Exception {
		String url = getVarSingleValue("url");
		Content content = Request.Get(url).execute().returnContent();
		String xml = new String(content.asBytes(), "utf-8");
		xml = xml.substring(xml.indexOf('<'));
		
		return getResult("result").setValue(xml);
	}

}
