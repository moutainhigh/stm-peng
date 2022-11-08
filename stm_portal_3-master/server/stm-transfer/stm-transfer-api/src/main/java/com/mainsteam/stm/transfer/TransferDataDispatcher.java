package com.mainsteam.stm.transfer;

import com.mainsteam.stm.transfer.obj.InnerTransferData;

/** 
 * DHS分发数据
 * 
 * @author 作者：ziw
 * @date 创建时间：2017年6月14日 下午12:52:34
 * @version 1.0
 */
public interface TransferDataDispatcher {
	public void dispatch(InnerTransferData[] datas);
}
