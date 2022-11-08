package com.mainsteam.stm.system.scriptcode.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.system.scriptcode.bo.ScriptCode;
import com.mainsteam.stm.system.scriptcode.dao.IScriptCodeDao;

@Repository("scriptCodeDao")
public class ScriptCodeDaoImpl extends BaseDao<ScriptCode> implements IScriptCodeDao{
	@Autowired
	public ScriptCodeDaoImpl(@Qualifier(SESSION_DEFAULT)SqlSessionTemplate session) {
		super(session, IScriptCodeDao.class.getName());
	}
	
	
	@Override
	public int save(ScriptCode scriptCode) {
		return getSession().insert(getNamespace()+"insert", scriptCode);
	}
	
	/*
	 * loadAllScriptCode
	 * */
	public List<ScriptCode> loadAllScriptCode(){
		return getSession().selectList(getNamespace()+"selectAll");
	}
	
	/*
	 *  loadByScriptCode
	 * */
	public List<ScriptCode> loadByScriptCode(String code){
		return getSession().selectList(getNamespace()+"selectByScriptCode",code);
	}
	
}
