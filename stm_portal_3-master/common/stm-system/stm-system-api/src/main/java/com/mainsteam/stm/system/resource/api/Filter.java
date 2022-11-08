package com.mainsteam.stm.system.resource.api;

import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;

/**
 * <li>文件名称: Filter.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月11日
 * @author   ziwenwen
 */
public interface Filter {
	/**
	 * <pre>
	 * 查询资源自定义过滤条件，返回true表示满足过滤条件，返回数据
	 * </pre>
	 * @param riBo
	 * @return
	 */
	boolean filter(ResourceInstanceBo riBo);
}


