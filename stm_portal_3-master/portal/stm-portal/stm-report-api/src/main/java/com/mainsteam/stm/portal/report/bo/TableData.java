package com.mainsteam.stm.portal.report.bo;

import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.converters.extended.ToAttributedValueConverter;

@XStreamAlias("tableData")
@XStreamConverter(value=ToAttributedValueConverter.class,strings={"value"})
public class TableData {
	
	//业务报表使用的业务ID
	@XStreamAsAttribute
	private String bizId;
	
	private String value;

	public String getBizId() {
		return bizId;
	}

	public void setBizId(String bizId) {
		this.bizId = bizId;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
	
}
