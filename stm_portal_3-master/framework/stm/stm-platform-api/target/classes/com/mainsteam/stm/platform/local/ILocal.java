package com.mainsteam.stm.platform.local;

import com.mainsteam.stm.util.IConstant;

/**
 * <li>文件名称: Local.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月14日
 * @author   ziwenwen
 */
public interface ILocal extends IConstant {
	String begin="${stm.local.";
	String end=p_close_b;
	/**
	 * 系统命名空间冠名的key<br/>
	 * 如oc.local.name、oc.local.sex<br/> 
	 * <strong>两个及以上模块公用key并且界面展示值都相同时<strong/> 推荐使用
	 * @param key
	 */
	String getSysKey(String key);
	
	/**
	 * 获取模块命名空间对应的key，忽略层级<br/> 
	 * 如oc.local.demo.name、oc.local.demo.sex<br/> 
	 * <strong>单个模块特用key或者key相同但界面展示值都不同时<strong/>推荐使用
	 * @param key
	 */
	String getKey(String key);
}


