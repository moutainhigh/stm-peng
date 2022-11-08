/**
 * 
 */
package com.mainsteam.stm.launch;

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

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.launch.SpringContextStopperMBean#stop()
	 */
	@Override
	public void stop() {
		SpingStopper.main(new String[]{});
	}
}
