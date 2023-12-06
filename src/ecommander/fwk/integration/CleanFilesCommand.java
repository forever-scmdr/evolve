package ecommander.fwk.integration;

import ecommander.controllers.AppContext;
import ecommander.fwk.IntegrateBase;
import ecommander.model.Item;
import ecommander.persistence.itemquery.ItemQuery;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.stream.Stream;

public class CleanFilesCommand extends IntegrateBase {

	@Override
	protected boolean makePreparations() throws Exception {
		return true;
	}

	@Override
	protected void integrate() throws Exception {
		info.setOperation("Поиск всех директорий");
		info.setCurrentJob("Поиск всех директорий");
		info.addLog("Начало поиска всех директорий. Может занять до нескольких минут. Нет обратной связи");
		Collection<File> dirs = FileUtils.listFilesAndDirs(new File(AppContext.getCommonFilesDirPath()),
				FileFilterUtils.directoryFileFilter(), FileFilterUtils.trueFileFilter());
		info.addLog("Поиск всех директорий завершен");
		info.setOperation("Удаление ненужных файлов");
		info.setCurrentJob("Удаление ненужных файлов");
		info.addLog("Поиск файлов для удаления. Удаление найденных файлов");
		int toProcess = dirs.size();
		info.setProcessed(0);
		for (File dir : dirs) {
			info.setToProcess(toProcess--);
			if (StringUtils.endsWith(dir.getName(), "f")) {
				String fullName = dir.getAbsolutePath();
				String idWithShashes = StringUtils.substringAfter(fullName, new File(AppContext.getCommonFilesDirPath()).getName());
				String idStr = StringUtils.substringBefore(StringUtils.replace(idWithShashes, File.separator, ""), "f");
				long id;
				try {
					id = Long.parseLong(idStr);
				} catch (Exception e) {
					continue;
				}
				Item item = ItemQuery.loadById(id);
				if (item == null || item.getStatus() == Item.STATUS_DELETED) {
					if (!FileUtils.deleteQuietly(dir))
						info.pushError("Unable to delete directory", dir.getAbsolutePath());
				}
				info.increaseProcessed();
			}
		}
		info.addLog("Поиск файлов для удаления и удаление завершено");
		info.setOperation("Удаление ненужных директорий");
		info.setCurrentJob("Удаление ненужных директорий");
		boolean hasEmpty = true;
		while (hasEmpty) {
			hasEmpty = false;
			for (File dir : dirs) {
				if (isEmpty(dir.toPath())) {
					if (!FileUtils.deleteQuietly(dir)) {
						info.pushError("Unable to delete directory", dir.getAbsolutePath());
					} else {
						hasEmpty = true;
					}
				}
			}
		}
	}

	@Override
	protected void terminate() throws Exception {

	}

	public boolean isEmpty(Path path) {
		try {
			if (Files.isDirectory(path)) {
				try (Stream<Path> entries = Files.list(path)) {
					return !entries.findFirst().isPresent();
				}
			}
		} catch (Exception e) {
			return false;
		}
		return false;
	}
}
