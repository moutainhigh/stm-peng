package com.mainsteam.stm.portal.report.service.impl;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.BeanUtils;

import com.mainsteam.stm.platform.file.service.IFileClientApi;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.portal.report.api.ReportApi;
import com.mainsteam.stm.portal.report.bo.Report;
import com.mainsteam.stm.portal.report.bo.ReportBo;
import com.mainsteam.stm.portal.report.bo.ReportQueryBo;
import com.mainsteam.stm.portal.report.dao.IReportDao;
import com.mainsteam.stm.portal.report.po.ReportPo;
import com.mainsteam.stm.simple.search.api.ISearchApi;
import com.mainsteam.stm.simple.search.bo.ResourceBizRel;

public class ReportImpl implements ReportApi {
	
	@Resource
	private IReportDao iReportDao;
	
	@Resource
	private ISequence ReportSeq;
	
	private IFileClientApi fileClient;
	@Resource
	private ISearchApi searchApi;
	
	public void setFileClient(IFileClientApi fileClient) {
		this.fileClient = fileClient;
	}
	
	/**
	 * 删除报表
	 */
	@Override
	public boolean removeReport(long reportId) {
		//先删除xml数据文件
		ReportPo po = iReportDao.selectByReportListId(reportId);
		try {
			fileClient.deleteFile(new Long(po.getReportXmlData()));
			if(iReportDao.del(reportId) != 1){
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/**
	 * 批量删除报表
	 */
	public boolean removeReportList(Long[] reportListIdArr,Long[] xmlFileIdArr){
		if(iReportDao.delReportList(reportListIdArr)==0){
	    	return false;
	    }
		for(Long xmlFileId:xmlFileIdArr){
			try {
				fileClient.deleteFile(xmlFileId);
				//删除报表资源信息
				ResourceBizRel model = new ResourceBizRel();
				model.setBizId(xmlFileId);
				searchApi.delSearchReport(model);
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		return true;
	}
	
	public boolean removeReportByTemplateId(long reportTemplateId){
		List<ReportPo> poList = iReportDao.selectByReportTemplateId(reportTemplateId);
		Set<Long> modelNameIdSet = new HashSet<Long>();  
		for(ReportPo rp:poList){
			try {
				fileClient.deleteFile(new Long(rp.getReportXmlData()));
				
				modelNameIdSet.add(Long.valueOf(rp.getReportModelName()));
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		}
		for(Long modelNameId:modelNameIdSet){
			//删除模板的模板文件
			ReportModelMain.deleteReportModel(fileClient,modelNameId);
		}
		
		if(iReportDao.delReportByDirectoryId(reportTemplateId) == 0){
			return false;
		}
		return true;
	}
	
	/**
	 * 获取报表列表
	 */
	@Override
	public List<Report> getReportByTemplateId(long reportTemplateId) {
		List<Report> reportList = new ArrayList<Report>();
//		ReportPo reportCondionPo = new ReportPo();
//		reportCondionPo.setReportTemplateId(reportTemplateId);
//		List<ReportPo> reportPoList = iReportDao.select(reportCondionPo);
		List<ReportPo> reportPoList = iReportDao.selectByReportTemplateId(reportTemplateId);
		
		for(ReportPo po : reportPoList){
			Report report = new Report();
			BeanUtils.copyProperties(po, report);
			reportList.add(report);
		}
		
		return reportList;
	}
	
	/**
	 * 获取报表列表
	 */
	@Override
	public void getReportPageByTemplateId(Page<ReportBo,ReportQueryBo> page) {
		
		iReportDao.getAllReportByPage(page);
		
	}
	
	/**
	 * 添加报表
	 */
	@Override
	public boolean addReport(Report report) {
		
		ReportPo po = new ReportPo();
		
		BeanUtils.copyProperties(report, po);
		po.setReportListId(ReportSeq.next());
		
		if(iReportDao.insert(po) != 1){
			return false;
		}
		return true;
	}
	
	/**
	 * 根据reportXmlData查找报表
	 */
	public List<Report> selectByReportXmlData(String[] reportXmlData){
		
		List<ReportPo> rpList = iReportDao.selectByReportXmlData(reportXmlData);
		List<Report> reportList = new ArrayList<Report>();
		for(ReportPo po : rpList){
			Report report = new Report();
			BeanUtils.copyProperties(po, report);
			reportList.add(report);
		}
		return reportList;
	}
	
	/**
	 * 更新报表已阅未阅状态
	 */
	public int updateReportStatus(Report report){
		ReportPo rpo = new ReportPo();
		BeanUtils.copyProperties(report, rpo);
		return iReportDao.updateReportStatus(rpo);
	}
	
	/**
	 * 根据templateId 时间查询reportList
	 */
	public List<ReportBo> selectByTemplateIdAndTime(ReportQueryBo rqb){
		return iReportDao.selectByTemplateIdAndTime(rqb);
	}
	
	/**
	 * 系统组接口: 根据templateId 查询所有的reportList
	 */
	public List<ReportBo> getreportListByTemplateId(Long templateId){
		ReportQueryBo rqb = new ReportQueryBo();
		Long[] temArr = new Long[1];
		temArr[0] = templateId;
		rqb.setReportTemplateIdArr(temArr);
		
		return iReportDao.selectByTemplateIdAndTime(rqb);
	}

}
