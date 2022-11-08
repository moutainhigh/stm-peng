package com.mainsteam.stm.tinytool;

import java.io.IOException;
import java.util.List;

import com.mainsteam.stm.tinytool.obj.SnmpTarget;

public interface TinyToolMBean {
	
	/**
	 * get DSC Ping result
	 * @param ip
	 * @return
	 */
	public String ping(String pingCmd);
	
	/**
	 * get tracert result
	 * @return
	 */
	public String tracert();
	
	/**
	 * exec cmd
	 * @param cmd
	 * @return
	 */
	public String cmd(String cmd);
	
	/**
	 * snmp Test
	 * @param snmpTarget
	 * @return
	 */
	public List<String> snmpTest(SnmpTarget snmpTarget) throws IOException, InterruptedException;
	
}
