package extra;

import ecommander.fwk.IntegrateBase;
import ecommander.model.Item;
import lunacrawler.fwk.ParsedInfoProvider;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * Created by E on 3/5/2018.
 */
public class MetaboIntegrateParsedCommand extends IntegrateBase {

	private ParsedInfoProvider infoProvider;

	@Override
	protected boolean makePreparations() throws Exception {
		infoProvider = new ParsedInfoProvider();
		return infoProvider.isValid();
	}

	@Override
	protected void integrate() throws Exception {
		Document tree = infoProvider.getTree();

	}

	private void processSection(Element section, Item parent) {

		section.select("> section");
	}

	@Override
	protected void terminate() throws Exception {

	}
}
