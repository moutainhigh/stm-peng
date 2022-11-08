package com.mainsteam.stm.system.scriptmanage.dao;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.system.scriptmanage.bo.ScriptManage;
import com.mainsteam.stm.system.scriptmanage.bo.ScriptManageTypeEnum;

public interface IScriptManageDao {
	/*
	 * add
	 * */
	public int save(ScriptManage scriptManage);
	
	/*
	 * del
	 * */
	public int del(Long[] scriptManageId);
	
	/*
	 * loadAll
	 * */
	public void loadAllByPage(Page<ScriptManage,ScriptManage> page);
	/*
	 * loadAllBytypecode
	 * */
	public List<ScriptManage> loadAllBytypecode(ScriptManageTypeEnum type);
	
	/*
	 * loadBydocNameAndtype
	 * */
	public List<ScriptManage> loadBydocNameAndtype(String docName,int scriptTypeCode);
	
	/*
	 * loadBydocName
	 * */
	public List<ScriptManage> loadBydocName(String docName);
	
	/*
	 * loadByscriptId
	 * */
	public List<ScriptManage> loadByscriptId(long scriptId);
	
	/*
	 * update
	 * */
	public int update(ScriptManage scriptManage);
}
