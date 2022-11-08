package com.mainsteam.stm.util;

import java.util.Timer;
import java.util.TimerTask;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author cx
 *
 */
public class TimerTaskUtil {
	private static final Log logger = LogFactory.getLog(DateUtil.class);

	private final static Timer timer=new Timer();
	/**
	 * @param task
	 * @param delay
	 * @see {@link java.util.Timer.schedule(task,delay)}
	 */
	public static void schedule(TimerTask task,long delay){
		timer.schedule(task, delay);
	}
	
	/**
	 * @param task
	 * @param delay delay in milliseconds before task is to be executed
	 * @param period time in milliseconds between successive task executions.
	 */
	public static void schedule(final CallBack call,long delay,long period){
		timer.schedule(new TimerTask(){
			@Override public void run() {
				try{
					call.call();
				}catch(Exception e){
					logger.error(e.getMessage(),e);
				}
			}
		}, delay,period);
	}
	
	public static interface CallBack{
		void call();
	}
}
