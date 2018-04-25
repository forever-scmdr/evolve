package ecommander.fwk;

import ecommander.controllers.AppContext;
import ecommander.controllers.PageController;
import ecommander.pages.Command;
import ecommander.pages.ExecutablePagePE;
import ecommander.pages.ResultPE;
import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by user on 24.04.2018.
 */
public class CreateSiteMap extends Command {

	private static final String COMMENT_PATTERN = "<!--(?<comment>.*)-->";

	@Override
	public ResultPE execute() throws Exception {
		ExecutablePagePE siteMap = getExecutablePage("sitemap");
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		PageController.newSimple().executePage(siteMap, bos);
		String pageContent = bos.toString("UTF-8");
		Pattern pattern = Pattern.compile(COMMENT_PATTERN);
		Matcher matcher = pattern.matcher(pageContent);
		StringBuilder sb = new StringBuilder();
		sb.append(pageContent.replace("</urlset>", ""));
		while(matcher.find()){
			String keyUnique = matcher.group("comment");
			String url = "sitemap_section/"+keyUnique;
			bos = new ByteArrayOutputStream();
			siteMap = getExecutablePage(url);
			PageController.newSimple().executePage(siteMap, bos);
			pageContent = bos.toString("UTF-8");
			pageContent = StringUtils.substringAfter(pageContent, "xsi:schemaLocation=\"http://www.sitemaps.org/schemas/sitemap/0.9/sitemap.xsd\">");
			pageContent = StringUtils.substringBefore(pageContent, "</urlset>");
			sb.append(pageContent);
		}
		sb.append("\n</urlset>");
		ResultPE res = getResult("complete");
		String fullSiteMap = sb.toString();
		String rootFolder = AppContext.getContextPath()+"sitemap.xml";
		Files.write(Paths.get(rootFolder), fullSiteMap.getBytes("UTF-8"));
		res.setValue(fullSiteMap);
		return res;
	}
}
