package com.mainsteam.stm.plugin.pop3;

public class Pop3CollectorUtil {
	private Pop3Collector pop3Collector;
	public Pop3CollectorUtil(){
		pop3Collector=new Pop3Collector();
	}
	public String ip(Pop3Bo pop3Bo){
		return pop3Bo.getIp();
	}
	
	public String port(Pop3Bo pop3Bo){
		return pop3Bo.getPort();
	}
	
	public String availability(Pop3Bo pop3Bo){
		return pop3Collector.availability(pop3Bo);
	}
	/**
	 * 响应时间
	 * @param pop3Bo
	 * @return
	 */
	public String responseTime(Pop3Bo pop3Bo){
		return pop3Collector.ResponseTime();
	}
	/**
	 * 给pluginsession的isalive方法调用
	 * @param pop3Bo
	 * @return
	 */
	public boolean init(Pop3Bo pop3Bo){
		return pop3Collector.connect(pop3Bo);
	}
}
