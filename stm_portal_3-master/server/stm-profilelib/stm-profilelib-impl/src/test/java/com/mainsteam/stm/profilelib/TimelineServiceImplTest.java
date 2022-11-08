package com.mainsteam.stm.profilelib;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.dbunit.DataSourceBasedDBTestCase;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.dataset.DataSetException;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.ITable;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.InputSource;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.dict.PerfMetricStateEnum;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.MetricSetting;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.profilelib.obj.ProfileThreshold;
import com.mainsteam.stm.profilelib.obj.Timeline;
import com.mainsteam.stm.profilelib.obj.TimelineInfo;
import com.mainsteam.stm.profilelib.objenum.TimelineTypeEnum;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
		"classpath*:META-INF/services/portal-*-beans.xml" })
public class TimelineServiceImplTest extends DataSourceBasedDBTestCase{

	@Resource
	private TimelineService timelineService;
	
	@Resource(name = "defaultDataSource")
	private DataSource source;
	
	@Resource
	private CapacityService capacityService;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("caplibs.path", "../../../Capacity\\cap_libs");
		System.setProperty("testCase","test");
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		super.setUp();
	}

	@After
	public void tearDown() throws Exception {
		super.tearDown();
	}

	protected void setUpDatabaseConfig(DatabaseConfig config) {
		config.setProperty(DatabaseConfig.PROPERTY_BATCH_SIZE, new Integer(97));
		config.setProperty(DatabaseConfig.FEATURE_BATCHED_STATEMENTS, true);
	}

	protected DatabaseOperation getSetUpOperation() throws Exception {
		return DatabaseOperation.CLEAN_INSERT;
	}
	
	@Override
	protected IDataSet getDataSet() throws Exception {
		FlatXmlProducer producer = new FlatXmlProducer(new InputSource(
				"src/test/resources/timeline_dataset.xml"));
		IDataSet dataSet = new FlatXmlDataSet(producer);
		return dataSet;
	}

	@Override
	protected DataSource getDataSource() {
		return this.source;
	}
	

	@Test
	public void testGetTimelinesByProfileId(){
		long proflieId = 1;
		List<Timeline> timelines = null;
		try {
			 timelines = timelineService.getTimelinesByProfileId(proflieId);
		} catch (ProfilelibException e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(timelines);
		for (Timeline timeline : timelines) {
			assertEquals(proflieId,timeline.getTimelineInfo().getProfileId());
		}
		/**
		 * 在数据库中查询数据，并验证是否相等。
		 */
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable timelineTable = actualDataset.getTable("stm_profilelib_timeline");
			ITable metricTable = actualDataset.getTable("stm_profilelib_metric");
			ITable thresholdTable = actualDataset.getTable("stm_profilelib_threshold");
			
			boolean find = false;
			for (Timeline timeline : timelines) {
				System.out.println(timeline.getTimelineInfo().getStartTime());
				//验证基线
				find = validateTimeline(timeline,timelineTable);
				assertTrue(find);
				//验证指标
				find = false;
				List<ProfileThreshold> profileThresholds = new ArrayList<ProfileThreshold>();
				find = validateMetrics(timeline.getMetricSetting().getMetrics(),metricTable,profileThresholds);
				assertTrue(find);
				//验证阈值
				find = false;
				find = validatethresholds(profileThresholds,thresholdTable);
				assertTrue(find);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testGetTimelineById(){
		long timelineId = 11;
		Timeline timeline = null;
		try {
			timeline= timelineService.getTimelinesById(timelineId);
		} catch (ProfilelibException e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(timeline);
		/**
		 * 在数据库中查询数据，并验证是否相等。
		 */
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable timelineTable = actualDataset.getTable("stm_profilelib_timeline");
			ITable metricTable = actualDataset.getTable("stm_profilelib_metric");
			ITable thresholdTable = actualDataset.getTable("stm_profilelib_threshold");
			
			boolean find = false;
			
			//验证基线
			find = validateTimeline(timeline,timelineTable);
			assertTrue(find);
			//验证指标
			find = false;
			List<ProfileThreshold> profileThresholds = new ArrayList<ProfileThreshold>();
			find = validateMetrics(timeline.getMetricSetting().getMetrics(),metricTable,profileThresholds);
			assertTrue(find);
			//验证阈值
			find = false;
			find = validatethresholds(profileThresholds,thresholdTable);
			assertTrue(find);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testGetMetricByTimelineId(){
		long timelineId = 11;
		List<ProfileMetric> profileMetrics = null;
		try {
			profileMetrics = timelineService.getMetricByTimelineId(timelineId);
		} catch (ProfilelibException e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(profileMetrics);
		/**
		 * 在数据库中查询数据，并验证是否相等。
		 */
		IDataSet actualDataset = null;
		try {
			actualDataset  = getConnection().createDataSet();
			ITable metricTable = actualDataset.getTable("stm_profilelib_metric");
			ITable thresholdTable = actualDataset.getTable("stm_profilelib_threshold");
			
			boolean find = false;
			
			List<ProfileThreshold> profileThresholds = new ArrayList<>();
			find = validateMetrics(profileMetrics,metricTable,profileThresholds);
			assertTrue(find);
			//验证阈值
			find = false;
			find = validatethresholds(profileThresholds,thresholdTable);
			assertTrue(find);
			
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
		
	}
	
	private boolean validateTimeline(Timeline timeline,ITable timelineTable) throws NumberFormatException, DataSetException{
		boolean find = false;
		//查找基线值
		for (int i = 0; i < timelineTable.getRowCount(); i++) {
			TimelineInfo timelineInfo = timeline.getTimelineInfo();
			if (timelineInfo.getTimeLineId() == Long.parseLong(timelineTable.getValue(i,
					"time_line_id").toString())) {
				System.out.println("找到了基线，进行比较");
				/*assertEquals(Date.parse(timelineTable.getValue(i, "start_time").toString()),
						timeline.getStartTime());
				assertEquals(Date.parse(timelineTable.getValue(i, "end_time").toString()),
						timeline.getEndTime());*/
				assertEquals(Long.parseLong(timelineTable.getValue(i,
						"profile_id").toString()), timelineInfo.getProfileId());
				assertEquals(timelineTable.getValue(i, "line_type"),
						timelineInfo.getTimeLineType().toString());
				assertEquals(timelineTable.getValue(i, "line_name"),
						timelineInfo.getName());
				find = true;
				break;
			}
		}
		return find;
	}
	
	private boolean validateMetrics(List<ProfileMetric> ProfileMetrics,ITable metricTable,List<ProfileThreshold> profileThresholds) throws NumberFormatException, DataSetException{
		boolean find = false;
		//查找指标值
		for(ProfileMetric profileMetric : ProfileMetrics){
			//System.out.println("查找ID:" + profileMetric.getProfileId() + " " + profileMetric.getTimeLineId() + " "+profileMetric.getMetricId());
			for (int i = 0; i < metricTable.getRowCount(); i++) {
				/*System.out.println("profileId:" +metricTable.getValue(
						i, "profile_id"));
				System.out.println("metric:"+metricTable.getValue(i, "metric_id"));
				System.out.println("timeline_id:" +metricTable.getValue(
						i, "timeline_id"));*/
				if (profileMetric.getProfileId()== Long.parseLong(metricTable.getValue(
						i, "profile_id").toString())
						&& profileMetric.getMetricId().equals(metricTable.getValue(i, "metric_id"))
						&& profileMetric.getTimeLineId() == Long.parseLong(metricTable.getValue(
								i, "timeline_id").toString())
						) {
					System.out.println("找到了基线指标，进行比较");
					assertEquals(Long.parseLong(metricTable.getValue(i,
							"alarm_repeat").toString()), profileMetric.getAlarmFlapping());
					assertEquals(metricTable.getValue(i,
							"dict_frequency_id"), profileMetric.getDictFrequencyId());
					find = true;
					if(profileMetric.getMetricThresholds() != null){
						profileThresholds.addAll(profileMetric.getMetricThresholds());
					}
					break;
				}
			}
			
		}
		return find;
	}
	
	private boolean validatethresholds(List<ProfileThreshold> profileThresholds,ITable thresholdTable) throws NumberFormatException, DataSetException{
		if(profileThresholds == null){
			return true;
		}
		boolean find = false;
		int count = 0;
		for (ProfileThreshold profileThreshold : profileThresholds) {
			//System.out.println("查找ID:" + profileThreshold.getProfileId() + " " + profileThreshold.getTimelineId() + " "+profileThreshold.getMetricId());
			for (int i = 0; i < thresholdTable.getRowCount(); i++) {
				if (Long.parseLong(thresholdTable.getValue(i,"mk_id").toString()) == profileThreshold.getThreshold_mkId()) {
					System.out.println("找到基线指标阈值比较");
					assertEquals(Long.parseLong(thresholdTable.getValue(
							i, "profile_id").toString()),profileThreshold.getProfileId());
					assertEquals(Long.parseLong(thresholdTable.getValue(i, "timeline_id").toString()), profileThreshold.getTimelineId());
					assertEquals(PerfMetricStateEnum.valueOf(thresholdTable.getValue(i,"dict_metric_state").toString()),profileThreshold.getPerfMetricStateEnum());
					assertEquals(thresholdTable.getValue(i,"expression_desc").toString(),profileThreshold.getExpressionDesc());
					assertEquals(thresholdTable.getValue(i,"threshold_value").toString(),profileThreshold.getThresholdValue());
					assertEquals(thresholdTable.getValue(i,"expression_operator").toString(),profileThreshold.getExpressionOperator());
					count++;
				}
			}
		}
		if(count == profileThresholds.size()){
			find = true;
		}
		return find;
	}
	
	@Test
	public void testInsertTimeline(){
		Timeline timeline = new Timeline();
		TimelineInfo timelineInfo = new TimelineInfo();
		long proflleId = 9;
	    String metricId = "cpu";
	    String metricId1 = "ifNum";
		
	    timelineInfo.setStartTime(new Date());
	    timelineInfo.setEndTime(new Date());
	    timelineInfo.setName("testName");
	    timelineInfo.setProfileId(proflleId);
	    timelineInfo.setTimeLineType(TimelineTypeEnum.DATE);
	    timeline.setTimelineInfo(timelineInfo);
		MetricSetting metricSetting = new MetricSetting();
		List<ProfileMetric> metrics = new ArrayList<ProfileMetric>();
		
		ProfileMetric profileMetric = new ProfileMetric();
		profileMetric.setAlarm(true);
		profileMetric.setAlarmFlapping(3);
		profileMetric.setDictFrequencyId("min5");
		profileMetric.setMetricId(metricId);
		profileMetric.setMonitor(true);
		profileMetric.setProfileId(proflleId);
		
		ProfileMetric profileMetric1 = new ProfileMetric();
		profileMetric1.setAlarm(true);
		profileMetric1.setAlarmFlapping(2);
		profileMetric1.setDictFrequencyId("min1");
		profileMetric1.setMetricId(metricId1);
		profileMetric1.setMonitor(true);
		profileMetric1.setProfileId(proflleId);
		
		
		//阈值数据构造
		List<ProfileThreshold> profileThresholds = new ArrayList<>();
		
		ProfileThreshold profileThreshold = new ProfileThreshold();
		profileThreshold.setPerfMetricStateEnum(PerfMetricStateEnum.Minor);
		profileThreshold.setMetricId(metricId);
		profileThreshold.setExpressionDesc(">20");
		profileThreshold.setExpressionOperator(">");
		profileThreshold.setProfileId(proflleId);
		profileThreshold.setThresholdValue("20");
		
		profileThresholds.add(profileThreshold);
		
		ProfileThreshold profileThreshold2 = new ProfileThreshold();
		profileThreshold2.setPerfMetricStateEnum(PerfMetricStateEnum.Indeterminate);
		profileThreshold2.setMetricId(metricId);
		profileThreshold2.setExpressionDesc("<50");
		profileThreshold2.setExpressionOperator("<");
		profileThreshold2.setProfileId(proflleId);
		profileThreshold2.setThresholdValue("50");
		
		profileThresholds.add(profileThreshold2);
		
		//阈值数据构造
		List<ProfileThreshold> profileThreshold1s = new ArrayList<>();
		
		ProfileThreshold profileThreshold3 = new ProfileThreshold();
		profileThreshold3.setPerfMetricStateEnum(PerfMetricStateEnum.Indeterminate);
		profileThreshold3.setMetricId(metricId1);
		profileThreshold3.setExpressionDesc(">30");
		profileThreshold3.setExpressionOperator(">");
		profileThreshold3.setProfileId(proflleId);
		profileThreshold3.setThresholdValue("30");
		
		profileThreshold1s.add(profileThreshold3);
		
		ProfileThreshold profileThreshold4 = new ProfileThreshold();
		profileThreshold4.setPerfMetricStateEnum(PerfMetricStateEnum.Major);
		profileThreshold4.setMetricId(metricId1);
		profileThreshold4.setExpressionDesc("<60");
		profileThreshold4.setExpressionOperator("<");
		profileThreshold4.setProfileId(proflleId);
		profileThreshold4.setThresholdValue("60");
		
		profileThreshold1s.add(profileThreshold4);
		
		//指标阈值
		profileMetric.setMetricThresholds(profileThresholds);
		profileMetric1.setMetricThresholds(profileThreshold1s);
		
		//指标
		metrics.add(profileMetric);
		metrics.add(profileMetric1);
		metricSetting.setMetrics(metrics);
		
		
		timeline.setMetricSetting(metricSetting);
		
		long newTimelineId = 0;
		//添加到数据库
		try {
			newTimelineId = timelineService.insertTimeline(timeline);
		} catch (ProfilelibException e) {
			e.printStackTrace();
			fail();
		}
		
		Timeline newTimeline = null;
		try {
			newTimeline= timelineService.getTimelinesById(newTimelineId);
		} catch (ProfilelibException e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(newTimeline);
		/**
		 * 在数据库中查询数据，并验证是否相等。
		 */
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable timelineTable = actualDataset.getTable("stm_profilelib_timeline");
			ITable metricTable = actualDataset.getTable("stm_profilelib_metric");
			ITable thresholdTable = actualDataset.getTable("stm_profilelib_threshold");
			
			boolean find = false;
			
			//验证基线
			find = validateTimeline(newTimeline,timelineTable);
			assertTrue(find);
			//验证指标
			find = false;
			profileThresholds = new ArrayList<ProfileThreshold>();
			find = validateMetrics(newTimeline.getMetricSetting().getMetrics(),metricTable,profileThresholds);
			assertTrue(find);
			//验证阈值
			find = false;
			find = validatethresholds(profileThresholds,thresholdTable);
			assertTrue(find);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
	}
	
//	@Test
//	public void testUpdateTimeline(){
//		long timelineId = 14;
//		Timeline timeline = new Timeline();
//		TimelineInfo timelineInfo = new TimelineInfo();
//		long proflleId = 2;
//	    String metricId = "cpu";
//	    String metricId1 = "ifNum";
//		
//	    timelineInfo.setStartTime(new Date());
//	    timelineInfo.setEndTime(new Date());
//	    timelineInfo.setName("testUpdateName");
//	    timelineInfo.setProfileId(proflleId);
//	    timelineInfo.setTimeLineType(TimelineTypeEnum.WEEKLY);
//	    timelineInfo.setTimeLineId(timelineId);
//		MetricSetting metricSetting = new MetricSetting();
//		List<ProfileMetric> metrics = new ArrayList<ProfileMetric>();
//		
//		ProfileMetric profileMetric = new ProfileMetric();
//		profileMetric.setAlarm(true);
//		profileMetric.setAlarmFlapping(3);
//		profileMetric.setDictFrequencyId("min6");
//		profileMetric.setMetricId(metricId);
//		profileMetric.setMonitor(true);
//		profileMetric.setProfileId(proflleId);
//		
//		profileMetric.setTimeLineId(timelineId);
//		
//		ProfileMetric profileMetric1 = new ProfileMetric();
//		profileMetric1.setAlarm(true);
//		profileMetric1.setAlarmFlapping(2);
//		profileMetric1.setDictFrequencyId("min1");
//		profileMetric1.setMetricId(metricId1);
//		profileMetric1.setMonitor(true);
//		profileMetric1.setProfileId(proflleId);
//		profileMetric1.setTimeLineId(timelineId);
//		
//		//阈值数据构造
//		List<ProfileThreshold> profileThresholds = new ArrayList<>();
//		
//		ProfileThreshold profileThreshold = new ProfileThreshold();
//		profileThreshold.setPerfMetricStateEnum(PerfMetricStateEnum.Minor);
//		profileThreshold.setMetricId(metricId);
//		profileThreshold.setExpressionDesc(">40");
//		profileThreshold.setExpressionOperator(">");
//		profileThreshold.setProfileId(proflleId);
//		profileThreshold.setThresholdValue("40");
//		profileThreshold.setTimelineId(timelineId);
//		profileThreshold.setThreshold_mkId(7);
//		profileThresholds.add(profileThreshold);
//		
//		ProfileThreshold profileThreshold2 = new ProfileThreshold();
//		profileThreshold2.setPerfMetricStateEnum(PerfMetricStateEnum.Major);
//		profileThreshold2.setMetricId(metricId);
//		profileThreshold2.setExpressionDesc("<90");
//		profileThreshold2.setExpressionOperator("<");
//		profileThreshold2.setProfileId(proflleId);
//		profileThreshold2.setThresholdValue("90");
//		profileThreshold2.setTimelineId(timelineId);
//		profileThreshold.setThreshold_mkId(8);
//		profileThresholds.add(profileThreshold2);
//		
////		//阈值数据构造
////		List<ProfileThreshold> profileThreshold1s = new ArrayList<>();
////		
////		ProfileThreshold profileThreshold3 = new ProfileThreshold();
////		profileThreshold3.setPerfMetricStateEnum(PerfMetricStateEnum.Green);
////		profileThreshold3.setMetricId(metricId1);
////		profileThreshold3.setExpressionDesc(">35");
////		profileThreshold3.setExpressionOperator(">");
////		profileThreshold3.setProfileId(proflleId);
////		profileThreshold3.setThresholdValue("35");
////		profileThreshold3.setTimelineId(timelineId);
////		profileThreshold1s.add(profileThreshold3);
////		
////		ProfileThreshold profileThreshold4 = new ProfileThreshold();
////		profileThreshold4.setPerfMetricStateEnum(PerfMetricStateEnum.Yellow);
////		profileThreshold4.setMetricId(metricId1);
////		profileThreshold4.setExpressionDesc("<65");
////		profileThreshold4.setExpressionOperator("<");
////		profileThreshold4.setProfileId(proflleId);
////		profileThreshold4.setThresholdValue("65");
////		profileThreshold4.setTimelineId(timelineId);
////		profileThreshold1s.add(profileThreshold4);
//		
//		//指标阈值
//		profileMetric.setMetricThresholds(profileThresholds);
//		//profileMetric1.setMetricThresholds(profileThreshold1s);
//		
//		//指标
//		metrics.add(profileMetric);
//		metrics.add(profileMetric1);
//		metricSetting.setMetrics(metrics);
//		
//		
//		timeline.setMetricSetting(metricSetting);
//		
//		//修改数据库
//		try {
//			timelineService.updateTimeline(timeline);
//		} catch (ProfilelibException e) {
//			e.printStackTrace();
//			fail();
//		}
//		
//		Timeline newTimeline = null;
//		try {
//			newTimeline= timelineService.getTimelinesById(timelineId);
//		} catch (ProfilelibException e) {
//			e.printStackTrace();
//			fail();
//		}
//		assertNotNull(newTimeline);
//		/**
//		 * 在数据库中查询数据，并验证是否相等。
//		 */
//		try {
//			IDataSet actualDataset = getConnection().createDataSet();
//			ITable timelineTable = actualDataset.getTable("stm_profilelib_timeline");
//			ITable metricTable = actualDataset.getTable("stm_profilelib_metric");
//			ITable thresholdTable = actualDataset.getTable("stm_profilelib_threshold");
//			
//			boolean find = false;
//			
//			//验证基线
//			find = validateTimeline(newTimeline,timelineTable);
//			assertTrue(find);
//			//验证指标
//			find = false;
//			profileThresholds = new ArrayList<ProfileThreshold>();
//			find = validateMetrics(newTimeline.getMetricSetting().getMetrics(),metricTable,profileThresholds);
//			assertTrue(find);
//			//验证阈值
//			find = false;
//			find = validatethresholds(profileThresholds,thresholdTable);
//			assertTrue(find);
//		} catch (Exception e1) {
//			e1.printStackTrace();
//			fail();
//		}
//	}
	
	@Test
	public void testRemoveTimelineByTimelineId(){
		long timelineId = 13;
		try {
			timelineService.removeTimelineByTimelineId(timelineId);
			Timeline timeline = timelineService.getTimelinesById(timelineId);
			assertNull(timeline);
		} catch (ProfilelibException e) {
			e.printStackTrace();
			fail();
		}
		
		/**
		 * 在数据库中查询数据，并验证是否相等。
		 */
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable timelineTable = actualDataset.getTable("stm_profilelib_timeline");
			ITable metricTable = actualDataset.getTable("stm_profilelib_metric");
			ITable thresholdTable = actualDataset.getTable("stm_profilelib_threshold");
			
			boolean find = true;
			//验证基线
			for (int i = 0; i < timelineTable.getRowCount(); i++) {
				if (timelineId == Long.parseLong(timelineTable.getValue(i,
						"time_line_id").toString())) {
					find = false;
					break;
				}
			}
			assertTrue(find);
			
			//验证指标
			find = true;
			for (int i = 0; i < metricTable.getRowCount(); i++) {
				if (timelineId == Long.parseLong(metricTable.getValue(i, "timeline_id").toString())) {
					find = false;
					break;
				}
			}
			assertTrue(find);
			
			find = true;
			//验证阈值
			for (int i = 0; i < thresholdTable.getRowCount(); i++) {
				if (Long.parseLong(thresholdTable.getValue(i, "timeline_id").toString()) == timelineId) {
					find = false;
					break;
				}
			}
			assertTrue(find);
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
		
	}
}
