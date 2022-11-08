package com.mainsteam.stm.transfer;


import com.mainsteam.stm.transfer.obj.TransferData;

/**
 * DCS发送数据接口的接口入口。
 */
public interface MetricDataTransferSender {
	/**
	 * @param data
	 */
	public void sendData(TransferData data);
}
