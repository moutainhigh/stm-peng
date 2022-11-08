package com.mainsteam.stm.portal.report.dao;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.report.bo.ReportBo;
import com.mainsteam.stm.portal.report.bo.ReportQueryBo;
import com.mainsteam.stm.portal.report.po.ReportPo;

public interface IReportDao {

	int insert(ReportPo reportPo);
	
	int del(Long reportId);
	
	int delReportList(Long[] reportListIdArr);
	
	int delReportByDirectoryId(Long reportTemplateId);
	
	List<ReportPo> select(ReportPo reportPo);
	
	public void getAllReportByPage(Page<ReportBo,ReportQueryBo> page) ;
	
	ReportPo selectByReportListId(Long reportListId);
	
	List<ReportPo> selectByReportTemplateId(Long reportTemplateId);
	
	List<ReportPo> selectByReportXmlData(String[] reportXmlDataIdArr);
	
	int updateReportStatus(ReportPo report);
	
	List<ReportBo> selectByTemplateIdAndTime(ReportQueryBo rqb);
}
