package com.mainsteam.stm.portal.threed.web.action;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.portal.threed.api.IUrlApi;
import com.mainsteam.stm.portal.threed.bo.UrlBo;
import com.mainsteam.stm.portal.threed.web.vo.UrlVo;

@Controller
@RequestMapping("/portal/3d/url")
public class UrlAction extends BaseAction{
	@Autowired
	IUrlApi urlApi;
	/**
	 * 第一次调用此方法向数据库加入一条数据
	 * @return
	 */
	@RequestMapping("/get3DInfo")
	public JSONObject get3DInfo(){
		UrlBo bo = urlApi.get3DInfo();
		if(bo==null){
			urlApi.add();
			bo = urlApi.get3DInfo();
		}
		UrlVo vo = toVoFromBo(bo);
		return toSuccess(vo);
	}
	/**
	 * 开启或关闭集成
	 * @return
	 */
	@RequestMapping("/updateStatus")
	public JSONObject updateStatus(String status){
		UrlBo bo = new UrlBo();
		bo.setStatus(status);
		int row = urlApi.update(bo);
		return toSuccess(row);
	}
	/**
	 * 更新3D机房IP
	 * @return
	 */
	@RequestMapping("/updateIp")
	public JSONObject updateIp(UrlBo bo){
		int row = urlApi.update(bo);
		return toSuccess(row);
	}
	
	private UrlVo toVoFromBo(UrlBo bo){
		UrlVo vo = new UrlVo();
		if(bo==null) return vo;
		String http = "http://"+bo.getIp()+":"+bo.getPort();
		vo.setIp(bo.getIp());
		vo.setPort(bo.getPort());
		vo.setAdminPath(http+bo.getAdminPath());
		vo.setHomePath(http+bo.getHomePath());
		vo.setPicturePath(http+bo.getPicturePath());
		vo.setStatus("0".equals(bo.getStatus())?false:true);
		vo.setWebservicePath(http+bo.getWebservicePath());
		return vo;
	}
}
