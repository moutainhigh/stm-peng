package com.mainsteam.stm.portal.report.web.action;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.license.License;
import com.mainsteam.stm.license.LicenseCheckException;//zw
import com.mainsteam.stm.license.LicenseModelEnum;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.report.api.ReportApi;
import com.mainsteam.stm.portal.report.api.ReportTemplateApi;
import com.mainsteam.stm.portal.report.bo.Report;
import com.mainsteam.stm.portal.report.bo.ReportBo;
import com.mainsteam.stm.portal.report.bo.ReportQueryBo;
import com.mainsteam.stm.portal.report.bo.ReportTemplate;
import com.mainsteam.stm.portal.report.bo.ReportTemplateData;
import com.mainsteam.stm.portal.report.bo.ReportTemplateDirectory;
import com.mainsteam.stm.portal.report.bo.ReportTemplateDirectoryInstance;
import com.mainsteam.stm.portal.report.bo.ReportTemplateExpand;
import com.mainsteam.stm.portal.report.bo.ReportTypeEnum;
import com.mainsteam.stm.portal.report.engine.ReportTask;
import com.mainsteam.stm.portal.report.service.impl.ReportTemplateImpl;
import com.mainsteam.stm.portal.report.web.vo.ReportPageVo;
import com.mainsteam.stm.portal.report.web.vo.ReportTemplateVo;
import com.mainsteam.stm.portal.report.web.vo.ReportVo;
import com.mainsteam.stm.system.um.domain.api.IDomainApi;
import com.mainsteam.stm.system.um.domain.bo.Domain;
import com.mainsteam.stm.system.um.relation.bo.UserRole;
import com.mainsteam.stm.util.DateUtil;
/**
 * 
 * <li>????????????: ReportTemplateListAction.java</li>
 * <li>????????????: ????????????????????????????????????</li>
 * <li>????????????: ????????????(C)2019-2020</li>
 * <li>????????????: ...</li>
 * <li>????????????: ...</li>
 * <li>????????????: ...</li>
 * @version  ms.stm
 * @since    2019???10???22???
 * @author   tongpl
 */

@Controller
@RequestMapping("/portal/report/reportTemplateList")
public class ReportTemplateListAction extends BaseAction {
	private static final Log logger = LogFactory.getLog(ReportTemplateListAction.class);
	
	@Resource
	private ReportTemplateApi reportTemplateApi;
	
	@Resource
	private ReportApi ReportApi;
	
	@Resource
	private IDomainApi domainApi;
	@RequestMapping("/busRep")
	public JSONObject busRep() throws LicenseCheckException{
		License lic = License.checkLicense();
		int busRep = lic.checkModelAvailableNum(LicenseModelEnum.stmModelBusi);
		return toSuccess(busRep);
	}
	
	/**
	 * ??????????????????
	 * @param
	 * @return
	 */
	@RequestMapping("/getReportList")
	public JSONObject getReportByType(HttpSession session){
		//????????????????????????
		ILoginUser user = getLoginUser(session);
		return toSuccess(reportTemplateApi.getAllReportTemplate(user));
	}
	
	/**
	 * reportList??????
	 * @param
	 * @return
	 */
	@RequestMapping("/delReportListById")
	public JSONObject delReportListById(Long reportListId,Long reportTemplateCreateUserId,Long domainId,HttpSession session){
		//??????????????????
		//????????????????????????
		ILoginUser user = getLoginUser(session);
		if(reportTemplateCreateUserId.equals(user.getId())){
			//??????
			return toSuccess(ReportApi.removeReport(reportListId));
		}else{
			if(user.isSystemUser()){
				//??????
				return toSuccess(ReportApi.removeReport(reportListId));
			}else if(user.isDomainUser()||user.isManagerUser()){
				//????????????,???????????????????????????????????????
				Set<IDomain> idList1 = user.getDomainManageDomains();
				Set<IDomain> idList2 = user.getManageDomains();
				boolean flag = false;
				for(IDomain id:idList1){
					if(domainId==id.getId()){
						//??????
						flag = true;
						break;
					}
				}
				if(!flag){
					for(IDomain id:idList2){
						if(domainId==id.getId()){
							//??????
							flag = true;
							break;
						}
					}
				}
				if(flag){
					//??????
					return toSuccess(ReportApi.removeReport(reportListId));
				}else{
					//????????????
					return toSuccess("permissionFalse");
				}
				
			}else if(user.isCommonUser()){
				//????????????
				return toSuccess("permissionFalse");
			}
			return toSuccess("permissionFalse");
		}
	}
	
