package com.mainsteam.stm.portal.inspect.bo;

import java.util.Date;

/**
 * <li>文件名称: InspectReportFileBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2017年1月10日 下午4:41:54
 * @author   dfw
 */
public class InspectReportFileBo {

	/** 巡检报告ID*/
	private long inspectReportId;
	
	/** 文件ID*/
	private long fileId;
	
	/** 文件生成时间*/
	private Date repGenerateTime;

	public long getInspectReportId() {
		return inspectReportId;
	}

	public void setInspectReportId(long inspectReportId) {
		this.inspectReportId = inspectReportId;
	}

	public long getFileId() {
		return fileId;
	}

	public void setFileId(long fileId) {
		this.fileId = fileId;
	}

	public Date getRepGenerateTime() {
		return repGenerateTime;
	}

	public void setRepGenerateTime(Date repGenerateTime) {
		this.repGenerateTime = repGenerateTime;
	}
	
}
