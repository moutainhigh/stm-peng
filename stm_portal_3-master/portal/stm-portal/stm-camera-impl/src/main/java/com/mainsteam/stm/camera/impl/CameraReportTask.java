package com.mainsteam.stm.camera.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;

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

public class CameraReportTask {

	private final Log logger = LogFactory.getLog(CameraReportTask.class);

	private boolean isSendMail = false;

	private ReportTemplate reportTemplate;

	private int reportType = 0;

	private String reportName;

	private int cycle = 0;

	private String attachFileTypes;

	private XMLHandlerApi xmlHandlerApi;

	private ReportApi reportApi;

	private String timeScope;

	private ISearchApi iSearchApi;

	private List<Long> resourceIds = new ArrayList();

	public CameraReportTask(ReportTemplate reportTemplate) {
		this.reportTemplate = reportTemplate;
		this.reportType = reportTemplate.getReportTemplateType();
		this.reportName = reportTemplate.getReportTemplateName();
		this.cycle = reportTemplate.getReportTemplateCycle();
		int emailStatus = reportTemplate.getReportTemplateEmailStatus();
		this.isSendMail = (emailStatus == 0);
		this.attachFileTypes = reportTemplate.getReportTemplateEmailFormat();

		List<ReportTemplateDirectory> directoryList = reportTemplate.getDirectoryList();
		for (ReportTemplateDirectory directory : directoryList) {
			List<ReportTemplateDirectoryInstance> instanceList = directory.getDirectoryInstanceList();
			for (ReportTemplateDirectoryInstance instance : instanceList)
				this.resourceIds.add(Long.valueOf(instance.getInstanceId()));
		}
	}

	public CameraReportTask(long reportTemplateId) {
		ReportTemplateApi reportTemplateApi = (ReportTemplateApi) SpringBeanUtil.getObject("ReportTemplateApi");
		ReportTemplate reportTemplate = reportTemplateApi.getReportTemplateForCurrentReport(reportTemplateId);

		this.reportTemplate = reportTemplate;
		this.reportType = reportTemplate.getReportTemplateType();
		this.reportName = reportTemplate.getReportTemplateName();
		this.cycle = reportTemplate.getReportTemplateCycle();
		int emailStatus = reportTemplate.getReportTemplateEmailStatus();
		this.isSendMail = (emailStatus == 0);
		this.attachFileTypes = reportTemplate.getReportTemplateEmailFormat();

		List<ReportTemplateDirectory> directoryList = reportTemplate.getDirectoryList();
		for (ReportTemplateDirectory directory : directoryList) {
			List<ReportTemplateDirectoryInstance> instanceList = directory.getDirectoryInstanceList();
			for (ReportTemplateDirectoryInstance instance : instanceList)
				this.resourceIds.add(Long.valueOf(instance.getInstanceId()));
		}
	}

