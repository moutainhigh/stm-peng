package com.mainsteam.stm.portal.config.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.portal.config.bo.ConfigScriptBo;
import com.mainsteam.stm.portal.config.dao.IConfigScriptDao;

public class ConfigScriptDaoImpl extends BaseDao<ConfigScriptBo> implements IConfigScriptDao {
	
	public ConfigScriptDaoImpl(SqlSessionTemplate session) {
		super(session, IConfigScriptDao.class.getName());
	}
	
	/**
	 * 查询全部
	 * @param bo
	 * @return
	 */
	public List<ConfigScriptBo> getAll(){
		return getSession().selectList(getNamespace()+"getAll");
	}
	
	/**
	 * 查询
	 * @param bo
	 * @return
	 */
	public List<ConfigScriptBo> queryByBo(ConfigScriptBo csb){
		return getSession().selectList(getNamespace()+"query",csb);
	}
	
	public List<ConfigScriptBo> queryEqualsByBo(ConfigScriptBo csb){
		return getSession().selectList(getNamespace()+"queryEquals",csb);
	}
	
	/**
	 * 根据ID查询
	 * @param bo
	 * @return
	 */
	public ConfigScriptBo getConfigScriptById(Long id){
		List<ConfigScriptBo> csbList = getSession().selectList(getNamespace()+"getById",id);
		
		if(null!=csbList && csbList.size()>0) return csbList.get(0);
		else return null;
	}
	
	/**
	 * 根据directoryId查询
	 * @param bo
	 * @return
	 */
	public List<ConfigScriptBo> getConfigScriptByDirectoryId(Long directoryId){
		List<ConfigScriptBo> csbList = getSession().selectList(getNamespace()+"getByDirectoryId",directoryId);
		
		if(null!=csbList && csbList.size()>0) return csbList;
		else return null;
	}
	
	/**
	 * 更新
	 * @param bo
	 * @return
	 */
	public int update(ConfigScriptBo csb){
		if(csb.getId()<1) return 0;
		return getSession().update(getNamespace()+"update",csb);
	}
	
	/**
	 * 删除
	 * @param bo
	 * @return
	 */
	public int delete(Long id){
		return getSession().delete(getNamespace()+"del",id);
	}
	
}
