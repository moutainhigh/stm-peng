package com.mainsteam.stm.camera.web.action;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.camera.api.ICameraMonitorService;
import com.mainsteam.stm.camera.bo.CameraResourceBo;
import com.mainsteam.stm.camera.bo.CameraResourcePageBo;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.instancelib.CustomPropService;
import com.mainsteam.stm.instancelib.DiscoverPropService;
import com.mainsteam.stm.instancelib.ModulePropService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.system.um.domain.api.IDomainApi;
import com.mainsteam.stm.system.um.user.api.IUserApi;

@Controller
@RequestMapping({ "/portal/resource/cameraMonitor" })
public class CameraMonitorAction extends BaseAction{

	private static Logger logger = Logger.getLogger(CameraMonitorAction.class);
	
	@Resource
	private CapacityService capacityService;
	
	@Resource
	private ResourceInstanceService resourceInstanceService;
	
	@Resource
	private IUserApi userApi;
	
	@Resource
	private IDomainApi domainApi;
	
	@Resource
	private CustomPropService customPropService;
	
	@Resource
	private ModulePropService modulePropService;
	
	@Resource
	private DiscoverPropService discoverPropService;
	
	@Resource
	private ICameraMonitorService cameraMonitorService;
	
	@RequestMapping({ "/getHaveMonitorCamera" })
	public JSONObject getHaveMonitorCamera(CameraResourcePageBo pageBo,
			HttpSession session) {
		ILoginUser user = getLoginUser(session);
		try {
			CameraResourceBo crbo = pageBo.getCondition();
			pageBo = cameraMonitorService.getHaveMonitorCamera(user,
					pageBo.getStartRow(), pageBo.getRowCount(),
                        crbo.getResourceStatus(), crbo.getiPorName(),
					crbo.getDomainId(), pageBo.getSort(),
					pageBo.getOrder());

		} catch (Exception e) {
			logger.error("getHaveMonitorCamera:", e);
		}
		return toSuccess(pageBo);
	}
	
	@RequestMapping({ "/getNotMonitorCamera" })
	public JSONObject getNotMonitorCamera(CameraResourcePageBo pageBo,
			HttpSession session) {
		ILoginUser user = getLoginUser(session);
		try {
			CameraResourceBo crbo = pageBo.getCondition();
			if(crbo == null) {
				crbo = new CameraResourceBo();
			}
			pageBo = cameraMonitorService.getNotMonitorCamera(user,
					pageBo.getStartRow(), pageBo.getRowCount(),
					crbo.getiPorName(), crbo.getDomainId(),
					pageBo.getSort(), pageBo.getOrder());

		} catch (Exception e) {
			logger.error("getHaveMonitorCamera:", e);
		}
		return toSuccess(pageBo);
	}
	
}
