/**
 * 
 */
package com.mainsteam.stm.pluginserver;

import static org.junit.Assert.assertNotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.mainsteam.stm.plugin.snmptest.SimplePluginInitParameter;
import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginprocessor.ResultSetMetaInfo;
import com.mainsteam.stm.pluginserver.adapter.PluginResponseClient;
import com.mainsteam.stm.pluginserver.constant.PluginRequestEnum;
import com.mainsteam.stm.pluginserver.message.PluginConvertParameter;
import com.mainsteam.stm.pluginserver.message.PluginProcessParameter;
import com.mainsteam.stm.pluginserver.message.PluginRequest;
import com.mainsteam.stm.pluginserver.message.PluginResult;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginArrayExecutorParameter;

/**
 * @author ziw
 * 
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration({ "classpath*:META-INF/services/public-*-beans.xml",
		"classpath*:META-INF/services/server-*-beans.xml" })
public class PluginServerTest {
	
	@Resource(name="pluginContainer")
	private PluginContainer container;

	/**
	 * @throws java.lang.Exception
	 */
	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		System.setProperty("caplibs.path", "../../../Capacity/cap_libs");
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

	/**
	 * 
	 */
	@Test
	public void testExecutePluginRequest() {
		final List<PluginResult> datas = new ArrayList<>();
		PluginResponseClient responseClient = new PluginResponseClient() {
			@Override
			public void sendPluginReponse(List<PluginResult> response) {
				datas.addAll(response);
			}
		};
		container.setPluginResponseClient(responseClient);
		container.handlePluginRequest(createList());
		assertNotNull(datas);
		for (PluginResult d : datas) {
			System.out.println(Arrays.asList(d.getResultData()));
		}
	}

	private List<PluginRequest> createList() {
		PluginRequest request = new PluginRequest();
		request.setBatch(1);
		request.setCacheExpireTime(10000);
		request.setCollectTime(new Date());
		request.setMetricId("macaddress");
		String resourceId = "JuniperSwitch";
		request.setResourceId(resourceId);

		PluginArrayExecutorParameter pluginExecutorParameter = new PluginArrayExecutorParameter();
		Parameter[] values = new Parameter[3];
		ParameterValue value = new ParameterValue();
		value.setValue("1.3.6.1.2.1.2.2.1.6");
		value.setKey("oid1");
		values[0] = value;
		value = new ParameterValue();
		value.setKey("oid2");
		value.setValue("1.3.6.1.2.1.4.20.1.1");
		values[1] = value;
		value = new ParameterValue();
		value.setKey("method");
		value.setValue("walk");
		values[2] = value;
		pluginExecutorParameter.setParameters(values);
		request.setPluginExecutorParameter(pluginExecutorParameter);

		request.setPluginId("SnmpPlugin");
		SimplePluginInitParameter pluginInitParameter = new SimplePluginInitParameter();
		SimplePluginInitParameter[] initParameters = new SimplePluginInitParameter[3];
		for (int i = 0; i < initParameters.length; i++) {
			initParameters[i] = new SimplePluginInitParameter();
		}
		pluginInitParameter.addParameterValueByKey("IP", "172.16.10.1");
		pluginInitParameter.addParameterValueByKey("community", "public");
		pluginInitParameter.addParameterValueByKey("port", "161");
		request.setPluginInitParameter(pluginInitParameter);

		PluginProcessParameter[] pluginProcessParameters = new PluginProcessParameter[1];
		PluginProcessParameter pluginProcessParameter = new PluginProcessParameter();
		pluginProcessParameter
				.setProcessorClass(PluginResultSetProcessorTestImpl.class);
		pluginProcessParameters[0] = pluginProcessParameter;
		request.setPluginProcessParameters(pluginProcessParameters);

		PluginConvertParameter pluginConvertParameter = new PluginConvertParameter();
		pluginConvertParameter
				.setConverterClass(PluginResultSetConverterTestImpl.class);
		request.setPluginConvertParameter(pluginConvertParameter);

		request.setPluginRequestType(PluginRequestEnum.monitor);
		request.setPluginSessionKey("testKey");
		request.setRequestId(1);

		ResultSetMetaInfo metaInfo = new ResultSetMetaInfo();
		metaInfo.addColumnName("cpu");
		metaInfo.addColumnName("desc");
		request.setResultSetMetaInfo(metaInfo);

		List<PluginRequest> list = new ArrayList<>(1);
		list.add(request);
		return list;
	}

}
