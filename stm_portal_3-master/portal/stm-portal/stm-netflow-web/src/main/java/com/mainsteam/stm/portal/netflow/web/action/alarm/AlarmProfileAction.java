/**
 * 
 */
package com.mainsteam.stm.portal.netflow.web.action.alarm;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.netflow.api.alarm.IAlarmProfileApi;
import com.mainsteam.stm.portal.netflow.bo.alarm.AlarmDataGridListBo;
import com.mainsteam.stm.portal.netflow.bo.alarm.AlarmProfileBo;
import com.mainsteam.stm.portal.netflow.bo.alarm.AlarmResourceBo;
import com.mainsteam.stm.portal.netflow.bo.alarm.AlarmThresholdBo;
import com.mainsteam.stm.portal.netflow.bo.alarm.InterfaceTreeBo;

/**
 * <li>文件名称: AlarmProfile.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年7月
 * @author xiejr
 */
@Controller
@RequestMapping("/netflow/alarm")
public class AlarmProfileAction extends BaseAction {
	@Autowired
	private IAlarmProfileApi alarmProfile;

	@RequestMapping("getAllAlarms")
	public JSONObject getAllAlarms(String rowCount, String startRow,
			String AlarmsName, String order) {
		Page<AlarmDataGridListBo, Map<String, String>> page = alarmProfile
				.getAllAlarms(rowCount, startRow, AlarmsName, order);
		return BaseAction.toSuccess(page);
	}

	@RequestMapping("getResource")
	public JSONObject getResource(String type) {
		List<AlarmResourceBo> resource = null;
		List<InterfaceTreeBo> treebo = null;
		if (type == null || "".equals(type)) {
			type = "1";
		}
		int resourceType = new Integer(type).intValue();
		if (resourceType == 1) {// 1对应接口资源
			treebo = alarmProfile.getInterface();
		} else if (resourceType == 2) {// 2对应接口分组资源
			resource = alarmProfile.getInterfaceGroup();
		} else if (resourceType == 3) {// 3对应IP分组资源
			resource = alarmProfile.getIPGroup();
		}
		if (resource != null) {
			return toSuccess(resource);
		} else if (treebo != null) {
			return toSuccess(treebo);
		} else {
			return null;
		}
	}

	@RequestMapping("getProtocol")
	public JSONObject getProtocol() {
		List<AlarmResourceBo> list = alarmProfile.getProtocol();
		return toSuccess(list);
	}

	@RequestMapping("getApplication")
	public JSONObject getApplication() {
		List<AlarmResourceBo> list = alarmProfile.getApplication();
		return toSuccess(list);
	}

	@RequestMapping("addAlarmBasic")
	public JSONObject addAlarmBasic(AlarmProfileBo apf) {
		if (apf.getProfileId() != null && !"".equals(apf.getProfileId())) {
			alarmProfile.updateAlarmBasic(apf);
			return toSuccess(apf.getProfileId());
		} else {
			alarmProfile.addAlarmBasic(apf);
			return toSuccess(apf.getId());
		}
	}

	@RequestMapping("addAlarmResource")
	public JSONObject addAlarmResource(AlarmProfileBo apf) {
		return toSuccess(alarmProfile.addAlarmResource(apf));
	}

	@RequestMapping("addAlarmRules")
	public JSONObject addAlarmRules(AlarmProfileBo apf) {
		return toSuccess(alarmProfile.addAlarmRules(apf));
	}

	public IAlarmProfileApi getAlarmProfile() {
		return alarmProfile;
	}

	public void setAlarmProfile(IAlarmProfileApi alarmProfile) {
		this.alarmProfile = alarmProfile;
	}

	/**
	 * 更新状态
	 *
	 * @param profileId
	 * @param status
	 * @return
	 */
	@RequestMapping("updateStatus")
	public JSONObject updateStatus(int profileId, int status) {
		return toSuccess(this.alarmProfile.updateStatus(profileId, status));
	}

	@RequestMapping("addAlarmThreshold")
	public JSONObject addAlarmThreshold(AlarmProfileBo apf) {
		String profileId = apf.getProfileId();
		if (profileId != null && !"".equals(profileId)) {
			List<String> minutes = apf.getNetflowAlarmThresholdMinute();
			List<String> counts = apf.getNetflowAlarmThresholdCount();
			List<String> values = apf.getNetflowAlarmThresholdValue();
			List<String> levels = apf.getNetflowAlarmThresholdLevel();
			List<String> units = apf.getNetflowAlarmFlowUnit();
			if (minutes.size() > 0) {
				List<AlarmThresholdBo> thresholds = new ArrayList<AlarmThresholdBo>();
				for (int loop = 0; loop < minutes.size(); loop++) {
					AlarmThresholdBo bo = new AlarmThresholdBo();
					String value = values.get(loop);
					String unit = units.get(loop);
					if (unit != null && !"".equals(unit)) {
						value = String.valueOf((new Long(value))
								* (new Long(unit)));
					}
					bo.setProfileId(profileId);
					bo.setNetflowAlarmThresholdMinute(minutes.get(loop));
					bo.setNetflowAlarmThresholdCount(counts.get(loop));
					bo.setNetflowAlarmThresholdValue(value);
					bo.setNetflowAlarmThresholdLevel(levels.get(loop));
					thresholds.add(bo);
				}

				return toSuccess(alarmProfile.addThresholds(thresholds));
			}
		}

		return toSuccess(-1);
	}

