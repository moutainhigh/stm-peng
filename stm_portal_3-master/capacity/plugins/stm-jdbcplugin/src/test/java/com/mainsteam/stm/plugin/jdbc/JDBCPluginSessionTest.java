package com.mainsteam.stm.plugin.jdbc;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.mainsteam.stm.plugin.jdbc.JDBCPluginSession.JDBCResultSet;
import com.mainsteam.stm.pluginprocessor.ParameterValue;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginArrayExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

public class JDBCPluginSessionTest {

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
	public void testInitMySQLSession() throws Exception {

		PluginArrayExecutorParameter executorParameter = new PluginArrayExecutorParameter();
		ParameterValue[] pvs = new ParameterValue[1];
		for (int i = 0; i < pvs.length; i++) {
			pvs[i] = new ParameterValue();
		}
		pvs[0].setKey("SQL");
		pvs[0].setValue("SELECT VARIABLE_VALUE FROM information_schema.GLOBAL_STATUS WHERE VARIABLE_NAME='Threads_connected'");
		executorParameter.setParameters(pvs);

		PluginSessionContext context = null;
		PluginInitParameter initParameters = new PluginInitParameter() {
			public Parameter[] getParameters() {
				ParameterValue[] pvs = new ParameterValue[6];
				for (int i = 0; i < pvs.length; i++) {
					pvs[i] = new ParameterValue();
				}

				pvs[0].setKey(JDBCPluginSession.JDBCPLUGIN_DB_TYPE);
				pvs[0].setValue("MySQL");
				pvs[1].setKey(JDBCPluginSession.JDBCPLUGIN_JDBC_PORT);
				pvs[1].setValue("3306");
				pvs[2].setKey(JDBCPluginSession.JDBCPLUGIN_IP);
				pvs[2].setValue("172.16.7.200");
				pvs[3].setKey(JDBCPluginSession.JDBCPLUGIN_DB_USERNAME);
				pvs[3].setValue("root");
				pvs[4].setKey(JDBCPluginSession.JDBCPLUGIN_DB_PASSWORD);
				pvs[4].setValue("root3306");
				pvs[5].setKey(JDBCPluginSession.JDBCPLUGIN_DB_NAME);
				pvs[5].setValue("mysql");
				return pvs;
			}

			public String getParameterValueByKey(String key) {
				return null;
			}
		};

		JDBCPluginSession session = new JDBCPluginSession();
		session.init(initParameters);
		System.out.println("is alive:" + session.isAlive());
		PluginResultSet pluginResultSet = session.execute(executorParameter,
				context);
		System.out
				.println("result row count:" + pluginResultSet.getRowLength());
		session.destory();

	}

	@Test
	public void testInitOracleSession() throws Exception {

		PluginArrayExecutorParameter executorParameter = new PluginArrayExecutorParameter();
		ParameterValue[] pvs = new ParameterValue[1];
		for (int i = 0; i < pvs.length; i++) {
			pvs[i] = new ParameterValue();
		}
		pvs[0].setKey("SQL");
		pvs[0].setValue("select utl_inaddr.get_host_address || '_' || instance_name from v$instance");
		executorParameter.setParameters(pvs);

		PluginSessionContext context = null;
		PluginInitParameter initParameters = new PluginInitParameter() {
			public Parameter[] getParameters() {
				ParameterValue[] pvs = new ParameterValue[6];
				for (int i = 0; i < pvs.length; i++) {
					pvs[i] = new ParameterValue();
				}

				pvs[0].setKey(JDBCPluginSession.JDBCPLUGIN_DB_TYPE);
				pvs[0].setValue("Oracle");
				pvs[1].setKey(JDBCPluginSession.JDBCPLUGIN_JDBC_PORT);
				pvs[1].setValue("1521");
				pvs[2].setKey(JDBCPluginSession.JDBCPLUGIN_IP);
				pvs[2].setValue("172.16.7.161");
				pvs[3].setKey(JDBCPluginSession.JDBCPLUGIN_DB_USERNAME);
				pvs[3].setValue("system");
				pvs[4].setKey(JDBCPluginSession.JDBCPLUGIN_DB_PASSWORD);
				pvs[4].setValue("oc4");
				pvs[5].setKey(JDBCPluginSession.JDBCPLUGIN_DB_NAME);
				pvs[5].setValue("orcl");
				return pvs;
			}

			public String getParameterValueByKey(String key) {
				return null;
			}
		};

		JDBCPluginSession session = new JDBCPluginSession();
		session.init(initParameters);
		System.out.println("is alive:" + session.isAlive());
		PluginResultSet pluginResultSet = session.execute(executorParameter,
				context);
		System.out
				.println("result row count:" + pluginResultSet.getRowLength());
		if(pluginResultSet.getRowLength() > 0){
			System.out.println(pluginResultSet.getRow(0)[0]);
		}
		session.destory();

	}
	
