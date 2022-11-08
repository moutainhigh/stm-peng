package com.mainsteam.stm.portal.vm.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.caplib.dict.MetricTypeEnum;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.caplib.resource.ResourceMetricDef;
import com.mainsteam.stm.common.metric.MetricDataService;
import com.mainsteam.stm.common.metric.obj.MetricData;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.portal.vm.api.TopNVmService;
import com.mainsteam.stm.portal.vm.bo.TopNWorkBench;
import com.mainsteam.stm.portal.vm.bo.VmCategoryResourceBo;
import com.mainsteam.stm.portal.vm.dao.ITopNUserWorkbenchDao;

/**
 * <li>文件名称: TopNVmServiceImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年3月20日
 * @author   caoyong
 */
public class TopNVmServiceImpl implements TopNVmService {
	@Autowired
	private ITopNUserWorkbenchDao topNUserWorkbenchDao;
	private List<TopNWorkBench> workBenches;
	@Resource
	private CapacityService capacityService;
	@Resource
	private ResourceInstanceService resourceInstanceService;
	@Resource
	private MetricDataService metricDataService;
	/**
	 * 日志
	 */
	private Logger logger = Logger.getLogger(TopNVmServiceImpl.class);
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public Object getTopNGraphData(String sortMetric,String ids,int topNum,final String sortOrder) throws Exception{
		//通过resource实例ID获取topn
		List<MetricData> result = new ArrayList<MetricData>();
		ResourceMetricDef md = null;
		if(sortMetric!=null && null!=ids&&!"".equals(ids.trim())){
			String[] resourceIds = ids.split(",");
			if(resourceIds!=null && resourceIds.length>0){
				long[] myInstanceIds = new long[resourceIds.length];
				for (int i=0;i<resourceIds.length;i++) {
					ResourceInstance rs = resourceInstanceService.getResourceInstance(Long.parseLong(resourceIds[i]));
					if(null == rs) continue;
					if(null == md) md = capacityService.getResourceMetricDef(rs.getResourceId(), sortMetric);
					myInstanceIds[i]=Long.parseLong(resourceIds[i]);
				}
				if(myInstanceIds!=null && myInstanceIds.length>0){
					result = metricDataService.findTop(sortMetric,myInstanceIds, topNum);
				}
			}
		}
		Map<String, Object> resultMap = new HashMap<String, Object>();
		List data = new ArrayList<Map<String, Object>>();
		List<String> cate = new ArrayList<String>();
		if (result != null && result.size() > 0) {
			for (MetricData metric : result) {
				ResourceInstance resourceInstance = resourceInstanceService.getResourceInstance(metric.getResourceInstanceId());
				Map<String, Object> dataObj = new HashMap<String, Object>();
				dataObj.put("name", resourceInstance==null?"":resourceInstance.getShowName());
				dataObj.put("y", metric.getData()==null?0:metric.getData()[0]);
				if(null!=md) dataObj.put("unit", md.getUnit());
				cate.add(resourceInstance==null?"":resourceInstance.getShowName());
				data.add(dataObj);
			}
			//sort metricData by sortOrder
			Collections.sort(data,new Comparator<Map>() {
				@Override
				public int compare(Map o1, Map o2) {
					Object obj1 = o1.get("y"),obj2 = o2.get("y");
					if((o1==null||obj1==null)&&obj2!=null) return "asc".equals(sortOrder)?-1:1;
					else if(obj1!=null&&(o2==null||obj2==null)) return "asc".equals(sortOrder)?1:-1;
					else if((o1==null||obj1==null)&&(o2==null||obj2==null)) return 0;
					else return "asc".equals(sortOrder)?((Double.parseDouble(o1.get("y").toString())-Double.parseDouble(o2.get("y").toString()))>0?1:-1)
							:((Double.parseDouble(o2.get("y").toString())-Double.parseDouble(o1.get("y").toString()))>0?1:-1);
				}
			});
			resultMap.put("data", data);
			resultMap.put("categories", cate);
		}
		return resultMap;
	}

	@Override
	public int delTopNUserWorkBench(TopNWorkBench arg0) {
		return topNUserWorkbenchDao.delSingleUserWorkBench(arg0);
	}

