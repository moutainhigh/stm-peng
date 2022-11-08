/**
 * 
 */
package com.mainsteam.stm.pluginserver.pool;

import org.apache.commons.pool.PoolableObjectFactory;
import org.apache.commons.pool.impl.GenericObjectPool;

/**
 * @author ziw
 * 
 */
public class PluginSessionExecutorRunnerPool extends GenericObjectPool {
	/**
	 * 
	 */
	public PluginSessionExecutorRunnerPool(PoolableObjectFactory factory,
			int maxActive) {
		super(factory, maxActive, GenericObjectPool.WHEN_EXHAUSTED_BLOCK, 0L);
	}
}
