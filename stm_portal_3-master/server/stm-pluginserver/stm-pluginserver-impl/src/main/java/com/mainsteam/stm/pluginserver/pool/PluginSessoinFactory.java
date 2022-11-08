/**
 * 
 */
package com.mainsteam.stm.pluginserver.pool;

import com.mainsteam.stm.caplib.plugin.PluginDef;
import com.mainsteam.stm.pluginserver.plugin.loader.PluginClassLoader;
import com.mainsteam.stm.pluginsession.PluginSession;

/**
 * @author ziw
 * 
 */
public class PluginSessoinFactory {

	private PluginClassLoader pluginClassLoader;

	public void setPluginClassLoader(PluginClassLoader pluginClassLoader) {
		this.pluginClassLoader = pluginClassLoader;
	}

	/**
	 * 
	 */
	public PluginSessoinFactory() {
	}

	public PluginSession createPluginSession(PluginDef pluginDef)
			throws InstantiationException, IllegalAccessException,
			ClassNotFoundException {
		assert pluginDef != null;
		if (pluginDef == null) {
			return null;
		}
		@SuppressWarnings("unchecked")
		Class<? extends PluginSession> c = (Class<? extends PluginSession>) pluginClassLoader
				.getPluginClassLoader(pluginDef.getClassUrl()).loadClass(
						pluginDef.getClassUrl());
		return c.newInstance();
	}
}
