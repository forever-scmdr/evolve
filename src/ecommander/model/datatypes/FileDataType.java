package ecommander.model.datatypes;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;

import ecommander.model.Item;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import ecommander.fwk.Strings;
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
			return StringUtils.lowerCase(((FileItem)value).getName());
		else if (value instanceof File)
			return StringUtils.lowerCase(((File) value).getName());
		else if (value instanceof URL)
			return Strings.getFileName(((URL) value).getFile());
		return StringUtils.lowerCase(super.outputValue(value, formatter));
	}

	@Override
	public Object createValue(String stringValue, Object formatter) {
		return StringUtils.lowerCase(stringValue);
	}

	@Override
	public boolean hasMeta() {
		return true;
	}

	@Override
	public HashMap<String, String> createMeta(Object value, Object... extraParams) {
		Item item = (Item) extraParams[0];
		HashMap<String, String> meta = new HashMap<>(3);
		if (value instanceof FileItem) {
			FileItem file = (FileItem) value;
			meta.put(SIZE_META, file.getSize() + "");
			meta.put(CREATED_META, DateDataType.DAY_FORMATTER.print(System.currentTimeMillis()));
			meta.put(EXTENSION_META, StringUtils.substringAfterLast(file.getName(), "."));
		} else {
			try {
				Path file = null;
				if (value instanceof File)
					file = ((File) value).toPath();
				else if (value instanceof String)
					file = new File(getItemFilePath(item) + value).toPath();
				if (file == null || !Files.exists(file))
					return meta;
				BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
				meta.put(SIZE_META, attr.size() + "");
				meta.put(CREATED_META, DateDataType.DATE_FORMATTER.print(new DateTime(attr.creationTime().toMillis())));
				meta.put(EXTENSION_META, StringUtils.substringAfterLast(file.getFileName().toString(), "."));
			} catch (IOException e) {
				return new HashMap<>(0);
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
		return Strings.getFileName(fileItem.getName());
	}

	/**
	 * Вернуть путь к текущей директории айтема
	 * @param item
	 * @return
	 */
	public static String getItemFilePath(Item item) {
		return AppContext.getFilesDirPath(item.isFileProtected()) + item.getRelativeFilesPath();
	}

	public static String getItemFileUrl(Item item) {
		return AppContext.getFilesUrlPath(item.isFileProtected()) + item.getRelativeFilesPath();
	}

	@Override
	public int getHashCode(Object value) {
		return outputValue(value, null).hashCode();
	}

	@Override
	public boolean getEquals(Object o1, Object o2) {
		return StringUtils.equalsIgnoreCase(outputValue(o1, null), outputValue(o2, null));
	}
}
