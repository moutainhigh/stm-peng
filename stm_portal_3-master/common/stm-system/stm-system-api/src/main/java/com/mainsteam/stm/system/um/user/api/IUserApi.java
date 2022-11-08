package com.mainsteam.stm.system.um.user.api;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.system.um.domain.bo.Domain;
import com.mainsteam.stm.system.um.relation.bo.UmRelation;
import com.mainsteam.stm.system.um.user.bo.DomainRole;
import com.mainsteam.stm.system.um.user.bo.User;
import com.mainsteam.stm.system.um.user.bo.UserResourceRel;
import com.mainsteam.stm.system.um.user.vo.UserConditionVo;

/**
 * <li>文件名称: IUserApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月19日
 * @author   ziwenwen
 */
public interface IUserApi {
	/**
	 * 新增用户 返回新增成功条数
	 * @param user
	 * @return
	 */
	int add(User user);
	int batchDel(Long[] ids);
	int batchUpdate(List<User> users);


	/**
	 * 修改用户 返回修改成功条数
	 * @param user
	 * @return
	 */
	int update(User user);

	/**
	 * 根据id获取用户详细信息
	 * @param user
	 * @return
	 */
	User get(Long id);
	
	/**
	 * <pre>
	 * 获取所有用户
	 * </pre>
	 * @return
	 */
	Map<Long, User> getAll();

	/**
	 * 根据条件分页查询用户信息
	 * @param user
	 * @return
	 */
	void selectByPage(Page<User,UserConditionVo> userPage);
	
	/**
	 * 判断用户名是否可用
	 * @param user	参数对象
	 * @return
	 * @author  ziwen
	 * @date	2019年9月2日
	 */
	int checkByAccount(User user);
	
	User getByAccount(String account);
	/**
	 * 修改密码接口
	 * @param account
	 * @param password
	 * @return
	 * @author  ziwen
	 * @date	2019年9月4日
	 */
	int updatePassword(String account,String password);
	void getAllUserByPage(Page<User,User> page);
	/**
	 * 获取用户资源权限
	 * @param userId
	 * @return
	 * @author	ziwen
	 * @date	2019年10月22日
	 */
	List<UserResourceRel> getUserResources(Long userId);
	/**
	 * 获取用户资源权限
	 * @param userId
	 * @return
	 * @author	ziwen
	 * @date	2019年10月22日
	 */
	List<UserResourceRel> getUserResources(Long userId,Long domainId);
	
	/**
	 * 获取用户资源权限 外加一组域ID
	 * @param userId
	 * @return
	 * @author	ziwen
	 * @date	2019年10月22日
	 */
	List<UserResourceRel> getUserResources(Long userId,Collection<Long> domainIds);
	/**
	 * 保存用户资源关联信息
	 * @param userResources
	 * @return
	 * @author	ziwen
	 * @param userId 
	 * @date	2019年10月22日
	 */
	int saveUserResources(List<UserResourceRel> userResources, Long userId) throws Exception;
	/**
	 * 更新用户 的 域角色关联关系
	 * @param list
	 * @author	ziwen
	 * @date	2019年10月23日
	 */
	int updateDomainRole(List<UmRelation> list, Long id);
	/**
	 * 获取所有域角色信息
	 * @return
	 * @author	ziwen
	 * @date	2019年11月17日
	 */
	List<DomainRole> getDomainRole();
	
	/**
	* @Title: queryAllUserNoPage
	* @Description: 不分布查询所有用户
	* @return  List<User>
	* @throws
	*/
	List<User> queryAllUserNoPage(User user);
	
	/**
	* @Title: queryAllUserNoPage
	* @Description: 不分布查询所有用户
	* @return  List<User>
	* @throws
	*/
	List<User> queryAllUserNoPage();
	
	/**
	 * @return
	 * @author	ziwen
	 * @date	2019年11月18日
	 */
	List<UmRelation> getDomainRoleRel(Long id);
	/**
	 * 获取用户 域角色信息
	 * @param id
	 * @return
	 * @author	ziwen
	 * @date	2019年11月27日
	 */
	List<DomainRole> getDomainRoleByUserId(Long id);
	/**
	 * 获取用户不是域管理员或者管理者的域
	 * @param id
	 * @return
	 * @author	ziwen
	 * @date	2019年12月5日
	 */
	List<Domain> getUserCommonDomains(Long id);
	/**
	 * 获取普通用户为域管理员的域
	 * @param id
	 * @return
	 * @author	ziwen
	 * @date	2019年12月17日
	 */
	List<Domain> getDomainUsersDomains(Long id);
	
	/**
	 * <pre>
	 * 
	 * </pre>
	 * @param userType
	 * @return
	 */
	List<User> getUsersByType(int userType);
	/**
	 * 导出用户信息
	 * @param conditions
	 * @return
	 * @author	ziwen
	 * @date	2019年1月5日
	 */
	List<User> exportUsers(UserConditionVo conditions);
	/**
	 * 导入用户信息
	 * @param inputStream
	 * @param loginUser
	 * @return
	 * @author	ziwen
	 * @date	2019年1月5日
	 */
	String saveUsers(InputStream inputStream, ILoginUser loginUser);
	/**
	 * 判断该资源组是否已经关联到用户
	 * @param resourceGroupId
	 * @return	true表示已经关联，false表示未关联
	 * @author	ziwen
	 * @date	2019年1月6日
	 */
	Boolean checkResourceGroupIsRelUser(Long resourceGroupId);
	/**
	 * 通过用户名获取用户信息
	 * @param account
	 * @return
	 */
	User getUserByAccount(String account);
	/**
	 * 清空所有有效用户的密码输入错误信息
	 * @return
	 */
	int updatePassErrorCnt2Zero();
	/**
	 * 更新有效用户密码更新时间
	 * @return
	 */
	int updateUpPassTime2Now();
}
