package com.mainsteam.stm.caplib.plugin;

/**
 * 插件文件定义
 * 
 * @author Administrator
 * 
 */
public class PluginDef implements Cloneable, java.io.Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -6934686723154880400L;
	/**
	 * 插件id
	 */
	private String id;
	/**
	 * 插件名称
	 */
	private String name;
	/**
	 * 插件类
	 */
	private String classUrl;
	private String maxActiveSession;
	private String maxIdleSession;
	private String sessionTTL;
	private String objectCacheTimeout;
	/**
	 * 初始化参数
	 */
	private PluginInitParameter[] pluginInitParameters;

	/**
	 * 获取id
	 * 
	 * @return
	 */
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	/**
	 * 获取name
	 * 
	 * @return
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/**
	 * 获取代理class的calssPath
	 * 
	 * @return
	 */
	public String getClassUrl() {
		return classUrl;
	}

	public void setClassUrl(String classUrl) {
		this.classUrl = classUrl;
	}

	/**
	 * 获取MaxActiveSession
	 * 
	 * @return
	 */
	public String getMaxActiveSession() {
		return maxActiveSession;
	}

	public void setMaxActiveSession(String maxActiveSession) {
		this.maxActiveSession = maxActiveSession;
	}

	/**
	 * 获取MaxIdleSession
	 * 
	 * @return
	 */
	public String getMaxIdleSession() {
		return maxIdleSession;
	}

	public void setMaxIdleSession(String maxIdleSession) {
		this.maxIdleSession = maxIdleSession;
	}

	/**
	 * 获取MaxIdleSession
	 * 
	 * @return
	 */
	public String getSessionTTL() {
		return sessionTTL;
	}

	public void setSessionTTL(String sessionTTL) {
		this.sessionTTL = sessionTTL;
	}

	/**
	 * 获取ObjectCacheTimeout
	 * 
	 * @return
	 */
	public String getObjectCacheTimeout() {
		return objectCacheTimeout;
	}

	public void setObjectCacheTimeout(String objectCacheTimeout) {
		this.objectCacheTimeout = objectCacheTimeout;
	}

	/**
	 * 获取插件的初始化参数集合
	 * 
	 * @return
	 */
	public PluginInitParameter[] getPluginInitParameters() {
		return pluginInitParameters;
	}

	public void setPluginInitParameters(
			PluginInitParameter[] pluginInitParameters) {
		this.pluginInitParameters = pluginInitParameters;
	}

	@Override
	public String toString() {
		return "PluginDef [id=" + id + ", name=" + name + "]";
	}

	@Override
	public Object clone() {
		PluginDef def = new PluginDef();
		def.setClassUrl(this.getClassUrl());
		def.setId(this.getId());
		def.setMaxActiveSession(this.getMaxActiveSession());
		def.setMaxIdleSession(this.getMaxIdleSession());
		def.setName(this.getName());
		def.setObjectCacheTimeout(this.getObjectCacheTimeout());
		PluginInitParameter[] pluginInits = new PluginInitParameter[this
				.getPluginInitParameters().length];
		for (int i = 0; i < this.getPluginInitParameters().length; i++) {
			pluginInits[i] = (PluginInitParameter) this
					.getPluginInitParameters()[i].clone();
		}
		def.setPluginInitParameters(pluginInits);
		def.setSessionTTL(this.getSessionTTL());
		return def;
	}

}
