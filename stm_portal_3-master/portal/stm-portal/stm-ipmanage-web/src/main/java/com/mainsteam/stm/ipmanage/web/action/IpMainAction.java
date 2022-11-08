package com.mainsteam.stm.ipmanage.web.action;

import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.ipmanage.api.IpMainService;
import com.mainsteam.stm.ipmanage.bo.IpMain;
import com.mainsteam.stm.ipmanage.bo.TreeNode;
import com.mainsteam.stm.platform.web.action.BaseAction;

@Controller
@RequestMapping({ "/portal/ipmanage/ipMain" })
public class IpMainAction extends BaseAction{
	Logger logger=Logger.getLogger(IpMainAction.class);

	@Resource
	private IpMainService ipMainService;

	@RequestMapping("/autoDiscoverIPAndMAC")
	@ResponseBody
	public JSONObject autoDiscoverIPAndMAC(){
		logger.error("/autoDiscoverIPAndMAC");
		return toSuccess(ipMainService.IpAutoDiscover());
	}

	@RequestMapping("/getIpList")
	@ResponseBody
	public JSONObject getIpList(IpMain ipMain){
		try {
			return toSuccess(ipMainService.getIPList(ipMain));
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e);
			logger.error(e.getMessage());
			return toSuccess("error");
		}
		
	}
	@RequestMapping("/insertIp")
	@ResponseBody
	public JSONObject insertIp(IpMain ipMain){
		return toSuccess(ipMainService.insertIp(ipMain));
	}
	@RequestMapping("/updateIp")
	@ResponseBody
	public JSONObject updateIp(IpMain ipMain){
		return toSuccess(ipMainService.update(ipMain));
	}
	@RequestMapping("/deleteIp")
	@ResponseBody
	public JSONObject deleteIp(Integer id){
		return toSuccess(ipMainService.delete(id));
	}
	@RequestMapping("/addIpList")
	@ResponseBody
	public JSONObject addIpList(String string){
		List<IpMain> list = JSONObject.parseArray(string,IpMain.class);
		return toSuccess(ipMainService.addIpList(list));
	}
	@RequestMapping("/exportTemplateExcl")
	public void exportTemplateExcl(HttpServletResponse response, HttpServletRequest request){
		try {
			ipMainService.export(response,request);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e);
		}
	}
	@RequestMapping(value = "/inputExcel",method=RequestMethod.POST)
	@ResponseBody
	public JSONObject upload(@RequestParam("file") MultipartFile file)throws Exception {
		
		return toSuccess(ipMainService.inputFile(file.getInputStream()));
	}

}
