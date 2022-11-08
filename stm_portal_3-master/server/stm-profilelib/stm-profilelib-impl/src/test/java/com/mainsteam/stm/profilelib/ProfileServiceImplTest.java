package com.mainsteam.stm.profilelib;

import static org.junit.Assert.assertNotEquals;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

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
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.InputSource;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.exception.CapabilityException;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.Profile;
import com.mainsteam.stm.profilelib.obj.ProfileInfo;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.profilelib.obj.ProfileThreshold;
import com.mainsteam.stm.profilelib.obj.Threshold;
import com.mainsteam.stm.profilelib.objenum.ProfileTypeEnum;
import com.mainsteam.stm.profilelib.util.ProfileCache;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
	"classpath*:META-INF/services/portal-*-beans.xml",
	"classpath*:META-INF/services/test-service-profilelib-beans.xml"})
public class ProfileServiceImplTest extends DataSourceBasedDBTestCase{

	@Resource
	private ProfileService profileService;
	
	@Resource
	private CapacityService capacityService;
	
	@Resource(name = "defaultDataSource")
	private DataSource source;
	
	@Resource
	private ProfileCache profileCache;
	@Rule 
	public ExpectedException thrown = ExpectedException.none();
	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("caplibs.path", "../../../Capacity\\cap_libs");
		System.setProperty("testCase","test");
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
		return DatabaseOperation.NONE;
	}
	
	@Override
	protected IDataSet getDataSet() throws Exception {
		FlatXmlProducer producer = new FlatXmlProducer(new InputSource(
				"src/test/resources/profile_dataset.xml"));
		IDataSet dataSet = new FlatXmlDataSet(producer);
		return dataSet;
	}
	
	@Override
	protected DataSource getDataSource() {
		return this.source;
	}

	@Test
	public void testAddMonitorUseDefault() throws ProfilelibException {
		//添加到监控
		long instId = 109865;
		System.out.println("开始加入监控");
		long profileId = 0;
		try {
			profileId = profileService.addMonitorUseDefault(instId);
		} catch (ProfilelibException e) {
			e.printStackTrace();
			fail();
		}
		/**
		 * 在数据库中查询数据，并验证是否相等。
		 */
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable profileInfoTable = actualDataset.getTable("stm_profilelib_main");
			ITable metricTable = actualDataset.getTable("stm_profilelib_metric");
			ITable profileInanceTable = actualDataset.getTable("stm_profilelib_instance");
			
			//验证策略存在
			boolean find = false;
			for (int i = 0; i < profileInfoTable.getRowCount(); i++) {
				if (profileId == Long.parseLong(profileInfoTable.getValue(i,
						"profile_id").toString())) {
					System.out.println("找到了该策略");
					find = true;
					break;
				}
			}
			assertTrue(find);
			find = false;
			//验证策略指标
			for (int i = 0; i < metricTable.getRowCount(); i++) {
				if (profileId == Long.parseLong(metricTable.getValue(
						i, "profile_id").toString())) {
					System.out.println("找到了监控策略指标");
					find = true;
					break;
				}
			}
			assertTrue(find);
			find = false;
			//验证实例是否监控
			for (int i = 0; i < profileInanceTable.getRowCount(); i++) {
				if (profileId == Long.parseLong(profileInanceTable.getValue(
						i, "profile_id").toString())
						&& instId == Long.parseLong(profileInanceTable.getValue(i, "instance_id").toString())) {
					System.out.println("找到了监控策略的实例");
					find = true;
					break;
				}
			}
			assertTrue(find);
			
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
		//演示已经添加到监控
		long instId_hasMonitor = 109866;
		thrown.expect(ProfilelibException.class);
		profileId = profileService.addMonitorUseDefault(instId_hasMonitor);
	}

	private boolean validateProfileInfo(ProfileInfo profileInfo,ITable profileInfoTable) throws NumberFormatException, DataSetException{
		boolean find = false;
		//查找策略值
		for (int i = 0; i < profileInfoTable.getRowCount(); i++) {
			if (profileInfo.getProfileId()== Long.parseLong(profileInfoTable.getValue(i,
					"profile_Id").toString())) {
				System.out.println("找到了策略，进行比较");
				assertEquals(Long.parseLong(profileInfoTable.getValue(i,
						"profile_id").toString()), profileInfo.getProfileId());
				assertEquals(profileInfoTable.getValue(i, "profile_name"),
						profileInfo.getProfileName());
				assertEquals(profileInfoTable.getValue(i, "profile_desc"),
						profileInfo.getProfileDesc());
				assertEquals(profileInfoTable.getValue(i, "resource_id"),
						profileInfo.getResourceId());
				assertEquals(profileInfoTable.getValue(i, "profile_type"),
						profileInfo.getProfileType().toString());
				assertEquals(profileInfoTable.getValue(i, "update_user"),
						profileInfo.getUpdateUser());
				assertEquals(profileInfoTable.getValue(i, "update_user_domain"),
						profileInfo.getUpdateUserDomain());
				find = true;
				break;
			}
		}
		return find;
	}
	
