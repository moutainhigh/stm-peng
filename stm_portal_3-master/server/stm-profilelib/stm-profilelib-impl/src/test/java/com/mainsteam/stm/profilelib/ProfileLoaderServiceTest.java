/**
 * 
 */
package com.mainsteam.stm.profilelib;

import java.text.SimpleDateFormat;
import java.util.List;

import javax.annotation.Resource;
import javax.sql.DataSource;

import org.dbunit.DataSourceBasedDBTestCase;
import org.dbunit.database.DatabaseConfig;
import org.dbunit.dataset.IDataSet;
import org.dbunit.dataset.xml.FlatXmlDataSet;
import org.dbunit.dataset.xml.FlatXmlProducer;
import org.dbunit.operation.DatabaseOperation;
import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.xml.sax.InputSource;

import com.mainsteam.stm.caplib.dict.FrequentEnum;
import com.mainsteam.stm.profilelib.obj.Instance;
import com.mainsteam.stm.profilelib.obj.MonitorProfile;
import com.mainsteam.stm.profilelib.obj.ProfileMetricMonitor;
import com.mainsteam.stm.profilelib.obj.TimelineProfileMetricMonitor;
import com.mainsteam.stm.profilelib.remote.ProfileLoaderService;

/**
 * @author ziw
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
		"classpath*:META-INF/services/server-processer-*-beans.xml" })
public class ProfileLoaderServiceTest extends DataSourceBasedDBTestCase {

	@Resource(name = "defaultDataSource")
	private DataSource source;

	@Resource
	private ProfileLoaderService loaderService;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("caplibs.path", "../../../Capacity\\cap_libs");
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

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.profilelib.remote.ProfileLoaderService#loadProfile(int)}
	 * .
	 */
	@Test
	public void testLoadProfile() {
		int nodeGroupId = 100;
		MonitorProfile[] list = loaderService.loadProfile(nodeGroupId);
		assertNotNull(list);
		assertTrue(list.length > 0);
		long profileId = 1;
		MonitorProfile mp = null;
		for (MonitorProfile monitorProfile : list) {
			assertNotNull(monitorProfile);
			if (monitorProfile.getProfileId() == 1) {
				mp = monitorProfile;
				break;
			}
		}
		assertNotNull(mp);
		assertNotNull(mp.getProfileInstanceRelations());
		assertNotNull(mp.getProfileInstanceRelations().getInstances());
		assertTrue(mp.getProfileInstanceRelations().getInstances().size() > 0);
		for (Instance st : mp.getProfileInstanceRelations().getInstances()) {
			System.out.println(st.getInstanceId());
		}
		assertNotNull(mp.getProfileMetricMonitors());
		assertTrue(mp.getProfileMetricMonitors().size() > 0);
		for (ProfileMetricMonitor st : mp.getProfileMetricMonitors()) {
			assertEquals(profileId, st.getProfileId());
			String metricId = st.getMetricId();
			long timelineId = st.getTimelineId();
			assertTrue(timelineId < 0);
			FrequentEnum freq = st.getMonitorFeq();
			System.out.println(timelineId + ":" + metricId + ":" + freq.name());
		}

		List<TimelineProfileMetricMonitor> timelineProfileMetricMonitors = mp
				.getTimelineProfileMetricMonitors();
		assertNotNull(timelineProfileMetricMonitors);
		assertTrue(timelineProfileMetricMonitors.size() > 0);
		SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mi:ss");
		for (TimelineProfileMetricMonitor timelineProfileMetricMonitor : timelineProfileMetricMonitors) {
			assertEquals(profileId, timelineProfileMetricMonitor.getProfileId());
			assertNotNull(timelineProfileMetricMonitor.getStartTime());
			assertNotNull(timelineProfileMetricMonitor.getEndTime());
			StringBuilder b = new StringBuilder();
			b.append("start:"
					+ dateFormat.format(timelineProfileMetricMonitor
							.getStartTime()));
			b.append(" end:"
					+ dateFormat.format(timelineProfileMetricMonitor
							.getEndTime()));
			b.append(" timelineType:"
					+ dateFormat.format(timelineProfileMetricMonitor
							.getTimelineType().name()));
			System.out.println(b.toString());
			for (ProfileMetricMonitor st : timelineProfileMetricMonitor
					.getProfileMetricMonitors()) {
				assertEquals(profileId, st.getProfileId());
				String metricId = st.getMetricId();
				long timelineId = st.getTimelineId();
				assertTrue(timelineId < 0);
				FrequentEnum freq = st.getMonitorFeq();
				System.out.println(timelineId + ":" + metricId + ":"
						+ freq.name());
			}
		}
	}

	/**
	 * Test method for
	 * {@link com.mainsteam.stm.profilelib.remote.ProfileLoaderService#loadProfileByProfileId(long[])}
	 * .
	 */
	@Test
	public void testLoadProfileByProfileId() {
		long[] profileIds = {1};
		MonitorProfile[] monitorProfiles = loaderService.loadProfileByProfileId(profileIds);
		assertNotNull(monitorProfiles);
		assertTrue(monitorProfiles.length > 0);
		long profileId = 1;
		MonitorProfile mp = null;
		for (MonitorProfile monitorProfile : monitorProfiles) {
			assertNotNull(monitorProfile);
			if (monitorProfile.getProfileId() == 1) {
				mp = monitorProfile;
				break;
			}
		}
		assertNotNull(mp);
		assertNotNull(mp.getProfileInstanceRelations());
		assertNotNull(mp.getProfileInstanceRelations().getInstances());
		assertTrue(mp.getProfileInstanceRelations().getInstances().size() > 0);
		for (Instance st : mp.getProfileInstanceRelations().getInstances()) {
			System.out.println(st.getInstanceId());
		}
		assertNotNull(mp.getProfileMetricMonitors());
		assertTrue(mp.getProfileMetricMonitors().size() > 0);
		for (ProfileMetricMonitor st : mp.getProfileMetricMonitors()) {
			assertEquals(profileId, st.getProfileId());
			String metricId = st.getMetricId();
			long timelineId = st.getTimelineId();
			assertTrue(timelineId < 0);
			FrequentEnum freq = st.getMonitorFeq();
			System.out.println(timelineId + ":" + metricId + ":" + freq.name());
		}

		List<TimelineProfileMetricMonitor> timelineProfileMetricMonitors = mp
				.getTimelineProfileMetricMonitors();
		assertNotNull(timelineProfileMetricMonitors);
		assertTrue(timelineProfileMetricMonitors.size() > 0);
		SimpleDateFormat dateFormat = new SimpleDateFormat("hh:mi:ss");
		for (TimelineProfileMetricMonitor timelineProfileMetricMonitor : timelineProfileMetricMonitors) {
			assertEquals(profileId, timelineProfileMetricMonitor.getProfileId());
			assertNotNull(timelineProfileMetricMonitor.getStartTime());
			assertNotNull(timelineProfileMetricMonitor.getEndTime());
			StringBuilder b = new StringBuilder();
			b.append("start:"
					+ dateFormat.format(timelineProfileMetricMonitor
							.getStartTime()));
			b.append(" end:"
					+ dateFormat.format(timelineProfileMetricMonitor
							.getEndTime()));
			b.append(" timelineType:"
					+ dateFormat.format(timelineProfileMetricMonitor
							.getTimelineType().name()));
			System.out.println(b.toString());
			for (ProfileMetricMonitor st : timelineProfileMetricMonitor
					.getProfileMetricMonitors()) {
				assertEquals(profileId, st.getProfileId());
				String metricId = st.getMetricId();
				long timelineId = st.getTimelineId();
				assertTrue(timelineId < 0);
				FrequentEnum freq = st.getMonitorFeq();
				System.out.println(timelineId + ":" + metricId + ":"
						+ freq.name());
			}
		}
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
}
