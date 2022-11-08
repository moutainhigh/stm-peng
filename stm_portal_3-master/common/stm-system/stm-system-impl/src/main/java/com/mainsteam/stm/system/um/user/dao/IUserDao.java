package com.mainsteam.stm.system.um.user.dao;

import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.system.um.domain.bo.Domain;
import com.mainsteam.stm.system.um.role.bo.Role;
import com.mainsteam.stm.system.um.user.bo.DomainRole;
import com.mainsteam.stm.system.um.user.bo.User;
import com.mainsteam.stm.system.um.user.bo.UserResourceRel;
import com.mainsteam.stm.system.um.user.vo.UserConditionVo;

public interface IUserDao {
	/**
	 * 新增用户 返回新增成功条数
	 * @param user
	 * @return
	 */
	int insert(User user);

	/**
	 * 删除用户 返回删除成功条数
	 * @param user
	 * @return
	 */
	int batchDel(Long[] ids);

	/**
	 * 修改用户 返回修改成功条数
	 * @param user
	 * @return
	 */
	int update(User user);
	/**
	 * 判断用户名是否可用
	 * @param user	参数对象
	 * @return
	 * @author  ziwen
	 * @date	2019年9月2日
	 */
	int queryByAccount(User  user);

	/**
	 * 根据id获取用户详细信息
	 * @param user
	 * @return
	 */
	User get(Long id);

	/**
	 * 获取所有用户
	 * @param user
	 * @return
	 */
	List<User> getAll(User user);

	/**
	 * 根据条件分页查询用户信息
	 * @param user
	 * @return
	 */
	void pageSelect(Page<User,UserConditionVo> userPage);
	
	/**
	 * @param users
	 * @return
	 * @author  ziwen
	 * @date	2019年8月23日
	 */
	int batchUpdate(List<User> users);
	
	List<User> getByAccount(String account);

	/**
	 * 用户密码修改
	 * @param user
	 * @return
	 * @author  ziwen
	 * @date	2019年9月4日
	 */
	int updatePassword(User user);
	void getAllUserByPage(Page<User, User> page);

	/**
	 * 保存用户资源信息
	 * @param userResources
	 * @author	ziwen
	 * @return 
	 * @date	2019年10月20日
	 */
	int saveUserResourceRels(List<UserResourceRel> userResources);

	/**
	 * @param userId
	 * @return
	 * @author	ziwen
	 * @date	2019年10月22日
	 */
	List<UserResourceRel> getUserResourceRel(Long userId);

	/**
	 * @param userId
	 * @return
	 * @author	ziwen
	 * @date	2019年10月22日
	 */
	List<UserResourceRel> getUserResourceRel(Long userId,Long domainId);

	/**
	 * 删除用户资源权限信息
	 * @param userId
	 * @author	ziwen
	 * @date	2019年10月22日
	 */
	int deleteUserResourceRel(Long userId);
	
	/**
	 * 通过resourceId删除用户资源权限信息
	 * @param resourceId
	 * @return
	 */
	int deleteUserResourceRelByResourceIds(Long[] resourceIds);
	
	/**
	 * 获取所有域信息
	 * @return
	 * @author	ziwen
	 * @date	2019年11月17日
	 */
	List<DomainRole> getAllDomains();

	/**
	 * 获取所有角色
	 * @return
	 * @author	ziwen
	 * @date	2019年11月17日
	 */
	List<Role> getAllRoles();

	/**
	 * @param id
	 * @author	ziwen
	 * @date	2019年11月19日
	 */
	int deleteUserResourceRelByDomains(UserResourceRel rel);

	/**
	 * 通过用户获取关联信息
	 * @param id
	 * @return
	 * @author	ziwen
	 * @date	2019年11月27日
	 */
	List<DomainRole> getDomainRoleByUserId(Long id);

	/**
	 * @param userId
	 * @param domainIds
	 * @return
	 * @author	ziwen
	 * @date	2019年11月27日
	 */
	List<UserResourceRel> getUserResourceRelByDomains(Long userId,
			Collection<Long> domainIds);

	/**
	 * @param id
	 * @return
	 * @author	ziwen
	 * @date	2019年12月5日
	 */
	List<Domain> getUserCommonDomains(Long id);

	/**
	 * 通过一组用户ID删除用户关联信息
	 * @param ids
	 * @author	ziwen
	 * @date	2019年12月9日
	 */
	int delUserRelations(Long[] ids);

	/**
	 * @param id
	 * @return
	 * @author	ziwen
	 * @date	2019年12月17日
	 */
	List<Domain> getDomainUsersDomains(Long id);

	List<User> getUsersByType(int userType);

	/**
	 * 获取该用户权限下的用户信息
	 * @param conditions
	 * @return
	 * @author	ziwen
	 * @date	2019年1月5日
	 */
	List<User> getUsers(UserConditionVo conditions);

	/**
	 * 获取与资源组关联的用户
	 * @param resourceGroupId
	 * @return
	 * @author	ziwen
	 * @date	2019年1月6日
	 */
	int getUserResourceGroupRelCount(Long resourceGroupId);
	
	int updatePassErrorCnt2Zero();
	
	int updateUpPassTime2Now();
}
