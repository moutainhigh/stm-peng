package com.mainsteam.stm.system.scriptcode.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.platform.sequence.service.SequenceFactory;
import com.mainsteam.stm.system.scriptcode.api.IScriptCodeApi;
import com.mainsteam.stm.system.scriptcode.bo.ScriptCode;
import com.mainsteam.stm.system.scriptcode.dao.IScriptCodeDao;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;



@Service("scriptCodeService")
public class ScriptCodeServiceImpl implements IScriptCodeApi{
	private static final Logger logger = LoggerFactory.getLogger(ScriptCodeServiceImpl.class);
	
	@Resource
	private IScriptCodeDao scriptCodeDao;
	
	private ISequence reportSeq;
	
	@Autowired
	public ScriptCodeServiceImpl(SequenceFactory sequenceFactory){
		this.reportSeq=sequenceFactory.getSeq("stm_sys_script_code");
	}
	
	private int save(ScriptCode scriptCode){
		return scriptCodeDao.save(scriptCode);
	}
	
	/*
	 * 保存
	 * */
	public boolean saveScriptCode(ScriptCode scriptCode){
		scriptCode.setCodeId(reportSeq.next());
		
		return (1==save(scriptCode)?true:false);
	}
	
	/*
	 * 查询所有脚本码
	 * */
	public List<ScriptCode> loadAllScriptCode(){
		
		return scriptCodeDao.loadAllScriptCode();
		
	}
	
	
	/*
	 * 根据脚本码查询
	 * */
	public ScriptCode loadByScriptCode(String code){
		List<ScriptCode> scList = scriptCodeDao.loadByScriptCode(code);
		
		if(null==scList || scList.size()==0){
			return null;
		}else{
			return scList.get(0);
		}
		
	}

	public void setScriptCodeDao(IScriptCodeDao scriptCodeDao) {
		this.scriptCodeDao = scriptCodeDao;
	}
	
}
