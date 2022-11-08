package com.mainsteam.stm.util;

import com.mainsteam.stm.network.snmp.SnmpTerminal;

public class SnmpTerminalTest {
	public static void main(String[] args) {
		try {
			SnmpTerminal terminal = new SnmpTerminal("172.16.10.254");
			String str = terminal.sendPDU("public", new int[]{1,3,6,1,2,1,1,1,0});
			String str1 = terminal.sendPDU("public", new int[]{1,3,6,1,2,1,1,1,0});
			System.out.println(str);
			System.out.println("设备描述信息："+str1.split("\r")[0]);
			System.out.println("OS版本信息："+str.split("\r")[0].split(",")[2].trim().split(" ")[1]);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
