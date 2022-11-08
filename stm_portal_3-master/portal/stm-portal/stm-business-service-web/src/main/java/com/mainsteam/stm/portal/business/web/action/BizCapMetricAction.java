package com.mainsteam.stm.portal.business.web.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.business.api.BizCanvasApi;
import com.mainsteam.stm.portal.business.api.BizCapMetricApi;
import com.mainsteam.stm.portal.business.bo.BizCapMetricBo;
import com.mainsteam.stm.portal.business.bo.BizCapMetricDataBo;
import com.mainsteam.stm.portal.business.web.vo.BizCapMetricVO;

@Controller
@RequestMapping("/portal/business/cap")
public class BizCapMetricAction extends BaseAction {
	@Autowired
	private BizCanvasApi bizCanvasApi;
	@Autowired
	private BizCapMetricApi bizCapMetricApi;
	@Resource
	private ResourceInstanceService resourceInstanceService;

	/**
	 * 容量指标查询
	 * 
	 * @param session
	 * @param bizId
	 * @param type
	 * @param query
	 * @return
	 * @throws InstancelibException
	 */
	@RequestMapping("/getCalculateMetricInstanceList")
	public JSONObject getCalculateMetricInstanceList(HttpSession session,
			String bizId, int type, String query) throws InstancelibException {

		List<BizCapMetricDataBo> vos = new ArrayList<BizCapMetricDataBo>();
		Set<Long> setids = null;

		if (type == 0) {// 主机
			setids = bizCanvasApi.getCalculateMetricInstanceList(Long
					.parseLong(bizId));
		} else if (type == 1) {// 存储
			setids = bizCanvasApi.getStoreMetricInstanceList(Long
					.parseLong(bizId));
		} else if (type == 2) {// 数据库
			setids = bizCanvasApi.getDatabaseMetricInstanceList(Long
					.parseLong(bizId));
		} else if (type == 3) {// 带宽
			setids = bizCanvasApi.getBandwidthMetricInstanceList(Long
					.parseLong(bizId));
		} else if (type == 4) {
			setids = bizCanvasApi.getResponeTimeMetricInstanceList(Long
					.parseLong(bizId));
		}
		List<Long> ids = new ArrayList<Long>(setids);
		Collections.sort(ids);
		List<ResourceInstance> instances = resourceInstanceService
				.getResourceInstances(ids);

		if (instances.size() != 0) {
			vos = bizCapMetricApi.getData(instances);
		}
		vos = getList(vos, query, type);
		return toSuccess(vos);

	}

	public List<BizCapMetricDataBo> getList(List<BizCapMetricDataBo> vos,
			String query, int type) {
		List<BizCapMetricDataBo> vostemp = new ArrayList<BizCapMetricDataBo>();
		for (int i = 0; i < vos.size(); i++) {

			if (!"".equals(query) && query != null) {

				if (type == 0) {
					if (vos.get(i).getName().toUpperCase()
							.contains(query.toUpperCase())) {// 匹配
						vostemp.add(vos.get(i));
					}
				} else {
					List<BizCapMetricDataBo> childs = vos.get(i).getChildren();
					List<BizCapMetricDataBo> childstemp = new ArrayList<BizCapMetricDataBo>();
					// Collections.sort(childs);
					for (int j = 0; j < childs.size(); j++) {
						if (childs.get(j).getName().toUpperCase()
								.contains(query.toUpperCase())) {// 匹配
							childstemp.add(childs.get(j));
						}
					}
					vos.get(i).setChildren(childstemp);
					if (vos.get(i).getChildren().size() == 0) {
						vos.remove(i);
					}
				}

			}
		}
		if (type == 0 && !"".equals(query) && query != null) {
			return vostemp;
		} else {
			return vos;
		}

	}

	/**
	 * 查询主机未选择结果集
	 * 
	 * @param session
	 * @param bizid
	 * @return
	 */
	@RequestMapping("/getUncheckedhostInfo")
	public JSONObject getUncheckedInfo(HttpSession session, long bizid) {
		List<Long> hostlist = bizCapMetricApi.getInfoByBizIdAndMetric(bizid, 0);
		return toSuccess(hostlist);

	}

