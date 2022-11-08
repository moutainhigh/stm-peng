package com.mainsteam.stm.plugin.sunjes.executors;

import com.mainsteam.stm.plugin.sunjes.amx.AMXManager;
import com.mainsteam.stm.plugin.sunjes.util.SunJESConnInfo;
import com.sun.appserv.management.j2ee.J2EEDomain;
import com.sun.appserv.management.j2ee.J2EEServer;

/**
 * 服务器可用性取值 <br>
 */
public class ServerAvailityExecutor extends BaseExecutor {
	private static String AVAILABLE="1";
	private static String NO_AVAILABLE="0";
	/**
	 * 可用性
	 * 
	 * @param manager
	 *            AMXManager
	 * @return Matric
	 * @throws Exception
	 *             Exception
	 */
	public String getAvailability(final AMXManager manager) {
		J2EEDomain domain = manager.getDomainRoot().getJ2EEDomain();
		J2EEServer server = (J2EEServer) domain.getServerMap().get(
				this.m_connInfo.getInstanceName());
		int availstate=server.getstate();
		if(availstate == 1)
		return AVAILABLE;
		else {
			return NO_AVAILABLE;
		}
	}

	/**
	 * Constructors.
	 * 
	 * @param source
	 *            连接信息
	 */
	public ServerAvailityExecutor(final SunJESConnInfo source) {
		this.m_connInfo = source;
	}

}
