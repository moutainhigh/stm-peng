package com.mainsteam.stm.portal.config.web.action;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.file.service.IFileClientApi;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.config.api.IConfigBackupLogApi;
import com.mainsteam.stm.portal.config.bo.ConfigBackupLogBo;
/**
 * <li>文件名称: ConfigBackupLogAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月28日
 * @author   caoyong
 */
@Controller
@RequestMapping("/portal/config/backup/log")
public class ConfigBackupLogAction extends BaseAction {
	private Logger logger = Logger.getLogger(ConfigBackupLogAction.class);
	@Autowired
	private IConfigBackupLogApi configBackupLogApi;
	
	@Resource
	private IFileClientApi fileClient;
	
	/**
	 * 备份历史分页数据
	 * @param page 分页对象
	 * @param resourceId 设备id
	 * @param all 是否查询全部的记录(true失败与成功的;false成功的)
	 * @param searchKey(搜索关键字,根据文件名称模糊查询)
	 * @return
	 */
	@RequestMapping(value="/getPage", method=RequestMethod.POST)
	public JSONObject getPage(Page<ConfigBackupLogBo, ConfigBackupLogBo> page,
			Long resourceId,boolean all,String searchKey){
		try {
			configBackupLogApi.selectByPage(page,resourceId,all,searchKey);
			logger.info("portal.config.backup.log.getPage successful");
		} catch (Exception e) {
			logger.error("portal.config.backup.log.getPage failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
		}
		return toSuccess(page);
	}
	
	@RequestMapping("/getFileDownload")
	public String getFileDownload(long[] fileIds,HttpServletResponse response){
		logger.info("下载文件，fileIds:" + JSON.toJSONString(fileIds));
		List<File> fileList = new ArrayList<>();
		for(long fileId : fileIds){
			File file = null;
			try {
				file = fileClient.getFileByID(fileId);
				fileList.add(file);
			} catch (Exception e) {
				logger.error("获取文件出错，fileId：" + fileId, e);
			}
		}
		FileOutputStream fous = null;
		ZipOutputStream zipOut = null;
		try {
			if(fileList.size()==1){
				String fileName = fileList.get(0).getName();
				writeFileToClient(fileList.get(0),fileName,response);
				return null;
			} else if(fileList.size()>1){
				Date date = new Date();
				String fileName = date.getTime()+".rar";
				File fileAll = new File(fileName);
				
				fous = new FileOutputStream(fileAll); 
				zipOut = new ZipOutputStream(fous);
			    for(int i = 0; i < fileList.size(); i++) {
			        File file = fileList.get(i);
			        zipFile(file, zipOut, file.getName());
			        
			    }
			    
			    zipOut.flush();
			    zipOut.close();
			    fous.close();
			    
			    writeFileToClient(fileAll, "配置文件-批量导出.rar", response);
				return null;
			} else {
				return "下载失败";
			}
		} catch(Exception e){
			e.printStackTrace();
			if(null!=fous){
				try {
					fous.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			if(null!=zipOut){
				try {
					zipOut.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			if(fileList.size()>1){
				for(File file:fileList){
					if(null!=file){
						file.delete();
					}
				}
			}
			return "下载失败!";
		} finally{
			if(null!=fous){
				try {
					fous.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			if(null!=zipOut){
				try {
					zipOut.close();
				} catch (Exception e2) {
					e2.printStackTrace();
				}
			}
			if(fileList.size()>1){
				for(File file:fileList){
					if(null!=file){
						file.delete();
					}
				}
			}
		}
	}
	
	/**
	 * 把文件写到前台
	 * @param file
	 * @param response
	 * @param request
	 * @throws IOException
	 */
	private void writeFileToClient(File file, String downloadName, HttpServletResponse response) {
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		
		try {
			fis = new FileInputStream(file);
//			downloadName = downloadName+"."+file.getName().split("\\.")[1];
			String fileName = URLEncoder.encode(downloadName, "UTF-8");
			response.setCharacterEncoding("UTF-8");
			
			//   filename*=utf-8'zh_cn'   兼容ie,chrome,firefox浏览器
			response.setHeader("Content-Disposition", "attachment; filename*=utf-8'zh_cn'" + fileName);
			response.setContentType("application/octet-stream");
			
			int contentLength = fis.available();
			response.setContentLength(contentLength);
			bis = new BufferedInputStream(fis);
			bos = new BufferedOutputStream(response.getOutputStream());
			byte[] buff = new byte[2048];
			int bytesRead;
			while (-1 != (bytesRead = bis.read(buff, 0, buff.length))) {
				bos.write(buff, 0, bytesRead);
			}
			bos.flush();
		} catch (Exception e) {
			logger.error(e);
			if (bis != null) {
				try {
					bis.close();
				} catch (Exception e1) {
					logger.error(e1);
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (Exception e2) {
					logger.error(e2);
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e3) {
					logger.error(e3);
				}
			}
			if(null!=file){
				file.delete();
			}
		} finally {
			if (bis != null) {
				try {
					bis.close();
				} catch (Exception e) {
					logger.error(e);
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (Exception e) {
					logger.error(e);
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {
					logger.error(e);
				}
			}
			if(null!=file){
				file.delete();
			}
		}
	}
	
	/**  
     * 根据输入的文件与输出流对文件进行打包
     */
    public static void zipFile(File inputFile, ZipOutputStream ouputStream,String fileName) {
        try {
            if(inputFile.exists()) {
                if (inputFile.isFile()) {
                    FileInputStream IN = new FileInputStream(inputFile);
                    BufferedInputStream bins = new BufferedInputStream(IN);
                    //org.apache.tools.zip.ZipEntry
//                    fileName = fileName+"."+inputFile.getName().split("\\.")[1];
                    ZipEntry entry = new ZipEntry(fileName);
                    ouputStream.putNextEntry(entry);
                    // 向压缩文件中输出数据   
                    int nNumber;
                    byte[] buffer = new byte[2048];
                    while ((nNumber = bins.read(buffer)) != -1) {
                        ouputStream.write(buffer, 0, nNumber);
                    }
//                    ouputStream.flush();
                    // 关闭创建的流对象   
                    bins.close();
                    IN.close();
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
