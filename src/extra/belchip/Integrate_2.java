package extra.belchip;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.GroupPrincipal;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.PosixFileAttributes;
import java.nio.file.attribute.PosixFilePermission;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import ecommander.fwk.EcommanderException;
import ecommander.fwk.ServerLogger;
import ecommander.fwk.XmlDocumentBuilder;
import ecommander.model.DataModelBuilder;
import ecommander.model.Item;
import ecommander.model.ItemTypeRegistry;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.commandunits.CleanAllDeletedItemsDBUnit;
import ecommander.persistence.commandunits.ItemStatusDBUnit;
import ecommander.persistence.commandunits.SaveItemDBUnit;
import extra._generated.Catalog;
import extra._generated.ItemNames;
import org.apache.commons.lang3.StringUtils;


import ecommander.controllers.AppContext;
import ecommander.controllers.PageController;
import ecommander.persistence.itemquery.ItemQuery;
import ecommander.persistence.mappers.LuceneIndexMapper;

/**
 * Интеграция файла XML Результаты валидации и выполнения в след. виде
 * 
 * 
 * <page> <message>Валидация показала наличие следующих ошибок. Интеграция не произведена</message> <error line="10" coloumn="30">Сообщение об
 * ошибке 1</error> <error line="20" coloumn="30">Сообщение об ошибке 2</error> .............. <error line="500" coloumn="40">Сообщение об ошибке
 * 50</error> </page>
 * 
 * 
 * @author EEEE
 * 
 */
public class Integrate_2 extends Command implements CleanAllDeletedItemsDBUnit.DeleteInformer {

	/**
	 * Основвные элементы - айтемы
	 */
	private static final Object MUTEX = new Object();
	private static volatile boolean isInProgress = false;
	private static volatile Info info = new Info();
	private File integration;
	private int deletedCount = 0;
	private int deletedBase = 0;

	public static class Error {
		public final String message;
		public final int lineNumber;
		public final int position;
		public final String originator;

		private Error(String message, int lineNumber, int position) {
			this.message = message;
			this.lineNumber = lineNumber;
			this.position = position;
			this.originator = null;
		}

		private Error(String message, String originator) {
			this.message = message;
			this.lineNumber = -1;
			this.position = -1;
			this.originator = originator;
		}

		public boolean isLineNumber() {
			return lineNumber != -1;
		}
	}

	private Item catalog; // Корневой айтем каталога продукции

