package com.mainsteam.stm.ct.web.action;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.mainsteam.stm.ct.api.IResourceinfoService;
import com.mainsteam.stm.ct.bo.MsCtResourceinfo;
import com.mainsteam.stm.ct.bo.Result;
import com.mainsteam.stm.platform.web.action.BaseAction;
@Controller
@RequestMapping({ "/portal/ct/resourceinfo" })
public class ResourceinfoAction extends BaseAction {
	Logger log=Logger.getLogger(ResourceinfoAction.class);
	@Resource
	private IResourceinfoService resourceinfoService;
	
	@RequestMapping("/selectById")
	@ResponseBody
	public Result<List<MsCtResourceinfo>> selectById(String id){
		Result<List<MsCtResourceinfo>> result=new Result<List<MsCtResourceinfo>>();
		try {
			List<MsCtResourceinfo> list = resourceinfoService.selectById(id);
			result.success("查询成功");
			result.setResult(list);
		} catch (Exception e) {
			// TODO: handle exception
			log.error(e.getMessage());
			result.error500("查询出错");
		}
		return result;
	}
}
