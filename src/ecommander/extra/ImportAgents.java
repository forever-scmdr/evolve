package ecommander.extra;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.joda.time.DateTime;
import org.joda.time.DateTimeZone;

import ecommander.application.extra.IntegrateBase;
import ecommander.application.extra.ItemUtils;
import ecommander.application.extra.POIExcelWrapper;
import ecommander.application.extra.POIUtils;
import ecommander.common.ServerLogger;
import ecommander.controllers.AppContext;
import ecommander.extra._generated.ItemNames;
import ecommander.model.item.Item;
import ecommander.model.item.ItemType;
import ecommander.model.item.ItemTypeRegistry;
import ecommander.persistence.DelayedTransaction;
import ecommander.persistence.commandunits.SaveNewItemDBUnit;
import ecommander.persistence.commandunits.UpdateItemDBUnit;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.LuceneIndexMapper;

public class ImportAgents extends IntegrateBase {
	private static final int TRANSACTION_LENGTH = 10;

	private static final ArrayList<String> FILE_AGENT_SINGLE_PARAMS = new ArrayList<String>();
	static {
		FILE_AGENT_SINGLE_PARAMS.add(ItemNames.agent.ORGANIZATION);
		FILE_AGENT_SINGLE_PARAMS.add(ItemNames.agent.COUNTRY);
		FILE_AGENT_SINGLE_PARAMS.add(ItemNames.agent.REGION);
		FILE_AGENT_SINGLE_PARAMS.add(ItemNames.agent.CITY);
		FILE_AGENT_SINGLE_PARAMS.add(ItemNames.agent.ADDRESS);
		FILE_AGENT_SINGLE_PARAMS.add(ItemNames.agent.CONTACT_NAME);
		FILE_AGENT_SINGLE_PARAMS.add(ItemNames.agent.PHONE);
		FILE_AGENT_SINGLE_PARAMS.add(ItemNames.agent.EMAIL);
		FILE_AGENT_SINGLE_PARAMS.add(ItemNames.agent.EMAIL_2);
		FILE_AGENT_SINGLE_PARAMS.add(ItemNames.agent.EMAIL_3);
		FILE_AGENT_SINGLE_PARAMS.add(ItemNames.agent.SITE);
		FILE_AGENT_SINGLE_PARAMS.add(ItemNames.agent.BOSS_POSITION);
		FILE_AGENT_SINGLE_PARAMS.add(ItemNames.agent.BOSS_POSITION_DAT);
		FILE_AGENT_SINGLE_PARAMS.add(ItemNames.agent.BOSS_NAME);
		FILE_AGENT_SINGLE_PARAMS.add(ItemNames.agent.BOSS_NAME_DAT);
		FILE_AGENT_SINGLE_PARAMS.add(ItemNames.agent.BOSS_GREETINGS);
		FILE_AGENT_SINGLE_PARAMS.add(ItemNames.agent.TYPE);
		FILE_AGENT_SINGLE_PARAMS.add(ItemNames.agent.BRANCH);
		FILE_AGENT_SINGLE_PARAMS.add(ItemNames.agent.DESC);
	}
	
	private Item allAgents = null;
	private File fileToImport = null;
	private POIExcelWrapper xlsWrapper = null;
	private AgentFileHeaderInfo header = new AgentFileHeaderInfo();
	private Iterator<Row> rowIterator = null;
	private HashMap<Long, Item> agentsById = new HashMap<Long, Item>();
	private HashMap<String, Item> agentsByEmail = new HashMap<String, Item>();
	
