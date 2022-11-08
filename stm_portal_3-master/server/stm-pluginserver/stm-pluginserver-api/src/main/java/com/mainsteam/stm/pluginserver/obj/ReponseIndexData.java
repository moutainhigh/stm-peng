package com.mainsteam.stm.pluginserver.obj;

import java.util.Arrays;

public class ReponseIndexData {
	
	private String[] data;
	
	private String indexPropertyName;
	
	private String indexPropertyValue;

	public ReponseIndexData() {
	}
	

	/**
	 * @param data
	 * @param indexPropertyName
	 * @param indexPropertyValue
	 */
	public ReponseIndexData(String[] data, String indexPropertyName,
			String indexPropertyValue) {
		this.data = data;
		this.indexPropertyName = indexPropertyName;
		this.indexPropertyValue = indexPropertyValue;
	}



	/**
	 * @return the data
	 */
	public final String[] getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public final void setData(String[] data) {
		this.data = data;
	}

	/**
	 * @return the indexPropertyName
	 */
	public final String getIndexPropertyName() {
		return indexPropertyName;
	}

	/**
	 * @param indexPropertyName the indexPropertyName to set
	 */
	public final void setIndexPropertyName(String indexPropertyName) {
		this.indexPropertyName = indexPropertyName;
	}

	/**
	 * @return the indexPropertyValue
	 */
	public final String getIndexPropertyValue() {
		return indexPropertyValue;
	}

	/**
	 * @param indexPropertyValue the indexPropertyValue to set
	 */
	public final void setIndexPropertyValue(String indexPropertyValue) {
		this.indexPropertyValue = indexPropertyValue;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "ReponseIndexData [data=" + Arrays.toString(data)
				+ ", indexPropertyName=" + indexPropertyName
				+ ", indexPropertyValue=" + indexPropertyValue + "]";
	}
}
