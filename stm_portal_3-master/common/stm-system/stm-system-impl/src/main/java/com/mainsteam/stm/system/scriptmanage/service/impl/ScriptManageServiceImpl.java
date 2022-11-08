package com.mainsteam.stm.system.scriptmanage.service.impl;


import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mainsteam.stm.platform.file.bean.FileGroupEnum;
import com.mainsteam.stm.platform.file.service.IFileClientApi;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.platform.sequence.service.SequenceFactory;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.system.scriptmanage.api.IScriptManageApi;
import com.mainsteam.stm.system.scriptmanage.bo.ScriptManage;
import com.mainsteam.stm.system.scriptmanage.bo.ScriptManageTypeEnum;
import com.mainsteam.stm.system.scriptmanage.dao.IScriptManageDao;

@Service("scriptManageService")
public class ScriptManageServiceImpl implements IScriptManageApi{
	private static final Logger logger = LoggerFactory.getLogger(ScriptManageServiceImpl.class);
	
	private static FileGroupEnum fileGroupEnum = FileGroupEnum.STM_SYSTEM;
	@Autowired
	@Qualifier("scriptManageDao")
	private IScriptManageDao scriptManageDao;
	
	private ISequence reportSeq;
	
	@Resource
	private IFileClientApi fileClient;
	
	@Value("${stm.file.upload_scriptmanage_type}")
	private String UPLOAD_FILE_TYPE;
	
	
	@Autowired
	public ScriptManageServiceImpl(SequenceFactory sequenceFactory){
		this.reportSeq=sequenceFactory.getSeq("stm_sys_script_manage");
	}
	
	public void setScriptManageDao(IScriptManageDao scriptManageDao) {
		this.scriptManageDao = scriptManageDao;
	}

	@Override
	public String[] getUploadScriptFileType(){
		if(null==UPLOAD_FILE_TYPE || "".equals(UPLOAD_FILE_TYPE)) return null;
		
		else return UPLOAD_FILE_TYPE.toUpperCase().split(",");
	}
	
	@Override
	public boolean saveScriptManage(MultipartFile file,int scriptType,String discription ,ILoginUser user) {
		
		long fileId = -1;
		try {
			fileId = fileClient.upLoadFile(this.fileGroupEnum, file);
		} catch (Exception e) {
			logger.error(e.getMessage());
			return false;
		}
		
		String fileName = file.getOriginalFilename();
		if(fileId>0 ){
			List<ScriptManage>  smList = this.loadBydocNameAndtype(fileName,scriptType);
			
			if(null!=smList && smList.size()>0){
				for(ScriptManage smTemp:smList){
					//删除不成功,无太大影响
					try {
						fileClient.deleteFile(smTemp.getFileId());
					} catch (Exception e) {
						logger.error(e.getMessage());
					}
					smTemp.setFileId(fileId);
					smTemp.setDiscription(discription);
					smTemp.setUpdateTime(new Date());
					smTemp.setFileSizeNum(file.getSize());
					smTemp.setUserId(user.getId());
					
					if(!this.update(smTemp)){
						return false;
					}
				}
				return true;
			}else{
				ScriptManage sm= new ScriptManage();
				sm.setDocName(fileName);
				sm.setFileId(fileId);
				sm.setDiscription(discription);
				sm.setScriptManageType(ScriptManageTypeEnum.getByScriptCode(scriptType));
				sm.setUpdateTime(new Date());
				sm.setFileSizeNum(file.getSize());
				sm.setUserId(user.getId());
				
				sm.setScriptId(reportSeq.next());
				
				if(1==save(sm)){
					return true;
				}
			}
			
		}
		
		return false;
	}
	
	private int save(ScriptManage scriptManage){
		return scriptManageDao.save(scriptManage);
	}
	
	@Override
	public boolean delScriptManage(Long[] scriptId,Long[] fileId){
		//删除不成功,无太大影响
		try {
			List<Long> fileIds = new ArrayList<Long>();
			
			for(Long id:fileId){
				fileIds.add(id);
			}
			
			fileClient.deleteFiles(fileIds);
		} catch (Exception e) {
			logger.error(e.getMessage());
		}
		
		if(0<scriptManageDao.del(scriptId)){
			return true;
		}
		return false;
	}
	
	@Override
	public void loadAllByPage(Page<ScriptManage,ScriptManage> page){
		 scriptManageDao.loadAllByPage(page);
	}
	@Override
	public List<ScriptManage> loadAllByTypeCode(ScriptManageTypeEnum type){
		return scriptManageDao.loadAllBytypecode(type);
	}
	
	@Override
	public List<ScriptManage> loadBydocName(String docName){
		return scriptManageDao.loadBydocName(docName);
	}
	
	@Override
	public List<ScriptManage> loadBydocNameAndtype(String docName,int scriptTypeCode){
		return scriptManageDao.loadBydocNameAndtype(docName, scriptTypeCode);
	}
	
	@Override
	public boolean update(ScriptManage scriptManage){
		if(0<scriptManageDao.update(scriptManage)){
			return true;
		}
		return false;
	}
	
	@Override
	public ScriptManage loadByscriptId(long scriptId){
		List<ScriptManage> smList = scriptManageDao.loadByscriptId(scriptId);
		return null==smList?null:smList.get(0);
	}
}
