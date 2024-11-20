package ecommander.application.extra;

import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Date;

import ecommander.common.ServerLogger;
import ecommander.common.exceptions.EcommanderException;
import ecommander.controllers.output.XmlDocumentBuilder;
import ecommander.pages.elements.Command;
import ecommander.pages.elements.ResultPE;
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
public abstract class IntegrateBase extends Command {

	/*********************************************************************************************************
	 *********************************************************************************************************
	 * 
	 * ВЫВОД ИНФОРМАЦИИ О ПРОЦЕССЕ РАЗБОРА
	 * 
	 *********************************************************************************************************
	 *********************************************************************************************************/

	private static final Format TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

	private static final Object MUTEX = new Object();
	private static boolean isInProgress = false;
	protected static Info info = null;

	private static final class LogMessage {
		private Date date;
		private String message;

		private LogMessage(String message) {
			this.date = new Date();
			this.message = message;
		}
	}
	
	private static class Error {
		public final String message;
		public final int lineNumber;
		public final int position;
		public final String originator;

		private Error(String message, int lineNumber, int position) {
			this.message = message;
			this.lineNumber = lineNumber;
			this.position = position;
			this.originator = "";
		}

		private Error(String message, String originator) {
			this.message = message;
			this.lineNumber = -1;
			this.position = -1;
			this.originator = originator;
		}

		@SuppressWarnings("unused")
		private boolean isLineNumber() {
			return lineNumber != -1;
		}
	}

	protected static class Info {
		private static final String _indexation = "Индексация названий товаров";

		private String operation = "Инициализация";
		private int lineNumber = 0;
		private int processed = 0;
		private ArrayDeque<LogMessage> log = new ArrayDeque<LogMessage>();
		private ArrayList<Error> errors = new ArrayList<Error>();
		private boolean inProgress = false;

		private synchronized void setOperation(String opName) {
			operation = opName;
		}

		private synchronized void setLineNumber(int lineNumber) {
			this.lineNumber = lineNumber;
		}

		private synchronized void setProcessed(int processed) {
			this.processed = processed;
		}

		private synchronized void addLog(String message) {
			log.addLast(new LogMessage(message));
		}

		private synchronized void pushLog(String message) {
			log.addFirst(new LogMessage(message));
		}
		
		private synchronized void addError(String message, int lineNumber, int position) {
			errors.add(new Error(message, lineNumber, position));
		}

		private synchronized void addError(String message, String originator) {
			errors.add(new Error(message, originator));
		}
		
		private synchronized void setInProgress(boolean inProgress) {
			this.inProgress = inProgress;
		}
		
		private synchronized void output(XmlDocumentBuilder doc) throws IOException {
			doc.startElement("operation").addText(operation).endElement();
			doc.startElement("line").addText(lineNumber).endElement();
			if (operation.equals(_indexation))
				doc.startElement("processed").addText(LuceneIndexMapper.getCountProcessed()).endElement();
			else
				doc.startElement("processed").addText(processed).endElement();
			if (inProgress) {
				if (errors.size() == 0) {
					doc.startElement("message").addText("Ошибки (пока) не обнаружены").endElement();
				} else {
					doc.startElement("message").addText("Произошли следующие ошибки. Интеграция не может быть выполнена в полном объеме")
							.endElement();
				}
			} else {
				doc.startElement("message").addText("Интеграция в данный момент не выполняется. Результаты предыдущей интеграции ниже")
						.endElement();
			}
			for (LogMessage message : log) {
				doc.startElement("log", "time", TIME_FORMAT.format(message.date)).addText(message.message).endElement();
			}
			for (Error error : errors) {
				doc.startElement("error", "line", error.lineNumber, "coloumn", error.position, "originator", error.originator)
						.addText(error.message).endElement();
			}
			ServerLogger.debug(doc.toString());
		}
	}

	private static Info getInfo() {
		if (info != null)
			return info;
		return newInfo();
	}
	
