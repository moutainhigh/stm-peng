package com.mainsteam.stm.system.um.right.dao;

import java.util.List;

import com.mainsteam.stm.system.um.right.bo.Right;
import com.mainsteam.stm.system.um.right.bo.SSOForRight;

/**
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月14日
 * @author   ziwenwen
 */

public interface IRightDao {
	int updateSort(List<Right> rs);

	/**
	 * 获取所有权限
	 * @return
	 */
	List<Right> getAll();
	
	/**
	 * 根据权限类型获取权限 可取0内部、1外部
	 * @return
	 */
	List<Right> getRightByType(int type);
	
	/**
	 * 根据权限id获取权限详情
	 * @param id
	 * @return
	 */
	Right get(Long id);
	
	/**
	 * 修改权限
	 * @param Right
	 * @return
	 */
	int update(Right right);
	
	/**
	 * 删除权限
	 * @param id
	 * @return
	 */
	int del(Long id);
	
	/**
	 * 新增权限
	 * @param Right
	 * @return
	 */
	int insert(Right right);

	List<Right> getRights(Long roleId);

	/**
	 * 修改权限状态
	 * @param right
	 * @return
	 * @author	ziwen
	 * @date	2019年1月24日
	 */
	int updateStatus(Right right);

	List<Right> getAll4Skin();
	
//	/**
//	 * 获取角色是否可用的权限列表
//	 * @return
//	 * @author	ziwen
//	 * @date	2019年9月26日
//	 */
//	List<Right> getAllRightsByRoleUsedType(int roleUsed);
	/**
	 * 查询总数
	 * @author jinshengkai
	 * @return
	 */
	int getCount();
	
	int updateDelStatus(Long id);
	/**
	 * 改变列状态
	 * @param ids
	 * @param startState
	 * @return
	 */
	int updateSSOForThirdStartState(Long[] ids, int startState);
	
	/**
	 * 页签升级
	 * @param ssoForRight
	 * @return
	 */
	int updateSSOForThird(SSOForRight ssoForRight);
	
	/**
	 * 在添加操作之前将已有的模块sort加1(目前现场较多,采用这种方式对之前数据没有影响)
	 * @return
	 */
	int updateSortForInsert();
	/**
	 * 根据PID找到二级菜单的ID
	 * @param pId
	 * @return 
	 * 
	 */
	List<Long> findIdByPid(long pId);
}


