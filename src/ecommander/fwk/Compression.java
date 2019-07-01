package ecommander.fwk;

import org.apache.commons.io.FileUtils;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;

public class Compression {
	public static void compress(ByteArrayInputStream bis, File file) throws IOException {
		FileOutputStream fos = new FileOutputStream(file);
		ZipOutputStream zipOut = new ZipOutputStream(fos);
		ZipEntry zipEntry = new ZipEntry(file.getName());
		zipOut.putNextEntry(zipEntry);
		byte[] bytes = new byte[1024];
		int length;
		while((length = bis.read(bytes)) >= 0) {
			zipOut.write(bytes, 0, length);
		}
		zipOut.close();
		bis.close();
		fos.close();
	}

	public static void decompress(File file, OutputStream os) throws IOException {
		byte[] buffer = new byte[1024];
		boolean isZip = false;
		try {
			ZipInputStream zis = new ZipInputStream(new FileInputStream(file));
			ZipEntry zipEntry = zis.getNextEntry();
			while (zipEntry != null) {
				isZip = true;
				int len;
				while ((len = zis.read(buffer)) > 0) {
					os.write(buffer, 0, len);
				}
				os.close();
				zipEntry = zis.getNextEntry();
			}
			zis.closeEntry();
			zis.close();
		} catch (ZipException zex) {
			isZip = false;
		}
		if (!isZip) {
			byte[] bytes = FileUtils.readFileToByteArray(file);
			os.write(bytes);
			os.close();
		}
	}
}
