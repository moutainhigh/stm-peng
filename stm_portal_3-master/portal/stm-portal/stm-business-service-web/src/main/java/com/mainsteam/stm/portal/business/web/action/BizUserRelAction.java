package com.mainsteam.stm.portal.business.web.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.business.api.BizMainApi;
import com.mainsteam.stm.portal.business.api.BizUserRelApi;
import com.mainsteam.stm.portal.business.bo.BizMainBo;
import com.mainsteam.stm.portal.business.bo.BizUserRelBo;

@Controller
@RequestMapping("/portal/business/user")
public class BizUserRelAction extends BaseAction {
	
	//private Logger logger = Logger.getLogger(BizUserRelAction.class);

	@Resource
	private BizUserRelApi bizUserRelApi;
	
	@Resource
	private BizMainApi bizMainApi;
	
	@RequestMapping("/getUserlistByBizId")
	public JSONObject getUserlistByBizId(long bizid,String queryName,long domainId){
		List<BizUserRelBo> list = new ArrayList<>();
		list = bizUserRelApi.getUserlistByBizId(bizid, queryName, domainId);
		BizMainBo bizMainBo = bizMainApi.getBasicInfo(bizid);
		
		//设置创建者和管理者edit权限
		for (int i = 0; i < list.size(); i++) {
			BizUserRelBo item = list.get(i);
			if (item.getUser_id()==bizMainBo.getManagerId() || item.getUser_id()==bizMainBo.getCreateId()) {
				item.setEdit(1);
			}else {
				item.setEdit(0);
			}
		}
		return toSuccess(list);
	}
	
	@RequestMapping("/update")
	public JSONObject update(long biz_id, String rowSet, String rows){
		JSONArray jsonArray = JSONArray.parseArray(rowSet);
		List<BizUserRelBo> list = new ArrayList<>();
		for (Object object : jsonArray) {
			list.add(JSONObject.parseObject(object.toString(), BizUserRelBo.class));
		}
		
		List<Long> rowsList = new ArrayList<>();
		jsonArray = JSONArray.parseArray(rows);
		for (Object item : jsonArray) {
			rowsList.add(JSONObject.parseObject(item.toString(), Long.class));
		}
		
		if (bizUserRelApi.update(biz_id, list, rowsList)) {
			return toSuccess("提交成功");
		}else {
			return toSuccess("提交失败");
		}
	}
	
	@RequestMapping("/getBizlistByUserId")
	public JSONObject getBizlistByUserId(long user_id){
		return toSuccess(bizUserRelApi.getBizlistByUserId(user_id));
	}
	
	@RequestMapping("/checkUserView")
	public JSONObject checkUserView(long user_id, long biz_id){
		return toSuccess(bizUserRelApi.checkUserView(user_id, biz_id));
	}
	
	@RequestMapping("/deleteBizById")
	public JSONObject deleteBizById(List<Long> biz_ids, List<Long> user_ids){
		String result = null;
		result = bizUserRelApi.deleteBizById(biz_ids,user_ids);
		return toSuccess(result);
	}
}
