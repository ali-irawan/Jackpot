package org.jackpotlib.common;

/**
 * TaskListener is listen to task event, when error is occured or when the task is done
 * 
 * @author Ali Irawan
 * @version 1.0
 */
public interface TaskListener {

	public void errorOccured(Task task, Exception e);
	public void done(Task task, Object message);
	
}
