package com.mainsteam.stm.plugin.weblogic;

import javax.management.remote.JMXConnector;

public class WeblogicBo {
	private String instancename;
	private JMXConnector jmxConnector;
	
	public JMXConnector getJmxConnector() {
		return jmxConnector;
	}

	public void setJmxConnector(JMXConnector jmxConnector) {
		this.jmxConnector = jmxConnector;
	}

	public String getInstancename() {
		return instancename;
	}

	public void setInstancename(String instancename) {
		this.instancename = instancename;
	}
	
}
