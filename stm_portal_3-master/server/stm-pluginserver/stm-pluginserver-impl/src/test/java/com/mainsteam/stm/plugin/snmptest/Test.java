package com.mainsteam.stm.plugin.snmptest;

import com.mainsteam.stm.pluginserver.context.PluginSessionContextImpl;

public class Test {

	public Test() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		PluginSessionContextImpl p = new PluginSessionContextImpl();
		p.setThreadLocalRuntimeValue("a", "v");
		System.out.println(p.getThreadLocalRuntimeValue("a"));
	}
	

}
