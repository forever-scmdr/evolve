package ecommander.fwk;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpHost;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.conn.ssl.TrustSelfSignedStrategy;
import org.apache.http.entity.ContentType;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;

/**
 * Класс для закачки файлов по урлу
 * Created by E on 2/2/2018.
 */
public class WebClient {
	private static final String UTF_8 = "UTF-8";

	private static String getString(String url, StringBuilder encName, String...proxy) throws IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
		SSLConnectionSocketFactory scsf = new SSLConnectionSocketFactory(
				SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build(),
				NoopHostnameVerifier.INSTANCE);
		try (CloseableHttpClient client = HttpClients.custom().setSSLSocketFactory(scsf).build()) {
			HttpGet get = new HttpGet(url);
			get.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
			if (proxy.length > 0 && StringUtils.isNotBlank(proxy[0])) {
				HttpHost proxyHost = new HttpHost(proxy[0]);
				RequestConfig config = RequestConfig.custom().setProxy(proxyHost).build();
				get.setConfig(config);
			}
			try (CloseableHttpResponse response = client.execute(get)) {
				int status = response.getStatusLine().getStatusCode();
				if (status >= 200 && status < 300) {
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
				} else {
					throw new HttpResponseException(status, response.getStatusLine().getReasonPhrase());
				}
			}
		}
	}

	public static String getString(String url, String...proxy) throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		return getString(url, new StringBuilder(UTF_8), proxy);
	}

	public static String getCleanHtml(String url, String...proxy) throws IOException, NoSuchAlgorithmException, KeyStoreException, KeyManagementException {
		StringBuilder charsetName = new StringBuilder(UTF_8);
		String badHtml = getString(url, charsetName, proxy);
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
			result = getString(url, new StringBuilder(realCharsetName), proxy);
			result = Strings.cleanHtml(result);
		}
		return result;
	}

	public static void saveFile(String url, String dirName, String saveAs, String...proxy) throws IOException, KeyStoreException, NoSuchAlgorithmException, KeyManagementException {
		String badPart = StringUtils.substringAfterLast(url,"/");
		url = url.replace(badPart, URLEncoder.encode(badPart, "UTF-8").replace("+", "%20"));
		SSLConnectionSocketFactory scsf = new SSLConnectionSocketFactory(
				SSLContexts.custom().loadTrustMaterial(null, new TrustSelfSignedStrategy()).build(),
				NoopHostnameVerifier.INSTANCE);
		try (CloseableHttpClient client = HttpClients.custom().setSSLSocketFactory(scsf).build()) {
			HttpGet get = new HttpGet(url);
			get.setHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
			if (proxy.length > 0 && StringUtils.isNotBlank(proxy[0])) {
				HttpHost proxyHost = new HttpHost(proxy[0]);
				RequestConfig config = RequestConfig.custom().setProxy(proxyHost).build();
				get.setConfig(config);
			}
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
					FileUtils.copyInputStreamToFile(response.getEntity().getContent(), file);
				} else {
					throw new HttpResponseException(status, response.getStatusLine().getReasonPhrase());
				}
			}
		}
	}


}
