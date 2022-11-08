/**
 * 
 */
package com.mainsteam.stm.metric.service.impl.job;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mainsteam.stm.caplib.dict.FrequentEnum;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.common.metric.obj.CustomMetricMonitorInfo;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.metric.CustomMetricChangeService;
import com.mainsteam.stm.metric.CustomMetricService;
import com.mainsteam.stm.metric.exception.CustomMetricException;
import com.mainsteam.stm.metric.obj.CustomMetric;
import com.mainsteam.stm.metric.obj.CustomMetricBind;
import com.mainsteam.stm.metric.obj.CustomMetricChange;
import com.mainsteam.stm.metric.obj.CustomMetricChangeResult;
import com.mainsteam.stm.metric.obj.CustomMetricCollectParameter;
import com.mainsteam.stm.metric.obj.CustomMetricDataProcess;
import com.mainsteam.stm.metric.obj.CustomMetricInfo;
import com.mainsteam.stm.metric.objenum.CustomMetricChangeEnum;
import com.mainsteam.stm.metric.objenum.CustomMetricDataProcessWayEnum;
import com.mainsteam.stm.profilelib.ProfileService;
import com.mainsteam.stm.profilelib.deploy.CustomMetricCollectDeployMBean;
import com.mainsteam.stm.profilelib.exception.ProfilelibException;
import com.mainsteam.stm.profilelib.obj.ProfileInfo;
import com.mainsteam.stm.rpc.client.OCRPCClient;

/**
 * @author ziw
 * 
 */
public class CustomMetricDeployJobTest {
	CustomMetricDeployJob job = new CustomMetricDeployJob();
	
	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@Before
	public void setUp() throws Exception {
	}

	/**
	 * @throws java.lang.Exception
	 */
	@After
	public void tearDown() throws Exception {
	}

	private void mockInstances(IMocksControl control)
			throws InstancelibException {
		ResourceInstanceService resourceInstanceService = control
				.createMock(ResourceInstanceService.class);
		ResourceInstance inst1 = new ResourceInstance();
		inst1.setId(1L);
		inst1.setDiscoverNode("1");
		EasyMock.expect(
				resourceInstanceService.getResourceInstance(EasyMock.eq(1L)))
				.andReturn(inst1).atLeastOnce();
		ResourceInstance inst2 = new ResourceInstance();
		inst2.setId(2L);
		inst2.setDiscoverNode("1");
		EasyMock.expect(
				resourceInstanceService.getResourceInstance(EasyMock.eq(2L)))
				.andReturn(inst2).atLeastOnce();
		job.setResourceInstanceService(resourceInstanceService);
	}

