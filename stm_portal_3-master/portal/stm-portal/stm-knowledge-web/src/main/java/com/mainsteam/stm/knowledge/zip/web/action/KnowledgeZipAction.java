package com.mainsteam.stm.knowledge.zip.web.action;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.knowledge.zip.api.IKnowledgeZipApi;
import com.mainsteam.stm.platform.web.action.BaseAction;

/**
 * <li>文件名称: com.mainsteam.stm.knowledge.zip.web.action.KnowledgeZipAction.java</li>
 * <li>文件描述: 本类描述</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年10月14日
 */
@Controller
@RequestMapping(value="/knowledgezip")
public class KnowledgeZipAction extends BaseAction{

	@Autowired
	private IKnowledgeZipApi zipApi;
	
	@Value("${jdbc.sqlite.url}")
	private String dbPath;
	
	private Logger logger = Logger.getLogger(getClass());
	
	@RequestMapping(value="/importCloudyZip", headers="content-type=multipart/*", method=RequestMethod.POST)
	public JSONObject importCloudyZip(@RequestParam("file") MultipartFile file, HttpSession session)throws Exception{
		return toSuccess(zipApi.importCloudyZip(file.getInputStream(), this.getLoginUser(session)));
	}
	
	@RequestMapping(value="/exprotZip")
	public void exprotZip(HttpServletResponse response)throws IOException{
		BufferedInputStream in = null;	//此处用这个目的是为了提高性能
		BufferedOutputStream os = null;
		// 统信 改 start
		InputStream result = zipApi.exprotLocalZip();	//此处目的是为了关闭打开的输入流
		int index = dbPath.lastIndexOf("\\");
		index = index==-1 ? dbPath.lastIndexOf("/") : index;
		String filename = dbPath.substring(index+1);
		filename = URLEncoder.encode(filename, "UTF-8");
		response.setHeader("Content-Disposition", "attachment; filename=" + filename);
		response.setContentType("application/octet-stream");
		response.setCharacterEncoding("UTF-8");
		try{
			in = new BufferedInputStream(result);
			os = new BufferedOutputStream(response.getOutputStream());
			int size = 1024;
			byte[] buf = new byte[size];
			while(in.read(buf)!=-1){
				os.write(buf);
			}
			os.flush();
		}catch(Exception e){
			logger.error("exprotZip:"+e.getMessage());
		}finally{
			if(result!=null){
				result.close();
			}
			if(in!=null){
				in.close();
			}
			if(os!=null){
				os.close();
			}
		}
		// 统信 改 end
	}
}