	private static Info newInfo() {
		info = new Info();
		return info;
	}
	/**
	 * Установить текущую операцию
	 * @param op
	 */
	protected static void setOperation(String opName) {
		getInfo().setOperation(opName);
	}
	/**
	 * Установить текущий номер строки
	 * @param lineNumber
	 */
	protected static void setLineNumber(int lineNumber) {
		getInfo().setLineNumber(lineNumber);
	}
	/**
	 * Установить количество обработанных информационных единиц (например, товаров)
	 * @param processed
	 */
	protected static void setProcessed(int processed) {
		getInfo().setProcessed(processed);
	}
	/**
	 * Добавить запись в конец лога
	 * @param message
	 */
	protected static void addLog(String message) {
		getInfo().addLog(message);
	}
	/**
	 * Добавить запись в начало лога
	 * @param message
	 */	
	protected static void pushLog(String message) {
		getInfo().pushLog(message);
	}	
	/**
	 * Добавить ошибку с точным местом в файле интеграции
	 * @param message
	 * @param lineNumber
	 * @param position
	 */
	protected static void addError(String message, int lineNumber, int position) {
		getInfo().addError(message, lineNumber, position);
	}
	/**
	 * Добавить ошибку с неточным местом в файле интеграции
	 * @param message
	 * @param originator
	 */
	protected static void addError(String message, String originator) {
		getInfo().addError(message, originator);
	}	
	
	/*********************************************************************************************************
	 *********************************************************************************************************
	 * 
	 * САМ РАЗБОР ФАЙЛА
	 * 
	 *********************************************************************************************************
	 *********************************************************************************************************/

	@Override
	public final ResultPE execute() throws Exception {
		String operation = getVarSingleValue("action");
		boolean async = getVarSingleValueDefault("mode", "async").equalsIgnoreCase("async");
		// Если команда находитя в стадии выполнения - вернуть результат сразу (не запускать команду по новой)
		if (isInProgress || !"start".equals(operation)) {
			return buildResult();
		} else {
			synchronized (MUTEX) {
				isInProgress = true;
				newInfo().setInProgress(true);
				setOperation("Инициализация");
				// Проверочные действия до начала разбора (проверка и загрузка файлов интеграции и т.д.)
				if (!makePreparations()) {
					setOperation("Интеграция завершена с ошибками");
					return buildResult();
				}
				setOperation("Выполнение интеграции");
				// Поток выполнения интеграции
				Thread thread = new Thread(new Runnable() {
					public void run() {
						try {
							integrate();
							setOperation("Интеграция завершена успешно");
						} catch (Exception se) {
							setOperation("Интеграция завершена с ошибками");
							ServerLogger.error("Integration error", se);
							info.addError(se.getMessage(), 0, 0);
						} finally {
							try {
								LuceneIndexMapper.closeWriter();
							} catch (IOException e) {
								try {
									throw e;
								} catch (IOException e1) {
									e1.printStackTrace();
								}
							} finally {
								isInProgress = false;
								getInfo().setInProgress(false);
							}
							isInProgress = false;
						}
					}
				});
				if (async)
					thread.start();
				else
					thread.run();
			}
		}
		return buildResult();
	}
	/**
	 * Действия до начала работы потока интеграции
	 */
	protected abstract boolean makePreparations();
	/**
	 * Сам процесс интеграции
	 */
	protected abstract void integrate() throws Exception;
	/**
	 * Создать результат выполнения команды (xml документ)
	 * 
	 * @return
	 * @throws IOException
	 */
	private ResultPE buildResult() throws IOException {
		XmlDocumentBuilder doc = XmlDocumentBuilder.newDoc();
		doc.startElement("page");
		info.output(doc);
		doc.endElement();
		ResultPE result = null;
		try {
			result = getResult("complete");
		} catch (EcommanderException e) {
			ServerLogger.error("no result found", e);
			return null;
		}
		result.setValue(doc.toString());
		return result;
	}
}