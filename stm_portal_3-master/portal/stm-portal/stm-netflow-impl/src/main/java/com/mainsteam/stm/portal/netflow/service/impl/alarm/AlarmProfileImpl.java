package com.mainsteam.stm.portal.netflow.service.impl.alarm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.portal.netflow.api.alarm.IAlarmProfileApi;
import com.mainsteam.stm.portal.netflow.bo.alarm.AlarmDataGridListBo;
import com.mainsteam.stm.portal.netflow.bo.alarm.AlarmProfileBo;
import com.mainsteam.stm.portal.netflow.bo.alarm.AlarmResourceBo;
import com.mainsteam.stm.portal.netflow.bo.alarm.AlarmThresholdBo;
import com.mainsteam.stm.portal.netflow.bo.alarm.InterfaceTreeBo;
import com.mainsteam.stm.portal.netflow.dao.alarm.IAlarmProfileDao;
import com.mainsteam.stm.profilelib.AlarmRuleService;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRule;
import com.mainsteam.stm.profilelib.alarm.obj.AlarmRuleProfileEnum;

public class AlarmProfileImpl implements IAlarmProfileApi {

	private IAlarmProfileDao AlarmDao;
	@Resource
	private AlarmRuleService alarmRuleService;

	@Override
	public Page<AlarmDataGridListBo, Map<String, String>> getAllAlarms(
			String RowCount, String StartRow, String AlarmsName, String order) {
		Map<String, String> map = new HashMap<String, String>();
		map.put("RowCount", RowCount);
		map.put("StartRow", StartRow);
		if (AlarmsName != null && !"".equals(AlarmsName)) {
			map.put("AlarmsName", AlarmsName);
		}
		if (order != null && !"".equals(order)) {
			map.put("order", order);
		}
		Page<AlarmDataGridListBo, Map<String, String>> page = new Page<AlarmDataGridListBo, Map<String, String>>(
				new Long(StartRow).longValue(), new Long(RowCount).longValue(),
				map);
		List<AlarmDataGridListBo> list = AlarmDao.getAllAlarms(page);
		for (AlarmDataGridListBo bo : list) {
			int profileId = bo.getId().intValue();
			int allCountAlarms = AlarmDao.getAllCountAlarms(profileId);
			int oneHoureAlarms = AlarmDao.getOneHoureAlarms(profileId);
			bo.setAllCountAlarm(String.valueOf(allCountAlarms));
			bo.setOneHoureAlarm(String.valueOf(oneHoureAlarms));
		}
		return page;
	}

	@Override
	public List<AlarmResourceBo> getIPGroup() {

		return AlarmDao.getIPGroup();
	}

	@Override
	public List<AlarmResourceBo> loadIPGroups(String profileId, String position) {
		List<AlarmResourceBo> IPGroups = AlarmDao.getIPGroup();
		List<AlarmResourceBo> result = new ArrayList<AlarmResourceBo>();
		List<String> ids = AlarmDao.loadLocalIPGoups(profileId);
		if (position != null && "left".equals(position)) {// ??????????????????????????????????????????????????????????????????
			for (AlarmResourceBo bo : IPGroups) {
				if (ids.contains(bo.getId()) == false) {
					result.add(bo);
				}
			}
		} else {
			for (AlarmResourceBo bo : IPGroups) {// ???????????????????????????????????????????????????????????????
				if (ids.contains(bo.getId()) == true) {
					result.add(bo);
				}
			}
		}
		return result;
	}

	@Override
	public List<InterfaceTreeBo> getInterface() {
		List<InterfaceTreeBo> inftree = new ArrayList<InterfaceTreeBo>();
		List<AlarmResourceBo> devices = AlarmDao.getDevice();
		for (AlarmResourceBo bo : devices) {
			int id = new Integer(bo.getId());
			List<AlarmResourceBo> inf = AlarmDao.getInterface(id);
			if (inf != null && inf.size() > 0) {
				InterfaceTreeBo parentTree = new InterfaceTreeBo();
				List<InterfaceTreeBo> children = new ArrayList<InterfaceTreeBo>();
				parentTree.setId("p" + bo.getId());
				parentTree.setName(bo.getName());
				// parentTree.setChecked(false);
				parentTree.setNocheck(false);
				parentTree.setIsParent(true);
				parentTree.setOpen(true);
				parentTree.setPId("0");
				parentTree.setResourceIds(bo.getResourceIds());
				parentTree.setChildren(children);
				for (AlarmResourceBo child : inf) {
					InterfaceTreeBo childTree = new InterfaceTreeBo();
					childTree.setId("c" + child.getId());
					childTree.setName(child.getName());
					// parentTree.setChecked(false);
					childTree.setIsParent(false);
					childTree.setNocheck(false);
					childTree.setPId("p" + bo.getId());
					childTree.setResourceIds(bo.getResourceIds());
					children.add(childTree);
				}
				inftree.add(parentTree);
			}

		}
		return inftree;
	}

