package org.jackpotlib.common;

import java.util.Vector;

/**
 * 
 * @author Ali Irawan
 * @version 1.0
 */
public class TaskWorker implements Runnable {
	private boolean quit = false;
	private Vector queue = new Vector();
	private TaskListener listener;
	private Thread thread;
	
	public TaskWorker(){
		thread = new Thread(this);
	}
	public TaskWorker(TaskListener listener){
		this.listener = listener;
		thread = new Thread(this);
	}
	
	public void start(){
		if(thread!=null) thread.start();
	}
	
	private Task getNext(){
		Task task = null;
		if(!queue.isEmpty()){
			task = (Task) queue.firstElement();
		}
		return task;
	}
	
	 public void run() {
         while (!quit) {
             Task task = getNext();
             if (task != null) {
            	 boolean retryOnFail = task.retryOnFail();
            	 int retryMaxAttempt = task.retryMaxAttemp();
            	 int currentAttempt = 0;
            	 while(true){
                     try {
                    	 Object result = task.doTask();
                    	 if(listener!=null) listener.done(task, result);
                    	 break;  // break the loop now
                     }catch(Exception e){
                    	 if(listener!=null) listener.errorOccured(task, e);
                    	 
                		 //always retry if retryMaxAttempt no need to count currentAttempt
                    	 if(retryMaxAttempt!=-1){
                        	 currentAttempt++;
                        	 if(currentAttempt>=retryMaxAttempt) break; // retry reach max attempt
                    	 }
                    	 
                    	 if(task.delayOnFailure()>0){
                        	 try {
    							Thread.currentThread().sleep(task.delayOnFailure());
    						} catch (InterruptedException ex) {}
                         }
                     }
                     // if retry on fail true, then should not break the loop
                     if(!retryOnFail) break;
            	 }
                 queue.removeElement(task);
             } else {
            	 // task is null and only reason will be that vector has no more tasks
                 synchronized (queue) {
                     try {
                         queue.wait();
                     } catch (InterruptedException e) {
                         e.printStackTrace();
                     }
                 }
             }
         }
     }

      public void addTask(Task task) {
         synchronized (queue) {
             if (!quit) {
                 queue.addElement(task);
                 queue.notify();
             }
         }
     }

     public void quit() {
         synchronized (queue) {
             quit = true;
             queue.notify();
         }
     }
}
