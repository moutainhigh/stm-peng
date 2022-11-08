package com.mainsteam.stm.portal.config.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.config.bo.ConfigWarnBo;
import com.mainsteam.stm.portal.config.bo.ConfigWarnResourceBo;
import com.mainsteam.stm.portal.config.dao.IConfigWarnDao;

/**
 * <li>文件名称: CustomResourceGroupDaoImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月13日
 * @author   caoyong
 */
public class ConfigWarnDaoImpl extends BaseDao<ConfigWarnBo> implements IConfigWarnDao {
	@Override
	public void selectByPage(Page<ConfigWarnBo, ConfigWarnBo> page) {
		this.select(SQL_COMMOND_PAGE_SELECT, page);
	}
	
	public ConfigWarnDaoImpl(SqlSessionTemplate session) {
		super(session, IConfigWarnDao.class.getName());
	}

	@Override
	public int checkNameIsExsit(String name) {
		return getSession().selectOne(getNamespace() +"checkNameIsExsit", name);
	}

	@Override
	public int batchInsertWarnResource(List<ConfigWarnResourceBo> resources) {
		return this.batchInsert("batchInsertWarnResource", resources);
	}

	@Override
	public int delWarnResourcesById(Long id) {
		return getSession().delete("delWarnResourcesById", id);
	}

	@Override
	public List<ConfigWarnResourceBo> getWarnResourcesById(Long id) {
		return getSession().selectList("getWarnResourcesById",id);
	}

	@Override
	public int batchDelConfigWarn(Long[] ids) {
		return getSession().delete("batchDelConfigWarn", ids);
	}

	@Override
	public int batchDelConfigWarnResource(Long[] ids) {
		return getSession().delete("batchDelConfigWarnResource", ids);
	}
	
	@Override
	public int batchDelCWRByResourceIds(Long[] ids) {
		return getSession().delete("batchDelCWRByResourceIds", ids);
	}

	@Override
	public Long getWarnIdByResourceId(Long id){
		return getSession().selectOne("getWarnIdByResourceId", id);
	}
}
