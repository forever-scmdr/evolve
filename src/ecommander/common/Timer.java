package ecommander.common;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;

/**
 * Таймер для отслеживания времени выполнения различных действий
 * @author EEEE
 *
 */
public class Timer {
	public static final String REQUEST_PROCESS = "request_process";
	public static final String INIT = "init";
	public static final String GET_FROM_CACHE = "get_from_cache";
	public static final String GENERATE_CACHE = "generate_cache";
	public static final String LOAD_DB_ITEMS = "load_db_items";
	public static final String LOAD_LUCENE_ITEMS = "load_lucene_items";
	public static final String XSL_TRANSFORM = "xsl_transform";
	
	
	private static final Locale RU_RU = new Locale("ru", "RU");

	private static final DecimalFormatSymbols SYMBOLS = new DecimalFormatSymbols(RU_RU);
	static {
		SYMBOLS.setDecimalSeparator('.');
		SYMBOLS.setGroupingSeparator(' ');		
	}
	private static final DecimalFormat FORMATTER = new DecimalFormat("#,##0.###", SYMBOLS);
	
    private static interface TimerMessage {
    	void output();
    	long getExecTime();
    }
    
	private static class TimeLogMessage implements TimerMessage{
		private String taskName;
		private String comment;
		private long execTime;
		
		private TimeLogMessage(String taskName, long execTime, String comment) {
			this.taskName = taskName;
			this.execTime = execTime;
			this.comment = comment;
		}

		public void output() {
			String message = "Thread #" + Thread.currentThread().getId() 
					+ ": '" + taskName + "' time - " + FORMATTER.format(execTime / 1000) + " mks";
			if (comment != null)
				message += "  URL: " + comment;
			ServerLogger.error(message);
		}

		public long getExecTime() {
			return execTime;
		}
	}
	
	private static class SimpleMessage implements TimerMessage  {
		private String message;
		
		private SimpleMessage(String message) {
			this.message = message;
		}
		
		public void output() {
			//ServerLogger.debug(message);
			System.out.println(message);
		}

		public long getExecTime() {
			return 0;
		}
	}
	
	private HashMap<String, TimeLogMessage> runningTasks = new HashMap<String, TimeLogMessage>();
	private ArrayList<TimerMessage> log = new ArrayList<TimerMessage>();
	
	private static ThreadLocal<Timer> threadLocalInstance = new ThreadLocal<Timer>() {
		@Override
		protected Timer initialValue() {
			return new Timer();
		}
	};
	/**
	 * Получить экземпляр таймера
	 * @return
	 */
	public static Timer getTimer() {
		return threadLocalInstance.get();
	}
	/**
	 * Пометить время начала выполнения
	 * @param name
	 */
	public void start(String name) {
		start(name, null);
	}
	/**
	 * Пометить время начала выполнения, хрантися дополительная информация
	 * @param name
	 * @param comment
	 */
	public void start(String name, String comment) {
		TimeLogMessage stamp = new TimeLogMessage(name, System.nanoTime(), comment);
		runningTasks.put(name, stamp);
//		log.add(new SimpleMessage("Thread #" + Thread.currentThread().getId() + " Starting '" + name + "'"));
	}
	/**
	 * Вывести сообщение с номером потока
	 * @param message
	 */
	public void logThread(CharSequence message) {
		log.add(new SimpleMessage("Thread #" + Thread.currentThread().getId() + " says\n" + message));
	}
	/**
	 * Подсчитать время выполнения
	 * @param name
	 */
	public void stop(String name) {
		TimeLogMessage stamp = runningTasks.get(name);
		if (stamp == null) {
			ServerLogger.warn("Timer for task '" + name + "' not started");
			return;
		}
		stamp.execTime = System.nanoTime() - stamp.execTime;
		log.add(stamp);
		runningTasks.remove(name);
	}
	/**
	 * Записать все значения таймеров в журнал
	 */
	public void flush() {
		for (TimerMessage stamp : log) {
			stamp.output();
		}
		log.clear();
	}
	
	public static void main(String[] args) throws InterruptedException {
		Timer.getTimer().start("All procedure");
		for (int i = 0; i < 10; i++) {
			Timer.getTimer().start("Subproc 10 times", "comment");
			for (int j = 0; j < 5; j++) {
				Timer.getTimer().logThread("cool");
				Timer.getTimer().start("Final 5 times");
				Thread.sleep(50);
				Timer.getTimer().stop("Final 5 times");
			}
			Timer.getTimer().stop("Subproc 10 times");
		}
		Timer.getTimer().stop("All procedure");
		Timer.getTimer().flush();
	}
}