	@Override
	public List<TopNWorkBench> getAllWorkBench() {
		if(workBenches==null){
			workBenches=topNUserWorkbenchDao.getAllWorkBench();
			for(int i=0,len=workBenches.size();i<len;i++){
				workBenches.get(i).setSort(i);
			}
		}
		return workBenches;
	}

	@Override
	public List<TopNWorkBench> getTopNUserWorkBenchs(Long arg0) {
		return topNUserWorkbenchDao.getTopNUserWorkBenchs(arg0);
	}

	@Override
	public TopNWorkBench getUserWorkBenchById(TopNWorkBench arg0) {
		return topNUserWorkbenchDao.getTopNUserWorkBenchById(arg0);
	}
	
	@Override
	public int updateTopNSetting(TopNWorkBench t) {
		return topNUserWorkbenchDao.updateTopNSetting(t);
	}

	@Override
	public int setTopNUserWorkBenchs(Long arg0, List<TopNWorkBench> arg1) {
		topNUserWorkbenchDao.delTopNUserWorkBench(arg0);
		if(null==arg1 || arg1.size()==0){
			return 0;
		}
		return topNUserWorkbenchDao.batchInsertWorkBenchs(arg1);
	}

	@Override
	public List<VmCategoryResourceBo> getVmCategory() {
		List<VmCategoryResourceBo> categoryVos = new ArrayList<VmCategoryResourceBo>();
		CategoryDef[] vmCategoryDefs = capacityService.getCategoryById("VM").getChildCategorys();
		for(CategoryDef c:vmCategoryDefs){
			if ("VM".equals(c.getParentCategory().getId()) && !"VCenter".equals(c.getName()) 
					&& !"VCenter5.5".equals(c.getName())
					&& !"VCenter6".equals(c.getName())
					&& !"VCenter6.5".equals(c.getName())) {
				VmCategoryResourceBo categoryVo = new VmCategoryResourceBo();
				categoryVo.setCategoryId(c.getId());
				categoryVo.setCategoryPid(c.getParentCategory().getId());
				categoryVo.setName(c.getName());
				categoryVo.setId(c.getId());
				categoryVos.add(categoryVo);
			}
		}
		return categoryVos;
	}
	/**
	 * @param arg0 categoryId
	 * @param arg1 domainId
	 * @param arg0 nameOrIp
	 */
	@Override
	public List<VmCategoryResourceBo> getVmInstances(String arg0,Long arg1,String arg2) throws Exception{
		
		CategoryDef def = capacityService.getCategoryById(arg0);
		
		List<VmCategoryResourceBo> results = new ArrayList<VmCategoryResourceBo>();
		
		if(def.getChildCategorys() != null && def.getChildCategorys().length > 0){
			//父类别
			for(CategoryDef childDef : def.getChildCategorys()){
				
				if(childDef.getId().equals("VCenter") ||
					childDef.getId().equals("VCenter5.5") ||
					childDef.getId().equals("VCenter6") ||
					childDef.getId().equals("VCenter6.5")){
					continue;
				}
				
				List<ResourceInstance> instances = resourceInstanceService.getParentInstanceByCategoryId(childDef.getId());
				
				if(null!=instances && instances.size()>0){
					for(ResourceInstance instance : instances){
						if(instance.getLifeState().equals(InstanceLifeStateEnum.MONITORED) && (-1==arg1 || instance.getDomainId()==arg1)){
							if(StringUtils.isEmpty(arg2) || (!StringUtils.isEmpty(arg2)
									&& (instance.getShowName().toLowerCase().contains(arg2)||(instance.getShowIP() != null && instance.getShowIP().contains(arg2))))){
								VmCategoryResourceBo v = new VmCategoryResourceBo();
								v.setId(String.valueOf(instance.getId()));
								v.setInstanceId(instance.getId());
								v.setInstancePid(instance.getParentId());
								v.setName(instance.getShowName());
								results.add(v);
							}
						}
					}
				}
			}
			
		}else{
			List<ResourceInstance> instances = resourceInstanceService.getParentInstanceByCategoryId(arg0);
			
			if(null!=instances && instances.size()>0){
				for(ResourceInstance instance : instances){
					if(instance.getLifeState().equals(InstanceLifeStateEnum.MONITORED) && (-1==arg1 || instance.getDomainId()==arg1)){
						if(StringUtils.isEmpty(arg2) || (!StringUtils.isEmpty(arg2)
								&& (instance.getShowName().toLowerCase().contains(arg2)||(instance.getShowIP() != null && instance.getShowIP().contains(arg2))))){
							VmCategoryResourceBo v = new VmCategoryResourceBo();
							v.setId(String.valueOf(instance.getId()));
							v.setInstanceId(instance.getId());
							v.setInstancePid(instance.getParentId());
							v.setName(instance.getShowName());
							results.add(v);
						}
					}
				}
			}
		}
		
		return results;
	}

