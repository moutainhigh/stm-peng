package com.mainsteam.stm.util;

import java.io.File;

/**
 * <li>文件名称: ClassPathUtil.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月21日
 * @author   ziwenwen
 */
public final class ClassPathUtil {
	
	private static final String tomcatHome=System.getProperties().getProperty("catalina.home")+File.separatorChar;
	
	private static final String commonClasses=tomcatHome+"common"+File.separatorChar+"classes"+File.separatorChar;

	/**
	 * 获取common下的资源文件类路径
	 * @return 形如 E:.../tomcat/common/classes/
	 */
	public static final String getCommonClasses(){
		return commonClasses;
	}
	
	/**
	 * 获取tomcat根路径
	 * @return 形如 E:.../tomcat/
	 */
	public static final String getTomcatHome(){
		return tomcatHome;
	}
}


