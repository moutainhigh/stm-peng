package com.mainsteam.stm.trap.server;

/**
 * @author cx
 *
 */
public interface TrapDataHandler{
	
	/** 需要开启的监听的UDP端口
	 * @return
	 */
	public int getPort();
	
	/**收到数据处理
	 * @param data
	 */
	public void handleData(String remoteIP,byte[] data);
	
	/**
	 * 判断是否认识该数据
	 * 
	 * @param data
	 * @return
	 * 
	 *modify by ziw at 2017年11月20日 上午9:56:45
	 *
	 *因为同一端口可以接收不同协议数据，使用该方法来区分数据是否用本handler处理来处理。
	 */
//	public boolean recognize(byte[] data);
}