	@Override
	protected boolean makePreparations() {
		try {
			allAgents = ItemUtils.ensureSingleRootItem(ItemNames.ALL_AGENTS, getInitiator(), false);
		} catch (Exception e) {
			ServerLogger.error("Integration error", e);
			addError("Невозможно загрузить список агентов, описание ошибки в логе", "root");
			return false;
		}
		fileToImport = allAgents.getFileValue(ItemNames.all_agents.FILE, AppContext.getFilesDirPath());
		if (fileToImport == null) {
			addError("Не задан файл интеграции", 0, 0);
			return false;
		}
		if (!fileToImport.exists()) {
			try {
				addError("Не найден файл интеграции '" + fileToImport.getCanonicalPath() + "'", 0, 0);
			} catch (IOException e) { /**/ }
			return false;
		}
		// Открытие шаблона документа excel
		xlsWrapper = POIExcelWrapper.create(fileToImport);
		XSSFWorkbook wb = (XSSFWorkbook) xlsWrapper.getWorkbook();
		XSSFSheet sheet = wb.getSheetAt(wb.getActiveSheetIndex());
		rowIterator = sheet.iterator();
		try {
			if (!header.init(rowIterator)) {
				addError("Не найден заголовок таблицы (строка, которая начинается с символа '№')", 0, 0);
				xlsWrapper.close();
				return false;
			}
		} catch (Exception e) {
			addError("Ошибка при разборе заголовка файла. Возможно использованы некорректные заголовки столбцов", 0, 0);
		}
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		setOperation("Импорт обновлений каталога агентов");
		
		// Загрузить всех агентов
		Collection<Item> agents = ItemQuery.newItemQuery(ItemNames.AGENT).loadItems();
		for (Item agent : agents) {
			addAgent(agent);
		}
		
		// Редактирование агентов
		DelayedTransaction transaction = new DelayedTransaction(getInitiator());
		Row row = null;
		Cell cell = null;
		ItemType agentItemType = ItemTypeRegistry.getItemType(ItemNames.AGENT);
		ItemType procItemType = ItemTypeRegistry.getItemType(ItemNames.PROCUREMENT);
		int itemsCreated = 0;
		try {
			while (rowIterator.hasNext()) {
				row = rowIterator.next();
				setLineNumber(row.getRowNum());

				// Редактирование девайса
				
				// Поиск агента среди существующих (создание, если не найден)
				String idStr = POIUtils.getCellAsString(row.getCell(header.getParamColumn("id")));
				String email = POIUtils.getCellAsString(row.getCell(header.getParamColumn(ItemNames.agent.EMAIL)));
				String email2 = POIUtils.getCellAsString(row.getCell(header.getParamColumn(ItemNames.agent.EMAIL_2)));
				String email3 = POIUtils.getCellAsString(row.getCell(header.getParamColumn(ItemNames.agent.EMAIL_3)));
				if (StringUtils.isBlank(email) && StringUtils.isBlank(email2) && StringUtils.isBlank(email3))
					continue;
				String agentEmails =  + ','
						+ POIUtils.getCellAsString(row.getCell(header.getParamColumn(ItemNames.agent.EMAIL_2))) + ','
						+ POIUtils.getCellAsString(row.getCell(header.getParamColumn(ItemNames.agent.EMAIL_3)));
				Item agent = getAgent(idStr, agentEmails);
				if (agent == null) {
					agent = Item.newChildItem(agentItemType, allAgents);
					agent.setValue(ItemNames.agent.REGISTER_DATE, DateTime.now(DateTimeZone.UTC).getMillis());
				}
				
				// Обновление параметров агента
				for (String paramName : FILE_AGENT_SINGLE_PARAMS) {
					Integer paramColNum = header.getParamColumn(paramName);
					if (paramColNum != null)
						agent.setValue(paramName, POIUtils.getCellAsString(row.getCell(paramColNum)));
				}
				String newsTags = POIUtils.getCellAsString(row.getCell(header.getParamColumn(ItemNames.agent.TAGS)));
				if (StringUtils.isNotBlank(newsTags)) {
					String[] tags = StringUtils.split(newsTags, ',');
					agent.removeValue(ItemNames.agent.TAGS);
					for (String tag : tags) {
						agent.setValue(ItemNames.agent.TAGS, tag);
					}
				}
				
				// Сохранение в БД агента
				if (agent.isNew()) {
					agent.setValue("refuzed", (byte)0);
					transaction.addCommandUnit(new SaveNewItemDBUnit(agent).fulltextIndex(false));
					addLog("Создан новый контрагент. Название: '" + agent.getStringValue(ItemNames.agent.ORGANIZATION) + "' Email: "
							+ agent.getStringValue(ItemNames.agent.EMAIL));
					addAgent(agent);
				} else {
					transaction.addCommandUnit(new UpdateItemDBUnit(agent).fulltextIndex(false));
				}

				String proc = POIUtils.getCellAsString(row.getCell(header.getParamColumn(ItemNames.procurement.TEXT)));
				
				// Пакетный коммит транзакции
				if (transaction.getCommandCount() >= TRANSACTION_LENGTH 
						|| (StringUtils.isNotBlank(proc) && agent.getId() == Item.DEFAULT_ID)) {
					transaction.execute();
					itemsCreated += transaction.getCommandCount();
					ServerLogger.debug("Processed: " + itemsCreated + " devices");
					setProcessed(itemsCreated);
				}

				// Добавление информации о поставках
				if (StringUtils.isNotBlank(proc)) {
					Item newProc = Item.newChildItem(procItemType, agent);
					newProc.setValue(ItemNames.procurement.DATE, DateTime.now(DateTimeZone.UTC).getMillis());
					newProc.setValue(ItemNames.procurement.TEXT, proc);
					transaction.addCommandUnit(new SaveNewItemDBUnit(newProc).fulltextIndex(false));
				}
			}
			transaction.execute();
			if (transaction.getCommandCount() != TRANSACTION_LENGTH)
				itemsCreated += transaction.getCommandCount();
			setProcessed(itemsCreated);
			addLog("Создание записей завершено");
			setOperation("Индексация");
			addLog("Индексация...");
			LuceneIndexMapper.reindexAll();
			addLog("Индексация завершена");
			
		} catch (Exception e) {
			addError("Ошибка формата. ", row != null ? row.getRowNum() : 0, cell != null ? cell.getColumnIndex() : 0);
			ServerLogger.error("Ошибка разбора", e);
		}
		addLog("Обновление каталога агентов завершено");
		xlsWrapper.close();
	}

	
	private void addAgent(Item agent) throws Exception {
		agentsById.put(agent.getId(), agent);
		String email = agent.getStringValue(ItemNames.agent.EMAIL, "") + ',' 
				+ agent.getStringValue(ItemNames.agent.EMAIL_2, "") + ',' 
				+ agent.getStringValue(ItemNames.agent.EMAIL_3, "");
		email = email.toLowerCase();
		String[] emails = StringUtils.split(email, ",; ");
		for (String agentEmail : emails) {
			if (agentsByEmail.containsKey(agentEmail)) {
				addError("Попытка присвоить двум агентам одинаковый email " + agentEmail, 
						"Агент " + agent.getStringValue(ItemNames.agent.ORGANIZATION));
				throw new Exception("Email agent error");
			}
			agentsByEmail.put(email, agent);
		}
	}
	
	private Item getAgent(String agentIdStr, String email) {
		if (StringUtils.isNotBlank(email)) {
			email = email.toLowerCase();
			String[] emails = StringUtils.split(email, ",; ");
			for (String agentEmail : emails) {
				Item agent = agentsByEmail.get(agentEmail);
				if (agent != null)
					return agent;
			}
		}
		long agentId = -1;
		if (StringUtils.isNotBlank(agentIdStr)) {
			try {
				agentId = Long.parseLong(agentIdStr);
			} catch (Exception e) {
				agentId = -1;
			}
		}
		return agentsById.get(agentId);
	}
	
	public static void main(String[] args) {
		System.out.println(Arrays.asList(StringUtils.split(", ,;, ;forever@forever-ds.com, forever2@forever-ds.com,,,", ",; ")));
	}

}