	/**
	 * 查询存储未选择结果集
	 * 
	 * @param session
	 * @param bizid
	 * @return
	 */
	@RequestMapping("/getUncheckedStorageInfo")
	public JSONObject getUncheckedStorageInfo(HttpSession session, long bizid) {
		List<Long> hostlist = bizCapMetricApi.getInfoByBizIdAndMetric(bizid, 1);
		return toSuccess(hostlist);

	}

	/**
	 * 查询数据库未选择结果集
	 * 
	 * @param session
	 * @param bizid
	 * @return
	 */
	@RequestMapping("/getUncheckedDatabaseInfo")
	public JSONObject getUncheckedDatabaseInfo(HttpSession session, long bizid) {
		List<Long> databaselist = bizCapMetricApi.getInfoByBizIdAndMetric(
				bizid, 2);
		return toSuccess(databaselist);

	}

	/**
	 * 查询带宽未选择结果集
	 * 
	 * @param session
	 * @param bizid
	 * @return
	 */
	@RequestMapping("/getUncheckedBandwidthInfo")
	public JSONObject getUncheckedBandwidthInfo(HttpSession session, long bizid) {
		List<Long> bandwidthlist = bizCapMetricApi.getInfoByBizIdAndMetric(
				bizid, 3);
		return toSuccess(bandwidthlist);

	}

	/**
	 * 查询url未选择结果集
	 * 
	 * @param session
	 * @param bizid
	 * @return
	 */
	@RequestMapping("/getUncheckedUrlInfo")
	public JSONObject getUncheckedUrlInfo(HttpSession session, long bizid) {
		List<Long> urltlist = bizCapMetricApi.getInfoByBizIdAndMetric(bizid, 4);
		return toSuccess(urltlist);

	}

	/**
	 * 新增url
	 * 
	 * @param session
	 * @param data
	 * @param bizid
	 * @return
	 */
	@RequestMapping("/insertUrlCapInfo")
	public JSONObject insertUrlCapInfo(HttpSession session, String data,
			long bizid) {
		Long[] urlids = null;
		if (data != null && data != "") {
			JSONArray jsonArray = JSONObject.parseArray(data);
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject obj = jsonArray.getJSONObject(i);

				if (StringUtils.isNotEmpty(obj.getString("url"))) {
					JSONArray urlidarr = obj.getJSONArray("url");
					urlids = getJsonToLongArray(urlidarr);
				}
			}
		}
		int num = 0;

		if (urlids != null && urlids.length != 0) {
			num = insertMethord(urlids, "responseTime", bizid);
			if (num == 1) {
				return toSuccess("响应时间保存失败！");
			}
		} else {
			bizCapMetricApi.deleteByInfo(bizid, "responseTime");
		}

