package ecommander.fwk;

import okhttp3.*;
import okhttp3.internal.http.RealResponseBody;
import okio.BufferedSource;
import okio.GzipSource;
import okio.Okio;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.brotli.dec.BrotliInputStream;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.util.concurrent.TimeUnit;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

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
		//OkHttpClient.Builder clientBuilder = new OkHttpClient.Builder().addInterceptor(new GzipInterceptor());
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


	private Request buildRequest(String url, String... headers) {
		Request.Builder builder = new Request.Builder().url(url)
				.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:128.0) Gecko/20100101 Firefox/128.0")
				.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/png,image/svg+xml,*/*;q=0.8")
				.addHeader("Accept-Language", "en-US,en;q=0.8,en-US;q=0.5,en;q=0.3")
				.addHeader("Accept-Encoding", "gzip, deflate, br, zstd")
				.addHeader("Connection", "keep-alive")
				.addHeader("Upgrade-Insecure-Requests", "1")
				.addHeader("Sec-Fetch-Dest", "document")
				.addHeader("Sec-Fetch-Mode", "navigate")
				.addHeader("Sec-Fetch-Site", "none")
				.addHeader("Sec-Fetch-User", "?1");
		for (int i = 0; i < headers.length - 1; i += 2) {
			String name = headers[i];
			String value = headers[i + 1];
			builder.removeHeader(name);
			builder.addHeader(name, value);
		}
		return builder.build();
	}

	public byte[] getBytes(String url, String... headers) throws IOException {
		Request request = buildRequest(url, headers);
		try (Response response = client.newCall(request).execute()) {
			return response.body().bytes();
		}
	}

	public String getString(String url, String... headers) throws IOException {
		Request request = buildRequest(url, headers);
		try (Response response = client.newCall(request).execute()) {
			ServerLogger.debug("\nOK client: " + url + "\nheaders: " + headers);
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
			if (response.body() != null) {
				return response.body().string();
			}
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
		byte[] result = postBytesHeaders(url, body, bodyType, headers);
		if (result != null) {
			return new String(result);
		}
		return null;
	}

	/**
	 * Отправить POST запрос с заголовками
	 * @param url
	 * @param body
	 * @param bodyType
	 * @param headers
	 * @return
	 * @throws IOException
	 */
	public byte[] postBytesHeaders(String url, String body, String bodyType, String... headers) throws IOException {
		RequestBody reqBody = RequestBody.create(body, MediaType.parse(bodyType));
		Request.Builder builder = new Request.Builder().url(url);
		for (int i = 1; i < headers.length; i += 2) {
			builder.header(headers[i - 1], headers[i]);
		}
		Request request = builder.post(reqBody).build();
		try (Response response = client.newCall(request).execute()) {
			if (response.body() != null)
				return response.body().bytes();
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
			if (response.body() == null) {
				return response;
			}
			// Отдельно для сайта digikey.com
			// На нем есть ошибка - неверно указан размер ответа, поэтому надо делать отдельную логическую ветку
			boolean isDigikey = StringUtils.contains(response.request().url().toString(), "digikey.");

		//check if we have gzip response
			String contentEncoding = response.headers().get("Content-Encoding");
			if (StringUtils.isBlank(contentEncoding))
				contentEncoding = response.headers().get("Content-Type");

			//this is used to decompress gzipped responses
			if (contentEncoding != null && StringUtils.containsIgnoreCase(contentEncoding,"gzip")) {
				// Если digikey - у них ошибка в длине ответа
				if (isDigikey) {
					byte[] bytesIn = response.body().bytes();
					InputStream is = new GzipCompressorInputStream(new ByteArrayInputStream(bytesIn));
					ByteArrayOutputStream bufferOut = new ByteArrayOutputStream();
					int nRead;
					byte[] data = new byte[4];
					try {
						while ((nRead = is.read(data, 0, data.length)) != -1) {
							bufferOut.write(data, 0, nRead);
						}
					} catch (Exception e) {
						ServerLogger.error("eeee", e);
					}
					bufferOut.flush();
					byte[] targetArray = bufferOut.toByteArray();
					ResponseBody newBody = ResponseBody.create(targetArray, response.body().contentType());
					return response.newBuilder().body(newBody).build();
				}
				// Так работает, если правильно указана длина contentLength. Если не правильно - ошибка
				else {
					long contentLength = response.body().contentLength();
					Headers strippedHeaders = response.headers().newBuilder().build();
					GzipSource responseBody = new GzipSource(response.body().source());
					return response.newBuilder().headers(strippedHeaders)
							.body(new RealResponseBody(response.body().contentType().toString(), contentLength, Okio.buffer(responseBody)))
							.build();
				}
			}
			else if (contentEncoding != null && StringUtils.equalsIgnoreCase(contentEncoding, "br")) {
				long contentLength = response.body().contentLength();
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

	/**
	 * Второй интерсептор для распаковки
	 */
	private static class GzipInterceptor implements Interceptor {
		@Override
		public Response intercept(Chain chain) throws IOException {
			Request.Builder newRequest = chain.request().newBuilder();
			newRequest.addHeader("Accept-Encoding", "gzip");
			Response response = chain.proceed(newRequest.build());

			if (isGzipped(response)) {
				return unzip(response);
			} else {
				return response;
			}
		}

		private Response unzip(final Response response) throws IOException {

			if (response.body() == null) {
				return response;
			}

			GzipSource gzipSource = new GzipSource(response.body().source());
			String bodyString = Okio.buffer(gzipSource).readUtf8();

			ResponseBody responseBody = ResponseBody.create(response.body().contentType(), bodyString);

			Headers strippedHeaders = response.headers().newBuilder()
					.removeAll("Content-Encoding")
					.removeAll("Content-Length")
					.build();
			return response.newBuilder()
					.headers(strippedHeaders)
					.body(responseBody)
					.message(response.message())
					.build();

		}

		private Boolean isGzipped(Response response) {
			return response.header("Content-Encoding") != null && response.header("Content-Encoding").equals("gzip");
		}
	}


}
