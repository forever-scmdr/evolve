package ecommander.services.filesystem;

import java.io.File;
import java.io.FileOutputStream;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLDecoder;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.StringUtils;

import ecommander.common.ServerLogger;
import ecommander.common.exceptions.FileException;
import ecommander.controllers.AppContext;
import ecommander.model.datatypes.FileDataType;
import ecommander.model.item.Item;
import ecommander.model.item.ParameterDescription;
import ecommander.model.item.SingleParameter;

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
//-- Надо решить удалять файл или нет если занчение параметра null
					Object value = param.getValue();
					if (value == null)
						continue;
					// Удаляется старый файл - старое значение параметра, если оно было
					for (Object oldVal : param.getOldValues()) {
						if (oldVal instanceof String) {
							File oldFile = new File(AppContext.getFilesDirPath() + fileDirectoryName + oldVal);
							// Удаление бэкапа (если есть)
							File  oldBackupFile =  new File(AppContext.getFilesDirPath()+ fileDirectoryName +"backup/" + oldVal);
							if(((String) oldVal).indexOf("[resized_to") != -1 && ((String) oldVal).indexOf("].") != -1){
								String resizeTrace = ((String) oldVal).substring(((String) oldVal).indexOf("[resized_to"), ((String) oldVal).indexOf("].")+1);
								oldBackupFile =  new File(AppContext.getFilesDirPath()+ fileDirectoryName +"backup/" + ((String) oldVal).replace(resizeTrace,""));
							}
							if(oldBackupFile.exists())
								oldBackupFile.delete();

							if (oldFile.exists())
								oldFile.delete();

						}
					}
//					Object value = param.getValue();
//					if (value == null)
//						continue;
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
						Pattern urlPattern = Pattern.compile("^(https?|ftp|file)://[-\\wА-Яа-я+&@#/%?=~|!:,.;]*[-\\wА-Яа-я+&@#/%=~|]");
						Matcher m = urlPattern.matcher((CharSequence) value);
						ReadableByteChannel rbc = null;
						FileOutputStream  fos = null;
						if (m.matches()) {
							try {
								URL webImg = new URL((String) value);
								String fName = URLDecoder.decode(webImg.getPath(), "UTF-8");
								fName = StringUtils.substringAfterLast(fName, "/");
								URLConnection conn = webImg.openConnection();
								conn.addRequestProperty("User-Agent", "Mozilla/4.0 (compatible; MSIE 6.0; Windows NT 5.1)");
								rbc = Channels.newChannel(conn.getInputStream());
								File folder = new File(AppContext.getFilesDirPath() + fileDirectoryName);
								folder.mkdirs();
								File newFile = new File(AppContext.getFilesDirPath() + fileDirectoryName + fName);
								fos = new FileOutputStream(newFile);
								fos.getChannel().transferFrom(rbc, 0, Long.MAX_VALUE);
								newValues.add((String) fName);
							} catch (Exception e) {
								e.printStackTrace();
							} finally{
								if(fos != null){fos.close();}
								if(rbc != null){rbc.close();}
							}
						} else {
							newValues.add((String) value);
						}
					}
				}
				// Замена объектов на имена файлов
				item.removeValue(paramDesc.getName());
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




}
