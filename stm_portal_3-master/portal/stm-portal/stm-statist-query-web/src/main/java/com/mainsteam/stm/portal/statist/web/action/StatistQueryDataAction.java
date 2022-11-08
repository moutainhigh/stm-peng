package com.mainsteam.stm.portal.statist.web.action;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.URLEncoder;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.statist.api.IStatistQueryDataApi;
import com.mainsteam.stm.portal.statist.api.IStatistQueryDetailApi;
import com.mainsteam.stm.portal.statist.bo.StatistQueryMainBo;
@Controller
@RequestMapping(value="/portal/statistQuery/data")
public class StatistQueryDataAction extends BaseAction {
	Logger log = Logger.getLogger(StatistQueryDataAction.class);
	@Resource
	private IStatistQueryDataApi statistQueryData;
	
	@Resource
	private IStatistQueryDetailApi statistQueryDetail;
	
	@RequestMapping("/getStatQDataByStatQMainId")
	private JSONObject getStatQDataByStatQMainId(Long id, String startTime, String endTime){
		StatistQueryMainBo sqmBo = statistQueryDetail.getSQMainByStatQId(id);
		return toSuccess(statistQueryData.getStatQDataByStatQMainId(sqmBo, startTime, endTime));
	}
	
	@RequestMapping("/downloadStatQChart")
	private String downloadStatQChart(Long id, String startTime, String endTime, String type, HttpServletResponse response) throws Exception{
		StatistQueryMainBo sqmBo = statistQueryDetail.getSQMainByStatQId(id);
		File file = statistQueryData.fillStatQChart(sqmBo, startTime, endTime, type);
		writeFileToClient(file, sqmBo.getName(), response);
		return null;
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
			downloadName = downloadName+"."+file.getName().split("\\.")[1];
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
}
