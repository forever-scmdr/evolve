package ecommander.fwk;

import okhttp3.*;
import okhttp3.internal.http.RealResponseBody;
import okio.BufferedSource;
import okio.GzipSource;
import okio.Okio;
import org.apache.commons.compress.compressors.brotli.BrotliCompressorInputStream;
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
import org.brotli.dec.BrotliInputStream;
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
import java.util.Collection;
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
		OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder().addInterceptor(new UnzippingInterceptor());
		client = clientBuilder.readTimeout(30, TimeUnit.SECONDS).writeTimeout(30, TimeUnit.SECONDS).build();
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

		request.setHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8");
		request.setHeader("accept-encoding", "gzip, deflate, br");
		request.setHeader("accept-language", "en-US,en;q=0.9,ru;q=0.8");
		request.setHeader("cache-control", "no-cache");
		request.setHeader("device-memory", "8");
		request.setHeader("downlink", "5.55");
		request.setHeader("dpr", "1");
		request.setHeader("ect", "4g");
		request.setHeader("pragma", "no-cache");
		request.setHeader("rtt", "250");
		request.setHeader("sec-ch-ua", "\"Google Chrome\";v=\"105\", \"Not)A;Brand\";v=\"8\", \"Chromium\";v=\"105\"");
		request.setHeader("sec-ch-ua-mobile", "?0");
		request.setHeader("sec-ch-ua-platform", "\"Windows\"");
		request.setHeader("sec-fetch-dest", "document");
		request.setHeader("sec-fetch-mode", "navigate");
		request.setHeader("sec-fetch-site", "same-origin");
		request.setHeader("user-agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36");
		request.setHeader("upgrade-insecure-requests", "1");
		request.setHeader("viewport-width", "1280");
		request.setHeader("Connection", "keep-alive");
//		request.setHeader("referer", "https://www.digikey.com/en/products/filter/accessories/800");

		//request.setHeader("x-requested-with", "XMLHttpRequest");



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
				.addHeader("accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9")
				.addHeader("accept-encoding", "gzip, deflate, br")
				.addHeader("accept-language", "en-US,en;q=0.9,ru;q=0.8")
				.addHeader("cache-control", "no-cache")
				.addHeader("device-memory", "8")
				.addHeader("downlink", "5.55")
				.addHeader("dpr", "1")
				.addHeader("ect", "4g")
				.addHeader("pragma", "no-cache")
				.addHeader("rtt", "250")
				.addHeader("sec-ch-ua", "\"Google Chrome\";v=\"105\", \"Not)A;Brand\";v=\"8\", \"Chromium\";v=\"105\"")
				.addHeader("sec-ch-ua-mobile", "?0")
				.addHeader("sec-ch-ua-platform", "\"Windows\"")
				.addHeader("sec-fetch-dest", "document")
				.addHeader("sec-fetch-mode", "navigate")
				.addHeader("sec-fetch-site", "same-origin")
				.addHeader("user-agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/104.0.0.0 Safari/537.36")
				.addHeader("upgrade-insecure-requests", "1")
				.addHeader("viewport-width", "1280")

//				.header("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8")
				//.header("Accept-Encoding", "gzip, deflate, br")
//				.header("Accept-Encoding", "dentity")
//				.header("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3")
//				.header("Cache-Control", "no-cache")
//				.header("Connection", "keep-alive")
//				.header("Pragma", "no-cache")
//				.header("Sec-Fetch-Dest", "document")
//				.header("Sec-Fetch-Mode", "navigate")
//				.header("Sec-Fetch-Site", "none")
//				.header("Sec-Fetch-User", "?1")
//				.header("Upgrade-Insecure-Requests", "1")
//				.header("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:102.0) Gecko/20100101 Firefox/102.0")
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
//			byte[] bytes = response.body().bytes();
//			String result1 = new String(bytes, StandardCharsets.UTF_8);
//			String result2 = new String(bytes, StandardCharsets.UTF_16);
//			String result3 = new String(bytes, StandardCharsets.ISO_8859_1);
//			String result4 = new String(bytes, StandardCharsets.US_ASCII);
//			String result5 = new String(bytes, StandardCharsets.UTF_16BE);
//			String result6 = new String(bytes, StandardCharsets.UTF_16LE);
//			System.out.println(result1 + result2 + result3 + result4 + result5 + result6);
			return response.body().string();
		}
	}

	/**
	 * Получить строку ответа с использованием заголовков запроса
	 * @param url
	 * @param headers
	 * @return
	 * @throws IOException
	 */
	public String getStringHeaders(String url, String... headers) throws IOException {
		Request.Builder builder = new Request.Builder().url(url);
		for (int i = 1; i < headers.length; i += 2) {
			builder.header(headers[i - 1], headers[i]);
		}
		Request request = builder.build();
		try (Response response = client.newCall(request).execute()) {
			if (response.body() != null)
				return response.body().string();
			return null;
		}
	}

	/**
	 * Получить строку ответа POST запроса с заголовками и с параметрами
	 * @param url
	 * @param body
	 * @param headers
	 * @return
	 * @throws IOException
	 */
	public String postStringHeaders(String url, String body, String bodyType, String... headers) throws IOException {
		RequestBody reqBody = RequestBody.create(MediaType.parse(bodyType), body);
		Request.Builder builder = new Request.Builder().url(url);
		for (int i = 1; i < headers.length; i += 2) {
			builder.header(headers[i - 1], headers[i]);
		}
		Request request = builder.post(reqBody).build();
		try (Response response = client.newCall(request).execute()) {
			if (response.body() != null)
				return response.body().string();
			return null;
		}
	}


	private static class UnzippingInterceptor implements Interceptor {
		@Override
		public Response intercept(Chain chain) throws IOException {
			Response response = chain.proceed(chain.request());
			return unzip(response);
		}


		// copied from okhttp3.internal.http.HttpEngine (because is private)
		private Response unzip(final Response response) throws IOException {
			if (response.body() == null)
			{
				return response;
			}

			//check if we have gzip response
			String contentEncoding = response.headers().get("Content-Encoding");
			if (StringUtils.isBlank(contentEncoding))
				contentEncoding = response.headers().get("Content-Type");

			//this is used to decompress gzipped responses
			if (contentEncoding != null && StringUtils.containsIgnoreCase(contentEncoding,"gzip")) {
				Long contentLength = response.body().contentLength();
				GzipSource responseBody = new GzipSource(response.body().source());
				Headers strippedHeaders = response.headers().newBuilder().build();
				return response.newBuilder().headers(strippedHeaders)
						.body(new RealResponseBody(response.body().contentType().toString(), contentLength, Okio.buffer(responseBody)))
						.build();
			}
			else if (contentEncoding != null && StringUtils.equalsIgnoreCase(contentEncoding, "br")) {
				Long contentLength = response.body().contentLength();
				//GzipSource responseBody = new GzipSource(response.body().source());
				Headers strippedHeaders = response.headers().newBuilder().build();
				BufferedSource src = Okio.buffer(Okio.source(new BrotliInputStream(response.body().source().inputStream())));
				return response.newBuilder().headers(strippedHeaders)
						.body(new RealResponseBody(response.body().contentType().toString(), contentLength, src))
						.build();
			}
			else
			{
				return response;
			}
		}
	}


}
