/**
 * 
 */
package com.mainsteam.stm.simple.manager.workbench.web.action;

import org.apache.log4j.Logger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.license.License;
import com.mainsteam.stm.license.LicenseCheckException;//zw
import com.mainsteam.stm.license.LicenseModelEnum;
import com.mainsteam.stm.message.MessageSendHelper;
import com.mainsteam.stm.message.mail.MailClient;
import com.mainsteam.stm.message.mail.MailSenderInfo;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.business.report.api.BizSerReportApi;
import com.mainsteam.stm.portal.business.report.obj.BizSerReport;
import com.mainsteam.stm.portal.report.api.ReportApi;
import com.mainsteam.stm.portal.report.api.ReportTemplateApi;
import com.mainsteam.stm.portal.report.api.XMLHandlerApi;
import com.mainsteam.stm.portal.report.bo.Chapter;
import com.mainsteam.stm.portal.report.bo.Columns;
import com.mainsteam.stm.portal.report.bo.ReportBo;
import com.mainsteam.stm.portal.report.bo.ReportDirectory;
import com.mainsteam.stm.portal.report.bo.ReportTemplate;
import com.mainsteam.stm.portal.report.bo.ReportTemplateData;
import com.mainsteam.stm.portal.report.bo.ReportTypeEnum;
import com.mainsteam.stm.portal.report.bo.Table;
import com.mainsteam.stm.portal.report.bo.TableData;
import com.mainsteam.stm.portal.report.convert.BaseDataConvert;
import com.mainsteam.stm.simple.manager.workbench.report.api.IWorkbenchApi;
import com.mainsteam.stm.simple.manager.workbench.report.bo.BusinessData;
import com.mainsteam.stm.simple.manager.workbench.report.bo.Directorie;
import com.mainsteam.stm.simple.manager.workbench.report.bo.ExpectBo;
import com.mainsteam.stm.simple.manager.workbench.report.bo.MessageInfo;
import com.mainsteam.stm.simple.manager.workbench.report.bo.MetricData;
import com.mainsteam.stm.simple.manager.workbench.report.bo.ReportData;
import com.mainsteam.stm.simple.manager.workbench.report.bo.ReportTypeBo;
import com.mainsteam.stm.simple.manager.workbench.report.bo.WorkbenchReportBo;
import com.mainsteam.stm.system.um.user.api.IUserApi;
import com.mainsteam.stm.system.um.user.bo.User;
import com.mainsteam.stm.util.DateUtil;

/**
 * <li>????????????: WorkbenchAction</li>
 * <li>????????????: ????????????????????????????????????</li>
 * <li>????????????: ????????????(C)2019-2020</li>
 * <li>????????????: ...</li>
 * <li>????????????: ...</li>
 * <li>????????????: ...</li>
 * @version  ms.stm
 * @since    2019???12???18??? ??????3:22:51
 * @author   ??????
 */
@Controller("managerWorkbenchAction")
@RequestMapping("simple/manager/workbench")
public class WorkbenchAction extends BaseAction {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(WorkbenchAction.class);

	@Resource(name="managerWorkbenchApi")
	private IWorkbenchApi workbenchApi;
	
	@Autowired
	private ReportTemplateApi reportTemplateApi;
	
	@Autowired
	private ReportApi reportApi;
	
	@Autowired
	private XMLHandlerApi xmlHandlerApi;
	
	@Autowired
	private BizSerReportApi bizSerReportApi;
	
	@Autowired
	private IUserApi userApi;
	
