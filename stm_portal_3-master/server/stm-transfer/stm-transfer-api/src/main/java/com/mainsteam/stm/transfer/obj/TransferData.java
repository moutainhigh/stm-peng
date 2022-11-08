/**
 * 
 */
package com.mainsteam.stm.transfer.obj;


/**
 * 待传输的数据对象
 * 
 * @author ziw
 *
 */
public class TransferData {

	private TransferDataType dataType;
	
	private Object data;
	
	
	/**
	 * 
	 */
	public TransferData() {
		
	}
	public TransferData(TransferDataType type,Object data) {
		this.dataType=type;
		this.data=data;
	}

	/**
	 * @return the dataType
	 */
	public final TransferDataType getDataType() {
		return dataType;
	}


	/**
	 * @param dataType the dataType to set
	 */
	public final void setDataType(TransferDataType dataType) {
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
