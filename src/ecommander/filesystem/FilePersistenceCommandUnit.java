package ecommander.filesystem;

import ecommander.controllers.AppContext;
import ecommander.fwk.Strings;
import ecommander.model.Item;
import ecommander.persistence.common.PersistenceCommandUnit;
import ecommander.persistence.common.TransactionContext;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Команда для файлов. TransactionContext не используется
 * @author EEEE
 */
public abstract class FilePersistenceCommandUnit implements PersistenceCommandUnit {

	public FilePersistenceCommandUnit() {

	}

	public TransactionContext getTransactionContext() {
		return null;
	}

	public void setTransactionContext(TransactionContext context) {
		// ничего не делать
	}

	protected static boolean isDirEmpty(final Path directory) throws IOException {
		try(DirectoryStream<Path> dirStream = Files.newDirectoryStream(directory)) {
			return !dirStream.iterator().hasNext();
		}
	}

	public static void moveItemFiles(String fromDirectory, String toDirectory, long...itemId) throws IOException {
		for (long id : itemId) {
			String pureSrcStr = Item.createItemFilesPath(id);
			Path srcPath = Paths.get(fromDirectory + pureSrcStr);
			if (!Files.exists(srcPath))
				continue;
			Path pureSrc = Paths.get(pureSrcStr);
			Path destPath = Paths.get(toDirectory).resolve(pureSrc);
			Files.createDirectories(destPath);
			FileUtils.copyDirectory(srcPath.toFile(), destPath.toFile());
			deleteItemDirectory(fromDirectory, id);
		}
	}
	/**
	 * Удаляет директорию айтема.
	 * Если при удалении директории в родительской не осталось ни одного файла и директории, то удалить и родительскую,
	 * предыдущую родительскоу и т.д.
	 * @return
	 */
	public static void deleteItemDirectory(String baseDirectory, long...itemId) throws IOException {
		for (long id : itemId) {
			Path itemDir = Paths.get(createItemFilesDirectoryName(baseDirectory, id));
			FileUtils.deleteQuietly(itemDir.toFile());
			boolean deleted = true;
			while (deleted) {
				itemDir = itemDir.getRoot().resolve(itemDir.subpath(0, itemDir.getNameCount() - 1));
				if (Files.exists(itemDir) && isDirEmpty(itemDir)) {
					Files.delete(itemDir);
					deleted = true;
				} else {
					deleted = false;
				}
			}
		}
	}

	public static String createItemFilesDirectoryName(String basePath, long itemId) {
		return basePath + Item.createItemFilesPath(itemId);
	}

	public static void main(String[] args) {
		long[] ids = new long[] {11, 150, 1122, 1234, 1156, 224455, 123456789, 112334, 1123344, 1123345, 112333};
		String src = "G:/test/files/";
		String dest = "G:/test/hidden/";
		try {
			for (long id : ids) {
				Path itemDir = Paths.get(createItemFilesDirectoryName(src, id));
				Files.createDirectories(itemDir);
				Files.createFile(itemDir.resolve("file1.txt"));
				Files.createFile(itemDir.resolve("file2.txt"));
				Files.createFile(itemDir.resolve("file3.txt"));
			}
			moveItemFiles(src, dest, ids);
			System.out.println("complete");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}