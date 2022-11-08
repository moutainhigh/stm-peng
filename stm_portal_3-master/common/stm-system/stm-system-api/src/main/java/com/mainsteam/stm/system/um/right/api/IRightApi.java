package com.mainsteam.stm.system.um.right.api;

import java.util.List;

import com.mainsteam.stm.license.LicenseCheckException;//zw
import com.mainsteam.stm.system.um.right.bo.Right;

/**
 * <li>文件名称: IRightApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 用于系统管理-系统设置-页签管理</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月5日
 * @author   ziwenwen
 */
public interface IRightApi {

	/**
	 * 获取所有权限
	 * @return
	 */
	List<Right> getAll() throws LicenseCheckException;
	
	/**
	 * 根据域类型获取域集合 0：内部域 1：外部域
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
	 * @param rightBo
	 * @return
	 */
	int update(Right rightBo);
	
	/**
	 * 更改页签排序
	 * @param rs
	 * @return
	 */
	int updateSort(List<Right> rs);
	
	/**
	 * 删除权限
	 * @param id
	 * @return
	 */
	int del(Long id);
	
	/**
	 * 新增权限
	 * @param rightBo
	 * @return
	 */
	int insert(Right rightBo);
	
	/**
	 * 根据角色id获取权限列表
	 * @param roleId
	 * @return
	 */
	List<Right> getRights(Long roleId) throws LicenseCheckException;
	
	int save(Right right);
	/**
	 * 修改页签状态
	 * @param right	   status值 1表示启用	0表示停用
	 * @return
	 * @author	ziwen
	 * @date	2019年1月24日
	 */
	int updateStatus(Right right);
//	/**
//	 * 获取角色可用的权限列表
//	 * @return
//	 * @author	ziwen
//	 * @date	2019年9月26日
//	 */
//	List<Right> getRoleRights();
	
	List<Right> getAll4Skin();
	
	/**
	 * 查询总条数
	 * @author jinshengkai
	 * @return
	 */
	int getCount();
	
	int updateDelStatus(Long id);
}


