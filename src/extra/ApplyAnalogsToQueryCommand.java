package extra;

import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * Добавить к переменным страниы аналоги из запроса
 */
public class ApplyAnalogsToQueryCommand extends Command {

	@Override
	public ResultPE execute() throws Exception {
		List<Object> queries = getVarValues("q");
		HashSet<String> analogQueries = new HashSet<>();
		int minQueryLength = Integer.parseInt(getVarSingleValueDefault("min_query_length", "4"));
		XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();
		for (Object query : queries) {
			String q = (String) query;
			if (q.length() < minQueryLength)
				continue;
			xml.startElement("set").addElement("base", q);
			ArrayList<Item> sets = ItemQuery.loadByParamValue(ItemNames.ANALOG, ItemNames.analog_.SET, (String) query);
			for (Item set : sets) {
				ArrayList<String> analogs = set.getStringValues(ItemNames.analog_.SET);
				for (String analog : analogs) {
					if (!StringUtils.equals(q, analog)) {
						xml.addElement("analog", analog);
						analogQueries.add(analog);
					}
				}
			}
			xml.endElement();
		}
		// Новая переменная страницы
		for (String analogQuery : analogQueries) {
			setPageVariable("q", analogQuery);
		}
		return getResult("analogs").setValue(xml.toString());
	}

}
