package com.mainsteam.stm.system.um.relation.dao;

import java.util.List;

import com.mainsteam.stm.system.um.relation.bo.DomainRole;
import com.mainsteam.stm.system.um.relation.bo.UmRelation;
import com.mainsteam.stm.system.um.relation.bo.UserDomain;
import com.mainsteam.stm.system.um.relation.bo.UserRole;


public interface IUmRelationDao {
	/**
	 * 批量添加
	 * @return
	 */
	int batchInsert(List<UmRelation> umRelations);
	/**
	 * 批量更新
	 * @return
	 */
	int batchUpdate(List<UmRelation> umRelations);
	/**
	* @Title: batchDel
	* @Description: 批量删除用户、角色、域关联关系
	* @param umRelations
	* @return  int
	* @throws
	*/
	int batchDel(List<UmRelation> umRelations);
	/**
	 * 查询所有用户角色关联信息
	 */
	List<UserRole> getUserRole();
	/**
	 * 查询所有用户角色关联信息for域管理员
	 * @return
	 */
	List<UserRole> getUserRoleForAdmin();
	/**
	 * 根据域Id查询用户角色信息
	 * @return
	 */
	List<UserRole> getUserRoleByDomainId(UserDomain userDomain);
	
	/**
	 * 查询所有域角色关联信息
	 */
	List<DomainRole> getDomainRole();
	/**
	 * 查询所有域角色关联信息for域管理员
	 */
	List<DomainRole> getDomainRoleForAdmin();
	/**
	 * 根据用户Id查询域角色信息
	 * @return
	 */
	List<DomainRole> getDomainRoleByUserId(Long userId);
	/**
	 * 查询所有域用户信息
	 * @return
	 */
	List<UserDomain> getUserDomain();
	/**
	 * 查询所有域用户信息for域管理员
	 * @return
	 */
	List<UserDomain> getUserDomainForAdmin();
	/**
	 * 根据角色Id查询域用户信息
	 * @return
	 */
	List<UserDomain> getUserDomainByRoleId(Long roleId);
	
	/**
	* @Title: getAllUmRelations
	* @Description: 通过关联关系对象获取所有的关联关系（）
	* @param relation
	* @return  List<UmRelation>
	* @throws
	*/
	List<UmRelation> getAllUmRelations(UmRelation relation);
}
