package ecommander.fwk;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.Charset;

public class FluentWebClient {
	private static final String UTF_8 = "UTF-8";

	private static String getString(String url, StringBuilder encName, String... proxy) throws IOException {
		Request req = Request.Get(url)
				.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
		if (proxy.length > 0 && StringUtils.isNotBlank(proxy[0])) {
			req.viaProxy(proxy[0]);
		}
		return req.connectTimeout(1000).socketTimeout(3000)
				.execute()
				.handleResponse(response -> {
					StatusLine statusLine = response.getStatusLine();
					HttpEntity entity = response.getEntity();
					if (statusLine.getStatusCode() >= 300) {
						throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
					}
					if (entity == null) {
						return null;
					}
					ContentType contentType = ContentType.getOrDefault(entity);
					Charset charset = contentType.getCharset();
					if (charset == null) {
						charset = Charset.forName(encName.toString());
					} else {
						encName.replace(0, encName.length(), charset.name());
					}
					return IOUtils.toString(entity.getContent(), charset);
				});
	}

	public static String getString(String url, String... proxy) throws IOException {
		return getString(url, new StringBuilder(UTF_8), proxy);
	}

	public static String getCleanHtml(String url, String... proxy) throws IOException {
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

	public static void saveFile(String url, String dirName, String saveAs, String... proxy) throws Exception {
		String badPart = StringUtils.substringAfterLast(url, "/");
		url = url.replace(badPart, URLEncoder.encode(badPart, "UTF-8").replaceAll("\\+", "%20"));
		String host = url.replaceAll("https?:\\/\\/(www.)?", "");
		host = host.substring(0, host.indexOf('/'));
		Request req = Request.Get(url)
				.addHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:87.0) Gecko/20100101 Firefox/87.0")
				.addHeader("Host", host);
		if (proxy.length > 0 && StringUtils.isNotBlank(proxy[0])) {
			req.viaProxy(proxy[0]);
		}
		String finalUrl = url;

		try {
			req.connectTimeout(1000).socketTimeout(3000)
				.execute()
				.handleResponse(response -> {
					StatusLine statusLine = response.getStatusLine();
					HttpEntity entity = response.getEntity();
					if (statusLine.getStatusCode() >= 300) {
						throw new HttpResponseException(statusLine.getStatusCode(), statusLine.getReasonPhrase());
					}
					if (entity == null) {
						return false;
					}
					String fileDirName = dirName;
					if (!StringUtils.endsWith(fileDirName, "/"))
						fileDirName += "/";
					URL urlUrl = new URL(finalUrl);
					String newFileName = saveAs;
					if (StringUtils.isBlank(newFileName))
						newFileName = Strings.getFileName(urlUrl.getPath());
					File file = new File(fileDirName + newFileName);
					FileUtils.copyInputStreamToFile(entity.getContent(), file);
					return true;
				});
		}catch (Exception  e){
			WebClient.saveFileQuick(finalUrl, dirName, saveAs, proxy);
		}
	}
}
