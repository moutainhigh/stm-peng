package com.mainsteam.stm.topo.web.action;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.Arrays;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.rpc.client.OCRPCClient;
import com.mainsteam.stm.topo.api.ITopoAuthSettingApi;
import com.mainsteam.stm.topo.api.LinkService;
import com.mainsteam.stm.topo.api.ThirdService;
import com.mainsteam.stm.topo.bo.LinkBo;
import com.mainsteam.stm.topo.bo.TopoAuthSettingBo;
@Controller
@RequestMapping(value="topo/link")
public class LinkAction extends BaseAction{
	Logger logger = Logger.getLogger(LinkAction.class);
	@Autowired
	private OCRPCClient client;
	@Autowired
	private LinkService lsvc;
	@Autowired
	private ThirdService thirdSvc;
	//权限控制
	@Autowired
	private ITopoAuthSettingApi authSvc;
	
	@ResponseBody
	@RequestMapping(value="remove/allLink",method=RequestMethod.POST)
	public JSONObject removeMultiLink(Long subTopoId,Long id){
		JSONObject retn = new JSONObject();
		if(authSvc.hasAuth(subTopoId, new String[]{TopoAuthSettingBo.EDIT})){//必须包含编辑权限
			lsvc.removeMultiLink(id);
			retn.put("state","success");
			retn.put("code",200);
			retn.put("msg", "删除成功");
		}else{
			retn.put("state","failed");
			retn.put("msg", "无权限操作");
			retn.put("code",700);
		}
		return retn;
	}
	
	/**
	 * 查询多链路
	 * @param page
	 * @param conditions
	 * @return
	 */
	@RequestMapping("/multi/list")
	public JSONObject getMultiLinkPageList(Page<LinkBo, LinkBo> page, LinkBo params) {
		try {
			//封装查询条件
			page.setCondition(params);
			lsvc.selectMultiLinkByPage(page);
			return super.toSuccess(page);
		} catch (Exception e) {
			logger.error("查询多链路列表数据失败!",e);
			return toJsonObject(700, "数据查询失败");	//700-999 各个业务模块自定义异常的代码取值范围
		}
	}
	
	/**
	 *	批量设置链路带宽利用率-阀值
	 * @param data 链路数据
	 * @return
	 */
	@ResponseBody
	@RequestMapping("/updateBandWidthUtil")
	public JSONObject updateBandWidthUtil(String info){
		try{
			if(StringUtils.isNotBlank(info)){
				boolean rst = lsvc.updateBandWidthUtil(info);
				String msg = rst?"设置成功":"设置失败";
				return super.toSuccess(msg);
			}else{
				return super.toJsonObject(700, "没有阈值信息,无法设置");
			}
		}catch(Exception e){
			logger.error("设置链路阈值指标失败!",e);
			return super.toJsonObject(700, "设置失败");
		}
	}
	
