package com.mainsteam.stm.platform.system.config.service.impl;

/**
 * <li>文件名称: LicenseImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2015-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年1月7日
 * @author   pengl
 */
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.event.ContextRefreshedEvent;

import com.mainsteam.stm.platform.system.config.service.ISystemInitializeConfigApi;

public class SystemInitializeConfigImpl implements ISystemInitializeConfigApi,ApplicationListener<ApplicationEvent>  {

	private static final Log logger = LogFactory.getLog(SystemInitializeConfigImpl.class);

	//标示数据库文件名
	private final String datFileName = "identity.dat";
	
	//标示数据库文件生成文件名
	private final String datFileGenerateFile = "ITMReader.exe";
	
	//license验证目录名
	private final String licenseValidateDirName = "temp";
	
	//密钥文件名
	private final String secretKeyFileName = "clientid.pub";
	
	//标示数据库文件路径
	@Value("${stm.license.path}")
	private String licensePath;
	
	public String getDatFileName() {
		return datFileName;
	}
	
	public String getDatFileGenerateFile(){
		return datFileGenerateFile;
	}

	public String getLicensePath() {
		return licensePath;
	}
	
	@Override
	public void onApplicationEvent(ApplicationEvent event) {
		// TODO Auto-generated method stub
		if(event instanceof ContextRefreshedEvent){
			//系统初始化或者刷新时检查标示数据库文件是否存在
			logger.info("Check license identity file is exsit ..........");
		//	checkLicenseIdentityFileIsExsit();
			
			//生成license验证文件的临时目录
			File tempDir = new File(licensePath + licenseValidateDirName);
			
			if(tempDir.exists() && tempDir.isDirectory()){
				//目录存在
				try {
					CopyFiel(new File(licensePath + File.separator + secretKeyFileName), new File(tempDir.getAbsolutePath() + File.separator + secretKeyFileName));
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
			}else{
				//目录不存在，创建temp目录
				boolean isSuccess = tempDir.mkdir();
				if(!isSuccess){
					//创建失败
					logger.error("Create license validate dir fail,path : " + tempDir.getAbsolutePath());
					return;
				}
				
				try {
					CopyFiel(new File(licensePath + File.separator + secretKeyFileName), new File(tempDir.getAbsolutePath() + File.separator + secretKeyFileName));
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
				
			}
		}
	}

	@Override
	public void checkLicenseIdentityFileIsExsit() {
		
		//判断操作系统类型
		if(System.getProperty("os.name").toLowerCase().indexOf("win") >= 0){
			
			//widows
			File file = new File(licensePath + datFileName);
			if(!file.exists()){
				//文件不存在
				logger.info("License identity file is not exsit ..........");
				
				//拿到exe执行文件
				File exeFile = new File(licensePath + datFileGenerateFile);
				
				if(exeFile.exists()){
					
					//执行文件存在
					Runtime runtime = Runtime.getRuntime();
					Process process = null;
					try {
						//获取盘符
						String partition = licensePath.substring(0,licensePath.indexOf(":") + 1);
						process = runtime.exec("cmd /c " + partition + " && cd " + licensePath + " && " + exeFile.getAbsolutePath());
			            
					} catch (Exception e) {
						logger.error(e.getMessage(),e);
					}
					
				}else{
					
					logger.info("License generate file is not exsit ..........");
					
				}
				
			}else{
				logger.info("License identity file is already exsit ..........");
			}
			
		}
		
	}

	@Override
	public void CopyFiel(File sourceFile, File targetFile) throws Exception {
        BufferedInputStream inBuff = null;
        BufferedOutputStream outBuff = null;
        try {
            // 新建文件输入流并对它进行缓冲
            inBuff = new BufferedInputStream(new FileInputStream(sourceFile));

            // 新建文件输出流并对它进行缓冲
            outBuff = new BufferedOutputStream(new FileOutputStream(targetFile));

            // 缓冲数组
            byte[] b = new byte[1024 * 5];
            int len;
            while ((len = inBuff.read(b)) != -1) {
                outBuff.write(b, 0, len);
            }
            // 刷新此缓冲的输出流
            outBuff.flush();
        } finally {
            // 关闭流
            if (inBuff != null)
                inBuff.close();
            if (outBuff != null)
                outBuff.close();
        }
    
	}

}
