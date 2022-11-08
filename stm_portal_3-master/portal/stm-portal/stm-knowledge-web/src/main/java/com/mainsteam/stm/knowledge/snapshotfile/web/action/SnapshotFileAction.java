package com.mainsteam.stm.knowledge.snapshotfile.web.action;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.file.bean.FileModel;
import com.mainsteam.stm.platform.file.bean.FileModelQuery;
import com.mainsteam.stm.platform.file.service.IFileClientApi;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.system.um.user.api.IUserApi;

/**
 * <li>文件名称: SnapshotFileAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2019年7月20日
 * @author   tongpl
 */

@Controller
@RequestMapping("/knowledge/snapshotFile")
public class SnapshotFileAction extends BaseAction {
	private Logger log = Logger.getLogger(SnapshotFileAction.class);
	private static String groupName = "STM_ALARM";
	
	@Resource
	private IFileClientApi fileClient;
	
	@Resource
	private IUserApi stm_system_userApi;
	
	
	
	@RequestMapping("/getAllsnapshotFileByPage")
	public JSONObject getAllsnapshotFileByPage(SnapshotFilePageVo vo){
		Page<FileModel, FileModelQuery> page = new Page<FileModel, FileModelQuery>();
		
		FileModelQuery condition = new FileModelQuery();
		condition.setFileGroup(groupName);
		
		if(null!=vo.getCondition() && null!=vo.getCondition().getIpAdress() && !"".equals(vo.getCondition().getIpAdress())){
			condition.setFileName(vo.getCondition().getIpAdress());
		}
		
		page.setCondition(condition);
		page.setRowCount(vo.getRowCount());
		page.setStartRow(vo.getStartRow());
		
		try {
			List<FileModel> listFM = fileClient.getFileModels(page);
			
			List<SnapshotFileVo> listSF = new ArrayList<SnapshotFileVo>();
			
			for(FileModel fm:listFM){
				SnapshotFileVo sf = new SnapshotFileVo();
				sf.setFileName(fm.getFileName());
				sf.setFileSize(fm.getFileSize());
				sf.setId(fm.getId());
				sf.setCreateDatetime(fm.getCreateDatetime());
				sf.setFileUnit("B");
				
				listSF.add(sf);
			}
//			for(int i=1;i<10;i++){
//				SnapshotFileVo sf = new SnapshotFileVo();
//				sf.setFileName("test"+i);
//				sf.setFileSize(i*3141451);
//				sf.setId(i*414);
//				sf.setCreateDatetime(new Date());
//				sf.setFileUnit("B");
//				
//				listSF.add(sf);
//			}
			
			vo.setTotalRecord(page.getTotalRecord());
			vo.setSnapshotFiles(listSF);
		} catch (Exception e) {
			log.error("query snapshotfile error!");
			log.error(e.getMessage());
			e.printStackTrace();
		}
		
		return toSuccess(vo);
	}
	
	@RequestMapping("/getSnapshotFileById")
	public JSONObject getSnapshotFileById(long id){
		SnapshotFileVo sf = new SnapshotFileVo();
		BufferedReader bf = null;
		InputStreamReader isr = null;
		
		try {
			FileModel fm = fileClient.getFileModelByID(id);
			sf.setFileName(fm.getFileName());
			sf.setFileSize(fm.getFileSize());
			sf.setId(fm.getId());
			sf.setCreateDatetime(fm.getCreateDatetime());
			sf.setFileUnit("B");
		} catch (Exception e) {
			log.error("query getSnapshotFileById error!");
			log.error(e.getMessage());
			return toSuccess(null);
		}
		
		//文件内容
		try {
			File file = fileClient.getFileByID(id);
			
            String readStr = null;
            isr = new InputStreamReader(new FileInputStream(file), "utf-8");
            bf = new BufferedReader(isr);
            StringBuffer sb = new StringBuffer();
            while ((readStr = bf.readLine()) != null) {
            	sb.append(readStr);
            	sb.append("\n");
            }
            sf.setFileContent(sb.toString());
            
		} catch (Exception e1) {
			log.error("ScriptManageAction file read error");
			log.error(e1.getMessage());
		} finally {
			if(isr != null){
				try {
					isr.close();
				} catch (IOException e) {
					log.error("ScriptManageAction file read stream close error2", e);
				}
			}
            if (bf != null) {
                try {
                    bf.close();
                } catch (IOException e2) {
                	log.error("ScriptManageAction file read stream close error1", e2);
                }
            }
        }
		
		return toSuccess(sf);
	}
	
