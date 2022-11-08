package com.mainsteam.stm.portal.report.engine;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.ireport.IreportFileTypeEnum;
import com.mainsteam.stm.message.mail.MailClient;
import com.mainsteam.stm.message.mail.MailSenderInfo;
import com.mainsteam.stm.obj.TimePeriod;
import com.mainsteam.stm.portal.report.api.ReportApi;
import com.mainsteam.stm.portal.report.api.ReportTemplateApi;
import com.mainsteam.stm.portal.report.api.XMLHandlerApi;
import com.mainsteam.stm.portal.report.bo.Chapter;
import com.mainsteam.stm.portal.report.bo.Report;
import com.mainsteam.stm.portal.report.bo.ReportDirectory;
import com.mainsteam.stm.portal.report.bo.ReportTemplate;
import com.mainsteam.stm.portal.report.bo.ReportTemplateData;
import com.mainsteam.stm.portal.report.bo.ReportTemplateDirectory;
import com.mainsteam.stm.portal.report.bo.ReportTemplateDirectoryInstance;
import com.mainsteam.stm.portal.report.bo.ReportTypeEnum;
import com.mainsteam.stm.portal.report.convert.AlarmDataConvert;
import com.mainsteam.stm.portal.report.convert.AnalysisDataConvert;
import com.mainsteam.stm.portal.report.convert.AvailableDataConvert;
import com.mainsteam.stm.portal.report.convert.BaseDataConvert;
import com.mainsteam.stm.portal.report.convert.BusinessDataConvert;
import com.mainsteam.stm.portal.report.convert.PerformanceDataConvert;
import com.mainsteam.stm.portal.report.convert.TopNDataConvert;
import com.mainsteam.stm.portal.report.convert.TrendAnalysisDataConvert;
import com.mainsteam.stm.portal.report.convert.VMAlarmDataConvert;
import com.mainsteam.stm.portal.report.convert.VMPerformanceDataConvert;
import com.mainsteam.stm.simple.search.api.ISearchApi;
import com.mainsteam.stm.simple.search.bo.ResourceBizRel;
import com.mainsteam.stm.util.SpringBeanUtil;

public class ReportTask {

	private  final Log logger = LogFactory.getLog(ReportTask.class);
	
	private  XMLHandlerApi xmlHandlerApi;
	private  ReportApi reportApi;
	private  ReportTemplate reportTemplate;
	private  ISearchApi iSearchApi;
	
	private String reportName;
	//模板类型,1.性能报告2.告警统计3.TOPN报告4.可用性报告5.综合性报告
	private int reportType;
	//报表周期：1.日报2.周报3.月报
	private int cycle;

	private String timeScope;

	//是否需要发送邮件
	private boolean isSendMail=false;
	//邮件的附件类型
	private String attachFileTypes;
	
	private List<Long> resourceIds=new ArrayList<Long>();
	public ReportTask(ReportTemplate reportTemplate){
		this.reportTemplate=reportTemplate;
		this.reportType=reportTemplate.getReportTemplateType();
		this.reportName=reportTemplate.getReportTemplateName();
		this.cycle=reportTemplate.getReportTemplateCycle();
		int emailStatus=reportTemplate.getReportTemplateEmailStatus();
		isSendMail=(emailStatus==0);
		attachFileTypes=reportTemplate.getReportTemplateEmailFormat();
		
		List<ReportTemplateDirectory> directoryList=reportTemplate.getDirectoryList();
		for(ReportTemplateDirectory directory:directoryList){
			List<ReportTemplateDirectoryInstance> instanceList=directory.getDirectoryInstanceList();
			for(ReportTemplateDirectoryInstance instance:instanceList){
				resourceIds.add(instance.getInstanceId());
			}
		}
		
	}
	
