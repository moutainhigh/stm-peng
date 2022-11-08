/**
 * 
 */
package com.mainsteam.stm.resourcelog.data;

import java.io.UnsupportedEncodingException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.transfer.MetricDataTransferSender;
import com.mainsteam.stm.transfer.obj.TransferData;
import com.mainsteam.stm.transfer.obj.TransferDataType;
import com.mainsteam.stm.trap.server.TrapDataHandler;

/** 
 * @author 作者：ziw
 * @date 创建时间：2017年11月30日 上午11:30:37
 * @version 1.0
 */
/** 
 */
public class SyslogDataHandler implements TrapDataHandler {

	private static final Log log = LogFactory.getLog(SyslogDataHandler.class);

	private MetricDataTransferSender metricDataTransferSender;
	
	private int port;
	
	/**
	 * 
	 */
	public SyslogDataHandler() {
	}
	
	public void setMetricDataTransferSender(MetricDataTransferSender metricDataTransferSender) {
		this.metricDataTransferSender = metricDataTransferSender;
	}

	@Override
	public int getPort() {
		return this.port;
	}
	
	public void setPort(int port) {
		this.port = port;
	}

	@Override
	public void handleData(String ip, byte[] arg1) {
		try {
			StringBuffer sb = new StringBuffer(1024+ip.length());
			sb.append("receive syslog message from device ");
			sb.append(ip).append(' ');
			sb.append(new String(arg1, "UTF8"));
			if (log.isInfoEnabled())
				log.info(sb.toString());
		} catch (UnsupportedEncodingException e1) {
			log.error("handleData", e1);
		}
		TransferData data = new TransferData();
		data.setDataType(TransferDataType.SyslogTrap);
		SyslogTransferData bo =new SyslogTransferData();
		bo.setMsg(arg1);
		bo.setIp(ip);
		data.setData(bo);
		metricDataTransferSender.sendData(data);
	}
}
