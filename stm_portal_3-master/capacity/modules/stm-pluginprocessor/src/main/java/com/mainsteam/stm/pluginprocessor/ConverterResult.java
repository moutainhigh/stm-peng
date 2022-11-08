package com.mainsteam.stm.pluginprocessor;

public class ConverterResult implements java.io.Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5109069352284149412L;

	
	
	public ConverterResult(String resultIndex, String[] resultData) {
		super();
		this.resultIndex = resultIndex;
		this.resultData = resultData;
	}

	private String resultIndex = null;
	private String[] resultData;

	public String getResultIndex() {
		return resultIndex;
	}

	public void setResultIndex(String resultIndex) {
		this.resultIndex = resultIndex;
	}

	public String[] getResultData() {
		return resultData;
	}

	public void setResultData(String[] resultData) {
		this.resultData = resultData;
	}

}
