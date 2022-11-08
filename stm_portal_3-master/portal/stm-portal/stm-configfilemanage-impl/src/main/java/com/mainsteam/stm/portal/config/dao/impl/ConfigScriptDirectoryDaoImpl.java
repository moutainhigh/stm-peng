package com.mainsteam.stm.portal.config.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.portal.config.bo.ConfigScriptDirectoryBo;
import com.mainsteam.stm.portal.config.dao.IConfigScriptDirectoryDao;

public class ConfigScriptDirectoryDaoImpl extends BaseDao<ConfigScriptDirectoryBo> implements IConfigScriptDirectoryDao {
	
	public ConfigScriptDirectoryDaoImpl(SqlSessionTemplate session) {
		super(session, IConfigScriptDirectoryDao.class.getName());
	}
	
	/**
	 * 查询全部
	 * @param bo
	 * @return
	 */
	public List<ConfigScriptDirectoryBo> getAll(){
		return getSession().selectList(getNamespace()+"getAll");
	}
	
	/**
	 * 根据parentId查询
	 * @param bo
	 * @return
	 */
	public List<ConfigScriptDirectoryBo> getConfigScriptDirectoryByParentId(Long id){
		List<ConfigScriptDirectoryBo> csdList = getSession().selectList(getNamespace()+"getByParentId",id);
		
		if(null!=csdList && csdList.size()>0) return csdList;
		else return null;
	}
	
	/**
	 * 根据ID查询
	 * @param bo
	 * @return
	 */
	public ConfigScriptDirectoryBo getConfigScriptDirectoryById(Long id){
		List<ConfigScriptDirectoryBo> csdList = getSession().selectList(getNamespace()+"getById",id);
		
		if(null!=csdList && csdList.size()>0) return csdList.get(0);
		else return null;
	}
	
	/**
	 * 更新
	 * @param bo
	 * @return
	 */
	public int update(ConfigScriptDirectoryBo csd){
		if(csd.getId()<1) return 0;
		return getSession().update(getNamespace()+"update",csd);
	}
	
	/**
	 * 删除
	 * @param bo
	 * @return
	 */
	public int delete(Long id){
		return getSession().delete(getNamespace()+"del",id);
	}
	
	/**
	 * 查询
	 * @param bo
	 * @return
	 */
	public ConfigScriptDirectoryBo selectByName(String name){
		List<ConfigScriptDirectoryBo> csdList = getSession().selectList(getNamespace()+"getByName",name);
		
		if(null!=csdList && csdList.size()>0) return csdList.get(0);
		else return null;
	}
	
}
