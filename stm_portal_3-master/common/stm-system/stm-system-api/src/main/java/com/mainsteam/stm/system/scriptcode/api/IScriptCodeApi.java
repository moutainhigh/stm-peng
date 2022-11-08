package com.mainsteam.stm.system.scriptcode.api;

import java.util.List;

import com.mainsteam.stm.system.scriptcode.bo.ScriptCode;

/**
 * <li>文件名称: IScriptCodeApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年7月2日
 * @author   tongpl
 */

public interface IScriptCodeApi {
	
	/*
	 * 保存
	 * */
	public boolean saveScriptCode(ScriptCode scriptCode);
	
	/*
	 * 查询所有脚本码
	 * */
	public List<ScriptCode> loadAllScriptCode();
	
	
	/*
	 * 根据脚本码查询
	 * */
	public ScriptCode loadByScriptCode(String code);
	
}
