package com.mainsteam.stm.system.um.domain.dao;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.system.um.domain.bo.Domain;
import com.mainsteam.stm.system.um.domain.bo.DomainDcs;
import com.mainsteam.stm.system.um.relation.bo.UmRelation;
import com.mainsteam.stm.system.um.relation.bo.UserDomain;
import com.mainsteam.stm.system.um.relation.bo.UserRole;
import com.mainsteam.stm.system.um.user.bo.User;

public interface IDomainDao {
	/**
	 * 获取所有域列表（不分页）
	 */
	List<Domain> getAllDomains();
	/**
	 * 根据domainId获取所有选中的UserRoles组合
	 */
	List<UserRole> getUserRolesByDomainId(Long domainId) ;
	/**
	 * 查询域的所有的管理员
	 */
	List<UserDomain> getDomainAdmin(UmRelation relation);
	/**
	 * 添加
	 * @return
	 */
	int insert(Domain domain);

	/**
	 * 批量更新(逻辑删除，是否启用)
	 * @return
	 */
	int batchUpdate(List<Domain> domains);
	

	/**
	* @Title: get
	* @Description: 通过域ID获取域信息
	* @param id
	* @return  Domain
	* @throws
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
	
	List<DomainDcs> getDomainDcsRel(long domainId);
	
	
	/**
	 * 批量插入域和采集器关联关系
	 * */
	int batchInsertDomainDcsRel(List<DomainDcs> domainDcs);
	
	/**
	 * 批量删除域和采集器关联关系
	 * */
	int batchDeleteDomainDcsRel(long domainId);
	
	public List<Domain> getDomains(Long userId,Long roleId);
	/**
	 * 删除域 返回删除成功条数
	 * @param user
	 * @return
	 */
	int batchDel(Long[] ids);
	
	List<User> queryDomainUsers(Long[] ids);
}
