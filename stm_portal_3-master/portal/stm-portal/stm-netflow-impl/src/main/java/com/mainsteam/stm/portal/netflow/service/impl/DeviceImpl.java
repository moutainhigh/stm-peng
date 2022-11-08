package com.mainsteam.stm.portal.netflow.service.impl;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;

import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.DeviceType;
import com.mainsteam.stm.common.instance.obj.InstanceStateEnum;
import com.mainsteam.stm.common.metric.InstanceStateService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.interceptor.InstancelibEvent;
import com.mainsteam.stm.instancelib.interceptor.InstancelibListener;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.instancelib.objenum.EventEnum;
import com.mainsteam.stm.license.License;
import com.mainsteam.stm.license.LicenseCheckException;
import com.mainsteam.stm.license.LicenseModelEnum;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.api.IDeviceApi;
import com.mainsteam.stm.portal.netflow.bo.ResourceDeviceBo;
import com.mainsteam.stm.portal.netflow.bo.ResourceInterfaceBo;
import com.mainsteam.stm.portal.netflow.dao.IDeviceDao;
import com.mainsteam.stm.state.obj.InstanceStateData;

public class DeviceImpl implements IDeviceApi, InstancelibListener {

	private static final String[] TYPES = { "Switch", "Router", "Firewall"/*
																		 * ,
																		 * "WirelessAP"
																		 */};// 交换机、路由器、防火墙、无线ap

	private IDeviceDao deviceDao;

	private ResourceInstanceService resourceInstanceService;

	private CapacityService capacityService;

	@Autowired
	private InstanceStateService instanceStateService;

	public void setInstanceStateService(
			InstanceStateService instanceStateService) {
		this.instanceStateService = instanceStateService;
	}

	public void setCapacityService(CapacityService capacityService) {
		this.capacityService = capacityService;
	}

	public void setResourceInstanceService(
			ResourceInstanceService resourceInstanceService) {
		this.resourceInstanceService = resourceInstanceService;
	}

	public void setDeviceDao(IDeviceDao deviceDao) {
		this.deviceDao = deviceDao;
	}

	@Override
	public List<ResourceDeviceBo> getAllResourceDevice() {
		List<ResourceDeviceBo> data = new ArrayList<>();
		List<Map<String, Object>> listMap = this.deviceDao.getDevice();
		Map<String, Object> ipsMap = new HashMap<>();
		if (listMap != null) {
			for (Map<String, Object> map : listMap) {
				ipsMap.put(String.valueOf(map.get("ip")), true);
			}
		}
		for (int i = 0; i < TYPES.length; i++) {
			try {
				List<ResourceInstance> list = this.resourceInstanceService
						.getParentInstanceByCategoryId(TYPES[i]);
				if (list != null) {
					for (ResourceInstance r : list) {
						if (ipsMap.get(r.getDiscoverIP()) != null) {
							continue;
						}
						ResourceDeviceBo d = new ResourceDeviceBo();
						d.setId(r.getId());
						d.setName(r.getName());
						d.setIp(r.getDiscoverIP());
						d.setType(TYPES[i]);
						String[] sysObjectIds = r
								.getModulePropBykey("sysObjectID");
						if (sysObjectIds != null && sysObjectIds.length > 0
								&& sysObjectIds[0] != null) {
							DeviceType dt = this.capacityService
									.getDeviceType(sysObjectIds[0]);
							if (dt != null && dt.getVendorName() != null) {
								d.setManufacturers(dt.getVendorName());
							}
						}
						if (!data.contains(d)) {
							data.add(d);
						}
					}
				}
			} catch (InstancelibException e) {
				e.printStackTrace();
			}
		}
		return data;
	}

