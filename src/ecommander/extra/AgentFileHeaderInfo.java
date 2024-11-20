package ecommander.extra;

import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.commons.collections4.BidiMap;
import org.apache.commons.collections4.bidimap.DualHashBidiMap;
import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;

import ecommander.application.extra.POIUtils;
import ecommander.application.extra.Pair;
import ecommander.extra._generated.ItemNames;

public class AgentFileHeaderInfo {
	public static final String ID_FILE = "№";
	public static final String COUNTRY_FILE = "Страна";
	public static final String REGION_FILE = "Область";
	public static final String CITY_FILE = "Город";
	public static final String ORGANIZATION_FILE = "Организация";
	public static final String ADDRESS_FILE = "Адрес";
	public static final String PHONE_FILE = "Телефон";
	public static final String EMAIL_FILE = "e-mail 1 общий";
	public static final String EMAIL_2_FILE = "e-mail 2";
	public static final String EMAIL_3_FILE = "e-mail 3";
	public static final String SITE_FILE = "Сайт";
	public static final String CONTACT_NAME_FILE = "Контактное лицо";
	public static final String BOSS_POSITION_FILE = "Должность руководителя";
	public static final String BOSS_POSITION_DAT_FILE = "Должность руководителя в д.п.";
	public static final String BOSS_NAME_FILE = "Руководитель";
	public static final String BOSS_NAME_DAT_FILE = "Руководитель в д.п.";
	public static final String BOSS_GREETINGS_FILE = "Обращение";
	public static final String TYPE_FILE = "Род деятельности";
	public static final String BRANCH_FILE = "Отрасль";
	public static final String DESC_FILE = "Примечание";
	public static final String TAGS_FILE = "Рассылка";
	public static final String PROCUREMENT_FILE = "Отгруженная продукция";
	
	
	public static LinkedHashMap<String, Pair<String, Integer>> FILE_PARAMS = new LinkedHashMap<String, Pair<String, Integer>>();
	public static final String[] TYPES = new String[] {
		"проектная организация", "монтажная организация", "проектно-монтажная организация", "газораспределяющая (ГРО) организация", 
		"эксплуатирующая организация", "торговая организация", "производитель (подробности в примечании)", "экспертиза и надзор"
	};
	public static final String[] BRANCHES = new String[] {
		"нефте-газодобыча и переработка", "теплоэнергетика (генерация)", "газораспределение", "металлургия", "химическая пром-ть", 
		"сельское хозяйство", "строительство", "ЖКХ"
	};
	static {
		FILE_PARAMS.put(ID_FILE, new Pair<String, Integer>("id", 3 * 256));
		FILE_PARAMS.put(COUNTRY_FILE, new Pair<String, Integer>(ItemNames.agent.COUNTRY, 15 * 256));
		FILE_PARAMS.put(REGION_FILE, new Pair<String, Integer>(ItemNames.agent.REGION, 15 * 256));
		FILE_PARAMS.put(CITY_FILE, new Pair<String, Integer>(ItemNames.agent.CITY, 15 * 256));
		FILE_PARAMS.put(ORGANIZATION_FILE, new Pair<String, Integer>(ItemNames.agent.ORGANIZATION, 23 * 256));
		FILE_PARAMS.put(ADDRESS_FILE, new Pair<String, Integer>(ItemNames.agent.ADDRESS, 30 * 256));
		FILE_PARAMS.put(PHONE_FILE, new Pair<String, Integer>(ItemNames.agent.PHONE, 30 * 256));
		FILE_PARAMS.put(EMAIL_FILE, new Pair<String, Integer>(ItemNames.agent.EMAIL, 20 * 256));
		FILE_PARAMS.put(EMAIL_2_FILE, new Pair<String, Integer>(ItemNames.agent.EMAIL_2, 20 * 256));
		FILE_PARAMS.put(EMAIL_3_FILE, new Pair<String, Integer>(ItemNames.agent.EMAIL_3, 20 * 256));
		FILE_PARAMS.put(SITE_FILE, new Pair<String, Integer>(ItemNames.agent.SITE, 25 * 256));
		FILE_PARAMS.put(CONTACT_NAME_FILE, new Pair<String, Integer>(ItemNames.agent.CONTACT_NAME, 30 * 256));
		FILE_PARAMS.put(BOSS_POSITION_FILE, new Pair<String, Integer>(ItemNames.agent.BOSS_POSITION, 15 * 256));
		FILE_PARAMS.put(BOSS_POSITION_DAT_FILE, new Pair<String, Integer>(ItemNames.agent.BOSS_POSITION_DAT, 15 * 256));
		FILE_PARAMS.put(BOSS_NAME_FILE, new Pair<String, Integer>(ItemNames.agent.BOSS_NAME, 30 * 256));
		FILE_PARAMS.put(BOSS_NAME_DAT_FILE, new Pair<String, Integer>(ItemNames.agent.BOSS_NAME_DAT, 30 * 256));
		FILE_PARAMS.put(BOSS_GREETINGS_FILE, new Pair<String, Integer>(ItemNames.agent.BOSS_GREETINGS, 40 * 256));
		FILE_PARAMS.put(TYPE_FILE, new Pair<String, Integer>(ItemNames.agent.TYPE, 30 * 256));
		FILE_PARAMS.put(BRANCH_FILE, new Pair<String, Integer>(ItemNames.agent.BRANCH, 30 * 256));
		FILE_PARAMS.put(DESC_FILE, new Pair<String, Integer>(ItemNames.agent.DESC, 30 * 256));
		FILE_PARAMS.put(TAGS_FILE, new Pair<String, Integer>(ItemNames.agent.TAGS, 30 * 256));
		FILE_PARAMS.put(PROCUREMENT_FILE, new Pair<String, Integer>(ItemNames.procurement.TEXT, 30 * 256));
	}

	private BidiMap<Integer, String> data = new DualHashBidiMap<Integer, String>();

	public AgentFileHeaderInfo() {
		int i = 0;
		for (Pair<String, Integer> paramName : FILE_PARAMS.values()) {
			data.put(i++, paramName.getLeft());			
		}
	}
	
	public boolean init(Iterator<Row> rows) {
		// Сначала находим строку, которая начинается с символа . (первая строка заголовка)
		Row firstRow = null;
		boolean hasHeader = false;
		while (rows.hasNext()) {
			firstRow = rows.next();
			Cell cell = firstRow.getCell(0);
			if (StringUtils.equals(POIUtils.getCellAsString(cell), ID_FILE)) {
				hasHeader = true;
				break;
			}
		}
		if (!hasHeader) {
			return false;
		}
		Iterator<Cell> firstRowIterator = firstRow.cellIterator();
		while (firstRowIterator.hasNext()) {
			Cell firstRowCell = firstRowIterator.next();
			data.put(firstRowCell.getColumnIndex(), FILE_PARAMS.get(firstRowCell.getStringCellValue()).getLeft());
		}
		return true;
	}
	
	public String getParamName(int colNumber) {
		return data.get(colNumber);
	}

	public Integer getParamColumn(String paramName) {
		return data.getKey(paramName);
	}
	
	public int getParamCount() {
		return data.size();
	}
}