package com.mainsteam.stm.platform.file.client;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;

import com.mainsteam.stm.platform.file.bean.FileModel;
import com.mainsteam.stm.util.FileUtil;

public class FileClient {
	
	private static FileClient fileClient;

	private static Logger logger=Logger.getLogger(FileClient.class);
	
	private FileClient() {
	}

	public static FileClient getInstance() {
		if (fileClient == null) {
			synchronized (FileClient.class) {
				if (fileClient == null) {
					fileClient = new FileClient();
				}
			}
		}
		return fileClient;
	}
	/**
	 * upload2FileServer
	 * @param fileModel
	 * @throws IOException
	 */
	public void uploadFile2Server(FileModel fileModel) throws IOException {
		//TempFile  临时文件
		String outFilePath = fileModel.getFilePath();
		File outFile = new File(outFilePath);
		
		logger.info("upload File to Server="+outFile.getAbsolutePath());
		
		if (!outFile.exists()) {
			FileUtil.createFile(outFile);
		}

		InputStream in = fileModel.getIn();

		byte[] inOutb = new byte[in.available()];
		FileOutputStream outStream = null;
		try {
			outStream = new FileOutputStream(outFile);
			inOutb = new byte[in.available()];
			in.read(inOutb);
			outStream.write(inOutb);
		} finally {
			if (in != null) {
				in.close();
			}
			if (outStream != null) {
				outStream.flush();
				outStream.close();
			}
		}

	}
	
	/**
	 * removeFile
	 * @param filePath
	 * @throws IOException
	 */
	public void removeFile(String filePath) throws IOException{
		File file=new File(filePath);
		if(file.exists()){
			FileUtil.removeFile(file);
		}
		
		if(file.exists()){
			file.delete();
		}
	}
	


}
