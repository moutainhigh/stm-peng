package com.mainsteam.stm.system.image.web.action;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.util.HashMap;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.file.bean.FileGroupEnum;
import com.mainsteam.stm.platform.file.service.IFileClientApi;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.system.image.api.IImageApi;
import com.mainsteam.stm.system.image.bo.ImageBo;

@Controller
@RequestMapping("system/image")
public class ImageAction extends BaseAction {
	private Logger logger = Logger.getLogger(getClass());
	@Resource
	private IFileClientApi fileClient;

	@Resource(name="systemImageApi")
	private IImageApi imageApi;
	
	@Value("${stm.VerificationCode}")
	private String verificationCode;

	@RequestMapping("getImageConfig")
	public JSONObject getImageConfig() {
		ImageBo image = imageApi.get();
		return toSuccess(image);
	}

	@RequestMapping("updateImageConfig")
	public @ResponseBody JSONObject updateImageConfig(ImageBo image,MultipartFile systemLogo,MultipartFile loginLogo) {
		try {
			boolean bo_systemLogo = true, bo_loginLogo = true;
//			ImageBo imageBo = imageApi.get();//获取系统图片配置
			if (image != null && image.getCurrentCopyRight() != null) {
				String currentCopyRight = image.getCurrentCopyRight().trim();
				if(!"".equals(currentCopyRight)){
					currentCopyRight = currentCopyRight.replaceAll("<", "&lt;").replaceAll(">", "&gt;").replaceAll("©", "&copy;");
				}
				image.setCurrentCopyRight(currentCopyRight);
			}
			if (systemLogo != null && systemLogo.getSize() > 0) {
				long systemLogoId = fileClient.upLoadFile(FileGroupEnum.STM_SYSTEM, systemLogo);
				image.setSystemCurrentLogo(String.valueOf(systemLogoId));
				bo_systemLogo = fileClient.isExist(systemLogoId);
			}
			if (loginLogo != null && loginLogo.getSize() > 0) {
				long loginLogoId = fileClient.upLoadFile(FileGroupEnum.STM_SYSTEM.toString(), loginLogo);
				image.setLoginCurrentLogo(String.valueOf(loginLogoId));
				bo_loginLogo = fileClient.isExist(loginLogoId);
			}
			boolean result = true;
			if(bo_systemLogo && bo_loginLogo){
				result = imageApi.update(image);
			}else{
				result = false;
			}
			return toSuccess(result);
		} catch (Exception e) {
			logger.error("updateImageConfig", e);
		}
		return toSuccess(false);
	}
	
	@RequestMapping(value="/getLoginLofiInputStream")
	public String getLoginLofiInputStream(HttpServletRequest request, HttpServletResponse response) throws Exception{

		ImageBo image = imageApi.get();
		FileInputStream fileIn=fileClient.getFileInputStreamByID(Long.valueOf(image.getLoginCurrentLogo()));

        BufferedInputStream bis = null; 
        BufferedOutputStream bos = null;
        
        try{
	        bis = new BufferedInputStream(fileIn);  
	        bos = new BufferedOutputStream(response.getOutputStream());  
	        byte[] buff = new byte[2048];  
	        int bytesRead;  
	        while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {  
	            bos.write(buff, 0, bytesRead);  
	        } 
	        bos.flush();
        }finally{
        	if(bis!=null){
        		bis.close();
        	}
        	if(bos!=null){
        		bos.close();
        	}
        	if(fileIn!=null){
        		fileIn.close();
        	}
        }

		return null;
	}

	
	@RequestMapping("getDownloadPadName")
	public JSONObject downloadLogoPsd() {
		String psdName = imageApi.getLogoPsd();
		return toSuccess(psdName);
	}

	@RequestMapping("getSystemLogo")
	public JSONObject getSystemLogo(){
		String logo = imageApi.getSystemLogo();
		return toSuccess(logo);
	}
	
	@RequestMapping("getLoginLogo")
	public JSONObject getLoginLogo(){
		Map<String, String> logoMap = new HashMap<String, String>();
		logoMap.put("logo", imageApi.getLoginLogo());
		logoMap.put("copyright", imageApi.getCopyright());
		logoMap.put("verificationCode", verificationCode);
		
		return toSuccess(logoMap);
	}
	
}
