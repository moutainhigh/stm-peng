package com.mainsteam.stm.system.um.role.dao;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.system.um.relation.bo.UserDomain;
import com.mainsteam.stm.system.um.role.bo.Role;
import com.mainsteam.stm.system.um.role.bo.RoleRight;
import com.mainsteam.stm.system.um.role.bo.RoleRightRel;

/**
 * <li>文件名称: IRoleDAO.java</li>
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
public interface IRoleDAO {

	/**
	 * 查询角色列表信息
	 * @param page
	 * @author  ziwen
	 * @date	2019年8月19日
	 */
	public void pageSelect(Page<Role, Role> page);

	/**
	 * 写入角色信息
	 * @param role
	 * @return
	 * @author  ziwen
	 * @date	2019年8月19日
	 */
	public int insert(Role role);

	/**
	 * 更新角色信息
	 * @param roles
	 * @return
	 * @author  ziwen
	 * @date	2019年8月19日
	 */
	public int batchUpdate(List<Role> roles);

	/**
	 * 通过主键获取角色信息
	 * @param id
	 * @return
	 * @author  ziwen
	 * @date	2019年8月19日
	 */
	public Role get(long id);

	/**
	 * @param role
	 * @return
	 * @author  ziwen
	 * @date	2019年8月19日
	 */
	public int update(Role role);

	/**
	 * 根据条件查询角色个数
	 * @param role
	 * @return
	 * @author  ziwen
	 * @date	2019年8月21日
	 */
	public int getRoleCountByNameId(Role role);

	/**
	 * 通过角色ID获取权限信息
	 * @param roleId
	 * @return
	 * @author  ziwen
	 * @date	2019年8月23日
	 */
	public List<RoleRight> getRightByRoleId(Long roleId);

	/**
	 * 通过多个角色ID获取权限信息
	 * @param roleIds
	 * @return
	 * @author  ziwen
	 * @date	2019年8月23日
	 */
	public List<RoleRight> getRightByRoleIds(Long[] roleIds);

//	/**
//	 * 批量更新权限信息
//	 * @param roles
//	 * @return
//	 * @author  ziwen
//	 * @date	2019年8月23日
//	 */
//	public int batchUpdateRight(List<RoleRightRel> roles);

	/**
	 * 批量写入角色权限关联信息
	 * @param roleRightRels
	 * @author  ziwen
	 * @date	2019年8月23日
	 */
	public int batchInsertRoleRightRel(List<RoleRightRel> roleRightRels);


	public List<Role> getRoles(Long userId);
	/**
	 * 删除角色 
	 * @param user
	 * @return
	 */
	int batchDel(Long[] ids);

	/**
	 * 获取角色已经关联到的用户记录数
	 * @param id
	 * @author	ziwen
	 * @date	2019年10月9日
	 */
	public int getRelByRoleId(Long id);

	/**
	 * 通过角色ID删除角色权限信息
	 * @param roleId
	 * @author	ziwen
	 * @date	2019年11月17日
	 */
	public void delRoleRightRelByRoleId(Long roleId);

	/**
	 * 新增角色权限信息
	 * @param roles
	 * @author	ziwen
	 * @date	2019年11月17日
	 */
	public void addRoleRightRels(List<RoleRightRel> roles);
	
	/**
	* @Title: queryAllRoles
	* @Description: 查询所有角色
	* @return  List<Role>
	* @throws
	*/
	List<Role> queryAllRoles();

	/**
	 * 获取角色的关联信息
	 * @param id
	 * @return
	 * @author	ziwen
	 * @date	2019年11月29日
	 */
	public List<UserDomain> getUserDomainByRoleId(Long id);

	/**
	 * 通过一组角色ID删除关联关系
	 * @param ids
	 * @author	ziwen
	 * @date	2019年12月9日
	 */
	public void delRelationByRoleIds(Long[] ids);
}
