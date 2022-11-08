package com.mainsteam.stm.portal.netflow.web.action;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.netflow.api.IDeviceApi;
import com.mainsteam.stm.portal.netflow.bo.ResourceDeviceBo;
import com.mainsteam.stm.portal.netflow.bo.ResourceInterfaceBo;

@Controller
@RequestMapping("/netflow/device")
public class DeviceAction extends BaseAction {

	@Autowired
	private IDeviceApi deviceApi;

	@RequestMapping("list")
	public JSONObject list(int startRow, int rowCount, String sort,
			String deviceSearchname, String order) {
		ResourceDeviceBo rdbo = new ResourceDeviceBo();
		if (sort != null && !"".equals(sort)) {
			rdbo.setName(sort);// 排序的列的名字
		}
		if (order != null && !"".equals(order)) {
			rdbo.setType(order);// 排序的顺序
		}
		if (deviceSearchname != null && !"".equals(deviceSearchname)) {
			rdbo.setDeviceType(deviceSearchname);// 查询条件的设备名称
		}
		Page<ResourceDeviceBo, ResourceDeviceBo> page = new Page<>(startRow,
				rowCount, rdbo);
		return super.toSuccess(this.deviceApi.list(page));
	}

	@RequestMapping("saveCheck")
	public JSONObject save(String[] infos) {
		if (this.deviceApi.save(infos)) {
			return super.toSuccess(1);
		}
		return super.toJsonObject(220, "设备ip已经添加，不能重复添加!");
	}

	@RequestMapping("confList")
	public JSONObject confList() {
		List<ResourceDeviceBo> data = this.deviceApi.getAllResourceDevice();
		Page<ResourceDeviceBo, ResourceDeviceBo> page = new Page<>(0,
				data.size());
		page.setDatas(data);
		page.setTotalRecord(data.size());
		return super.toSuccess(page);
	}

	@RequestMapping("confInterface")
	public JSONObject confInterface(int resourceId, String resourceType,
			int startRow, int rowCount) {
		List<ResourceInterfaceBo> data = this.deviceApi.getIntercface(
				resourceId, resourceType);
		Page<ResourceInterfaceBo, ResourceInterfaceBo> page = new Page<>(
				startRow, rowCount);
		page.setDatas(data.subList(startRow,
				(startRow + rowCount) > data.size() ? data.size() : startRow
						+ rowCount));
		page.setTotalRecord(data.size());
		return super.toSuccess(page);
	}

	@RequestMapping("addInterface")
	public JSONObject addInterface(int deviceId, String infos) {
		List<ResourceInterfaceBo> data = new ArrayList<>();
		JSONArray array = JSONArray.parseArray(infos);
		for (int i = 0; i < array.size(); i++) {
			JSONObject obj = array.getJSONObject(i);
			ResourceInterfaceBo bo = new ResourceInterfaceBo();
			data.add(bo);
			bo.setId(obj.getLongValue("id"));
			bo.setName(obj.getString("name"));
			bo.setIndex(obj.getIntValue("index"));
			bo.setIfSpeed(obj.getLong("ifSpeed"));
		}
		return super.toSuccess(this.deviceApi.addInterface(deviceId, data));
	}

	@RequestMapping("delInterface")
	public JSONObject delInterface(int[] interfaceIds) {
		if (interfaceIds != null && interfaceIds.length > 0) {
			return super.toSuccess(this.deviceApi.delInterface(interfaceIds));
		}
		return super.toSuccess(1);
	}

	@RequestMapping("delDevice")
	public JSONObject delDevice(int[] ids) {
		return super.toSuccess(this.deviceApi.delDevice(ids));
	}
}
