package com.mainsteam.stm.portal.report.dao.impl;


import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.report.bo.Report;
import com.mainsteam.stm.portal.report.bo.ReportBo;
import com.mainsteam.stm.portal.report.bo.ReportQueryBo;
import com.mainsteam.stm.portal.report.dao.IReportDao;
import com.mainsteam.stm.portal.report.po.ReportPo;

public class IReportDaoImpl extends BaseDao<ReportPo> implements IReportDao {

	public IReportDaoImpl(SqlSessionTemplate session) {
		super(session, IReportDao.class.getName());
	}

	@Override
	public int delReportByDirectoryId(Long reportTemplateId) {
		return getSession().delete(getNamespace() + "delReportByDirectoryId", reportTemplateId);
	}
	
	@Override
	public void getAllReportByPage(Page<ReportBo,ReportQueryBo> page) {
//		 getSession().selectList(getNamespace()+"getAllReportByPage", page);
		this.select(SQL_COMMOND_PAGE_SELECT, page);
	}
	
	public ReportPo selectByReportListId(Long reportListId){
		List<ReportPo> rpList = getSession().selectList(getNamespace() + "selectByReportListId", reportListId);
		if(null != rpList){
			return (ReportPo)getSession().selectList(getNamespace() + "selectByReportListId", reportListId).get(0);
		}else{
			return null;
		}
		
	}
	
	public List<ReportPo> selectByReportTemplateId(Long reportTemplateId){
		return getSession().selectList(getNamespace() + "selectByReportTemplateId", reportTemplateId);
	}
	
	public List<ReportPo> selectByReportXmlData(String[] reportXmlDataIdArr){
		return getSession().selectList(getNamespace() + "selectByRreportXmlData", reportXmlDataIdArr);
	}
	
	public int updateReportStatus(ReportPo report){
		return getSession().update(getNamespace() + "reportStatusUpdate", report);
	}
	
	public List<ReportBo> selectByTemplateIdAndTime(ReportQueryBo rqb){
		return getSession().selectList(getNamespace() + "selectByTemplateIdAndTime", rqb);
	}
	
	public int delReportList(Long[] reportListIdArr){
		return getSession().delete(getNamespace() + "delReportList", reportListIdArr);
	}

}
