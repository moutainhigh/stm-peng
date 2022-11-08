package com.mainsteam.stm.portal.vm.web.action;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.api.IResourceDetailInfoApi;
import com.mainsteam.stm.portal.vm.api.VmResourceListService;
import com.mainsteam.stm.portal.vm.bo.VmResourceBo;
import com.mainsteam.stm.portal.vm.bo.VmResourcePageBo;
import com.mainsteam.stm.portal.vm.web.vo.VmResourcePageVo;
import com.mainsteam.stm.portal.vm.web.vo.VmResourceVo;
import com.mainsteam.stm.util.StringUtil;

@Controller
@RequestMapping("/portal/vm/vmResourceList")
public class VmResourceListAction extends BaseAction {
	
	private static Logger logger = Logger.getLogger(VmResourceListAction.class);
	
	@Resource
	private VmResourceListService vmResourceListService;
	@Resource(name = "resourceDetailInfoApi")
	private IResourceDetailInfoApi resourceDetailInfoApi;
	/**
	 * 删除虚拟化资源
	 * @param ids
	 * @return
	 */
	@RequestMapping("/batchDelResource")
	public JSONObject batchDelResource(long[] ids){
		boolean isSuccess = true;
		try {
			isSuccess = vmResourceListService.deleteVmResources(ids);
		} catch (Exception e) {
			logger.error("delete vm resource error:", e);
		}
		return toSuccess(isSuccess ? 0 : -1);
	}
	
	@RequestMapping("/delResourcePools")
	public JSONObject delResourcePools(String[] uuids){
		boolean isSuccess = vmResourceListService.deleteResourcePools(uuids);
		return toSuccess(isSuccess ? 0 : -1);
	}
	
	/**
	 * 获取已监控资源列表
	 * @param session
	 * @return
	 */
	@RequestMapping("/getMonitorList")
	public JSONObject getMonitorList(VmResourcePageVo pageVo, HttpSession session){
		ILoginUser user = getLoginUser(session);
		VmResourceVo vrsv = pageVo.getCondition();
		if(vrsv.getCategoryId().equals("ResourcePool")){
			VmResourcePageBo vrpb = vmResourceListService.getResourcePoolList(toPageBo(pageVo), user);
			VmResourcePageVo vrpv = toSimplePageVo(vrpb);
			return toSuccess(vrpv);
		}
		
		VmResourcePageBo pageBo = vmResourceListService.getMonitorList(toPageBo(pageVo), user);
		return toSuccess(toPageVo(pageBo));
	}
	
	@RequestMapping("/getVMachineMonitorList")
	public JSONObject getVMachineMonitorList(VmResourcePageVo pageVo, HttpSession session){
		ILoginUser user = getLoginUser(session);
		
		VmResourcePageBo pageBo = vmResourceListService.getResourcePoolVMByPage(toPageBo(pageVo));
		return toSuccess(toPageVo(pageBo));
	}
	
	/**
	 * 获取未监控资源列表
	 * @param session
	 * @return
	 */
	@RequestMapping("/getUnMonitorList")
	public JSONObject getUnMonitorList(VmResourcePageVo pageVo, HttpSession session){
		ILoginUser user = getLoginUser(session);
		
		VmResourcePageBo pageBo = vmResourceListService.getUnMonitorList(toPageBo(pageVo), user);
		return toSuccess(toPageVo(pageBo));
	}
	
	/**
	 * pageVo turn to pageBo
	 * @param pageVo
	 * @return
	 */
	private VmResourcePageBo toPageBo(VmResourcePageVo pageVo){
		VmResourcePageBo pageBo = new VmResourcePageBo();
		BeanUtils.copyProperties(pageVo, pageBo);
		if(pageVo.getCondition() != null){
			pageBo.setCondition(toBo(pageVo.getCondition()));
		}
		if(pageVo.getVmResources() != null){
			List<VmResourceBo> vmResources = new ArrayList<VmResourceBo>();
			for(int i = 0; i < pageVo.getVmResources().size(); i ++){
				vmResources.add(toBo(pageVo.getVmResources().get(i)));
			}
			pageBo.setVmResources(vmResources);
		}
		return pageBo;
	}
	
