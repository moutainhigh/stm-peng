package com.mainsteam.stm.topo.web.action;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.home.screen.api.ITopoApi;
import com.mainsteam.stm.license.License;
import com.mainsteam.stm.license.LicenseCheckException;//zw
import com.mainsteam.stm.license.LicenseModelEnum;
import com.mainsteam.stm.license.calc.api.ILicenseCalcService;
import com.mainsteam.stm.platform.system.config.service.ISystemConfigApi;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.topo.api.HelperService;
import com.mainsteam.stm.topo.api.ThirdService;
/**
 * 页面必要参数请求帮组控制器
 * @author 富强
 *
 */
@Controller
@RequestMapping(value="topo/help")
public class HelperController extends BaseAction{
	@Autowired
	private HelperService helperService;
	@Autowired
	private ThirdService tsvc;
	@Autowired
	@Qualifier(value="homeScreenTopoApi")
	private ITopoApi topoApi;
	@Autowired
	private ISystemConfigApi configApi;
	@Autowired
	@Qualifier("licenseCalcService")
	private ILicenseCalcService license;
	/**
	 * 当前登录用户的信息
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="userinfo",method={RequestMethod.GET,RequestMethod.POST})
	public JSONObject userinfo(){
		JSONObject retn = new JSONObject();
		ILoginUser user = getLoginUser();
		retn.put("name", user.getName());
		retn.put("account", user.getAccount());
		@SuppressWarnings("deprecation")
		JSONObject info = tsvc.extractDomainInfo();
		//合并
		for(Map.Entry<String,Object> entry : info.entrySet()){
			retn.put(entry.getKey(), entry.getValue());
		}
		return toSuccess(retn);
	}
	/**
	 * 当前主题
	 * @return
	 */
	@ResponseBody
	@RequestMapping(value="currentTheme",method={RequestMethod.GET,RequestMethod.POST})
	public JSONObject getCurrentTheme(){
		String theme=configApi.getCurrentSkin();
		JSONObject retn = new JSONObject();
		retn.put("status", 200);
		retn.put("theme", theme);
		return retn;
	}
	/**
	 * 获取当前licence情况
	 */
	@ResponseBody
	@RequestMapping(value="lic",method={RequestMethod.GET,RequestMethod.POST})
	public JSONObject lic(String categoryId){
		JSONObject retn = new JSONObject();
		retn.put("status", 200);
		boolean status = license.isLicenseEnough(LicenseModelEnum.stmMonitorTopMr);
		retn.put("toporoom", status);
		if(categoryId!=null){
			retn.put(categoryId, license.isLicenseEnough(categoryId));
		}
		try {
			retn.put("toporoom_enabled", License.checkLicense().checkModelAvailableNum(LicenseModelEnum.stmModelTopoMr));
		} catch (LicenseCheckException e) {
			retn.put("toporoom_enabled",e.getMessage());
		}
		return retn;
	}
	/**
	 * 拓扑初始化的一些信息
	 */
	@ResponseBody
	@RequestMapping(value="initTopo",method={RequestMethod.GET,RequestMethod.POST})
	public void initTopo(){
		helperService.initTopo();
	}
}