	@RequestMapping("delProfiles")
	public JSONObject delProfiles(String ids) {
		String[] profileIds = null;
		if (ids != null && !"".equals(ids)) {
			profileIds = ids.split(",");
			return toSuccess(alarmProfile.delProfiles(profileIds));
		} else {
			return toSuccess(-1);
		}

	}

	/**
	 * 基本信息请求处理
	 *
	 * @param profileId
	 * @return
	 */
	@RequestMapping("loadBasic")
	public JSONObject loadBasic(int profileId) {
		AlarmProfileBo basicInfo = this.alarmProfile.loadBasic(profileId);
		List<AlarmResourceBo> apps = alarmProfile.getApplication();
		List<AlarmResourceBo> protocols = alarmProfile.getProtocol();
		Map<String, Object> ret = new HashMap<String, Object>();
		ret.put("apps", apps);
		ret.put("protocols", protocols);
		ret.put("basicInfo", basicInfo);
		return toSuccess(ret);
	}

	/**
	 * 加载阈值的请求
	 *
	 * @param profileId
	 * @return
	 */
	@RequestMapping("loadThreshold")
	public JSONObject loadThreshold(int profileId) {
		List<AlarmThresholdBo> list = this.alarmProfile
				.loadThreshold(profileId);
		if (null != list && !list.isEmpty()) {
			for (AlarmThresholdBo b : list) {
				String value = b.getNetflowAlarmThresholdValue();
				String realValue = value;
				String unit = "1";
				if (null != value && 0 != value.length()
						&& Integer.parseInt(value) > 1024) {
					int v = Integer.parseInt(value);
					unit = getUnit(v);
					realValue = String.valueOf((v / Integer.parseInt(unit)));
				}
				b.setNetflowAlarmThresholdValue(realValue);
				b.setNetflowAlarmFlowUnit(unit);
			}
		}
		return toSuccess(list);
	}

	/**
	 * 根据阈值计算对应的单位
	 *
	 * @param value
	 * @return
	 */
	private String getUnit(int value) {
		int ret = value / 1024;
		int count = 1;
		if (ret > 1024) {
			count += 1;
			return getUnit(ret);
		}
		return String.valueOf(1024 * count);
	};

	@RequestMapping("getCount")
	public JSONObject getCount(String name, Integer id) {
		return toSuccess(this.alarmProfile.getCount(name, id));
	}
	
	
	/**
	 * 编辑某个告警的时候，如果资源为接口，那么这个方法是获取接口左边未选取的接口数据
	 * @param profileID
	 * @return
	 */
	@RequestMapping("getInterfaceResourceleft")
	public JSONObject getInterfaceResourceleft(String profileID){
		List<InterfaceTreeBo> treebo = null;
		treebo = this.alarmProfile.loadInterfaces(profileID, "left");
		return toSuccess(treebo);
	}
	/**
	 * 编辑某个告警的时候，如果资源为接口，那么这个方法是展示右边已经被选取的接口数据
	 * @param profileID
	 * @return
	 */
	@RequestMapping("getInterfaceResourceright")
	public JSONObject getInterfaceResourceright(String profileID){
		List<InterfaceTreeBo> treebo = null;
		treebo = this.alarmProfile.loadInterfaces(profileID, "right");
		return toSuccess(treebo);
	}
	
	/**
	 * 编辑某个告警的时候，如果资源为IP分组，那么这个方法是获取接口左边未选取的IP分组
	 * @param profileID
	 * @return
	 */
	
	@RequestMapping("getleftIPGroup")
	public JSONObject getleftIPGroup(String profileId){
		List<AlarmResourceBo> resource = null;
		resource = this.alarmProfile.loadIPGroups(profileId,"left");
		return toSuccess(resource);
	}
	@RequestMapping("getrightIPGroup")
	public JSONObject getrightIPGroup(String profileId){
		List<AlarmResourceBo> resource = null;
		resource = this.alarmProfile.loadIPGroups(profileId,"right");
		return toSuccess(resource);
	}
	
	@RequestMapping("getleftIfGroup")
	public JSONObject getleftIfGroup(String profileId){
		List<AlarmResourceBo> resource = null;
		resource = this.alarmProfile.loadIFGroups(profileId,"left");
		return toSuccess(resource);
	}
	@RequestMapping("getrightIfGroup")
	public JSONObject getrightIfGroup(String profileId){
		List<AlarmResourceBo> resource = null;
		resource = this.alarmProfile.loadIFGroups(profileId,"right");
		return toSuccess(resource);
	}
}
