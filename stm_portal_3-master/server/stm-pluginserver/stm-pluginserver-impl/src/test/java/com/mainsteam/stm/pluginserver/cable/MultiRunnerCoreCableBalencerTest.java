package com.mainsteam.stm.pluginserver.cable;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mainsteam.stm.pluginserver.pool.PluginSessionPool;
import com.mainsteam.stm.pluginsession.PluginSession;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

public class MultiRunnerCoreCableBalencerTest {

	private MultiRunnerCoreCableBalencer balencer;

	private PluginSessionPool pool;

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
		if (balencer == null) {
			balencer = new MultiRunnerCoreCableBalencer();
			pool = new PluginSessionPool() {

				@Override
				public void returnSession(PluginSession session)
						throws Exception {
					// TODO Auto-generated method stub

				}

				@Override
				public String getPluginSessionKey() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public String getPluginId() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public int getMaxIdle() {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public int getMaxActive() {
					// TODO Auto-generated method stub
					return 0;
				}

				@Override
				public PluginInitParameter getInitParameter() {
					// TODO Auto-generated method stub
					return null;
				}

				@Override
				public void destory(PluginSession session) throws Exception {
					// TODO Auto-generated method stub

				}

				@Override
				public void destory() throws Exception {
					// TODO Auto-generated method stub

				}

				@Override
				public PluginSession borrowSession() throws Exception {
					// TODO Auto-generated method stub
					return null;
				}
			};
			// balencer.start();
		}
	}

	@After
	public void tearDown() throws Exception {
		RunnerCoreCableRangeLevel[] cableRangeLevels = RunnerCoreCableRangeLevel
				.values();
		for (RunnerCoreCableRangeLevel runnerCoreCableRangeLevel : cableRangeLevels) {
			MultiRunnerCoreCable muliCable = balencer
					.getMultiRunnerCoreCable(runnerCoreCableRangeLevel);
			RunnerCoreCable cable = null;
			while ((cable = muliCable.next()) != null) {
				muliCable.removeCoreCable(cable);
			}
		}
	}

	@Test
	public void testSelectMultiRunnerCoreCableForBanlencer() {
		MultiRunnerCoreCable coreCable = balencer
				.getMultiRunnerCoreCable(RunnerCoreCableRangeLevel.FIRST);
		RunnerCoreCable cable = new RunnerCoreCable("test1", pool, coreCable);
		assertEquals(1, coreCable.getRunnerCoreCableSize());
		assertEquals(cable, coreCable.getRunnerCoreCable(0));

		MultiRunnerCoreCable selectCoreCable = balencer
				.selectMultiRunnerCoreCableForBanlencer();
		assertNotEquals(coreCable, selectCoreCable);

		cable = new RunnerCoreCable("test1", pool, selectCoreCable);
		assertEquals(1, coreCable.getRunnerCoreCableSize());
		assertEquals(cable, selectCoreCable.getRunnerCoreCable(0));

		MultiRunnerCoreCable selectCoreCable1 = balencer
				.selectMultiRunnerCoreCableForBanlencer();
		assertNotEquals(coreCable, selectCoreCable1);
		assertNotEquals(coreCable, selectCoreCable);
	}

	@Test
	public void testJudgeMoveCable() {
		MultiRunnerCoreCable first = balencer
				.getMultiRunnerCoreCable(RunnerCoreCableRangeLevel.FIRST);
		int firstMaxSize = RunnerCoreCableRangeLevel.FIRST.getMaxActiveSize();
		int halfSize = firstMaxSize / 2;
		for (int i = 0; i < halfSize; i++) {
			RunnerCoreCable cable = new RunnerCoreCable("FIRST_test" + (i + 1),
					pool, first);
			assertEquals(i + 1, first.getRunnerCoreCableSize());
			assertEquals(cable, first.getRunnerCoreCable(i));
		}

		MultiRunnerCoreCable second = balencer
				.getMultiRunnerCoreCable(RunnerCoreCableRangeLevel.SECOND);
		int secondMaxSize = RunnerCoreCableRangeLevel.SECOND.getMaxActiveSize();
		int threeFours = secondMaxSize * 3 / 4;
		for (int i = 0; i < threeFours; i++) {
			RunnerCoreCable cable = new RunnerCoreCable(
					"SECOND_test" + (i + 1), pool, second);
			assertEquals(i + 1, second.getRunnerCoreCableSize());
			assertEquals(cable, second.getRunnerCoreCable(i));
		}
		assertTrue(balencer.judgeMoveCable(first, second));
	}
	
	@Test
	public void testAdjustment(){
		//TODO
	}
}
