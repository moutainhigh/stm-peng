package com.mainsteam.stm.plugin.oracleas;

import javax.management.remote.JMXConnector;

public class OracleasBo {
	private String oc4jInstanceName;
	private JMXConnector connection;
	private JMXConnector clusterConnection;

	public JMXConnector getConnection() {
		return connection;
	}

	public void setConnection(JMXConnector connection) {
		this.connection = connection;
	}

	public JMXConnector getClusterConnection() {
		return clusterConnection;
	}

	public void setClusterConnection(JMXConnector clusterConnection) {
		this.clusterConnection = clusterConnection;
	}

	public String getOc4jInstanceName() {
		return oc4jInstanceName;
	}

	public void setOc4jInstanceName(String oc4jInstanceName) {
		this.oc4jInstanceName = oc4jInstanceName;
	}
	
}
