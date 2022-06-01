package ecommander.fwk;

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
import java.util.regex.Pattern;

/**
 * Класс для закачки файлов по урлу
 * Created by E on 2/2/2018.
 */
public class WebClient implements Closeable {
	private static final String UTF_8 = "UTF-8";
	private static final Pattern URL_ENCODED_PATTERN = Pattern.compile("%[0-9a-d]{2}");

	private CloseableHttpClient client = null;
	//private HttpClientContext httpContext = null;
	private BasicCookieStore cookieStore = null;


	private void startSession() throws KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
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
	}

	private WebClient() {

	}

	private static WebClient newClient() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		WebClient client = new WebClient();
		client.startSession();
		return client;
	}


	private void prepareHeadersAndProxies(HttpRequestBase request, String...proxy) {
		request.setHeader("User-Agent", "Mozilla/5.0 (Windows NT 6.1; Win64; x64; rv:100.0) Gecko/20100101 Firefox/100.0");
		request.setHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,*/*;q=0.8");
		request.setHeader("Accept-Language", "ru-RU,ru;q=0.8,en-US;q=0.5,en;q=0.3");
		request.setHeader("Accept-Encoding", "gzip, deflate, br");
		request.setHeader("DNT", "1");
		request.setHeader("Connection", "keep-alive");
		request.setHeader("Upgrade-Insecure-Requests", "1");
		request.setHeader("Sec-Fetch-Dest", "document");
		request.setHeader("Sec-Fetch-Mode", "navigate");
		request.setHeader("Sec-Fetch-Site", "none");
		request.setHeader("Sec-Fetch-User", "?1");
		request.setHeader("Pragma", "no-cache");
		request.setHeader("Cache-Control", "no-cache");

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


	public static WebClient startClient() throws NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		return newClient();
	}

	private static String getString(String url, StringBuilder encName, String...proxy)
			throws IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
		try (WebClient client = newClient()) {
			return client.getStringWithClient(url, encName, proxy);
		}
	}


	private String getStringWithClient(String url, StringBuilder encName, String...proxy) throws IOException {
		HttpGet get = new HttpGet(url);
		prepareHeadersAndProxies(get, proxy);
		try (CloseableHttpResponse response = client.execute(get)) {
			int status = response.getStatusLine().getStatusCode();
			if (status >= 200 && status < 300) {
				return processSuccessfulResponse(response, encName);
			} else {
				throw new HttpResponseException(status, response.getStatusLine().getReasonPhrase());
			}
		}
	}


	private String processSuccessfulResponse(CloseableHttpResponse response, StringBuilder encName) throws IOException {
		if (response.getEntity() == null)
			return null;
		ContentType contentType = ContentType.getOrDefault(response.getEntity());
		Charset charset = contentType.getCharset();
		if (charset == null) {
			charset = Charset.forName(encName.toString());
		} else {
			encName.replace(0, encName.length(), charset.name());
		}
		return IOUtils.toString(response.getEntity().getContent(), charset);
	}

	/**
	 * Запрос POST с параметрами
	 * @param url
	 * @param encName
	 * @param postParams
	 * @return
	 * @throws IOException
	 */
	public String postStringSession(String url, StringBuilder encName, String... postParams) throws IOException {
		HttpPost post = new HttpPost(url);
		prepareHeadersAndProxies(post);
		if (postParams.length > 0) {
			ArrayList<NameValuePair> paramValues = new ArrayList<>();
			for (int i = 0; i < postParams.length - 1; i += 2)
				paramValues.add(new BasicNameValuePair(postParams[i], postParams[i + 1]));
			post.setEntity(new UrlEncodedFormEntity(paramValues, StandardCharsets.UTF_8));
		}
		try (CloseableHttpResponse response = client.execute(post)) {
			int status = response.getStatusLine().getStatusCode();
			if (status >= 200 && status < 300) {
				return processSuccessfulResponse(response, encName);
			} else if (status == 302) {
				return null;
			} else {
				throw new HttpResponseException(status, response.getStatusLine().getReasonPhrase());
			}
		}
	}

	/**
	 * Запрос POST с параметрами
	 * @param url
	 * @param postParams
	 * @return
	 * @throws IOException
	 */
	public String postStringSession(String url, String... postParams) throws IOException {
		return postStringSession(url, new StringBuilder(UTF_8), postParams);
	}

	/**
	 * Получить строку по урлу без сохранения сеанса
	 * @param url
	 * @param proxy
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws KeyManagementException
	 */
	public static String getString(String url, String...proxy)
			throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		return getString(url, new StringBuilder(UTF_8), proxy);
	}

	/**
	 * Получить строку по урлу с сохранением сеанса
	 * Создавать клиент и закрывать его надо отдельно
	 * @param url
	 * @param proxy
	 * @return
	 * @throws IOException
	 */
	public String getStringSession(String url, String...proxy) throws IOException {
		return getStringWithClient(url, new StringBuilder(UTF_8), proxy);
	}

	/**
	 * Получить строку по урлу с сохранением сеанса
	 * Создавать клиент и закрывать его надо отдельно
	 * @param url
	 * @param encName
	 * @param proxy
	 * @return
	 * @throws IOException
	 */
	private String getStringSession(String url, StringBuilder encName, String...proxy) throws IOException {
		return getStringWithClient(url, encName, proxy);
	}

	/**
	 * Получить строку по урлу без сохранения сеанса, но с очищенным HTML кодом
	 * @param url
	 * @param proxy
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws KeyManagementException
	 */
	public static String getCleanHtml(String url, String...proxy)
			throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		try (WebClient client = newClient()) {
			return client.getCleanHtmlSession(url, proxy);
		}
	}


	/**
	 * Получить строку по урлу с сохранением сеанса, но с очищенным HTML кодом
	 * @param url
	 * @param proxy
	 * @return
	 * @throws IOException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyStoreException
	 * @throws KeyManagementException
	 */
	public String getCleanHtmlSession(String url, String...proxy)
			throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		StringBuilder charsetName = new StringBuilder(UTF_8);
		String badHtml = getStringSession(url, charsetName, proxy);
		String result = Strings.cleanHtml(badHtml);
		// Проверка, правильная ли была использована кодировка для сохранения ответа
		Document jsoupDoc = Jsoup.parse(result);
		String contentTypeStr = jsoupDoc.select("meta[http-equiv=Content-Type]").attr("content");
		String[] parts = StringUtils.split(contentTypeStr, ";");
		String realCharsetName = UTF_8;
		for (String part : parts) {
			String[] nameVal = StringUtils.split(part, "=");
			if (nameVal.length > 1 && StringUtils.equalsIgnoreCase(StringUtils.trim(nameVal[0]), "charset")) {
				realCharsetName = StringUtils.trim(nameVal[1]);
			}
		}
		if (!StringUtils.equalsIgnoreCase(realCharsetName, charsetName.toString())) {
			result = getStringSession(url, new StringBuilder(realCharsetName), proxy);
			result = Strings.cleanHtml(result);
		}
		return result;
	}

	/**
	 * Сохранить файл без сохранения сеанса с сервером источником файла
	 * @param url
	 * @param dirName
	 * @param saveAs
	 * @param proxy
	 * @throws IOException
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public static void saveFile(String url, String dirName, String saveAs, String...proxy)
			throws IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
		try (WebClient client = WebClient.newClient()) {
			client.saveFileSession(url, dirName, saveAs, null, proxy);
		}
	}

	/**
	 * Сохранить файл без сохранения сеанса с сервером источником файла
	 * @param url
	 * @param dirName
	 * @param saveAs
	 * @param proxy
	 * @throws IOException
	 * @throws KeyStoreException
	 * @throws NoSuchAlgorithmException
	 * @throws KeyManagementException
	 */
	public static void saveFileQuick(String url, String dirName, String saveAs, String...proxy)
			throws IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
		try (WebClient client = WebClient.newClient()) {
			client.saveFileWithClient(url, dirName, saveAs, null, proxy);
		}
	}

	/**
	 * Сохранить файл с сохранением сеанса с сервером источником файла
	 * @param url
	 * @param dirName
	 * @param saveAs
	 * @param proxy
	 * @throws IOException
	 */
	public void saveFileSession(String url, String dirName, String saveAs, Charset encoding, String...proxy) throws IOException {
		String originalUrl = url;
		String badPart = StringUtils.substringAfterLast(url,"/");
		boolean urlChanged = false;
		if (StringUtils.isNotBlank(badPart) && !URL_ENCODED_PATTERN.matcher(badPart).find()) {
			url = url.replace(badPart, URLEncoder.encode(badPart, "UTF-8").replaceAll("\\+", "%20"));
			urlChanged = true;
		}
		try {
			saveFileWithClient(url, dirName, saveAs, encoding, proxy);
		} catch (Exception e) {
			if (urlChanged)
				saveFileWithClient(originalUrl, dirName, saveAs, encoding, proxy);
			else throw e;
		}
	}

	private void saveFileWithClient(String url, String dirName, String saveAs, Charset encoding, String...proxy) throws IOException {
		HttpGet get = new HttpGet(url);
		prepareHeadersAndProxies(get, proxy);
		try (CloseableHttpResponse response = client.execute(get)) {
			int status = response.getStatusLine().getStatusCode();
			if (status >= 200 && status < 300) {
				if (response.getEntity() == null)
					return;
				String fileDirName = dirName;
				if (!StringUtils.endsWith(fileDirName, "/"))
					fileDirName += "/";
				URL urlUrl = new URL(url);
				String newFileName = saveAs;
				if (StringUtils.isBlank(newFileName))
					newFileName = Strings.getFileName(urlUrl.getPath());
				File file = new File(fileDirName + newFileName);
				if (encoding != null) {
					try (FileOutputStream fos = new FileOutputStream(file);
					     InputStreamReader isr = new InputStreamReader(response.getEntity().getContent(), encoding)) {
						IOUtils.copy(isr, fos, StandardCharsets.UTF_8);
					}
				} else {
					FileUtils.copyInputStreamToFile(response.getEntity().getContent(), file);
				}
			} else {
				throw new HttpResponseException(status, response.getStatusLine().getReasonPhrase());
			}
		}
	}


	@Override
	public void close() throws IOException {
		if (client != null)
			client.close();
	}
}
