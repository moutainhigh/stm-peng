package com.mainsteam.stm.simple.manager.workbench.report.api;

import java.util.List;

import com.mainsteam.stm.simple.manager.workbench.report.bo.ExpectBo;
import com.mainsteam.stm.simple.manager.workbench.report.bo.MsgUserBo;
import com.mainsteam.stm.simple.manager.workbench.report.bo.WorkbenchReportBo;
import com.mainsteam.stm.simple.manager.workbench.report.bo.ReportTypeBo;

/**
 * <li>文件名称: IWorkbenchApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月17日
 * @author   ziwenwen
 */
public interface IWorkbenchApi {
	
	
	/**
	* @Title: queryReportTemplate
	* @Description: 获取报表模板
	* @return  List<ReportTypeBo>
	* @throws
	*/
	List<ReportTypeBo> queryReportTemplate();
	
	
	
	/**
	* @Title: getReports
	* @Description: 通过报表模板获取所有报表列表
	* @param templateId 报表模板ID
	* @return  List<ReportBo>
	* @throws
	*/
	List<WorkbenchReportBo> queryReportsByTemplate(Long templateId);
	
	/**
	 * <pre>
	 * 根据报表id获取报表的期望数据
	 * </pre>
	 * @param reportId
	 * @return
	 */
	List<ExpectBo> getExpects(Long reportId);
	
	/**
	 * <pre>
	 * 获取默认的报表期望值
	 * </pre>
	 * @return
	 */
	ExpectBo getDefaultExpect();
	
	
	/**
	* @Title: getExpectById
	* @Description: 通过ID获取期望值
	* @param id
	* @return  ExpectBo
	* @throws
	*/
	ExpectBo getExpectById(Long id);
	
	/**
	 * <pre>
	 * 保存期望的数据
	 * </pre>
	 * @param expect
	 * @return 返回成功保存的条数
	 */
	ExpectBo saveExpect(ExpectBo expect);
	
	/**
	 * <pre>
	 * 根据报表id获取要联系的负责人信息
	 * </pre>
	 * @param reportId
	 * @return
	 */
	List<MsgUserBo> getMsgUsers(Long reportId);
	
	/**
	* @Title: deleteExpect
	* @Description: 删除期望值
	* @param expectId
	* @return  boolean
	* @throws
	*/
	boolean deleteExpect(Long expectId);
	
	/**
	* @Title: updateExpect
	* @Description: 更新期望值状态，是否已通知过联系人
	* @param id
	* @param state
	* @return  boolean
	* @throws
	*/
	boolean updateExpect(Long id,int state);
}


