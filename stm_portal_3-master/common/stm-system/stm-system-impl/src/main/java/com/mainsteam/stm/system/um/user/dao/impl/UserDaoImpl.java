package com.mainsteam.stm.system.um.user.dao.impl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Repository;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.system.um.domain.bo.Domain;
import com.mainsteam.stm.system.um.role.bo.Role;
import com.mainsteam.stm.system.um.user.bo.DomainRole;
import com.mainsteam.stm.system.um.user.bo.User;
import com.mainsteam.stm.system.um.user.bo.UserResourceRel;
import com.mainsteam.stm.system.um.user.dao.IUserDao;
import com.mainsteam.stm.system.um.user.vo.UserConditionVo;
@Repository
public class UserDaoImpl extends BaseDao<User> implements IUserDao{

	@Autowired
	public UserDaoImpl(@Qualifier(SESSION_DEFAULT)SqlSessionTemplate session) {
		super(session, IUserDao.class.getName());
	}
	
	@Override
	public int queryByAccount(User  user) {
		return getSession().selectOne(getNamespace()+"checkAccount", user);
	}

	@Override
	public void pageSelect(Page<User, UserConditionVo> userPage) {
		this.select(SQL_COMMOND_PAGE_SELECT, userPage);
	}


	@Override
	public List<User> getByAccount(String account) {
		return getSession().selectList(getNamespace()+"getByAccount", account);
	}


	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.user.dao.IUserDao#updatePassword(java.lang.String, java.lang.String)
	 */
	@Override
	public int updatePassword(User user) {
		return update("updatePassword", user);
	}


	@Override
	public void getAllUserByPage(Page<User, User> page) {
		 getSession().selectList(getNamespace()+"getAllUserByPage", page);
		
	}


	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.user.dao.IUserDao#saveUserResourceRels(java.util.List)
	 */
	@Override
	public int saveUserResourceRels(List<UserResourceRel> userResources) {
		return getSession().insert(getNamespace()+"saveUserResourceRels", userResources);
	}


	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.user.dao.IUserDao#getUserResourceRel(java.lang.Long)
	 */
	@Override
	public List<UserResourceRel> getUserResourceRel(Long userId) {
		Map<String,Long> condition=new HashMap<String, Long>();
		condition.put("userId",userId);
		return getSession().selectList(getNamespace()+"getUserResourceRel", condition);
	}
	
	@Override
	public List<UserResourceRel> getUserResourceRel(Long userId,Long domainId) {
		Map<String,Long> condition=new HashMap<String, Long>();
		condition.put("userId",userId);
		condition.put("domainId",domainId);
		return getSession().selectList(getNamespace()+"getUserResourceRel", condition);
	}


	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.user.dao.IUserDao#deleteUserResourceRel(java.lang.Long)
	 */
	@Override
	public int deleteUserResourceRel(Long userId) {
		return getSession().delete(getNamespace()+"deleteUserResourceRel", userId);
	}

	@Override
	public List<User> getAll(User user) {
		return getSession().selectList(getNamespace()+"getAll",user);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.user.dao.IUserDao#getAllDomains()
	 */
	@Override
	public List<DomainRole> getAllDomains() {
		return getSession().selectList(getNamespace()+"getAllDomains");
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.user.dao.IUserDao#getAllRoles()
	 */
	@Override
	public List<Role> getAllRoles() {
		return getSession().selectList(getNamespace()+"getAllRoles");
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.user.dao.IUserDao#deleteUserResourceRelByDomains(com.mainsteam.stm.system.um.user.bo.UserResourceRel)
	 */
	@Override
	public int deleteUserResourceRelByDomains(UserResourceRel rel) {
		return getSession().delete(getNamespace()+"deleteUserResourceRelByDomains", rel);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.user.dao.IUserDao#getDomainRoleByUserId(java.lang.Long)
	 */
	@Override
	public List<DomainRole> getDomainRoleByUserId(Long id) {
		return getSession().selectList(getNamespace()+"getDomainRoleByUserId", id);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.user.dao.IUserDao#getUserResourceRelByDomains(java.lang.Long, java.util.List)
	 */
	@Override
	public List<UserResourceRel> getUserResourceRelByDomains(Long userId,
			Collection<Long> domainIds) {
		Map<String,Object> condition=new HashMap<String, Object>();
		condition.put("userId",userId);
		condition.put("domainIds",domainIds);
		return getSession().selectList(getNamespace()+"getUserResourceRelByDomains", condition);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.user.dao.IUserDao#getUserCommonDomains(java.lang.Long)
	 */
	@Override
	public List<Domain> getUserCommonDomains(Long id) {
		return getSession().selectList(getNamespace()+"getUserCommonDomains", id);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.user.dao.IUserDao#delUserRelations(java.lang.Long[])
	 */
	@Override
	public int delUserRelations(Long[] ids) {
		return getSession().delete(getNamespace()+"delUserRelations", ids);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.user.dao.IUserDao#getDomainUsersDomains(java.lang.Long)
	 */
	@Override
	public List<Domain> getDomainUsersDomains(Long id) {
		return getSession().selectList(getNamespace()+"getDomainUsersDomains", id);
	}

	@Override
	public List<User> getUsersByType(int userType) {
		return select("getUsersByType", userType);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.user.dao.IUserDao#getUsers(int)
	 */
	@Override
	public List<User> getUsers(UserConditionVo conditions) {
		return select("getUsers", conditions);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.user.dao.IUserDao#getUserResourceGroupRelCount(java.lang.Long)
	 */
	@Override
	public int getUserResourceGroupRelCount(Long resourceGroupId) {
		return getSession().selectOne(getNamespace()+"getUserResourceGroupRelCount", resourceGroupId);
	}

	@Override
	public int deleteUserResourceRelByResourceIds(Long[] resourceIds) {
		return getSession().delete(getNamespace()+"deleteUserResourceRelByResourceIds", resourceIds);
	}

	@Override
	public int updatePassErrorCnt2Zero() {
		return getSession().update(getNamespace()+"updatePassErrorCnt2Zero");
	}

	@Override
	public int updateUpPassTime2Now() {
		return getSession().update(getNamespace()+"updateUpPassTime2Now");
	}
}
