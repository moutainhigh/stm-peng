package com.mainsteam.stm.deploy;

import java.io.IOException;

public interface ZipFileDeployServiceMBean {

	
	/**
	 * @param fileName
	 * @param charset
	 * @return
	 * @throws IOException
	 */
	public String unZipFile(String fileName,String charset) throws IOException;
	
}
