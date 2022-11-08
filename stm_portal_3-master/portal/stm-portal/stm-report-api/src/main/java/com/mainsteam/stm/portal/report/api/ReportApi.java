package com.mainsteam.stm.portal.report.api;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.report.bo.Report;
import com.mainsteam.stm.portal.report.bo.ReportBo;
import com.mainsteam.stm.portal.report.bo.ReportQueryBo;

public interface ReportApi {
	
	/**
	 * 删除报表
	 */
	public boolean removeReport(long reportId);
	
	/**
	 * 根据reportTemplateId删除报表模板
	 */
	public boolean removeReportByTemplateId(long reportTemplateId);
	
	/**
	 * 批量删除报表
	 */
	public boolean removeReportList(Long[] reportListIdArr,Long[] xmlFileIdArr);
	
	/**
	 * 查找所有报表模板
	 */
	public List<Report> getReportByTemplateId(long reportTemplateId);
	
	/**
	 * 根据reportXmlData查找报表
	 */
	public List<Report> selectByReportXmlData(String[] reportXmlData);
	
	/**
	 * 添加报表
	 */
	public boolean addReport(Report report);
	
	public void getReportPageByTemplateId(Page<ReportBo,ReportQueryBo> page);
	
	/**
	 * 更新报表已阅未阅状态
	 */
	public int updateReportStatus(Report report);
	
	/**
	 * 根据templateId 时间查询reportList
	 */
	public List<ReportBo> selectByTemplateIdAndTime(ReportQueryBo rqb);
	
	/**
	 * 系统组接口: 根据templateId 查询所有的reportList
	 */
	public List<ReportBo> getreportListByTemplateId(Long templateId);
}
