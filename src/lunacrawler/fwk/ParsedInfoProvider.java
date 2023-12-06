package lunacrawler.fwk;

import ecommander.controllers.AppContext;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.Strings;
import ecommander.fwk.Timer;
import ecommander.fwk.XmlDataSource;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Parser;
import org.jsoup.select.Elements;

import javax.xml.stream.XMLStreamException;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Извлекает информацию из файлов для парсинга
 * Created by E on 2/5/2018.
 */
public class ParsedInfoProvider {
	public static final String RESULT_DIR = "parsing.result_dir"; // директория, в которой лежат файлы со стилями
	public static final String UTF_8 = "UTF-8";


	public static class InfoAccessor {
		private Element root;

		public InfoAccessor(Document root) {
			this.root = root;
		}

		public String getNodeText(String tag, String...defaultValue) {
			Element el = root.getElementsByTag(tag).first();
			if (el == null || StringUtils.isBlank(el.ownText()))
				return defaultValue.length > 0 ? defaultValue[0] : null;
			return el.ownText();
		}

		public String getNodeHtml(String tag, String...defaultValue) {
			Element el = root.getElementsByTag(tag).first();
			if (el == null)
				return defaultValue.length > 0 ? defaultValue[0] : null;
			return el.html();
		}

		public Element getRoot() {
			return root;
		}

		public Element getFirst(String tag) {
			return root.getElementsByTag(tag).first();
		}

		public Elements getChildrenOfFirst(String firstTag, String childrenTag) {
			Element el = root.getElementsByTag(firstTag).first();
			if (el == null)
				return new Elements();
			return el.getElementsByTag(childrenTag);
		}
	}


	private Path compiledDir;
	private Path filesDir;
	private Document tree;
	private XmlDataSource bigTree;
	private Timer timer = null;

	public ParsedInfoProvider(Timer... timer) {
		new ParsedInfoProvider(false);
	}

	public ParsedInfoProvider(boolean isTreeBig, Timer... timer) {
		String resultDir = AppContext.getRealPath(AppContext.getProperty(RESULT_DIR, null));
		if (resultDir != null && !resultDir.endsWith("/"))
			resultDir += "/";
		if (resultDir != null) {
			compiledDir = Paths.get(resultDir + "_compiled/");
			filesDir = Paths.get(resultDir + "_files/");
		}
		if (!Files.exists(compiledDir)) {
			throw new IllegalStateException("parsing result is not found");
		}
		Path treeFile = compiledDir.resolve("!_tree_!.xml");
		if (!Files.exists(treeFile)) {
			throw new IllegalStateException("parsing tree is not found");
		}
		if (isTreeBig) {
			try {
				bigTree = new XmlDataSource(treeFile.toString(), StandardCharsets.UTF_8, timer);
			} catch (Exception e) {
				ServerLogger.error("Error while opening result tree file", e);
				bigTree = null;
			}
		} else {
			try {
				String xml = new String(Files.readAllBytes(treeFile), UTF_8);
				tree = Jsoup.parse(xml, "localhost", Parser.xmlParser());
			} catch (Exception e) {
				ServerLogger.error("Error while parsing result tree file", e);
				tree = null;
			}
		}
		if (timer.length > 0) {
			this.timer = timer[0];
		} else {
			this.timer = new Timer();
		}
	}

	/**
	 * Для небольших документов
	 * @return
	 */
	public Document getTree() {
		return tree;
	}

	/**
	 * Для больших документов
	 * @return
	 */
	public XmlDataSource getBigTree() {
		return bigTree;
	}

	public Document getItem(String id) throws IOException {
		String fileName = Strings.createFileName(id) + ".xml";
		String divisionDirName = Crawler.getUrlDirName(id);
		Path file = compiledDir.resolve(divisionDirName + fileName);
		timer.start("file read");
		String xml = new String(Files.readAllBytes(file), UTF_8);
		timer.stop("file read");
		Document doc = Jsoup.parse(xml, "localhost", Parser.xmlParser());
		return doc;
	}

	public InfoAccessor getAccessor(String id) throws IOException {
		return new InfoAccessor(getItem(id));
	}


	public InfoAccessor getAccessorJsoup(Element element) {
		String code = element.attr("id");
		if (StringUtils.isBlank(code))
			return null;
		try {
			return getAccessor(code);
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * Вернуть файл или null, если файл не найден
	 * @param id
	 * @param fileUrl
	 * @return
	 */
	public Path getFile(String id, String fileUrl) {
		String fileName = fileUrl;
		Path itemDir = Paths.get(filesDir + File.separator + id);
		Path file = null;
		if (StringUtils.contains(fileName, File.separator) || StringUtils.contains(fileName, '/')) {
			fileName = Strings.getFileName(fileName);
			file = itemDir.resolve(fileName);
		}
		if (file == null) {
			file = itemDir.resolve(fileName);
			if (!Files.exists(file)) {
				fileName = Strings.getFileName(fileName);
				file = itemDir.resolve(fileName);
			}
		}
		if (Files.exists(file))
			return file;
		return null;
	}

	public boolean isValid() {
		return tree != null || bigTree != null;
	}
}