	@Override
	public List<VmCategoryResourceBo> getVmMetricByCategoryId(String arg0) {
		
		CategoryDef def = capacityService.getCategoryById(arg0);
		
		List<VmCategoryResourceBo> metricDefVos = new ArrayList<VmCategoryResourceBo>();
		
		if(def.getChildCategorys() != null && def.getChildCategorys().length > 0){
			List<String> resourceIdList = new ArrayList<String>();
			//父类别
			for(CategoryDef childDef : def.getChildCategorys()){
				
				if(childDef.getId().equals("VCenter") ||
					childDef.getId().equals("VCenter5.5") ||
					childDef.getId().equals("VCenter6") ||
					childDef.getId().equals("VCenter6.5")){
					continue;
				}
				
				ResourceDef[] resourceDefs = childDef.getResourceDefs();
				if(resourceDefs==null) return null;
				for(ResourceDef resourceDef:resourceDefs){
					resourceIdList.add(resourceDef.getId());
				}
			}
			
			Map<String, List<VmCategoryResourceBo>> metricMap = new HashMap<String, List<VmCategoryResourceBo>>();
			
			for(String resourceId : resourceIdList){
				ResourceDef resourceDef = capacityService.getResourceDefById(resourceId);
				if(resourceDef == null){
					continue;
				}
				Map<Long, VmCategoryResourceBo> performanceMetricMap = new TreeMap<Long, VmCategoryResourceBo>();
				for(ResourceMetricDef metricDef : resourceDef.getMetricDefs()){
					if(metricDef.getMetricType() == MetricTypeEnum.PerformanceMetric){
						VmCategoryResourceBo metricDefVo = new VmCategoryResourceBo();
						metricDefVo.setId(metricDef.getId());
						metricDefVo.setName(metricDef.getName());
						metricDefVo.setMetricId(metricDef.getId());
						metricDefVos.add(metricDefVo);
					}
				}
				metricMap.put(resourceId, new ArrayList<VmCategoryResourceBo>(performanceMetricMap.values()));
			}
			
			//取所有集合的交集
			for(String resourceId : metricMap.keySet()){
				
				if(metricDefVos.size() <= 0){
					metricDefVos = metricMap.get(resourceId);
					continue;
				}
				
				List<VmCategoryResourceBo> metricLists = metricMap.get(resourceId);
				metricDefVos.retainAll(metricLists);
				
			}
			
		}else{
			ResourceDef[] resourceDefs = def.getResourceDefs();
			if(resourceDefs==null) return null;
			for(ResourceDef resourceDef:resourceDefs){
				ResourceMetricDef[] metricDefs = resourceDef.getMetricDefs();
				if(null != metricDefs && metricDefs.length>0){
					for(ResourceMetricDef metricDef: metricDefs){
						if(MetricTypeEnum.PerformanceMetric.equals(metricDef.getMetricType())){
							VmCategoryResourceBo metricDefVo = new VmCategoryResourceBo();
							metricDefVo.setId(metricDef.getId());
							metricDefVo.setName(metricDef.getName());
							metricDefVo.setMetricId(metricDef.getId());
							metricDefVos.add(metricDefVo);
						}
					}
				}
			}
		}
		
		return metricDefVos;
	}
}
