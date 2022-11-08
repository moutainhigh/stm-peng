package com.mainsteam.stm.platform.web.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;

/**
 * <li>文件名称: WebUtil.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年12月24日
 * @author   ziwenwen
 */
public class WebUtil {
	public static final String getRemoteIp(HttpServletRequest request) {
        String ip = request.getHeader("x-forwarded-for");
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getHeader("WL-Proxy-Client-IP");
        }
        if (StringUtils.isBlank(ip) || "unknown".equalsIgnoreCase(ip)) {
            ip = request.getRemoteAddr();
        }
        return ip;
    }
	
	public static final void download(String fileName,InputStream in,
			HttpServletRequest request,HttpServletResponse response) throws IOException{
		
		initHttpServletResponse(fileName, response, request);
		
        BufferedInputStream bis = null;
        BufferedOutputStream bos = null;
        
        try{
	        bis = new BufferedInputStream(in);  
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
        	if(in!=null){
        		in.close();
        	}
        }
	}

	public static void initHttpServletResponse(String filename,
			HttpServletResponse response,HttpServletRequest request) throws UnsupportedEncodingException{
		String encoding="UTF-8";
		response.setContentType("application/download;");
		response.addHeader("Content-Type", "text/html; charset="+encoding);
		String agent = request.getHeader("USER-AGENT");
		if(null==agent){
			filename = URLEncoder.encode(filename,encoding); 
		}else if((agent=agent.toLowerCase()).indexOf("firefox")>-1){
			filename = new String(filename.getBytes(encoding),"iso-8859-1");
		}else{
			filename = URLEncoder.encode(filename,encoding);
			if(agent.indexOf("msie")>-1&&filename.length()>150)//解决IE 6.0 bug
				filename=new String(filename.getBytes(encoding),"ISO-8859-1");
		}
		response.setHeader( "Content-Disposition", "attachment;filename="+filename);
	}
	
}
