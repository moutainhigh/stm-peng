package com.mainsteam.stm.export.excel;

import java.util.List;

/**
 * <li>文件名称: com.mainsteam.stm.export.excel.IExcelConvert.java</li>
 * <li>文件描述: 本类描述</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年1月6日
 */
public interface IExcelConvert {

	/**
	 * 数据转换器
	 * @param cellsValue	当前行数据
	 * @param rowIndex	当前行下标
	 * @return
	 * @author	ziwen
	 * @date	2019年1月6日
	 */
	public Object convert(List<String> cellsValue, int rowIndex);
}
