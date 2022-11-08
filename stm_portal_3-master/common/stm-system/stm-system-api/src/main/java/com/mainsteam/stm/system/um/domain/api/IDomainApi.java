package com.mainsteam.stm.system.um.domain.api;

import java.util.List;

import com.mainsteam.stm.node.Node;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.system.um.domain.bo.Domain;
import com.mainsteam.stm.system.um.domain.bo.DomainDcs;
import com.mainsteam.stm.system.um.relation.bo.UmRelation;
import com.mainsteam.stm.system.um.relation.bo.UserDomain;
import com.mainsteam.stm.system.um.relation.bo.UserRole;
import com.mainsteam.stm.system.um.user.bo.User;

public interface IDomainApi {
	/**
	 * 获取所有域列表（不分页）
	 */
	List<Domain> getAllDomains();
	/**
	 * 根据域id查询所在域下的选中的UserRole组合
	 */
	List<UserRole> getUserRolesByDomainId(Long domainId);
	/**
	 * 查询域的所有的管理员
	 */
	List<UserDomain> getDomainAdmin(UmRelation relation);
	
	/**
	* @Title: addUserRoleByDomainRel
	* @Description: 添加域与用户角色的关联关系
	* @param relations
	* @return  int
	* @throws
	*/
	int addUserRoleByDomainRel(List<UmRelation> relations);
	
	/**
	* @Title: updateUserRoleByDomainRel
	* @Description: 用最新的关联关系更新用户域角色关联关系
	* @param relations
	* @return  int
	* @throws
	*/
	int updateUserRoleByDomainRel(List<UmRelation> relations,long domainId);
	
	/**
	 * 添加域基本信息
	 * @return
	 */
	Domain insert(Domain domain);
	/**
	 * 批量删除域，同时删除域相关的用户角色关系以及域与DCS的关系
	 * @return
	 */
	List<String> batchDel(Long[] ids);
	
	/**
	 * 根据id获取对象
	 * @return
	 */
	Domain get(long id);
	
	/**
	 * 分页查询
	 * @return
	 */
	List<Domain> pageSelect(Page<Domain,User> page);
	
	/**
	 * 更新
	 * @return
	 */
	int update(Domain domain);
	
	/**
	 * 根据名称查询，判断名称是否可用
	 * @return
	 */
	int queryByName(String name);
	
	/**
	 * 根据域ID获取域采集器关联关系
	 * */
	List<DomainDcs> getDomainDcs(Long domainId);
		
	/**
	 * 修改域和采集器关联关系 
	 * */
	int updateDomainDcs(List<DomainDcs> domainDcs,Long domainId);
	
	/**
	 * 通过域ID查询域下所有DCS
	 * */
	List<DomainDcs> getDomainDcsIsChecked(Long domainId);
	
	/**
	 * 根据用户id和角色id获取域列表
	 * @param userId
	 * @param roleId
	 * @return
	 */
	public List<Domain> getDomains(Long userId,Long roleId);
	
	/**
	 * 通过域ID获取域下的采集器
	 * */
	List<Node> getDomainNodesById(Long domainId);
	
	/**
	* @Title: queryUserByDomains
	* @Description: 通过域ＩＤ集合获取域下所有用户
	* @param ids
	* @return  List<User>
	* @throws
	*/
	List<User> queryUserByDomains(Long[] ids);
}
