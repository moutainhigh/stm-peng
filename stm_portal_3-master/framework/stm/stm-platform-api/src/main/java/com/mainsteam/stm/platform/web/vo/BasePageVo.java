package com.mainsteam.stm.platform.web.vo;

import java.io.Serializable;
import java.util.Collection;

/**
 * <li>文件名称: BasePageVo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年7月24日
 * @author   ziwenwen
 */
public interface BasePageVo extends Serializable{
	
	/**
	 * 分页操作起始行
	 * @param startRow
	 */
	void setStartRow(long startRow);
	
	/**
	 * 分页查询每页展示的条数
	 * @param rowCount
	 */
	void setRowCount(long rowCount);
	
	/**
	 * 分页操作总的结果集数量
	 * @return
	 */
	long getTotal();
	
	/**
	 * 分页操作每页展示的结果集数据
	 * @return
	 */
	Collection<? extends Object> getRows();
}


