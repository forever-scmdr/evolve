package ecommander.persistence;

import ecommander.common.exceptions.EcommanderException;

/**
 * Резльтат выполнения транзакции
 * Может быть либо успешным, либо нет.
 * Если результат неуспешный, можно получить ошибку
 * @author EEEE
 *
 */
public class TransactionResult {
	private EcommanderException exception = null;
	
	public TransactionResult() {
		
	}
	
	public TransactionResult(EcommanderException exception) {
		this.exception = exception;
	}
	
	public void setError(EcommanderException exception) {
		this.exception = exception;
	}
	
	public boolean isSuccessfull() {
		return exception == null;
	}
	
	public EcommanderException getError() {
		return exception;
	}
}
