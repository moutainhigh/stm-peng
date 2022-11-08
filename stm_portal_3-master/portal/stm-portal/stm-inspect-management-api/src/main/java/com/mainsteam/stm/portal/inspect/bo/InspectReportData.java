package com.mainsteam.stm.portal.inspect.bo;

import java.util.List;

import com.mainsteam.stm.portal.inspect.bo.InspectReportBasicBo;
import com.mainsteam.stm.portal.inspect.bo.InspectReportContentBo;
import com.mainsteam.stm.portal.inspect.bo.InspectReportResultsSettingBo;
import com.mainsteam.stm.portal.inspect.bo.InspectReportSelfItemBo;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

/**
 * <li>文件名称: InspectReportData.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2017年1月10日 下午4:41:48
 * @author   dfw
 */
public class InspectReportData {

	
	private InspectReportBasicBo data;
	
	@XStreamImplicit(itemFieldName="reportSelfs")
	private List<InspectReportSelfItemBo> reportSelfs;
	
	@XStreamImplicit(itemFieldName="reportResults")
	private List<InspectReportResultsSettingBo> reportResults;
	
	@XStreamImplicit(itemFieldName="reportContents")
	private List<InspectReportContentBo> reportContents;

	public InspectReportBasicBo getData() {
		return data;
	}

	public void setData(InspectReportBasicBo data) {
		this.data = data;
	}

	public List<InspectReportSelfItemBo> getReportSelfs() {
		return reportSelfs;
	}

	public void setReportSelfs(List<InspectReportSelfItemBo> reportSelfs) {
		this.reportSelfs = reportSelfs;
	}

	public List<InspectReportResultsSettingBo> getReportResults() {
		return reportResults;
	}

	public void setReportResults(List<InspectReportResultsSettingBo> reportResults) {
		this.reportResults = reportResults;
	}

	public List<InspectReportContentBo> getReportContents() {
		return reportContents;
	}

	public void setReportContents(List<InspectReportContentBo> reportContents) {
		this.reportContents = reportContents;
	}
	
}
