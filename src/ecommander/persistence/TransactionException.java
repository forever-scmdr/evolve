/*
* $RCSfile$
* $Revision$
* $Date$
* (c) Copyright International Business Machines Corporation, 2007
*/  
package ecommander.persistence;

/**
 * Исключение, которое выбрасывает TransactionExecutor когда транзакция не была
 * завершена
 * 
 * @author karlov
 */
public class TransactionException extends Exception
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final String EXCEPTION_TEXT = "Transaction Exception occured. ";
	/**
	 * 
	 */
	public TransactionException()
	{
		super(EXCEPTION_TEXT);
	}

	/**
	 * @param arg0
	 */
	public TransactionException(Throwable arg0)
	{
		super(arg0);
	}

	/**
	 * @param s
	 */
	public TransactionException(String s)
	{
		super(EXCEPTION_TEXT + s);
	}

}