	public ReportTask(long reportTemplateId){
		
		ReportTemplateApi reportTemplateApi = (ReportTemplateApi)SpringBeanUtil.getObject("ReportTemplateApi");
		ReportTemplate reportTemplate = reportTemplateApi.getReportTemplateForCurrentReport(reportTemplateId);

		this.reportTemplate=reportTemplate;
		this.reportType=reportTemplate.getReportTemplateType();
		this.reportName=reportTemplate.getReportTemplateName();
		this.cycle=reportTemplate.getReportTemplateCycle();
		int emailStatus=reportTemplate.getReportTemplateEmailStatus();
		isSendMail=(emailStatus==0);
		attachFileTypes=reportTemplate.getReportTemplateEmailFormat();
		
		List<ReportTemplateDirectory> directoryList=reportTemplate.getDirectoryList();
		for(ReportTemplateDirectory directory:directoryList){
			List<ReportTemplateDirectoryInstance> instanceList=directory.getDirectoryInstanceList();
			for(ReportTemplateDirectoryInstance instance:instanceList){
				resourceIds.add(instance.getInstanceId());
			}
		}
		
	}
	
	/**
	 * 开始产生报表
	 * @throws Exception
	 */
	public void start() throws Exception {	
		
		boolean instanceIsNull = true;
		
		//判断模板资源是否被删除后为空
		for(ReportTemplateDirectory directory : reportTemplate.getDirectoryList()){
			if(directory.getDirectoryInstanceList().size() > 0){
				instanceIsNull = false;
				break;
			}
		}
		
		if(instanceIsNull){
			logger.error("ReportTemplate have none instance!!! , reportTemplateId : " + reportTemplate.getReportTemplateId());
			return;
		}
		
		if(logger.isDebugEnabled()){
			logger.debug("ReportTask do start");
		}

		xmlHandlerApi=(XMLHandlerApi)SpringBeanUtil.getObject("xmlHandlerApi");
		reportApi=(ReportApi)SpringBeanUtil.getObject("ReportApi");
		iSearchApi=SpringBeanUtil.getBean(ISearchApi.class);
		/**
		 * 获取报表数据
		 */
		if(logger.isDebugEnabled()){
			logger.debug("ReportTask do getReportDate");
		}
		ReportTemplateData reporteDate = this.getReportDate(null,null);
		
		/**
		 * 创建XML数据
		 */
		if(logger.isDebugEnabled()){
			logger.debug("ReportTask Make report XML");
		}
	
		long xmlDataFileId=xmlHandlerApi.createRTDXmlFile(reporteDate);
		logger.info("----------------Make report Data XML:"+xmlDataFileId);

		/**
		 * 存储报表数据到DB
		 */
		if(logger.isDebugEnabled()){
			logger.debug("ReportTask saveReportToDB ");
		}
		logger.info("----------------save Report To DB.");
		

		saveReportToDB(xmlDataFileId);

		/**
		 * 发送报表给用户
		 */
		logger.info("----------------send Report E-mail to user.");
		if(isSendMail){
			String[] attachFilePaths = null;
			if (attachFileTypes != null && !attachFileTypes.isEmpty()) {
				String[] types = attachFileTypes.split(",");
				attachFilePaths = new String[types.length];
				//邮件附件类型:1.Excel2.Word3.PDF(逗号隔开)
				for (int i = 0; i < types.length; i++) {
					if ("1".equals(types[i])) {
						File attachFile=xmlHandlerApi.exportFileByType(xmlDataFileId, IreportFileTypeEnum.EXCEL, Long.parseLong(reportTemplate.getReportTemplateModelName()));
						//验证文件是否存在
						if(attachFile!=null && attachFile.isFile() && attachFile.exists()){
							String fileName = attachFile.getName();
							String fileSuffix = fileName.substring(fileName.indexOf("."));
							File reportFile = new File(attachFile.getParent() + File.separator + reportTemplate.getReportTemplateName() + fileSuffix);
							if(reportFile.exists() && reportFile.isFile()){
								if(reportFile.delete()){
									if(attachFile.renameTo(reportFile)){
										attachFilePaths[i]=reportFile.getAbsolutePath();
									}else{
										logger.error("RenameTo file fail , fail name : " + reportTemplate.getReportTemplateName() + fileSuffix);
									}
								}else{
									logger.error("Delete file fail , fail name : " + reportTemplate.getReportTemplateName() + fileSuffix);
								}
							}else{
								if(attachFile.renameTo(reportFile)){
									attachFilePaths[i]=reportFile.getAbsolutePath();
								}else{
									logger.error("RenameTo file fail , fail name : " + reportTemplate.getReportTemplateName() + fileSuffix);
								}
							}
						}
						
					}
					if ("2".equals(types[i])) {
						File attachFile=xmlHandlerApi.exportFileByType(xmlDataFileId, IreportFileTypeEnum.WORD, Long.parseLong(reportTemplate.getReportTemplateModelName()));
						//验证文件是否存在
						if(attachFile!=null && attachFile.isFile() && attachFile.exists()){
							String fileName = attachFile.getName();
							String fileSuffix = fileName.substring(fileName.indexOf("."));
							File reportFile = new File(attachFile.getParent() + File.separator + reportTemplate.getReportTemplateName() + fileSuffix);
							if(reportFile.exists() && reportFile.isFile()){
								if(reportFile.delete()){
									if(attachFile.renameTo(reportFile)){
										attachFilePaths[i]=reportFile.getAbsolutePath();
									}else{
										logger.error("RenameTo file fail , fail name : " + reportTemplate.getReportTemplateName() + fileSuffix);
									}
								}else{
									logger.error("Delete file fail , fail name : " + reportTemplate.getReportTemplateName() + fileSuffix);
								}
							}else{
								if(attachFile.renameTo(reportFile)){
									attachFilePaths[i]=reportFile.getAbsolutePath();
								}else{
									logger.error("RenameTo file fail , fail name : " + reportTemplate.getReportTemplateName() + fileSuffix);
								}
							}
						}
					}
					if ("3".equals(types[i])) {
						File attachFile=xmlHandlerApi.exportFileByType(xmlDataFileId, IreportFileTypeEnum.PDF, Long.parseLong(reportTemplate.getReportTemplateModelName()));
						//验证文件是否存在
						if(attachFile!=null && attachFile.isFile() && attachFile.exists()){
							String fileName = attachFile.getName();
							String fileSuffix = fileName.substring(fileName.indexOf("."));
							File reportFile = new File(attachFile.getParent() + File.separator + reportTemplate.getReportTemplateName() + fileSuffix);
							if(reportFile.exists() && reportFile.isFile()){
								if(reportFile.delete()){
									if(attachFile.renameTo(reportFile)){
										attachFilePaths[i]=reportFile.getAbsolutePath();
									}else{
										logger.error("RenameTo file fail , fail name : " + reportTemplate.getReportTemplateName() + fileSuffix);
									}
								}else{
									logger.error("Delete file fail , fail name : " + reportTemplate.getReportTemplateName() + fileSuffix);
								}
							}else{
								if(attachFile.renameTo(reportFile)){
									attachFilePaths[i]=reportFile.getAbsolutePath();
								}else{
									logger.error("RenameTo file fail , fail name : " + reportTemplate.getReportTemplateName() + fileSuffix);
								}
							}
						}

					}
				}
				
			}

			this.sendMail(attachFilePaths);
		}
		
		/**
		 * 搜索
		 * 
		 */
		logger.info("----------------save search resources.");
		ResourceBizRel resourceBizRel=new ResourceBizRel();
		resourceBizRel.setBizId(xmlDataFileId);
		resourceBizRel.setNav(ReportTypeEnum.getName(this.reportType));
		resourceBizRel.setResourceIds(resourceIds);
		iSearchApi.saveSearchReport(resourceBizRel);
	}
	
	
	/**
	 * get Report Date
	 * @return
	 */
	public ReportTemplateData getReportDate(Date startTime,Date endTime){

		List<ReportDirectory> reportDirectoryList=new ArrayList<ReportDirectory>();
		List<ReportTemplateDirectory> reportTemplateDirs=reportTemplate.getDirectoryList();
	
		BaseDataConvert convert=null;
		int reportCount=1;
		for(ReportTemplateDirectory reportTemplateDirectory:reportTemplateDirs){
			int directoryType=-1;
			if(reportType==ReportTypeEnum.PERFORMANCE_REPORT.getIndex()){
				/**
				 * 性能报告
				 */
				convert=new PerformanceDataConvert(reportTemplate,reportTemplateDirectory);
			}else if(reportType==ReportTypeEnum.ALARM_REPORT.getIndex()){
				/**
				 * 告警报告
				 */
				convert=new AlarmDataConvert(reportTemplate,reportTemplateDirectory);
			}else if(reportType==ReportTypeEnum.TOPN_REPORT.getIndex()){
				/**
				 * Top N 报告
				 */
				convert=new TopNDataConvert(reportTemplate,reportTemplateDirectory);
			}else if(reportType==ReportTypeEnum.AVAILABILITY_REPORT.getIndex()){
				/**
				 * 可用性报告
				 */
				convert=new AvailableDataConvert(reportTemplate,reportTemplateDirectory);
			}else if(reportType==ReportTypeEnum.TREND_REPORT.getIndex()){
				/**
				 * 趋势报告
				 */
				convert=new TrendAnalysisDataConvert(reportTemplate,reportTemplateDirectory);
			}else if(reportType==ReportTypeEnum.ANALYSIS_REPORT.getIndex()){
				/**
				 * 分析报告
				 */
				convert=new AnalysisDataConvert(reportTemplate,reportTemplateDirectory);
			}else if(reportType==ReportTypeEnum.BUSINESS_REPORT.getIndex()){
				/**
				 * 业务
				 */
				convert=new BusinessDataConvert(reportTemplate,reportTemplateDirectory);
			}
			else if(reportType==ReportTypeEnum.VM_PERFORMANCE_REPORT.getIndex()){
				/**
				 * 虚拟性能报表
				 */
				convert=new VMPerformanceDataConvert(reportTemplate,reportTemplateDirectory);
			}
			else if(reportType==ReportTypeEnum.VM_ALARM_REPORT.getIndex()){
				/**
				 * 虚拟告警报表
				 */
				convert=new VMAlarmDataConvert(reportTemplate,reportTemplateDirectory);
			}
			else if(reportType==ReportTypeEnum.COMPREHENSIVE_REPORT.getIndex()){
				/**
				 * 汇总特殊处理
				 */
				directoryType=reportTemplateDirectory.getReportTemplateDirectoryType();
				
				ReportTypeEnum reportTypeEnum=ReportTypeEnum.getReportTypeEnum(directoryType);
				switch (reportTypeEnum) {
				case PERFORMANCE_REPORT:
					convert=new PerformanceDataConvert(reportTemplate,reportTemplateDirectory);
					break;
				case ALARM_REPORT:
					convert=new AlarmDataConvert(reportTemplate,reportTemplateDirectory);
					break;
				case TOPN_REPORT:
					convert=new TopNDataConvert(reportTemplate,reportTemplateDirectory);
					break;
				case AVAILABILITY_REPORT:
					convert=new AvailableDataConvert(reportTemplate,reportTemplateDirectory);
					break;
				case TREND_REPORT:
					convert=new TrendAnalysisDataConvert(reportTemplate,reportTemplateDirectory);
					break;
				case ANALYSIS_REPORT:
					convert=new AnalysisDataConvert(reportTemplate,reportTemplateDirectory);
					break;
				case BUSINESS_REPORT:
					convert=new BusinessDataConvert(reportTemplate,reportTemplateDirectory);
					break;
				default:
					convert=new PerformanceDataConvert(reportTemplate,reportTemplateDirectory);
					break;
				}

			}
			
			if(startTime!=null && endTime!=null){
				convert.setStartTime(startTime);
				convert.setEndTime(endTime);
				
				List<TimePeriod> timePeriods=new ArrayList<TimePeriod>();
				TimePeriod timePeriod=new TimePeriod();
				timePeriod.setStartTime(startTime);
				timePeriod.setEndTime(endTime);
				timePeriods.add(timePeriod);
				convert.setTimePeriods(timePeriods);
				//即时查询
				convert.isInstantQuery = true;
			}
			ReportDirectory reportDirectory=convert.getReportDirectory();
			reportDirectory.setType(String.valueOf(directoryType));
			String name=getReportNameNo(reportCount)+reportDirectory.getName();
			reportDirectory.setName(name);
			// 报表修改chapter序号
			List<Chapter> chapterList = reportDirectory.getChapter();
			for (int i = 0; i < chapterList.size(); i++) {
				Chapter chapter = chapterList.get(i);
				//服务报表没有二级标题
				if(reportType==ReportTypeEnum.BUSINESS_REPORT.getIndex()){
					chapter.setName("");
				}else{
					chapter.setName(reportCount + "." + (i + 1) + "." + chapter.getName());
				}
			}
			
			reportDirectoryList.add(reportDirectory);
			reportCount++;
		}
		
		if(this.timeScope==null){
			this.timeScope=convert.getTimeScope();
		}
		ReportTemplateData reporteDate=new ReportTemplateData();
		reporteDate.setType(String.valueOf(this.reportType));
		reporteDate.setName(this.reportName);
		reporteDate.setCycle(String.valueOf(this.cycle));
		reporteDate.setTimeScope(timeScope);
		reporteDate.setReportDirectory(reportDirectoryList);
		
		return reporteDate;
	}

	
	private void saveReportToDB(long fileId){
		Report report=new Report();
		report.setReportName(reportTemplate.getReportTemplateName());
		report.setReportModelName(reportTemplate.getReportTemplateModelName());
		report.setReportStatus(0);
		report.setReportGenerateTime(new Date());
		report.setReportTemplateId(reportTemplate.getReportTemplateId());
		report.setReportXmlData(String.valueOf(fileId));
		reportApi.addReport(report);
	}
	
	

	
	/**
	 * 发送附件给报表制定用户
	 * @param attachFile
	 */
	private void sendMail(String[] attachFiles){
		
		String subject="Report -["+reportName+"]";
		String emailAdds=reportTemplate.getReportTemplateEmailAddress();
		
		if(emailAdds==null || "".equals(emailAdds)){
			logger.error("Not found the user E-mail address.");
			return;
		}
		
		String[] toAddress=null;
		if(emailAdds.indexOf(";")==-1){
			toAddress=new String[]{emailAdds};
		}else{
			toAddress=emailAdds.split(";"); 
		}

		String content="Report Attached Files";
		MailSenderInfo mailInfo=new MailSenderInfo();
		mailInfo.setSubject(subject);
		mailInfo.setContent(content);
		mailInfo.setToAddress(toAddress);
		
		
		if(attachFiles!=null && attachFiles.length>0){
			List<String> attachFileList=new ArrayList<String>();
			for(String filePath:attachFiles){
				if(filePath == null || filePath.equals("")){
					continue;
				}
				File file=new File(filePath);
				if(file.isFile() && file.exists()){
					attachFileList.add(filePath);
				}
			}
			mailInfo.setAttachFileNames(attachFileList.toArray(new String[attachFileList.size()]));
		}
		
		logger.trace("AttachFiles Path length:"+attachFiles.length);
		logger.trace("To Address length :"+toAddress.length);
		
		Boolean sendSucess =MailClient.sendTextMail(mailInfo);
		
		if(sendSucess){
			logger.info("------------Mail Send Sucessful-------------");
		}else{
			logger.error("------------Mail Send Faild-------------");
		}
		
	}
	
	/**
	 * 获取报表的 章节编号
	 * @param count
	 * @return
	 */
	public String getReportNameNo(int count){
		String countTitle=null;
		
		String[] countArray={".","一","二","三","四","五","六","七","八","九","十","百","千"};
		String point=countArray[0];
		
		if(count<=10){
			countTitle=countArray[count]+point;
		}else{
			//暂时扩展到10，后续可以扩展到99 -countArray[10]="十" 来拼接到999
			countTitle=count+".";
		}
		return countTitle;
	}
	
	public static void main(String[] args){

	}

}
