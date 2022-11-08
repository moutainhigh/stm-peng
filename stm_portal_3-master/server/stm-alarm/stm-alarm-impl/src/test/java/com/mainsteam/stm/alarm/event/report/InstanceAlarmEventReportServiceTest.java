package com.mainsteam.stm.alarm.event.report;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.alarm.obj.AlarmEvent;
import com.mainsteam.stm.obj.TimePeriod;


@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration
@Transactional 
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
	"classpath*:META-INF/services/portal-*-beans.xml" })
public class InstanceAlarmEventReportServiceTest {
	@Autowired InstanceAlarmEventReportService instanceAlarmEventReportService;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("caplibs.path", "../../../Capacity/cap_libs");
		System.setProperty("testCase", "true");
	}
	@Test
	public void testFindTotleAlarmReport(){
		InstanceAlarmEventReportQuery query=new InstanceAlarmEventReportQuery();
		Calendar cal=Calendar.getInstance();
		cal.set(Calendar.MONTH, 8);
		
		List<TimePeriod> pds=new ArrayList<TimePeriod>();
		query.setTimePeriods(pds);
		
		TimePeriod pd=new TimePeriod();
		pds.add(pd);
		pd.setStartTime(cal.getTime());
		cal.set(Calendar.MONTH, 9);
		pd.setEndTime(cal.getTime());
		
		pd=new TimePeriod();
		pds.add(pd);
		pd.setStartTime(cal.getTime());
		pd.setEndTime(new Date());
		
		
		List<Long> instanceIDes=new ArrayList<>();
		instanceIDes.add(5478145l);
		query.setInstanceIDes(instanceIDes);
		
		Collection<InstanceAlarmEventReportData>  list=instanceAlarmEventReportService.findTotleAlarmReport(query);
	
		System.out.println("FindTotleAlarmReport:"+JSON.toJSONString(list));
	}
	
	@Test
	public void testFindTotleAlarmDetail(){
		InstanceAlarmEventReportQuery query=new InstanceAlarmEventReportQuery();
		Calendar cal=Calendar.getInstance();
		cal.set(Calendar.MONTH, 8);
		
		List<TimePeriod> pds=new ArrayList<TimePeriod>();
		query.setTimePeriods(pds);
		
		TimePeriod pd=new TimePeriod();
		pds.add(pd);
		pd.setStartTime(cal.getTime());
		cal.set(Calendar.MONTH, 9);
		pd.setEndTime(cal.getTime());
		
		pd=new TimePeriod();
		pds.add(pd);
		pd.setStartTime(cal.getTime());
		pd.setEndTime(new Date());
		
		
//		List<InstanceStateEnum> states=new ArrayList<InstanceStateEnum>();
//		states.add(InstanceStateEnum.CRITICAL);
//		query.setStates(states);
		
		List<Long> instanceIDes=new ArrayList<>();
		instanceIDes.add(5478145l);
		query.setInstanceIDes(instanceIDes);
		
		Collection<AlarmEvent>  list=instanceAlarmEventReportService.findTotleAlarmDetail(query);
	
		System.out.println("FindTotleAlarmReport:"+JSON.toJSONString(list));
	}
	
	@Test
	public void testFindTotleAlarmDetailToFile(){
		InstanceAlarmEventReportQuery query=new InstanceAlarmEventReportQuery();
		Calendar cal=Calendar.getInstance();
		cal.set(Calendar.DAY_OF_MONTH, 26);
		
		List<TimePeriod> pds=new ArrayList<TimePeriod>();
		query.setTimePeriods(pds);
		
		TimePeriod pd=new TimePeriod();
		pds.add(pd);
		pd.setStartTime(cal.getTime());
		pd.setEndTime(new Date());
		
		List<Long> instanceIDes=new ArrayList<>();
		instanceIDes.add(1501l);
		instanceIDes.add(1650l);
		instanceIDes.add(1651l);
		instanceIDes.add(1652l);
		instanceIDes.add(1653l);
		instanceIDes.add(1654l);
		instanceIDes.add(1904l);
		instanceIDes.add(1890l);
		instanceIDes.add(1873l);
		instanceIDes.add(1874l);
		instanceIDes.add(1872l);
		query.setInstanceIDes(instanceIDes);
		
		Collection<AlarmEvent>  list=instanceAlarmEventReportService.findTotleAlarmDetail(query);
		
		System.out.println("FindTotleAlarmReport:"+JSON.toJSONString(list));
		
		File file=new File("./out.txt");
		if(!file.exists()){
			try {
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		try {
			FileOutputStream fout=new FileOutputStream(file);
			
			SimpleDateFormat format = new SimpleDateFormat("yyyy-M-dd HH:mm:ss"); 
			for(AlarmEvent event:list){
				StringBuilder sb=new StringBuilder();
				sb.append("\nalarmContent="+event.getContent());
				sb.append(",alarmID="+event.getEventID());
				sb.append(",alarmSourceID="+event.getSourceID());
				sb.append(",alarmSourceIP="+event.getSourceIP());
				sb.append(",alarmSourceName="+event.getSourceName());
				sb.append(",alarmSourceResourceID="+event.getExt0());
				sb.append(",alarmSourceCategoryID="+event.getExt1());
				sb.append(",alarmSourceMetricID="+event.getExt3());
				sb.append(",alarmTime="+(event.getCollectionTime()!=null?format.format(event.getCollectionTime()):null));
				sb.append(",alarmIsRecover="+event.isRecovered());
				sb.append(",recoverAlarmID="+event.getRecoveryEventID());
				sb.append(",recoverTime="+(event.getRecoveryTime()!=null?format.format(event.getRecoveryTime()):null));
				fout.write(sb.toString().getBytes());
			}
			
			fout.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		
	}
}
