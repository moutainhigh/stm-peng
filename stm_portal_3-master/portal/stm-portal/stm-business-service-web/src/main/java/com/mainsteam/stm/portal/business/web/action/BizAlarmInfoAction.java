package com.mainsteam.stm.portal.business.web.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.business.api.BizAlarmInfoApi;
import com.mainsteam.stm.portal.business.api.BizMainApi;
import com.mainsteam.stm.portal.business.bo.BizAlarmInfoBo;
import com.mainsteam.stm.portal.business.bo.BizServiceBo;
import com.mainsteam.stm.portal.business.bo.BizWarnViewBo;
import com.mainsteam.stm.portal.business.web.vo.WarnInfoVo;

@Controller
@RequestMapping("/portal/business/alarmInfo")
public class BizAlarmInfoAction extends BaseAction {
	@Autowired
	private BizAlarmInfoApi bizAlarmInfoApi;
	@Resource
	private BizMainApi bizMainApi;
	
	@RequestMapping("/insertalarmInfo")
	public JSONObject insertalarmInfo(HttpSession session, BizAlarmInfoBo infoBo) {
		BizAlarmInfoBo b= new BizAlarmInfoBo();
//		b.setId(infoBo.getId());
		b.setBizId(infoBo.getBizId());
		BizAlarmInfoBo bo = bizAlarmInfoApi.getAlarmInfo(b);
//		long result = bizAlarmInfoApi.insertBizAlarmInfo(infoBo);

	/*	if (result == -1) {
			return toSuccess(null);
		} else {
			return toSuccess(result);
		}*/
		if (bo != null) {
			infoBo.setId(bo.getId());
			long result = bizAlarmInfoApi.updateBizAlarmInfo(infoBo);

			if (result == -1) {
				return toSuccess(null);
			} else {
				if(!infoBo.getDeathThreshold().equals(bo.getDeathThreshold()) ||
						!infoBo.getSeriousThreshold().equals(bo.getSeriousThreshold())
						|| !infoBo.getWarnThreshold().equals(bo.getWarnThreshold())){
					bizMainApi.calculateBizHealth(bo.getBizId(), 5, false, null, -1);
					
				}
				return toSuccess(result);
			}
		} else {
			long result = bizAlarmInfoApi.insertBizAlarmInfo(infoBo);

			if (result == -1) {
				return toSuccess(null);
			} else {
				return toSuccess(result);
			}
		}

	}

	@RequestMapping("/getalarmInfo")
	public JSONObject getalarmInfo(HttpSession session, BizAlarmInfoBo infoBo) {

		BizAlarmInfoBo bo = bizAlarmInfoApi.getAlarmInfo(infoBo);

		if (bo == null) {
			return toSuccess("faild");
		} else {
			return toSuccess(bo);
		}

	}
	@RequestMapping("/getalarmInfoByBizId")
	public JSONObject getalarmInfoByBizId(HttpSession session, String bizid) {
		BizAlarmInfoBo infoBo = new BizAlarmInfoBo();
		infoBo.setBizId(Long.parseLong(bizid));
		BizAlarmInfoBo bo = bizAlarmInfoApi.getAlarmInfo(infoBo);

		if (bo == null) {
			return toSuccess("faild");
		} else {
			return toSuccess(bo);
		}

	}
	
	/**
	 * 查询业务应用对应的告警分页数据
	 * @param page 分页数据
	 * @param bo 业务应用
	 * @param status 状态
	 * @return
	 */
	@RequestMapping("/selectWarnViewPage")
	public JSONObject selectWarnViewPage(Page<BizWarnViewBo, BizWarnViewBo> page,
			BizServiceBo bo,String status,String nodeId,String restore,Date startTime,Date endTime,String queryTime,String isCheckedRadioOne,String isCheckedRadioTwo){
		try {
			bizAlarmInfoApi.selectWarnViewPage(page,bo,status,nodeId,restore,startTime,endTime,queryTime,isCheckedRadioOne,isCheckedRadioTwo);
			return toSuccess(page);
		} catch (Exception e) {
			e.printStackTrace();
			return toJsonObject(299, "查询告警信息失败");
		}
	}
	
	@RequestMapping("/getAlarmInfos")
	public JSONObject getAlarmInfos(){
		List<WarnInfoVo> infoVos = new ArrayList<WarnInfoVo>();
	
		String jsonStr="[{name:'业务系统名称',value:'${业务系统名称}',description:'业务系统名称'},"
				+ "{name:'健康度',value:'${健康度}',description:'业务健康度'},"
				+ "{name:'告警节点名称',value:'${告警节点名称}',description:'直接导致业务故障的第一个节点名称'},"
				+ "{name:'告警节点类型',value:'${告警节点类型}',description:'直接导致业务故障的第一个节点类型'},"
				+ "{name:'节点告警内容',value:'${节点告警内容}',description:'直接导致业务故障的第一个节点告警内容'},"
				+ "{name:'责任人',value:'${责任人}',description:'业务系统责任人'},"
				+ "{name:'告警级别',value:'${告警级别}',description:'务系统告警级别'}]";
		
		JSONArray json = JSONArray.parseArray(jsonStr);
		if(json.size()>0){
		  for(int i=0;i<json.size();i++){
				WarnInfoVo vo = new WarnInfoVo();
		    JSONObject job = json.getJSONObject(i);  // 遍历 jsonarray 数组，把每一个对象转成 json 对象
		    vo.setName(job.getString("name"));
			vo.setValue(job.getString("value"));
			vo.setDescription(job.getString("description"));
			infoVos.add(vo);
		  }
		}
		
		return toSuccess(infoVos);
	}
	
}