	/**
	 * pageBo turn to pageVo
	 * @param pageBo
	 * @return
	 */
	private VmResourcePageVo toSimplePageVo(VmResourcePageBo pageBo){
		VmResourcePageVo pageVo = new VmResourcePageVo();
		BeanUtils.copyProperties(pageBo, pageVo);
		if(pageBo.getCondition() != null){
			pageVo.setCondition(toVo(pageBo.getCondition()));
		}
		if (pageBo.getVmResources() != null) {
			List<VmResourceVo> vmResources = new ArrayList<VmResourceVo>();
			for (int i = 0; i < pageBo.getVmResources().size(); i++) {
				VmResourceBo bo = pageBo.getVmResources().get(i);
				VmResourceVo vo = toVo(bo);
				vmResources.add(vo);
				
			}
			pageVo.setVmResources(vmResources);
		}
		return pageVo;
	}
	
	private VmResourcePageVo toPageVo(VmResourcePageBo pageBo){
		VmResourcePageVo pageVo = new VmResourcePageVo();
		BeanUtils.copyProperties(pageBo, pageVo);
		if(pageBo.getCondition() != null){
			pageVo.setCondition(toVo(pageBo.getCondition()));
		}
		if (pageBo.getVmResources() != null) {
			List<VmResourceVo> vmResources = new ArrayList<VmResourceVo>();
			for (int i = 0; i < pageBo.getVmResources().size(); i++) {
				vmResources.add(toVo(pageBo.getVmResources().get(i)));
			}
			pageVo.setVmResources(vmResources);
			for (VmResourceVo vmResourceVo : vmResources) {
				Double free = 0.0;
				Double Volume = 0.0;
				String used = "";
				Map<String, Object> result = resourceDetailInfoApi.getMetricInfo(vmResourceVo.getId());
				Set<String> keys = result.keySet();
				Iterator<String> iterator = keys.iterator();
				while (iterator.hasNext()) {
					String key = iterator.next();
					ArrayList arrayList = (ArrayList) result.get(key);
					if (arrayList != null && arrayList.size() != 0) {
						for (Object o : arrayList) {
							JSONObject obj = (JSONObject) JSONObject.toJSON(o);
							String dsFreeSpace = obj.get("DataStorageFreeSpace").toString().replace("GB", "");
							Double freeTemp = StringUtil.isNumber(dsFreeSpace) ? Double.parseDouble(dsFreeSpace) : 0D;
							String dsVolume = obj.get("DataStorageVolume").toString().replace("GB", "");
							Double VolumeTemp = StringUtil.isNumber(dsVolume) ? Double.parseDouble(dsVolume) : 0D;
							free += freeTemp;
							Volume += VolumeTemp;
						}
						DecimalFormat df = new DecimalFormat("0.00%");
						if (Volume == free) {
							vmResourceVo.setMemUsedRate("0.00%");
						} else if(Volume == 0){
							vmResourceVo.setMemUsedRate("--");
						} else {
							used = df.format((Volume - free) / Volume);
							vmResourceVo.setMemUsedRate(used);
						}
					} else {
						vmResourceVo.setMemUsedRate("--");
					}
				}
			}
		}
		return pageVo;
	}
	
	/**
	 * Vo turn to Bo
	 * @param vo
	 * @return
	 */
	private VmResourceBo toBo(VmResourceVo vo){
		VmResourceBo bo = new VmResourceBo();
		BeanUtils.copyProperties(vo, bo);
		return bo;
	}
	
	/**
	 * Bo turn to Vo
	 * @param bo
	 * @return
	 */
	private VmResourceVo toVo(VmResourceBo bo){
		VmResourceVo vo = new VmResourceVo();
		BeanUtils.copyProperties(bo, vo);
		return vo;
	}
}
