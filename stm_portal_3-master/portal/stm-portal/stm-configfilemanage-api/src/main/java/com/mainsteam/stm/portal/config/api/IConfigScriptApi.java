package com.mainsteam.stm.portal.config.api;

import java.util.List;
import java.util.Map;

import com.mainsteam.stm.portal.config.bo.ConfigScriptBo;
import com.mainsteam.stm.portal.config.vo.ScriptVo;

public interface IConfigScriptApi {
	/**
	 * 查询全部
	 * @param bo
	 * @return
	 */
	public List<ConfigScriptBo> getAllConfigScript();
	
	/**
	 * 根据ID查询
	 * @param bo
	 * @return
	 */
	public ConfigScriptBo getConfigScriptById(Long id);
	
	public List<ConfigScriptBo> queryByBo(ConfigScriptBo csb);
	
	public List<ConfigScriptBo> queryEqualsByBo(ConfigScriptBo csb);
	/**
	 * 更新
	 * @param bo
	 * @return
	 */
	public boolean updateConfigScript(ConfigScriptBo csb);
	
	/**
	 * 删除
	 * @param bo
	 * @return
	 */
	public boolean deleteConfigScriptById(Long id);
	
	/**
	 * 更新
	 * @param bo
	 * @return
	 */
	public boolean addORupdate(ConfigScriptBo csb);
	
	/**
	 * 更新Model
	 * @param bo
	 * @return
	 */
	public boolean updateConfigScriptBackUpScript(long configScriptId,String fileName,String type,String cmd);
	
	public boolean updateConfigScriptRecoveryScript(long configScriptId,String fileName,String type,String cmd);
	
	/**
	 * 删除恢复,备份脚本
	 * @return
	 */
	public boolean delBackUpModelScript(long configScriptId,String[] fileNameArr);
	
	public boolean delRecoveryModelScript(long configScriptId,String[] fileNameArr);
	
	/**
	 * 根据ConfigScriptId获取恢复,备份脚本
	 * @return
	 */
	public Map<String,List<ScriptVo>> getConfigScriptModelByConfigScriptId(long configScriptId);
	
	public ScriptVo getModelScript(long configScriptId,String scriptName,String type);
	
	/*
	 * 将xml内预置内容同步到数据库
	 * */
	public boolean synXmlData(long rootDirectoryId);
	
}
