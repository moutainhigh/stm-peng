package com.mainsteam.stm.portal.vm.web.action;

import java.util.List;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.vm.api.VmProfileService;

@Controller
@RequestMapping("/portal/vm/vmProfileAction")
public class VmProfileAction extends BaseAction {

	private static final Log logger = LogFactory.getLog(VmProfileAction.class);
	
	@Resource
	private VmProfileService vmProfileService;

	/**
	 * 获取虚拟化资源类别
	 * @return
	 */
	@RequestMapping("/getVmResourceCategory")
	public JSONObject getVmResourceCategory(){
		
		List<CategoryDef> defs = vmProfileService.getVmCategoryDef();
		
		return toSuccess(defs);
		
	}
	
}
