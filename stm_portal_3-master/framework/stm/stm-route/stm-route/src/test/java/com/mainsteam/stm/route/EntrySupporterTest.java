/**
 * 
 */
package com.mainsteam.stm.route;

import com.mainsteam.stm.route.logic.LogicAppEnum;
import com.mainsteam.stm.route.logic.LogicClient;
import com.mainsteam.stm.route.physical.PhysicalServer;

/**
 * @author ziw
 *
 */
public class EntrySupporterTest implements RouteEntrySupporter {

	private PhysicalServer physicalServer;
	
	private LogicClient logicClient;
	
	/**
	 * 
	 */
	public EntrySupporterTest() {
	}
	
	
	
	/**
	 * @param physicalServer the physicalServer to set
	 */
	public final void setPhysicalServer(PhysicalServer physicalServer) {
		this.physicalServer = physicalServer;
	}



	/**
	 * @param logicClient the logicClient to set
	 */
	public final void setLogicClient(LogicClient logicClient) {
		this.logicClient = logicClient;
	}



	public void start(){
		System.out.println("start set supporter.");
		this.physicalServer.setSupporter(this);
		this.logicClient.setSupporter(this);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.route.RouteEntrySupporter#getNextIp(java.lang.String, int)
	 */
	@Override
	public RouteEntry getNextIp(String distIp, int distPort,LogicAppEnum enum1) {
		RouteEntry r = new RouteEntry();
		r.setIp(distIp);
		r.setPort(distPort);
		System.out.println("getNextIp "+distIp+":"+distPort);
		return null;
	}

	@Override
	public RouteEntry getCurrentIp() {
		RouteEntry entry = new RouteEntry();
		entry.setIp("127.0.0.1");
		entry.setPort(9999);
		return entry;
	}
}