	@Test
	public void testInitSQLServerSession() throws Exception {

		PluginArrayExecutorParameter executorParameter = new PluginArrayExecutorParameter();
		ParameterValue[] pvs = new ParameterValue[1];
		for (int i = 0; i < pvs.length; i++) {
			pvs[i] = new ParameterValue();
		}
		pvs[0].setKey("SQL");
		pvs[0].setValue("select count(1) from dbo.sysdatabases");
		executorParameter.setParameters(pvs);

		PluginSessionContext context = null;
		PluginInitParameter initParameters = new PluginInitParameter() {
			public Parameter[] getParameters() {
				ParameterValue[] pvs = new ParameterValue[6];
				for (int i = 0; i < pvs.length; i++) {
					pvs[i] = new ParameterValue();
				}

				pvs[0].setKey(JDBCPluginSession.JDBCPLUGIN_DB_TYPE);
				pvs[0].setValue("SQLServer");
				pvs[1].setKey(JDBCPluginSession.JDBCPLUGIN_JDBC_PORT);
				pvs[1].setValue("1433");
				pvs[2].setKey(JDBCPluginSession.JDBCPLUGIN_IP);
				pvs[2].setValue("172.16.7.161");
				pvs[3].setKey(JDBCPluginSession.JDBCPLUGIN_DB_USERNAME);
				pvs[3].setValue("sa");
				pvs[4].setKey(JDBCPluginSession.JDBCPLUGIN_DB_PASSWORD);
				pvs[4].setValue("Root3306");
				pvs[5].setKey(JDBCPluginSession.JDBCPLUGIN_DB_NAME);
				pvs[5].setValue("master");
				return pvs;
			}

			public String getParameterValueByKey(String key) {
				return null;
			}
		};

		JDBCPluginSession session = new JDBCPluginSession();
		session.init(initParameters);
		System.out.println("is alive:" + session.isAlive());
		PluginResultSet pluginResultSet = session.execute(executorParameter,
				context);
		System.out
				.println("result row count:" + pluginResultSet.getRowLength());
		if(pluginResultSet.getRowLength() > 0){
			System.out.println(pluginResultSet.getRow(0)[0]);
		}
		session.destory();

	}
	
	@Test
	public void testInitSybaseSession() throws Exception {

		PluginArrayExecutorParameter executorParameter = new PluginArrayExecutorParameter();
		ParameterValue[] pvs = new ParameterValue[1];
		for (int i = 0; i < pvs.length; i++) {
			pvs[i] = new ParameterValue();
		}
		pvs[0].setKey("SQL");
		pvs[0].setValue("select srvname from dbo.sysservers where srvid=0");
		executorParameter.setParameters(pvs);

		PluginSessionContext context = null;
		PluginInitParameter initParameters = new PluginInitParameter() {
			public Parameter[] getParameters() {
				ParameterValue[] pvs = new ParameterValue[6];
				for (int i = 0; i < pvs.length; i++) {
					pvs[i] = new ParameterValue();
				}

				pvs[0].setKey(JDBCPluginSession.JDBCPLUGIN_DB_TYPE);
				pvs[0].setValue("Sybase");
				pvs[1].setKey(JDBCPluginSession.JDBCPLUGIN_JDBC_PORT);
				pvs[1].setValue("5000");
				pvs[2].setKey(JDBCPluginSession.JDBCPLUGIN_IP);
				pvs[2].setValue("172.16.7.162");
				pvs[3].setKey(JDBCPluginSession.JDBCPLUGIN_DB_USERNAME);
				pvs[3].setValue("sa");
				pvs[4].setKey(JDBCPluginSession.JDBCPLUGIN_DB_PASSWORD);
				pvs[4].setValue("password123!");
				pvs[5].setKey(JDBCPluginSession.JDBCPLUGIN_DB_NAME);
				pvs[5].setValue("win20itsjcq89y");
				return pvs;
			}

			public String getParameterValueByKey(String key) {
				return null;
			}
		};

		JDBCPluginSession session = new JDBCPluginSession();
		session.init(initParameters);
		System.out.println("is alive:" + session.isAlive());
		PluginResultSet pluginResultSet = session.execute(executorParameter,
				context);
		System.out
				.println("result row count:" + pluginResultSet.getRowLength());
		if(pluginResultSet.getRowLength() > 0){
			System.out.println(pluginResultSet.getRow(0)[0]);
		}
		session.destory();

	}

	@Test
	public void testSendCommandString() throws Exception {
		JDBCPluginSession jdbc = new JDBCPluginSession("MySQL", "localhost",
				3306, "root", "", "mysql", "");
		jdbc.connect(1);
		JDBCResultSet r = null;
		try {
			r = jdbc.sendCommand("select count(*) from user");
			System.out.print("用户数:" + r);

			r = jdbc.sendCommand("select Host,User from user");
			System.out.print("用户:" + r);
		} catch (Exception e) {
			e.printStackTrace();
		}
		jdbc.disconnect();

	}

	@Test
	public void testSendCommandString2() throws Exception {
		JDBCPluginSession jdbc = new JDBCPluginSession("MySQL", "127.0.0.1",
				3306, "root", "", "mysql", "");
		jdbc.connect(1);
		JDBCResultSet r = null;
		try {
			r = jdbc.sendCommand("show status", true);
			System.out.print("lines:" + r);
		} catch (Exception e) {
			e.printStackTrace();
		}
		jdbc.disconnect();

	}

}
