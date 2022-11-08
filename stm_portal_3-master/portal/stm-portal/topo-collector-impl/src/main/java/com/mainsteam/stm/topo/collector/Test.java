package com.mainsteam.stm.topo.collector;

import com.mainsteam.stm.topo.collector.TestMBean;

public class Test implements TestMBean{

	@Override
	public String test() {
		System.out.println("xie debug == test start");
		return "Test OK";
	}
	public void before(){
		System.out.println("xie debug == test start !");
	}
}