	private void mockCustomMetricService(String metricId, String pluginId,
			IMocksControl control) throws CustomMetricException {
		List<CustomMetricCollectParameter> customMetricPluginParameters = new ArrayList<>();
		CustomMetricCollectParameter parameter = new CustomMetricCollectParameter();
		parameter.setMetricId(metricId);
		parameter.setParameterKey("method");
		parameter.setParameterType("");
		parameter.setParameterValue("walk");
		parameter.setPluginId(pluginId);
		customMetricPluginParameters.add(parameter);

		CustomMetricCollectParameter parameter1 = new CustomMetricCollectParameter();
		parameter1.setMetricId(metricId);
		parameter1.setParameterKey("");
		parameter1.setParameterType("");
		parameter1.setParameterValue("1.3.6.8.9.0.1");
		parameter1.setPluginId(pluginId);
		customMetricPluginParameters.add(parameter1);
		CustomMetricService metricService = control
				.createMock(CustomMetricService.class);
		
		List<CustomMetricBind> customMetricBinds = new ArrayList<>();
		CustomMetricBind bind = new CustomMetricBind();
		bind.setInstanceId(1);
		bind.setMetricId(metricId);
		bind.setPluginId(pluginId);
		customMetricBinds.add(bind);
		bind = new CustomMetricBind();
		bind.setInstanceId(2);
		bind.setMetricId(metricId);
		bind.setPluginId(pluginId);
		customMetricBinds.add(bind);
		EasyMock.expect(metricService.getCustomMetricBindsByMetricId(metricId))
				.andReturn(customMetricBinds).atLeastOnce();

		List<CustomMetric> instanceCustomMetricInfos = new ArrayList<>();
		CustomMetric metric = new CustomMetric();
		CustomMetricInfo info = new CustomMetricInfo();
		info.setAlert(true);
		info.setFlapping(1);
		info.setFreq(FrequentEnum.min5);
		info.setId(metricId);
		info.setMonitor(true);
		info.setName(metricId);
		info.setStyle(MetricTypeEnum.PerformanceMetric);
		info.setUnit("%");
		metric.setCustomMetricInfo(info);
		
		CustomMetricDataProcess dataProcess = new CustomMetricDataProcess();
		dataProcess.setDataProcessWay(CustomMetricDataProcessWayEnum.MAX);
		dataProcess.setPluginId("SnmpPlugin");
		dataProcess.setMetricId(metricId);
		metric.setCustomMetricDataProcess(dataProcess);
		instanceCustomMetricInfos.add(metric);
		EasyMock.expect(metricService.getCustomMetricsByInstanceId(2L))
				.andReturn(instanceCustomMetricInfos).atLeastOnce();
		EasyMock.expect(metricService.getCustomMetric(metricId))
				.andReturn(metric).atLeastOnce();
		job.setMetricService(metricService);
	}

	@SuppressWarnings("unchecked")
	private void mockCustomMetricChangeService(String metricId, String pluginId,
			IMocksControl control) {
		CustomMetricChangeService changeService = control
				.createMock(CustomMetricChangeService.class);
		List<Long> changeIdsList = new ArrayList<>();
		long changeId = 1;
		List<CustomMetricChange> customMetricChanges = new ArrayList<>();
		CustomMetricChange change = new CustomMetricChange();
		change.setChangeId(changeId++);
		change.setChangeTime(new Date());
		change.setInstanceId(1);
		change.setMetricId(metricId);
		change.setOccurTime(new Date());
		change.setOperateMode(CustomMetricChangeEnum.ADD_METRIC_PLUGIN_BIND
				.name());
		change.setPluginId(pluginId);
		customMetricChanges.add(change);
		changeIdsList.add(change.getChangeId());

		change = new CustomMetricChange();
		change.setChangeId(changeId++);
		change.setChangeTime(new Date());
		change.setInstanceId(1);
		change.setMetricId(metricId);
		change.setOccurTime(new Date());
		change.setOperateMode(CustomMetricChangeEnum.DELETE_METRIC_PLUGIN_BIND
				.name());
		change.setPluginId(pluginId);
		customMetricChanges.add(change);
		changeIdsList.add(change.getChangeId());

		change = new CustomMetricChange();
		change.setChangeId(changeId++);
		change.setChangeTime(new Date());
		change.setInstanceId(2);
		change.setOccurTime(new Date());
		change.setOperateMode(CustomMetricChangeEnum.INSTANCE_MONITOR.name());
		customMetricChanges.add(change);
		changeIdsList.add(change.getChangeId());

		change = new CustomMetricChange();
		change.setChangeId(changeId++);
		change.setChangeTime(new Date());
		change.setInstanceId(2);
		change.setOccurTime(new Date());
		change.setOperateMode(CustomMetricChangeEnum.INSTANCE_CANCEL_MONITOR
				.name());
		customMetricChanges.add(change);
		changeIdsList.add(change.getChangeId());

		change = new CustomMetricChange();
		change.setChangeId(changeId++);
		change.setChangeTime(new Date());
		change.setMetricId(metricId);
		change.setOccurTime(new Date());
		change.setOperateMode(CustomMetricChangeEnum.METRIC_BASIC_UPDATE.name());
		customMetricChanges.add(change);
		changeIdsList.add(change.getChangeId());

		change = new CustomMetricChange();
		change.setChangeId(changeId++);
		change.setChangeTime(new Date());
		change.setMetricId(metricId);
		change.setOccurTime(new Date());
		change.setOperateMode(CustomMetricChangeEnum.CHANGE_METRIC_PLUGIN_COLLECT
				.name());
		customMetricChanges.add(change);
		changeIdsList.add(change.getChangeId());

		EasyMock.expect(changeService.selectChangesWithNotApply(EasyMock.eq(5000)))
				.andReturn(customMetricChanges).once();

		long[] change_ids = new long[changeIdsList.size()];
		for (int i = 0; i < change_ids.length; i++) {
			change_ids[i] = changeIdsList.get(i);
		}
		List<CustomMetricChangeResult> changeResults = new ArrayList<>();
		for (int i = 0; i < 1; i++) {
			CustomMetricChangeResult changeResult = new CustomMetricChangeResult();
			changeResult.setChangeId(change_ids[i]);
			changeResult.setDcsGroupId(1);
			changeResult.setOperateTime(new Date());
			changeResult.setResultState(false);
			changeResults.add(changeResult);
		}
		EasyMock.expect(
				changeService.getCustomMetricChangeResults(EasyMock
						.aryEq(change_ids))).andReturn(changeResults)
				.once();
		changeService.insertCustomMetricChangeResults(EasyMock.anyObject(List.class));
		EasyMock.expectLastCall().andVoid().atLeastOnce();
		
		changeService.updateCustomMetricChangeResults(EasyMock.anyObject(List.class));
		EasyMock.expectLastCall().andVoid().atLeastOnce();
		
		changeService.updateCustomMetricChangeToApply(EasyMock.anyObject(long[].class));
		EasyMock.expectLastCall().andVoid().atLeastOnce();
		job.setChangeService(changeService);
	}
	