	@Override
	public ResultPE execute() throws Exception {
		String op = getVarSingleValue("action");
		if (isInProgress || StringUtils.isEmpty(op) || !op.equals("start")) {
			return buildResult();
		} else {
			synchronized (MUTEX) {
				isInProgress = true;
				info = new Info();
				try {
					// Загрузить каталог продукции
					List<Item> catalogs = new ItemQuery(ItemNames.CATALOG).loadItems();
					if (catalogs.size() != 1) {
						info.addError("Каталог должен быть создан и содрежать файл интеграции", "");
						isInProgress = false;
						return buildResult();
					}
					catalog = catalogs.get(0);
					// Проверить, есть ли файл для интеграции
					integration = catalog.getFileValue("integration", AppContext.getFilesDirPath(catalog.isFileProtected()));
					if (integration == null || !integration.exists()) {
						info.addError("Не найден файл интеграции", "");
						isInProgress = false;
						info.finish();
						return buildResult();
					}

					final Integrate_2 integrate = this;

					// Создаем поток.
					Thread integrationThread = new Thread(new Runnable() {
						public void run() {
							try {
								// Прасить документ
								SAXParserFactory factory = SAXParserFactory.newInstance();
								SAXParser parser = factory.newSAXParser();
								// Подсчет строк
								parser.parse(integration, new LineCounter(info));
								// Валидация
								info.setOperation("Валидация файла интеграции");
								info.addMessage("Начало процесса валидации");
								if (info.getErrorsCount() == 0)
									parser.parse(integration, new ValidationHandler(catalog, info));
								// Удаление каталога и файла с классами

								catalog.setValueUI(Catalog.INTEGRATION_PENDING, "1");
								executeAndCommitCommandUnits(SaveItemDBUnit.get(catalog).ignoreUser(true));

								info.addMessage("Валидация завершена. Удаление старого каталога");
								info.setOperation("Удаление старого каталога");
								info.setProductsCreated(0);
								// Удалить все разделы
								List<Item> sections = new ItemQuery(ItemNames.SECTION).setParentId(catalog.getId(), false)
										.loadItems();
								for (Item section : sections) {
									integrate.deletedBase += integrate.deletedCount;
									integrate.deletedCount = 0;
									ItemStatusDBUnit delete = ItemStatusDBUnit.delete(section);
									executeAndCommitCommandUnits(delete.ignoreUser(true));
								}
								// Удаление файла классов
								File modelFile = new File(AppContext.getUserModelPath());
								modelFile.delete();
								info.setProductsCreated(0);
								info.addMessage("Каталог удален. Начало создания классов продуктов и разделов каталога");
								info.setOperation("Создание разделов каталога, фильтров и классов продукции");
								// Постороение списка классов с параметрами
								try {
									//enableModelCustomGroupWrite();
									ItemTypeRegistry.lock();
									if (info.getErrorsCount() == 0)
										parser.parse(integration, new ProductClassHandler(catalog, info));
									DataModelBuilder.newForceUpdate().tryLockAndReloadModel();
								} catch (Exception e) {
									ServerLogger.error("Error updating user model", e);
									ItemTypeRegistry.unlockRollback();
								} finally {
									ItemTypeRegistry.unlockSumbit();
									//enableModelCustomGroupWrite();
								}
								info.addMessage("Создание разделов и классов завершено. Начало создания продукции");
								info.setOperation("Создание продукции каталога");
								// Запись товаров в каталог
								if (info.getErrorsCount() == 0)
									parser.parse(integration, new CatalogCreationHandler(catalog, info));
								info.setOperation("Заполнение аналогов и сопутствующих товаров");
								File analogList = catalog.getFileValue(Catalog.ANALOGS, AppContext.getFilesDirPath(catalog.isFileProtected()));
								if (analogList.exists() && analogList.isFile()) {
									new AnalogHandler(info).parse(analogList.toPath());
								} else {
									info.addMessage("Список аналогов не найден.");
								}
								info.setOperation("Текстовая индексация");
								info.addMessage("Создание продукции завершено. Начало текстовой индексации");
								LuceneIndexMapper.getSingleton().reindexAll();
								info.addMessage("Индексация завершена");
								// Установление даты обновления каталога
								long h = (long) 3 * 60 * 60 * 1000;
								catalog.setValue(Catalog.DATE, System.currentTimeMillis() + h);
								catalog.setValueUI(Catalog.INTEGRATION_PENDING, "0");
								executeAndCommitCommandUnits(SaveItemDBUnit.get(catalog).ignoreUser(true));
								PageController.clearCache();
								info.finish();
							} catch (Exception e) {
								ServerLogger.error("Integration error", e);
								String exceptionMessage = (StringUtils.isBlank(e.getMessage())) ? e.toString() : e.getMessage();
								info.addError(exceptionMessage, Thread.currentThread().getStackTrace()[1].getClassName());
							} finally {
								isInProgress = false;
							}
						}
					});

					integrationThread.setName("Belchip big integration");
					integrationThread.start();

				} catch (Exception se) {
					ServerLogger.error("Integration error", se);
					// errors.add(new Error(se.getMessage(), 0, 0));
				}
				return buildResult();
			}
		}
	}

	private ResultPE buildResult() throws EcommanderException, IOException {
		XmlDocumentBuilder doc = XmlDocumentBuilder.newDoc();
		doc.startElement("page");
		info.output(doc);
		doc.endElement();
		ResultPE result = null;
		try {
			result = getResult("success");
		} catch (EcommanderException e) {
			ServerLogger.error("no result found", e);
			return null;
		}
		result.setValue(doc.toString());
		return result;
	}

	public static class Info {
		private static final Format TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");
		private ArrayList<Error> errors = new ArrayList<Error>();
		private ArrayList<LogMessage> log = new ArrayList<LogMessage>();
		private boolean refresh = true;
		private String currentOperation = "инициализация";
		private int totalLineCount;
		private int currentLine = 0;
		private int sectionsCreated;
		private int productsCreated;

