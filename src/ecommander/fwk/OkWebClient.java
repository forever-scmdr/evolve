package ecommander.fwk;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.ssl.SSLContexts;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.*;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;

/**
 * Класс для закачки файлов по урлу
 * Created by E on 2/2/2018.
 */
public class OkWebClient {
	private static final String UTF_8 = "UTF-8";
	private static final Pattern URL_ENCODED_PATTERN = Pattern.compile("%[0-9a-d]{2}");

	private static OkWebClient instance;

	private OkHttpClient client = null;
	//private HttpClientContext httpContext = null;
	private BasicCookieStore cookieStore = null;


	private void startSession() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
		/*
		SSLConnectionSocketFactory scsf = new SSLConnectionSocketFactory(
				SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build(),
				NoopHostnameVerifier.INSTANCE);

		RequestConfig requestConfig = RequestConfig.custom().setCookieSpec(CookieSpecs.STANDARD).build();
		cookieStore = new BasicCookieStore();

		// automatically follow redirects
		client = HttpClients
				.custom()
				.setRedirectStrategy(new LaxRedirectStrategy())
				.setDefaultRequestConfig(requestConfig)
				.setSSLSocketFactory(scsf)
				.setDefaultCookieStore(cookieStore)
				.build();

		 */
	}

	private OkWebClient() {
		client = new OkHttpClient.Builder().readTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).build();
	}

	public static OkWebClient getInstance() {
		if (instance == null)
			instance = new OkWebClient();
		return instance;
	}


	private void prepareHeadersAndProxies(HttpRequestBase request, String...proxy) {

//		request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:100.0) Gecko/20100101 Firefox/100.0");
//		request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8");
//		request.setHeader("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3");
//		request.setHeader("Accept-Encoding", "gzip, deflate, br");
//		request.setHeader("DNT", "1");
//		request.setHeader("Connection", "keep-alive");
//		request.setHeader("Upgrade-Insecure-Requests", "1");
//		request.setHeader("Sec-Fetch-Dest", "document");
//		request.setHeader("Sec-Fetch-Mode", "navigate");
//		request.setHeader("Sec-Fetch-Site", "none");
//		request.setHeader("Sec-Fetch-User", "?1");
//		request.setHeader("Pragma", "no-cache");
//		request.setHeader("Cache-Control", "no-cache");

		request.setHeader("accept", "text/javascript, application/javascript, application/ecmascript, application/x-ecmascript, */*; q=0.01");
		request.setHeader("accept-encoding", "gzip, deflate, br");
		request.setHeader("accept-language", "en-US,en;q=0.9,ru;q=0.8");
		request.setHeader("DNT", "1");
		request.setHeader("Connection", "keep-alive");
		request.setHeader("pragma", "no-cache");
		request.setHeader("referer", "https://www.digikey.com/en/products/filter/accessories/800");
		request.setHeader("cache-control", "no-cache");
		request.setHeader("ym_uid", "1549020452972288896;");
		request.setHeader("ga", "GA1.2.968796129.1549020453;");
		request.setHeader("sec-ch-ua", "\"Chromium\";v=\"104\", \" Not A;Brand\";v=\"99\", \"Google Chrome\";v=\"104\"");
		request.setHeader("sec-ch-ua-mobile", "?0");
		request.setHeader("sec-ch-ua-platform", "\"Windows\"");
		request.setHeader("sec-fetch-dest", "empty");
		request.setHeader("sec-fetch-mode", "cors");
		request.setHeader("sec-fetch-site", "same-origin");
		request.setHeader("user-agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36");
		request.setHeader("x-requested-with", "XMLHttpRequest");



		if (proxy.length > 0 && StringUtils.isNotBlank(proxy[0])) {
			HttpHost proxyHost = new HttpHost(proxy[0]);
			RequestConfig config = RequestConfig.custom().setProxy(proxyHost).build();
			request.setConfig(config);
		}
		if (!cookieStore.getCookies().isEmpty()) {
			StringBuilder cookies = new StringBuilder();
			for (Cookie cookie : cookieStore.getCookies()) {
				if (cookies.length() > 0)
					cookies.append("; ");
				cookies.append(cookie.getName()).append('=').append(cookie.getValue());
			}
			request.setHeader("Cookie", cookies.toString());
		}
	}


	public String getString(String url) throws IOException {

		Request request = new Request.Builder().url(url)
				.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
				//.header("Accept-Encoding", "gzip, deflate, br")
				.header("Accept-Encoding", "dentity")
				.header("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3")
				.header("Cache-Control", "no-cache")
				.header("Connection", "keep-alive")
				.header("Pragma", "no-cache")
				.header("Sec-Fetch-Dest", "document")
				.header("Sec-Fetch-Mode", "navigate")
				.header("Sec-Fetch-Site", "none")
				.header("Sec-Fetch-User", "?1")
				.header("Upgrade-Insecure-Requests", "1")
				.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:102.0) Gecko/20100101 Firefox/102.0")
				//.header("Accept-Encoding", "dentity")
				.build();

//		Request request = new Request.Builder().url(url)
//				.header("Connection", "keep-alive")
//				.header("Connection", "keep-alive")
//				.header("Accept", "text/plain,text/html,*/*")
//				.header("User-Agent", "Mozilla/5.0 (compatible; Googlebot/2.1; +http://www.google.com/bot.html)")
//				.header("Accept-Encoding", "dentity")
//				.build();
		try (Response response = client.newCall(request).execute()) {
			return response.body().string();
		}
	}


}
