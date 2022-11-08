package com.mainsteam.stm.system.scriptcode.dao;

import java.util.List;

import com.mainsteam.stm.system.scriptcode.bo.ScriptCode;

public interface  IScriptCodeDao {
	/*
	 * add
	 * */
	public int save(ScriptCode scriptCode);
	
	/*
	 * loadAllScriptCode
	 * */
	public List<ScriptCode> loadAllScriptCode();
	
	/*
	 *  loadByScriptCode
	 * */
	public List<ScriptCode> loadByScriptCode(String code);
}
