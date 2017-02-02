package ecommander.services.filesystem;

import java.io.File;
import java.util.ArrayList;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;

import ecommander.common.ServerLogger;
import ecommander.common.exceptions.FileException;
import ecommander.controllers.AppContext;
import ecommander.model.datatypes.FileDataType;
import ecommander.model.item.Item;
import ecommander.model.item.MultipleParameter;
import ecommander.model.item.SingleParameter;

/**
 * Временно сохраняет один из файлов множественного файлового параметра
 * @author EEEE
 * 
 * http://cwiki.apache.org/WW/handling-file-uploads.html
 */
public class SaveItemFileUnit extends ItemFileUnit {

	public static String MULTIPLE_FILE_INPUT_NAME = "multipleParamValue";
	
	private String uploadedFileName;
	private int paramId;
	private FileItem fileItem;
	
	public SaveItemFileUnit(Item item, int paramId, FileItem fileItem) {
		super(item);
		this.paramId = paramId;
		this.fileItem = fileItem;
	}
	
	/**
	 * Файл сохраняется во временной директории, а к айтему добавляется соответствующий параметр
	 */
	public void execute() throws Exception {
		if (fileItem != null) {
			// Если название файла содержит путь - удалить этот путь
			String fileName = FileDataType.getFileName(fileItem);
			String fileDirectoryName = item.getPredecessorsAndSelfPath();
			uploadedFileName = fileName;
			File newFile = new File(AppContext.getFilesDirPath() + fileDirectoryName + fileName);
			// Удаление файла, если он уже есть
			if (newFile.exists())
				newFile.delete();
			// Создание новой директории
			File itemFileDirectory = new File(AppContext.getFilesDirPath() + fileDirectoryName);
			if (!itemFileDirectory.exists() && !itemFileDirectory.mkdirs()) {
				throw new FileException("Can not create diractory '" + itemFileDirectory.getName() + "'");
			}
			ServerLogger.debug("Writing file '" + fileName + "' to '" + newFile + "'");
			try {
				fileItem.write(newFile);
			} catch (Exception e) {
				throw new FileException("File '" + newFile + "' has not been created successfully");
			}
			// !! Установка нового значения параметра в айтеме, т. к. только здесь определяется его значение
			// Проверка нужна в случае, если файл меняется на файл с таким же названием во множественном параметре
			// В этом случае файл автоматически перезаписывается и нужно чтобы не образовывалось повторное упоминание
			// его названии в параметре
			if (!item.getParameter(paramId).containsValue(fileName))
				item.setValueUI(paramId, fileName);			
		}

	}
	/**
	 * Удаляется файл из временной директории и удаляется параметр из айтема
	 */
	public void rollback() throws Exception {
		if (StringUtils.isBlank(uploadedFileName)) {
			// Находится индекс значения параметра для удаления
			MultipleParameter param = (MultipleParameter)item.getParameter(paramId);
			ArrayList<SingleParameter> paramArray = new ArrayList<SingleParameter>(param.getValues());
			int index = 0;
			for (;index < param.getValues().size(); index++) {
				if (paramArray.get(index).getValue().equals(uploadedFileName)) break;
			}
			(new File(AppContext.getFilesDirPath() + createParameterFileName(item.getValue(paramId).toString()))).delete();
			item.removeValue(paramId, index);			
		}
	}

}