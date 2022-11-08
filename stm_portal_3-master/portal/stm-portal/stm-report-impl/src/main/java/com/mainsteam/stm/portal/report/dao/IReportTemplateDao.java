package com.mainsteam.stm.portal.report.dao;

import java.util.List;

import com.mainsteam.stm.portal.report.po.ReportTemplatePo;

public interface IReportTemplateDao {
	
	int insert(ReportTemplatePo reportTemplate);
	
	int del(Long reportTemplateId);
	
	ReportTemplatePo get(Long reportTemplateId);
	
	List<ReportTemplatePo> select(ReportTemplatePo reportTemplate);
	
	int update(ReportTemplatePo reportTemplate);
	
	List<ReportTemplatePo> getReportTemplateListByType(int type);
	
	int logicDelete(Long reportTemplateId);	
}
