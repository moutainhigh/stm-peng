package com.mainsteam.stm.portal.vm.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.vm.api.DiscoveryVmService;
import com.mainsteam.stm.portal.vm.bo.VmDiscoveryListBo;
import com.mainsteam.stm.portal.vm.bo.VmDiscoveryListPageBo;
import com.mainsteam.stm.portal.vm.bo.VmDiscoveryParaBo;
import com.mainsteam.stm.portal.vm.web.vo.VmDiscoveryListPageVo;
import com.mainsteam.stm.portal.vm.web.vo.VmDiscoveryListVo;


@Controller
@RequestMapping("/portal/vm/discoveryVm")
public class DiscoveryVmAction extends BaseAction {
	
	private static Logger logger = Logger.getLogger(DiscoveryVmAction.class);
	
	@Resource
	private DiscoveryVmService discoveryVmService;
	
	/**
	 * 自动发现
	 * @param domain
	 * @param dcs
	 * @param discoveryType
	 * @param ip
	 * @param userName
	 * @param password
	 * @param session
	 * @return
	 */
	@RequestMapping("/autoDiscovery")
	public JSONObject autoDiscovery(VmDiscoveryParaBo dParaBo, HttpSession session){
		ILoginUser user = getLoginUser(session);
		Map<String, Object> resultCollection = discoveryVmService.autoDiscovery(dParaBo, user);
		return toSuccess(resultCollection);
	}
	
	
	/**
	 * 自动发现加入监控
	 * @param session
	 * @return
	 */
	@RequestMapping("/addMoniterAutoDiscoveryVm")
	public JSONObject addMoniterAutoDiscoveryVm(String instanceIds, HttpSession session){
		ILoginUser user =  getLoginUser(session);
		List<Long> instanceIdList = new ArrayList<Long>();
		if(instanceIds != null && !"".equals(instanceIds)){
			String[] instanceIdArray = instanceIds.split(",");
			for (int i = 0; i < instanceIdArray.length; i++) {
				if(!"".equals(instanceIdArray[i])){
					instanceIdList.add(Long.valueOf(instanceIdArray[i]));
				}
			}
		}
		return toSuccess(discoveryVmService.addMoniterAutoDiscoveryVm(user, instanceIdList));
	}
	
	/**
	 * 补充发现所需要的vmresource
	 * @param session
	 * @return
	 */
	@RequestMapping("/suppleDiscoveryVmResources")
	public JSONObject suppleDiscoveryVmResources(HttpSession session){
		ILoginUser user = getLoginUser(session);
		return toSuccess(discoveryVmService.suppleDiscoveryVmResources(user));
	}
	
	/**
	 * 补充发现
	 * @param instanceId
	 * @param session
	 * @return
	 */
	@RequestMapping("/suppleDiscovery")
	public JSONObject suppleDiscovery(String instanceId, HttpSession session){
		ILoginUser user = getLoginUser(session);
		return toSuccess(discoveryVmService.suppleDiscovery(instanceId, user));
	}
	
	/**
	 * 补充发现加入监控
	 * @param instanceIds
	 * @param session
	 * @return
	 */
	@RequestMapping("/addMoniterSuppleDiscoveryVm")
	public JSONObject addMoniterSuppleDiscoveryVm(String instanceIds, HttpSession session){
		ILoginUser user = getLoginUser(session);
		List<Long> instanceIdList = new ArrayList<Long>();
		if(instanceIds != null && !"".equals(instanceIds)){
			String[] instanceIdArray = instanceIds.split(",");
			for (int i = 0; i < instanceIdArray.length; i++) {
				if(!"".equals(instanceIdArray[i])){
					instanceIdList.add(Long.valueOf(instanceIdArray[i]));
				}
			}
		}
		return toSuccess(discoveryVmService.addMoniterSuppleDiscoveryVm(user, instanceIdList));
	}
	/**
	 * 修改发现参数
	 * @param jsonData
	 * @param instanceId
	 * @return
	 */
	@RequestMapping("/updateDiscoverParamterVm")
	public JSONObject updateDiscoverParamterVm(String jsonData, long instanceId) {
		Map paramter = JSONObject.parseObject(jsonData, HashMap.class);
		int result = discoveryVmService.updateDiscoverParamterVm(paramter, instanceId);
		return toSuccess(result);
	}
	
	@RequestMapping("/testDiscoverVm")
	public JSONObject testDiscoverVm(String jsonData, long instanceId){
		Map paramter = JSONObject.parseObject(jsonData, HashMap.class);
		int result = discoveryVmService.testDiscoverVm(paramter, instanceId);
		return toSuccess(result);
	}
	
	@RequestMapping("/reDiscoverVm")
	public JSONObject reDiscoverVm(String jsonData, long instanceId){
		Map paramter = JSONObject.parseObject(jsonData, HashMap.class);
		int result = discoveryVmService.reDiscoveryVm(paramter, instanceId);
		return toSuccess(result);
	}
	
