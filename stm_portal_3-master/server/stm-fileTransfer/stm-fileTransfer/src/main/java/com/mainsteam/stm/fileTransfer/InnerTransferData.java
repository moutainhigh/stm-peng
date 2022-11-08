package com.mainsteam.stm.fileTransfer;

public class InnerTransferData {
	private byte[] data;
	private String fileName;
	private long point;
	private long size;
	private String charset;
	private boolean finish;
	public byte[] getData() {
		return data;
	}
	public void setData(byte[] data) {
		this.data = data;
	}
	public String getFileName() {
		return fileName;
	}
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	public String getCharset() {
		return charset;
	}
	public void setCharset(String charset) {
		this.charset = charset;
	}
	public boolean isFinish() {
		return finish;
	}
	public void setFinish(boolean finish) {
		this.finish = finish;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public long getPoint() {
		return point;
	}
	public void setPoint(long point) {
		this.point = point;
	}
}
