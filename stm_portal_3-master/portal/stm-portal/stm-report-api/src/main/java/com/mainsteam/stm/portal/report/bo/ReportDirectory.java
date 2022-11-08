package com.mainsteam.stm.portal.report.bo;

import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamImplicit;

@XStreamAlias("reportDirectory")
public class ReportDirectory {
	 
	@XStreamAsAttribute
	private String name;
	@XStreamImplicit(itemFieldName="chapter")
	private List<Chapter> chapter;
	

	//type:1:性能报告,2:告警统计,3:TOPN报告,4:可用性报告,5:趋势报告,6:分析报告
	@XStreamAsAttribute
	private String type;

	
	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public List<Chapter> getChapter() {
		return chapter;
	}

	public void setChapter(List<Chapter> chapter) {
		this.chapter = chapter;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
}
