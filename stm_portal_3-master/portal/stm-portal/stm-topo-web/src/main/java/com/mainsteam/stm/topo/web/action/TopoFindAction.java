package com.mainsteam.stm.topo.web.action;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.topo.api.ITopoAuthSettingApi;
import com.mainsteam.stm.topo.api.TopoFindService;
/**
 * 拓扑发现控制器
 * @author 富强
 *
 */
@Controller
@RequestMapping(value="topo/find")
public class TopoFindAction extends BaseAction{
	Logger logger = Logger.getLogger(TopoFindAction.class);
	@Autowired
	private TopoFindService tfsvc;
	//权限控制
	@Autowired
	private ITopoAuthSettingApi authSvc;
	/**
	 * 拓扑发现
	 * @return 启动采集器的状态标记
	 */
	@RequestMapping(value="type/{type}",method=RequestMethod.POST)
	public JSONObject find(@PathVariable(value="type") String type){
		JSONObject msg = new JSONObject();
		try{
			ILoginUser user = getLoginUser();
			if(user.isSystemUser()){
				final String errorConst = "error-";
				String message = tfsvc.topoFind(type);
				if(message.startsWith(errorConst)){
					message = message.replace(errorConst, "");
					msg.put("failed", true);
				}
				msg.put("msg",message);
			}else{
				msg.put("msg", "没有拓扑发现权限，请向管理员申请");
				msg.put("failed","true");
			}
		}catch(Exception e){
			msg.put("msg", "服务器错误,拓扑发现异常");
			msg.put("failed","true");
			logger.error("服务器错误,拓扑发现异",e);
		}
		return toSuccess(msg);
	}
	/**
	 * 拓扑发现结果
	 * @return 启动采集器的状态标记
	 */
	@RequestMapping(value="result",method=RequestMethod.POST)
	@ResponseBody
	public JSONObject resultInfo(Integer index){
		JSONObject rst = tfsvc.resultInfo(index);
//		logger.info("拓扑发现结果：\n"+rst.toJSONString());
		return rst;
	}
	/**
	 * 单资源发现
	 * @param info 包含发现的基本信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="single",method=RequestMethod.POST)
	public JSONObject singleTopoFind(String info){
		ILoginUser user = getLoginUser();
		if(user.isSystemUser()){
			JSONObject param = (JSONObject) JSON.parseObject(info); 
			return toSuccess(tfsvc.singleTopoFind(param));
		}else{
			JSONObject failed = new JSONObject();
			failed.put("msg", "无权操作");
			return toSuccess(failed);
		}
	}
	@RequestMapping(value="cancel",method=RequestMethod.POST,produces="text/html;charset=UTF-8")
	public String cancelDiscover(){
		JSONObject retn = new JSONObject();
		ILoginUser user = getLoginUser();
		if(user.isSystemUser()){
			int code =  tfsvc.cancelDiscover();
			retn.put("code", code);
		}else{
			retn.put("code", -1);
			retn.put("msg", "无权操作");
		}
		return retn.toJSONString();
	}
}
