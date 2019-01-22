package ecommander.model.datatypes;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;

import ecommander.model.Item;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import ecommander.fwk.Strings;
import ecommander.controllers.AppContext;


public class FileDataType extends StringDataType {

	public static class BufferedPic {
		public BufferedPic(BufferedImage pic, String name, String type) {
			this.pic = pic;
			this.name = name;
			this.type = type;
		}
		public final BufferedImage pic;
		public final String name;
		public final String type;
	}

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
		else if (value instanceof BufferedPic)
			return Strings.getFileName(((BufferedPic) value).name);
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
		} else if (value instanceof URL) {
			return meta;
		} else if (value instanceof BufferedPic) {
			BufferedPic file = (BufferedPic) value;
			meta.put(CREATED_META, DateDataType.DAY_FORMATTER.print(System.currentTimeMillis()));
			meta.put(EXTENSION_META, StringUtils.substringAfterLast(file.name, "."));
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

	/**
	 * Вернуть путь к файлу (файл) с известным именем, который лежит в директории айтема
	 * @param item
	 * @param fileName
	 * @return
	 */
	public static Path getItemFile(Item item, String fileName) {
		return Paths.get(getItemFilePath(item) + fileName);
	}

	/**
	 * Получить урл для файла определенного айтема (как он будет выдаваться сервером)
	 * @param item
	 * @param fileName
	 * @return
	 */
	public static String getItemFileUrl(Item item, String fileName) {
		return getItemFileUrl(item) + fileName;
	}

	@Override
	public int getHashCode(Object value) {
		return outputValue(value, null).hashCode();
	}

	@Override
	public boolean getEquals(Object o1, Object o2) {
		if (!o1.getClass().equals(o2.getClass()))
			return false;
		if (o1 instanceof FileItem) {
			return StringUtils.equalsIgnoreCase(((FileItem) o1).getName(), ((FileItem) o2).getName());
		}
		else if (o1 instanceof File) {
			return StringUtils.equalsIgnoreCase(((File) o1).getAbsolutePath(), ((File) o2).getAbsolutePath());
		}
		else if (o1 instanceof URL) {
			return o1.equals(o2);
		}
		else if (o1 instanceof BufferedPic) {
			return StringUtils.equalsIgnoreCase(((BufferedPic) o1).name, ((BufferedPic) o2).name);
		}
		return StringUtils.equalsIgnoreCase(o1.toString(), o2.toString());
	}
}