//	private boolean validateMetrics(List<ProfileMetric> ProfileMetrics,ITable metricTable,List<ProfileThreshold> profileThresholds) throws NumberFormatException, DataSetException{
//		boolean find = false;
//		//查找指标值
//		for(ProfileMetric profileMetric : ProfileMetrics){
//			//System.out.println("查找ID:" + profileMetric.getProfileId() + " " + profileMetric.getTimeLineId() + " "+profileMetric.getMetricId());
//			for (int i = 0; i < metricTable.getRowCount(); i++) {
//				System.out.println("profileId:" +metricTable.getValue(
//						i, "profile_id"));
//				System.out.println("metric:"+metricTable.getValue(i, "metric_id"));
//				if (profileMetric.getProfileId()== Long.parseLong(metricTable.getValue(
//						i, "profile_id").toString())
//						&& profileMetric.getMetricId().equals(metricTable.getValue(i, "metric_id"))
//						) {
//					System.out.println("找到了策略指标，进行比较");
//					assertEquals(Long.parseLong(metricTable.getValue(i,
//							"alarm_repeat").toString()), profileMetric.getAlarmFlapping());
//					assertEquals(metricTable.getValue(i,
//							"dict_frequency_id"), profileMetric.getDictFrequencyId());
//					find = true;
//					if(profileMetric.getMetricThresholds() != null){
//						profileThresholds.addAll(profileMetric.getMetricThresholds());
//					}
//					break;
//				}
//			}
//			
//		}
//		return find;
//	}
	
