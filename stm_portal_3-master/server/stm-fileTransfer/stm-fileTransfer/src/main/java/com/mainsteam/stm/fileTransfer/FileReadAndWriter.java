package com.mainsteam.stm.fileTransfer;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

public class FileReadAndWriter {
	
	static final Map<String,FileReadAndWriter> fileMap=new HashMap<>();
	
	private RandomAccessFile raf;
	private String fileName;
	
	FileReadAndWriter(String file) throws FileNotFoundException{
		raf=new RandomAccessFile(file,"rw");
		this.fileName=file;
	}
	
	public synchronized void write(long point,byte[] data) throws IOException{
		raf.getChannel().position(point);
//		raf.getChannel().w
//		raf.seek(point);
		raf.write(data);
	}
	
	 public synchronized void close() throws IOException{
		 raf.close(); 
		 fileMap.remove(fileName);
	 }
	
	
	public static FileReadAndWriter create(String fileName) throws FileNotFoundException{
		
		FileReadAndWriter raf=fileMap.get(fileName);
		if(raf==null){
			raf=new FileReadAndWriter(fileName);
			fileMap.put(fileName, raf);
		}
		
		return raf;
	}

	public synchronized void setLength(long size) throws IOException {
		raf.setLength(size);
	}

}
