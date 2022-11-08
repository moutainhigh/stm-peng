/**
 * 
 */
package com.mainsteam.stm.home.workbench.business.web.action;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.business.api.BizMainApi;
import com.mainsteam.stm.portal.business.service.bo.BizMainBo;
import com.mainsteam.stm.portal.business.service.bo.BizMetricDataBo;

/**
 * 
 * 
 * <li>文件名称:HomeBizMainAction.java</li> 
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有:版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2017年3月21日 下午2:12:27
 * @author tandl
 */
@Controller
@RequestMapping("/home/biz")
public class HomeBizMainAction extends BaseAction {
	
	@Autowired
	private BizMainApi bizMainApi;
	
	/*
	 * 业务数据列表
	 */
	@RequestMapping("/getList")
	public JSONObject getBizSetData(HttpServletRequest request){
		
		ILoginUser user=getLoginUser();
		List<BizMainBo> bizs = bizMainApi.getBizsForHome(user);
		JSONObject out = new JSONObject();
		out.put("size", bizs.size());
		out.put("rows", bizs);
		return toSuccess(out);
	}
	
	@RequestMapping("/getTable")
	public JSONObject getTable(HttpServletRequest request,String startTime,String endTime,Long ... bizIds){
		ILoginUser user=getLoginUser();
		Date startDate = new Date(Long.parseLong(startTime));
		Date endDate = new Date(Long.parseLong(endTime));
		JSONObject out = new JSONObject();
		List<BizMetricDataBo> bizs = bizMainApi.getPageListRunInfo(user,startDate,endDate);
		List<BizMetricDataBo> obizs = new ArrayList<>();
		
		if(bizIds != null && bizIds.length >0)
			for (BizMetricDataBo bmd : bizs) {
				for(Long id : bizIds){
					if(bmd.getBizId() == id){
						obizs.add(bmd);
						break;
					}
				}
			}
		
		out.put("size", obizs.size());
		out.put("rows", obizs);
		
		
		return toSuccess(out);
	}
	
}
