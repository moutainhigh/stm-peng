/**
 * 
 */
package com.mainsteam.stm.node.server;

import java.lang.reflect.Method;

import com.mainsteam.stm.node.LocaleNodeService;
import com.mainsteam.stm.rpc.client.OCRPCClient;
import com.mainsteam.stm.util.SpringBeanUtil;

/**
 * @author ziw
 * 
 */
public class SpringContextStopper implements SpringContextStopperMBean {

	/**
	 * 
	 */
	public SpringContextStopper() {
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see com.mainsteam.stm.launch.SpringContextStopperMBean#stop()
	 */
	@Override
	public void stop() throws Exception {
		String stopper = "com.mainsteam.stm.launch.SpingStopper";
		try {
			Class<?> c = Class.forName(stopper);
			Method mainMethod = null;
			mainMethod = c.getDeclaredMethod("main", String[].class);
			String[] args = new String[0];
			// SpingStopper.main(null);
			mainMethod.invoke(c, (Object) args);
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	public static void main(String[] args) throws Exception {
		try {
			OCRPCClient client = (OCRPCClient) SpringBeanUtil
					.getObject("OCRPClient");
			LocaleNodeService nodeService = (LocaleNodeService) SpringBeanUtil
					.getObject("localNodeService");
			SpringContextStopperMBean stopper = client.getRemoteSerivce(
					nodeService.getCurrentNode(),
					SpringContextStopperMBean.class);
			stopper.stop();
			System.out.println("Stop success.");
			System.exit(0);
		} catch (Throwable e) {
			e.printStackTrace();
			System.exit(1);
		}
		System.exit(0);
	}
}
