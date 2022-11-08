package com.mainsteam.stm.fileTransfer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;

public class WriteWithReadFileStream {
	 protected byte buf[];
	 protected int count;
	 
	 RandomAccessFile raf;
	
	public WriteWithReadFileStream(File fransferFile) throws FileNotFoundException {
		raf=new RandomAccessFile(fransferFile, "rw");
	}

	public long available() throws IOException {
		return raf.length();
	}
	
//	public void setPoint(long point) throws IOException{
//		raf.seek(point);
//	}

//	public void write(byte[] data) throws IOException {
//		raf.write(data);
//	}

	public void close() throws IOException {
		raf.close();
	}

	public void write(long from, byte[] data) throws IOException {
		raf.seek(from);
		raf.write(data);
	}
}
