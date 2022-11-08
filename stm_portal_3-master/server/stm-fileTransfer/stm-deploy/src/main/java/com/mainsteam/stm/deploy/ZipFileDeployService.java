package com.mainsteam.stm.deploy;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipInputStream;

import com.mainsteam.stm.util.OSUtil;


public class ZipFileDeployService implements ZipFileDeployServiceMBean {

	@Override
	public String unZipFile(String fileName, String charset) throws IOException {
		String basePath=OSUtil.getEnv("transferPath", "../transfer");
		String filePath=basePath+File.separator+fileName;


		 ZipFile zipFile = new ZipFile(filePath);//实例化ZipFile，每一个zip压缩文件都可以表示为一个ZipFile
         //实例化一个Zip压缩文件的ZipInputStream对象，可以利用该类的getNextEntry()方法依次拿到每一个ZipEntry对象
         ZipInputStream zipInputStream = new ZipInputStream(new FileInputStream(filePath), Charset.forName(charset));
         ZipEntry zipEntry = null;
         while ((zipEntry = zipInputStream.getNextEntry()) != null) {
             String unZipFileItem = zipEntry.getName();
             File temp = new File("../" + unZipFileItem);
             if (! temp.getParentFile().exists())
                 temp.getParentFile().mkdirs();
             
             if(temp.isDirectory()){
            	 temp.mkdirs();
             }else{
            	 if(temp.exists()){
            		 temp.createNewFile();
            	 }
	             OutputStream os = new FileOutputStream(temp);
	             //通过ZipFile的getInputStream方法拿到具体的ZipEntry的输入流
	             InputStream is = zipFile.getInputStream(zipEntry);
	             int len = 0;
	             while ((len = is.read()) != -1)
	                 os.write(len);
	             os.close();
	             is.close();
             }
         }
         zipInputStream.close();
         zipFile.close();

		return "success";
	}
}
