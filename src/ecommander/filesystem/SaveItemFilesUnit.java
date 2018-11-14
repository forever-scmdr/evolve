package ecommander.filesystem;

import ecommander.fwk.FileException;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.Strings;
import ecommander.fwk.WebClient;
import ecommander.model.Item;
import ecommander.model.ParameterDescription;
import ecommander.model.SingleParameter;
import ecommander.model.datatypes.FileDataType;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.regex.Pattern;

/**
 * Сохраняет все файлы - одиночные параметры айтема
 * @author EEEE
 * TODO <enhance> переделать для новой версии сервлетов
 * вызывать эту команду до апдейта параметров в индексе
 */
public class SaveItemFilesUnit extends SingleItemDirectoryFileUnit {

	private static final Pattern URL_PATTERN = Pattern.compile("^(https?|ftp|file)://[-\\wА-Яа-я+&@#/%?=~|!:,.;]*[-\\wА-Яа-я+&@#/%=~|]");
	//Matcher m = URL_PATTERN.matcher((CharSequence) value);
	//if (m.matches()) {

	private ArrayList<File> files;

	public SaveItemFilesUnit(Item item) {
		super(item);
		files = new ArrayList<>();
	}
	/**
	 * Перемещает файлы из директории загрузки в постоянную директорию и дает им новое название (c ID предков)
	 * Также устанавливает новое название в соответствующем параметре айтема
	 */
	public void execute() throws Exception {
		String fileDirectoryName = createItemDirectoryName();
		for (ParameterDescription paramDesc : item.getItemType().getParameterList()) {
			if (paramDesc.getDataType().isFile()) {
				// Пропустить параметры, которые не менялись
				if (!item.getParameter(paramDesc.getId()).hasChanged())
					continue;
				ArrayList<SingleParameter> params = new ArrayList<>();
				params.addAll(item.getParamValues(paramDesc.getName()));
				ArrayList<String> newValues = new ArrayList<>();
				HashSet<String> existingFileNames = new HashSet<>();
				for (int i = 0; i < params.size(); i++) {
					SingleParameter param = params.get(i);
					//-- Надо решить удалять файл или нет если значение параметра null
					Object value = param.getValue();
					if (value == null)
						continue;
					// Удаляется старый файл - старое значение параметра, если оно было
					for (Object oldVal : param.getOldValues()) {
						if (oldVal instanceof String && !StringUtils.equalsIgnoreCase(value.toString(), (String) oldVal)) {
							File oldFile = new File(fileDirectoryName + oldVal);
							if (oldFile.exists())
								oldFile.delete();
						}
					}
//					Object value = param.getValue();
//					if (value == null)
//						continue;
					boolean isUploaded = value instanceof FileItem;
					boolean isDirect = value instanceof File;
					boolean isUrl = value instanceof URL;
					// Если файл прикреплен, то он должен быть типа FileItem или типа File
					if (isUploaded || isDirect || isUrl) {
						// Если название файла содержит путь - удалить этот путь
						String fileName = null;
						if (isUploaded) {
							fileName = FileDataType.getFileName((FileItem) value);
						} else if (isDirect) {
							fileName = ((File) value).getName();
						} else if (isUrl) {
							fileName = Strings.getFileName(((URL) value).getFile());
						}
						// Проверка, добавлялся ли к этому параметру файл с таким именем ранее
						while (existingFileNames.contains(fileName)) {
							fileName = decorateFileName(paramDesc, i, fileName);
						}
						existingFileNames.add(fileName);
						// Создание новой директории
						File dir = new File(fileDirectoryName);
						dir.mkdirs();
						files.add(dir);
						File newFile = new File( fileDirectoryName + fileName);
						// Удаление файла, если он уже есть
						while (newFile.exists()) {
							/*
							if (!newFile.canWrite())
								throw new FileException("File '" + newFile.getName() + "' is write protected");
							newFile.delete();
							*/
							fileName = decorateFileName(paramDesc, i, fileName);
							newFile = new File( fileDirectoryName + fileName);
						}
						try {
							if (isUploaded)
								((FileItem) value).write(newFile);
							else if (isDirect)
								Files.copy(((File) value).toPath(), newFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
							else if (isUrl)
								WebClient.saveFile(value.toString(), fileDirectoryName, fileName);
						} catch (Exception e) {
							ServerLogger.error("File error", e);
							throw new FileException("File '" + newFile.getName() + "' has not been moved successfully");
						}
						files.add(newFile);
						newValues.add(fileName);
						item.removeEqualValue(paramDesc.getName(), value);
						item.setValueUI(paramDesc.getId(), fileName);
					} else {
						newValues.add((String) value);
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

	private String decorateFileName(ParameterDescription param, int index, String fileName) {
		String beforeDot = StringUtils.substringBeforeLast(fileName, ".");
		String afterDot = StringUtils.substringAfterLast(fileName, ".");
		String newFileName = param.getName() + "_" + beforeDot;
		if (index > 0)
			newFileName += "_" + index;
		if (StringUtils.isNotBlank(afterDot))
			newFileName += "." + afterDot;
		return newFileName;
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
