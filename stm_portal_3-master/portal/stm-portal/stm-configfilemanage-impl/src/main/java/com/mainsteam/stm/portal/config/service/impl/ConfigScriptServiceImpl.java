package com.mainsteam.stm.portal.config.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.portal.config.api.IConfigScriptApi;
import com.mainsteam.stm.portal.config.bo.ConfigScriptBo;
import com.mainsteam.stm.portal.config.bo.ConfigScriptDirectoryBo;
import com.mainsteam.stm.portal.config.dao.IConfigScriptDao;
import com.mainsteam.stm.portal.config.util.ScriptsHandle;
import com.mainsteam.stm.portal.config.util.jaxb.Model;
import com.mainsteam.stm.portal.config.util.jaxb.Script;
import com.mainsteam.stm.portal.config.util.jaxb.Scripts;
import com.mainsteam.stm.portal.config.vo.ScriptVo;

/**
 * 
 * <li>文件名称: ConfigScriptImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2016年1月5日
 * @author   tongpl
 */

public class ConfigScriptServiceImpl implements IConfigScriptApi{
	private static Logger logger = Logger.getLogger(ConfigScriptServiceImpl.class.getName());
	
	private static final String backUp = "backUp";
	private static final String recovery = "recovery";
	
	@Resource
	private IConfigScriptDao configScriptDao;
	
	public void setConfigScriptDao(IConfigScriptDao configScriptDao) {
		this.configScriptDao = configScriptDao;
	}

	private ISequence seq;
	public void setSeq(ISequence seq) {
		this.seq = seq;
	}
	
	
	/**
	 * 查询全部
	 * @param bo
	 * @return
	 */
	public List<ConfigScriptBo> getAllConfigScript(){
		return configScriptDao.getAll();
	}
	
	public List<ConfigScriptBo> queryByBo(ConfigScriptBo csb){
		return configScriptDao.queryByBo(csb);
	}
	
	public List<ConfigScriptBo> queryEqualsByBo(ConfigScriptBo csb){
		return configScriptDao.queryEqualsByBo(csb);
	}
	
	/**
	 * 根据ID查询
	 * @param bo
	 * @return
	 */
	public ConfigScriptBo getConfigScriptById(Long id){
		return configScriptDao.getConfigScriptById(id);
	}
	
	/**
	 * 更新
	 * @param bo
	 * @return
	 */
	public boolean updateConfigScript(ConfigScriptBo csb){
		int flag = configScriptDao.update(csb);
		
		if(flag>0) return true;
		else return false;
	}
	
	/**
	 * 删除
	 * @param bo
	 * @return
	 */
	public boolean deleteConfigScriptById(Long id){
		int flag = configScriptDao.delete(id);
		
		boolean delFileFlag = delConfigScriptToFile(id);
		
		if(flag>0 && delFileFlag) return true;
		else return false;
	}
	
	/**
	 * 更新
	 * @param bo
	 * @return
	 */
	public boolean addORupdate(ConfigScriptBo csb){
		if(csb.getId()>0){
			boolean flag =  this.updateConfigScript(csb);
			
			boolean fileFlag = addOrUpdateConfigScriptToFile(csb.getId(),csb.getName(),csb.getOid());
			if(flag && fileFlag) return true;
			else return false;
		}else{
			csb.setId(seq.next());
			int flag = configScriptDao.insert(csb);
			
			boolean fileFlag = addOrUpdateConfigScriptToFile(csb.getId(),csb.getName(),csb.getOid());
			if(flag>0 && fileFlag) return true;
			else return false;
		}
	}
	
	private boolean addOrUpdateConfigScriptToFile(long configScriptId,String fileName,String oid){
		boolean flagBack = addOrUpdateConfigScriptToFileByType(configScriptId,fileName,oid,backUp);
		boolean flagRecovery = addOrUpdateConfigScriptToFileByType(configScriptId,fileName,oid,recovery);
		return (flagBack&&flagRecovery);
	}
	
	private boolean delConfigScriptToFile(long configScriptId){
		boolean flagBack = delConfigScriptToFileByType(configScriptId,backUp);
		boolean flagRecovery = delConfigScriptToFileByType(configScriptId,recovery);
		return (flagBack&&flagRecovery);
	}
	
