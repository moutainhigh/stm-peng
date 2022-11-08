package com.mainsteam.stm.system.resource.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.resource.api.InfoMetricQueryAdaptApi;
import com.mainsteam.stm.system.resource.api.IResourceApi;
import com.mainsteam.stm.system.resource.bo.ResourceInstanceBo;
import com.mainsteam.stm.system.resource.web.vo.ResourceQueryVo;

/**
 * <li>文件名称: ResourceAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月28日
 * @author   ziwenwen
 */
@Controller("stm_system_resourceAction")
@RequestMapping("system/resource/")
public class ResourceAction extends BaseAction {
	
	@Autowired
	@Qualifier("stm_system_resourceApi")
	private IResourceApi resourceApi;
	@Resource
	private MetricDataService metricDataService;
	@Resource
	private InfoMetricQueryAdaptApi infoMetricQueryAdaptService;
	
	@RequestMapping("getResourceById")
	public JSONObject getResourceById(Long resourceId){
		return toSuccess(resourceApi.getResource(resourceId));
	}
	
	@RequestMapping("getResources")
	public JSONObject getResources(){
		return toSuccess(resourceApi.getResources(getLoginUser()));
	}
	
	@RequestMapping("getResourcesByDomainIds")
	public JSONObject getResources(String domainIds){
		return toSuccess(resourceApi.getResources(getLoginUser(),JSONArray.parseArray(domainIds, Long.class)));
	}
	
	@RequestMapping("getResourcesByDomainId")
	public JSONObject getResources(Long domainId){
		return toSuccess(resourceApi.getResources(getLoginUser(),domainId));
	}
	
	@RequestMapping("getResourcesByResource")
	public JSONObject getResources(ResourceQueryVo resource){
		resource.setUser(getLoginUser());
		List<ResourceInstanceBo> riBoList = resourceApi.getResources(resource);
		getResourcesIp(riBoList);
		return toSuccess(riBoList);
	}
	
	@RequestMapping("getCategory")
	public JSONObject getCategory(){
		return toSuccess(resourceApi.getTreeCategory());
	}
	
	@RequestMapping("getCategoryById")
	public JSONObject getCategory(String categoryId){
		return toSuccess(resourceApi.getTreeCategory(categoryId));
	}
	
	@RequestMapping("getCategoryMapper")
	public JSONObject getCategoryMapper(){
		return toSuccess(resourceApi.getCategoryMapper());
	}
	
	private void getResourcesIp(List<ResourceInstanceBo> riBoList){
		String metricId = "ip";
		Map<Long, ResourceInstanceBo> riBoMap = new HashMap<Long, ResourceInstanceBo>();
		List<Long> instIdList = new ArrayList<Long>();
		for (int i = 0; riBoList != null && i < riBoList.size(); i++) {
			ResourceInstanceBo riBo = riBoList.get(i);
			if(riBo.getDiscoverIP() == null || "".equals(riBo.getDiscoverIP())){
				instIdList.add(riBo.getId());
				riBoMap.put(riBo.getId(), riBo);
			}
		}
		long[] instIdArray = new long[instIdList.size()];
		for (int i = 0; i < instIdList.size(); i++) {
			instIdArray[i] = instIdList.get(i);
		}
		if(!instIdList.isEmpty()){
//			List<MetricData> mdList = metricDataService.getMetricInfoDatas(instIdArray, new String[]{metricId});
			//infoMetricQueryAdaptService
			List<MetricData> mdList = infoMetricQueryAdaptService.getMetricInfoDatas(instIdArray, new String[]{metricId});

			for (int i = 0; mdList != null && i < mdList.size(); i++) {
				MetricData md = mdList.get(i);
				if(md != null && riBoMap.containsKey(md.getResourceInstanceId()) && metricId.equals(md.getMetricId())){
					String ip = md.getData() != null && md.getData().length > 0 ? md.getData()[0] : "";
					riBoMap.get(md.getResourceInstanceId()).setDiscoverIP(ip);
				}
			}
		}
	}
}


