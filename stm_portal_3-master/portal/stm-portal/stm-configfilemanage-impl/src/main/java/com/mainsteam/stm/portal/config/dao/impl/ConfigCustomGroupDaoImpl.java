package com.mainsteam.stm.portal.config.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.config.bo.ConfigCustomGroupBo;
import com.mainsteam.stm.portal.config.bo.ConfigCustomGroupResourceBo;
import com.mainsteam.stm.portal.config.dao.IConfigCustomGroupDao;

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
public class ConfigCustomGroupDaoImpl extends BaseDao<ConfigCustomGroupResourceBo> implements IConfigCustomGroupDao {
	@Override
	public void selectByPage(Page<ConfigCustomGroupBo, ConfigCustomGroupBo> page) {
		this.select(SQL_COMMOND_PAGE_SELECT, page);
	}
	
	public ConfigCustomGroupDaoImpl(SqlSessionTemplate session) {
		super(session, IConfigCustomGroupDao.class.getName());
	}

	@Override
	public List<ConfigCustomGroupBo> getList(long userId) {
		return getSession().selectList(getNamespace() + "getList",userId);
	}

	@Override
	public int insert(ConfigCustomGroupBo cBo) {
		return getSession().insert(getNamespace()+"insertConfigCustomGroup", cBo);
	}

	@Override
	public int batchInsert(List<ConfigCustomGroupResourceBo> clist) {
		return this.batchInsert("batchInsertConfigCustomResource", clist);
	}

	/**
	 * 通过组ID查找对应资源ID集合
	 * @param id
	 * @return
	 */
	@Override
	public List<String> getGroupResourceIdsByGroup(long id) {
		return getSession().selectList(getNamespace() + "getGroupResourceIdsByGroup",id);
	}

	@Override
	public int updateGroup(ConfigCustomGroupBo cPo) {
		return getSession().update(getNamespace()+"updateConfigCustomGroup", cPo);
	}

	/**
	 * 
	 * 通过配置组ID删除资源和配置组的对应关系
	 * @param id
	 * @return
	 */
	@Override
	public int deleteResourceIDsByGroup(long id) {
		return getSession().delete(getNamespace()+"deleteResourceIDsByGroup", id);
	}

	@Override
	public int deleteGroupByGroup(long id) {
		return getSession().delete(getNamespace()+"deleteGroupByGroup", id);
	}

	@Override
	public List<ConfigCustomGroupBo> checkGroupNameIsExsit(String groupName) {
		return getSession().selectList(getNamespace() + "checkGroupNameIsExsit", groupName);
	}

	@Override
	public int deleteResourceFromCustomGroupByIds(ConfigCustomGroupBo groupBo) {
		return getSession().delete(getNamespace()+"deleteResourceFromCustomGroupByIds", groupBo);
	}

	@Override
	public List<Long> selectResourceNumberIsZeroGroup() {
		return getSession().selectList(getNamespace() + "selectResourceNumberIsZeroGroup",null);
	}

	@Override
	public int deleteGroupAndResourceRelationById(long[] ids) {
		return getSession().delete(getNamespace()+"deleteGroupAndResourceRelationById", ids);
	}

	@Override
	public ConfigCustomGroupBo getCustomGroup(long id) {
		return getSession().selectOne(getNamespace() +"getCustomGroup", id);
	}
}
