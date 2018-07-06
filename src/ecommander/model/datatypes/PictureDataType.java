package ecommander.model.datatypes;

import ecommander.controllers.AppContext;
import ecommander.fwk.Strings;
import ecommander.fwk.WebClient;
import ecommander.model.Item;
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
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.HashMap;
import java.util.Iterator;


public class PictureDataType extends FileDataType {

	private static final String WIDTH_META = "width"; // размер файла
	private static final String HEIGHT_META = "height"; // размер файла
	private static final String SIZE_META = "size"; // размер файла
	private static final String CREATED_META = "created"; // дата создания файла
	private static final String EXTENSION_META = "extenstion"; // расширение файла

	public PictureDataType(Type type) {
		super(type);
	}

	@Override
	public HashMap<String, String> createMeta(Object value, Object... extraParams) {
		Item item = (Item) extraParams[0];
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
				Path file = null;
				if (value instanceof File)
					file = ((File) value).toPath();
				else if (value instanceof String)
					file = new File(getItemFilePath(item) + value).toPath();
				if (file == null || !Files.exists(file))
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
}
