package com.mainsteam.stm.system.um.role.api;

import java.util.List;

import com.mainsteam.stm.license.LicenseCheckException;//zw
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.system.um.relation.bo.UserDomain;
import com.mainsteam.stm.system.um.role.bo.Role;
import com.mainsteam.stm.system.um.role.bo.RoleRight;
import com.mainsteam.stm.system.um.role.bo.RoleRightRel;

/**
 * <li>文件名称: IRoleApi.java</li>
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
public interface IRoleApi {
	/**
	 * 获取角色列表信息
	 * @param page	分页对象
	 * @return
	 * @author  ziwen
	 * @date	2019年8月19日
	 */
	public void pageSelect(Page<Role, Role> page);
	/**
	 * 写入角色信息
	 * @param role	角色信息
	 * @return
	 * @author  ziwen
	 * @date	2019年8月19日
	 */
	public Long insert(Role role);
	/**
	 * 删除角色信息（此处为批量逻辑删除）
	 * @param roles	角色列表
	 * @return
	 * @author  ziwen
	 * @date	2019年8月19日
	 */
	public int batchDel(Long[] ids);
	/**
	 * 通过主键获取角色信息
	 * @param id
	 * @return
	 * @author  ziwen
	 * @date	2019年8月19日
	 */
	public Role get(long id);
	/**
	 * 更新角色信息
	 * @param role
	 * @return
	 * @author  ziwen
	 * @date	2019年8月19日
	 */
	public int update(Role role);
	
	/**
	 * 根据角色获取角色权限信息
	 * @param roleId
	 * @return
	 */
	public List<RoleRight> getRight(Long roleId) throws LicenseCheckException;
	
	/**
	 * 根据一组角色id获取权限信息
	 * @param roleId
	 * @return
	 * 此方法有改动
	 * @author  ziwen
	 */
	@Deprecated
	public List<RoleRight> getRight(Long[] roleIds) throws LicenseCheckException;
	/**
	 * 检测角色名是否可用
	 * @param role
	 * @return
	 * @author  ziwen
	 * @date	2019年8月21日
	 */
	public int checkName(Role role);
	/**
	 * @param roles
	 * @return
	 * @author  ziwen
	 * @date	2019年8月23日
	 */
	public int batchUpdateRight(List<RoleRightRel> roles);
	
	/**
	 * 根据用户id获取角色列表
	 * @param userId
	 * @return
	 */
	public List<Role> getRoles(Long userId);
	/**
	 * 检测角色是否已经关联用户
	 * @param ids
	 * @return
	 * @author	ziwen
	 * @date	2019年10月9日
	 */
	public boolean checkRelUser(Long[] ids);
	
	/**
	* @Title: queryAllRoleNoPage
	* @Description: 不分页查询所有角色
	* @return  List<Role>
	* @throws
	*/
	List<Role> queryAllRoleNoPage();
	/**
	 * 通过角色ID获取角色的用户域关联信息
	 * @param id
	 * @return
	 * @author	ziwen
	 * @date	2019年11月29日
	 */
	public List<UserDomain> getUserDomainByRoleId(Long id);
}