	private void mockProfileService(IMocksControl control) throws ProfilelibException{
		ProfileService profileService = control
				.createMock(ProfileService.class);
		List<Long> allInstanceId = new ArrayList<>();
		allInstanceId.add(1L);
		allInstanceId.add(2L);
		Map<Long, ProfileInfo> map = new HashMap<Long, ProfileInfo>();
		for (Iterator<Long> iterator = allInstanceId.iterator(); iterator
				.hasNext();) {
			Long long1 = (Long) iterator.next();
			ProfileInfo value = new ProfileInfo();
			value.setProfileId(long1);
			map.put(long1, value);
		}
		EasyMock.expect(
				profileService.getBasicInfoByResourceInstanceIds(EasyMock
						.eq(allInstanceId))).andReturn(map).atLeastOnce();
		job.setProfileService(profileService);
	}
	
	private void mockRPCService(IMocksControl control) throws IOException{
		OCRPCClient rpcClient = control.createMock(OCRPCClient.class);
		EasyMock.expect(
				rpcClient.getRemoteSerivce(1,
						CustomMetricCollectDeployMBean.class))
				.andReturn(new CustomMetricCollectDeployMBean() {
					@Override
					public void deployCustomMetricMonitors(
							CustomMetricMonitorInfo[] arg0, Map<Long, Long> arg1) {
						// TODO Auto-generated method stub
						
					}
				}).atLeastOnce();
		job.setRpcClient(rpcClient);
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.metric.service.impl.job.CustomMetricDeployJobBackUpCode#execute(org.quartz.JobExecutionContext)}
	 * .
	 * 
	 * @throws Exception
	 */
	@Test
	public void testExecute() throws Exception {
		IMocksControl control = EasyMock.createControl();
		String metricId = "testMetric";
		String pluginId = "SnmpPlugin";
		mockCustomMetricChangeService(metricId, pluginId, control);
		mockCustomMetricService(metricId, pluginId, control);
		mockInstances(control);
		mockProfileService(control);
		mockRPCService(control);

		control.replay();
		job.execute(null);
		control.verify();
	}
}
