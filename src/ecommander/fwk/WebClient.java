package ecommander.fwk;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.fluent.Request;
import org.w3c.dom.Document;

import java.io.IOException;

/**
 * Класс для закачки файлов по урлу
 * Created by E on 2/2/2018.
 */
public class WebClient {
	public static String getString(String url, String encoding) {
		Request.Get(url)
				.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)")
				.connectTimeout(1000).socketTimeout(3000)
				.execute()
				.handleResponse(new ResponseHandler<Document>() {
					@Override
					public Document handleResponse(HttpResponse httpResponse) throws ClientProtocolException, IOException {
						return null;
					}
				});
	}
}
