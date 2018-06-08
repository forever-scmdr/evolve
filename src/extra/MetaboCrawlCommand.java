package extra;

import edu.uci.ics.crawler4j.url.WebURL;
import lunacrawler.CrawlCommand;
import org.apache.commons.lang3.StringUtils;

/**
 * Created by E on 25/4/2018.
 */
public class MetaboCrawlCommand extends CrawlCommand {
	@Override
	public void modifyUrl(WebURL url) {
		String href = url.getURL();
		if (StringUtils.contains(href, ".html?")) {
			href = StringUtils.substringBeforeLast(href, "?");
			url.setURL(href);
		}
	}
}