	/**
	 * 手动发现
	 * @return
	 */
	@RequestMapping("/reDiscoverVmByTreeResult")
	public JSONObject reDiscoverVmByTreeResult(VmDiscoveryParaBo dParaBo, HttpSession session){
		ILoginUser user = getLoginUser(session);
		Map<String, Object> resultCollection = new HashMap<String,Object>();
		//保存发现信息
		Map paramter = JSONObject.parseObject(dParaBo.getDataJson(), HashMap.class);
		int result = discoveryVmService.updateDiscoverParamterVm(paramter, dParaBo.getInstanceId());
		if(result == 1){
			//重新发现
			resultCollection = discoveryVmService.reDiscoveryTreeResult(dParaBo, user);
		}else if(result == 2){
			resultCollection.put("isSuccess", false);
			resultCollection.put("errorMsg", "发现参数不正确");
		}else{
			resultCollection.put("isSuccess", false);
			resultCollection.put("errorMsg", "保存发现参数有误!");
		}
		
//		discoveryVmService.autoRerfreshVmResourceTest(dParaBo);
		return toSuccess(resultCollection);
	}
	
	/**
	 * 手动发现,加入监控
	 * @param session
	 * @return
	 */
	@RequestMapping("/addMoniterReDiscoveryVm")
	public JSONObject addMoniterReDiscoveryVm(String instanceIds,String instanceIdsUnchecked, HttpSession session){
		ILoginUser user =  getLoginUser(session);
		HashSet<Long> instanceIdSet = new HashSet<Long>();
		HashSet<Long> instanceIdUncheckedSet = new HashSet<Long>();
		
		if(instanceIds != null && !"".equals(instanceIds)){
			String[] instanceIdArray = instanceIds.split(",");
			for (int i = 0; i < instanceIdArray.length; i++) {
				if(!"".equals(instanceIdArray[i]) ){
					instanceIdSet.add(Long.valueOf(instanceIdArray[i]));
				}
			}
		}
		if(instanceIdsUnchecked != null && !"".equals(instanceIdsUnchecked)){
			String[] instanceIdArray = instanceIdsUnchecked.split(",");
			for (int i = 0; i < instanceIdArray.length; i++) {
				if(!"".equals(instanceIdArray[i])){
					instanceIdUncheckedSet.add(Long.valueOf(instanceIdArray[i]));
				}
			}
		}
		return toSuccess(discoveryVmService.addMoniterReDiscoveryVm(user, instanceIdSet , instanceIdUncheckedSet));
	}
	
	/**
	 * 
	 * @param pageVo
	 * @param session
	 * @return
	 */
	@RequestMapping("/getVmDiscoveryList")
	public JSONObject getVmDiscoveryList(VmDiscoveryListPageVo pageVo, HttpSession session){
		ILoginUser user = getLoginUser(session);
		VmDiscoveryListPageBo pageBo = discoveryVmService.getDiscoveryList(toPageBo(pageVo), user);
		return toSuccess(toPageVo(pageBo));
	}
	
	
	private VmDiscoveryListPageBo toPageBo(VmDiscoveryListPageVo pageVo){
		VmDiscoveryListPageBo pageBo = new VmDiscoveryListPageBo();
		BeanUtils.copyProperties(pageVo, pageBo);
		if(pageVo.getCondition() != null){
			pageBo.setCondition(toBo(pageVo.getCondition()));
		}
		if(pageVo.getVmDiscoveryList() != null){
			List<VmDiscoveryListBo> vmDList = new ArrayList<VmDiscoveryListBo>();
			for(int i = 0; i < pageVo.getVmDiscoveryList().size(); i ++){
				vmDList.add(toBo(pageVo.getVmDiscoveryList().get(i)));
			}
			pageBo.setVmDiscoveryList(vmDList);
		}
		return pageBo;
	}
	private VmDiscoveryListPageVo toPageVo(VmDiscoveryListPageBo pageBo){
		VmDiscoveryListPageVo pageVo = new VmDiscoveryListPageVo();
		BeanUtils.copyProperties(pageBo, pageVo);
		if(pageBo.getCondition() != null){
			pageVo.setCondition(toVo(pageBo.getCondition()));
		}
		if(pageBo.getVmDiscoveryList() != null){
			List<VmDiscoveryListVo> vmDList = new ArrayList<VmDiscoveryListVo>();
			for(int i = 0; i < pageBo.getVmDiscoveryList().size(); i++){
				vmDList.add(toVo(pageBo.getVmDiscoveryList().get(i)));
			}
			pageVo.setVmDiscoveryList(vmDList);
		}
		return pageVo;
	}
	private VmDiscoveryListBo toBo(VmDiscoveryListVo vo){
		VmDiscoveryListBo bo = new VmDiscoveryListBo();
		BeanUtils.copyProperties(vo, bo);
		return bo;
	}
	private VmDiscoveryListVo toVo(VmDiscoveryListBo bo){
		VmDiscoveryListVo vo = new VmDiscoveryListVo();
		BeanUtils.copyProperties(bo, vo);
		return vo;
	}
}