	public List<InterfaceTreeBo> loadInterfaces(String profileID,
			String position) {
		List<InterfaceTreeBo> inftree = new ArrayList<InterfaceTreeBo>();
		List<AlarmResourceBo> devices = AlarmDao.getDevice();
		List<String> localInterfaces = AlarmDao.loadLocalInterfaces(profileID);
		if (position != null && "left".equals(position)) {
			for (AlarmResourceBo bo : devices) {
				int id = new Integer(bo.getId());
				List<AlarmResourceBo> inf = AlarmDao.getInterface(id);
				if (inf != null && inf.size() > 0) {
					InterfaceTreeBo parentTree = new InterfaceTreeBo();
					List<InterfaceTreeBo> children = new ArrayList<InterfaceTreeBo>();
					parentTree.setId("p" + bo.getId());
					parentTree.setName(bo.getName());
					// parentTree.setChecked(false);
					parentTree.setNocheck(false);
					parentTree.setIsParent(true);
					parentTree.setOpen(true);
					parentTree.setPId("0");
					parentTree.setChildren(children);
					for (AlarmResourceBo child : inf) {
						if (localInterfaces.contains(child.getId()) == false) {// ???????????????????????????????????????????????????????????????????????????
							InterfaceTreeBo childTree = new InterfaceTreeBo();
							childTree.setId("c" + child.getId());
							childTree.setName(child.getName());
							// parentTree.setChecked(false);
							childTree.setIsParent(false);
							childTree.setNocheck(false);
							childTree.setPId("p" + bo.getId());
							children.add(childTree);
						}
					}
					if (children.size() > 0) {
						inftree.add(parentTree);
					}
				}

			}
		} else {
			for (AlarmResourceBo bo : devices) {
				int id = new Integer(bo.getId());
				List<AlarmResourceBo> inf = AlarmDao.getInterface(id);
				if (inf != null && inf.size() > 0) {
					InterfaceTreeBo parentTree = new InterfaceTreeBo();
					List<InterfaceTreeBo> children = new ArrayList<InterfaceTreeBo>();
					parentTree.setId("p" + bo.getId());
					parentTree.setName(bo.getName());
					parentTree.setNocheck(false);
					parentTree.setChecked(true);
					parentTree.setIsParent(true);
					parentTree.setOpen(true);
					parentTree.setPId("0");
					parentTree.setChildren(children);
					for (AlarmResourceBo child : inf) {
						if (localInterfaces.contains(child.getId()) == true) {// ????????????????????????????????????????????????????????????????????????
							InterfaceTreeBo childTree = new InterfaceTreeBo();
							childTree.setId("c" + child.getId());
							childTree.setName(child.getName());
							childTree.setChecked(true);
							childTree.setIsParent(false);
							childTree.setNocheck(false);
							childTree.setPId("p" + bo.getId());
							children.add(childTree);
						}
					}
					if (children.size() > 0) {
						inftree.add(parentTree);
					}
				}

			}
		}

		return inftree;
	}

	@Override
	public List<AlarmResourceBo> getInterfaceGroup() {
		return AlarmDao.getInterfaceGroup();
	}

	@Override
	public List<AlarmResourceBo> loadIFGroups(String profileId, String position) {
		List<AlarmResourceBo> IPGroups = AlarmDao.getInterfaceGroup();
		List<AlarmResourceBo> result = new ArrayList<AlarmResourceBo>();
		List<String> ids = AlarmDao.loadLocalIFGoups(profileId);
		if (position != null && "left".equals(position)) {// ??????????????????????????????????????????????????????????????????
			for (AlarmResourceBo bo : IPGroups) {
				if (ids.contains(bo.getId()) == false) {
					result.add(bo);
				}
			}
		} else {
			for (AlarmResourceBo bo : IPGroups) {// ???????????????????????????????????????????????????????????????
				if (ids.contains(bo.getId()) == true) {
					result.add(bo);
				}
			}
		}
		return result;
	}

	@Override
	public List<AlarmResourceBo> getProtocol() {
		return AlarmDao.getProtocol();
	}

	@Override
	public List<AlarmResourceBo> getApplication() {
		return AlarmDao.getApplication();
	}

	@Override
	public int addAlarmBasic(AlarmProfileBo apf) {
		return AlarmDao.addAlarmBasic(apf);
	}

	@Override
	public int updateAlarmBasic(AlarmProfileBo apf) {
		return AlarmDao.updateAlarmBasic(apf);
	}

	@Override
	public int addAlarmResource(AlarmProfileBo apf) {
		AlarmDao.updateAlarmDeviceType(apf.getProfileId(),
				apf.getNetflowAlarmObj());
		AlarmDao.delDevices(new Integer(apf.getProfileId()));
		return AlarmDao.addAlarmResource(apf);
	}

	@Override
	public int addAlarmRules(AlarmProfileBo apf) {
		int profileId = new Integer(apf.getProfileId());
		this.insertRule(apf);
		AlarmDao.delProtos(profileId);
		AlarmDao.delApps(profileId);
		AlarmDao.delIPs(profileId);
		this.insertProto(apf);
		this.insertApp(apf);
		this.insertIps(apf);
		return 1;
	}

