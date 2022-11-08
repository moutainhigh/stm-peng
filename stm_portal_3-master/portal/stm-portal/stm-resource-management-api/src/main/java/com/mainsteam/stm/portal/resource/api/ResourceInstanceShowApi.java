package com.mainsteam.stm.portal.resource.api;

import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.portal.resource.bo.ResourceInstancePageBo;

/**
 * <li>文件名称: ResourceInstanceShowApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月7日
 * @author   pengl
 */
public interface ResourceInstanceShowApi {

	/**
	 * 根据资源类别或者资源ID分页查询
	 * @param startRecord 起始行
	 * @param pageSize 查询的数量
	 * @param user 查询条件
	 * @return
	 */
	ResourceInstancePageBo pageSelect(long startRecord,long pageSize,ResourceInstance resourceInstance);
	
}
