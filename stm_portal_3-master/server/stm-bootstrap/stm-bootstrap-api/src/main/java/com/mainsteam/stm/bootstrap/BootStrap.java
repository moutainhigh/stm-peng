/**
 * 
 */
package com.mainsteam.stm.bootstrap;

import java.io.File;

/**
 * 
 * 启动系统服务
 * 
 * @author ziw
 * 
 */
public interface BootStrap {
	
	public static final String MODULE = "BOOTSTRAP";
	
	/**
	 * 设置启动依赖的jar目录列表
	 * 
	 * @param jarFilePath
	 */
	public void setJarFilePath(File[] jarFilePath);

	/**
	 * 设置系统启动的ClassPath路径列表
	 * 
	 * @param classPathDir
	 */
	public void setClassPath(File[] classPathDir);

	/**
	 * 设置系统的启动类
	 * 
	 * @param startUpClass
	 */
	public void setStartUpClass(String startUpClassName);

	/**
	 * 调用系统的启动类，并执行指定的参数
	 * 
	 * @param args
	 * @throws Exception 
	 */
	public void startServer(String[] args) throws Exception;
}
