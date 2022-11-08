package com.mainsteam.stm.system.um.relation.api;

import java.util.List;

import com.mainsteam.stm.system.um.relation.bo.DomainRole;
import com.mainsteam.stm.system.um.relation.bo.UmRelation;
import com.mainsteam.stm.system.um.relation.bo.UserDomain;
import com.mainsteam.stm.system.um.relation.bo.UserRole;

/**
 * <li>文件名称: IUmRelationApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 用于维护用户、角色、域关系</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月20日
 * @author   ziwenwen
 */
public interface IUmRelationApi {
	/**
	 * 添加关联关系
	 * @param umRs
	 * @return
	 */
	int addUmR(List<UmRelation> umRs);
	
	/**
	 * 将关联关系设置为有效或者无效
	 * @param umRs
	 * @return
	 */
	@Deprecated
	int checkUpdate(List<UmRelation> umRs);
	
	/**
	 * 根据对象物理删除关联关系
	 * @param umRs
	 * @return
	 */
	int batchDel(List<UmRelation> umRelations);
	
	/**
	* @Title: delRelation
	* @Description:通过对象删除组合关系
	* @param umRelation
	* @return  int
	* @throws
	*/
	int delRelation(UmRelation umRelation);
	
	/**
	 * <pre>
	 *  获取所有UserRole的组合
	 * </pre>
	 * @return
	 */
	List<UserRole> getUserRoleByDomain();
	/**
	 * 查询所有用户角色关联信息for域管理员
	 * @return
	 */
	List<UserRole> getUserRoleForAdmin();
	
	/**
	 * <pre>
	 * 获取所有UserDomain的组合
	 * </pre>
	 * @return
	 */
	List<UserDomain> getUserDomainByRole();
	/**
	 * 查询所有域用户信息for域管理员
	 * @return
	 */
	List<UserDomain> getUserDomainForAdmin();
	
	/**
	 * <pre>
	 * 获取所有DomainRole的组合
	 * </pre>
	 * @return
	 */
	List<DomainRole> getDomainRoleByUser();
	/**
	 * 查询所有域角色关联信息for域管理员
	 */
	List<DomainRole> getDomainRoleForAdmin();
	
	
	
	/**
	 * 根据domainId查询所有UserRole组合
	 * @return
	 */
	List<UserRole> getUserRoleByDomainId(UserDomain userDomain);
	
	/**
	 * <pre>
	 * 根据角色id获取用户角色组合信息
	 * 如果关联表存在该角色的关联关系，信息主要从关联表连接用户和域表查询
	 * 如果关联表不存在该域的关联关系，信息需从用户和域表组合查询
	 * 当系统不存在用户或者域信息时，返回空集合
	 * </pre>
	 * @return
	 */
	List<UserDomain> getUserDomainByRoleId(Long roleId);
	
	/**
	 * <pre>
	 * 根据用户id获取域角色组合信息
	 * 如果关联表存在该用户的关联关系，信息主要从关联表连接域和角色表查询
	 * 如果关联表不存在该用户的关联关系，信息需从域和角色表组合查询
	 * 当系统不存在域或者角色信息时，返回空集合
	 * </pre>
	 * @return
	 */
	List<DomainRole> getDomainRoleByUserId(Long userId);
	

	/**
	* @Title: getAllUmRelations
	* @Description: 通过关联关系对象获取所有的关联关系（）
	* @param relation
	* @return  List<UmRelation>
	* @throws
	*/
	List<UmRelation> getAllUmRelations(UmRelation relation);
}


