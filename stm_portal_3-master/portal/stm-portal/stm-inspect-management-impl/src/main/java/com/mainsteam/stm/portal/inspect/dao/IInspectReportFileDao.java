package com.mainsteam.stm.portal.inspect.dao;

import com.mainsteam.stm.portal.inspect.bo.InspectReportFileBo;

/**
 * <li>文件名称: IInspectReportFileDao.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2017年1月10日 下午4:40:52
 * @author   dfw
 */
public interface IInspectReportFileDao {

	/**
	 * 新增
	 * @param inspectReportFileBo
	 * @return
	 */
	int insert(InspectReportFileBo inspectReportFileBo);
	
	/**
	 * 根据报告ID获取
	 * @param id
	 * @return
	 */
	InspectReportFileBo getByReportId(long inspectReportId);
	
	/**
	 * 根据报告ID删除
	 * @param inspectReportFileBo
	 * @return
	 */
	int deleteByReportId(InspectReportFileBo inspectReportFileBo);
}
