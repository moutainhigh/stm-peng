package com.mainsteam.stm.portal.netflow.web.action;

import java.util.regex.Pattern;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.netflow.api.IApplicationManagerApi;
import com.mainsteam.stm.portal.netflow.bo.ApplicationBo;

@Controller
@RequestMapping("netflow/applicationManager")
public class ApplicationManagerAction extends BaseAction {

	private static final Pattern FORMAT = Pattern.compile("^,+$",
			Pattern.CASE_INSENSITIVE);

	@Autowired
	private IApplicationManagerApi applicationManagerApi;

	@RequestMapping("protocols")
	public JSONObject protocols() {
		return super.toSuccess(this.applicationManagerApi.getAllProtocols());
	}

	@RequestMapping("save")
	public JSONObject save(Integer id, String name, int protocolId,
			String ports, String ips) {
		int u = 0;
		try {
			if (id == null) {
				u = this.applicationManagerApi.save(name, protocolId, ports,
						ips);
			} else {
				u = this.applicationManagerApi.update(id, name, protocolId,
						ports, ips);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return super.toSuccess(u);

	}

	@RequestMapping("list")
	public JSONObject list(int startRow, int rows, String applicationName,
			String applicationFirstName, String order) {
		ApplicationBo appBo = new ApplicationBo();
		if (applicationName != null && !"".equals(applicationName)) {
			appBo.setProtocolName(applicationName);
		}
		if (order != null && !"".equals(order)) {
			appBo.setPorts(order);
		}
		if (applicationFirstName != null && !"".equals(applicationFirstName)) {
			appBo.setIps(applicationFirstName);
		}
		Page<ApplicationBo, ApplicationBo> page = this.applicationManagerApi
				.list(new Page<ApplicationBo, ApplicationBo>(startRow, rows,
						appBo));
		if (page.getDatas() != null) {
			for (ApplicationBo app : page.getDatas()) {
				if (app.getIps() != null && FORMAT.matcher(app.getIps()).find()) {
					app.setIps("");
				}
			}
		}
		return super.toSuccess(page);
	}

	@RequestMapping("del")
	public JSONObject del(int[] ids) {
		return super.toSuccess(this.applicationManagerApi.del(ids));
	}

	@RequestMapping("get")
	public JSONObject get(int id) {
		return super.toSuccess(this.applicationManagerApi.get(id));
	}

	@RequestMapping("restoreDefault")
	public JSONObject restoreDefault() {
		return super.toSuccess(applicationManagerApi.restoreDefault());
	}

	@RequestMapping("getCount")
	public JSONObject getCount(String name, Integer id) {
		return super.toSuccess(this.applicationManagerApi.getCount(name, id));
	}
}