	/**
	* @Title: queryReportTemplate
	* @Description: ????????????????????????
	* @return  JSONObject
	* @throws
	*/
	@RequestMapping("queryReportTemplate")
	public JSONObject queryReportTemplate() {
		if (logger.isDebugEnabled()) {
			logger.debug("queryReportTemplate() - start - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())); //$NON-NLS-1$ //$NON-NLS-2$
		}

		//????????????BUSINESS_REPORT?????????????????????AVAILABILITY_REPORT??????????????????????????????
		List<ReportTemplate> templates = reportTemplateApi.getReportTemplateListByType(ReportTypeEnum.BUSINESS_REPORT);
		List<ReportTypeBo> reportTypeBos = null;
		if(null!=templates && templates.size()>0){
			reportTypeBos = new ArrayList<ReportTypeBo>();
			ReportTypeBo reportType = null;
			for (ReportTemplate reportTemplate : templates) {
				reportType = new ReportTypeBo();
				reportType.setId(reportTemplate.getReportTemplateId());
				String cycle = reportTemplate.getReportTemplateCycle()==1?"???":reportTemplate.getReportTemplateCycle()==2?"???":reportTemplate.getReportTemplateCycle()==3?"???":"";
				reportType.setName(reportTemplate.getReportTemplateName()+(cycle==""?"":"("+cycle+")"));
				reportType.setCycleType(reportTemplate.getReportTemplateCycle());
				reportTypeBos.add(reportType);
			}
		}
		JSONObject returnJSONObject = toSuccess(reportTypeBos);
		if (logger.isDebugEnabled()) {
			logger.debug("queryReportTemplate() - end - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return returnJSONObject;
	}
	
	/**
	* @Title: queryReportByTemplate
	* @Description: ????????????ID??????????????????
	* @param templateId ??????ID
	* @return  JSONObject
	* @throws
	*/
	@RequestMapping("queryReportByTemplate")
	public JSONObject queryReportByTemplate(Long templateId){
		if (logger.isDebugEnabled()) {
			logger.debug("queryReportByTemplate(Long) - start - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())); //$NON-NLS-1$ //$NON-NLS-2$
		}

		List<WorkbenchReportBo> workbenchReports = null;
		if(null!=templateId){
			List<ReportBo> reports = reportApi.getreportListByTemplateId(templateId);
			if(null!=reports && reports.size()>0){
				workbenchReports = new ArrayList<WorkbenchReportBo>();
				WorkbenchReportBo workbenchReport = null;
				for (ReportBo report : reports) {
					workbenchReport = new WorkbenchReportBo();
					workbenchReport.setId(report.getReportListId());
					workbenchReport.setReportFileId(report.getReportXmlData());
					workbenchReport.setTitle(report.getReportName());
					workbenchReport.setCreater(report.getCreateUserName());
					workbenchReport.setDate(DateUtil.format(report.getReportGenerateTime(), "yyyy-MM-dd HH:mm:ss"));
					workbenchReports.add(workbenchReport);
				}
			}
		}
		JSONObject returnJSONObject = toSuccess(workbenchReports);
		if (logger.isDebugEnabled()) {
			logger.debug("queryReportByTemplate(Long) - end - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return returnJSONObject;
	}
	
	/**
	* @Title: getReportDetailInfo
	* @Description: ????????????ID??????????????????
	* @param reportId
	* @return  JSONObject
	* @throws
	*/
	@RequestMapping("getReportDetailInfo")
	public JSONObject getReportDetailInfo(String reportFileId) {
		if (logger.isDebugEnabled()) {
			logger.debug("getReportDetailInfo(String) - start - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())); //$NON-NLS-1$ //$NON-NLS-2$
		}

		ReportData reportData = null;//????????????????????????
		if(null!=reportFileId){
			//??????????????????ID??????????????????????????????
			ReportTemplateData reportTemplateData = xmlHandlerApi.createReportTemplateData(Long.valueOf(reportFileId));
			
			//???????????????????????????????????????????????????????????????????????????
//			ReportTemplate reportTemplate = reportTemplateApi.getReportTemplateForCurrentReport(4390501);
//			ReportTemplateData reportTemplateData = null;
//			try {
//				DateFormat parseDateTime = new SimpleDateFormat("yyyy-MM-dd HH:mm");
//				Date dateStart = parseDateTime.parse("2014-01-01 00:01");
//				Date dateEnd =Calendar.getInstance().getTime();
//				ReportTask reportTask = new ReportTask(reportTemplate);
//				reportTemplateData = reportTask.getReportDate(dateStart,dateEnd);
//			} catch (ParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
			
			if(reportTemplateData!=null){
				reportData = new ReportData();
				reportData.setReportTitle(reportTemplateData.getName());
				//??????????????????
				List<ReportDirectory> reportDirectories = reportTemplateData.getReportDirectory();
				List<Directorie> directories = null;//?????????????????? 
				if (null != reportDirectories && reportDirectories.size() > 0) {
					directories = new ArrayList<Directorie>();
					Directorie directorie = null;
					for (ReportDirectory reportDirectory : reportDirectories) {
						directorie = new Directorie();
						directorie.setName(reportDirectory.getName());
						//????????????
						List<Chapter> chapters = reportDirectory.getChapter();
						List<List<BusinessData>> businessDatas = null;//???????????????????????????
						if(null!=chapters && chapters.size()>0){
							for (Chapter chapter : chapters) {
								businessDatas = new ArrayList<>();
								//??????????????????????????????
								List<Table> tables = chapter.getTable();
								//????????????????????????????????????????????????
								List<BusinessData> business = null;
								for (Table table : tables) {
									business = new ArrayList<>();
									//???????????????title
									List<Columns> columns = table.getColumnsTitle().getColumns();
									//????????????????????????
									List<TableData> tableDatas = table.getColumnsData().getTableData();
									//?????????????????????????????????
									BusinessData busines = null;
									if(tableDatas!=null && tableDatas.size()>0){
										for (TableData tableData : tableDatas) {
											busines = new BusinessData();
											//???????????????????????????
											busines.setId(tableData.getBizId());
											List<MetricData> metridData = new ArrayList<MetricData>();
											//????????????values??????????????????????????????
											String[] values = tableData.getValue().split(BaseDataConvert.SEPARATOR);
											for (int i=0;i<values.length;i++) {
												if(i==1){
													//????????????????????????????????????????????????????????????????????????????????????
													busines.setName(values[i]);
												}else if(i>1){
													metridData.add(new MetricData(columns.get(i).getBizMetricId(),columns.get(i).getText(),values[i]));
												}
											}
											busines.setMetricDatas(metridData);
											business.add(busines);
										}
									}else{
										busines = new BusinessData();
										List<MetricData> metridData = new ArrayList<MetricData>();
										for (Columns titles : columns) {
											metridData.add(new MetricData(titles.getBizMetricId(),titles.getText(),null));
										}
										busines.setMetricDatas(metridData);
										business.add(busines);
									}
									businessDatas.add(business);
								}
							}
						}
						directorie.setBusinessDatas(businessDatas);
						directories.add(directorie);
					}
					reportData.setDirectories(directories);					
				}
			}
		}
		JSONObject returnJSONObject = toSuccess(reportData);
		if (logger.isDebugEnabled()) {
			logger.debug("getReportDetailInfo(String) - end - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return returnJSONObject;
	}
	
	/**
	* @Title: queryExpectByReport
	* @Description: ????????????ID??????????????????????????????
	* @param reportId
	* @return  JSONObject
	* @throws
	*/
	@RequestMapping("queryExpectByReport")
	public JSONObject queryExpectByReport(Long reportId){
		if (logger.isDebugEnabled()) {
			logger.debug("queryExpectByReport(Long) - start - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if(null!=reportId){
			JSONObject returnJSONObject = toSuccess(workbenchApi.getExpects(reportId));
			if (logger.isDebugEnabled()) {
				logger.debug("queryExpectByReport(Long) - end - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())); //$NON-NLS-1$ //$NON-NLS-2$
			}
			return returnJSONObject;
		}
		JSONObject returnJSONObject = toSuccess(null);
		if (logger.isDebugEnabled()) {
			logger.debug("queryExpectByReport(Long) - end - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return returnJSONObject;
	}
	
	/**
	* @Title: getDefaultExpect
	* @Description: ?????????????????????
	* @return  JSONObject
	* @throws
	*/
	@RequestMapping("getDefaultExpect")
	public JSONObject getDefaultExpect(){
		if (logger.isDebugEnabled()) {
			logger.debug("getDefaultExpect() - start - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())); //$NON-NLS-1$ //$NON-NLS-2$
		}

		ExpectBo expect = workbenchApi.getDefaultExpect();
		JSONObject returnJSONObject = toSuccess(expect);
		if (logger.isDebugEnabled()) {
			logger.debug("getDefaultExpect() - end - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return returnJSONObject;
	}
	
	/**
	* @Title: getExpectById
	* @Description: ??????ID???????????????
	* @param id
	* @return  JSONObject
	* @throws
	*/
	@RequestMapping("getExpectById")
	public JSONObject getExpectById(Long id){
		if (logger.isDebugEnabled()) {
			logger.debug("getExpectById(Long) - start - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if(id!=null){
			JSONObject returnJSONObject = toSuccess(workbenchApi.getExpectById(id));
			if (logger.isDebugEnabled()) {
				logger.debug("getExpectById(Long) - end - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())); //$NON-NLS-1$ //$NON-NLS-2$
			}
			return returnJSONObject;
		}
		JSONObject returnJSONObject = toSuccess(null);
		if (logger.isDebugEnabled()) {
			logger.debug("getExpectById(Long) - end - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return returnJSONObject;
	}
	
	/**
	* @Title: saveReportExpect
	* @Description: ???????????????
	* @param expect
	* @return  JSONObject
	* @throws
	*/
	@RequestMapping("saveReportExpect")
	public JSONObject saveReportExpect(ExpectBo expect){
		if (logger.isDebugEnabled()) {
			logger.debug("saveReportExpect(ExpectBo) - start - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())); //$NON-NLS-1$ //$NON-NLS-2$
		}

		if(null!=expect){
			ILoginUser user = getLoginUser();
			expect.setCreatorId(user.getId());
			expect.setCreatorName(user.getName());
			JSONObject returnJSONObject = toSuccess(workbenchApi.saveExpect(expect));
			if (logger.isDebugEnabled()) {
				logger.debug("saveReportExpect(ExpectBo) - end - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())); //$NON-NLS-1$ //$NON-NLS-2$
			}
			return returnJSONObject;
		}
		JSONObject returnJSONObject = toSuccess(null);
		if (logger.isDebugEnabled()) {
			logger.debug("saveReportExpect(ExpectBo) - end - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return returnJSONObject;
	}
	
	/**
	* @Title: deleteReportExpect
	* @Description: ??????ID???????????????
	* @param expectId
	* @return  JSONObject
	* @throws
	*/
	@RequestMapping("deleteReportExpect")
	public JSONObject deleteReportExpect(Long expectId){
		if (logger.isDebugEnabled()) {
			logger.debug("deleteReportExpect(Long) - start - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())); //$NON-NLS-1$ //$NON-NLS-2$
		}

		JSONObject returnJSONObject = toSuccess(workbenchApi.deleteExpect(expectId));
		if (logger.isDebugEnabled()) {
			logger.debug("deleteReportExpect(Long) - end - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return returnJSONObject;
	}
	
	/**
	* @Title: getBizContacts
	* @Description: ????????????ID??????????????????
	* @param ids
	* @return  JSONObject
	* @throws
	*/
	@RequestMapping("getBizContacts")
	public JSONObject getBizContacts(Long[] ids){
		if (logger.isDebugEnabled()) {
			logger.debug("getBizContacts(Long[]) - start - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())); //$NON-NLS-1$ //$NON-NLS-2$
		}

		List<BizSerReport> bizSerReports = new ArrayList<BizSerReport>();
		if(ids !=null && ids.length>0){
			List<Long> bizIds = new ArrayList<Long>();
			for (Long id : ids) {
				if(!bizIds.contains(id)){
					bizIds.add(id);
				}
			}
			bizSerReports = bizSerReportApi.getBizSerReports(bizIds.toArray(new Long[]{}));
		}
		JSONObject returnJSONObject = toSuccess(bizSerReports);
		if (logger.isDebugEnabled()) {
			logger.debug("getBizContacts(Long[]) - end - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())); //$NON-NLS-1$ //$NON-NLS-2$
		}
		return returnJSONObject;
	}
	
	/**
	* @Title: sendEmailToContacts
	* @Description: ????????????????????????
	* @param userIds
	* @param message  void
	* @throws
	*/
	/**
	* @Title: sendEmailToContacts
	* @Description: TODO(?????????????????????????????????????????????)
	* @param messageInfos
	* @param message  void
	* @throws
	*/
	@RequestMapping("sendEmailToContacts")
	public void sendEmailToContacts(String messageInfos,String message,Long expectId){
		if (logger.isDebugEnabled()) {
			logger.debug("sendEmailToContacts(String, String, Long) - start - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())); //$NON-NLS-1$ //$NON-NLS-2$
		}

		try {
			if(messageInfos != null && !messageInfos.equals("")){
				List<MessageInfo> infos = JSONObject.parseArray(messageInfos,MessageInfo.class);
				if(infos!=null && infos.size()>0){
					MailSenderInfo sendInfo  = new MailSenderInfo();
					sendInfo.setContent(message);
					for (MessageInfo messageInfo : infos) {
						User user = userApi.get(messageInfo.getUserId());
						if(user!=null){
							sendInfo.setSubject(messageInfo.getBizName()+"????????????????????????");
							sendInfo.setToAddress(new String[]{user.getEmail()});
							MailClient.sendTextMail(sendInfo);
						}
					}
					workbenchApi.updateExpect(expectId, ExpectBo.IS_NOTICE_CONTACT);
				}
			}
		} catch (Exception e) {
			logger.error("sendEmailToContacts(String, String, Long) - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()), e); //$NON-NLS-1$ //$NON-NLS-2$

			e.printStackTrace();
		}

		if (logger.isDebugEnabled()) {
			logger.debug("sendEmailToContacts(String, String, Long) - end - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	* @Title: sendMessageToContacts
	* @Description: ????????????????????????
	* @param userIds
	* @param message  void
	* @throws
	*/
	@RequestMapping("sendMessageToContacts")
	public void sendMessageToContacts(String messageInfos,String message,Long expectId){
		if (logger.isDebugEnabled()) {
			logger.debug("sendMessageToContacts(String, String, Long) - start - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())); //$NON-NLS-1$ //$NON-NLS-2$
		}

		try {
			if(messageInfos != null && !messageInfos.equals("")){
				List<MessageInfo> infos = JSONObject.parseArray(messageInfos,MessageInfo.class);
				if(infos!=null && infos.size()>0){
					MailSenderInfo sendInfo  = new MailSenderInfo();
					sendInfo.setContent(message);
					for (MessageInfo messageInfo : infos) {
						User user = userApi.get(messageInfo.getUserId());
						if(user!=null){
							List<String> users = new ArrayList<String>();
							message = messageInfo.getBizName()+"????????????????????????;"+message;
							users.add(user.getMobile());
							MessageSendHelper.sendMessage(users, message);
						}
					}
					workbenchApi.updateExpect(expectId, ExpectBo.IS_NOTICE_CONTACT);
				}
			}
		} catch (Exception e) {
			logger.error("sendMessageToContacts(String, String, Long) - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime()), e); //$NON-NLS-1$ //$NON-NLS-2$
			e.printStackTrace();
		}

		if (logger.isDebugEnabled()) {
			logger.debug("sendMessageToContacts(String, String, Long) - end - " + new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Calendar.getInstance().getTime())); //$NON-NLS-1$ //$NON-NLS-2$
		}
	}
	
	/**
	 * <pre>
	 * ??????????????????
	 * </pre>
	 * @return
	 */
	@RequestMapping("/ifHaveBizAuthority")
	public JSONObject ifHaveBizAuthority(HttpSession session){
		//????????????????????????
		ILoginUser user = getLoginUser(session);
		if(user.isManagerUser()){
			return toSuccess(checkModularAuthority(LicenseModelEnum.stmModelBusi));
		}else if(user.isSystemUser() || user.isDomainUser()){
			return toSuccess(true);
		}else {
			return toSuccess(false);
		}
		
	}
	private boolean checkModularAuthority(LicenseModelEnum lme){
		try {
			int num = License.checkLicense().checkModelAvailableNum(lme);
			if(num>0){
				return true;
			}
		} catch (LicenseCheckException e) {
			logger.error(e.getMessage());
		}
		return false;
	}
}
