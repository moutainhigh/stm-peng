package com.mainsteam.stm.knowledge.modelset.web.action;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.dict.CaplibAPIResult;
import com.mainsteam.stm.knowledge.modelset.api.IModelSetApi;
import com.mainsteam.stm.knowledge.modelset.bo.ModuleBo;
import com.mainsteam.stm.knowledge.modelset.bo.ModuleQueryBo;
import com.mainsteam.stm.knowledge.modelset.constants.ModelSetConstants;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.system.resource.api.IResourceApi;
import com.mainsteam.stm.system.resource.bo.CategoryBo;
import com.mainsteam.stm.system.resource.bo.ResourceModuleBo;

/**
 * <li>文件名称: com.mainsteam.stm.knowledge.modelset.web.action.ModelSetAction.java</li>
 * <li>文件描述: 本类描述</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年12月2日
 */
@RequestMapping(value="/knowledge/modelset")
@Controller
public class ModelSetAction extends BaseAction{

	@Autowired
	private IModelSetApi modelSetApi;
	@Autowired
	private IResourceApi resourceApi;
	
	@RequestMapping(value="/page")
	public JSONObject page(Page<ModuleBo, ModuleQueryBo> page, ModuleQueryBo condition){
		page.setCondition(condition);
		modelSetApi.getPage(page);
		return toSuccess(page);
	}
	
	@RequestMapping("/getAllModules")
	@ResponseBody
	public Map<String, String> getAllModules(){
		List<ResourceModuleBo> moduleBos = resourceApi.getModules(ModelSetConstants.RESOURCE_TYPE_HOST);
		moduleBos.addAll(resourceApi.getModules(ModelSetConstants.RESOURCE_TYPE_NETDEVICE));
		Map<String, String> moduleMap = new HashMap<String, String>();
		for(ResourceModuleBo moduleBo : moduleBos){
			moduleMap.put(moduleBo.getId(), moduleBo.getName());
		}
		return moduleMap;
	}
	
	@RequestMapping(value="/getFirstResourceType")
	@ResponseBody
	public CategoryBo[] getFirstResourceType(String type){
		return resourceApi.getTreeCategory(type).getChildren();
	}
	
	@RequestMapping(value="/getModuleType")
	@ResponseBody
	public List<ResourceModuleBo> getModuleType(String categoryId){
		return resourceApi.getModules(categoryId);
	}
	
	@RequestMapping(value="/save")
	public JSONObject save(ModuleBo moduleBo){
		CaplibAPIResult result = modelSetApi.save(moduleBo);
		return toSuccess(result != null ? result : -1);
	}
	
	@RequestMapping(value="/checkSysOid")
	@ResponseBody
	public Boolean checkSysOid(String sysOid){
		return modelSetApi.getModuleBoBysisOid(sysOid);
	}
	
	@RequestMapping(value="/delete")
	public JSONObject delete(String sysOid){
		CaplibAPIResult result = modelSetApi.delete(sysOid);
		return toSuccess(result != null ? result : -1);
	}
}
