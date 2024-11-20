package ecommander.migration;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang3.StringUtils;

import ecommander.common.ServerLogger;
import ecommander.model.item.ItemType;
import ecommander.model.item.ItemTypeRegistry;
import ecommander.model.item.ParameterDescription;
import ecommander.persistence.commandunits.DBPersistenceCommandUnit;
import ecommander.persistence.mappers.DBConstants;

public class FilterEntityIdToNameConverter extends DBPersistenceCommandUnit {

	public void execute() throws Exception {
		String sql = "SELECT " + DBConstants.Item.ID + ", " + DBConstants.Item.PARAMS + " FROM " + DBConstants.Item.TABLE + " WHERE "
				+ DBConstants.Item.PARAMS + " LIKE '%&lt;filter %'";
		Statement stmt = getTransactionContext().getConnection().createStatement();
		HashMap<Long, String> filters = new HashMap<Long, String>();
		ServerLogger.debug(sql);
		ResultSet rs = stmt.executeQuery(sql);
		while (rs.next())
			filters.put(rs.getLong(1), rs.getString(2));
		for (Long id : filters.keySet()) {
			String filter = filters.get(id);
			int itemTypeId = -1;
			ItemType type = null;
			String itemIdStr = StringUtils.substringBetween(filter, "item-desc=&quot;", "&quot;");
			try {
				itemTypeId = Integer.parseInt(itemIdStr);
				type = ItemTypeRegistry.getItemType(itemTypeId);
			} catch (NumberFormatException e) {
				type = ItemTypeRegistry.getItemType(itemIdStr);
			}
			if (type == null)
				type = ItemTypeRegistry.getItemType(itemIdStr);
			if (type != null) {
				// Заменить ID типа айтема на название айтема
				filter = filter.replace("item-desc=&quot;" + itemTypeId + "&quot;", "item-desc=&quot;" + type.getName() + "&quot;");
				// Заменить ID параметров на их названия
				List<String> paramIdStrings = getAllMatches(filter, "paramId=&quot;.+?&quot;");
				for (String paramIdString : paramIdStrings) {
					int paramId = Integer.parseInt(StringUtils.substringBetween(paramIdString, "&quot;"));
					ParameterDescription param = type.getParameter(paramId);
					if (param != null)
						filter = filter.replace(paramIdString, "param=&quot;" + param.getName() + "&quot;");
				}
			} else {
				// Удалить фильтр
				filter = filter.replaceFirst("(?s)&lt;filter.*&lt;/filter&gt;", "");
			}
			String update = "UPDATE " + DBConstants.Item.TABLE + " SET " + DBConstants.Item.PARAMS + "='" + filter + "' WHERE "
					+ DBConstants.Item.ID + "=" + id;
			stmt.executeUpdate(update);
			ServerLogger.debug("filter converted: " + id);
		}
		stmt.close();
	}

    public static List<String> getAllMatches(String text, String regex) {
        List<String> matches = new ArrayList<String>();
        Matcher m = Pattern.compile("(?=(" + regex + "))").matcher(text);
        while(m.find()) {
            matches.add(m.group(1));
        }
        return matches;
    }
	
