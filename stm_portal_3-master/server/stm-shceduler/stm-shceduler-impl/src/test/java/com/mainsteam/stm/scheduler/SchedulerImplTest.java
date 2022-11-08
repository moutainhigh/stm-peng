/**
 * 
 */
package com.mainsteam.stm.scheduler;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.executor.MetricExecutor;
import com.mainsteam.stm.executor.exception.MetricExecutorException;
import com.mainsteam.stm.executor.obj.CustomMetricPluginParameter;
import com.mainsteam.stm.executor.obj.DiscoveryMetricData;
import com.mainsteam.stm.executor.obj.InstanceMetricExecuteParameter;
import com.mainsteam.stm.executor.obj.MetricDiscoveryParameter;
import com.mainsteam.stm.executor.obj.MetricExecuteParameter;
import com.mainsteam.stm.pluginserver.adapter.PluginRequestClient;
import com.mainsteam.stm.scheduler.obj.CustomMetricScheduleTask;
import com.mainsteam.stm.scheduler.obj.DailyLimitedPeriodTimeFire;
import com.mainsteam.stm.scheduler.obj.LimitedPeriodTimeFire;
import com.mainsteam.stm.scheduler.obj.MetricScheduleTask;
import com.mainsteam.stm.scheduler.obj.PeriodTimeFire;
import com.mainsteam.stm.scheduler.obj.ProfileScheduleTask;
import com.mainsteam.stm.scheduler.obj.ScheduleTask;
import com.mainsteam.stm.scheduler.obj.TimelineScheduleTask;

/**
 * @author ziw
 * 
 */
public class SchedulerImplTest {

	private static SchedulerImpl sch;

	private static MetricExecutorMock executorMock;

