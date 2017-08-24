package ecommander.pages;

import java.util.ArrayList;
import java.util.LinkedList;

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

		@Override
		public String toString() {
			return "Origin: " + originator + "\tMessage: " + message;
		}
	}
	
	public static class LineMessage {
		public final String message;
		public final int lineNumber;
		
		public LineMessage(int lineNumber, String message) {
			this.message = message;
			this.lineNumber = lineNumber;
		}

		@Override
		public String toString() {
			return "Line: " + lineNumber + "\tMessage: " + message;
		}
	}
	
	private ArrayList<StructureMessage> structureMessages = new ArrayList<StructureMessage>();
	private ArrayList<LineMessage> lineMessages = new ArrayList<LineMessage>();
	private Throwable exception;
	
	private LinkedList<Object> bufferData = new LinkedList<>(); // данные, которые необходимы для валидации (передаются с верхних уровней на нижние)
	
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
	
	public void pushBufferData(Object data) {
		bufferData.push(data);
	}

	public void popBufferData() {
		bufferData.pop();
	}

	public Object getBufferData() {
		return bufferData.peek();
	}
}
