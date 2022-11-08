package com.mainsteam.stm.portal.config.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

import com.mainsteam.stm.portal.config.util.jaxb.Model;
import com.mainsteam.stm.portal.config.util.jaxb.Script;
import com.mainsteam.stm.portal.config.util.jaxb.Scripts;
import com.mainsteam.stm.util.ClassPathUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

/**
 * 
 * <li>文件名称: ConfigScriptManagementAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2016年1月12日
 * @author   tongpl
 */

/*
 * 用于脚本配置管理功能,对xml进行解析,修改
 * by tongpl
 * */

public class ScriptsHandle {
	
	private static final String backUpPath = ClassPathUtil.getCommonClasses()+"config"+File.separator+"config-script.xml";
	
	private static final String recoveryPath = ClassPathUtil.getCommonClasses()+"config"+File.separator+"recovery-config-script.xml";
	
	
//	public static Scripts getBackUpScripts(){
//		return getScriptsByType(getBackUpScriptsFile());
//	}
//	
//	public static Scripts getRecoveryScripts(){
//		return getScriptsByType(getBackUpScriptsFile());
//	}
	
	public static File getBackUpScriptsFile(){
		return new File(backUpPath);
	}
	
	public static File getRecoveryScriptsFile(){
		return new File(recoveryPath);
	}
	
	//解析xml
	public static Scripts parseXml(File file){
		Scripts spts = new Scripts();
		XStream sx = new XStream(new DomDriver("utf-8"));
		Class[] cla = {Object.class,Scripts.class,Model.class,Script.class};
		sx.processAnnotations(cla);
		sx.alias(spts.getClass().getSimpleName(), spts.getClass());
		
		return (Scripts)sx.fromXML(file);
	}
	
	//生成xml
	public static boolean createXml(Scripts spts,File file)throws Exception{
		XStream sx = new XStream(new DomDriver("utf-8"));
		
		Class[] cla = {Object.class,Scripts.class,Model.class,Script.class};
		sx.processAnnotations(cla);		
		sx.alias(spts.getClass().getSimpleName(), spts.getClass());
		
		OutputStream fileOutput = null;
		try {
			fileOutput =  new FileOutputStream(file);
			sx.toXML(spts, fileOutput);
		} catch (FileNotFoundException e) {
			throw e;
		}finally{
			if (fileOutput != null) {
				try {
					fileOutput.close();
				} catch (Exception e) {
					throw e;
				}
			}
		}
		
		return true;
	}
}
