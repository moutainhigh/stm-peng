/**
 * 
 */
package com.mainsteam.stm.node;

/**
 * @author ziw
 *
 */
public interface NodeCheckMBean {
	/**
	 * 检查节点的运行状态
	 * 
	 * @return
	 */
	public NodeState checkNode();
}
