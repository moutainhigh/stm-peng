package com.mainsteam.stm.job;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.quartz.CronExpression;
import org.quartz.CronScheduleBuilder;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;


public class TriggerFactory {
	
	private final String SYSTEM_TRIGGER_NAME="SYSTEM_TRIGGER_NAME_";
	private final String SYSTEM_TRIGGER_GROUP="SYSTEM_TRIGGER_GROUP_";
	
	private static final String SECOND="SECOND_";
	private static final String MINUTE="MINUTE_";
	private static final String HOUR="HOUR_";
	private static final String DAY="DAY_";
	private static final String WEEK="WEEK_";
	private static final String MONTH="MONTH_";
	private static final String YEAR="YEAR_";
	
	private static final Map<String, Trigger> triggerMap = new ConcurrentHashMap<String, Trigger>();
	
	private static final Map<String, String> cronMap = new ConcurrentHashMap<String, String>();
	
	private static TriggerFactory triggerFactory = null;
	private TriggerFactory() {}

	public static synchronized TriggerFactory getInstance() {
		if (triggerFactory == null) {
			synchronized (TriggerFactory.class) {
				if (triggerFactory == null) {
					cronMap.put(SECOND, "SECOND * * * * ?");
					cronMap.put(MINUTE, "* MINUTE * * * ?");
					cronMap.put(HOUR, "* * HOUR * * ?");
					cronMap.put(DAY, "* * * DAY * ?");
					cronMap.put(MONTH, "* * * * MONTH ?");
					cronMap.put(WEEK, "* * * ? * WEEK");
					cronMap.put(YEAR, "* * * * * ? YEAR");
					triggerFactory = new TriggerFactory();
				}
			}
		}
		return triggerFactory;
	}
	
	public Trigger getSecondTrigger(int second){
		return createTrigger(SECOND,second);
	}
	
	public Trigger getMinuteTrigger(int minute){
		return createTrigger(SECOND,minute);
	}
	
	public Trigger getHourTrigger(int hour){
		return createTrigger(HOUR,hour);
	}
	
	public Trigger getDayTrigger(int day){
		return createTrigger(DAY,day);
	}
	
	public Trigger getMonthTrigger(int month){
		return createTrigger(MONTH,month);
	}
	
	public Trigger getWeekTrigger(int week){
		return createTrigger(WEEK,week);
	}
	
	public Trigger getYearTrigger(int year){
		return createTrigger(YEAR,year);
	}
	
	public Trigger get(CronExpression cronExp){
		return null;
	}
	
	
	
//	秒       0-59        , - * /  
//	分       0-59        , - * /  
//	小时      0-23        , - * /  
//	日期      1-31        , - * ? / L W C  
//	月份      1-12 或者 JAN-DEC         , - * /  
//	星期      1-7 或者 SUN-SAT      , - * ? / L C #  
//	年（可选）       留空, 1970-2099       , - * /  
	
	private Trigger createTrigger(String type,int cnt){
		Trigger trigger =null;
		String key=SYSTEM_TRIGGER_NAME+type+cnt;
		if(triggerMap.containsKey(key)){
			trigger = triggerMap.get(key);
		}else{
			String cronExp=cronMap.get(type);
			
			cronExp=cronExp.replace(type, Integer.toString(cnt));
			if(CronExpression.isValidExpression(cronExp)){
		    	TriggerBuilder<Trigger> triggerBuilder=null;
				CronScheduleBuilder cronScheduleBuilder=CronScheduleBuilder.cronSchedule(cronExp);	
				triggerBuilder = TriggerBuilder.newTrigger();
				triggerBuilder.withIdentity(key,SYSTEM_TRIGGER_GROUP+type);
				triggerBuilder.withSchedule(cronScheduleBuilder);
				trigger=triggerBuilder.build();
				
				triggerMap.put(key, trigger);
			}else{
				//thorw Exception ... CronExpression error
				//throw new Exception("wrong Expression");
			}	
			
		}
		return trigger;
	}
	
	public static void main(String[] args) {
		
		String cron="* * * * * ? *";
		System.out.println(CronExpression.isValidExpression(cron));
		
		
	}

}