	@RequestMapping("/delSnapshotFileById")
	public JSONObject delSnapshotFileById(Long[] fileIds){
		
//		if(null==fileIds || fileIds.length==0){
//			return toSuccess(false);
//		}
		try {
			List<Long> fileIdList = (List<Long>)changeType(fileIds);
			
			fileClient.deleteFiles(fileIdList);
		} catch (Exception e) {
			log.error("delete snapshotfile error!");
			log.error(e.getMessage());
			return toSuccess(false);
		}
		
		return toSuccess(true);
	}
	
	@RequestMapping("/downloadSnapshotFileById")
	public void downloadSnapshotFileById(Long[] fileIds,HttpServletResponse response){
		
		List<File> fileList = new ArrayList<File>();
		try {
			for(Long fileId:fileIds){
				File file = fileClient.getFileByID(fileId);
				if(null!=file){
					fileList.add(file);
				}
			}
		} catch (Exception e) {
			log.error("get snapshotfile error!");
			log.error(e.getMessage());
		}
		
		if(fileList.size()==0){
			return ;
		}else if(fileList.size()==1){
			writeFileToClient(fileList.get(0),fileList.get(0).getName(),response);
		}else if(fileList.size()>1){
			FileOutputStream fous = null;
			ZipOutputStream zipOut = null;
			
			Date date = new Date();
			String fileName = date.getTime()+".rar";
			File fileAll = new File(fileName);
			
			try {
				fous = new FileOutputStream(fileAll);
				zipOut = new ZipOutputStream(fous);
		        for(int i = 0; i < fileList.size(); i++) {
		            File file = fileList.get(i);
		            zipFile(file, zipOut,file.getName());
		        }
		        
		        zipOut.flush();
		        zipOut.close();
		        fous.close();
			} catch (Exception e) {
				log.error("snapshotfile handle error!");
				log.error(e.getMessage());
			}
	        writeFileToClient(fileAll,"快照文件批量导出.rar",response);
		}
		
	}
	
	/**
	 * 把文件写到前台
	 * @param file
	 * @param response
	 * @param request
	 * @throws IOException
	 */
	private void writeFileToClient(File file,String downloadName, HttpServletResponse response) {
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		BufferedOutputStream bos = null;
		try {
			fis = new FileInputStream(file);
			String fileName = URLEncoder.encode(downloadName, "UTF-8");
			
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
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
			log.error(e);
			if (bis != null) {
				try {
					bis.close();
				} catch (Exception e1) {
					log.error(e1);
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (Exception e2) {
					log.error(e2);
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e3) {
					log.error(e3);
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
					log.error(e);
				}
			}
			if (bos != null) {
				try {
					bos.close();
				} catch (Exception e) {
					log.error(e);
				}
			}
			if (fis != null) {
				try {
					fis.close();
				} catch (Exception e) {
					log.error(e);
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
    public static void zipFile(File inputFile,
            ZipOutputStream ouputStream,String fileName) {
        try {
            if(inputFile.exists()) {
                if (inputFile.isFile()) {
                    FileInputStream IN = new FileInputStream(inputFile);
                    BufferedInputStream bins = new BufferedInputStream(IN);
                    //org.apache.tools.zip.ZipEntry
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
	
	private List<?> changeType(Object[] arrs){
		List<Object> list = new ArrayList<Object>();
		
		for(Object obj:arrs){
			list.add(obj);
		}
		
		return list;
	}
	
}
