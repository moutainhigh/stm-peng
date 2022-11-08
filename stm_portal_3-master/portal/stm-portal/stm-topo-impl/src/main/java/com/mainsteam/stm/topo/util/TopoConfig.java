package com.mainsteam.stm.topo.util;

import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Repository;

import com.mainsteam.stm.util.PropertiesFileUtil;

/**
 * <li>拓扑系统配置参数</li>
 * @version  ms.stm
 * @since  2019年12月25日
 * @author zwx
 */
@Repository
public class TopoConfig {
	private static Map<String, Object> configMap = null;
	
	@PostConstruct
	public void init(){
		// 初始化系统参数
		initSettings();
	}
	
	/**
	 * 初始化系统参数.
	 */
	private static void initSettings(){
		try {
			Properties config = PropertiesFileUtil.getProperties("properties/topoConfig.properties");
			Enumeration<Object> keys = config.keys();
			configMap = new HashMap<String, Object>();
			while (keys.hasMoreElements()) {
				String key = (String) keys.nextElement();
				configMap.put(key, config.getProperty(key));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * 获取参数
	 * @param property
	 * @return
	 */
	public static String getValue(String property) {
		if (configMap == null) {
			initSettings();
		}
		return (String) configMap.get(property);
	}
}