    public static void main(String[] args) {
    	String test = "<name>Душевые поддоны</name>\r\n" + 
    			" <search>101009008000х, Душевые поддоны, Душевой поддон, поддон, leitdst gjlljys, leitdjq gjlljy, gjlljy</search>\r\n" + 
    			" <filter>&lt;filter item-desc=&quot;172&quot; id=&quot;1&quot; count=&quot;9&quot;&gt;\r\n" + 
    			" 	&lt;input id=&quot;2&quot; type=&quot;checkgroup&quot; domain=&quot;[Душевые поддоны] Страна производства&quot; caption=&quot;Страна производства&quot; description=&quot;&quot;&gt;\r\n" + 
    			" 		&lt;domain name=&quot;[Душевые поддоны] Страна производства&quot;&gt;\r\n" + 
    			" 			&lt;value&gt;Польша&lt;/value&gt;\r\n" + 
    			" 			&lt;value&gt;Чехия&lt;/value&gt;\r\n" + 
    			" 		&lt;/domain&gt;\r\n" + 
    			" 		&lt;criteria id=&quot;3&quot; paramId=&quot;1403&quot; sign=&quot;=&quot; pattern=&quot;&quot;/&gt;\r\n" + 
    			" 	&lt;/input&gt;\r\n" + 
    			" 	&lt;input id=&quot;4&quot; type=&quot;checkgroup&quot; domain=&quot;[Душевые поддоны] Производитель&quot; caption=&quot;Производитель&quot; description=&quot;&quot;&gt;\r\n" + 
    			" 		&lt;domain name=&quot;[Душевые поддоны] Производитель&quot;&gt;\r\n" + 
    			" 			&lt;value&gt;Riho&lt;/value&gt;\r\n" + 
    			" 			&lt;value&gt;Roltechnik&lt;/value&gt;\r\n" + 
    			" 			&lt;value&gt;Sanplast&lt;/value&gt;\r\n" + 
    			" 		&lt;/domain&gt;\r\n" + 
    			" 		&lt;criteria id=&quot;5&quot; paramId=&quot;1404&quot; sign=&quot;=&quot; pattern=&quot;&quot;/&gt;\r\n" + 
    			" 	&lt;/input&gt;\r\n" + 
    			" 	&lt;input id=&quot;6&quot; type=&quot;checkgroup&quot; domain=&quot;[Душевые поддоны] Материал&quot; caption=&quot;Материал&quot; description=&quot;&quot;&gt;\r\n" + 
    			" 		&lt;domain name=&quot;[Душевые поддоны] Материал&quot;&gt;\r\n" + 
    			" 			&lt;value&gt;акрил&lt;/value&gt;\r\n" + 
    			" 		&lt;/domain&gt;\r\n" + 
    			" 		&lt;criteria id=&quot;7&quot; paramId=&quot;1405&quot; sign=&quot;=&quot; pattern=&quot;&quot;/&gt;\r\n" + 
    			" 	&lt;/input&gt;\r\n" + 
    			" 	&lt;input id=&quot;8&quot; type=&quot;checkgroup&quot; domain=&quot;[Душевые поддоны] Форма&quot; caption=&quot;Форма&quot; description=&quot;&quot;&gt;\r\n" + 
    			" 		&lt;domain name=&quot;[Душевые поддоны] Форма&quot;&gt;\r\n" + 
    			" 			&lt;value&gt;квадратная&lt;/value&gt;\r\n" + 
    			" 			&lt;value&gt;круглая&lt;/value&gt;\r\n" + 
    			" 			&lt;value&gt;полуквадратная&lt;/value&gt;\r\n" + 
    			" 			&lt;value&gt;полукруглая&lt;/value&gt;\r\n" + 
    			" 			&lt;value&gt;прямоугольная&lt;/value&gt;\r\n" + 
    			" 		&lt;/domain&gt;\r\n" + 
    			" 		&lt;criteria id=&quot;9&quot; paramId=&quot;1407&quot; sign=&quot;=&quot; pattern=&quot;&quot;/&gt;\r\n" + 
    			" 	&lt;/input&gt;\r\n" + 
    			" &lt;/filter&gt;</filter>";
    	// Замена item-desc
    	List<String> parts = getAllMatches(test, "item-desc=&quot;.+?&quot;");
    	System.out.println(parts);
    	System.out.println(Long.parseLong(StringUtils.substringBetween(test, "item-desc=&quot;", "&quot;")));
    	for (String string : parts) {
			System.out.println(StringUtils.substringBetween(string, "&quot;"));
			test = test.replace(string, "item-desc=&quot;mega&quot;");
		}
    	
    	parts = getAllMatches(test, "paramId=&quot;.+?&quot;");
    	System.out.println(parts);
    	System.out.println(StringUtils.substringBetween(test, "item-desc=&quot;", "&quot;"));
    	for (String string : parts) {
			System.out.println(StringUtils.substringBetween(string, "&quot;"));
			test = test.replace(string, "param=&quot;cool&quot;");
		}
    	System.out.println(test);
    	System.out.println(test.replaceAll("(?s)&lt;filter.*&lt;/filter&gt;", ""));
    }
}