		private synchronized void output(XmlDocumentBuilder doc) throws IOException {
			if (refresh) {
				doc.startElement("refresh").endElement();
			}
			doc.startElement("operation").addText(currentOperation).endElement();
			doc.startElement("line").addText(currentLine).endElement();
			doc.startElement("total-line-number").addText(totalLineCount).endElement();
			doc.startElement("sections-created").addText(sectionsCreated).endElement();
			doc.startElement("products-created").addText(productsCreated).endElement();
			doc.startElement("items-indexed").addText(LuceneIndexMapper.getSingleton().getCountProcessed()).endElement();

			for (LogMessage msg : log) {
				doc.startElement("message", "time", TIME_FORMAT.format(msg.date)).addText(msg.message).endElement();
			}
			if (errors.size() != 0) {
				doc.startElement("message").addText("Во время интеграции произошли ошибки. Процесс интеграции был прерван.").endElement();
				doc.startElement("message").addText("Список ошибок:").endElement();
				for (Error e : errors) {
					doc.startElement("error", "line", e.lineNumber, "coloumn", e.position).addText(e.message).endElement();
				}
			}
			ServerLogger.debug(doc.toString());
		}

		public synchronized int getErrorsCount() {
			return errors.size();
		}

		private synchronized void finish() {
			currentLine = 0;
			totalLineCount = 0;
			currentOperation = "Интеграция завершена.";
			refresh = false;
		}

		public synchronized void setCurrentLine(int n) {
			currentLine = n;
		}

		public synchronized void addMessage(String msg) {
			log.add(new LogMessage(msg));
		}

		public synchronized void setOperation(String operationName) {
			currentOperation = operationName;
		}

		public synchronized void setLineCount(int n) {
			totalLineCount = n;
		}

		public void addError(String message, int lineNumber, int position) {
			errors.add(new Error(message, lineNumber, position));
		}

		public void addError(String message, String originator) {
			errors.add(new Error(message, originator));
		}

		public synchronized void setSectionsCreated(int n) {
			sectionsCreated = n;
		}

		public synchronized void setProductsCreated(int n) {
			productsCreated = n;
		}
	}

	private static final class LogMessage {
		private Date date;
		private String message;

		private LogMessage(String message) {
			this.date = new Date();
			this.message = message;
		}
	}

	private void enableModelCustomGroupWrite() throws Exception {
		Path modelCustomFolder = Paths.get(AppContext.getModelPath());
		Path modelCustomFile = Paths.get(AppContext.getModelPath(), "model_custom.xml");
		
		if(!modelCustomFile.toFile().isFile()) {info.addMessage("model_custom.xml does not exist."); return;}
		//UserPrincipal owner = Files.getOwner(modelCustomFolder);
		GroupPrincipal group = Files.readAttributes(modelCustomFolder, PosixFileAttributes.class, LinkOption.NOFOLLOW_LINKS).group();

		//Files.setOwner(modelCustomFile, owner);
		Files.getFileAttributeView(modelCustomFile, PosixFileAttributeView.class, LinkOption.NOFOLLOW_LINKS).setGroup(group);

		HashSet<PosixFilePermission> perms = new HashSet<PosixFilePermission>();
		perms.add(PosixFilePermission.GROUP_READ);
		perms.add(PosixFilePermission.GROUP_WRITE);
		perms.add(PosixFilePermission.GROUP_EXECUTE);

		perms.add(PosixFilePermission.OWNER_READ);
		perms.add(PosixFilePermission.OWNER_WRITE);
		perms.add(PosixFilePermission.OWNER_EXECUTE);

		perms.add(PosixFilePermission.OTHERS_READ);
		perms.add(PosixFilePermission.OTHERS_EXECUTE);

		Files.setPosixFilePermissions(modelCustomFile, perms);

	}

	public void receiveDeletedCount(int deletedCount) {
		this.deletedCount = deletedCount;
		info.setProductsCreated(this.deletedBase + deletedCount);
	}

}