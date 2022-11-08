package com.mainsteam.stm.system.scriptmanage.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.system.scriptmanage.bo.ScriptManage;
import com.mainsteam.stm.system.scriptmanage.bo.ScriptManageTypeEnum;
import com.mainsteam.stm.system.scriptmanage.dao.IScriptManageDao;

@Repository("scriptManageDao")
public class ScriptManageDaoImpl extends BaseDao<ScriptManage> implements IScriptManageDao{
	@Autowired
	public ScriptManageDaoImpl(@Qualifier(SESSION_DEFAULT)SqlSessionTemplate session) {
		super(session, IScriptManageDao.class.getName());
	}
	
	@Override
	public int save(ScriptManage scriptManage) {
		return getSession().insert(getNamespace()+"insert", scriptManage);
	}
	
	@Override
	public int del(Long[] scriptManageId){
		
		return getSession().insert(getNamespace()+"delete", scriptManageId);
	}
	
	@Override
	public void loadAllByPage(Page<ScriptManage,ScriptManage> page){
		 getSession().selectList(getNamespace()+"pageSelect", page);
	}
	
	@Override
	public List<ScriptManage> loadAllBytypecode(ScriptManageTypeEnum type){
		List<ScriptManage> smList =getSession().selectList(getNamespace()+"selectAllByscriptTypeCode", type.getScriptCode());
		return smList;
	}
	
	@Override
	public List<ScriptManage> loadBydocName(String docName){
		List<ScriptManage> smList =getSession().selectList(getNamespace()+"selectBydocName", docName);
		return smList;
	}
	
	@Override
	public List<ScriptManage> loadBydocNameAndtype(String docName,int scriptTypeCode){
		ScriptManage sm = new ScriptManage();
		sm.setDocName(docName);
		sm.setScriptTypeCode(scriptTypeCode);
		
		List<ScriptManage> smList =getSession().selectList(getNamespace()+"selectByScriptManage", sm);
		return smList;
	}
	
	@Override
	public int update(ScriptManage scriptManage){
		return getSession().update(getNamespace()+"update",scriptManage);
	}
	
	@Override
	public List<ScriptManage> loadByscriptId(long scriptId){
		List<ScriptManage> smList =getSession().selectList(getNamespace()+"selectByscriptId", scriptId);
		return smList;
	}
}
