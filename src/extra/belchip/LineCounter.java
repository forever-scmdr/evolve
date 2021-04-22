package extra.belchip;

import java.util.Date;

import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class LineCounter  extends DefaultHandler{
	private int lineNumber = 0;
	private long start, end;
	private Locator locator;
	private final static String OPERATION = "Подсчет количества строк в файле интеграции.";
	private static final String MSG = "Подсчет строк завершен. Затраченное время: ";
	private Integrate_2.Info info = null;
	
	public LineCounter(Integrate_2.Info info) {
		start = new Date().getTime();
		this.info = info;
		this.info.setOperation(OPERATION);
	}
	@Override
	public void setDocumentLocator(Locator locator) {
		this.locator = locator;
	}
	@Override
	public void startDocument() throws SAXException {};
	@Override
	public void endDocument() throws SAXException {
		lineNumber = locator.getLineNumber();
		this.info.setLineNumber(lineNumber);
		end = new Date().getTime();
		long elapsedTime = end - start;
		String msg = MSG + MsBike.showTime(elapsedTime);
		this.info.addLog(msg);
	}
	
}
