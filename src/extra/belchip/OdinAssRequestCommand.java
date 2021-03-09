package extra.belchip;

import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import org.apache.commons.io.IOUtils;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.protocol.HttpClientContext;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;

import java.net.URLEncoder;
import java.nio.charset.Charset;

public class OdinAssRequestCommand extends Command {
	
	private static final String URL = "https://office.belchip.by/ut/hs/OrderStatus/GetStatus?purchase=%s&mail=%s";
	private static final String CREDENTIAL = "Services1c:KpPD6VMv97T4SHqs";
	private static final String ENCODING = "UTF-8";
	
	@Override
	public ResultPE execute() throws Exception {
		try(CloseableHttpClient httpclient = HttpClients.createDefault()){
			
			CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
			credentialsProvider.setCredentials(AuthScope.ANY, new UsernamePasswordCredentials(CREDENTIAL));
			HttpClientContext localContext = HttpClientContext.create();
			localContext.setCredentialsProvider(credentialsProvider);
		
			String orderNumber = URLEncoder.encode(getVarSingleValue("order_number"), ENCODING);
			String mail = URLEncoder.encode(getVarSingleValue("email"), ENCODING);
			
			HttpGet httpget = new HttpGet(String.format(URL, orderNumber, mail));
			try(CloseableHttpResponse response = httpclient.execute(httpget, localContext)){
				int status = response.getStatusLine().getStatusCode();
				if (status >= 200 && status < 300) {
					if (response.getEntity() == null)
						return null;
					ContentType contentType = ContentType.getOrDefault(response.getEntity());
					Charset charset = contentType.getCharset();
					if (charset == null) {
						charset = Charset.forName(ENCODING);
					}
					String content =  IOUtils.toString(response.getEntity().getContent(), charset);
					ResultPE res = getResult("complete");
					res.setValue(content);
					return res;
				} else {
					throw new HttpResponseException(status, response.getStatusLine().getReasonPhrase());
				}
			}
		}
	}

}