	public int insertRule(AlarmProfileBo apf) {
		Map<String, String> map = new HashMap<String, String>();
		String profileId = String.valueOf(apf.getProfileId()); // profile??????ID
		String flowType = String.valueOf(apf
				.getNetflowAlarmThresholdFilterInOut());// ??????profle?????????flow_type??????????????????????????????????????????
		String flowParam = String.valueOf(apf
				.getNetflowAlarmThresholdFilterType());// ??????profile?????????flow_param??????????????????????????????????????????????????????????????????
		map.put("profileId", profileId);
		map.put("flowType", flowType);
		map.put("flowParam", flowParam);
		return AlarmDao.insertRule(map);
	}

	public int insertApp(AlarmProfileBo apf) {
		Map<String, String> map = new HashMap<String, String>();
		String profileId = String.valueOf(apf.getProfileId()); // profile??????ID
		String app = apf.getNetflowAlarmApp();
		if ("all".equals(app)) {
			List<AlarmResourceBo> apps = this.getApplication();
			for (AlarmResourceBo bo : apps) {
				bo.setName(profileId);// ???????????????????????????????????????ID??????????????????ID????????????name?????????profileID,????????????????????????
			}
			return AlarmDao.insertApps(apps);
		} else {
			map.put("profileId", profileId);
			map.put("appId", app);
			return AlarmDao.insertApp(map);
		}
	}

	public int insertProto(AlarmProfileBo apf) {
		Map<String, String> map = new HashMap<String, String>();
		String profileId = String.valueOf(apf.getProfileId()); // profile??????ID
		String proto = apf.getNetflowAlarmProtocal();
		String port = apf.getNetflowAlarmProtoPort();
		String prefix = "";
		String subfix = "";
		if (port.contains("-")) {
			String[] ports = port.split("-");
			prefix = ports[0];
			subfix = ports[1];
		} else {
			prefix = port;
			subfix = port;
		}
		if ("all".equals(proto)) {
			List<AlarmResourceBo> protos = this.getProtocol();
			for (AlarmResourceBo bo : protos) {
				bo.setName(profileId);// ???????????????????????????????????????id?????????????????????id,name????????????????????????profileId,prefix???????????????????????????subfix????????????????????????
				bo.setPrefix(prefix);
				bo.setSubfix(subfix);
			}
			return AlarmDao.insertProtos(protos);

		} else {
			map.put("profileId", profileId);
			map.put("protoId", proto);
			map.put("prefix", prefix);
			map.put("subfix", subfix);
			return AlarmDao.insertProto(map);

		}
	}

	public int insertIps(AlarmProfileBo apf) {
		Map<String, String> map = new HashMap<String, String>();
		String profileId = String.valueOf(apf.getProfileId()); // profile??????ID
		String netflowNetworkIpAddrStart = apf.getNetflowNetworkIpAddrStart();
		String netflowNetworkIpAddrEnd = apf.getNetflowNetworkIpAddrEnd();
		/*
		 * if(netflowNetworkIpAddrEnd==null||"".equals(netflowNetworkIpAddrEnd)){
		 * netflowNetworkIpAddrEnd = netflowNetworkIpAddrStart; }
		 */
		map.put("profileId", profileId);
		map.put("startIp", netflowNetworkIpAddrStart);
		map.put("endIp", netflowNetworkIpAddrEnd);
		return AlarmDao.insertIPs(map);

	}

	public IAlarmProfileDao getAlarmDao() {
		return AlarmDao;
	}

	public void setAlarmDao(IAlarmProfileDao alarmDao) {
		AlarmDao = alarmDao;
	}

	@Override
	public int updateStatus(int profileId, int status) {
		return this.AlarmDao.updateAlarmStatus(profileId, status);
	}

	@Override
	public int addThresholds(List<AlarmThresholdBo> thresholds) {
		AlarmDao.delThresholds(new Integer(thresholds.get(0).getProfileId()));
		return AlarmDao.addThresholds(thresholds);
	}

	@Override
	public int delProfiles(String[] ids) {
		for (String s : ids) {
			List<AlarmRule> list = alarmRuleService.getAlarmRulesByProfileId(
					new Long(s), AlarmRuleProfileEnum.netFlow);
			if (list != null && list.size() > 0) {
				long[] ruleIds = new long[list.size()];
				for (int loop = 0; loop < list.size(); loop++) {
					ruleIds[loop] = list.get(loop).getId();
				}
				alarmRuleService.deleteAlarmRuleById(ruleIds);
			}

		}
		return AlarmDao.delProfiles(ids);
	}

	@Override
	public AlarmProfileBo loadBasic(int profileId) {
		return this.AlarmDao.loadBasic(profileId);
	}

	@Override
	public List<AlarmThresholdBo> loadThreshold(int profileId) {
		return this.AlarmDao.loadThreshold(profileId);
	}

	@Override
	public int getCount(String name, Integer id) {
		return this.AlarmDao.getCount(name, id);
	}
}
