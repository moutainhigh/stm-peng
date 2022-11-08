package com.mainsteam.stm.system.um.role.dao.impl;

import java.util.List;

import org.apache.ibatis.session.SqlSession;
import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.system.um.relation.bo.UserDomain;
import com.mainsteam.stm.system.um.role.bo.Role;
import com.mainsteam.stm.system.um.role.bo.RoleRight;
import com.mainsteam.stm.system.um.role.bo.RoleRightRel;
import com.mainsteam.stm.system.um.role.dao.IRoleDAO;

/**
 * <li>文件名称: RoleDAOImpl.java</li>
 * <li>文件描述: 本类描述</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年8月19日
 */
public class RoleDAOImpl extends BaseDao<Role> implements IRoleDAO {

	/**
	 * @param session
	 * @param iDaoNamespace
	 */
	public RoleDAOImpl(SqlSessionTemplate session) {
		super(session, IRoleDAO.class.getName());
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.role.dao.IRoleDAO#pageSelect(com.mainsteam.stm.platform.mybatis.plugin.pagination.Page)
	 */
	@Override
	public void pageSelect(Page<Role, Role> page) {
		getSession().selectList(getNamespace()+SQL_COMMOND_PAGE_SELECT,page);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.role.dao.IRoleDAO#getRoleCountByNameId(com.mainsteam.stm.system.um.role.bo.Role)
	 */
	@Override
	public int getRoleCountByNameId(Role role) {
		return getSession().selectOne(getNamespace()+SQL_COMMOND_COUNT, role);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.role.dao.IRoleDAO#getRightByRoleId(java.lang.Long)
	 */
	@Override
	public List<RoleRight> getRightByRoleId(Long roleId) {
		return getSession().selectList(getNamespace()+"getRightByRoleId", roleId);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.role.dao.IRoleDAO#getRightByRoleIds(java.lang.Long[])
	 */
	@Override
	public List<RoleRight> getRightByRoleIds(Long[] roleIds) {
		return getSession().selectList(getNamespace()+"getRightByRoleIds", roleIds);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.role.dao.IRoleDAO#batchUpdateRight(java.util.List)
	 */
//	@Override
//	public int batchUpdateRight(List<RoleRightRel> roleRightRels) {
//		SqlSession session=getSession();
//		int count=0;
//		for(RoleRightRel roleRightRel : roleRightRels){
//			count += session.update(getNamespace() + "batchUpdateRight", roleRightRel);
//		}
//		return count;
//	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.role.dao.IRoleDAO#batchInsertRoleRightRel(java.util.List)
	 */
	@Override
	public int batchInsertRoleRightRel(List<RoleRightRel> roleRightRels) {
		SqlSession session=getSession();
		int count=0;
		for(RoleRightRel roleRightRel : roleRightRels){
			count += session.update(getNamespace() + "batchInsertRoleRightRel", roleRightRel);
		}
		return count;
	}

	@Override
	public List<Role> getRoles(Long userId) {
		return select("getRoles", userId);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.role.dao.IRoleDAO#getRelByRoleId(java.lang.Long)
	 */
	@Override
	public int getRelByRoleId(Long id) {
		return getSession().selectOne(getNamespace()+"getRelByRoleId", id);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.role.dao.IRoleDAO#delRoleRightRelByRoleId(java.lang.Long)
	 */
	@Override
	public void delRoleRightRelByRoleId(Long roleId) {
		getSession().delete(getNamespace()+"delRoleRightRelByRoleId", roleId);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.role.dao.IRoleDAO#addRoleRightRels(java.util.List)
	 */
	@Override
	public void addRoleRightRels(List<RoleRightRel> roles) {
		getSession().insert(getNamespace()+"addRoleRightRels", roles);
	}

	@Override
	public List<Role> queryAllRoles() {
		return getSession().selectList(getNamespace()+"queryAllRoles");
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.role.dao.IRoleDAO#getUserDomainByRoleId(java.lang.Long)
	 */
	@Override
	public List<UserDomain> getUserDomainByRoleId(Long id) {
		return getSession().selectList(getNamespace()+"getUserDomainByRoleId", id);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.role.dao.IRoleDAO#delRelationByRoleIds(java.lang.Long[])
	 */
	@Override
	public void delRelationByRoleIds(Long[] ids) {
		getSession().delete(getNamespace()+"delRelationByRoleIds", ids);
	}
}
