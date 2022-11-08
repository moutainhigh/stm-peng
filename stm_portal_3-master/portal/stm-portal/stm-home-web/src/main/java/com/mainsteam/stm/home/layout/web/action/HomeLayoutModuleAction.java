package com.mainsteam.stm.home.layout.web.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.home.layout.api.HomeLayoutModuleApi;
import com.mainsteam.stm.home.layout.bo.HomeLayoutModuleBo;
import com.mainsteam.stm.home.layout.bo.HomeLayoutModuleConfigBo;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;

/**
 * <li>文件名称: HomeLayoutModuleAction.java</li>
 * <li>公 司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 下午4:43:00
 * @author dengfuwei
 */
@Controller
@RequestMapping("system/home/layout/module")
public class HomeLayoutModuleAction extends BaseAction {

	@Resource
	private HomeLayoutModuleApi homeLayoutModuleApi;

	@RequestMapping("getModule")
	public JSONObject getModule() {
		List<HomeLayoutModuleBo> list = homeLayoutModuleApi.get();
		Map<String, Object> result = new HashMap<>();
		result.put("list", list);
		return toSuccess(result);
	}

	/**
	 * 根据layout ID 获取与其相关的所有模块
	 * 
	 * @param layoutId
	 * @return
	 */
	@RequestMapping("getByLayout")
	public JSONObject getModuleByLayoutId(Long layoutId) {
		List<HomeLayoutModuleConfigBo> list = homeLayoutModuleApi.queryByLayoutId(layoutId);

		Map<String, Object> result = new HashMap<>();
		result.put("list", list);
		
		return toSuccess(result);
	}

	/**
	 * 增加模块
	 * 
	 * @param moduleConfig
	 * @return
	 */
	@RequestMapping("add")
	public JSONObject addModule(HomeLayoutModuleConfigBo moduleConfig) {
		Map<String, Object> result = new HashMap<>();
		
		result.put("success", false);
		if(moduleConfig.getLayoutId() == 0){
			result.put("success", false);
			result.put("msg", "布局Id不能为空");
		}
		
		 ILoginUser user = getLoginUser();
		 moduleConfig.setUserId(user.getId());
		
		Long ct = homeLayoutModuleApi.add(moduleConfig);
		if(ct > 0){
			result.put("success", true);
			result.put("id", ct);
		}
		
		return toSuccess(result);
	}

	/**
	 * 跟新模块配置信息
	 * 
	 * @param moduleConfig
	 * @return
	 */
	@RequestMapping("update")
	public JSONObject updateModule(HomeLayoutModuleConfigBo moduleConfig) {
		Map<String, Object> result = new HashMap<>();
		
		result.put("success", false);
		if(moduleConfig.getLayoutId() == 0){
			result.put("success", false);
			result.put("msg", "布局Id不能为空");
		}
		
		if(moduleConfig.getId() == 0){
			result.put("success", false);
			result.put("msg", "Id不能为空");
			return toSuccess(result);
		}
		
		try{
			homeLayoutModuleApi.update(moduleConfig);
			result.put("success", true);
		}catch(Exception e){
			result.put("success", false);
		}
		
		return toSuccess(result);
	}

	/**
	 * 删除一个模块
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("delete")
	public JSONObject deleteModule(Long id) {
		Map<String, Object> result = new HashMap<>();
		
		try{
			homeLayoutModuleApi.delete(id);
			result.put("success", true);
		}catch(Exception e){
			result.put("success", false);
		}
		
		return toSuccess(result);
	}

	/**
	 * 删除指定layout下所有关联的模块
	 * 
	 * @param layoutId
	 * @return
	 */
	@RequestMapping("deleteByLayout")
	public JSONObject deleteModuleByLayoutId(Long layoutId) {
Map<String, Object> result = new HashMap<>();
		try{
			homeLayoutModuleApi.deleteByLayoutId(layoutId);;
			result.put("success", true);
		}catch(Exception e){
			result.put("success", false);
		}
		
		return toSuccess(result);
	}
}
