package ecommander.filesystem;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;

import ecommander.fwk.ServerLogger;
import ecommander.fwk.FileException;
import ecommander.controllers.AppContext;
import ecommander.model.datatypes.FileDataType;
import ecommander.model.Item;
import ecommander.model.ParameterDescription;
import ecommander.model.SingleParameter;

/**
 * Сохраняет все файлы - одиночные параметры айтема
 * @author EEEE
 * TODO <enhance> переделать для новой версии сервлетов
 * TODO <fix> Сделать удаление старых файлов при замене их на новые (сейчас отсутствует). Загружать старые значения из StringIndex, 
 * 			  вызывать эту команду до апдейта параметров в индексе
 */
public class SaveItemFilesUnit extends ItemFileUnit {

	private ArrayList<File> files;
	
	public SaveItemFilesUnit(Item item) {
		super(item);
		files = new ArrayList<File>();
	}
	/**
	 * Перемещает файлы из директории загрузки в постоянную директорию и дает им новое название (c ID предков)
	 * Также устанавливает новое название в соответствующем параметре айтема
	 */
	public void execute() throws Exception {
		String fileDirectoryName = createItemFileDirectoryName(item.getId(), item.getPredecessorsPath());
		for (Iterator<ParameterDescription> iter = item.getItemType().getParameterList().iterator(); iter.hasNext();) {
			ParameterDescription paramDesc = iter.next();
			if (paramDesc.getDataType().isFile()) {
				ArrayList<SingleParameter> params = new ArrayList<SingleParameter>();
				params.addAll(item.getParamValues(paramDesc.getName()));
				ArrayList<String> newValues = new ArrayList<String>();
				for (SingleParameter param : params) {
					// Удаляется старый файл - старое значение параметра, если оно было
					for (Object oldVal : param.getOldValues()) {
						if (oldVal instanceof String) {
							File oldFile = new File(AppContext.getFilesDirPath() + fileDirectoryName + oldVal);
							if (oldFile.exists())
								oldFile.delete();
						}
					}
					Object value = param.getValue();
					if (value == null)
						continue;
					boolean isUploaded = value instanceof FileItem;
					boolean isDirect = value instanceof File;
					// Если файл прикреплен, то он должен быть типа FileItem или типа File
					if (isUploaded || isDirect) {
						// Если название файла содержит путь - удалить этот путь
						String fileName = null;
						if (isUploaded)
							fileName = FileDataType.getFileName((FileItem) value);
						else
							fileName = ((File) value).getName();
						// Создание новой директории
						File dir = new File(AppContext.getFilesDirPath() + fileDirectoryName);
						dir.mkdirs();
						files.add(dir);
						File newFile = new File(AppContext.getFilesDirPath() + fileDirectoryName + fileName);
						// Удаление файла, если он уже есть
						if (newFile.exists()) {
							if (!newFile.canWrite())
								throw new FileException("File '" + newFile.getName() + "' is write protected");
							newFile.delete();
						}
						try {
							if (isUploaded)
								((FileItem) value).write(newFile);
							else
								Files.copy(((File) value).toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
						} catch (Exception e) {
							throw new FileException("File '" + newFile.getName() + "' has not been moved successfully");
						}
						files.add(newFile);
						newValues.add(fileName);
						item.removeEqualValue(paramDesc.getName(), fileName);
						item.setValueUI(paramDesc.getId(), fileName);
					} else {
						newValues.add((String)value);
					}
				}
				// Замена объектов на имена файлов
				item.clearParameter(paramDesc.getName());
				for (String newVal : newValues) {
					item.setValue(paramDesc.getName(), newVal);
				}
			}
		}
	}
	/**
	 * Устанавливает старое значение для всех парамтеров файлов (без ID предков) 
	 * и удаляет файл из директории для файлов
	 */
	public void rollback() throws Exception {
		for (File file : files) {
			ServerLogger.debug("Deleting file '" + file.getAbsolutePath() + "' - " + FileUtils.deleteQuietly(file));
		}
	}

	public static void main(String[] args) {
	    try {
			String strDirectoy ="D:/test";
		    String strManyDirectories="D:/dir1/dir2/dir3/eeee";
		    (new File(strDirectoy)).mkdirs();
		    (new File(strManyDirectories)).mkdirs();	    	
	    } catch (Exception e) {
	    	System.out.println(e);
	    }

	}
	

}
