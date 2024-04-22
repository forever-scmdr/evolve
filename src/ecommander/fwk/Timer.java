package ecommander.fwk;

import org.apache.commons.collections4.queue.CircularFifoQueue;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.math.NumberUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.*;

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
	public static final String PAGE_INNER_PROCESS = "page_inner_process";
	public static final String LOAD_LUCENE_ITEMS = "load_lucene_items";
	public static final String XSL_TRANSFORM = "xsl_transform";
	
	
	private static final Locale RU_RU = new Locale("ru", "RU");

	private static final DecimalFormatSymbols SYMBOLS = new DecimalFormatSymbols(RU_RU);
	static {
		SYMBOLS.setDecimalSeparator('.');
		SYMBOLS.setGroupingSeparator(' ');		
	}
	private static final DecimalFormat FORMATTER = new DecimalFormat("#,##0.###", SYMBOLS);
	
    private interface TimerMessage {
    	void output();
    	long getExecTime();
    }
    
	public static class TimeLogMessage implements TimerMessage {
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
			ServerLogger.warn(message);
		}

		public long getExecTime() {
			return execTime;
		}

		public long getExecTimeMillis() {
			return (long)(execTime / (double) 1000000);
		}
	}
	
	private static class SimpleMessage implements TimerMessage {
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
	
	private HashMap<String, TimeLogMessage> runningTasks = new HashMap<>();
	private Queue<TimerMessage> log = new CircularFifoQueue<>(20);
	private LinkedHashMap<String, Pair<Long, Integer>> tasksTotal = new LinkedHashMap<>();

	private static ThreadLocal<Timer> threadLocalInstance = ThreadLocal.withInitial(() -> new Timer());
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
	public TimeLogMessage stop(String name) {
		TimeLogMessage stamp = runningTasks.get(name);
		if (stamp == null) {
			//ServerLogger.warn("Timer for task '" + name + "' not started");
			return null;
		}
		stamp.execTime = System.nanoTime() - stamp.execTime;
		log.add(stamp);
		runningTasks.remove(name);
		Pair<Long, Integer> timeQty = tasksTotal.get(name);
		long totalNanos = timeQty != null ? timeQty.getLeft() : 0;
		int totalQty = timeQty != null ? timeQty.getRight() : 0;
		totalNanos += stamp.execTime;
		totalQty++;
		tasksTotal.put(name, new Pair<>(totalNanos, totalQty));
		return stamp;
	}

	/**
	 * Получить время работы таймера
	 * @param name
	 * @return
	 */
	public long getNanos(String name) {
		TimeLogMessage stamp = runningTasks.get(name);
		if (stamp == null) {
			ServerLogger.warn("Timer for task '" + name + "' not started");
			return -1;
		}
		return System.nanoTime() - stamp.execTime;
	}

	/**
	 * Получить общее время работы всех заданий с определенным названием
	 * @param name
	 * @return
	 */
	public long getTotalNanos(String name) {
		if (!tasksTotal.containsKey(name)) {
			return getNanos(name);
		}
		return tasksTotal.get(name).getLeft();
	}

	/**
	 * Получить времы работы таймера в секундах
	 * @param name
	 * @return
	 */
	public double getSeconds(String name) {
		return getNanos(name) / (double) 1000000000;
	}

	/**
	 * Получить общее время работы всех заданий с определенным названием в секундах
	 * @param name
	 * @return
	 */
	public double getTotalSeconds(String name) {
		return getTotalNanos(name) / (double) 1000000000;
	}

	/**
	 * Получить общее время работы всех заданий с определенным названием в минутах
	 * @param name
	 * @return
	 */
	public BigDecimal getTotalMinutes(String name) {
		return BigDecimal.valueOf(getTotalSeconds(name) / 60).setScale(2, RoundingMode.HALF_EVEN);
	}

	/**
	 * Получяить общее количество выполнений таймера
	 * @param name
	 * @return
	 */
	public int getTotalQty(String name) {
		if (!tasksTotal.containsKey(name)) {
			return 0;
		}
		return tasksTotal.get(name).getRight();
	}

	/**
	 * Получить общее время работы всех заданий с определенным названием в милисекундах
	 * @param name
	 * @return
	 */
	public double getTotalMillis(String name) {
		return getTotalNanos(name) / (double) 1000000;
	}

	/**
	 * Прекратить запись таймеров, очистить все записи значений и лог
	 * Записать все значения таймеров в журнал
	 */
	public void finish() {
		for (TimerMessage stamp : log) {
			stamp.output();
		}
		log.clear();
		runningTasks.clear();
		tasksTotal.clear();
	}

	/**
	 * Все названия завершенных таймеров
	 * @return
	 */
	public Set<String> getAllTimerNames() {
		return tasksTotal.keySet();
	}

	/**
	 * Написать в виде строки все суммарные счетчики времени
	 * @return
	 */
	public String writeTotals(String... totalsOfWhat) {
		StringBuilder sb = new StringBuilder();
		String tag = totalsOfWhat.length > 0 ? tag = totalsOfWhat[0] : null;
		if (StringUtils.isBlank(tag)) {
			sb.append("\n\n\t\t\tTIMER TOTALS\n\n");
		} else {
			sb.append("\n\n\t\t\tTIMER TOTALS (").append(tag).append(")\n\n");
		}
		for (String timerName : tasksTotal.keySet()) {
			sb.append("\t").append(timerName).append(":\t\t").append(String.format("%.4f", getTotalSeconds(timerName))).append(" seconds; ")
					.append(getTotalQty(timerName)).append(" times\n");
		}
		sb.append("\n=============================================\n\n");
		return sb.toString();
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
		ServerLogger.warn("TOTAL SECONDS: " + Timer.getTimer().getSeconds("All procedure"));
		Timer.getTimer().stop("All procedure");
		Timer.getTimer().finish();
	}
}
