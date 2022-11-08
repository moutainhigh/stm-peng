package com.mainsteam.stm.portal.inspect.api;

import com.mainsteam.stm.portal.inspect.bo.InspectReportData;
import com.mainsteam.stm.portal.inspect.bo.InspectReportFileBo;

/**
 * <li>文件名称: InspectReportFileApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2017年1月10日 下午4:41:11
 * @author   dfw
 */
public interface InspectReportFileApi {

	/**
	 * 新增
	 * @param inspectReportFileBo
	 * @return
	 */
	void insert(InspectReportFileBo inspectReportFileBo);
	
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
	void deleteByReportId(InspectReportFileBo inspectReportFileBo);
	
	/**
	 * 上传并保存文件（一个事务里，保证文件和巡检报告的关系记录保持一致）
	 * @param inspectReportData
	 */
	void saveFile(InspectReportData inspectReportData);
}
