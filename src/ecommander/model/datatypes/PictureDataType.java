package ecommander.model.datatypes;

import ecommander.controllers.AppContext;
import ecommander.fwk.Strings;
import org.apache.commons.fileupload.FileItem;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.FileImageInputStream;
import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.MemoryCacheImageInputStream;
import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Iterator;


public class PictureDataType extends StringDataType {

	private static final String WIDTH_META = "width"; // размер файла
	private static final String HEIGHT_META = "height"; // размер файла
	private static final String SIZE_META = "size"; // размер файла
	private static final String CREATED_META = "created"; // дата создания файла
	private static final String EXTENSION_META = "extenstion"; // расширение файла

	public PictureDataType(Type type) {
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
		HashMap<String, String> meta = new HashMap<>(3);
		if (value instanceof FileItem) {
			try {
				FileItem file = (FileItem) value;
				Dimension size = getImageDimension(file.getName(), file.getInputStream());
				meta.put(WIDTH_META, size.width + "");
				meta.put(HEIGHT_META, size.height + "");
				meta.put(SIZE_META, file.getSize() + "");
				meta.put(CREATED_META, DateDataType.DAY_FORMATTER.print(System.currentTimeMillis()));
				meta.put(EXTENSION_META, StringUtils.substringAfterLast(file.getName(), "."));
			} catch (IOException e) {
					return new HashMap<>(0);
			}
		} else {
			try {
				Path file;
				if (value instanceof File)
					file = ((File) value).toPath();
				else
					file = new File(AppContext.getCommonFilesDirPath() + parentPath + value).toPath();
				if (!Files.exists(file))
					file = new File(AppContext.getProtectedFilesDirPath() + parentPath + value).toPath();
				if (!Files.exists(file))
					return meta;
				FileInputStream is = new FileInputStream(file.toFile());
				Dimension size = getImageDimension(file.getFileName().toString(), is);
				BasicFileAttributes attr = Files.readAttributes(file, BasicFileAttributes.class);
				meta.put(WIDTH_META, size.width + "");
				meta.put(HEIGHT_META, size.height + "");
				meta.put(SIZE_META, attr.size() + "");
				meta.put(CREATED_META, DateDataType.DATE_FORMATTER.print(new DateTime(attr.creationTime().toMillis())));
				meta.put(EXTENSION_META, StringUtils.substringAfterLast(file.getFileName().toString(), "."));
			} catch (IOException e) {
				return new HashMap<>(0);
			}
		}

		return meta;
	}


	public static Dimension getImageDimension(String fileName, InputStream picInputStream) throws IOException {
		int pos = fileName.lastIndexOf(".");
		if (pos == -1)
			throw new IOException("No extension for file: " + fileName);
		String suffix = fileName.substring(pos + 1);
		Iterator<ImageReader> iter = ImageIO.getImageReadersBySuffix(suffix);
		while(iter.hasNext()) {
			ImageReader reader = iter.next();
			try {
				MemoryCacheImageInputStream stream = new MemoryCacheImageInputStream(picInputStream);
				reader.setInput(stream);
				int width = reader.getWidth(reader.getMinIndex());
				int height = reader.getHeight(reader.getMinIndex());
				return new Dimension(width, height);
			} catch (IOException e) {
				throw new IOException("Error reading: " + fileName, e);
			} finally {
				reader.dispose();
			}
		}
		throw new IOException("Not a known image file: " + fileName);
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
	 * @param fileName
	 * @return
	 */
	public static String getFileName(String fileName) {
		return Strings.translit(fileName.replaceFirst(".*[\\/]", ""));
	}
}
