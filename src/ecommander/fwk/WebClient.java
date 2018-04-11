package ecommander.fwk;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.FilenameUtils;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.http.HttpEntity;
import org.apache.http.StatusLine;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.fluent.Request;
import org.apache.http.entity.ContentType;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.Charset;

/**
 * Класс для закачки файлов по урлу
 * Created by E on 2/2/2018.
 */
public class WebClient {
	public static String getString(String url, String...proxy) throws IOException {
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
						charset = Charset.defaultCharset();
					}
					return IOUtils.toString(entity.getContent(), charset);
				});
	}

	public static void saveFile(String url, String dirName, String saveAs, String...proxy) throws IOException {
		Request req = Request.Get(url)
				.addHeader("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
		if (proxy.length > 0 && StringUtils.isNotBlank(proxy[0])) {
			req.viaProxy(proxy[0]);
		}
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
					URL urlUrl = new URL(url);
					String newFileName = saveAs;
					if (StringUtils.isBlank(newFileName))
						newFileName = Strings.getFileName(urlUrl.getPath());
					File file = new File(fileDirName + newFileName);
					FileUtils.copyInputStreamToFile(entity.getContent(), file);
					return true;
				});
	}


}
