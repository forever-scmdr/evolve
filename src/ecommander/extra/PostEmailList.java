package ecommander.extra;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;

import ecommander.extra._generated.ItemNames;
import ecommander.model.item.Item;
import ecommander.pages.elements.Command;
import ecommander.pages.elements.ResultPE;
import ecommander.persistence.itemquery.ItemQuery;

public class PostEmailList extends Command {

	@Override
	public ResultPE execute() throws Exception {
		String url = getVarSingleValue("url");
		List<Item> allAgents = ItemQuery.newItemQuery(ItemNames.AGENT).loadItems();
		HttpClient client = HttpClients.createDefault();
		HttpPost post = new HttpPost(url);
		List<NameValuePair> params = new ArrayList<NameValuePair>(allAgents.size());
		for (Item agent : allAgents) {
			params.add(new BasicNameValuePair("email", agent.getStringValue(ItemNames.agent.EMAIL)));
		}
		post.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
		HttpResponse response = client.execute(post);
		HttpEntity entity = response.getEntity();
		if (entity != null) {
		    InputStream instream = entity.getContent();
		    try {
		        // do something useful
		    } finally {
		        instream.close();
		    }
		}
		return null;
	}

}
