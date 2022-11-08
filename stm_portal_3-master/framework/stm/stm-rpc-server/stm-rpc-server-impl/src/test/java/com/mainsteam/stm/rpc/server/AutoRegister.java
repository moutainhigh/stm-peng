/**
 * 
 */
package com.mainsteam.stm.rpc.server;

/**
 * @author ziw
 *
 */
public class AutoRegister implements AutoRegisterMBean {

	/**
	 * 
	 */
	public AutoRegister() {
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.rpc.server.AutoRegisterMBean#sayHello()
	 */
	@Override
	public String sayHello() {
		System.out.println("hello");
		return "hello";
	}

}