	/**
	 * reportList????????????
	 * @param
	 * @return
	 */
	@RequestMapping("/delReportListByIdArr")
	public JSONObject delReportListByIdArr(Long[] reportListIdArr,Long[] xmlFileIdArr){
		return toSuccess(ReportApi.removeReportList(reportListIdArr,xmlFileIdArr));
	}
	
	/**
	 * reportTemplate??????
	 * @param
	 * @return
	 */
	@RequestMapping("/delReportTemplateById")
	public JSONObject delReportTemplateById(Long reportTemplateId,int removeType){
		if(removeType==2){
			return toSuccess(reportTemplateApi.removeReportTemplate(reportTemplateId));
		}else{
			return toSuccess(reportTemplateApi.removeReportTemplateOnly(reportTemplateId));
		}
	}
	
	/**
	 * ??????reportTemplate?????????reportList??????
	 * @param
	 * @return
	 */
	@RequestMapping("/getReportTemplateAndreportListById")
	public JSONObject getReportTemplateAndreportListById(ReportPageVo reportPageVo,HttpSession session){
		Long reportTemplateId = reportPageVo.getReportTemplateId()[0];
		ReportTemplateExpand  rt= reportTemplateApi.getSimpleReportTemplateById(reportTemplateId);
		ReportQueryBo rqb = new ReportQueryBo();
		if(reportTemplateId>0){
			rqb.setReportTemplateIdArr(reportPageVo.getReportTemplateId());
		}
		ReportTemplateVo rto = new ReportTemplateVo();
		BeanUtils.copyProperties(rt,rto);
		rto = analyseReportTemplateHandWay(rto,session);
		
		reportPageVo = queryReportPageVo(reportPageVo,session);//ReportApi.selectByTemplateIdAndTime(rqb);
		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("reportTemplateInfo", rto);
		map.put("reportList", reportPageVo);
		
		
		return toSuccess(map);
	}
	
	/**
	 * ??????reportTemplate??????
	 * @param
	 * @return
	 */
	@RequestMapping("/getSimpleReportTemplateById")
	public JSONObject getSimpleReportTemplateById(Long reportTemplateId){
		return toSuccess(reportTemplateApi.getSimpleReportTemplateById(reportTemplateId));
	}
	
	/**
	 * ????????????    ?????????????????????reportTemplate???????????????(????????????,????????????)
	 * @param
	 * @return
	 */
	@RequestMapping("/getReportListByReportTemplateIdAndTime")
	public JSONObject getReportListByReportTemplateIdAndTime(Long reportTemplateId,String dateStartStr,String dateEndStr){
		
		ReportTemplate reportTemplate = reportTemplateApi.getReportTemplateForCurrentReport(reportTemplateId);
		DateFormat parseDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm");
		
		boolean instanceIsNull = true;
		//??????????????????????????????????????????
		for(ReportTemplateDirectory directory : reportTemplate.getDirectoryList()){
			if(directory.getDirectoryInstanceList().size() > 0){
				instanceIsNull = false;
				break;
			}
		}
		
		if(instanceIsNull){
			return toJsonObject(201, "?????????????????????????????????!");
		}
		
		try{
			Date dateStart = parseDateTime.parse(dateStartStr);
			Date dateEnd = parseDateTime.parse(dateEndStr);
			ReportTask reportTask = new ReportTask(reportTemplate);
			
			return toSuccess(reportTask.getReportDate(dateStart,dateEnd));
		}catch(Exception e){
			e.printStackTrace();
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
			return toSuccess("error!!!");
		}
		
	}
	
