package lunacrawler;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.fwk.JsoupUtils;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import extra._generated.Parse_item;
import lunacrawler.fwk.SingleItemCrawlerController;
import net.sf.saxon.TransformerFactoryImpl;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.Reader;
import java.io.StringReader;

/**
 * Команда тестирования XSLT преобразования для одного айтема parse_item
 * Created by E on 13/2/2018.
 */
public class TestParseItemCommand extends Command {
	@Override
	public ResultPE execute() throws Exception {
		Parse_item pi = Parse_item.get(getSingleLoadedItem("pi"));
		if (pi != null) {
			SingleItemCrawlerController controller = new SingleItemCrawlerController(new IntegrateBase.Info());
			String stylesDir = AppContext.getRealPath(AppContext.getProperty(SingleItemCrawlerController.STYLES_DIR, null));
			if (stylesDir != null && !stylesDir.endsWith("/"))
				stylesDir += "/";
			File xslFile = new File(stylesDir + controller.getStyleForUrl(pi.get_url()));
			if (!xslFile.exists()) {
				return getResult("test").setValue("NO template exists for url " + pi.get_url());
			}
			TransformerFactory factory = TransformerFactoryImpl.newInstance();
			Transformer transformer = factory.newTransformer(new StreamSource(xslFile));

			Document jsoupDoc = Jsoup.parse(pi.get_html());
			String html = JsoupUtils.outputHtmlDoc(jsoupDoc);

			Reader reader = new StringReader(html);
			ByteArrayOutputStream bos = new ByteArrayOutputStream();
			transformer.transform(new StreamSource(reader), new StreamResult(bos));
			return getResult("test").setValue(bos.toString(SingleItemCrawlerController.UTF_8));
		}
		return null;
	}
}
