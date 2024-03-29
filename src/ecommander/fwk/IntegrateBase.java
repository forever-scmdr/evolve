package ecommander.fwk;

import ecommander.model.Item;
import ecommander.pages.Command;
import ecommander.pages.ResultPE;
import ecommander.persistence.mappers.LuceneIndexMapper;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.helpers.MessageFormatter;

import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Интеграция файла XML Результаты валидации и выполнения в след. виде
 *
 *
 * <page> <message>Валидация показала наличие следующих ошибок. Интеграция не произведена</message> <error line="10" coloumn="30">Сообщение об
 * ошибке 1</error> <error line="20" coloumn="30">Сообщение об ошибке 2</error> .............. <error line="500" coloumn="40">Сообщение об ошибке
 * 50</error> </page>
 *
 * @author EEEE
 */
public abstract class IntegrateBase extends Command {

	/*******************************************
	 *
	 *          СТАТИЧЕСКИЕ ПОЛЯ
	 *
	 *******************************************/

	private static final Format TIME_FORMAT = new SimpleDateFormat("HH:mm:ss");

	private static final ConcurrentHashMap<String, IntegrateBase> runningTasks;
	private static volatile Info lastRunInfo;
	static {
		runningTasks = new ConcurrentHashMap<>();
	}


	/*********************************************************************************************************
	 *
	 *          ВЫВОД ИНФОРМАЦИИ О ПРОЦЕССЕ РАЗБОРА
	 *
	 *********************************************************************************************************/


	private static final class LogMessage {
		private Date date;
		private String message;

		private LogMessage(String message, Object... params) {
			this.date = new Date();
			this.message = MessageFormatter.arrayFormat(message, params).getMessage();
		}

		@Override
		public String toString() {
			return message;
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

		private Error(Throwable e, Info info) {
			message = ExceptionUtils.getStackTrace(e);
			lineNumber = info.lineNumber;
			position = info.position;
			originator = "";
		}

		@SuppressWarnings("unused")
		private boolean isLineNumber() {
			return lineNumber != -1;
		}
	}

	public static class Info {
		private volatile Timer timer = new Timer();

		private static final String _indexation = "Индексация названий товаров";

		private volatile String operation = "Инициализация";
		private volatile String currentJob = "Инициализация";
		private volatile int lineNumber = 0;
		private volatile int position = 0;
		private volatile int processed = 0;
		private volatile int toProcess = 0;
		private volatile ArrayDeque<LogMessage> log = new ArrayDeque<>();
		private volatile ArrayList<Error> errors = new ArrayList<>();
		private volatile boolean inProgress = false;
		private volatile int logSize = 300;
		private volatile TreeMap<Long, String> slowQueries = new TreeMap<>();
		private String host;

		public Info() {
			ServerLogger.debug("IntegrateBase.Info instance created");
		}

		public synchronized void setOperation(String opName) {
			operation = opName;
		}

		public synchronized void setCurrentJob(String currentJob) {
			this.currentJob = currentJob;
		}

		public synchronized void setLineNumber(int lineNumber) {
			this.lineNumber = lineNumber;
		}

		public synchronized void setLinePosition(int position) {
			this.position = position;
		}

		public synchronized void setProcessed(int processed) {
			this.processed = processed;
		}

		public synchronized void setToProcess(int toProcess) {
			this.toProcess = toProcess;
		}

		public synchronized int getProcessed() {
			return this.processed;
		}

		public synchronized int getToProcess() {
			return this.toProcess;
		}

		public synchronized void increaseProcessed() {
			this.processed++;
		}

		public synchronized void increaseProcessed(int procCount) {
			this.processed += procCount;
		}

		public synchronized void increaseLineNumber() {
			this.lineNumber++;
		}

		public synchronized void addLog(String message, Object... params) {
			if (log.size() >= logSize)
				log.removeFirst();
			log.addLast(new LogMessage(message, params));
		}

		public synchronized void pushLog(String message, Object... params) {
			if (log.size() >= logSize)
				log.removeLast();
			log.addFirst(new LogMessage(message, params));
		}

		public synchronized void limitLog(int size) {
			logSize = size;
		}

