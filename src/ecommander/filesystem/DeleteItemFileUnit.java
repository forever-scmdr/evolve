package ecommander.filesystem;

import java.io.File;
import java.util.ArrayList;

import ecommander.controllers.AppContext;
import ecommander.model.Item;
import ecommander.model.MultipleParameter;
import ecommander.model.SingleParameter;

/**
 * Удаляет файлы - множественные параметры, также удаляет значение соответствующего параметра
 * На самом деле перемещает файл во временную папку, чтобы его можно было восстановить потом
 * 
 * !!! Сохранение айтема с удаленным параметром не происходит !!!
 * @author EEEE
 *
 */
public class DeleteItemFileUnit extends ItemFileUnit {

	private int paramId;
	private int paramIndex;
	
	public DeleteItemFileUnit(Item item, int paramId, int paramIndex) {
		super(item);
		this.paramIndex = paramIndex;
		this.paramId = paramId;
	}

	public void execute() throws Exception {
		if (item.getItemType().getParameter(paramId).isMultiple()) {
			ArrayList<SingleParameter> aramArray = new ArrayList<SingleParameter>(
					((MultipleParameter) item.getParameter(paramId)).getValues());
			String paramValue = aramArray.get(paramIndex).outputValue();
			deleteFile(paramValue);
			// Удаление значения параметра в айтеме
			item.removeMultipleParamValue(paramId, paramIndex);
		} else {
			String paramValue = ((SingleParameter)item.getParameter(paramId)).outputValue();
			deleteFile(paramValue);
			item.clearParameter(paramId);
		}
	}

	private void deleteFile(String name) {
		String fileName = createParameterFileName(name);
		File file = new File(AppContext.getFilesDirPath() + fileName);
		if (file.exists()) {
			/* Временно файл просто удаляется TODO <low priority>
			// Создать директорию во временной папке
			String itemFilesDirectory = createItemFileDirectoryName(item.getId());
			new File(TEMP_FOLDER + itemFilesDirectory).mkdirs();
			boolean success = file.renameTo(new File(TEMP_FOLDER + fileName));
			if (!success) {
				throw new Exception("File '" + file.getName() + "' has not been moved successfully");
			}
			*/
			file.delete();
		}
	}
	
	public void rollback() throws Exception {
		/* Временно ничего не происходит TODO <low priority>
		if (item.getItemType().getParameter(paramName).isMultiple()) {
			File file = new File(FILES_FOLDER + fileName);
			if (file.exists()) {
				boolean success = file.renameTo(new File(FILES_FOLDER + fileName));
				if (!success) {
					throw new Exception("File '" + file.getName() + "' has not been moved successfully");
				}
			}
			// Установка нового значения параметра в айтеме
			item.setParameterDirect(paramName, fileName);	
		}
		*/
	}

}