	/**
	 * ??????reportList??????
	 * @param
	 * @return
	 */
	@RequestMapping("/getAllReportListByTemplateId")
	public JSONObject getAllReportListByTemplateId(Long reportTemplateId,String dateStartStr,String dateEndStr){
		ReportQueryBo rqb = new ReportQueryBo();
		if(reportTemplateId>0){
			Long[] reportTemplateIdArr = new Long[1];
			reportTemplateIdArr[0] = reportTemplateId;
			rqb.setReportTemplateIdArr(reportTemplateIdArr);
		}
		if(!dateStartStr.equals(dateEndStr)){
			rqb.setReportDateEndSelect(dateEndStr);
			rqb.setReportDateStartSelect(dateStartStr);
		}
		
		return toSuccess(ReportApi.selectByTemplateIdAndTime(rqb));
	}
	/*
	 * ???????????????????????????????????????
	 * */
	@RequestMapping("/getUserDomain")
	public JSONObject getUserDomain(HttpSession session){
		//????????????????????????
		ILoginUser user = getLoginUser(session);
		List<Map<String,Object>>  domainIdMapList = new ArrayList<Map<String,Object>>();
		if(user.isCommonUser()&&!(user.isDomainUser()||user.isManagerUser())){
			//???????????????????????????
			
		}else if(user.isDomainUser()||user.isManagerUser()){
			//?????????
			Set<IDomain> idListManageDomains = user.getDomainManageDomains();
			Set<IDomain> idListManage = user.getManageDomains();
			idListManage.addAll(idListManageDomains);
			Set<IDomain> allList = idListManage;
			
			Set<Long>  domainIdSet = new HashSet<Long>();
			for(IDomain idm:allList){
				domainIdSet.add(idm.getId());
			}
			for(Long doId:domainIdSet){
				for(IDomain idm:allList){
					if(doId==idm.getId()){
						Map<String,Object> map = new HashMap<String,Object>();
						map.put("domainId", doId);
						map.put("domainName", idm.getName());
						domainIdMapList.add(map);
						break;
					}
				}
			}
		}else if(user.isSystemUser()){
			//?????????????????????????????????????????????
			List<Domain> idListAll = domainApi.getAllDomains();
			for(Domain dom:idListAll){
				Map<String,Object> map = new HashMap<String,Object>();
				map.put("domainId", dom.getId());
				map.put("domainName", dom.getName());
				domainIdMapList.add(map);
			}
		}
		return toSuccess(domainIdMapList);
	}
	
	/**
	 * ??????report type ??????????????????
	 * @param
	 * @return
	 */
	@RequestMapping("/getReportTemplateListByType")
	public JSONObject getReportTemplateListByType(int type){
		//ReportTypeEnum
//		List<ReportTemplate> rtList = reportTemplateApi.getReportTemplateListByType(ReportTypeEnum.BUSINESS_REPORT);
		List<ReportBo> rtList = ReportApi.getreportListByTemplateId(new Long(4225004));
		
		return toSuccess(rtList);
	}
	
	/**
	 * ??????reportTemplateId????????????
	 * @param
	 * @return
	 */
	@RequestMapping("/getReportListByTemplateId")
	public JSONObject getReportListByTemplateId(ReportPageVo rpv,HttpSession session){
		
		return toSuccess(queryReportPageVo(rpv,session));
	}
	
