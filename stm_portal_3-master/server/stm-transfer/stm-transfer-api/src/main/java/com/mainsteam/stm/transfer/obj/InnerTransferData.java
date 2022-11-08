package com.mainsteam.stm.transfer.obj;



public class InnerTransferData {

	private int dataType;

	private Object data;
	
	private long cacheTime;

	public InnerTransferData() {
	}

	public long getCacheTime() {
		return cacheTime;
	}



	public void setCacheTime(long cacheTime) {
		this.cacheTime = cacheTime;
	}



	/**
	 * @return the dataType
	 */
	public final int getDataType() {
		return dataType;
	}

	/**
	 * @param dataType the dataType to set
	 */
	public final void setDataType(int dataType) {
		this.dataType = dataType;
	}

	/**
	 * @return the data
	 */
	public final Object getData() {
		return data;
	}

	/**
	 * @param data the data to set
	 */
	public final void setData(Object data) {
		this.data = data;
	}
}
