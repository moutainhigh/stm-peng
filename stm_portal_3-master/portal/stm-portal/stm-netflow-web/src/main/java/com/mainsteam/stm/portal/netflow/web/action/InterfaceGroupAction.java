package com.mainsteam.stm.portal.netflow.web.action;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.netflow.api.IInterfaceGroupApi;
import com.mainsteam.stm.portal.netflow.bo.zTreeBo;
import com.mainsteam.stm.portal.netflow.web.vo.InterfaceGroupPageVo;

@Controller
@RequestMapping("/netflow/interfaceGroup")
public class InterfaceGroupAction extends BaseAction {

	@Autowired
	private IInterfaceGroupApi interfaceGroupApi;

	@RequestMapping("/deviceInterface")
	public JSONObject deviceInterface(String name, int[] notIds, int[] ids) {
		if ("r".equals(BaseAction.getHttpServletRequest().getParameter("f"))
				&& (ids == null || ids.length == 0)) {
			return super.toSuccess(new ArrayList<zTreeBo>());
		}
		return super.toSuccess(this.interfaceGroupApi
				.queryDeviceInterface(name, notIds != null
						&& notIds.length == 0 ? null : notIds, ids));
	}

	@RequestMapping("/save")
	public JSONObject save(Integer id, String name, String interfaceIds,
			String description) {
		int u = 0;
		if (id == null) {
			u = this.interfaceGroupApi.save(name, interfaceIds, description);
		} else {
			u = this.interfaceGroupApi.update(id, name, interfaceIds,
					description);
		}
		return super.toSuccess(u);
	}

	@RequestMapping("/list")
	public JSONObject list(Integer startRow, Integer rows,
			String interfaceGroupName, String order) {
		return super.toSuccess(new InterfaceGroupPageVo(this.interfaceGroupApi
				.pageSelect(startRow, rows, interfaceGroupName, order))
				.toPage());
	}

	@RequestMapping("/del")
	public JSONObject del(int[] ids) {
		return super.toSuccess(this.interfaceGroupApi.del(ids));
	}

	@RequestMapping("/get")
	public JSONObject get(int id) {
		return super.toSuccess(this.interfaceGroupApi.get(id));
	}

	@RequestMapping("getCount")
	public JSONObject getCount(String name, Integer id) {
		return super.toSuccess(this.interfaceGroupApi.getCount(name, id));
	}
}
