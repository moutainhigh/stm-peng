package com.mainsteam.stm.portal.netflow.web.action;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.netflow.api.IIpGroupApi;
import com.mainsteam.stm.portal.netflow.bo.IpGroupBo;

@Controller
@RequestMapping("netflow/ipGroup")
public class IpGroupAction extends BaseAction {

	@Autowired
	private IIpGroupApi ipGroupApi;

	@RequestMapping("save")
	public JSONObject save(Integer id, String name, String ips,
			String description) {
		int u = 0;
		if (id == null) {
			u = this.ipGroupApi.save(name, ips, description);
		} else {
			u = this.ipGroupApi.update(id, name, ips, description);
		}
		return super.toSuccess(u);
	}

	@RequestMapping("list")
	public JSONObject list(int startRow, int rows, String ipGroupName,
			String order) {
		IpGroupBo ipgbo = new IpGroupBo();
		if (ipGroupName != null && !"".equals(ipGroupName)) {
			ipgbo.setDescription(ipGroupName);
		}
		if (order != null && !"".equals(order)) {
			ipgbo.setIps(order);
		}
		Page<IpGroupBo, IpGroupBo> page = new Page<>(startRow, rows, ipgbo);
		List<IpGroupBo> list = this.ipGroupApi.list(page);
		page.setDatas(list);
		return super.toSuccess(page);
	}

	@RequestMapping("del")
	public JSONObject del(int[] ids) {
		return super.toSuccess(this.ipGroupApi.del(ids));
	}

	@RequestMapping("get")
	public JSONObject get(int id) {
		return super.toSuccess(this.ipGroupApi.get(id));
	}

	@RequestMapping("getCount")
	public JSONObject getCount(String name, Integer id) {
		return super.toSuccess(this.ipGroupApi.getCount(name, id));
	}
}
