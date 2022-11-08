package com.mainsteam.stm.portal.vm.web.vo;

import java.util.Collection;
import java.util.List;

import com.mainsteam.stm.platform.web.vo.BasePageVo;

public class VmReportPageVo implements BasePageVo{

		/**
	 * 
	 */
	private static final long serialVersionUID = 5702368759812018622L;
		/**
		 * 
		 */
		
		
		private long startRow;
		private long rowCount;
		private long totalRecord;
		
//		@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")  
//		private Date reportDateStartSelect;
//		@DateTimeFormat(pattern="yyyy-MM-dd HH:mm:ss")  
//		private Date reportDateEndSelect;
		private String reportDateStartSelect;
		private String reportDateEndSelect;
		
		private int reportType;
	    private String reportName;
		private VmReportVo condition;
		private Long[] reportTemplateId;
		private List<VmReportVo> reports;
		//datagrid过滤查询条件
		private String[] reportQueryStatus;
		private String[] reportTemplateQueryCycle;
		private String  reportQueryCreateUserName;
		private Long[] reportQueryDomain;
		
		public Long[] getReportQueryDomain() {
			return reportQueryDomain;
		}

		public void setReportQueryDomain(Long[] reportQueryDomain) {
			this.reportQueryDomain = reportQueryDomain;
		}

		public int getReportType() {
			return reportType;
		}

		public void setReportType(int reportType) {
			this.reportType = reportType;
		}

		public String getReportQueryCreateUserName() {
			return reportQueryCreateUserName;
		}

		public void setReportQueryCreateUserName(String reportQueryCreateUserName) {
			this.reportQueryCreateUserName = reportQueryCreateUserName;
		}

		public String[] getReportQueryStatus() {
			return reportQueryStatus;
		}

		public void setReportQueryStatus(String[] reportQueryStatus) {
			this.reportQueryStatus = reportQueryStatus;
		}

		public String[] getReportTemplateQueryCycle() {
			return reportTemplateQueryCycle;
		}

		public void setReportTemplateQueryCycle(String[] reportTemplateQueryCycle) {
			this.reportTemplateQueryCycle = reportTemplateQueryCycle;
		}

		public long getStartRow() {
			return startRow;
		}

		public void setStartRow(long startRow) {
			this.startRow = startRow;
		}

		public long getRowCount() {
			return rowCount;
		}

		public void setRowCount(long rowCount) {
			this.rowCount = rowCount;
		}

		public long getTotalRecord() {
			return totalRecord;
		}

		public void setTotalRecord(long totalRecord) {
			this.totalRecord = totalRecord;
		}

		public String getReportDateStartSelect() {
			return reportDateStartSelect;
		}

		public String getReportDateEndSelect() {
			return reportDateEndSelect;
		}

		public void setReportDateStartSelect(String reportDateStartSelect) {
			this.reportDateStartSelect = reportDateStartSelect;
		}

		public void setReportDateEndSelect(String reportDateEndSelect) {
			this.reportDateEndSelect = reportDateEndSelect;
		}

		
		
		public VmReportVo getCondition() {
			return condition;
		}

		public void setCondition(VmReportVo condition) {
			this.condition = condition;
		}

		public List<VmReportVo> getReports() {
			return reports;
		}

		public void setReports(List<VmReportVo> reports) {
			this.reports = reports;
		}

		public String getReportName() {
			return reportName;
		}

		public void setReportName(String reportName) {
			this.reportName = reportName;
		}


		public Long[] getReportTemplateId() {
			return reportTemplateId;
		}

		public void setReportTemplateId(Long[] reportTemplateId) {
			this.reportTemplateId = reportTemplateId;
		}

		@Override
		public long getTotal() {
			return this.totalRecord;
		}
		@Override
		public Collection<? extends Object> getRows() {
			return this.reports;
		}
		
}
