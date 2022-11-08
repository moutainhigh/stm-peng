package com.mainsteam.stm.plugin.tomcat;

import org.junit.Test;

import com.mainsteam.stm.plugin.tomcat.TomcatCollector.TomcatVersion;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

public class TomcatPluginSessionTest {
	@Test
	public void testInit() throws PluginSessionRunException {
		PluginInitParameter initParameter = new PluginInitParameter() {

			@Override
			public Parameter[] getParameters() {
				ParameterValue[] pvs = new ParameterValue[5];
				for (int i = 0; i < pvs.length; i++) {
					pvs[i] = new ParameterValue();
				}
				pvs[0].setKey("tomcatusername");
				pvs[0].setValue("tomcat");
				pvs[1].setKey("tomcatpassword");
				pvs[1].setValue("tomcat");
				pvs[2].setKey("tomcatport");
				pvs[2].setValue("8080");
				pvs[3].setKey("IP");
				pvs[3].setValue("127.0.0.1");
				pvs[4].setKey("tomcatVersion");
				pvs[4].setValue("7");
				return pvs;
			}

			@Override
			public String getParameterValueByKey(String arg0) {
				// TODO Auto-generated method stub
				return null;
			}
		};
		TomcatPluginSession session = new TomcatPluginSession();
		session.init(initParameter);
		System.out.println(session.isAlive());
	}

//	@Test
//	public void testcon() {
//		TomcatCollector.TomcatVersion version = TomcatVersion.Tomcat7x;
//		TomcatCollector iiCollector = new TomcatCollector(
//				"http://127.0.0.1:8080/manager/status", "tomcat", "tomcat",
//				version);
//	}

	@Test
	public void testData() {
		TomcatCollector.TomcatVersion version = TomcatVersion.Tomcat7x;
		TomcatCollector session = new TomcatCollector(
				"http://127.0.0.1:8080/manager/status", "tomcat", "tomcat",
				version);
		// System.out.println(session.getJVMTotal(version));
		// System.out.println(session.getJVMInitial(version));
		// System.out.println(session.getJVMMemoryPool(version));
		// System.out.println(session.getJVMMaximum(version));
		System.out.println(session.getJVMUsed(version));
		// System.out.println(session.getJVMType(version));
	}
}

class ParameterValue implements Parameter {

	private String key;
	private String value;
	private String type;

	public ParameterValue() {
	}

	public String getType() {
		return type;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
