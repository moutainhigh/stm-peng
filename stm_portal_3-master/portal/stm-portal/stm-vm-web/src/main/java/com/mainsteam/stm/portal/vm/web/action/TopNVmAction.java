package com.mainsteam.stm.portal.vm.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONArray;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.vm.api.TopNVmService;
import com.mainsteam.stm.portal.vm.bo.TopNWorkBench;

/**
 * <li>文件名称: TopNVmAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年3月20日
 * @author   caoyong
 */
@Controller
@RequestMapping("portal/vm/topN")
public class TopNVmAction extends BaseAction {
	@Resource
	private TopNVmService topNVmService;
	
	@Resource
	private CapacityService capacityService;
	
	/**
	 * 获取topN柱状图数据
	 * @param objects 查询参数
	 * @return
	 */
	@RequestMapping("/getTopNGraphData")
	Object getTopNGraphData(String sortMetric,String resourceIds,int topNum,String sortOrder){
		try {
			return toSuccess(topNVmService.getTopNGraphData(sortMetric,resourceIds,topNum,sortOrder));
		} catch (Exception e) {
			e.printStackTrace();
			return toJsonObject(799, "获取柱状图失败");
		}
	}
	/**
	 * 获取topN设置信息数据
	 * @param objects
	 * @return
	 */
	@RequestMapping("/getTopNSettingData")
	Object getTopNSettingData(TopNWorkBench topNWorkBench){
		return toSuccess(topNVmService.getUserWorkBenchById(topNWorkBench));
	}
	
	/**
	 * 获取topN用户workbenchs数据
	 * @param objects
	 * @return
	 */
	@RequestMapping("/getTopNUserWorkbenchs")
	Object getTopNUserWorkbenchs(HttpSession session){
		ILoginUser loginUser=getLoginUser(session);
		return toSuccess(topNVmService.getTopNUserWorkBenchs(loginUser.getId()));
	}
	/**
	 * 获取topN所有的workbenchs数据
	 * @param objects
	 * @return
	 */
	@RequestMapping("/getAllWorkbenchs")
	Object getAllWorkbenchs(){
		return toSuccess(topNVmService.getAllWorkBench());
	}
	/**
	 * 获取topN 当前用户所有的workbenchs数据
	 * @param objects
	 * @return
	 */
	@RequestMapping("/geTopNUserWorkbenchs")
	Object geTopNUserWorkbenchs(HttpSession session){
		ILoginUser loginUser=getLoginUser(session);
		return toSuccess(topNVmService.getTopNUserWorkBenchs(loginUser.getId()));
	}
	/**
	 * 设置topN 当前用户workbenchs数据
	 * @param objects
	 * @return
	 */
	@RequestMapping("/setTopNUserWorkbenchs")
	Object setTopNUserWorkbenchs(HttpSession session,String tuwb){
		ILoginUser loginUser=getLoginUser(session);
		return toSuccess(topNVmService.setTopNUserWorkBenchs(loginUser.getId(), JSONArray.parseArray(tuwb, TopNWorkBench.class)));
	}
	
	/**
	 * 删除topN 当前用户的一个workbench数据
	 * @param objects
	 * @return
	 */
	@RequestMapping("/delSingleUserWorkbench")
	Object delSingleUserWorkbench(TopNWorkBench twb){
		return toSuccess(topNVmService.delTopNUserWorkBench(twb));
	}
	
	/**
	 * 更新topN 当前用户的一个workbench数据
	 * @param objects
	 * @return
	 */
	@RequestMapping("/updateTopNSetting")
	Object updateTopNSetting(TopNWorkBench twb){
		return toSuccess(topNVmService.updateTopNSetting(twb));
	}
	
	/**
	 * 获取topN 模块类型数据(vm模型)
	 * @param objects
	 * @return
	 */
	@RequestMapping("/getVmCategory")
	Object getVmCategory(){
		
		List<Map<String,Object>> listMap = new ArrayList<Map<String,Object>>();
		
		CategoryDef categoryDef = capacityService.getCategoryById("VM");
		if(null != categoryDef ){
			CategoryDef[] childCategoryDef =categoryDef.getChildCategorys();
			if(null!=childCategoryDef && childCategoryDef.length>0){
				for(CategoryDef child : childCategoryDef){
					
					Map<String,Object> map = new HashMap<String,Object>();
					map.put("id", child.getId());
					map.put("categoryId", child.getId());
					map.put("categoryPid", null);
					map.put("name", child.getName());
					map.put("pid", "Resource");
					map.put("state", "open");
					
					listMap.add(map);
					
					CategoryDef[] childSecondCategoryDef = child.getChildCategorys();
					if(childSecondCategoryDef != null && childSecondCategoryDef.length > 0){
						for(CategoryDef secondChild : childSecondCategoryDef){
							
							if(secondChild.getId().equals("VCenter") ||
								secondChild.getId().equals("VCenter5.5") ||
								secondChild.getId().equals("VCenter6") ||
								secondChild.getId().equals("VCenter6.5")){
								continue;
							}
							
							Map<String,Object> childMap = new HashMap<String,Object>();
							childMap.put("id", secondChild.getId());
							map.put("categoryId", secondChild.getId());
							map.put("categoryPid", child.getId());
							childMap.put("name", secondChild.getName());
							childMap.put("pid", child.getId());
							childMap.put("state", null);
							
							listMap.add(childMap);
						}
					}
					
				}
			}
		}
		
		return toSuccess(listMap);
		
//		return toSuccess(topNVmService.getVmCategory());
	}
	/**
	 * 获取topN 模型性能指标数据(根据模型resourceId)
	 * @param objects
	 * @return
	 */
	@RequestMapping("/getVmMetricByCategoryId")
	Object getVmMetricByCategoryId(String categoryId){
		return toSuccess(topNVmService.getVmMetricByCategoryId(categoryId));
	}
	
	/**
	 * 获取topN 模型资源数据(根据模型resourceId)
	 * @param objects
	 * @return
	 */
	@RequestMapping("/getVmInstances")
	Object getVmInstances(String categoryId,Long domainId,String nameOrIp){
		try {
			return toSuccess(topNVmService.getVmInstances(categoryId,domainId,nameOrIp));
		} catch (Exception e) {
			e.printStackTrace();
			return toJsonObject(799, "根据模型查询相应资源失败");
		}
	}
	
}
