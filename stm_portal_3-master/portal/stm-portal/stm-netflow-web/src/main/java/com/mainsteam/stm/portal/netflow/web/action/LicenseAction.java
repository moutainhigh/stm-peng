package com.mainsteam.stm.portal.netflow.web.action;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.license.License;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.netflow.api.IConfLicenseApi;
import com.mainsteam.stm.portal.netflow.bo.ConfLicense;
import com.mainsteam.stm.portal.netflow.util.RedisUtil;

@Controller
@RequestMapping("netflow/license")
public class LicenseAction extends BaseAction {

	@Autowired
	private IConfLicenseApi confLicenseApi;

	@RequestMapping("register")
	public JSONObject register(String ip, int port) {
		int result = 0;
		try {
			License lic = License.checkLicense();
			Date date = lic.getValidDate();
			String dateStr = lic.getValidDate() == null ? ""
					: new SimpleDateFormat("yyyyMMdd").format(date);
			String license = dateStr
					+ "#00:00:00:00:00:00##[NTAS]NF_ANA:ON#NF_DEV_NUMBER:10#NF_IF_NUMBER:100#NAT_LOG:OFF#NAT_LOG_FPS:10000#NAT_LOG_SIZE:20[/NTAS]";
			RedisUtil.setLicense(ip, port, license);
			ConfLicense confLicense = new ConfLicense();
			confLicense.setId(1);
			confLicense.setIp(ip);
			confLicense.setPort(port);
			this.confLicenseApi.updateLicense(confLicense);
			result = 1;
		} catch (Exception e) {
		}
		return super.toSuccess(result);
	}

	@RequestMapping("getConf")
	public JSONObject getConf() {
		return super.toSuccess(this.confLicenseApi.getLicense(1));
	}
}
