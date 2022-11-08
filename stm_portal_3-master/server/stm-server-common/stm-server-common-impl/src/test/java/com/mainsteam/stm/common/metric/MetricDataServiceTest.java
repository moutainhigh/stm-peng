package com.mainsteam.stm.common.metric;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.transaction.TransactionConfiguration;
import org.springframework.transaction.annotation.Transactional;
import com.alibaba.fastjson.JSON;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.common.metric.query.MetricHistoryDataQuery;
import com.mainsteam.stm.common.metric.query.MetricRealtimeDataQuery;
import com.mainsteam.stm.executor.exception.MetricExecutorException;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;


@RunWith(SpringJUnit4ClassRunner.class)
@TransactionConfiguration
@Transactional
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
		"classpath*:META-INF/services/server-processer-*-beans.xml" })
public class MetricDataServiceTest {
	private Log logger =LogFactory.getLog(MetricDataServiceTest.class);
	@Autowired MetricDataService metricDataService;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("testCase","true");
		System.setProperty("caplibs.path", "../../../Capacity/cap_libs");
	}
	
	@Test
	public void testGetMetricDatas_thread10(){
		for(int i=0;i<3;i++){
			Thread th=new Thread(new Runnable() {
				@Override public void run() {
					int i=0;
					while(i++<10){
						synchronized (this) {
							try {
								this.wait(5000);
							} catch (InterruptedException e) {
								e.printStackTrace();
							}
						}
						
						MetricRealtimeDataQuery query=new MetricRealtimeDataQuery();
						query.setMetricID(new String[]{"ifInOctetsSpeed","cpurate"});
						query.setInstanceID(new long[]{1258});
						query.setOrderMetricID("cpurate");
						query.setOrderForDesc(true);
						Page<Map<String,?>,MetricRealtimeDataQuery>  data=metricDataService.queryRealTimeMetricDatas(query,0,5);
						System.out.println("result:"+JSON.toJSONString(data));
					}
				}
			});
			th.start();
		}
		synchronized (this) {
			try {
				this.wait(50000*4);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	@Test
	public void testQueryRealTimeMetricData(){

		MetricRealtimeDataQuery query=new MetricRealtimeDataQuery();
		query.setMetricID(new String[]{"123"});
		query.setInstanceID(new long[]{590});
		List<Map<String,?>>  data=metricDataService.queryRealTimeMetricData(query);
		logger.warn("result:"+JSON.toJSONString(data));
	}
	
	
	@Test
	public void testGetMetricDatas(){
		{
			MetricRealtimeDataQuery query=new MetricRealtimeDataQuery();
			query.setMetricID(new String[]{"ifInOctetsSpeed"});
			query.setInstanceID(new long[]{1258});
			query.setOrderMetricID("CPUID");
			query.setOrderForDesc(true);
			Page<Map<String,?>,MetricRealtimeDataQuery>  data=metricDataService.queryRealTimeMetricDatas(query,0,5);
			System.out.println("result:"+JSON.toJSONString(data));
		}
		
		{
			MetricRealtimeDataQuery query=new MetricRealtimeDataQuery();
			query.setMetricID(new String[]{"CPUModel","cpurate"});
			query.setInstanceID(new long[]{7892145});
			query.setOrderMetricID("cpurate");
			query.setOrderForDesc(true);
			
			List<Map<String,?>>  data=metricDataService.queryRealTimeMetricData(query);
			System.out.println("result:"+JSON.toJSONString(data));
		}
		{
			MetricRealtimeDataQuery query=new MetricRealtimeDataQuery();
			query.setMetricID(new String[]{"cpurate","123123"});
			query.setInstanceID(new long[]{7892145});
			query.setOrderMetricID("cpurate");
			query.setOrderForDesc(true);
			
			List<Map<String,?>> data=metricDataService.queryRealTimeMetricData(query);
			System.out.println("result:"+JSON.toJSONString(data));
		}
	}
	
	@Test
	public void insertRealtimeDate(){
		Calendar cal=Calendar.getInstance();
		
		int size=10;
		int peroid=5;
		cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE)-(size*peroid));
		
		for(int i=0;i<size;i++){
			MetricData data=new MetricData();
			data.setResourceInstanceId(10032);
			data.setMetricId("ifOutOctetsSpeed");
			data.setProfileId(2343l);
			data.setTimelineId(1232l);
			data.setCollectTime(cal.getTime());
			data.setData(new String[]{String.valueOf(Math.random()*10)});
			metricDataService.updatePerformanceMetricData(data);
			cal.set(Calendar.MINUTE, cal.get(Calendar.MINUTE)+peroid);
		}
		
		try {
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	@Test
	public void testGetCustomMetric(){
		MetricData data=metricDataService.getCustomerMetricData(8062652, "1587000");
		logger.info(JSON.toJSONString(data));
	}
	
	@Test
	public void testQueryHistoryCustomerMetricData(){
		metricDataService.queryHistoryCustomerMetricData("cutomer", 1232, new Date(),  new Date());
	}
	
	@Test
	public void testQueryHistoryMetricDatas(){
		Calendar cal=Calendar.getInstance();
		cal.set(Calendar.YEAR, 2000);
		
		MetricHistoryDataQuery query=new MetricHistoryDataQuery();
		query.setEndTime(new Date());
		query.setStartTime(cal.getTime());
		query.setInstanceID(2001);
		query.setMetricID("ifnum");
		
		Page<List<MetricData>,MetricHistoryDataQuery> pager=metricDataService.queryHistoryMetricDatas(query,0,5);
		System.out.println("result:"+JSON.toJSONString(pager));
	}
	
	@Test
	public void testUpdateMetricData(){
		List<MetricData> mds=new ArrayList<MetricData>();
		Date now=new Date();
		for(int i=0;i<50;i++){
			MetricData md=new MetricData();
			md.setMetricId("cpu");
			md.setResourceInstanceId(2311+i);
			md.setData(new String[]{"1"+i});
			md.setCollectTime(now);
			mds.add(md);
		}
		
		for(int i=0;i<50;i++){
			MetricData md=new MetricData();
			md.setMetricId("memery");
			md.setResourceInstanceId(2311+i);
			md.setData(new String[]{"2"+i});
			md.setCollectTime(now);
			mds.add(md);
		}
		
		for(int i=0;i<50;i++){
			MetricData md=new MetricData();
			md.setMetricId("network");
			md.setResourceInstanceId(2311+i);
			md.setData(new String[]{"3"+i});
			md.setCollectTime(now);
			mds.add(md);
		}
		
		metricDataService.updateMetricDatas(mds);
	}
	
	@Test
	public void testAddCustomerData(){
		MetricData md=new MetricData();
		md.setMetricId("ifnum");
		md.setResourceInstanceId(2311);
		md.setCollectTime(new Date());
		md.setMetricType(MetricTypeEnum.PerformanceMetric);
		md.setData(new String[]{"222"});
		metricDataService.addCustomerMetricData(md);
	}
	
	@Test
	public void testUpadate_dataNull(){
		MetricData md=new MetricData();
		md.setMetricId("ifnum");
		md.setResourceInstanceId(2311);
		md.setCollectTime(new Date());
		metricDataService.updatePerformanceMetricData(md);
	}
	
	@Test
	public void testCatchRealtimeMetricData() throws MetricExecutorException{
		MetricData  md=metricDataService.catchRealtimeMetricData(2311, "ifnum");
		System.out.print("result:"+JSON.toJSONString(md));
	}
	
	@Test
	public void testTriggerInfoMetricGather() throws MetricExecutorException{
		metricDataService.triggerInfoMetricGather(64001,false);
	}
	@Test
	public void testUpadate(){
		MetricData md=new MetricData();
		md.setMetricId("ifnum");
		md.setResourceInstanceId(2311);
		md.setData(new String[]{"12.3"});
		md.setCollectTime(new Date());
		metricDataService.updatePerformanceMetricData(md);
	}
	
	
	@Test
	public void testAddMetricInfoData(){
		Date now=new Date();
		for(int i=0;i<50;i++){
			MetricData md=new MetricData();
			md.setMetricId("ifnum");
			md.setResourceInstanceId(2311+i);
			md.setData(new String[]{"1"+i});
			md.setCollectTime(now);
			metricDataService.addMetricInfoData(md);
		}
	}
	
	@Test
	public void testGetMetricInfoDatas(){
		List<MetricData>  list=metricDataService.getMetricInfoDatas(1351648, new String[]{"PartitionName", "PartitionSize", "PartitionUsedSize", "PartitionUnusedSize", "inodeFree", "inodeUsed"});
		System.out.println("testGetMetricInfoDatas:"+JSON.toJSONString(list));
	}
	@Test
	public void testGetMetricAvailableData(){
		MetricData data=metricDataService.getMetricAvailableData(8081,"getMetricAvailableData");
		System.out.println("testGetMetricInfoDatas:"+JSON.toJSONString(data));
	}
	@Test
	public void testGetMetricInfoDatas_metricID_empty(){
		List<MetricData>  list=metricDataService.getMetricInfoDatas(1351648, new String[]{});
		System.out.println("testGetMetricInfoDatas:"+JSON.toJSONString(list));
	}
	
	@Test
	public void testGetMetricInfoDatas_metricID_NULL(){
		List<MetricData>  list=metricDataService.getMetricInfoDatas(1351648, null);
		System.out.println("testGetMetricInfoDatas:"+JSON.toJSONString(list));
	}
	
	@Test
	public void testGetMetricInfoData(){
		MetricData  md=metricDataService.getMetricInfoData(1351648, "IPAddress");
		System.out.print("result:"+JSON.toJSONString(md));
	}
	
	@Test
	public void testFindTop(){
		List<MetricData>  list=metricDataService.findTop("memery", new long[]{ 2312,2313,2317,2321,2324,2326}, 5);
		logger.info("result:"+JSON.toJSONString(list));
	}
}