	private static ProfileScheduleTask defaultProfileScheduleTask;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		if (sch == null) {
			sch = new SchedulerImpl();
			executorMock = new MetricExecutorMock();
			sch.setMetricExecutor(executorMock);

			defaultProfileScheduleTask = new ProfileScheduleTask();
			defaultProfileScheduleTask.setProfileId(1);
			long[] instIds = new long[1];
			instIds[0] = 10000001l;
			defaultProfileScheduleTask.setResourceInstIds(instIds);
			defaultProfileScheduleTask
					.setTasks(new ArrayList<MetricScheduleTask>());
			sch.start();
			sch.schedule(defaultProfileScheduleTask);
		}
	}

	/**
	 * @throws java.lang.Exception
	 */
	@AfterClass
	public static void tearDownAfterClass() throws Exception {
		sch.stop();
		sch = null;
		defaultProfileScheduleTask = null;
		executorMock = null;
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
		List<Long> profileIds = sch.listProfileScheduleTasks();
		if (profileIds.size() > 1) {
			profileIds.remove(defaultProfileScheduleTask.getProfileId());
			sch.removeSchedule(profileIds);
		}
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.scheduler.SchedulerImpl#schedule(com.mainsteam.stm.scheduler.obj.ProfileScheduleTask)}
	 * .
	 */
	@Test
	public void testScheduleDefaultProfileScheduleTask() {
		System.out
				.println("-------------------------testScheduleDefaultProfileScheduleTask-----------start");
		ProfileScheduleTask task = new ProfileScheduleTask();
		task.setProfileId(1123);
		long[] instIds = new long[1];
		instIds[0] = 1;
		task.setResourceInstIds(instIds);

		List<MetricScheduleTask> metricScheduleTasks = new ArrayList<MetricScheduleTask>(
				1);
		MetricScheduleTask mt = new MetricScheduleTask();
		mt.setActive(true);
		mt.setMetricId("testMetric_defaultProfile_task");
		mt.setProfileId(task.getProfileId());
		PeriodTimeFire fire = new SecondPeriodTimeFire(5);
		mt.setTimeFire(fire);
		metricScheduleTasks.add(mt);
		task.setTasks(metricScheduleTasks);

		Date currentDate = new Date();
		sch.schedule(task);
		/**
		 * 等待任务被调用，然后验证任务被触发
		 */
		synchronized (this) {
			try {
				this.wait(6000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		MetricExecuteParameter parameter = executorMock.getExecuteParameter(
				instIds[0], mt.getMetricId());
		assertNotNull(parameter);
		assertNotNull(parameter.getExecuteTime());
		long offset = parameter.getExecuteTime().getTime()
				- currentDate.getTime();
		System.out.println("offset=" + offset);
		assertTrue(offset >= 5000);
		assertTrue(offset < 5000 + 100);
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.scheduler.SchedulerImpl#schedule(com.mainsteam.stm.scheduler.obj.MetricScheduleTask)}
	 * .
	 */
	@Test
	public void testScheduleMetricScheduleTask() {
		MetricScheduleTask mt = new MetricScheduleTask();
		mt.setActive(true);
		mt.setMetricId("testMetric");
		mt.setProfileId(defaultProfileScheduleTask.getProfileId());
		PeriodTimeFire fire = new SecondPeriodTimeFire(5);
		mt.setTimeFire(fire);

		Date currentDate = new Date();
		sch.schedule(mt);
		/**
		 * 等待任务被调用，然后验证任务被触发
		 */
		synchronized (this) {
			try {
				this.wait(6000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		MetricExecuteParameter parameter = executorMock.getExecuteParameter(
				defaultProfileScheduleTask.getResourceInstIds()[0],
				mt.getMetricId());
		assertNotNull(parameter);
		assertNotNull(parameter.getExecuteTime());
		long offset = parameter.getExecuteTime().getTime()
				- currentDate.getTime();
		System.out.println("offset=" + offset);
		assertTrue(offset >= 5000);
		assertTrue(offset < 5000 + 100);

		sch.cancelScheduleByProfileId(
				defaultProfileScheduleTask.getProfileId(), mt.getMetricId());
		MetricScheduleTask metricScheduleTask = sch.getScheduleTask(
				defaultProfileScheduleTask.getProfileId(), mt.getMetricId());
		assertNotNull(metricScheduleTask);
		assertFalse(metricScheduleTask.isActive());
		synchronized (this) {
			try {
				this.wait(6000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		executorMock.clear();
		synchronized (this) {
			try {
				this.wait(6000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		parameter = executorMock.getExecuteParameter(
				defaultProfileScheduleTask.getResourceInstIds()[0],
				mt.getMetricId());
		assertNull(parameter);
		System.out.println("任务已经不再执行了");
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.scheduler.SchedulerImpl#getScheduleTask(java.lang.String, java.lang.String)}
	 * .
	 */
	@Test
	public void testGetScheduleTask() {
		MetricScheduleTask mt = new MetricScheduleTask();
		mt.setActive(true);
		mt.setMetricId("testMetric");
		mt.setProfileId(defaultProfileScheduleTask.getProfileId());
		PeriodTimeFire fire = new SecondPeriodTimeFire(5);
		mt.setTimeFire(fire);

		sch.schedule(mt);
		MetricScheduleTask selectTask = sch.getScheduleTask(mt.getProfileId(),
				mt.getMetricId());
		assertNotNull(selectTask);
		assertEquals(mt.isActive(), selectTask.isActive());
		assertEquals(mt.getProfileId(), selectTask.getProfileId());
		assertEquals(mt.getMetricId(), selectTask.getMetricId());
		assertEquals(mt.getTimeFire(), selectTask.getTimeFire());
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.scheduler.SchedulerImpl#getProfileScheduleTask(java.lang.String)}
	 * .
	 */
	@Test
	public void testGetProfileScheduleTask() {
		ScheduleTask profileScheduleTask = sch
				.getProfileScheduleTask(defaultProfileScheduleTask
						.getProfileId());
		assertNotNull(profileScheduleTask);
		assertEquals(defaultProfileScheduleTask.getProfileId(),
				profileScheduleTask.getProfileId());
		assertEquals(defaultProfileScheduleTask.getClass(),
				profileScheduleTask.getClass());
		Arrays.equals(defaultProfileScheduleTask.getResourceInstIds(),
				((ProfileScheduleTask) profileScheduleTask)
						.getResourceInstIds());
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.scheduler.SchedulerImpl#listProfileScheduleTasks()}
	 * .
	 */
	@Test
	public void testListProfileScheduleTasks() {
		List<Long> profileIds = sch.listProfileScheduleTasks();
		assertNotNull(profileIds);
		assertTrue(profileIds.contains(defaultProfileScheduleTask
				.getProfileId()));
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.scheduler.SchedulerImpl#schedule(java.lang.String, long[])}
	 * .
	 */
	@Test
	public void testScheduleStringLongArray() {
		MetricScheduleTask mt = new MetricScheduleTask();
		mt.setActive(true);
		mt.setMetricId("testMetric");
		mt.setProfileId(defaultProfileScheduleTask.getProfileId());
		PeriodTimeFire fire = new SecondPeriodTimeFire(5);
		mt.setTimeFire(fire);
		sch.schedule(mt);
		/**
		 * 等待任务被调用
		 */
		synchronized (this) {
			try {
				this.wait(6000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		long[] newInstIds = new long[10];
		for (int i = 0; i < newInstIds.length; i++) {
			newInstIds[i] = defaultProfileScheduleTask.getResourceInstIds()[0]
					+ i + 1;
		}
		long old_offset = sch.getOffset_threshold();
		sch.setOffset_threshold(1000);
		sch.schedule(defaultProfileScheduleTask.getProfileId(), newInstIds);
		sch.setOffset_threshold(old_offset);
		/**
		 * 等待任务被调用，然后验证任务被触发
		 */
		synchronized (this) {
			try {
				this.wait(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		for (int i = 0; i < newInstIds.length; i++) {
			MetricExecuteParameter parameter = executorMock
					.getExecuteParameter(newInstIds[i], mt.getMetricId());
			assertNotNull(parameter);
			assertEquals(newInstIds[i], parameter.getResourceInstanceId());
		}
		/**
		 * 等待任务被调用，然后验证任务被触发
		 */
		synchronized (this) {
			try {
				this.wait(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		/**
		 * 测试取消资源实例监控
		 */
		for (int i = 0; i < newInstIds.length; i++) {
			MetricExecuteParameter parameter = executorMock
					.getExecuteParameter(newInstIds[i], mt.getMetricId());
			assertNotNull(parameter);
			assertEquals(newInstIds[i], parameter.getResourceInstanceId());
		}
		
		ProfileScheduleTask task = sch
				.getProfileScheduleTask(defaultProfileScheduleTask
						.getProfileId());
		long[] resourceIds = task.getResourceInstIds();
		System.out.println(Arrays.toString(resourceIds));
		sch.removeScheduleByResourceInstanceIds(newInstIds);
		task = sch
				.getProfileScheduleTask(defaultProfileScheduleTask
						.getProfileId());
		resourceIds = task.getResourceInstIds();
		System.out.println(Arrays.toString(resourceIds));
		for (int i = 0; i < newInstIds.length; i++) {
			int index = Arrays.binarySearch(resourceIds, newInstIds[i]);
			assertTrue(index < 0);
		}
		synchronized (this) {
			try {
				this.wait(6000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		executorMock.clear();
		synchronized (this) {
			try {
				this.wait(6000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		for (int i = 0; i < newInstIds.length; i++) {
			MetricExecuteParameter parameter = executorMock
					.getExecuteParameter(newInstIds[i], mt.getMetricId());
			assertNull(parameter);
		}
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.scheduler.SchedulerImpl#schedule(java.lang.String, com.mainsteam.stm.scheduler.obj.TimelineScheduleTask)}
	 * .
	 */
	@Test
	public void testScheduleStringBaseLineProfileScheduleTask() {
		MetricScheduleTask mt = new MetricScheduleTask();
		mt.setActive(true);
		mt.setMetricId("helloMetric");
		mt.setProfileId(defaultProfileScheduleTask.getProfileId());
		PeriodTimeFire fire = new SecondPeriodTimeFire(5);
		mt.setTimeFire(fire);
		sch.schedule(mt);

		TimelineScheduleTask timelineTask = new TimelineScheduleTask();
		timelineTask.setProfileId(defaultProfileScheduleTask.getProfileId());
		timelineTask.setTimelineId(1002034);
		LimitedPeriodTimeFire limitedPeriodTimeFire = new DailyLimitedPeriodTimeFire();
		Calendar c = Calendar.getInstance();
		int currentSecond = c.get(Calendar.SECOND);
		c.set(Calendar.MINUTE, c.get(Calendar.MINUTE) + 1);
		c.set(Calendar.SECOND, 20);
		Date start = c.getTime();
		c.set(Calendar.SECOND, 50);
		Date end = c.getTime();
		limitedPeriodTimeFire.setLimitedPeriodTime(start.getTime(),
				end.getTime());

		timelineTask.setLimitedPeriodTimeFire(limitedPeriodTimeFire);
		/**
		 * 等待任务被调用，然后验证任务被触发
		 */
		synchronized (this) {
			try {
				this.wait(5000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		MetricExecuteParameter parameter = executorMock.getExecuteParameter(
				defaultProfileScheduleTask.getResourceInstIds()[0],
				mt.getMetricId());
		assertNotNull(parameter);

		mt = new MetricScheduleTask();
		mt.setActive(true);
		mt.setMetricId("helloMetric");
		mt.setProfileId(timelineTask.getProfileId());
		fire = new SecondPeriodTimeFire(1);
		mt.setTimeFire(fire);
		List<MetricScheduleTask> scheduleTasks = new ArrayList<>(1);
		scheduleTasks.add(mt);
		timelineTask.setTasks(scheduleTasks);
		sch.schedule(timelineTask);

		int fireOffsetSecond = 60 - currentSecond + 30;
		/**
		 * 等待任务被调用，然后验证任务被触发
		 */
		synchronized (this) {
			try {
				this.wait(fireOffsetSecond * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		parameter = executorMock.getExecuteParameter(
				defaultProfileScheduleTask.getResourceInstIds()[0],
				mt.getMetricId());
		assertNotNull(parameter);
	}

	@Test
	public void testCustomMetricScheduleTaskByInstanceId() {
		CustomMetricScheduleTask scheduleTask = new CustomMetricScheduleTask();
		CustomMetricPluginParameter parameter = new CustomMetricPluginParameter();
		parameter.setCustomMetricId("customMetricId");
		parameter
				.setInstanceId(defaultProfileScheduleTask.getResourceInstIds()[0]);
		parameter.setPluginId("pluginId");
		scheduleTask.setPluginParameter(parameter);

		scheduleTask.setActive(true);
		scheduleTask.setTimeFire(new SecondPeriodTimeFire(1));
		scheduleTask.setMetricId(parameter.getCustomMetricId());
		sch.schedule(scheduleTask);

		MetricScheduleTask task = sch.getCustomMetricScheduleTask(
				defaultProfileScheduleTask.getProfileId(),
				defaultProfileScheduleTask.getResourceInstIds()[0],
				parameter.getCustomMetricId());
		assertEquals(scheduleTask, task);
		assertEquals(defaultProfileScheduleTask.getProfileId(),
				task.getProfileId());

		/**
		 * 等待任务被调用，然后验证任务被触发
		 */
		synchronized (this) {
			try {
				this.wait(2 * 1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		MetricExecuteParameter executeParameter = executorMock
				.getExecuteParameter(
						defaultProfileScheduleTask.getResourceInstIds()[0],
						scheduleTask.getMetricId());
		assertNotNull(executeParameter);

		sch.removeCustomMetricScheduleTaskByInstanceId(
				defaultProfileScheduleTask.getProfileId(),
				defaultProfileScheduleTask.getResourceInstIds()[0],
				scheduleTask.getMetricId());
		task = sch.getCustomMetricScheduleTask(
				defaultProfileScheduleTask.getProfileId(),
				defaultProfileScheduleTask.getResourceInstIds()[0],
				parameter.getCustomMetricId());
		assertNull(task);

		sch.schedule(scheduleTask);
		task = sch.getCustomMetricScheduleTask(
				defaultProfileScheduleTask.getProfileId(),
				defaultProfileScheduleTask.getResourceInstIds()[0],
				parameter.getCustomMetricId());
		assertNotNull(task);

		sch.removeCustomMetricScheduleTaskByInstanceId(
				defaultProfileScheduleTask.getProfileId(),
				defaultProfileScheduleTask.getResourceInstIds()[0],
				parameter.getCustomMetricId());
		task = sch.getCustomMetricScheduleTask(
				defaultProfileScheduleTask.getProfileId(),
				defaultProfileScheduleTask.getResourceInstIds()[0],
				parameter.getCustomMetricId());
		assertNull(task);
	}
}

class MetricExecutorMock implements MetricExecutor {

	private Map<String, MetricExecuteParameter> executeMap = new HashMap<>();

	private boolean isValid = false;

	private long limitInterval;

	private long lastExecuteTime;

	private long limitStart = 0;

	/**
	 * @return the executeMap
	 */
	protected final Map<String, MetricExecuteParameter> getExecuteMap() {
		return executeMap;
	}

	public void clear() {
		executeMap.clear();
	}

	/**
	 * @param executeMap
	 *            the executeMap to set
	 */
	protected final void setExecuteMap(
			Map<String, MetricExecuteParameter> executeMap) {
		this.executeMap = executeMap;
	}

	/**
	 * @return the isValid
	 */
	protected final boolean isValid() {
		return isValid;
	}

	/**
	 * @param isValid
	 *            the isValid to set
	 */
	protected final void setValid(boolean isValid) {
		this.isValid = isValid;
	}

	/**
	 * @return the limitInterval
	 */
	protected final long getLimitInterval() {
		return limitInterval;
	}

	/**
	 * @param limitInterval
	 *            the limitInterval to set
	 */
	protected final void setLimitInterval(long limitInterval) {
		this.limitInterval = limitInterval;
	}

	@Override
	public List<DiscoveryMetricData> catchDiscoveryMetricDatas(
			List<MetricDiscoveryParameter> bindParameters, String discoveryWay)
			throws MetricExecutorException {
		return null;
	}

	@Override
	public MetricData catchRealtimeMetricData(long instanceID, String metricID)
			throws MetricExecutorException {
		return null;
	}

	private void assertOffsetTime(long executeTime) {
		if (executeTime >= limitStart) {
			assertTrue(executeTime - lastExecuteTime >= limitInterval);
		}
	}

	@Override
	public void execute(List<MetricExecuteParameter> parameters) {
		long temp = System.currentTimeMillis();
		System.out.println("execute execute parameter start.");
		StringBuilder b = new StringBuilder();
		long executeTempTime = 0;
		for (MetricExecuteParameter metricExecuteParameter : parameters) {
			b.append(metricExecuteParameter.getResourceInstanceId())
					.append('.').append(metricExecuteParameter.getMetricId())
					.append(" execute at time ")
					.append(metricExecuteParameter.getExecuteTime())
					.append('\n');
			executeMap.put(
					generateId(metricExecuteParameter.getResourceInstanceId(),
							metricExecuteParameter.getMetricId()),
					metricExecuteParameter);
			executeTempTime = metricExecuteParameter.getExecuteTime().getTime();
		}
		lastExecuteTime = executeTempTime;
		assertOffsetTime(executeTempTime);
		System.out.println(b.toString());
		long offset = (System.currentTimeMillis() - temp);
		System.out.println("execute lossTime=" + offset);
	}

	public MetricExecuteParameter getExecuteParameter(long resourceInstId,
			String metricId) {
		return executeMap.get(generateId(resourceInstId, metricId));
	}

	private String generateId(long resourceInstId, String metricId) {
		return new StringBuilder().append(resourceInstId).append('.')
				.append(metricId).toString();
	}

	@Override
	public void setRequestClient(PluginRequestClient requestClient) {
	}

	@Override
	public List<MetricData> catchRealtimeMetricData(
			List<InstanceMetricExecuteParameter> arg0)
			throws MetricExecutorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getNetworkSystemOid(MetricDiscoveryParameter arg0)
			throws MetricExecutorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MetricData catchMetricDataWithCustomPlugin(
			CustomMetricPluginParameter arg0) throws MetricExecutorException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public MetricData catchRealtimeMetricData(long arg0, String arg1,
			Map<String, String> arg2) throws MetricExecutorException {
		// TODO Auto-generated method stub
		return null;
	}
}
