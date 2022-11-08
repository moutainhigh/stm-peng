package com.mainsteam.stm.route;

public class RouteEntry {
	
	private String ip;
	private int port;

	public RouteEntry() {
	}

	/**
	 * @return the ip
	 */
	public final String getIp() {
		return ip;
	}

	/**
	 * @param ip the ip to set
	 */
	public final void setIp(String ip) {
		this.ip = ip;
	}

	/**
	 * @return the port
	 */
	public final int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public final void setPort(int port) {
		this.port = port;
	}
}
