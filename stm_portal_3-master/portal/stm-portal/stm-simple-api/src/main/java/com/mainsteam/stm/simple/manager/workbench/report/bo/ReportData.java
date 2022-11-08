package com.mainsteam.stm.simple.manager.workbench.report.bo;

import java.io.Serializable;
import java.util.List;

/**
 * <li>文件名称: ReportData</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 报表数据</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月20日 下午3:59:35
 * @author   俊峰
 */
public class ReportData implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -3383092735713322380L;
	/**
	 * 报表ID
	 */
	private Long reportId;
	/**
	 * 报表标题
	 */
	private String reportTitle;
	/**
	 * 报表章节
	 */
	private List<Directorie> directories;
	public Long getReportId() {
		return reportId;
	}
	public void setReportId(Long reportId) {
		this.reportId = reportId;
	}
	public String getReportTitle() {
		return reportTitle;
	}
	public void setReportTitle(String reportTitle) {
		this.reportTitle = reportTitle;
	}
	public List<Directorie> getDirectories() {
		return directories;
	}
	public void setDirectories(List<Directorie> directories) {
		this.directories = directories;
	}
	
	
}
