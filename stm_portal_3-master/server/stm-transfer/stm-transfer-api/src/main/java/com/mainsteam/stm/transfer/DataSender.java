package com.mainsteam.stm.transfer;

import java.io.IOException;
import java.util.List;

import com.mainsteam.stm.transfer.obj.InnerTransferData;

/**
 * 用来从DCS发送到DHS的实现的接口定义。
 * 
 * @author 作者：ziw
 * @date 创建时间：2017年6月14日 下午12:07:54
 * @version 1.0
 */
public interface DataSender {
	public void init() throws IOException;

	public boolean isValid() throws IOException;

	public int sendData(List<InnerTransferData> datas) throws IOException;

	public void close() throws IOException;
}
