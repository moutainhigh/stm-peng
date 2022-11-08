package com.mainsteam.stm.plugin.cache;


import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginArrayExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

public class CachePluginSessionTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}
	
	
	@Test
	public void testInitCacheSession() throws Exception {

		PluginArrayExecutorParameter executorParameter = new PluginArrayExecutorParameter();
		ParameterValue[] pvs = new ParameterValue[1];
		for (int i = 0; i < pvs.length; i++) {
			pvs[i] = new ParameterValue();
		}
		pvs[0].setKey("COMMAND");
		pvs[0].setValue("com.mainsteam.stm.plugin.cache.CacheCollector.getDBBlocks");
		executorParameter.setParameters(pvs);

		PluginSessionContext context = null;
		PluginInitParameter initParameters = new PluginInitParameter() {
			public Parameter[] getParameters() {
				ParameterValue[] pvs = new ParameterValue[6];
				for (int i = 0; i < pvs.length; i++) {
					pvs[i] = new ParameterValue();
				}

				pvs[0].setKey(CachePluginSession.CACHE_DB_NAME);
				pvs[0].setValue("SAMPLES");
				pvs[1].setKey(CachePluginSession.CACHE_PORT);
				pvs[1].setValue("1972");
				pvs[2].setKey(CachePluginSession.CACHE_IP);
				pvs[2].setValue("172.16.7.156");
				pvs[3].setKey(CachePluginSession.CACHE_USERNAME);
				pvs[3].setValue("_SYSTEM");
				pvs[4].setKey(CachePluginSession.CACHE_PASSWORD);
				pvs[4].setValue("cache");
				pvs[5].setKey(CachePluginSession.CACHE_NAMESPACE);
				pvs[5].setValue("%SYS");
				return pvs;
			}

			public String getParameterValueByKey(String key) {
				return null;
			}
		};

		CachePluginSession session = new CachePluginSession();
		session.init(initParameters);
		System.out.println("is alive:" + session.isAlive());
		PluginResultSet pluginResultSet = session.execute(executorParameter,
				context);
		System.out
				.println("result row count:" + pluginResultSet.getRowLength());
		System.out.println("ResultSet is :" + pluginResultSet.toString());
		session.destory();

	}

}
