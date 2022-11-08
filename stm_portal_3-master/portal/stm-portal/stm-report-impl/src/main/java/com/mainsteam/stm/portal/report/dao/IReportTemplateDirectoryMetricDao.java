package com.mainsteam.stm.portal.report.dao;

import java.util.List;

import com.mainsteam.stm.portal.report.po.ReportTemplateDirectoryMetricPo;

public interface IReportTemplateDirectoryMetricDao {

	int insert(ReportTemplateDirectoryMetricPo reportTemplateDirectoryMetricPo);
	
	int deleteMetricRelationByDirectoryId(long reportTemplateDirectoryId);
	
	List<ReportTemplateDirectoryMetricPo> select(ReportTemplateDirectoryMetricPo reportTemplateDirectoryMetricPo);
	
}
