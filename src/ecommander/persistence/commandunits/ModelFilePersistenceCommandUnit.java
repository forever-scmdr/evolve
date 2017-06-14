package ecommander.persistence.commandunits;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.sql.SQLException;
import java.util.ArrayList;

import org.apache.commons.lang3.StringUtils;

import ecommander.pages.output.MetaDataWriter;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.persistence.common.PersistenceCommandUnit;
import ecommander.persistence.common.TransactionContext;

/**
 * Абстрактный класс для команд базы данных
 * @author EEEE
 *
 */
abstract class ModelFilePersistenceCommandUnit implements PersistenceCommandUnit {
	
	public static final String EXPAND_MARK = "<!--EXP-->";
	public static final String START_MARK = "\n<!--START_$-->";
	public static final String END_MARK = "<!--END_$-->";
	
	private String fileContents; // содержимое файла в виде строки 
	private ArrayList<PersistenceCommandUnit> executedCommands;
	private String fileName; // путь к файлу с айтемами
	
	protected abstract String getFileName();
	
	protected abstract String getRootElementName();
	
	protected ModelFilePersistenceCommandUnit() {
		fileName = getFileName();
	}
	
	public final void execute() throws Exception {
		Path path = Paths.get(fileName);
		if (Files.exists(path)) {
			Charset utf8 = Charset.forName("UTF-8");
			byte[] bytes = Files.readAllBytes(path);
			fileContents = new String(bytes, utf8);
		}
		if (StringUtils.isBlank(fileContents)) {
			XmlDocumentBuilder doc = XmlDocumentBuilder.newDoc();
			doc.startElement(getRootElementName());
			doc.addComment(EXPAND_MARK);
			doc.endElement();
			fileContents = doc.toString();
		}
		
		executeInt();
	}
	/**
	 * Создание резервной копии файла модели
	 * @throws IOException
	 */
	public final void backup() throws IOException {
		Path path = Paths.get(fileName);
		if (Files.exists(path)) {
			Path backupPath = path.resolveSibling("~" + path.getFileName());
			Files.copy(path, backupPath, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
		}
	}
	/**
	 * Восстановление файла модели из резервной копии
	 * @throws IOException
	 */
	public final void restore() throws IOException {
		Path path = Paths.get(fileName);
		Path backupPath = path.resolveSibling("~" + path.getFileName());
		if (Files.exists(backupPath)) {
			Files.copy(backupPath, path, StandardCopyOption.COPY_ATTRIBUTES, StandardCopyOption.REPLACE_EXISTING);
		}
	}
	
	public TransactionContext getTransactionContext() {
		return null;
	}

	public void setTransactionContext(TransactionContext context) {
	
	}

	public final void rollback() throws Exception {
		if (executedCommands != null)
			for (PersistenceCommandUnit commandUnit : executedCommands) {
				commandUnit.rollback();
			}
		rollbackSelf();
	}

	protected void rollbackSelf() {
		// Переопределять при надобности
	}

	protected void executeCommand(ModelFilePersistenceCommandUnit commandUnit) throws Exception {
		if (executedCommands == null)
			executedCommands = new ArrayList<PersistenceCommandUnit>();
		commandUnit.execute();
		executedCommands.add(commandUnit);
	}

	protected abstract void executeInt() throws Exception;
	/**
	 * Сохранить в файл текущее содержимое
	 * @throws IOException
	 */
	protected final void saveFile() throws IOException {
		Files.write(Paths.get(fileName), fileContents.getBytes(StandardCharsets.UTF_8));
	}
	
	protected final String getFileContents() {
		return fileContents;
	}
	
	protected final void setFileContents(String str) {
		fileContents = str;
	}
	
	protected final String getStartMark(String entityName) {
		return StringUtils.replace(START_MARK, "$", entityName);
	}
	
	protected final String getEndMark(String entityName) {
		return StringUtils.replace(END_MARK, "$", entityName);
	}
	/**
	 * Установить имя файла для записи айтемов (по умолчению - файл для пользовательских айтемов,
	 * взятый из settings.properties)
	 * @param fileName
	 */
	public final void setFileName(String fileName) {
		this.fileName = fileName;
	}
	/**
	 * Создать строку, представляющую XML определение одного айтема
	 * @param writer
	 * @param entityName
	 * @return
	 * @throws SQLException
	 */
	protected final XmlDocumentBuilder writeEntity(MetaDataWriter writer, String entityName) throws SQLException {
		return writeEntity(null, writer, entityName);
	}
	/**
	 * Создать строку, представляющую XML определение одного айтема
	 * @param xml
	 * @param writer
	 * @param entityName
	 * @return
	 * @throws SQLException
	 */
	protected final XmlDocumentBuilder writeEntity(XmlDocumentBuilder xml, MetaDataWriter writer, String entityName) throws SQLException {
		if (xml == null)
			xml = XmlDocumentBuilder.newDocPart();
		xml.addComment(getStartMark(entityName));
		writer.write(xml);
		return xml.addComment(getEndMark(entityName));
	}
}