package org.jackpotlib.common;

/**
 * General task interface
 * 
 * Usage:
 * <code>
 * public class MyTask implements Task {
 *    public Object doTask() throws Exception {
 *        // Perform your task here
 *    }
 *    public boolean retryOnFail(){
 *         return true;
 *    }
 *	  public int retryMaxAttemp() {
 *		   return -1; // -1 means always retry	
 *    }
 *	  public long delayOnFailure(){
 *         return 10000; // 10 seconds
 *    }
 * }
 * </code>
 * 
 * @author Ali Irawan
 * @version 1.0
 */
public interface Task {

	public Object doTask() throws Exception;
	public boolean retryOnFail();
	public int retryMaxAttemp();
	public long delayOnFailure();
	
}