	@Override
	public List<ResourceInterfaceBo> getIntercface(long id, String type) {
		Map<Long, ResourceInterfaceBo> mapIn = new HashMap<>();
		try {
			ResourceInstance resourceInstance = resourceInstanceService
					.getResourceInstance(id);
			if (resourceInstance != null
					&& resourceInstance.getChildren() != null) {
				List<ResourceInstance> children = resourceInstance
						.getChildren();
				for (ResourceInstance in : children) {
					ResourceInterfaceBo inter = new ResourceInterfaceBo();
					inter.setId(in.getId());
					inter.setName(in.getName());
					String[] values = in.getModulePropBykey("ifIndex");
					if (values != null) {
						inter.setIndex(Integer.parseInt(values[0]));
						String[] ifSpeeds = in.getModulePropBykey("ifSpeed");
						inter.setIfSpeed(ifSpeeds == null
								|| ifSpeeds.length == 0 ? null
								: (new BigDecimal(ifSpeeds[0])).longValue());
						mapIn.put(inter.getId(), inter);
					}
					InstanceStateData isd = instanceStateService.getState(in
							.getId());
					if (isd != null) {
						if (isd.getState() != null) {
							if (isd.getState() != InstanceStateEnum.CRITICAL
									&& isd.getState() != InstanceStateEnum.UNKOWN) {
								inter.setAvailable(true);
							}
						}
					}
				}
			}
		} catch (InstancelibException e) {
			e.printStackTrace();
		}
		List<ResourceInterfaceBo> data = new ArrayList<>();
		if (mapIn.size() > 0) {
			List<Integer> selectIds = this.deviceDao
					.isInterface(mapIn.keySet());
			for (Integer interfaceId : selectIds) {
				mapIn.get(Long.parseLong(interfaceId + "")).setChecked(true);
			}
			Iterator<Long> keys = mapIn.keySet().iterator();
			while (keys.hasNext()) {
				Long k = keys.next();
				data.add(mapIn.get(k));
			}
		}
		Collections.sort(data, new Comparator<ResourceInterfaceBo>() {
			@Override
			public int compare(ResourceInterfaceBo o1, ResourceInterfaceBo o2) {
				if (o1.getChecked() && !o2.getChecked()) {
					return -1;
				} else if (!o1.getChecked() && o2.getChecked()) {
					return 1;
				}
				return o1.getIndex() - o2.getIndex();
			}
		});
		return data;
	}

	@Override
	public boolean save(String[] infos) {
		for (String info : infos) {
			String[] objArray = info.split("\\|");
			try {
				this.deviceDao.save(objArray[1], objArray[2], objArray[3],
						Integer.parseInt(objArray[0]), objArray[4]);
			} catch (Exception e) {
				this.deviceDao.updateSaveDeviceByManager(objArray[1],
						objArray[2], objArray[3],
						Integer.parseInt(objArray[0]), objArray[4]);
			}
		}
		return true;
	}

	@Override
	public Page<ResourceDeviceBo, ResourceDeviceBo> list(
			Page<ResourceDeviceBo, ResourceDeviceBo> page) {
		return this.deviceDao.list(page);
	}

	@Override
	public int addInterface(int deviceId, List<ResourceInterfaceBo> data) {
		int state = 1;
		try {
			License lic = License.checkLicense();
			for (int i = 0; i < data.size(); i++) {
				int num = 0;
				try {
					num = lic
							.checkModelAvailableNum(LicenseModelEnum.oc4MonitorFlow
									.toString());
				} catch (Exception e) {
					return i == 0 ? -1 : -2;// -1没有License，全部接口未添加，-2没有License，部分接口未添加
				}
				if (num <= this.deviceDao.queryInterfaceCount()) {
					return -3;// 添加接口已到上限
				}
				ResourceInterfaceBo bo = data.get(i);
				try {
					this.deviceDao.addInterface(deviceId, (int) bo.getId(),
							bo.getName(), bo.getIndex(), bo.getIfSpeed());
				} catch (Exception e) {
					this.deviceDao.updateInterface(deviceId, (int) bo.getId(),
							bo.getName(), bo.getIndex(), bo.getIfSpeed());
					continue;
				}
			}
		} catch (LicenseCheckException e1) {
			state = -1;// -1没有License，全部接口未添加
		}
		return state;
	}

	@Override
	public int delInterface(int[] interfaceIds) {
		return this.deviceDao.delInterface(interfaceIds);
	}

	@Override
	public int delDevice(int[] ids) {
		int u = this.deviceDao.delDeviceByManagerByInterface(ids);
		this.deviceDao.delDevice(ids);
		return u;
	}

	@Override
	public void listen(InstancelibEvent event) throws Exception {
		if (event.getEventType() == EventEnum.INSTANCE_DELETE_EVENT) {
			@SuppressWarnings("unchecked")
			List<Long> deleteIds = (List<Long>) event.getSource();
			if (deleteIds != null && deleteIds.size() > 0) {
				int[] ids = new int[deleteIds.size()];
				for (int i = 0; i < deleteIds.size(); i++) {
					ids[i] = Integer.parseInt(deleteIds.get(i) + "");
				}
				this.delDevice(ids);
			}
		}
	}
}