		public synchronized void addError(String message, int lineNumber, int position) {
			errors.add(new Error(message, lineNumber, position));
		}

		public synchronized void addError(Throwable e) {
			errors.add(new Error(e, this));
		}

		public synchronized void addError(String message, String originator) {
			errors.add(new Error(message, originator));
		}

		public synchronized void setInProgress(boolean inProgress) {
			this.inProgress = inProgress;
		}

		public synchronized void addSlowQuery(String queryLog, long nanos) {
			slowQueries.put(nanos, queryLog);
			if (slowQueries.size() > 100) {
				slowQueries.remove(slowQueries.firstKey());
			}
		}

		public String getHost() {
			return host;
		}

		public synchronized void output(XmlDocumentBuilder doc) throws IOException {
			doc.startElement("base").addText(host).endElement();
			doc.startElement("operation").addText(operation).endElement();
			doc.startElement("current_job").addText(currentJob).endElement();
			doc.startElement("line").addText(lineNumber).endElement();
			if (operation.equals(_indexation))
				doc.startElement("processed").addText(LuceneIndexMapper.getSingleton().getCountProcessed()).endElement();
			else {
				doc.startElement("to_process").addText(toProcess).endElement();
				doc.startElement("processed").addText(processed).endElement();
			}
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
			if (slowQueries.size() > 0) {
				doc.startElement("slow");
				for (Map.Entry<Long, String> entry : slowQueries.entrySet()) {
					doc
							.startElement("q")
							.startElement("log").addText(entry.getValue()).endElement()
							.startElement("time").addText(entry.getKey() / 1000000).endElement()
							.endElement();


				}
				doc.endElement();
			}
			doc.startElement("timers").addText(timer.writeTotals()).endElement();
			ServerLogger.debug(doc.toString());
		}

		public int getErrorCount() {
			return errors.size();
		}

		public synchronized void indexsationStarted() {
			operation = _indexation;
		}

		public Timer getTimer() {
			return timer;
		}
	}


	/*********************************************************************************************************
	 *
	 *          ПОЛЯ И МЕТОДЫ ЭКЗЕМПЛЯРА
	 *
	 *********************************************************************************************************/


	protected Info info = null;
	protected volatile boolean needTermination = false;
	protected volatile boolean isFinished = false;


	public IntegrateBase() {

	}

	public IntegrateBase(Command outer) {
		super(outer);
		if(IntegrateBase.class.isAssignableFrom(outer.getClass())){
			this.info = ((IntegrateBase)outer).getInfo();
		}
		if (this.info == null) {
			info = new Info();
		}
	}

	protected Info getInfo() {
		return info;
	}

	private Info newInfo() {
		info = new Info();
		return info;
	}

	/**
	 * Установить текущую операцию
	 *
	 * @param opName
	 */
	public void setOperation(String opName) {
		getInfo().setOperation(opName);
	}

	/**
	 * Установить текущий номер строки
	 *
	 * @param lineNumber
	 */
	public void setLineNumber(int lineNumber) {
		getInfo().setLineNumber(lineNumber);
	}

	/**
	 * Установить количество обработанных информационных единиц (например, товаров)
	 *
	 * @param processed
	 */
	public void setProcessed(int processed) {
		getInfo().setProcessed(processed);
	}

	/**
	 * Добавить запись в конец лога
	 *
	 * @param message
	 */
	public void addLog(String message) {
		getInfo().addLog(message);
	}

	/**
	 * Добавить запись в начало лога
	 *
	 * @param message
	 */
	public void pushLog(String message) {
		getInfo().pushLog(message);
	}

	/**
	 * Добавить ошибку с точным местом в файле интеграции
	 *
	 * @param message
	 * @param lineNumber
	 * @param position
	 */
	public void addError(String message, int lineNumber, int position) {
		getInfo().addError(message, lineNumber, position);
	}

