package com.mainsteam.stm.platform.file.web.action;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.file.bean.FileGroupEnum;
import com.mainsteam.stm.platform.file.bean.FileModel;
import com.mainsteam.stm.platform.file.service.IFileClientApi;
import com.mainsteam.stm.platform.file.service.impl.FileClientImpl;
import com.mainsteam.stm.util.IConstant;

/**
 * 
* <li>文件名称: FileUploadAction.java</li>
* <li>公　　司: 武汉美新翔盛科技有限公司</li>
* <li>版权所有: 版权所有(C)2019-2020</li>
* <li>修改记录: 公共的文件 Upload and Download</li>
* <li>内容摘要: ...</li>
* <li>其他说明: ...</li>
* @version  ms.stm
* @since    2019年8月14日
* @author   wangxinghao
* @tags
 */
@Controller
@RequestMapping("/platform/file")
public class FileUploadAction extends CommonsMultipartResolver  {
	
	@Resource
	private IFileClientApi fileClient;
	
	private static Logger logger=Logger.getLogger(FileUploadAction.class);
	
	/**
	 * 文件上传
	 * @param fileGroup
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/fileUpload", headers="content-type=multipart/*", method=RequestMethod.POST)
	public String fileUpload(@RequestParam("fileGroup") String fileGroup,@RequestParam("fileId") Long fileId,@RequestParam("file") MultipartFile file) throws Exception{
		if(fileGroup==null){
			fileGroup=FileGroupEnum.STM_PUBLIC.toString();
		}
		if(fileId!=null && fileId>0){
			fileClient.deleteFile(fileId);
			fileClient.upLoadFile(fileGroup, file,fileId);
		}else{
			fileId=fileClient.upLoadFile(fileGroup, file);
		}
		
		return  String.valueOf(fileId);
	}
	
	/**
	 * 多文件上传
	 * @param fileGroup
	 * @param files
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/uploadFiles", headers="content-type=multipart/*", method=RequestMethod.POST)
	public List<FileModel> fileUpload(@RequestParam("fileGroup") String fileGroup,@RequestParam("files") MultipartFile files[]) throws Exception{

		if(fileGroup==null){
			fileGroup=FileGroupEnum.STM_PUBLIC.toString();
		}
		
		List<FileModel> fmList=new ArrayList<FileModel>();
		for(MultipartFile file:files){
			long fileId=fileClient.upLoadFile(fileGroup, file);
			FileModel fileModel=fileClient.getFileModelByID(fileId);
			fmList.add(fileModel);
		}

		return fmList;
	}
	
	/**
	 * 更新文件
	 * @param fileId
	 * @param fileGroup
	 * @param file
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/fileUpdate", headers="content-type=multipart/*", method=RequestMethod.POST)
	public String fileUpdate(@RequestParam("fileId") long fileId,@RequestParam("fileGroup") String fileGroup,@RequestParam("file") MultipartFile file) throws Exception{

		if(fileGroup==null){
			fileGroup=FileGroupEnum.STM_PUBLIC.toString();
		}
		
		fileClient.updateFile(fileId, file);
		
		return  String.valueOf(fileId);
	}
	
	
	@RequestMapping(value="/getFileInputStream")
	public void getFileInputStream(HttpServletRequest request, HttpServletResponse response){
		String fileIdStr=request.getParameter("fileId");
		long fileID=0;
		if(fileIdStr!=null){
			fileID=Long.parseLong(fileIdStr);
		}

		FileInputStream fileIn = null;
        BufferedInputStream bis = null; 
        BufferedOutputStream bos = null;
        
        try{
    		fileIn=fileClient.getFileInputStreamByID(fileID);
    		
	        bis = new BufferedInputStream(fileIn);  
	        bos = new BufferedOutputStream(response.getOutputStream());  
	        byte[] buff = new byte[2048];  
	        int bytesRead;  
	        while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {  
	            bos.write(buff, 0, bytesRead);  
	        } 
	        bos.flush();
        }catch(Exception e){
        	logger.error("Write IO Exception:"+e.getMessage());
        }finally{
        	try {
        		if(bis!=null){
    				bis.close();
            	}
            	if(bos!=null){
            		bos.close();
            	}
            	if(fileIn!=null){
            		fileIn.close();
            	}
			} catch (Exception e) {
				logger.error("Close IO Exception:"+e.getMessage());
			}

        }

	}
	
	@RequestMapping(value="/downloadFile")
	public String downloadFile(HttpServletRequest request, HttpServletResponse response) throws Exception{
		String fileIdStr=request.getParameter("fileId");
		long fileID=0;
		if(fileIdStr!=null){
			fileID=Long.parseLong(fileIdStr);
		}
		
		FileModel fm=fileClient.getFileModelByID(fileID);
		
		String fileName = URLEncoder.encode(fm.getFileName(), "UTF-8");

        response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
		response.setContentType("application/octet-stream"); 	
		int contentLength = new Long(fm.getFileSize()).intValue();
		response.setContentLength(contentLength);
        
		FileInputStream fileIn=fileClient.getFileInputStreamByID(fileID);
		
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
	
	/**
	 * 通过文件 ID 删除文件
	 * @param fileId
	 * @return
	 */
	@RequestMapping(value="/deleteFile")
	public JSONObject deleteFile(long fileId){
		JSONObject jo = new JSONObject();
		jo.put(IConstant.str_code, 200);
		try {
			fileClient.deleteFile(fileId);
		} catch (Exception e) {
			jo.put(IConstant.str_data, false);
		}
		jo.put(IConstant.str_data, true);
		return jo;
	}
	
	/**
	 * 
	 * @param fileId
	 * @return
	 * @throws Exception
	 */
	@RequestMapping(value="/getFileModel")
	public FileModel getFileModelById(long fileId) throws Exception{
		return fileClient.getFileModelByID(fileId);
	}

	
}
