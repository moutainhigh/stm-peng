package com.mainsteam.stm.portal.resource.dao.impl;

import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.portal.resource.bo.CustomGroupBo;
import com.mainsteam.stm.portal.resource.dao.ICustomResourceGroupDao;
import com.mainsteam.stm.portal.resource.po.CustomGroupPo;
import com.mainsteam.stm.portal.resource.po.CustomGroupResourcePo;

/**
 * 
 * <li>文件名称: CustomResourceGroupDaoImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年7月24日
 * @author   wangxinghao
 */
@Repository("customResourceGroupDao")
public class CustomResourceGroupDaoImpl extends BaseDao<CustomGroupResourcePo> implements ICustomResourceGroupDao {

	@Autowired
	public CustomResourceGroupDaoImpl(@Qualifier(SESSION_DEFAULT)SqlSessionTemplate session) {
		super(session, ICustomResourceGroupDao.class.getName());
	}

	@Override
	public List<CustomGroupPo> getList(long userId) {
		CustomGroupPo po = new CustomGroupPo();
		po.setEntryId(userId);
		return getSession().selectList(getNamespace() + "getList",po);
	}

	@Override
	public int insert(CustomGroupPo customGroupPo) {
		// TODO Auto-generated method stub
		return getSession().insert(getNamespace() + "insertCustomGroup", customGroupPo);
	}

	@Override
	public int batchInsert(List<CustomGroupResourcePo> customGroupResourcePoList) {
		// TODO Auto-generated method stub
		return this.batchInsert("batchCustomGroupResource", customGroupResourcePoList);
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
	public int updateGroup(CustomGroupPo customGroupPo) {
		return getSession().update(getNamespace() + "updateGroup", customGroupPo);
	}

	/**
	 * 
	 * 通过资源组ID删除资源和资源组的对应关系
	 * @param id
	 * @return
	 */
	@Override
	public int deleteResourceIDsByGroup(long id) {
		return getSession().delete(getNamespace() + "deleteGroupResource", id);
	}

	@Override
	public int deleteGroupByGroup(long id) {
		return getSession().delete(getNamespace() + "deleteGroup", id);
	}

	@Override
	public int checkGroupNameIsExsit(CustomGroupPo customGroupPo) {
		return getSession().selectOne(getNamespace() + "checkGroupNameIsExsit", customGroupPo);
	}

	@Override
	public int deleteResourceFromCustomGroupByIds(CustomGroupBo groupBo) {
		
		return getSession().delete(getNamespace() + "deleteResourceFromGroup", groupBo);
	}

	@Override
	public List<Long> selectResourceNumberIsZeroGroup() {
		return getSession().selectList(getNamespace() + "selectResourceNumberIsZeroGroup",null);
	}

	@Override
	public int deleteGroupAndResourceRelationById(long[] ids) {
		return getSession().delete(getNamespace() + "deleteGroupAndResourceRelationById", ids);
	}

	@Override
	public CustomGroupPo getCustomGroup(long id) {
		
		
		return getSession().selectOne(getNamespace() +"getCustomGroup", id);
	}

	@Override
	public int checkGroupResourceRelationIsExsit(CustomGroupResourcePo customGroupResourcePo) {
		// TODO Auto-generated method stub
		return getSession().selectOne(getNamespace() + "checkGroupResourceRelationIsExsit", customGroupResourcePo);
	}
	
	@Override
	public int getMaxSortByEntryId(long entryId) {
		Integer i = getSession().selectOne(getNamespace() + "getMaxSortByEntryId", entryId);
		return i == null ? 0 : i;
	}

	@Override
	public int updateGroupSort(Map<String, String> map) {
		return getSession().update(getNamespace() + "updateGroupSort", map);
	}

	@Override
	public List<CustomGroupPo> getChildGroupsById(Map<String, String> map) {
		return getSession().selectList(getNamespace() + "getChildGroupsById", map);
	}

}