	/**
	 * Добавить ошибку с неточным местом в файле интеграции
	 *
	 * @param message
	 * @param originator
	 */
	public void addError(String message, String originator) {
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
		final String CLASS_NAME = getClass().getName();
		boolean wantTerminate = StringUtils.equalsIgnoreCase("terminate", operation);
		boolean wantToStart = StringUtils.equalsIgnoreCase("start", operation);
		boolean isInProgress, mustTerminate, mustStart;
		IntegrateBase runningTask;
		synchronized (runningTasks) {
			runningTask = runningTasks.get(CLASS_NAME);
			isInProgress = runningTask != null && !runningTask.isFinished;
			mustTerminate = isInProgress && wantTerminate;
			mustStart = !isInProgress && wantToStart;
			if (mustStart) {
				runningTask = this;
				runningTasks.put(CLASS_NAME, this);
			}
		}
		if (mustStart) {
			newInfo().setInProgress(true);
			setOperation("Инициализация");
			lastRunInfo = getInfo();
			// Проверочные действия до начала разбора (проверка и загрузка файлов интеграции и т.д.)

			boolean readyToStart = false;
			try {
				readyToStart = makePreparations();
			} catch (Exception e) {
				ServerLogger.error(e.getMessage(), e);
				getInfo().addError(e.toString() + " says [ " + e.getMessage() + "]", -1,-1);
			}

			if (!readyToStart) {
				setOperation("Ошибка подготовительного этапа. Интеграция не может быть начата");
				runningTask.isFinished = true;
				return buildResult();
			}
			setOperation("Выполнение интеграции");
			// Поток выполнения интеграции
			Thread thread = new Thread(() -> {
				try {
					integrate();
					setOperation("Интеграция завершена успешно");
				} catch (Exception se) {
					setOperation("Интеграция завершена с ошибками");
					ServerLogger.error("Integration error", se);
					getInfo().addError(se.toString() + " says [ " + se.getMessage() + "]", info.lineNumber, info.position);
				} finally {
					isFinished = true;
					getInfo().setInProgress(false);
					//runningTasks.remove(CLASS_NAME);
				}
			});
			thread.setDaemon(true);
			if (async)
				thread.start();
			else
				thread.run();
		}
		if (mustTerminate) {
			runningTask.needTermination = true;
			runningTask.terminate();
			runningTask.isFinished = true;
			return runningTask.buildResult();
		} else if (isInProgress || !wantToStart || isFinished) {
			if (runningTask == null) {
				return buildResult();
			}
		}
		return runningTask.buildResult();
	}

	/**
	 * Действия до начала работы потока интеграции
	 */
	protected abstract boolean makePreparations() throws Exception;

	/**
	 * Сам процесс интеграции
	 */
	protected abstract void integrate() throws Exception;

	/**
	 * Действие по прерыванию команды
	 * Посылается команде
	 */
	protected abstract void terminate() throws Exception;

	/**
	 * Вызвать другую команду интеграции
	 * @param inner
	 * @throws Exception
	 */
	protected void executeOtherIntegration(IntegrateBase inner) throws Exception {
		try {
			inner.info = this.info;
			if (inner.makePreparations()) {
				inner.integrate();
			}
		} catch (Exception e) {
			inner.terminate();
			throw e;
		}
	}
	/**
	 * Создать результат выполнения команды (xml документ)
	 *
	 * @return
	 * @throws IOException
	 */
	private ResultPE buildResult() throws IOException {
		XmlDocumentBuilder doc = XmlDocumentBuilder.newDoc();
		doc.startElement("page", "name", getPageName());
		doc.startElement("base").addText(getUrlBase()).endElement();
		Info info = getInfo();
		if (info == null)
			info = lastRunInfo;
		if (info == null)
			info = newInfo();
		info.output(doc);
		doc.endElement();
		ResultPE result;
		try {
			result = getResult("complete");
		} catch (EcommanderException e) {
			ServerLogger.error("no result found", e);
			return null;
		}
		result.setValue(doc.toString());
		return result;
	}

	protected void addDebug(Item item) throws Exception {
		String thread = Thread.currentThread().getName() + " : " + Thread.currentThread().getId();
		StringBuilder debug = new StringBuilder();
		debug.append("Time: ").append(System.nanoTime()).append("\r\n");
		debug.append("Page: ").append(getPageName()).append("\r\n\r\n");
		for (StackTraceElement element : Thread.currentThread().getStackTrace()) {
			debug.append(element.toString()).append("\r\n");
		}
		item.setValueUI("thread", thread);
		item.setValueUI("debug", debug.toString());
	}

}