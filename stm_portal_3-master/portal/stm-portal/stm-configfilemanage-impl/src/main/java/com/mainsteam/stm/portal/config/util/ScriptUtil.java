package com.mainsteam.stm.portal.config.util;

import java.io.File;

import com.alibaba.druid.util.StringUtils;
import com.mainsteam.stm.portal.config.util.jaxb.Model;
import com.mainsteam.stm.portal.config.util.jaxb.Script;
import com.mainsteam.stm.portal.config.util.jaxb.Scripts;
import com.mainsteam.stm.util.ClassPathUtil;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.DomDriver;

public class ScriptUtil {
	
//	public static Scripts getScripts() throws Exception{
//		String filePath = ClassPathUtil.getCommonClasses()+"config"+File.separator+"config-script.xml";
//		JAXBContext context = JAXBContext.newInstance(Scripts.class);
//		Unmarshaller un = context.createUnmarshaller();
//		File file = new File(filePath);
//		if(!file.exists() || !file.isFile()) throw new Exception("未找到脚本配置文件"+filePath);
//		Scripts scripts = (Scripts) un.unmarshal(file);
//		return scripts;
//	}
	private static final String backUpPath = ClassPathUtil.getCommonClasses()+"config"+File.separator+"config-script.xml";
	
	private static final String recoveryPath = ClassPathUtil.getCommonClasses()+"config"+File.separator+"recovery-config-script.xml";
	
	public static Scripts getScripts() throws Exception{
		File file = new File(backUpPath);
		Scripts spt = new Scripts();
		XStream sx = new XStream(new DomDriver("utf-8"));
		Class[] cla = {Object.class,Scripts.class,Model.class,Script.class};
		sx.processAnnotations(cla);
		sx.alias(spt.getClass().getSimpleName(), spt.getClass());
		
		return (Scripts)sx.fromXML(file);
	}
	
	public static Model getModel(String oid) throws Exception{
		Scripts scripts = getScripts();
		return getModel(scripts, oid);
	}
	
	public static Model getModel(Scripts scripts,String oid){
		if(scripts==null) return null;
		for(Model model : scripts.getModels()){
			if(StringUtils.equals(oid, model.getOid())){
				return model;
			}
		}
		return null;
	}
	
	public static Scripts getReScripts() throws Exception{
		File file = new File(recoveryPath);
		Scripts spt = new Scripts();
		XStream sx = new XStream(new DomDriver("utf-8"));
		Class[] cla = {Object.class,Scripts.class,Model.class,Script.class};
		sx.processAnnotations(cla);
		sx.alias(spt.getClass().getSimpleName(), spt.getClass());
		
		return (Scripts)sx.fromXML(file);
	}
			
}
