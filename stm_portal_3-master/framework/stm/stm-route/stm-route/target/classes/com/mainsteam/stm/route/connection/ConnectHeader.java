/**
 * 
 */
package com.mainsteam.stm.route.connection;

import java.io.IOException;

import com.mainsteam.stm.route.util.IpUtil;

/**
 * 连接用头信息
 * 
 * @author ziw
 * 
 */
public class ConnectHeader {

	public static final byte OPERATION_CREATE_CONNECTION = 1;

	public static final byte OPERATION_CREATE_CONNECTION_RESPONSE = 2;

	/**
	 * 版本
	 */
	private byte version;

	/**
	 * 操作:0:创建连接,1:传输数据
	 */
	private byte opertion;

	/**
	 * 连接使用的app
	 */
	private byte app;

	private int dataLength;
	
	/**
	 * 保留位
	 */
	private byte reserve;

	public static final int getHeadSize() {
		return 8;
	}

	/**
	 * 
	 */
	public ConnectHeader() {
	}

	/**
	 * @return the version
	 */
	public final byte getVersion() {
		return version;
	}

	/**
	 * @return the opertion
	 */
	public final byte getOpertion() {
		return opertion;
	}

	/**
	 * @return the app
	 */
	public final byte getApp() {
		return app;
	}

	/**
	 * @param version
	 *            the version to set
	 */
	public final void setVersion(byte version) {
		this.version = version;
	}

	/**
	 * @param opertion
	 *            the opertion to set
	 */
	public final void setOpertion(byte opertion) {
		this.opertion = opertion;
	}

	/**
	 * @param app
	 *            the app to set
	 */
	public final void setApp(byte app) {
		this.app = app;
	}

	/**
	 * @param reserve
	 *            the reserve to set
	 */
	public final void setReserve(byte reserve) {
		this.reserve = reserve;
	}

	/**
	 * @return the reserve
	 */
	public final byte getReserve() {
		return reserve;
	}
	

	public int getDataLength() {
		return dataLength;
	}

	public void setDataLength(int dataLength) {
		this.dataLength = dataLength;
	}

	public static ConnectHeader readHeader(byte[] headerBytes)
			throws IOException {
		ConnectHeader header = new ConnectHeader();
		header.version = headerBytes[0];
		header.opertion = headerBytes[1];
		header.app = headerBytes[2];
		header.reserve = headerBytes[3];
		header.dataLength = IpUtil.makeInt(headerBytes, 4);
		return header;
	}

	public byte[] toBytes() {
		byte[] headerBytes = new byte[8];
		headerBytes[0] = this.version;
		headerBytes[1] = this.opertion;
		headerBytes[2] = this.app;
		headerBytes[3] = this.reserve;
		byte[] b = IpUtil.toBytes(dataLength);
		System.arraycopy(b, 0, headerBytes, 4, b.length);
		return headerBytes;
	}
}