	/**
	 * 获取链路信息（链路的详细信息）
	 * @param id 链路的图元id
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="{instanceId}",method=RequestMethod.POST)
	public JSONObject getDetailInfo(@PathVariable(value="instanceId") Long instanceId){
		JSONObject retn = new JSONObject();
		try {
			return lsvc.getDetailInfo(instanceId);
		} catch (Exception e) {
			logger.error("获取链路信息异常",e);
			retn.put("msg","获取链路信息异常");
			retn.put("state",700);
		}
		return retn;
	}
	@ResponseBody
	@RequestMapping(value="tip",method=RequestMethod.POST)
	public JSONObject getLinkInfoForTip(Long instanceId){
		JSONObject retn = null;
		try {
			retn = lsvc.getLinkInfoForTip(instanceId);
		} catch (Exception e) {
			logger.error("获取链路提示信息异常：LinkAction.getLinkInfoForTip()",e);
			retn=new JSONObject();
			retn.put("status", 700);
			retn.put("msg","无法获取完整数据,请检查链路数据");
			retn.put("more",e.getMessage());
		}
		return retn;
	}
	/**
	 * 更新链路
	 * @param data 链路数据
	 * @return
	 */
	@RequestMapping(value="updateLink")
	public JSONObject updateLink(String info){
		logger.info("更新链路信息:"+info);
		try{
			boolean isOk = lsvc.refreshLink(info);
			String msg = isOk?"更新成功":"更新失败";
			return super.toSuccess(msg);
		}catch(Exception e){
			logger.error("更新链路信息失败!",e);
			return super.toJsonObject(700, "更新链路信息失败");
		}
	}
	@ResponseBody
	@RequestMapping(value="addLink",method=RequestMethod.POST)
	public JSONObject addLink(Long subTopoId,LinkBo lb){
		JSONObject retn = new JSONObject();
		try {
			if(authSvc.hasAuth(subTopoId, new String[]{TopoAuthSettingBo.EDIT})){//必须包含编辑权限
				if(lsvc.addLink(lb)){
					retn.put("link", JSON.toJSON(lb));
					retn.put("state",200);
					retn.put("msg", "添加成功");
				}else{
					throw new RuntimeException("重复添加");
				}
			}else{
				throw new RuntimeException("无权限操作");
			}
		} catch (Exception e) {
			retn.put("state",700);
			retn.put("msg", e.getMessage());
		}
		return retn;
	}
	@ResponseBody
	@RequestMapping(value="removeLink",method=RequestMethod.POST)
	public JSONObject removeLink(Long subTopoId,Long[] ids){
		JSONObject retn = new JSONObject();
		try{
			if(authSvc.hasAuth(subTopoId, new String[]{TopoAuthSettingBo.EDIT})){//必须包含编辑权限
				lsvc.removeLink(Arrays.asList(ids));
				retn.put("state","success");
				retn.put("code",200);
				retn.put("msg", "删除成功");
			}else{
				retn.put("state","failed");
				retn.put("msg", "无权限操作");
				retn.put("code",700);
			}
		}catch(Exception e){
			retn.put("state","failed");
			retn.put("msg", "删除链路失败");
			retn.put("code",700);
			logger.error("删除链路失败",e);
		}
		return retn;
	}
	@ResponseBody
	@RequestMapping(value="addMonitor",method=RequestMethod.POST)
	public JSONObject addMonitor(Long subTopoId,Long id){
		JSONObject retn = new JSONObject();
		try {
			if(authSvc.hasAuth(subTopoId, new String[]{TopoAuthSettingBo.EDIT})){//必须包含编辑权限
				JSONObject relation = lsvc.addMonitor(Arrays.asList(id));
				if(relation.isEmpty()){
					retn.put("state",700);
					retn.put("msg", "链路加入监控失败");
					logger.error("链路加入监控失败");
				}else{
					retn.put("state",200);
					retn.put("relation", relation);
				}
			}else{
				retn.put("msg", "无权限操作");
				retn.put("state",700);
			}
		} catch (Exception e) {
			retn.put("msg", "链路加入监控失败");
			logger.error("链路加入监控失败",e);
		}
		return retn;
	}
	@ResponseBody
	@RequestMapping(value="updateAttr",method=RequestMethod.POST)
	public JSONObject updateAttr(LinkBo link,Long subTopoId){
		JSONObject retn = new JSONObject();
		if(authSvc.hasAuth(subTopoId, new String[]{TopoAuthSettingBo.EDIT})){//必须包含编辑权限
			return lsvc.updateAttr(link);
		}else{
			retn.put("msg", "无权限操作");
			retn.put("state",700);
		}
		return retn;
	}
	/**
	 * 获取带宽
	 * @param instanceId 接口资源实例id
	 * @param unit 单位(默认bps)
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="bandWidth",method=RequestMethod.POST)
	public JSONObject bandWidth(Long instanceId,String unit){
		JSONObject retn = new JSONObject();
		try{
			if(instanceId!=null){
				double bw = lsvc.getBandWidth(instanceId,unit);
				retn.put("state", 200);
				retn.put("bandWidth", bw);
			}else{
				retn.put("msg", "非法操作");
				retn.put("state", 700);
			}
		}catch(Exception e){
			logger.error("获取带宽发生异常",e);
			retn.put("msg", "获取带宽失败");
			retn.put("state", 700);
		}
		return retn;
	}
	@ResponseBody
	@RequestMapping(value="caculateFlow",method=RequestMethod.POST)
	public JSONObject caculateFlow(Float min,Float max,Float base){
		JSONObject retn = new JSONObject();
		if(min==null || max==null || base==null){
			retn.put("msg", "非法操作");
			retn.put("state",700);
		}else{
			DecimalFormat df = new DecimalFormat("0.00");
			BigDecimal minR=new BigDecimal(min).divide(new BigDecimal(100));
			BigDecimal maxR=new BigDecimal(max).divide(new BigDecimal(100));
			BigDecimal baseV=new BigDecimal(base);
			retn.put("min", df.format(minR.multiply(baseV)));
			retn.put("max", df.format(maxR.multiply(baseV)));
		}
		return retn;
	}
	@ResponseBody
	@RequestMapping(value="getValueInstId",method=RequestMethod.POST)
	public JSONObject getValueInstId(Long instId){
		JSONObject retn = new JSONObject();
		Long valInstId=lsvc.getValueInstId(instId);
		if(valInstId!=null){
			retn.put("status",200);
			retn.put("instanceId", valInstId);
		}else{
			retn.put("status",700);
			retn.put("msg","无法获取链路的取值接口");
		}
		return retn;
	}
}