	private ReportTemplateVo analyseReportTemplateHandWay(ReportTemplateVo rtv,HttpSession session){
		//????????????????????????
		ILoginUser user = getLoginUser(session);
		if(user.isSystemUser()){
			rtv.setRemove(true);
		}else if(user.isDomainUser()){
			Set<IDomain> idList1 = user.getDomainManageDomains();
//			Set<IDomain> idList2 = user.getManageDomains();
			
			for(IDomain id:idList1){
				if(rtv.getReportTemplateDomainId()==id.getId()){
					rtv.setRemove(true);
					break;
				}
			}
//			if(!rtv.isRemove()){
//				for(IDomain id:idList2){
//					if(rtv.getReportTemplateDomainId()==id.getId()){
//						rtv.setRemove(true);
//						break;
//					}
//				}
//			}
		}else if(user.isCommonUser()){
		}
		
		if(user.getId() == rtv.getReportTemplateCreateUserId()){
			rtv.setRemove(true);
		}
		//??????????????????
		if(user.getId() == rtv.getReportTemplateCreateUserId()){
			rtv.setEdit(true);
		}
		return rtv;
	}
	/*          ????????????              ????????????(???????????????)   ??????????????? (??????????????????)  ??????????????? 
	 * ??????:     ????????????              ????????????                                      ????????????                                         ??????
	 * ?????????              ????????????              ????????????                                      ????????????                                         ????????????             
	 * ?????????              ????????????              ????????????                                      ????????????                                         ??????
	 * */
	private ReportQueryBo userDomainManagement(ReportPageVo rpv,HttpSession session){
		ReportQueryBo rqb = new ReportQueryBo();
		//????????????????????????
		ILoginUser user = getLoginUser(session);
		Long[] userIdArr = null;
		Long[] userDomainId = null;
		
		//????????????????????????????????????????????????????????????
		if(null==rpv.getReportQueryDomain()){
			if(user.isCommonUser()&&!(user.isDomainUser()||user.isManagerUser())){
				//?????????????????????,??????,???????????????????????????
				userIdArr = new Long[1];
				userIdArr[0] = user.getId();
				
			}else if(user.isDomainUser()||user.isManagerUser()){
				//??????,??????,???????????????????????????
				userIdArr = new Long[1];
				userIdArr[0] = user.getId();
				
				//????????????,???????????????????????????????????????
				Set<IDomain> idList1 = user.getDomainManageDomains();
				Set<IDomain> idList2 = user.getManageDomains();
				Set<Long>  domainIdSet = new HashSet<Long>();
				for(IDomain idm:idList1){
					domainIdSet.add(idm.getId());
				}
				for(IDomain idm:idList2){
					domainIdSet.add(idm.getId());
				}
				userDomainId = new Long[domainIdSet.size()];
				domainIdSet.toArray(userDomainId);
				
			}else if(user.isSystemUser()){
				//???????????????????????????????????????
			}
		}
		
		//??????????????????
		if(null!=userIdArr){
			rqb.setUserIdArr(userIdArr);
		}
			
		if(null != rpv.getReportQueryDomain()){
//			//?????????????????????????????????
//			userIdArr = new Long[1];
//			userIdArr[0] = user.getId();
//			rqb.setUserIdArr(userIdArr);
			
			rqb.setReportTemplateDomainId(rpv.getReportQueryDomain());
		}else if(null!=userDomainId){
			rqb.setReportTemplateDomainId(userDomainId);
		}
		
		if(null!=rpv.getReportTemplateId() && rpv.getReportTemplateId().length>0){
			rqb.setReportTemplateIdArr(rpv.getReportTemplateId());
		}
		
		if(null!=rpv.getReportDateStartSelect()&&null!=rpv.getReportDateEndSelect()&&!rpv.getReportDateStartSelect().equals(rpv.getReportDateEndSelect())){
			rqb.setReportDateEndSelect(rpv.getReportDateEndSelect());
			rqb.setReportDateStartSelect(rpv.getReportDateStartSelect());
		}
		if(null!=rpv.getReportName() && !"".equals(rpv.getReportName().trim())){
			rqb.setReportName(rpv.getReportName());
		}
		if(null!=rpv.getReportQueryStatus()){
			rqb.setReportQueryStatus(rpv.getReportQueryStatus());
		}
		if(null!=rpv.getReportTemplateQueryCycle()){
			rqb.setReportTemplateQueryCycle(rpv.getReportTemplateQueryCycle());
		}
        if(null!=rpv.getReportQueryCreateUserName()){
			rqb.setReportQueryCreateUserName(rpv.getReportQueryCreateUserName());
		}
        if(rpv.getReportType() > 0){
			rqb.setReportType(rpv.getReportType());
		}
        if(rpv.getReportDateStartSelect()!=null && rpv.getReportDateEndSelect()!=null){
        	if(rpv.getReportDateStartSelect().equals(rpv.getReportDateEndSelect())){
        		rqb.setReportDateStartSelect(null);
        		rqb.setReportDateEndSelect(null);
        	}else{
        		rqb.setReportDateStartSelect(rpv.getReportDateStartSelect());
        		rqb.setReportDateEndSelect(rpv.getReportDateEndSelect());
        	}
        	
        }
        if(rpv.getOrder()!=null){
        	rqb.setOrder(rpv.getOrder().toLowerCase());
        }
        return rqb;
	}
    private ReportPageVo queryReportPageVo(ReportPageVo rpv,HttpSession session){
    	Page<ReportBo, ReportQueryBo> page = new Page<ReportBo, ReportQueryBo>();
		ReportQueryBo rqb = new ReportQueryBo();

		if(rpv.getReportType() == ReportTypeEnum.BUSINESS_REPORT.getIndex()){
			//?????????????????????,??????????????????,????????????
			rqb.setReportType(rpv.getReportType());
		    if(rpv.getReportDateStartSelect()!=null && rpv.getReportDateEndSelect()!=null){
	        	if(rpv.getReportDateStartSelect().equals(rpv.getReportDateEndSelect())){
	        		rqb.setReportDateStartSelect(null);
	        		rqb.setReportDateEndSelect(null);
	        	}else{
	        		rqb.setReportDateStartSelect(rpv.getReportDateStartSelect());
	        		rqb.setReportDateEndSelect(rpv.getReportDateEndSelect());
	        	}
	        	
	        }
//		}else{
//			rqb = userDomainManagement(rpv,session);
		}
		rqb = userDomainManagement(rpv,session);
		
		page.setCondition(rqb);
		page.setRowCount(rpv.getRowCount());
		page.setStartRow(rpv.getStartRow());
		
		ReportApi.getReportPageByTemplateId(page);
		
		rpv.setTotalRecord(page.getTotalRecord());
		
		List<ReportVo> rv = new ArrayList<ReportVo>();
		Set<Long> reportDomainIdSet = new HashSet<Long>();
		
		//????????????????????????
		ILoginUser user = getLoginUser(session);
		boolean delFlag = false;
		if(user.isDomainUser()){
			delFlag = true;
		}else if(user.isSystemUser()){
			//???????????????????????????????????????
			delFlag = true;
		}
		
		for(ReportBo rt:page.getDatas()){
			ReportVo rvTemp = toReportVo(rt);
			//??????????????????????????????????????????
			if(delFlag){
				rvTemp.setRemoveAble(delFlag);
			}else{
				if(user.getId().longValue() == Long.parseLong(rt.getCreateUserId())){
					rvTemp.setRemoveAble(true);
				}else{
					rvTemp.setRemoveAble(false);
				}
			}
			rv.add(rvTemp);
			if(rt.getReportTemplateDomainId()>0){
				reportDomainIdSet.add(rt.getReportTemplateDomainId());
			}
		}
		//???????????????   ?????????????????????
		for(Long domainId:reportDomainIdSet){
			Domain domain = domainApi.get(domainId);
			if(null != domain){
				for(ReportVo rvo:rv){
					if(null!=rvo.getReportTemplateDomainId() && rvo.getReportTemplateDomainId()==domain.getId()){
						rvo.setReportTemplateDomainName(domain.getName());
					}
				}
			}
		}
		
		rpv.setReports(rv);
		return rpv;
    }
	
	private ReportVo toReportVo(ReportBo rt){
		ReportVo rvTemp = new ReportVo();
		rvTemp.setReportTemplateId(rt.getReportTemplateId());
		rvTemp.setReportListId(rt.getReportListId());
		rvTemp.setReportGenerateTime(rt.getReportGenerateTime());
		rvTemp.setReportModelName(rt.getReportModelName());
		rvTemp.setReportName(rt.getReportName());
		rvTemp.setReportStatus(rt.getReportStatus());
		rvTemp.setReportXmlData(rt.getReportXmlData());
		
		rvTemp.setReportTemplateType(rt.getReportTemplateType());
		rvTemp.setReportTemplateCycle(rt.getReportTemplateCycle());
		rvTemp.setReportTemplateCreateUser(rt.getCreateUserName());
		rvTemp.setCreateUserId(rt.getCreateUserId());
		if(rt.getReportTemplateDomainId()>0){
			rvTemp.setReportTemplateDomainId(rt.getReportTemplateDomainId());
		}
		
		return rvTemp;
	}

}
