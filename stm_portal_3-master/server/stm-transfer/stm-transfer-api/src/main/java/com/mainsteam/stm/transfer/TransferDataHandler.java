/**
 * 
 */
package com.mainsteam.stm.transfer;

import com.mainsteam.stm.transfer.obj.TransferDataType;
import com.mainsteam.stm.transfer.obj.TransferData;

/**
 * 处理上传的数据
 * 
 * @author ziw
 * 
 */
public interface TransferDataHandler {
	public TransferDataType getDataTransferType();
	public void handle(TransferData data);
}
