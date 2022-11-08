/**
 * 
 */
package com.mainsteam.stm.transfer.local.sender;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.mainsteam.stm.transfer.DataSender;
import com.mainsteam.stm.transfer.obj.InnerTransferData;
import com.mainsteam.stm.transfer.obj.TransferDataType;
import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.io.xml.StaxDriver;

/** 
 * @author 作者：ziw
 * @date 创建时间：2017年6月14日 下午12:59:56
 * @version 1.0
 */
/** 
 */
public class LocalDataSenderAdapter implements DataSender {

	private LocalDataSender localDataSender;
	private XStream xs = new XStream(new StaxDriver());
	private boolean isValid = false;
	/**
	 * 
	 */
	public LocalDataSenderAdapter() {
	}

	@Override
	public void init() throws IOException {
		localDataSender = new LocalDataSender();
		isValid = true;
	}

	@Override
	public boolean isValid() throws IOException {
		return isValid;
	}

	@Override
	public int sendData(List<InnerTransferData> datas) throws IOException {
		if (datas != null && datas.size() > 0) {
			String content = xs.toXML(datas);
			localDataSender.sendData(content);
		}
		return 0;
	}

	@Override
	public void close() throws IOException {
	}

	public static void main(String[] args) throws IOException {
		InnerTransferData d = new InnerTransferData();
		d.setCacheTime(System.currentTimeMillis());
		d.setDataType(TransferDataType.DCSHeartbeatData.ordinal());
		d.setData("hello");
		LocalDataSenderAdapter sender = new LocalDataSenderAdapter();
		sender.init();
		List<InnerTransferData> l = new ArrayList<>();
		l.add(d);
		try {
			sender.sendData(l);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
