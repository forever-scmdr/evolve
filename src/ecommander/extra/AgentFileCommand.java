package ecommander.extra;

import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.util.CellRangeAddressList;
import org.apache.poi.xssf.usermodel.XSSFDataValidation;
import org.apache.poi.xssf.usermodel.XSSFDataValidationConstraint;
import org.apache.poi.xssf.usermodel.XSSFDataValidationHelper;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import ecommander.application.extra.Pair;
import ecommander.controllers.AppContext;
import ecommander.extra._generated.ItemNames;
import ecommander.model.item.Item;
import ecommander.pages.elements.Command;
import ecommander.pages.elements.ResultPE;
import ecommander.pages.elements.ResultPE.ResultType;
import ecommander.persistence.itemquery.ItemQuery;

public class AgentFileCommand extends Command {

	@Override
	public ResultPE execute() throws Exception {
		// Загрузить роды деятельности и отрасли
		String[] types = AgentFileHeaderInfo.TYPES;
		String[] branches = AgentFileHeaderInfo.BRANCHES;
		Item branchReg = ItemQuery.loadSingleItemByName(ItemNames.AGENT_BRANCHES);
		if (branchReg != null) {
			List<Item> items = ItemQuery.newItemQuery(ItemNames.STRING_ELEMENT).setPredecessorId(branchReg.getId()).loadItems();
			ArrayList<String> vals = new ArrayList<String>();
			for (Item item : items) {
				String val = item.getStringValue(ItemNames.string_element.NAME);
				if (StringUtils.isNotBlank(val))
					vals.add(val);
			}
			if (vals.size() > 0)
				branches = vals.toArray(new String[vals.size()]);
		}
		Item typeReg = ItemQuery.loadSingleItemByName(ItemNames.AGENT_TYPES);		
		if (typeReg != null) {
			List<Item> items = ItemQuery.newItemQuery(ItemNames.STRING_ELEMENT).setPredecessorId(typeReg.getId()).loadItems();
			ArrayList<String> vals = new ArrayList<String>();
			for (Item item : items) {
				String val = item.getStringValue(ItemNames.string_element.NAME);
				if (StringUtils.isNotBlank(val))
					vals.add(val);
			}
			if (vals.size() > 0)
				types = vals.toArray(new String[vals.size()]);
		}
		
		// Создание файла
		XSSFWorkbook wb = new XSSFWorkbook();
		XSSFSheet sheet = wb.createSheet("Sheet1");
		int rowIdx = 0;
		LinkedHashMap<Long, Item> agents = getLoadedItems("agent");
		XSSFRow row = sheet.createRow(rowIdx);
		int colIdx = -1;
		//AgentFileHeaderInfo header = new AgentFileHeaderInfo();
		for (String colHeader : AgentFileHeaderInfo.FILE_PARAMS.keySet()) {
			row.createCell(++colIdx).setCellValue(colHeader);
			sheet.setColumnWidth(colIdx, AgentFileHeaderInfo.FILE_PARAMS.get(colHeader).getRight());
		}
		for (Item agent : agents.values()) {
			row = sheet.createRow(++rowIdx);
			colIdx = -1;
			for (Pair<String, Integer> paramName : AgentFileHeaderInfo.FILE_PARAMS.values()) {
				if (StringUtils.equalsIgnoreCase(paramName.getLeft(), ItemNames.procurement.TEXT))
					continue;
				String value = null;
				if (StringUtils.equalsIgnoreCase(paramName.getLeft(), "id"))
					value = agent.getId() + "";
				else
					value = StringUtils.join(agent.outputValues(paramName.getLeft()), ", ");
				row.createCell(++colIdx).setCellValue(value);
				// Добавить выпадающий список для type и branch
				if (StringUtils.equalsIgnoreCase(paramName.getLeft(), ItemNames.agent.TYPE)) {
					XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
					XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper
							.createExplicitListConstraint(types);
					CellRangeAddressList addressList = new CellRangeAddressList(rowIdx, rowIdx, colIdx, colIdx);
					XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, addressList);
					validation.setShowErrorBox(true);
					sheet.addValidationData(validation);
				} else if (StringUtils.equalsIgnoreCase(paramName.getLeft(), ItemNames.agent.BRANCH)) {
					XSSFDataValidationHelper dvHelper = new XSSFDataValidationHelper(sheet);
					XSSFDataValidationConstraint dvConstraint = (XSSFDataValidationConstraint) dvHelper
							.createExplicitListConstraint(branches);
					CellRangeAddressList addressList = new CellRangeAddressList(rowIdx, rowIdx, colIdx, colIdx);
					XSSFDataValidation validation = (XSSFDataValidation) dvHelper.createValidation(dvConstraint, addressList);
					validation.setShowErrorBox(true);
					sheet.addValidationData(validation);
				}
			}
		}
		//sheet.setColumnWidth(header.getParamColumn(ItemNames.agent.ORGANIZATION), 50 * 256);
		FileOutputStream fos = new FileOutputStream(AppContext.getFilesDirPath() + "/agents.xlsx");
		wb.write(fos);
		wb.close();
		return getResultingUrl(getUrlBase() + "/" + AppContext.getFilesUrlPath() + "agents.xlsx", ResultType.redirect);
	}

}
