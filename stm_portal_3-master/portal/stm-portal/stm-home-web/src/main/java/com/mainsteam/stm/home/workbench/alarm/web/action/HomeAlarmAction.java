package com.mainsteam.stm.home.workbench.alarm.web.action;

import org.apache.log4j.Logger;

import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.alarm.obj.AlarmEvent;
import com.mainsteam.stm.home.workbench.alarm.api.IHomeAlarmApi;
import com.mainsteam.stm.home.workbench.resource.api.IResourceApi;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.util.DateUtil;

/**
 * <li>文件名称: AlarmAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2019年9月16日
 * @author   zhangjunfeng
 *  
 * modified
 * @since  2017年03月29日
 * @author tandl 
 */
@Controller
@RequestMapping("system/home")
public class HomeAlarmAction extends BaseAction {
	/**
	 * Logger for this class
	 */
	private static final Logger logger = Logger.getLogger(HomeAlarmAction.class);

	@Resource(name="homeAlarmApi")
	private IHomeAlarmApi homeAlarmApi;
	
	@Autowired
	@Qualifier("stm_home_workbench_resourceApi")
	private IResourceApi homeResourceApi;
	
	/** 
	* @Title: getHomoAlarmData 
	* @Description: TODO(获取首页告警一览数据)
	* @param resource
	* @return JSONObject    返回类型 
	* @throws 
	*/
	@RequestMapping("getHomoAlarmData")
	public JSONObject getHomoAlarmData(String resource,long groupId,Long ... domainId){
		if (logger.isDebugEnabled()) {
			logger.debug("getHomoAlarmData(String, long, long) - start"+DateUtil.format(Calendar.getInstance().getTime(), "yyyy-MM-dd HH:mm:ss")); //$NON-NLS-1$
		}
		ILoginUser user = getLoginUser();
		List<Long> instanceIds = homeResourceApi.queryGroupResourceByDomain(resource, user, groupId, domainId);
		Map<String, Integer> result = homeAlarmApi.getHomeAlarmData(instanceIds);
		if (logger.isDebugEnabled()) {
			logger.debug("getHomoAlarmData(String, long, long) - end"+DateUtil.format(Calendar.getInstance().getTime(), "yyyy-MM-dd HH:mm:ss")); //$NON-NLS-1$
		}
		return toSuccess(result);
	}
	@RequestMapping("getHomoAlarmInfoBySourceid")
	public JSONObject getHomoAlarmInfoBySourceid(String resource){
		if (logger.isDebugEnabled()) {
			logger.debug("getHomoAlarmData(String, long, long) - start"+DateUtil.format(Calendar.getInstance().getTime(), "yyyy-MM-dd HH:mm:ss")); //$NON-NLS-1$
		}
	
		List<AlarmEvent> events=homeAlarmApi.getHomeOneAlarm(Long.valueOf(resource));

		if (logger.isDebugEnabled()) {
			logger.debug("getHomoAlarmData(String, long, long) - end"+DateUtil.format(Calendar.getInstance().getTime(), "yyyy-MM-dd HH:mm:ss")); //$NON-NLS-1$
		}
		return toSuccess(events);
	}
	@RequestMapping("getHomoAlarmDataBySourceid")
	public JSONObject getHomoAlarmDataBySourceid(String resource){
		if (logger.isDebugEnabled()) {
			logger.debug("getHomoAlarmData(String, long, long) - start"+DateUtil.format(Calendar.getInstance().getTime(), "yyyy-MM-dd HH:mm:ss")); //$NON-NLS-1$
		}
		ILoginUser user = getLoginUser();
		Map<String, Integer> result = homeAlarmApi.getHomeAlarmDataById(Long.parseLong(resource));

		if (logger.isDebugEnabled()) {
			logger.debug("getHomoAlarmData(String, long, long) - end"+DateUtil.format(Calendar.getInstance().getTime(), "yyyy-MM-dd HH:mm:ss")); //$NON-NLS-1$
		}
		return toSuccess(result);
	}
		
	/**
	 * 按照资源类型获取按照资源类型进行分组的告警数量及其汇总信息
	 * @param resources 
	 * @param groupId
	 * @param domainId
	 * @return
	 */
	@RequestMapping("getDetailAlarmCount")
	public JSONObject getDetailAlarmCount(String[] resources,long groupId,long start, long end,Long ... domainId){
	
		logger.debug("getHomoAlarmData(String, long, long) - start"+DateUtil.format(Calendar.getInstance().getTime(), "yyyy-MM-dd HH:mm:ss")); //$NON-NLS-1$
		
		ILoginUser user = getLoginUser();
		
		if(domainId != null){
			//domainId[0] ==-1 表示是获取当前用户所有的域
			if(domainId[0] == -1){
				Set<IDomain> dms = user.getDomains();
				Long dmIds[] = new Long[dms.size()];
				int i=0;
				for (IDomain dm : dms) {
					dmIds[i++] = dm.getId();
				}
				domainId = dmIds;
			}
		}
		
		//Map<String, Object> detail = new HashMap<>(); 
		
		Date startd = new Date(start);
		Date endd = new Date(end);
		
		int critical = 0;
		int serious = 0;
		int warn = 0;
		
		JSONArray jcritical = new JSONArray();
		JSONArray jserious = new JSONArray();
		JSONArray jwarn = new JSONArray();
		
		for(int i=0;i<resources.length; i++){
			String resource = resources[i];
			List<Long> instanceIds = homeResourceApi.queryGroupResourceByDomain(resource, user, groupId, domainId);
			Map<String, Integer> ac = homeAlarmApi.getHomeAlarmData(instanceIds,startd,endd);
			int tc = ac.get("critical");
			if(tc>0){
				critical += tc;
				
				JSONObject  out = new JSONObject();
				out.put("name", resource);
				out.put("value", tc);
				jcritical.add(out);
			}
			
			tc = ac.get("serious");
			if(tc>0){
				serious += tc;
				
				JSONObject  out = new JSONObject();
				out.put("name", resource);
				out.put("value", tc);
				jserious.add(out);
			}
			tc = ac.get("warn");
			if(tc>0){
				warn += tc;
				
				JSONObject  out = new JSONObject();
				out.put("name", resource);
				out.put("value", tc);
				jwarn.add(out);
			}
		}
		
		JSONObject ocritical = new JSONObject();
		ocritical.put("value", critical);
		ocritical.put("name", "critical");
		
		JSONObject oserious = new JSONObject();
		oserious.put("name", "serious");
		oserious.put("value", serious);

		JSONObject owarn = new JSONObject();
		owarn.put("name", "warn");
		owarn.put("value", warn);
		
		
		JSONArray total= new JSONArray();
		total.add(ocritical);
		total.add(oserious);
		total.add(owarn);
		
		JSONArray detail= new JSONArray();
		detail.add(jcritical);
		detail.add(jserious);
		detail.add(jwarn);
		
		
		JSONObject result = new JSONObject(); 
		result.put("detail", detail);
		result.put("total", total);
		
		return toSuccess(result);
	}
	
}
