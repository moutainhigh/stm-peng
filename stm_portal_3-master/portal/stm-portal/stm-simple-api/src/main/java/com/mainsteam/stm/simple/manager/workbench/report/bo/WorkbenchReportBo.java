package com.mainsteam.stm.simple.manager.workbench.report.bo;

import java.io.Serializable;
import java.util.Date;

/**
 * <li>文件名称: ReportBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 报表数据</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月17日
 * @author   ziwenwen
 */
public class WorkbenchReportBo implements Serializable{
	private static final long serialVersionUID = 4272157162189398443L;

	/**
	 * 报表ID
	 */
	private Long id;
	
	/**
	 * 报表文件ID
	 */
	private String reportFileId;
	/**
	 * 报表标题
	 */
	private String title;
	/**
	 * 报表创建日期
	 */
	private String date;
	/**
	 * 创建人姓名
	 */
	private String creater;
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}


	public String getReportFileId() {
		return reportFileId;
	}

	public void setReportFileId(String reportFileId) {
		this.reportFileId = reportFileId;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public String getCreater() {
		return creater;
	}

	public void setCreater(String creater) {
		this.creater = creater;
	}
}


