package ecommander.model.datatypes;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import ecommander.common.Strings;
import ecommander.controllers.AppContext;


public class FileDataType extends StringDataType {

	private static final String SIZE_META = "size"; // размер файла
	private static final String CREATED_META = "created"; // дата создания файла
	private static final String EXTENSION_META = "extenstion"; // расширение файла
	
	public FileDataType(Type type) {
		super(type);
	}

	@Override
	public boolean isFile() {
		return true;
	}

	@Override
	public String outputValue(Object value, Object formatter) {
		if (value instanceof FileItem)
			return ((FileItem)value).getName();
		else if (value instanceof File)
			return ((File) value).getName();
		return super.outputValue(value, formatter);
	}

	@Override
	public boolean hasMeta() {
		return true;
	}

	@Override
	public HashMap<String, String> getMeta(Object value, Object... extraParams) {
		String parentPath = (String) extraParams[0];
		HashMap<String, String> meta = new HashMap<String, String>(3);
		if (value instanceof FileItem) {
			FileItem file = (FileItem) value;
			meta.put(SIZE_META, file.getSize() + "");
			meta.put(CREATED_META, DateDataType.DAY_FORMATTER.print(System.currentTimeMillis()));
			meta.put(EXTENSION_META, StringUtils.substringAfterLast(file.getName(), "."));
		} else  {
			try {
				Path file = null;
				if (value instanceof File) file = ((File) value).toPath();
				else file = new File(AppContext.getFilesDirPath() + parentPath + value).toPath();
				BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
				meta.put(SIZE_META, attr.size() + "");
				meta.put(CREATED_META, DateDataType.DATE_FORMATTER.print(new DateTime(attr.creationTime().toMillis())));
				meta.put(EXTENSION_META, StringUtils.substringAfterLast(file.getFileName().toString(), "."));
			} catch (IOException e) {
				return new HashMap<String, String>(0);
			}
		}
		return meta;
	}

	/**
	 * Получить название файла из объекта FileItem
	 * @param fileItem
	 * @return
	 */
	public static String getFileName(FileItem fileItem) {
		return getFileName(fileItem.getName());
	}
	/**
	 * Получить название файла из пути к файлу
	 * @param fileItem
	 * @return
	 */
	public static String getFileName(String fileName) {
		return Strings.translit(fileName.replaceFirst(".*[\\/]", ""));
	}
}
