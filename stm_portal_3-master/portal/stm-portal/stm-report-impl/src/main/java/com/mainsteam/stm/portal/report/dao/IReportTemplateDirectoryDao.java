package com.mainsteam.stm.portal.report.dao;

import java.util.List;

import com.mainsteam.stm.portal.report.po.ReportTemplateDirectoryPo;

public interface IReportTemplateDirectoryDao {
	
	int insert(ReportTemplateDirectoryPo reportTemplateDirectoryPo);
	
	List<Long> selectDirectoryIdByTemplateId(long reportTemplateId);
	
	int del(long reportTemplateDirectoryId);
	
	List<ReportTemplateDirectoryPo> select(ReportTemplateDirectoryPo reportTemplateDirectoryPo);
	
	int update(ReportTemplateDirectoryPo reportTemplateDirectoryPo);
	
	List<Long> selectTemplateIdByTemplateId(long[] ids);
	
}
