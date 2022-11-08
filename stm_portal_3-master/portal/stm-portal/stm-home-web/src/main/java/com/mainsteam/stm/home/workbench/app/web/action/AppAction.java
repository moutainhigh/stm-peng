package com.mainsteam.stm.home.workbench.app.web.action;

import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.stereotype.Controller;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.home.workbench.app.api.IHomeAppApi;
import com.mainsteam.stm.platform.web.action.BaseAction;

/**
 * <li>文件名称: AppAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2019年9月16日
 * @author   zhangjunfeng
 */
@Controller
@RequestMapping("system/home")
public class AppAction extends BaseAction {

	@Resource(name="homeAppApi")
	private IHomeAppApi homeAppApi;
	
	/** 
	* @Title: getHomeAppData 
	* @Description: TODO(获取首页关注应用数据)
	* @return JSONObject    返回类型 
	* @throws 
	*/
	@RequestMapping("getHomeAppData")
	public JSONObject getHomeAppData(String resourceId){
		Map<String, Object> resourceDetail = homeAppApi.getHomeAppData(resourceId);
		return toSuccess(resourceDetail);
	}
	
	
	/** 
	* @Title: getResourceMetricList 
	* @Description: TODO(通过资源ID获取资源性能指标列表)
	* @param resourctId
	* @return
	* @return JSONObject    返回类型 
	* @throws 
	*/
	@RequestMapping("getResourceMetricList")
	public JSONObject getResourceMetricList(String resourctId){
		List<Map<String, Object>> metricList = homeAppApi.getMetricList(resourctId);
		return toSuccess(metricList);
	}
}
