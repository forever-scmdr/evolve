package ecommander.filesystem;

import ecommander.model.Item;
import ecommander.persistence.common.PersistenceCommandUnit;
import ecommander.persistence.common.TransactionContext;
import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * Команда для файлов. TransactionContext не используется
 * @author EEEE
 */
public abstract class ItemDirectoryCommandUnit implements PersistenceCommandUnit {

	public ItemDirectoryCommandUnit() {

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

	public static void moveItemDirectory(String fromDirectory, String toDirectory, long...itemId) throws IOException {
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
	public static boolean deleteItemDirectory(String baseDirectory, long...itemId) throws IOException {
		boolean wereDeletions = false;
		for (long id : itemId) {
			Path basePath = Paths.get(baseDirectory);
			Path itemPath = Paths.get(Item.createItemFilesPath(id));
			boolean deleted = FileUtils.deleteQuietly(basePath.resolve(itemPath).toFile());
			wereDeletions |= deleted;
			while (deleted && itemPath.getNameCount() > 1) {
				itemPath = itemPath.subpath(0, itemPath.getNameCount() - 1);
				Path completePath = basePath.resolve(itemPath);
				if (Files.exists(completePath) && isDirEmpty(completePath)) {
					Files.delete(completePath);
					deleted = true;
				} else {
					deleted = false;
				}
			}
		}
		return wereDeletions;
	}

	public static String createItemFilesDirectoryName(String basePath, long itemId) {
		return basePath + Item.createItemFilesPath(itemId);
	}
/*
	public static void main(String[] args) {
		long[] ids = new long[] {11, 150, 1122, 1234, 1156, 224455, 123456789, 112334, 1123344, 1123345, 112333};
		String src = "I:/test/files/";
		String dest = "I:/test/hidden/";
		try {
			for (long id : ids) {
				Path itemDir = Paths.get(createItemFilesDirectoryName(src, id));
				Files.createDirectories(itemDir);
				Files.createFile(itemDir.resolve("file1.txt"));
				Files.createFile(itemDir.resolve("file2.txt"));
				Files.createFile(itemDir.resolve("file3.txt"));
			}
			moveItemDirectory(src, dest, ids);
			System.out.println("complete");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

 */
}