//	private boolean validatethresholds(List<ProfileThreshold> profileThresholds,ITable thresholdTable) throws NumberFormatException, DataSetException{
//		if(profileThresholds == null){
//			return true;
//		}
//		boolean find = false;
//		int count = 0;
//		for (ProfileThreshold profileThreshold : profileThresholds) {
//			//System.out.println("查找ID:" + profileThreshold.getProfileId() + " " + profileThreshold.getTimelineId() + " "+profileThreshold.getMetricId());
//			for (int i = 0; i < thresholdTable.getRowCount(); i++) {
//				if (Long.parseLong(thresholdTable.getValue(i,"mk_id").toString()) == profileThreshold.getMkId()) {
//					System.out.println("找到策略指标阈值比较");
//					assertEquals(Long.parseLong(thresholdTable.getValue(
//							i, "profile_id").toString()),profileThreshold.getProfileId());
//					assertEquals(Long.parseLong(thresholdTable.getValue(i, "timeline_id").toString()), profileThreshold.getTimelineId());
//					assertEquals(MetricStateEnum.valueOf(thresholdTable.getValue(i,"dict_metric_state").toString()),profileThreshold.getDictMetricState());
//					assertEquals(thresholdTable.getValue(i,"expression_desc").toString(),profileThreshold.getExpressionDesc());
//					assertEquals(thresholdTable.getValue(i,"threshold_value").toString(),profileThreshold.getThresholdValue());
//					assertEquals(thresholdTable.getValue(i,"expression_operator").toString(),profileThreshold.getExpressionOperator());
//					count++;
//				}
//			}
//		}
//		if(count == profileThresholds.size()){
//			find = true;
//		}
//		return find;
//	}
//	
//	@Test
//	public void testAddMonitorUseSpecial() throws Exception {
//		String mainInstanceId = INST_ID;
//		String mainProfileId = ID_PROFILE;
//		Map<String, String> subInstId2ProfileIds = new HashMap<String, String>();
//		subInstIdt2ProfileIds.put(SUB_INST_ID, SUB_PROFILE_ID);
//		// boolean success =
//		// this.profileService.addMonitorUseSpecial(mainInstanceId,
//		// mainProfileId, subInstId2ProfileIds);
//		// assertTrue(success);
//
//		// ProfileInstRelationPO mainInstRelationPO =
//		// this.profileInstRelDAO.getInstRelationByInstId(mainInstanceId);
//		// assertTrue(mainInstRelationPO != null);
//		// assertTrue(mainInstRelationPO.getParentInstId()==null);
//		// assertTrue(mainInstRelationPO.getProfileId().equals(mainProfileId));
//
//		for (Entry<String, String> subInstId2ProfileId : subInstId2ProfileIds
//				.entrySet()) {
//			String subInstId = subInstId2ProfileId.getKey();
//			String subProfileId = subInstId2ProfileId.getValue();
//			// ProfileInstRelationPO subInstRelationPO =
//			// profileInstRelDAO.getInstRelationByInstId(subInstId);
//			// assertTrue(subInstRelationPO.getParentInstId().equals(mainInstanceId));
//			// assertTrue(subInstRelationPO.getProfileId().equals(subProfileId));
//		}
//	}

	@Test
	public void testCreateMonitorUsePersonalize(){
		long profileId = 1;
		Profile profile = null;
		try {
			profile = profileService.getProfilesById(profileId);
			profileId = profileService.createPersonalizeProfile(profile);
		    profile = profileService.getProfilesById(profileId);
		} catch (ProfilelibException e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(profile);
		
		System.out.println("策略名称:" + profile.getProfileInfo().getProfileName());
		/**
		 * 在数据库中查询数据，并验证是否相等。
		 */
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable profileInfoTable = actualDataset.getTable("stm_profilelib_main");
			ITable metricTable = actualDataset.getTable("stm_profilelib_metric");
			ITable profileInanceTable = actualDataset.getTable("stm_profilelib_instance");
			
			//验证策略存在
			boolean find = false;
			for (int i = 0; i < profileInfoTable.getRowCount(); i++) {
				if (profileId == Long.parseLong(profileInfoTable.getValue(i,
						"profile_Id").toString())) {
					System.out.println("找到了该策略");
					find = true;
					break;
				}
			}
			//验证策略指标
			for (int i = 0; i < metricTable.getRowCount(); i++) {
				if (profileId == Long.parseLong(metricTable.getValue(
						i, "profile_id").toString())) {
					System.out.println("找到了监控策略指标");
					find = true;
					break;
				}
			}
			assertTrue(find);
			
			//验证实例是否监控
			for (int i = 0; i < profileInanceTable.getRowCount(); i++) {
				if (profileId == Long.parseLong(profileInanceTable.getValue(
						i, "profile_id").toString())
						) {
					System.out.println("找到了监控策略的实例");
					find = true;
					break;
				}
			}
			assertTrue(find);
			
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
		assertNotNull(profile.getChildren());
		assertTrue(profile.getChildren().size() > 0);
		assertNotNull(profile.getMetricSetting());
		assertNotNull(profile.getMetricSetting().getMetrics());
		assertTrue(profile.getMetricSetting().getMetrics().size()>0);
		for (Profile childProfile : profile.getChildren()) {
			assertNotNull(childProfile.getMetricSetting());
			assertNotNull(childProfile.getMetricSetting().getMetrics());
			//assertTrue(childProfile.getMetricSetting().getMetrics().size()>0);
			System.out.println("子策略名称："
					+ childProfile.getProfileInfo().getProfileName());
		}
		assertEquals(profileId, profile.getProfileInfo().getProfileId());
	}
	
	@Test
	public void testUpdateProfileMetricFrequency() throws Exception {
		long profileId = 1;
		String metricId = "cpuUsage";
		String metricFrequency = "min11";
		Map<String,String> MetricFrequencys = new HashMap<String, String>();
		MetricFrequencys.put(metricId, metricFrequency);
		profileService.updateProfileMetricFrequency(profileId, MetricFrequencys);
		
		/**
		 * 在数据库中查询数据，并验证是否相等。
		 */
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable metricTable = actualDataset.getTable("stm_profilelib_metric");
			
			//验证策略指标
			for (int i = 0; i < metricTable.getRowCount(); i++) {
				if (profileId == Long.parseLong(metricTable.getValue(
						i, "profile_id").toString()) 
						&& metricId.equals(metricTable.getValue(
						i, "metric_id"))) {
					assertEquals(metricFrequency, metricTable.getValue(i,"dict_frequency_id"));
					break;
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testUpdateProfileMetricMonitor(){
		long profileId = 1;
		String metricId = "cpuUsage";
		boolean metricMonitor = false;
		Map<String,Boolean> monitor = new HashMap<String, Boolean>();
		monitor.put(metricId, metricMonitor);
		try {
			profileService.updateProfileMetricMonitor(profileId, monitor);
		} catch (ProfilelibException e) {
			e.printStackTrace();
			fail();
		}
		
		/**
		 * 在数据库中查询数据，并验证是否相等。
		 */
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable metricTable = actualDataset.getTable("stm_profilelib_metric");
			
			//验证策略指标
			for (int i = 0; i < metricTable.getRowCount(); i++) {
				if (profileId == Long.parseLong(metricTable.getValue(
						i, "profile_id").toString()) 
						&& metricId.equals(metricTable.getValue(
						i, "metric_id"))) {
					assertFalse(Boolean.parseBoolean(metricTable.getValue(i,"is_use").toString()));
					break;
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testUpdateProfileMetricAlarm(){
		long profileId = 1;
		String metricId = "cpuUsage";
		boolean metricAlarm = false;
	    Map<String,Boolean> MetricAlarms = new HashMap<>();
	    MetricAlarms.put(metricId, metricAlarm);
		try {
			profileService.updateProfileMetricAlarm(profileId, MetricAlarms);
		} catch (ProfilelibException e) {
			e.printStackTrace();
			fail();
		}
		
		/**
		 * 在数据库中查询数据，并验证是否相等。
		 */
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable metricTable = actualDataset.getTable("stm_profilelib_metric");
			
			//验证策略指标
			for (int i = 0; i < metricTable.getRowCount(); i++) {
				if (profileId == Long.parseLong(metricTable.getValue(
						i, "profile_id").toString()) 
						&& metricId.equals(metricTable.getValue(
						i, "metric_id"))) {
					assertFalse(Boolean.parseBoolean(metricTable.getValue(i,"is_alarm").toString()));
					break;
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
	}

	@Test
	public void testUpdateProfileMetricThreshold(){
		long mk_id_one = 1;
		long mk_id_two = 2;
		List<Threshold> thresholds = new ArrayList<>();
		Threshold threshold = new Threshold();
		threshold.setThreshold_mkId(mk_id_one);
		threshold.setExpressionDesc(">23");
		threshold.setExpressionOperator(">");
		threshold.setThresholdValue("23");
		thresholds.add(threshold);
		
		Threshold threshold2 = new Threshold();
		threshold2.setThreshold_mkId(mk_id_two);
		threshold2.setExpressionDesc("<75");
		threshold2.setExpressionOperator("<");
		threshold2.setThresholdValue("75");
		thresholds.add(threshold2);
		
		try {
			profileService.updateProfileMetricThreshold(1,thresholds);
		} catch (ProfilelibException e) {
			e.printStackTrace();
			fail();
		}
		/**
		 * 在数据库中查询数据，并验证是否相等。
		 */
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable metricTable = actualDataset.getTable("stm_profilelib_threshold");
			
			//验证策略指标
			for (int i = 0; i < metricTable.getRowCount(); i++) {
				if (mk_id_one == Long.parseLong(metricTable.getValue(
						i, "mk_id").toString())) {
					assertEquals(metricTable.getValue(
						i, "expression_desc"), thresholds.get(0).getExpressionDesc());
					assertEquals(metricTable.getValue(
							i, "expression_operator"), thresholds.get(0).getExpressionOperator());
					assertEquals(metricTable.getValue(
							i, "threshold_value"), thresholds.get(0).getThresholdValue());
				}else if(mk_id_two == Long.parseLong(metricTable.getValue(
						i, "mk_id").toString())){
					assertEquals(metricTable.getValue(
							i, "expression_desc"), thresholds.get(1).getExpressionDesc());
					assertEquals(metricTable.getValue(
								i, "expression_operator"), thresholds.get(1).getExpressionOperator());
					assertEquals(metricTable.getValue(
								i, "threshold_value"), thresholds.get(1).getThresholdValue());
				}
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testCreateProfile() {
		// System.out.println("TESTCASE_PROFILE0010---测试创建策略...");
		String resourceId = "CiscoSwitch";
		ResourceDef mainDef = capacityService.getResourceDefById(resourceId);
		assertNotNull(mainDef);
		ResourceDef[] chilrends = mainDef.getChildResourceDefs();
		assertNotNull(chilrends);
		assertTrue(chilrends.length > 0);
		System.out.println("创建CiscoSwitch，验证了其有子资源");

		ProfileInfo parentProfileInfo = new ProfileInfo();
		parentProfileInfo.setResourceId(resourceId);
		parentProfileInfo.setProfileType(ProfileTypeEnum.SPECIAL);
		parentProfileInfo.setProfileName("CiscoSwitchName");
		parentProfileInfo.setProfileDesc("CiscoSwitchDesc");
		parentProfileInfo.setUpdateTime(new Date());
		parentProfileInfo.setUpdateUser("sunsht");
		parentProfileInfo.setUpdateUserDomain("domain");
		parentProfileInfo.setDomainId(121212);
		System.out.println("准备创建" + mainDef.getName() + " 的策略基础信息");
		List<ProfileInfo> children = new ArrayList<>();

		for (ResourceDef resourceDef : chilrends) {
			System.out.println("准备创建" + resourceDef.getName() + " 的策略基础信息");
			ProfileInfo child = new ProfileInfo();
			child.setResourceId(resourceDef.getId());
			child.setProfileType(ProfileTypeEnum.SPECIAL);
			child.setProfileName(resourceDef.getName());
			child.setProfileDesc(resourceDef.getDescription());
			child.setUpdateTime(new Date());
			child.setUpdateUser("sunsht");
			child.setUpdateUserDomain("domain");
			children.add(child);
		}

		long profileId = 0;
		
		try {
			System.out.println("开始创建");
			profileId =	profileService.createSpecialProfile(parentProfileInfo, children);
			assertTrue(parentProfileInfo.getProfileId() >= 0);
		} catch (ProfilelibException e) {
			e.printStackTrace();
			fail();
		}
		parentProfileInfo.setProfileId(profileId);
		System.out.println("创建结束");
		/**
		 * 在数据库中查询数据，并验证是否相等。
		 */
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable profileInfoTable = actualDataset.getTable("stm_profilelib_main");
			ITable metricTable = actualDataset.getTable("stm_profilelib_metric");
			
			boolean find = validateProfileInfo(parentProfileInfo,profileInfoTable);
			assertTrue(find);
			//验证策略指标
			for (int i = 0; i < metricTable.getRowCount(); i++) {
				if (profileId == Long.parseLong(metricTable.getValue(
						i, "profile_id").toString())) {
					System.out.println("找到了监控策略指标");
					find = true;
					break;
				}
			}
			assertTrue(find);
		
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testCopyProfile() {
		long copyProfileId = 1;

		String copyProfileName = "复制的策略Name";
		String copyProfileDesc = "复制的策略Desc";
		long domainId = 3333333;
		Profile p = null;
		System.out.println("准备要复制的策略信息 copyProfileId=" + copyProfileId
				+ " copyProfileName=" + copyProfileName + " copyProfileDesc="
				+ copyProfileDesc);
		ProfileInfo profileInfo = new ProfileInfo();
		profileInfo.setDomainId(domainId);
		profileInfo.setProfileDesc(copyProfileDesc);
		profileInfo.setProfileName(copyProfileName);
		try {
			p = profileService.copyProfile(copyProfileId,profileInfo);
		} catch (ProfilelibException e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(p);
		assertNotNull(p.getProfileInfo());
		assertNotEquals(copyProfileId, p.getProfileInfo().getProfileId());
		System.out.println("复制结束");
	}
	/**
	 * 获取策略
	 * 
	 * @throws CapabilityException
	 */
	@Test
	public void testGetProfileById() throws CapabilityException {
		System.out.println("TESTCASE_PROFILE0020---测试读取策略...");
		long profileId = 1;
		Profile profile = null;
		try {
			profile = profileService.getProfilesById(profileId);
		} catch (ProfilelibException e) {
			e.printStackTrace();
			fail();
		}
		assertNotNull(profile);
		
		System.out.println("策略名称:" + profile.getProfileInfo().getProfileName());
		/**
		 * 在数据库中查询数据，并验证是否相等。
		 */
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable profileInfoTable = actualDataset.getTable("stm_profilelib_main");
			ITable metricTable = actualDataset.getTable("stm_profilelib_metric");
			ITable profileInanceTable = actualDataset.getTable("stm_profilelib_instance");
			
			//验证策略存在
			boolean find = false;
			for (int i = 0; i < profileInfoTable.getRowCount(); i++) {
				if (profileId == Long.parseLong(profileInfoTable.getValue(i,
						"profile_Id").toString())) {
					System.out.println("找到了该策略");
					find = true;
					break;
				}
			}
			//验证策略指标
			for (int i = 0; i < metricTable.getRowCount(); i++) {
				if (profileId == Long.parseLong(metricTable.getValue(
						i, "profile_id").toString())) {
					System.out.println("找到了监控策略指标");
					find = true;
					break;
				}
			}
			assertTrue(find);
			
			//验证实例是否监控
			for (int i = 0; i < profileInanceTable.getRowCount(); i++) {
				if (profileId == Long.parseLong(profileInanceTable.getValue(
						i, "profile_id").toString())
						) {
					System.out.println("找到了监控策略的实例");
					find = true;
					break;
				}
			}
			assertTrue(find);
			
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
		assertNotNull(profile.getChildren());
		assertTrue(profile.getChildren().size() > 0);
		assertNotNull(profile.getMetricSetting());
		assertNotNull(profile.getMetricSetting().getMetrics());
		assertTrue(profile.getMetricSetting().getMetrics().size()>0);
		for (Profile childProfile : profile.getChildren()) {
			assertNotNull(childProfile.getMetricSetting());
			assertNotNull(childProfile.getMetricSetting().getMetrics());
			//assertTrue(childProfile.getMetricSetting().getMetrics().size()>0);
			System.out.println("子策略名称："
					+ childProfile.getProfileInfo().getProfileName());
		}
		assertEquals(profileId, profile.getProfileInfo().getProfileId());
	}

	@Test
	public void TestGetProfileBasicInfoByResourceId(){
		String resourceId = "JuniperSwitch1";
		List<String> list = new ArrayList<String>();
		list.add(resourceId);
		List<ProfileInfo> profileInfos = null;
		try {
			profileInfos = profileService.getProfileBasicInfoByResourceId(list,ProfileTypeEnum.DEFAULT);
			assertNotNull(profileInfos);
		} catch (ProfilelibException e) {
			e.printStackTrace();
			fail();
		}
		/**
		 * 在数据库中查询数据，并验证是否相等。
		 */
		
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable profileInfoTable = actualDataset.getTable("stm_profilelib_main");
			ITable metricTable = actualDataset.getTable("stm_profilelib_metric");
			for(ProfileInfo profileInfo : profileInfos){
				boolean find = validateProfileInfo(profileInfo,profileInfoTable);
				assertTrue(find);
				//验证策略指标
				for (int i = 0; i < metricTable.getRowCount(); i++) {
					if (profileInfo.getProfileId() == Long.parseLong(metricTable.getValue(
							i, "profile_id").toString())) {
						System.out.println("找到了监控策略指标");
						find = true;
						break;
					}
				}
				assertTrue(find);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
		
	}

	@Test
	public void TestAddChildProfile(){
		ProfileInfo profileInfo = new ProfileInfo();
		profileInfo.setProfileName("testChildName11");
		profileInfo.setResourceId("CiscoSwitchNIC");
		profileInfo.setProfileType(ProfileTypeEnum.SPECIAL);
		try {
			long profileId = profileService.createChildProfile(401501, profileInfo);
			/**
			 * 在数据库中查询数据，并验证是否相等。
			 */
			
			IDataSet actualDataset = getConnection().createDataSet();
			ITable profileInfoTable = actualDataset.getTable("stm_profilelib_main");
			//验证策略存在
			boolean find = false;
			for (int i = 0; i < profileInfoTable.getRowCount(); i++) {
				if (profileId == Long.parseLong(profileInfoTable.getValue(i,
						"profile_Id").toString())) {
					System.out.println("找到了该策略");
					find = true;
					break;
				}
			}
			assertTrue(find);	
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testAddProfileInstance(){
		long profileId = 1;
		List<Long> instances = new ArrayList<Long>();
		instances.add(12L);
		instances.add(13L);
		try {
			profileService.addProfileInstance(profileId, instances);
		} catch (ProfilelibException e) {
			e.printStackTrace();
			fail();
		}
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable profileInanceTable = actualDataset.getTable("stm_profilelib_instance");
			boolean find = false;
			//验证实例是否监控
			int count = 0;
			for (int i = 0; i < profileInanceTable.getRowCount(); i++) {
				if (profileId == Long.parseLong(profileInanceTable.getValue(
						i, "profile_id").toString())) {
					for(long intanceId : instances){
						if(Long.parseLong(profileInanceTable.getValue(i, "instance_id").toString())==intanceId){
							count++;
						}
					}
					System.out.println("找到了监控策略的实例");
					find = true;
					break;
				}
			}
			if(count == instances.size()){
				find = true;
			}
			assertTrue(find);
			System.out.println("数据库中找到已经监控的值");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void testRemoveInstancesFromProfile(){
		long profileId = 4;
		long intanceId = 109870;
		List<Long> instanceIds = new ArrayList<Long>();
		instanceIds.add(intanceId);
		
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable profileInanceTable = actualDataset.getTable("stm_profilelib_instance");
			boolean find = false;
			//验证实例是否监控
			for (int i = 0; i < profileInanceTable.getRowCount(); i++) {
				if (profileId == Long.parseLong(profileInanceTable.getValue(
						i, "profile_id").toString())
						&& intanceId == Long.parseLong(profileInanceTable.getValue(i, "instance_id").toString())) {
					System.out.println("找到了监控策略的实例，删除之前");
					find = true;
					break;
				}
			}
			assertTrue(find);
			find = false;
			//
			profileService.removeInstancesFromProfile(profileId, instanceIds);
			profileInanceTable = actualDataset.getTable("stm_profilelib_instance");
			//验证实例是否监控
			for (int i = 0; i < profileInanceTable.getRowCount(); i++) {
				if (profileId == Long.parseLong(profileInanceTable.getValue(
						i, "profile_id").toString())
						&& intanceId == Long.parseLong(profileInanceTable.getValue(i, "instance_id").toString())) {
					find = true;
					break;
				}
			}
			assertFalse(find);
			System.out.println("删除之后！数据库中找不到该值");
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}

	@Test
	public void testGetBasicInfoByResourceInstanceId(){
		long intanceId = 109870;
		try {
			ProfileInfo profileInfo = profileService.getBasicInfoByResourceInstanceId(intanceId);
			IDataSet actualDataset = getConnection().createDataSet();
			ITable profileInfoTable = actualDataset.getTable("stm_profilelib_main");
			boolean find = validateProfileInfo(profileInfo, profileInfoTable);
			assertTrue(find);
		} catch (Exception e) {
			e.printStackTrace();
			fail();
		}
	}
	
	@Test
	public void TestgetProfileBasicInfoByResourceIds(){
		String resourceId = "Linux";
		List<String> list = new ArrayList<String>();
		list.add(resourceId);
		List<ProfileInfo> profileInfos = null;
		try {
			profileInfos = profileService.getProfileBasicInfoByResourceIds(list);
			assertNotNull(profileInfos);
		} catch (ProfilelibException e) {
			e.printStackTrace();
			fail();
		}
		/**
		 * 在数据库中查询数据，并验证是否相等。
		 */
		
		try {
			IDataSet actualDataset = getConnection().createDataSet();
			ITable profileInfoTable = actualDataset.getTable("stm_profilelib_main");
			ITable metricTable = actualDataset.getTable("stm_profilelib_metric");
			for(ProfileInfo profileInfo : profileInfos){
				boolean find = validateProfileInfo(profileInfo,profileInfoTable);
				assertTrue(find);
				//验证策略指标
				for (int i = 0; i < metricTable.getRowCount(); i++) {
					if (profileInfo.getProfileId() == Long.parseLong(metricTable.getValue(
							i, "profile_id").toString())) {
						System.out.println("找到了监控策略指标");
						find = true;
						break;
					}
				}
				assertTrue(find);
			}
		} catch (Exception e1) {
			e1.printStackTrace();
			fail();
		}
		
	}
	
	@Test
	public void testGetThreshold(){
		try {
			Profile profile = profileService.getProfilesById(48006);
			long profileId = 48006;
			String metricId = "cpuRate";
			System.out.println(profileId + metricId);
		} catch (ProfilelibException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		long profileId = 40510;
		String metricId = "cpuRate";
		//profileService.load();
//		List<ProfileThreshold> addProfileThresholds  = new ArrayList<ProfileThreshold>(1);
//		ProfileThreshold p = new ProfileThreshold();
//		p.setProfileId(profileId);
//		p.setMetricId(metricId);
//		p.setThresholdValue("89");
//		
//		addProfileThresholds.add(p);
		
		
		
		//profileCache.addProfileThreshold(profileId, metricId, addProfileThresholds);
		List<ProfileThreshold> profileThresholds =  profileCache.getProfileThresholdBymetricId(profileId,metricId);
		for (ProfileThreshold profileThreshold : profileThresholds) {
			StringBuilder b = new StringBuilder();
				b.append("threshold_value=").append(profileThreshold.getThresholdValue());
				System.out.println("th" + b);
		}
	}
}
