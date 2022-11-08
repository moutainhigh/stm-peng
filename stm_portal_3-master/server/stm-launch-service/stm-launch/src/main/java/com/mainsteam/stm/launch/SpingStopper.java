/**
 * 
 */
package com.mainsteam.stm.launch;

/**
 * @author ziw
 * 
 */
public class SpingStopper {

	/**
	 * 
	 */
	public SpingStopper() {
	}

	public static void main(String[] args) {
		SpringLauncher launcher = SpringLauncher.getInstance();
		if (launcher != null) {
			System.out.println("Stopping Server...");
			launcher.stopServer();
			System.out.println("Stop Server success.");
		} else {
			System.err.println("Not found Server lancher.");
		}
	}
}