	private boolean delConfigScriptToFileByType(long configScriptId,String type){
		File file = ScriptsHandle.getBackUpScriptsFile();
		switch (type) {
		case "recovery":
			file = ScriptsHandle.getRecoveryScriptsFile();
			break;
		case "backUp":
			break;
		}
		Scripts spts = ScriptsHandle.parseXml(file);
		
		List<Model> modelList =spts.getModels();
		if(null==modelList){
			modelList = new ArrayList<Model>();
			spts.setModels(modelList);
		}else{
			for(int i=0;i<modelList.size();i++){
				if(modelList.get(i).getConfigScriptId()==configScriptId){
					modelList.remove(i);
					break;
				}
			}
		}
		
		//往回写
		try {
			return ScriptsHandle.createXml(spts,file);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
	}
	
	private boolean addOrUpdateConfigScriptToFileByType(long configScriptId,String fileName,String oid,String type){
		File file = ScriptsHandle.getBackUpScriptsFile();
		switch (type) {
		case "recovery":
			file = ScriptsHandle.getRecoveryScriptsFile();
			break;
		case "backUp":
			break;
		}
		Scripts spts = ScriptsHandle.parseXml(file);
		
		Model model = new Model();
		model.setConfigScriptId(configScriptId);
		model.setName(fileName);
		model.setOid(oid);
		
		List<Model> modelList =spts.getModels();
		if(null==modelList){
			modelList = new ArrayList<Model>();
			spts.setModels(modelList);
		}else{
			boolean flag = true;
			for(Model mod:spts.getModels()){
				if(mod.getOid().equals(oid)){
					mod.setName(fileName);
					mod.setOid(oid);
					flag = false;
					break;
				}
			}
			//再通过configScriptId比对
			if(flag){
				for(Model mod:spts.getModels()){
					if(mod.getConfigScriptId()==configScriptId){
						mod.setName(fileName);
						mod.setOid(oid);
						flag = false;
						break;
					}
				}
			}
			if(flag){
				modelList.add(model);
			}
		}
		
		//往回写
		try {
			return ScriptsHandle.createXml(spts,file);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
	}
	
	/**
	 * 更新Model
	 * @param bo
	 * @return
	 */
	public boolean updateConfigScriptBackUpScript(long configScriptId,String fileName,String scriptType,String cmd){
		return updateConfigScriptToFileByType(configScriptId,fileName,scriptType,cmd,backUp);
	}
	
	public boolean updateConfigScriptToFileByType(long configScriptId,String fileName,String scriptType,String cmd,String fileType){
		Script spt = new Script();
		spt.setFileName(fileName);
		spt.setType(scriptType);
		spt.setCmd(cmd);
		
		File file = ScriptsHandle.getBackUpScriptsFile();
		switch (fileType) {
		case "recovery":
			file = ScriptsHandle.getRecoveryScriptsFile();
			break;
		case "backUp":
			break;
		}
		Scripts spts = ScriptsHandle.parseXml(file);
		
		if(null==spts.getModels()) return false;
		for(Model model:spts.getModels()){
			if(model.getConfigScriptId()==configScriptId){
				if(null==model.getScripts()){
					List<Script> sptList = new ArrayList<Script>();
					model.setScripts(sptList);
					
					sptList.add(spt);
				}else{
					boolean flag =true; 
					for(Script script:model.getScripts()){
						if(script.getFileName().equals(fileName)){
							script.setCmd(cmd);
							script.setType(scriptType);
							flag = false;
						}
					}
					if(flag){
						model.getScripts().add(spt);
					}
				}
			}
		}
		
		//往回写
		try {
			return ScriptsHandle.createXml(spts,file);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
	}
	
	public boolean updateConfigScriptRecoveryScript(long configScriptId,String fileName,String scriptType,String cmd){
		return updateConfigScriptToFileByType(configScriptId,fileName,scriptType,cmd,recovery);
	}
	
	/**
	 * 根据ConfigScriptId获取恢复,备份脚本
	 * @return
	 */
	public Map<String,List<ScriptVo>> getConfigScriptModelByConfigScriptId(long configScriptId){
		File fileRecovery = ScriptsHandle.getRecoveryScriptsFile();
		Scripts sptsRecovery = ScriptsHandle.parseXml(fileRecovery);
		
		File fileBackUp = ScriptsHandle.getBackUpScriptsFile();
		Scripts sptsBackUp = ScriptsHandle.parseXml(fileBackUp);
		
		Map<String,List<ScriptVo>> scriptListMap = new HashMap<String,List<ScriptVo>>();
		
		if(null==sptsRecovery.getModels() || sptsRecovery.getModels().size()==0){
			scriptListMap.put("recovery", null);
		}else{
			for(Model model:sptsRecovery.getModels()){
				if(model.getConfigScriptId()==configScriptId){
					scriptListMap.put("recovery", copyToListVo(model.getScripts()));
				}
			}
		}
		
		if(null==sptsBackUp.getModels() || sptsBackUp.getModels().size()==0){
			scriptListMap.put("bakcUp", null);
		}else{
			for(Model model:sptsBackUp.getModels()){
				if(model.getConfigScriptId()==configScriptId){
					scriptListMap.put("bakcUp", copyToListVo(model.getScripts()));
				}
			}
		}
		
		return scriptListMap;
	}
	
	public ScriptVo getModelScript(long configScriptId,String scriptName,String type){
		File file = ScriptsHandle.getBackUpScriptsFile();
		switch (type) {
		case "recovery":
			file = ScriptsHandle.getRecoveryScriptsFile();
			break;
		case "backUp":
			break;
		}
		Scripts spts = ScriptsHandle.parseXml(file);
		
		if(null==spts.getModels() || spts.getModels().size()==0)  return null;
		for(Model model:spts.getModels()){
			if(model.getConfigScriptId()==configScriptId){
				List<Script> sptList = model.getScripts();
				if(null==sptList || sptList.size()==0)  return null;
				
				for(Script spt:sptList){
					if(spt.getFileName().equals(scriptName)){
						return copyToVo(spt);
					}
				}
			}
		}
		return null;
	}
	
	private ScriptVo copyToVo(Script st){
		ScriptVo sv = new ScriptVo();
		if(null!=st){
			sv.setCmd(st.getCmd());
			sv.setFileName(st.getFileName());
			sv.setType(st.getType());
		}
		
		return sv;
	}
	
	private List<ScriptVo> copyToListVo(List<Script> st){
		List<ScriptVo> svList = new ArrayList<ScriptVo>();
		if(null!=st)
		for(Script spt:st){
			ScriptVo sv = new ScriptVo();
			sv.setCmd(spt.getCmd());
			sv.setFileName(spt.getFileName());
			sv.setType(spt.getType());
			svList.add(sv);
		}
		
		return svList;
	}
	
	/**
	 * 删除恢复,备份脚本
	 * @return
	 */
	public boolean delBackUpModelScript(long configScriptId,String[] fileNameArr){
		return delModelScriptToFileByType(configScriptId,fileNameArr,backUp);
	}
	
	private boolean delModelScriptToFileByType(long configScriptId,String[] fileNameArr,String type){
		File file = ScriptsHandle.getBackUpScriptsFile();
		switch (type) {
		case "recovery":
			file = ScriptsHandle.getRecoveryScriptsFile();
			break;
		case "backUp":
			break;
		}
		Scripts spts = ScriptsHandle.parseXml(file);
		
		if(null==spts.getModels() || spts.getModels().size()==0)  return false;
		for(String fileName:fileNameArr){
			modelFor : for(Model model:spts.getModels()){
				if(model.getConfigScriptId()==configScriptId){
					List<Script> sptList = model.getScripts();
					if(null==sptList || sptList.size()==0) return false;
					
					for(int i=0;i<sptList.size();i++){
						Script temp = sptList.get(i);
						if(temp.getFileName().equals(fileName)){
							sptList.remove(i);
							break modelFor;
						}
					}
				}
			}
		}
		
		//往回写
		try {
			return ScriptsHandle.createXml(spts,file);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
	}
	
	public boolean delRecoveryModelScript(long configScriptId,String[] fileNameArr){
		return delModelScriptToFileByType(configScriptId,fileNameArr,recovery);
	}
	
	/*
	 * 将xml内预置内容同步到数据库
	 * */
	public boolean synXmlData(long rootDirectoryId){
		boolean flagBack = synXmlDataByPath(backUp,rootDirectoryId);
		boolean flagRecovery = synXmlDataByPath(recovery,rootDirectoryId);
		return (flagBack&&flagRecovery);
	}
	
	public boolean synXmlDataByPath(String type,long rootDirectoryId){
		File file = ScriptsHandle.getBackUpScriptsFile();
		switch (type) {
		case "recovery":
			file = ScriptsHandle.getRecoveryScriptsFile();
			break;
		case "backUp":
			break;
		}
		Scripts spts = ScriptsHandle.parseXml(file);
		
		if(null==spts.getModels() || spts.getModels().size()==0)  return false;
		for(Model model:spts.getModels()){
			if(model.getConfigScriptId()<1){
				//查询数据库内是否已存在
				ConfigScriptBo csbTemp = new ConfigScriptBo();
				csbTemp.setOid(model.getOid());
				List<ConfigScriptBo> csbList = configScriptDao.queryEqualsByBo(csbTemp);
				//初始化到数据库
				if(null==csbList || csbList.size()==0){
					ConfigScriptBo csb = new ConfigScriptBo();
					csb.setDirectoryId(rootDirectoryId);
					csb.setName(model.getName());
					csb.setOid(model.getOid());
					
					if(addORupdate(csb)){
						model.setConfigScriptId(csb.getId());
					}
				}else{
					csbTemp = csbList.get(0);
					model.setConfigScriptId(csbTemp.getId());
				}
				
			}
		}
		List<ConfigScriptBo> csbList = this.getAllConfigScript();
		for(ConfigScriptBo csb:csbList){
			boolean flag = true;
			for(Model model:spts.getModels()){
				if(csb.getOid().equals(model.getOid())){
					flag = false;
					break;
				}
			}
			if(flag){
				Model modelAdd = new Model();
				modelAdd.setConfigScriptId(csb.getId());
				modelAdd.setName(csb.getName());
				modelAdd.setOid(csb.getOid());
				spts.getModels().add(modelAdd);
			}
		}
		
		//往回写
		try {
			return ScriptsHandle.createXml(spts,file);
		} catch (Exception e) {
			logger.error(e.getMessage(), e);
			return false;
		}
	}
	
}
