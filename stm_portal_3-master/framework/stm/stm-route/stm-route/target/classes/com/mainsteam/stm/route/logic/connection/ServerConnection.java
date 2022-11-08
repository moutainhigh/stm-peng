/**
 * 
 */
package com.mainsteam.stm.route.logic.connection;

import com.mainsteam.stm.route.logic.LogicConnection;

/**
 * server端连接
 * 
 * @author ziw
 * 
 */
public interface ServerConnection extends LogicConnection {
	public String getSrcIp();
	public int getSrcPort();
}