		return toSuccess("success");

	}

	/**
	 * 新增容量指标
	 * 
	 * @param session
	 * @param data
	 * @param bizid
	 * @return
	 */
	@RequestMapping("/insertCapInfo")
	public JSONObject insertCapInfo(HttpSession session, String data, long bizid) {
		Long[] planids = null;
		Long[] databaseids = null;
		Long[] stroageids = null;
		Long[] bandwidthids = null;
		Long[] urlids = null;
		if (data != null && data != "") {
			JSONArray jsonArray = JSONObject.parseArray(data);
			for (int i = 0; i < jsonArray.size(); i++) {
				JSONObject obj = jsonArray.getJSONObject(i);
				if (StringUtils.isNotEmpty(obj.getString("plan"))) {
					JSONArray planidarr = obj.getJSONArray("plan");
					planids = getJsonToLongArray(planidarr);
				}
				if (StringUtils.isNotEmpty(obj.getString("database"))) {
					JSONArray databaseiarr = obj.getJSONArray("database");
					databaseids = getJsonToLongArray(databaseiarr);
				}
				if (StringUtils.isNotEmpty(obj.getString("stroage"))) {
					JSONArray stroageidarr = obj.getJSONArray("stroage");
					stroageids = getJsonToLongArray(stroageidarr);
				}
				if (StringUtils.isNotEmpty(obj.getString("bandwidth"))) {
					JSONArray bandwidthidarr = obj.getJSONArray("bandwidth");
					bandwidthids = getJsonToLongArray(bandwidthidarr);
				}
				if (StringUtils.isNotEmpty(obj.getString("url"))) {
					JSONArray urlidarr = obj.getJSONArray("url");
					urlids = getJsonToLongArray(urlidarr);
				}
			}
		}
		int num = 0;
		if (planids != null && planids.length != 0) {
			num = insertMethord(planids, "hostCapacity", bizid);
			if (num == 1) {
				return toSuccess("计算容量保存失败！");
			}
		} else {
			bizCapMetricApi.deleteByInfo(bizid, "hostCapacity");
		}
		if (databaseids != null && databaseids.length != 0) {
			num = insertMethord(databaseids, "databaseCapacity", bizid);
			if (num == 1) {
				return toSuccess("数据库容量保存失败！");
			}
		} else {
			bizCapMetricApi.deleteByInfo(bizid, "databaseCapacity");
		}
		if (stroageids != null && stroageids.length != 0) {
			num = insertMethord(stroageids, "storageCapacity", bizid);
			if (num == 1) {
				return toSuccess("存储容量保存失败！");
			}
		} else {
			bizCapMetricApi.deleteByInfo(bizid, "storageCapacity");
		}
		if (bandwidthids != null && bandwidthids.length != 0) {
			num = insertMethord(bandwidthids, "bandwidthCapacity", bizid);
			if (num == 1) {
				return toSuccess("带宽容量保存失败！");
			}
		} else {
			bizCapMetricApi.deleteByInfo(bizid, "bandwidthCapacity");
		}
		if (urlids != null && urlids.length != 0) {
			num = insertMethord(urlids, "responseTime", bizid);
			if (num == 1) {
				return toSuccess("响应时间保存失败！");
			}
		} else {
			bizCapMetricApi.deleteByInfo(bizid, "responseTime");
		}

		return toSuccess("success");

	}

	public static Long[] getJsonToLongArray(JSONArray strarr) {
		Long[] arr = new Long[strarr.size()];
		for (int i = 0; i < strarr.size(); i++) {
			arr[i] = strarr.getLong(i);
		}
		return arr;
	}

	public int insertMethord(Long[] ids, String metric, Long bizid) {
		int num = 0;

		try {
			bizCapMetricApi.deleteByInfo(bizid, metric);
			for (int i = 0; i < ids.length; i++) {
				BizCapMetricBo metricBo = new BizCapMetricBo();
				metricBo.setBizId(bizid);
				metricBo.setMetricId(metric);
				metricBo.setInstanceId(ids[i]);
				bizCapMetricApi.insertInfo(metricBo);
			}
		} catch (NumberFormatException e) {
			num = 1;
			e.printStackTrace();
		}
		return num;
	}

	@RequestMapping("/getResponeTimeMetricInstanceList")
	public JSONObject getResponeTimeMetricInstanceList(HttpSession session,
			long bizid) throws InstancelibException {
		Set<Long> setids = bizCanvasApi.getResponeTimeMetricInstanceList(bizid);
		List<Long> ids = new ArrayList<Long>(setids);
		List<ResourceInstance> instances = resourceInstanceService
				.getResourceInstances(ids);
		List<BizCapMetricVO> vos = new ArrayList<BizCapMetricVO>();
		if (ids.size() != 0) {
			for (int i = 0; i < instances.size(); i++) {
				BizCapMetricVO vo = new BizCapMetricVO();
				vo.setId(String.valueOf(instances.get(i).getId()));
				vo.setName(instances.get(i).getName());
				vo.setPId(String.valueOf(instances.get(i).getParentId()));
				vo.setIsParent(true);
				vos.add(vo);
			}
		}
		return toSuccess(vos);

	}

}
