package com.mainsteam.stm.portal.report.bo;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

@XStreamAlias("columns")
@XStreamConverter(value = ToAttributedValueConverter.class, strings = { "text" })
public class Columns {

	// 是否拆开列(即为分为三列平均值,最大值,最小值)
	// 创建报表时为数字表示几列，生成数据时为分成的几列例如：（平均值,最大值,最小值）
	@XStreamAsAttribute
	private String apart;
	// 业务报表使用的业务ID
	@XStreamAsAttribute
	private String bizMetricId;

	private String text;

	public Columns() {

	}

	public Columns(String text) {
		this.text = text;
	}

	public Columns(String text, String apart) {
		this.text = text;
		this.apart = apart;

	}

	public String isApart() {
		return apart;
	}

	public void setApart(String isApart) {
		this.apart = isApart;
	}

	public String getApart() {
		return apart;
	}

	public String getBizMetricId() {
		return bizMetricId;
	}

	public void setBizMetricId(String bizMetricId) {
		this.bizMetricId = bizMetricId;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
