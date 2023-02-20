package extra;

import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.itemquery.ItemQuery;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

/**
 * Добавить к переменным страниы аналоги из запроса
 */
public class ApplyAnalogsToQueryCommand extends Command {

	@Override
	public ResultPE execute() throws Exception {
		List<Object> queries = getVarValues("q");
		LinkedHashMap<String, LinkedHashSet<String>> analogQueries = new LinkedHashMap<>();
		HashSet<String> allQueries = new HashSet<>();
		int minQueryLength = Integer.parseInt(getVarSingleValueDefault("min_query_length", "4"));
		XmlDocumentBuilder xml = XmlDocumentBuilder.newDocPart();
		for (Object query : queries) {
			String q = (String) query;
			if (q.length() < minQueryLength || allQueries.contains(q))
				continue;
			allQueries.add(q);
			xml.startElement("set").addElement("base", q);
			analogQueries.put(q, new LinkedHashSet<>());
			ArrayList<Item> sets = ItemQuery.loadByParamValue(ItemNames.ANALOG, ItemNames.analog_.SET, (String) query);
			for (Item set : sets) {
				ArrayList<String> analogs = set.getStringValues(ItemNames.analog_.SET);
				for (String analog : analogs) {
					if (!StringUtils.equals(q, analog)) {
						allQueries.add(analog);
						xml.addElement("analog", analog);
						analogQueries.get(q).add(analog);
					}
				}
			}
			xml.endElement();
		}
		// Новая переменная страницы
		if (analogQueries.size() > 0) {
			setPageVariable("q", null);
			for (String baseQuery : analogQueries.keySet()) {
				setPageVariable("q", baseQuery);
				for (String analog : analogQueries.get(baseQuery)) {
					setPageVariable("q", analog);
				}
			}
		}
		return getResult("analogs").setValue(xml.toString());
	}

}