	/**
	 * 开始定时任务
	 * @throws Exception
	 */
	public void start() throws Exception {
		boolean instanceIsNull = true;

		for (ReportTemplateDirectory directory : this.reportTemplate.getDirectoryList()) {
			if (directory.getDirectoryInstanceList().size() > 0) {
				instanceIsNull = false;
				break;
			}
		}

		if (instanceIsNull) {
			this.logger.error("ReportTemplate have none instance!!! , reportTemplateId : "
					+ this.reportTemplate.getReportTemplateId());
			return;
		}

		if (this.logger.isDebugEnabled()) {
			this.logger.info("CameraReportTask do start");
		}

		this.xmlHandlerApi = ((XMLHandlerApi) SpringBeanUtil.getObject("xmlHandlerApi"));
		this.reportApi = ((ReportApi) SpringBeanUtil.getObject("ReportApi"));
		this.iSearchApi = ((ISearchApi) SpringBeanUtil.getBean(ISearchApi.class));
		if (this.logger.isDebugEnabled()) {
			this.logger.info("CameraReportTask do getReportDate");
		}

		ReportTemplateData reporteDate = getReportDate(null, null);
		if (this.logger.isDebugEnabled()) {
			this.logger.info("CameraReportTask Make report XML");
		}

		long xmlDataFileId = this.xmlHandlerApi.createRTDXmlFile(reporteDate).longValue();
		this.logger.info("----------------Make report Data XML:" + xmlDataFileId);

		if (this.logger.isDebugEnabled()) {
			this.logger.info("CameraReportTask saveReportToDB ");
		}
		this.logger.info("----------------save Report To DB.");
		saveReportToDB(xmlDataFileId);
		if (this.isSendMail) {
			String[] attachFilePaths = null;
			if ((this.attachFileTypes != null) && (!(this.attachFileTypes.isEmpty()))) {
				String[] types = this.attachFileTypes.split(",");
				attachFilePaths = new String[types.length];

				for (int i = 0; i < types.length; ++i) {
					if ("1".equals(types[i])) {
						File attachFile = this.xmlHandlerApi.exportFileByType(Long.valueOf(xmlDataFileId),
								IreportFileTypeEnum.EXCEL,
								Long.valueOf(Long.parseLong(this.reportTemplate.getReportTemplateModelName())));

						if ((attachFile != null) && (attachFile.isFile()) && (attachFile.exists())) {
							String fileName = attachFile.getName();
							String fileSuffix = fileName.substring(fileName.indexOf("."));
							File reportFile = new File(attachFile.getParent() + File.separator
									+ this.reportTemplate.getReportTemplateName() + fileSuffix);
							if ((reportFile.exists()) && (reportFile.isFile())) {
								if (reportFile.delete()) {
									if (attachFile.renameTo(reportFile))
										attachFilePaths[i] = reportFile.getAbsolutePath();
									else
										this.logger.error("RenameTo file fail , fail name : "
												+ this.reportTemplate.getReportTemplateName() + fileSuffix);
								} else {
									this.logger.error("Delete file fail , fail name : "
											+ this.reportTemplate.getReportTemplateName() + fileSuffix);
								}
							} else if (attachFile.renameTo(reportFile))
								attachFilePaths[i] = reportFile.getAbsolutePath();
							else {
								this.logger.error("RenameTo file fail , fail name : "
										+ this.reportTemplate.getReportTemplateName() + fileSuffix);
							}
						}

					}

					if ("2".equals(types[i])) {
						File attachFile = this.xmlHandlerApi.exportFileByType(Long.valueOf(xmlDataFileId),
								IreportFileTypeEnum.WORD,
								Long.valueOf(Long.parseLong(this.reportTemplate.getReportTemplateModelName())));

						if ((attachFile != null) && (attachFile.isFile()) && (attachFile.exists())) {
							String fileName = attachFile.getName();
							String fileSuffix = fileName.substring(fileName.indexOf("."));
							File reportFile = new File(attachFile.getParent() + File.separator
									+ this.reportTemplate.getReportTemplateName() + fileSuffix);
							if ((reportFile.exists()) && (reportFile.isFile())) {
								if (reportFile.delete()) {
									if (attachFile.renameTo(reportFile))
										attachFilePaths[i] = reportFile.getAbsolutePath();
									else
										this.logger.error("RenameTo file fail , fail name : "
												+ this.reportTemplate.getReportTemplateName() + fileSuffix);
								} else {
									this.logger.error("Delete file fail , fail name : "
											+ this.reportTemplate.getReportTemplateName() + fileSuffix);
								}
							} else if (attachFile.renameTo(reportFile))
								attachFilePaths[i] = reportFile.getAbsolutePath();
							else {
								this.logger.error("RenameTo file fail , fail name : "
										+ this.reportTemplate.getReportTemplateName() + fileSuffix);
							}
						}
					}

					if ("3".equals(types[i])) {
						File attachFile = this.xmlHandlerApi.exportFileByType(Long.valueOf(xmlDataFileId),
								IreportFileTypeEnum.PDF,
								Long.valueOf(Long.parseLong(this.reportTemplate.getReportTemplateModelName())));

						if ((attachFile != null) && (attachFile.isFile()) && (attachFile.exists())) {
							String fileName = attachFile.getName();
							String fileSuffix = fileName.substring(fileName.indexOf("."));
							File reportFile = new File(attachFile.getParent() + File.separator
									+ this.reportTemplate.getReportTemplateName() + fileSuffix);
							if ((reportFile.exists()) && (reportFile.isFile())) {
								if (reportFile.delete()) {
									if (attachFile.renameTo(reportFile))
										attachFilePaths[i] = reportFile.getAbsolutePath();
									else
										this.logger.error("RenameTo file fail , fail name : "
												+ this.reportTemplate.getReportTemplateName() + fileSuffix);
								} else {
									this.logger.error("Delete file fail , fail name : "
											+ this.reportTemplate.getReportTemplateName() + fileSuffix);
								}
							} else if (attachFile.renameTo(reportFile))
								attachFilePaths[i] = reportFile.getAbsolutePath();
							else {
								this.logger.error("RenameTo file fail , fail name : "
										+ this.reportTemplate.getReportTemplateName() + fileSuffix);
							}
						}

					}

				}

			}

			sendMail(attachFilePaths);

		}

		ResourceBizRel resourceBizRel = new ResourceBizRel();
		resourceBizRel.setBizId(Long.valueOf(xmlDataFileId));
		resourceBizRel.setNav(ReportTypeEnum.getName(this.reportType));
		resourceBizRel.setResourceIds(this.resourceIds);
		this.iSearchApi.saveSearchReport(resourceBizRel);
	}

	
	public ReportTemplateData getReportDate(Date startTime, Date endTime) {
		List reportDirectoryList = new ArrayList();
		List<ReportTemplateDirectory> reportTemplateDirs = this.reportTemplate.getDirectoryList();

		BaseDataConvert convert = null;
		int reportCount = 1;

		// 用来判断是摄像头还是其他设备
		boolean typeFlag = false;

		for (ReportTemplateDirectory reportTemplateDirectory : reportTemplateDirs) {
			int directoryType = -1;
			if (this.reportType == ReportTypeEnum.PERFORMANCE_REPORT.getIndex()) {
				if (!reportTemplateDirectory.getReportTemplateDirectoryCategoryId().equals("Camera")) {
					convert = new PerformanceDataConvert(this.reportTemplate, reportTemplateDirectory);
					typeFlag = false;
				} else {
					typeFlag = true;
					convert = new CameraDataConvert(this.reportTemplate, reportTemplateDirectory, this.resourceIds);
				}

			}

			if ((startTime != null) && (endTime != null)) {
				convert.setStartTime(startTime);
				convert.setEndTime(endTime);
				List timePeriods = new ArrayList();
				TimePeriod timePeriod = new TimePeriod();
				timePeriod.setStartTime(startTime);
				timePeriod.setEndTime(endTime);
				timePeriods.add(timePeriod);
				convert.setTimePeriods(timePeriods);
				convert.isInstantQuery = true;
			}
			ReportDirectory reportDirectory = convert.getReportDirectory();
			reportDirectory.setType(String.valueOf(directoryType));
			// 执行非摄像头的逻辑
			if (!typeFlag) {
				String name = getReportNameNo(reportCount) + reportDirectory.getName();
				reportDirectory.setName(name);
			} else {
				reportDirectory.setName("视频设备考核报表");
			}

			if (!typeFlag) {
				List chapterList = reportDirectory.getChapter();
				for (int i = 0; i < chapterList.size(); ++i) {
					Chapter chapter = (Chapter) chapterList.get(i);
					if (this.reportType == ReportTypeEnum.BUSINESS_REPORT.getIndex())
						chapter.setName("");
					else {
						chapter.setName(reportCount + "." + (i + 1) + "." + chapter.getName());
					}
				}
			}

			reportDirectoryList.add(reportDirectory);
			++reportCount;
		}

		if (this.timeScope == null) {
			this.timeScope = convert.getTimeScope();
		}

		ReportTemplateData reporteDate = new ReportTemplateData();
		reporteDate.setType(String.valueOf(this.reportType));
		reporteDate.setName(this.reportName);
		reporteDate.setCycle(String.valueOf(this.cycle));
		reporteDate.setTimeScope(this.timeScope);
		reporteDate.setReportDirectory(reportDirectoryList);

		return reporteDate;
	}

