package com.mainsteam.stm.util;

import java.io.File;
import java.io.IOException;

import com.mainsteam.stm.network.tftp.TFTPServer;

public class TFTPServerTest {
	public static final String TFTP_DIRECTORY = "../tftpTemp";
	public static void main(String[] args) {
			//临时中转到文件服务器的目录
			File file = new File("tftpTemp");
			if(!file.exists()) file.mkdir();
		try {
			TFTPServer server = new TFTPServer(file, file, TFTPServer.ServerMode.GET_AND_PUT);
			server.run();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
}
