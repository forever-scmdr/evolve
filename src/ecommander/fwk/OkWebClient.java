package ecommander.fwk;

import okhttp3.*;
import okhttp3.internal.http.RealResponseBody;
import okio.BufferedSource;
import okio.GzipSource;
import okio.Okio;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.cookie.Cookie;
import org.apache.http.impl.client.BasicCookieStore;
import org.brotli.dec.BrotliInputStream;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
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


	public byte[] getBytes(String url) throws IOException {
		Request request = new Request.Builder().url(url)
				.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:128.0) Gecko/20100101 Firefox/128.0")
				.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/png,image/svg+xml,*/*;q=0.8")
				.addHeader("Accept-Language", "en-US,en;q=0.8,en-US;q=0.5,en;q=0.3")
				.addHeader("Accept-Encoding", "gzip, deflate, br, zstd")
				.addHeader("Connection", "keep-alive")
				.addHeader("Cookie", "_pxhd=e493f6917b36b18625839234fe1557c7fd5cc6e2c8c06577b252226e03313f5b:f7db3f6f-612c-11ee-a749-89aabfc42dfd; search=%7B%22id%22%3A%22a09e64b7-c2a2-496e-b9e9-2b6f00125a36%22%2C%22usage%22%3A%7B%22dailyCount%22%3A4%2C%22lastRequest%22%3A%222024-07-16T15%3A54%3A53.201Z%22%7D%2C%22version%22%3A1.1%7D; ai_user=6DfIbztGcZl84THWizZ7Yz|2024-07-09T11:45:53.390Z; pf-accept-language=en-US; ping-accept-language=en-US; utag_main=v_id:01909750839b001834a766128be605050004e00d00c48$_sn:4$_se:6$_ss:0$_st:1721150201108$ses_id:1721145295139%3Bexp-session$_pn:6%3Bexp-session; _pxvid=f7db3f6f-612c-11ee-a749-89aabfc42dfd; TS01173021=01f9ef228d20770a0fc379bdb80b2194baa06c22d00b34c8d5c1d6b3497df78fab39231344bdd1937b319105a3e314f35108186f62; TScaafd3c3027=08205709cbab2000444efa0242365d4e90b8b01863ffbe6e01f04cdfbcf9291510e846bfde9ea32f08cf00fd60113000a012c670f152396577bc6604d9d598ae0b4997a396582928f3f27c9df73a013ec2d0d0f189298c7ed865aa2f546443c6; dkc_tracker=3652960826380; search_prefs=%7B%22theme%22%3A%22light%22%7D; pxcts=55a10177-42ad-11ef-9a3c-49aa6398a784; TSd6475659027=08e0005159ab2000df30e4fa1366b6cf6d771bd414ee41391763af24aeeb17e6b24e21459115545208ad4d7ecc113000a23313484b4ac634ac24b38b1d6be5e561f97d33ffd1d5b857d8b80ed9bc0d5b6854781ced572573f07db59020653d25; ai_session=UVkEFF5SjYYayGllLHWAuf|1721145293746|1721145293746; website#lang=en-US; TS0198bc0d=01c72bed21994c9ea667a2a96dd46df70c6147d739fd37c70969aa471c3dc2c76a851365a10ab619226289b799da12422e8cd5d871; TSe14c7dc7027=08a1509f8aab2000932e60061c30320a68a109f21cf7780b975fbef9a68bc8a78ce782ec78d6264008bdc407c6113000a8054e7de7ee880f0d29bb3e61dfe98c7693bb07e417ecd2f9562946d996ae5f1a38d63e141afd10216c97044ae95360; TS016ca11c=01c72bed2137738fd13c6a515723c584a5676aa06e41f32b931e9b860cfabc2317d9b31d228c156a50b07f04a75a2adc525038016a; TSc580adf4027=08a1509f8aab2000b7fa5ec40b26e78d7750d114f2b530d9b27514cd188af8710ef8dede201d9c1c0886aa7cba113000db6710f939aafe899b6b798df563b4c8dafaf81952b53df0ceed1301cf6d2bf05bde0f52744b97bed56fecd7e89b6661; TS019f5e6c=01c72bed21263d9aaf45c06f618a1a21dadb05dd939a87e5847f00777ec79afd9f2ca0ead9c8863a83197388712b923d4538fe6242; TSbafe380b027=08a1509f8aab20007d4390be5a0d8c607120ac506b3fe851b923a3491534dc86c5da6ce95acb7c9f08e54a5db2113000ceb6567e7f8fa3999b6b798df563b4c8cbd04f85fae225d55dfa130237b54aff328157f5161c6596efdd7ca7b49e1fa7; _cs_mk=0.37244327748550354_1721147244005; _dd_s=rum=0&expire=1721149510140; _pxde=9d0f57c3e90aa962d9c606f22e0e8f7774b45b81e5186c2ef88a506d9a767f72:eyJ0aW1lc3RhbXAiOjE3MjExNDg0NjI3MjAsImZfa2IiOjAsImlwY19pZCI6WzE3XSwiaW5jX2lkIjpbImIzN2M0YjlkNzQzNmM1YjIyMmM5N2Q1M2Y2MjNlNWNjIl19; _px2=eyJ1IjoiZjlkM2FjYTAtNDM5Mi0xMWVmLWEyNjQtOGY5MzE5OWNjOTg0IiwidiI6ImY3ZGIzZjZmLTYxMmMtMTFlZS1hNzQ5LTg5YWFiZmM0MmRmZCIsInQiOjE3MjExNDg3MDI0MTIsImgiOiJlY2VmZTA2ZmU3YWZmZjIxODM3ODU5YWEwN2JjMGU0MzIyNWRhYmYzYjE5ZTViYzcyOTJhMWUzNDA0YjIyZTA5In0=")
				.addHeader("Upgrade-Insecure-Requests", "1")
				.addHeader("Sec-Fetch-Dest", "document")
				.addHeader("Sec-Fetch-Mode", "navigate")
				.addHeader("Sec-Fetch-Site", "none")
				.addHeader("Sec-Fetch-User", "?1")
				.build();
		try (Response response = client.newCall(request).execute()) {
//			byte[] bytes = response.body().bytes();
//			String result1 = new String(bytes, StandardCharsets.UTF_8);
//			String result2 = new String(bytes, StandardCharsets.UTF_16);
//			String result3 = new String(bytes, StandardCharsets.ISO_8859_1);
//			String result4 = new String(bytes, StandardCharsets.US_ASCII);
//			String result5 = new String(bytes, StandardCharsets.UTF_16BE);
//			String result6 = new String(bytes, StandardCharsets.UTF_16LE);
//			System.out.println(result1 + result2 + result3 + result4 + result5 + result6);
			return response.body().bytes();
		}
	}

	public String getString(String url) throws IOException {
		Request request = new Request.Builder().url(url)
				.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:128.0) Gecko/20100101 Firefox/128.0")
				.addHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/png,image/svg+xml,*/*;q=0.8")
				.addHeader("Accept-Language", "en-US,en;q=0.8,en-US;q=0.5,en;q=0.3")
				.addHeader("Accept-Encoding", "gzip, deflate, br, zstd")
				.addHeader("Connection", "keep-alive")
				.addHeader("Cookie", "_pxhd=e493f6917b36b18625839234fe1557c7fd5cc6e2c8c06577b252226e03313f5b:f7db3f6f-612c-11ee-a749-89aabfc42dfd; search=%7B%22id%22%3A%22a09e64b7-c2a2-496e-b9e9-2b6f00125a36%22%2C%22usage%22%3A%7B%22dailyCount%22%3A4%2C%22lastRequest%22%3A%222024-07-16T15%3A54%3A53.201Z%22%7D%2C%22version%22%3A1.1%7D; ai_user=6DfIbztGcZl84THWizZ7Yz|2024-07-09T11:45:53.390Z; pf-accept-language=en-US; ping-accept-language=en-US; utag_main=v_id:01909750839b001834a766128be605050004e00d00c48$_sn:4$_se:6$_ss:0$_st:1721150201108$ses_id:1721145295139%3Bexp-session$_pn:6%3Bexp-session; _pxvid=f7db3f6f-612c-11ee-a749-89aabfc42dfd; TS01173021=01f9ef228d20770a0fc379bdb80b2194baa06c22d00b34c8d5c1d6b3497df78fab39231344bdd1937b319105a3e314f35108186f62; TScaafd3c3027=08205709cbab2000444efa0242365d4e90b8b01863ffbe6e01f04cdfbcf9291510e846bfde9ea32f08cf00fd60113000a012c670f152396577bc6604d9d598ae0b4997a396582928f3f27c9df73a013ec2d0d0f189298c7ed865aa2f546443c6; dkc_tracker=3652960826380; search_prefs=%7B%22theme%22%3A%22light%22%7D; pxcts=55a10177-42ad-11ef-9a3c-49aa6398a784; TSd6475659027=08e0005159ab2000df30e4fa1366b6cf6d771bd414ee41391763af24aeeb17e6b24e21459115545208ad4d7ecc113000a23313484b4ac634ac24b38b1d6be5e561f97d33ffd1d5b857d8b80ed9bc0d5b6854781ced572573f07db59020653d25; ai_session=UVkEFF5SjYYayGllLHWAuf|1721145293746|1721145293746; website#lang=en-US; TS0198bc0d=01c72bed21994c9ea667a2a96dd46df70c6147d739fd37c70969aa471c3dc2c76a851365a10ab619226289b799da12422e8cd5d871; TSe14c7dc7027=08a1509f8aab2000932e60061c30320a68a109f21cf7780b975fbef9a68bc8a78ce782ec78d6264008bdc407c6113000a8054e7de7ee880f0d29bb3e61dfe98c7693bb07e417ecd2f9562946d996ae5f1a38d63e141afd10216c97044ae95360; TS016ca11c=01c72bed2137738fd13c6a515723c584a5676aa06e41f32b931e9b860cfabc2317d9b31d228c156a50b07f04a75a2adc525038016a; TSc580adf4027=08a1509f8aab2000b7fa5ec40b26e78d7750d114f2b530d9b27514cd188af8710ef8dede201d9c1c0886aa7cba113000db6710f939aafe899b6b798df563b4c8dafaf81952b53df0ceed1301cf6d2bf05bde0f52744b97bed56fecd7e89b6661; TS019f5e6c=01c72bed21263d9aaf45c06f618a1a21dadb05dd939a87e5847f00777ec79afd9f2ca0ead9c8863a83197388712b923d4538fe6242; TSbafe380b027=08a1509f8aab20007d4390be5a0d8c607120ac506b3fe851b923a3491534dc86c5da6ce95acb7c9f08e54a5db2113000ceb6567e7f8fa3999b6b798df563b4c8cbd04f85fae225d55dfa130237b54aff328157f5161c6596efdd7ca7b49e1fa7; _cs_mk=0.37244327748550354_1721147244005; _dd_s=rum=0&expire=1721149510140; _pxde=9d0f57c3e90aa962d9c606f22e0e8f7774b45b81e5186c2ef88a506d9a767f72:eyJ0aW1lc3RhbXAiOjE3MjExNDg0NjI3MjAsImZfa2IiOjAsImlwY19pZCI6WzE3XSwiaW5jX2lkIjpbImIzN2M0YjlkNzQzNmM1YjIyMmM5N2Q1M2Y2MjNlNWNjIl19; _px2=eyJ1IjoiZjlkM2FjYTAtNDM5Mi0xMWVmLWEyNjQtOGY5MzE5OWNjOTg0IiwidiI6ImY3ZGIzZjZmLTYxMmMtMTFlZS1hNzQ5LTg5YWFiZmM0MmRmZCIsInQiOjE3MjExNDg3MDI0MTIsImgiOiJlY2VmZTA2ZmU3YWZmZjIxODM3ODU5YWEwN2JjMGU0MzIyNWRhYmYzYjE5ZTViYzcyOTJhMWUzNDA0YjIyZTA5In0=")
				.addHeader("Upgrade-Insecure-Requests", "1")
				.addHeader("Sec-Fetch-Dest", "document")
				.addHeader("Sec-Fetch-Mode", "navigate")
				.addHeader("Sec-Fetch-Site", "none")
				.addHeader("Sec-Fetch-User", "?1")
				.build();
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
