package com.mainsteam.stm.system.resource.sortable.api;

import java.util.List;

import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;

/**
 * <li>文件名称: IResourceSortableApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2015-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 单字段排序接口</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年1月7日
 * @author   ziwenwen
 */
public interface IResourceSortableBySingleFieldApi {
	
	/**
	 * <pre>
	 * 根据提供的字段和排序规则进行排序操作
	 * </pre>
	 * @param resources 无序的资源集合
	 * @param field 要排序的字段
	 * @param order 排序的方向 可取 asc：正序 desc逆序
	 * @return
	 */
	List<ResourceInstanceBo> sort(List<ResourceInstanceBo> resources,String field,String order);
}