	private void saveReportToDB(long fileId) {
		Report report = new Report();
		report.setReportName(this.reportTemplate.getReportTemplateName());
		report.setReportModelName(this.reportTemplate.getReportTemplateModelName());
		report.setReportStatus(0);
		report.setReportGenerateTime(new Date());
		report.setReportTemplateId(this.reportTemplate.getReportTemplateId());
		report.setReportXmlData(String.valueOf(fileId));
		this.reportApi.addReport(report);
	}

	private void sendMail(String[] attachFiles) {
		String subject = "Report -[" + this.reportName + "]";
		String emailAdds = this.reportTemplate.getReportTemplateEmailAddress();

		if ((emailAdds == null) || ("".equals(emailAdds))) {
			this.logger.error("Not found the user E-mail address.");
			return;
		}

		String[] toAddress = null;
		if (emailAdds.indexOf(";") == -1)
			toAddress = new String[] { emailAdds };
		else {
			toAddress = emailAdds.split(";");
		}

		String content = "Report Attached Files";
		MailSenderInfo mailInfo = new MailSenderInfo();
		mailInfo.setSubject(subject);
		mailInfo.setContent(content);
		mailInfo.setToAddress(toAddress);

		if ((attachFiles != null) && (attachFiles.length > 0)) {
			List attachFileList = new ArrayList();
			for (String filePath : attachFiles) {
				if (filePath == null)
					continue;
				if (filePath.equals("")) {
					continue;
				}
				File file = new File(filePath);
				if ((file.isFile()) && (file.exists())) {
					attachFileList.add(filePath);
				}
			}
			mailInfo.setAttachFileNames((String[]) attachFileList.toArray(new String[attachFileList.size()]));
		}

		this.logger.trace("AttachFiles Path length:" + attachFiles.length);
		this.logger.trace("To Address length :" + toAddress.length);

		Boolean sendSucess = Boolean.valueOf(MailClient.sendTextMail(mailInfo));

		if (sendSucess.booleanValue())
			this.logger.info("------------Mail Send Sucessful-------------");
		else
			this.logger.error("------------Mail Send Faild-------------");
	}

	public String getReportNameNo(int count) {
		String countTitle = null;

		String[] countArray = { ".", "一", "二", "三", "四", "五", "六", "七", "八", "九", "十", "百", "千" };
		String point = countArray[0];

		if (count <= 10) {
			countTitle = countArray[count] + point;
		} else {
			countTitle = count + ".";
		}
		return countTitle;
	}

}
