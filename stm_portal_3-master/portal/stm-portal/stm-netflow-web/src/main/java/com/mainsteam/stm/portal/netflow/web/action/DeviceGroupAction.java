package com.mainsteam.stm.portal.netflow.web.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.netflow.api.IDeviceGroupApi;
import com.mainsteam.stm.portal.netflow.bo.ConfDeviceBo;
import com.mainsteam.stm.portal.netflow.bo.DeviceGroupBo;
import com.mainsteam.stm.portal.netflow.web.vo.zTreeVo;

@Controller
@RequestMapping("/netflow/deviceGroup")
public class DeviceGroupAction extends BaseAction {

	@Resource
	private IDeviceGroupApi deviceGroupApi;

	@RequestMapping("getAllDevice")
	public JSONObject getAllDevice(String name, int[] notIds) {
		List<ConfDeviceBo> list = this.deviceGroupApi.getAllDevice(name,
				notIds != null && notIds.length == 0 ? null : notIds);
		List<zTreeVo> zTreeVos = new ArrayList<>();
		for (ConfDeviceBo bo : list) {
			zTreeVo tree = new zTreeVo();
			zTreeVos.add(tree);
			tree.setId(bo.getId() + "");
			tree.setName(bo.getName());
		}
		return super.toSuccess(zTreeVos);
	}

	@RequestMapping("getRDevice")
	public JSONObject getRDevice(int[] ids) {
		List<zTreeVo> zTreeVos = new ArrayList<>();
		if (ids != null && ids.length > 0) {
			List<ConfDeviceBo> list = this.deviceGroupApi.getDevice(ids);
			for (ConfDeviceBo bo : list) {
				zTreeVo tree = new zTreeVo();
				zTreeVos.add(tree);
				tree.setId(bo.getId() + "");
				tree.setName(bo.getName());
			}
		}
		return super.toSuccess(zTreeVos);
	}

	@RequestMapping("save")
	public JSONObject save(Integer id, String name, String description,
			String deviceIds) {
		int u = 0;
		if (id == null) {
			u = this.deviceGroupApi.save(name, description, deviceIds);
		} else {
			u = this.deviceGroupApi.update(id, name, description, deviceIds);
		}
		return super.toSuccess(u);
	}

	@RequestMapping("list")
	public JSONObject list(int startRow, int rows, String deviceGroupName,
			String order) {
		DeviceGroupBo dgbo = new DeviceGroupBo();
		if (deviceGroupName != null && !"".equals(deviceGroupName)) {
			dgbo.setDescription(deviceGroupName);
		}
		if (order != null && !"".equals(order)) {
			dgbo.setDeviceIds(order);
		}
		Page<DeviceGroupBo, DeviceGroupBo> page = new Page<>(startRow, rows,
				dgbo);
		page.setDatas(this.deviceGroupApi.list(page));
		return super.toSuccess(page);
	}

	@RequestMapping("del")
	public JSONObject del(int[] ids) {
		return super.toSuccess(this.deviceGroupApi.del(ids));
	}

	@RequestMapping("get")
	public JSONObject get(int id) {
		return super.toSuccess(this.deviceGroupApi.get(id));
	}

	@RequestMapping("getCount")
	public JSONObject getCount(String name, Integer id) {
		return super.toSuccess(this.deviceGroupApi.getCount(name, id));
	}
}
