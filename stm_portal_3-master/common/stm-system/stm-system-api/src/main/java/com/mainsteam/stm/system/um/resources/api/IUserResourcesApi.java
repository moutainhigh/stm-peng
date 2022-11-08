package com.mainsteam.stm.system.um.resources.api;

import java.util.List;

import com.mainsteam.stm.system.um.resources.bo.UserResourceBo;

/**
 * <li>文件名称: IUserResourcesApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月15日
 * @author   ziwenwen
 */
public interface IUserResourcesApi {
	/**
	 * <pre>
	 * 批量插入用户域资源关联关系
	 * </pre>
	 * @param urBos
	 * @return
	 */
	int batchInsert(List<UserResourceBo> urBos);
	
	/**
	 * <pre>
	 * 
	 * </pre>
	 * @param urBos
	 * @return
	 */
	int batchDel(List<UserResourceBo> urBos);
	
	/**
	 * <pre>
	 * 根据用户id获取用户拥有的资源
	 * </pre>
	 * @param userId
	 * @return
	 */
	List<UserResourceBo> getByUserId(Long userId);
	
	/**
	 * <pre>
	 * 重置用户所拥有的资源信息
	 * 目前的需求为，建立用户或者分配用户域时将域下的所有资源分配给用户
	 * </pre>
	 * @param userId
	 * @return
	 */
	int resetUserResource(Long userId);
}


