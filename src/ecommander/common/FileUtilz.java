package ecommander.common;

import java.io.File;
import java.io.IOException;
import java.util.Collection;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FalseFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;

public class FileUtilz {
	/**
	 * Удаляет все поддиректории заданной директории
	 * @param file
	 * @return
	 * @throws IOException 
	 */
	public static final boolean deleteSubdirs(File file) throws IOException {
		@SuppressWarnings("unchecked")
		Collection<File> subdirs = FileUtils.listFiles(file, FalseFileFilter.FALSE, TrueFileFilter.TRUE);
		boolean success = true;
		for (File subdir : subdirs) {
			success &= FileUtils.deleteQuietly(subdir);
		}
		return success;
	}
}
