package com.mainsteam.stm.resourcelog.data.bigData;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.system.bigdata.api.IBigdataApi;
import com.mainsteam.stm.system.bigdata.bo.BigdataBo;
import com.mainsteam.stm.util.SpringBeanUtil;

/**用于大数据的UDP发送服务接口
 * @author heshengchao
 *
 */
public class UdpSenderForBigData {
	
	public static final int SYNC_DATA_METRIC=1;
	public static final int SYNC_DATA_ALARM=2;
	public static final int SYNC_DATA_SYSLOG=3;
	
	private static final Log logger=LogFactory.getLog(UdpSenderForBigData.class);
	private DatagramSocket datagramSocket=null;
	private BigdataBo serverConfigbo=null;
	
	private long lastOptTime=System.currentTimeMillis();
	
	private UdpSenderForBigData(){}
	private final static UdpSenderForBigData instance=new UdpSenderForBigData();
	public static UdpSenderForBigData getInstance(){
		return instance;
	}
	
	
	private BigdataBo getConfig(){
		long curTime=System.currentTimeMillis();
		if(curTime-lastOptTime>1000*60 || serverConfigbo==null){
			IBigdataApi bigDataApi=SpringBeanUtil.getBean(IBigdataApi.class);
			serverConfigbo= bigDataApi==null?null:bigDataApi.get();
		}
		
		return serverConfigbo;
		
	}
	
	public boolean allowSync(){
		BigdataBo bo=getConfig();
		return bo!=null?bo.isIntegrate():false;
	}
	
	
	public void sendMsg(byte[] msg,int type){
		if(!allowSync()){
			if(logger.isInfoEnabled())
				logger.info("not allow send data to bigDataServer!");
			return ;
		}
		
		InetAddress splunkInetAddress;
		try {
			splunkInetAddress = Inet4Address.getByName(serverConfigbo.getIp());
		} catch (UnknownHostException e) {
			if(logger.isErrorEnabled())
        		logger.error(e.getMessage(),e);
			return;
		}
		
//		out.write(1);//版本
//		out.write(type);//消息类型
//		byte[] cont=msg.getBytes("UTF-8");
//		out.write(intToByte(cont.length));//消息长度

		ByteBuffer bbf=ByteBuffer.allocate(msg.length+4+4+4);
		bbf.putInt(1);
		bbf.putInt(type);
		bbf.putInt(msg.length);
		bbf.put(msg);
		bbf.flip();
		
		
		byte[] sendData=bbf.array();
		
		
		DatagramPacket packet = new DatagramPacket(sendData,sendData.length,splunkInetAddress,serverConfigbo.getUdp());
		
		try {  
			if(datagramSocket==null || datagramSocket.isClosed()){
				synchronized (this) {
					if(datagramSocket==null || datagramSocket.isClosed()){
						datagramSocket=openSocket();
					}
				}
			}
			datagramSocket.send(packet); //发送数据  
        } catch (IOException e) {
        	if(logger.isErrorEnabled())
        		logger.error(e.getMessage(),e);
        	datagramSocket.close();
        }  
		
	}
	
	private DatagramSocket openSocket() throws SocketException{
		DatagramSocket datagramSocket = new DatagramSocket(20000);
		return datagramSocket;
	}
}
