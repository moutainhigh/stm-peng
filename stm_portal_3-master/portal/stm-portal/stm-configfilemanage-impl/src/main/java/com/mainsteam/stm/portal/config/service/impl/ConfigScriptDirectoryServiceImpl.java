package com.mainsteam.stm.portal.config.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;

import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.portal.config.api.IConfigScriptDirectoryApi;
import com.mainsteam.stm.portal.config.bo.ConfigScriptBo;
import com.mainsteam.stm.portal.config.bo.ConfigScriptDirectoryBo;
import com.mainsteam.stm.portal.config.dao.IConfigScriptDao;
import com.mainsteam.stm.portal.config.dao.IConfigScriptDirectoryDao;
import com.mainsteam.stm.portal.config.util.ScriptsHandle;
import com.mainsteam.stm.portal.config.util.jaxb.Model;
import com.mainsteam.stm.portal.config.util.jaxb.Scripts;

public class ConfigScriptDirectoryServiceImpl implements IConfigScriptDirectoryApi{
	private static Logger logger = Logger.getLogger(ConfigScriptDirectoryServiceImpl.class.getName());
	
	private static final String backUp = "backUp";
	private static final String recovery = "recovery";
	
	@Resource
	private IConfigScriptDirectoryDao configScriptDirectoryDao;
	
	@Resource
	private IConfigScriptDao configScriptDao;
	
	public void setConfigScriptDirectoryDao(
			IConfigScriptDirectoryDao configScriptDirectoryDao) {
		this.configScriptDirectoryDao = configScriptDirectoryDao;
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
	public List<ConfigScriptDirectoryBo> getAllConfigScriptDirectory(){
		return configScriptDirectoryDao.getAll();
	}
	
	/**
	 * 根据ID查询
	 * @param bo
	 * @return
	 */
	public ConfigScriptDirectoryBo getConfigScriptDirectoryById(Long id){
		return configScriptDirectoryDao.getConfigScriptDirectoryById(id);
	}
	
	/**
	 * 更新
	 * @param bo
	 * @return
	 */
	public boolean updateConfigScriptDirectory(ConfigScriptDirectoryBo csd){
		int flag = configScriptDirectoryDao.update(csd);
		
		if(flag>0) return true;
		else return false;
	}
	
	/**
	 * 删除
	 * @param bo
	 * @return
	 */
	public boolean deleteConfigScriptDirectoryById(Long id){
		int flag = configScriptDirectoryDao.delete(id);
		
		if(flag>0) return true;
		else return false;
	}
	
	public boolean deleteConfigScriptDirectoryALL(Long directoryId){
		List<ConfigScriptBo> csbList = configScriptDao.getConfigScriptByDirectoryId(directoryId);
		if(null!=csbList&&csbList.size()>0){
			long[] csbArr = new long[csbList.size()];
			for(int i=0;i<csbList.size();i++){
				long id = csbList.get(i).getId();
				csbArr[i] = id;
				if(!(configScriptDao.delete(id)>0)){
					return false;
				}
			}
			if(!delConfigScriptToFile(csbArr)){
				return false;
			}
			
		}
		
		List<ConfigScriptDirectoryBo> csdbList = configScriptDirectoryDao.getConfigScriptDirectoryByParentId(directoryId);
		if(null!=csdbList&&csdbList.size()>0){
			for(ConfigScriptDirectoryBo csdb:csdbList){
				if(!deleteConfigScriptDirectoryALL(csdb.getId())){
					return false;
				}
			}
		}
				
		boolean flag = deleteConfigScriptDirectoryById(directoryId);
		
		return flag;
	}
	
	public boolean deleteConfigScriptDirectoryONLY(Long directoryId){
		ConfigScriptDirectoryBo csdbo = configScriptDirectoryDao.getConfigScriptDirectoryById(directoryId);
		List<ConfigScriptBo> csbList = configScriptDao.getConfigScriptByDirectoryId(directoryId);
		
		if(null!=csbList&&csbList.size()>0){
			for(ConfigScriptBo csb:csbList){
				csb.setDirectoryId(csdbo.getParentId());
				configScriptDao.update(csb);
			}
			if(!updateConfigScriptToFile(csdbo.getId(),csdbo.getParentId())){
				return false;
			}
		}
		
		
		List<ConfigScriptDirectoryBo> csdbList = configScriptDirectoryDao.getConfigScriptDirectoryByParentId(directoryId);
		if(null!=csdbList&&csdbList.size()>0){
			for(ConfigScriptDirectoryBo csdb:csdbList){
				csdb.setParentId(csdbo.getParentId());
				csdb.setLevelEnum(csdbo.getLevelEnum());
				configScriptDirectoryDao.update(csdb);
			}
		}
		
		boolean flag = deleteConfigScriptDirectoryById(directoryId);
		
		return flag;
	}
	
	private boolean delConfigScriptToFile(long[] configScriptIdArr){
		boolean flagBack = delConfigScriptToFileByType(configScriptIdArr,backUp);
		boolean flagRecovery = delConfigScriptToFileByType(configScriptIdArr,recovery);
		return (flagBack&&flagRecovery);
	}
	
	private boolean updateConfigScriptToFile(long configScriptBeforeId,long configScriptAfterId){
		boolean flagBack = updateConfigScriptToFileByType(configScriptBeforeId,configScriptAfterId,backUp);
		boolean flagRecovery = updateConfigScriptToFileByType(configScriptBeforeId,configScriptAfterId,recovery);
		return (flagBack&&flagRecovery);
	}
	
	private boolean updateConfigScriptToFileByType(long configScriptBeforeId,long configScriptAfterId,String type){
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
			for(Model model:modelList){
				if(model.getConfigScriptId()==configScriptBeforeId){
					model.setConfigScriptId(configScriptAfterId);
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
	
	private boolean delConfigScriptToFileByType(long[] configScriptIdArr,String type){
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
			for(long configScriptId:configScriptIdArr){
				for(int i=0;i<modelList.size();i++){
					if(modelList.get(i).getConfigScriptId()==configScriptId){
						modelList.remove(i);
						break;
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
	
	/**
	 * 更新
	 * @param bo
	 * @return
	 */
	public boolean addORupdate(ConfigScriptDirectoryBo csd){
		if(csd.getId()>0){
			return this.updateConfigScriptDirectory(csd);
		}else{
			csd.setId(seq.next());
			int flag = configScriptDirectoryDao.insert(csd);
			
			if(flag>0) return true;
			else return false;
		}
		
	}
	
	/**
	 * 根据名字查询
	 * @param bo
	 * @return
	 */
	public ConfigScriptDirectoryBo selectByName(String name){
		return configScriptDirectoryDao.selectByName(name);
	}
}
