package com.mainsteam.stm.knowledge.scriptmanage.web.action;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.file.service.IFileClientApi;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.system.scriptcode.api.IScriptCodeApi;
import com.mainsteam.stm.system.scriptcode.bo.ScriptCode;
import com.mainsteam.stm.system.scriptmanage.api.IScriptManageApi;
import com.mainsteam.stm.system.scriptmanage.bo.ScriptManage;
import com.mainsteam.stm.system.scriptmanage.bo.ScriptManageTypeEnum;
import com.mainsteam.stm.system.um.user.api.IUserApi;

/**
 * <li>文件名称: AccessControlAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2019年5月20日
 * @author   tongpl
 */

@Controller
@RequestMapping("/knowledge/scriptmanage")
public class ScriptManageAction extends BaseAction {
	private Logger log = Logger.getLogger(ScriptManageAction.class);
	
	@Resource
	private IScriptManageApi scriptManageService;
	
	@Resource
	private IScriptCodeApi scriptCodeService;
	
	@Resource
	private IFileClientApi fileClient;
	
	@Resource
	private IUserApi stm_system_userApi;
	
	@RequestMapping("delScript")
	public JSONObject delScript(Long[] scriptId,Long[] fileId){
		return toSuccess(scriptManageService.delScriptManage(scriptId,fileId));
	}
	
	
	@RequestMapping("loadAllScriptCode")
	public JSONObject loadAllScriptCode(){
		
		List<ScriptCode> scList = scriptCodeService.loadAllScriptCode();
		
//		for(int i =0 ; i<10;i++){
//			ScriptCode sc = new ScriptCode();
//			sc.setScriptCode("code"+i);
//			sc.setCodeDiscription("脚本码"+i);
//			
//			scriptCodeService.saveScriptCode(sc);
//		}
//		ScriptCode sc1 = scriptCodeService.loadByScriptCode("code1");
//		scList.size();
//		sc1.getCodeDiscription();
		return toSuccess(scList);
	}
	
	@RequestMapping("loadByscriptId")
	public JSONObject loadByscriptId(Long scriptId){
		
		ScriptManage sm = scriptManageService.loadByscriptId(scriptId);
		ScriptManageVo smVo = new ScriptManageVo();
		BufferedReader bf = null;
		InputStreamReader isr = null;
		
		try {
			File file = fileClient.getFileByID(sm.getFileId());
			
            String readStr = null;
            isr = new InputStreamReader(new FileInputStream(file), "utf-8");
            bf = new BufferedReader(isr);
            StringBuffer sb = new StringBuffer();
            while ((readStr = bf.readLine()) != null) {
            	sb.append(readStr);
            	sb.append("\n");
            }
            smVo.setFileContent(sb.toString());
            
		} catch (Exception e) {
			log.error("ScriptManageAction file read error");
			log.error(e.getMessage());
			
		} finally {
            if (isr != null) {
                try {
                	isr.close();
                } catch (IOException e1) {
                	log.error("ScriptManageAction file read stream close error2", e1);
                }
            }
            if (bf != null) {
                try {
                    bf.close();
                } catch (IOException e1) {
                	log.error("ScriptManageAction file read stream close error1", e1);
                }
            }
        }
		
		BeanUtils.copyProperties(sm, smVo);
		if(null!=sm.getUserId()){
			smVo.setUserName(stm_system_userApi.get(sm.getUserId()).getName());
		}
		
		return toSuccess(smVo);
	}
	
	@RequestMapping("updateByscriptId")
	public JSONObject updateByscriptId(Long scriptId,String discription){
		ScriptManage sm = new ScriptManage();
		sm.setScriptId(scriptId);
		sm.setDiscription(discription);
		return toSuccess(scriptManageService.update(sm));
	}
	
	@RequestMapping("loadByType")
	public JSONObject loadByType(ScriptManagePageVo vo,int scriptTypeCode){
		Page<ScriptManage,ScriptManage> page = new Page<ScriptManage,ScriptManage>();
		page.setStartRow(vo.getStartRow());
		page.setRowCount(vo.getRowCount());
		ScriptManage sm = new ScriptManage();
		sm.setScriptManageType(ScriptManageTypeEnum.getByScriptCode(scriptTypeCode));
		page.setCondition(sm);
		
		
		scriptManageService.loadAllByPage(page);
		List<ScriptManage> smList = page.getDatas();
		List<ScriptManageVo> smvList = new ArrayList<ScriptManageVo>();
		Set<Long> smSet = new HashSet<Long>();
		Map<Long,String> userNameMap = new HashMap<Long,String>();
		for(ScriptManage smanage:smList){
			if(smSet.contains(smanage.getUserId())){
				ScriptManageVo smVo = new ScriptManageVo();
				BeanUtils.copyProperties(smanage, smVo);
				smVo.setUserName(userNameMap.get(smanage.getUserId()));
				smvList.add(smVo);
			}else{
				smSet.add(smanage.getUserId());
				
				ScriptManageVo smVo = new ScriptManageVo();
				BeanUtils.copyProperties(smanage, smVo);
				String username = stm_system_userApi.get(smanage.getUserId()).getName();
				smVo.setUserName(username);
				userNameMap.put(smanage.getUserId(), username);
				smvList.add(smVo);
			}
			
		}
		vo.setTotalRecord(page.getTotalRecord());
		vo.setScripts(smvList);
		
		return toSuccess(vo);
	}
	
	@RequestMapping(value="/scriptFileUpload", headers="content-type=multipart/*", method=RequestMethod.POST)
	@ResponseBody
	public boolean scriptFileUpload(@RequestParam("file") MultipartFile file,@RequestParam("scriptType")int scriptType,@RequestParam("discripStr")String discripStr ,HttpSession session) {
		ILoginUser user = getLoginUser(session);
		return scriptManageService.saveScriptManage(file,scriptType,discripStr,user);
	}
	
	@RequestMapping("/selectByfileNameAndScriptType")
	public JSONObject selectByfileNameAndScriptType(String fileName,int scriptType){
		
		List<ScriptManage>  smList = scriptManageService.loadBydocNameAndtype(fileName,scriptType);
		
		return toSuccess(smList);
	}
	
	@RequestMapping("/getUploadScriptFileType")
	public JSONObject getUploadScriptFileType(){
		return toSuccess(scriptManageService.getUploadScriptFileType());
	}
	
	
//	@RequestMapping("/scriptFileDownload")
//	public String getFileDownload(HttpServletResponse response,long fileId){
//		File file;
//		try {
//			file = fileClient.getFileByID(fileId);
//			writeFileToClient(file,file.getName(),response);
//		} catch (Exception e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return null;
//	}
	
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
	
}
