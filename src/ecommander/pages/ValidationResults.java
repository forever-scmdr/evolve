package ecommander.pages;

import java.util.ArrayList;

/**
 * Результаты валидации элементов страницы
 * @author EEEE
 *
 */
public class ValidationResults {
	
	public static class StructureMessage {
		public final String originator;
		public final String message;
		
		private StructureMessage(String originator, String message) {
			this.originator = originator;
			this.message = message;
		}
	}
	
	public static class LineMessage {
		public final String message;
		public final int lineNumber;
		
		public LineMessage(int lineNumber, String message) {
			this.message = message;
			this.lineNumber = lineNumber;
		}
	}
	
	private ArrayList<StructureMessage> structureMessages = new ArrayList<StructureMessage>();
	private ArrayList<LineMessage> lineMessages = new ArrayList<LineMessage>();
	private Throwable exception;
	
	private Object bufferData; // данные, которые необходимы для валидации (передаются с верхних уровней на нижние)
	
	public void addError(String originator, String message) {
		structureMessages.add(new StructureMessage(originator, message));
	}
	
	public void addError(int lineNumber, String message) {
		lineMessages.add(new LineMessage(lineNumber, message));
	}
	
	public void setException(Throwable exception) {
		this.exception = exception;
	}
	
	public ArrayList<StructureMessage> getStructureErrors() {
		return structureMessages;
	}

	public ArrayList<LineMessage> getLineErrors() {
		return lineMessages;
	}
	
	public Throwable getException() {
		return exception;
	}
	
	public boolean isSuccessful() {
		return structureMessages.size() == 0 && lineMessages.size() == 0 && exception == null;
	}
	
	public void setBufferData(Object data) {
		bufferData = data;
	}
	
	public Object getBufferData() {
		return bufferData;
	}
}
