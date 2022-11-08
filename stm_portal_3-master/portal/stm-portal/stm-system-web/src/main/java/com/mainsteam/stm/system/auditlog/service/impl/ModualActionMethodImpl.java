package com.mainsteam.stm.system.auditlog.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.system.auditlog.api.IModualActionMethodApi;
import com.mainsteam.stm.system.auditlog.bo.MethodEntity;
import com.mainsteam.stm.util.ClassPathUtil;

/**
 * <li>文件名称: com.mainsteam.stm.system.auditlog.service.impl.ModualActionMethodImpl.java</li>
 * <li>文件描述: 本类描述</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年12月15日
 */
@SuppressWarnings("unchecked")
@Service
public class ModualActionMethodImpl implements IModualActionMethodApi{
	private Logger logger = Logger.getLogger(getClass());
	
	private static final String FILE_PATH = ClassPathUtil.getCommonClasses()
			+"config"+File.separatorChar
			+"module_action_mapper"+File.separatorChar
			+"ModuleActionMapper.xml";

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.auditlog.api.IModualActionMethodApi#reLoadXmlMapper()
	 */
	@PostConstruct
	@Override
	public void reLoadXmlMapper() {
		init(FILE_PATH);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.auditlog.api.IModualActionMethodApi#getMethodEntityList()
	 */
	@Override
	public List<MethodEntity> getMethodEntityList() {
		return (List<MethodEntity>)ENTITY_LIST.clone();
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.auditlog.api.IModualActionMethodApi#getMethodEntityMap()
	 */
	@Override
	public Map<String, MethodEntity> getMethodEntityMap() {
		return (Map<String, MethodEntity>)ENTITY_MAP.clone();
	}
	private static final SAXReader reader = new SAXReader();
	
	private static final HashMap<String, MethodEntity> ENTITY_MAP = new HashMap<String, MethodEntity>();
	private static final ArrayList<MethodEntity> ENTITY_LIST = new ArrayList<MethodEntity>();
	
	private Document getDocument(String filePath){
		try {
			return reader.read(new File(filePath));
		} catch (DocumentException e) {
			if(logger.isInfoEnabled()){
				logger.error("ModuleActionMapper.xml文件不存在-" + e.getMessage());
			}
			return null;
		}
	}
	/**
	 * key分隔符
	 */
	private static final char KEY_SEPARATOR = '.';
	/**
	 * description分隔符
	 */
	private static final char DESCRIPTION_SEPARATOR = '-';
	/**
	 * class属性
	 */
	private static final String ATTR_CLASS = "@class";
	/**
	 * name属性
	 */
	private static final String ATTR_NAME = "@name";
	/**
	 * id属性
	 */
	private static final String ATTR_ID = "@id";
	/**
	 * type属性
	 */
	private static final String ATTR_TYPE = "@type";
	/**
	 * description属性
	 */
	private static final String ATTR_DESCRIPTION = "@description";
	/**
	 * isAfter属性
	 */
	private static final String ATTR_IS_AFTER = "@isAfter";
	/**
	 * module节点
	 */
	private static final String NODE_MODULE = "module";
	/**
	 * action节点
	 */
	private static final String NODE_ACTION = "action";
	/**
	 * method节点
	 */
	private static final String NODE_METHOD = "method";
	
	private void init(String filePath){
		Document document = getDocument(filePath);
		if(document==null){
			logger.error("日志methods未进行初始化");
			return ;
		}
		ENTITY_MAP.clear();
		ENTITY_LIST.clear();
		
		List<Element> elements = document.selectNodes("/mapper/module");	//顶层节点（模块）
		for(Element element : elements){
			MethodEntity listEntity = new MethodEntity();
			String name = element.valueOf(ATTR_NAME);
			listEntity.setModuleName(name);
			String id = element.valueOf(ATTR_ID);
			listEntity.setModuleId(id);
			ENTITY_LIST.add(listEntity);
			
			Parent parent = new Parent();
			parent.setKey(new StringBuilder(element.valueOf(ATTR_CLASS)));
			parent.setDescription(new StringBuilder());
			parent.setModuleId(id);
			parent.setModuleName(name);
			analyzeElement(parent,element);
			
		}
	}
	
	private void analyzeElement(Parent parent, Element element){
		List<Element> actions = element.elements(NODE_ACTION);
		if(!actions.isEmpty()){
			for(Element action : actions){
				StringBuilder key = new StringBuilder(parent.getKey().toString());
				StringBuilder description = new StringBuilder(parent.getDescription().toString());
				
				key.append(KEY_SEPARATOR + action.valueOf(ATTR_CLASS));
				description.append(DESCRIPTION_SEPARATOR + action.valueOf(ATTR_NAME));
				
				List<Element> methods = action.elements(NODE_METHOD);
				for(Element method : methods){
					String key1 = key.toString() + KEY_SEPARATOR + method.valueOf(ATTR_NAME);
					String description1 = description.toString() + DESCRIPTION_SEPARATOR + method.valueOf(ATTR_DESCRIPTION);
					MethodEntity methodEntity = new MethodEntity();
					methodEntity.setKey(key1);
					methodEntity.setDescription(description1.substring(1));
					methodEntity.setType(method.valueOf(ATTR_TYPE));
					methodEntity.setModuleName(parent.getModuleName());
					methodEntity.setModuleId(parent.getModuleId());
					String isAfter = method.valueOf(ATTR_IS_AFTER);
					if(StringUtils.isNotBlank(isAfter)){
						methodEntity.setIsAfter(Boolean.valueOf(isAfter));
					}
					
					ENTITY_MAP.put(key1, methodEntity);
				}
			}
		}
		List<Element> modules = element.elements(NODE_MODULE);
		if(modules.isEmpty()){
			return ;
		}else{
			for(Element module : modules){
				parent.getKey().append(KEY_SEPARATOR + module.valueOf(ATTR_CLASS));
				parent.getDescription().append(DESCRIPTION_SEPARATOR + module.valueOf(ATTR_NAME));
				analyzeElement(parent, module);
			}
		}
	}
}

class Parent{
	private StringBuilder key;
	private StringBuilder description;
	private String moduleId;
	private String moduleName;
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
	public StringBuilder getKey() {
		return key;
	}
	public void setKey(StringBuilder key) {
		this.key = key;
	}
	public StringBuilder getDescription() {
		return description;
	}
	public void setDescription(StringBuilder description) {
		this.description = description;
	}
	public String getModuleId() {
		return moduleId;
	}
	public void setModuleId(String moduleId) {
		this.moduleId = moduleId;
	}
}
