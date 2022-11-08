package com.mainsteam.stm.portal.threed.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.StringUtils;

import com.mainsteam.stm.portal.threed.api.IUrlApi;
import com.mainsteam.stm.portal.threed.bo.UrlBo;
import com.mainsteam.stm.portal.threed.dao.IUrlDao;
import com.mainsteam.stm.portal.threed.job.MoniterEngine;
import com.mainsteam.stm.system.um.right.api.IRightApi;
import com.mainsteam.stm.system.um.right.bo.Right;

public class UrlImpl implements IUrlApi{
	IUrlDao urlDao;
	@Autowired
	IRightApi stm_system_right_impl;
	@Autowired
	private MoniterEngine moniterEngine;
	public void setUrlDao(IUrlDao urlDao) {
		this.urlDao = urlDao;
	}

	@Override
	public UrlBo get3DInfo() {
		return urlDao.get3DInfo();
	}

	@Override
	public String get3DWebservicePath() {
		UrlBo bo = this.get3DInfo();
		if(bo == null) return "";
		return getHttp(bo)+bo.getWebservicePath();
	}

	@Override
	public String get3DHomePath() {
		UrlBo bo = this.get3DInfo();
		if(bo == null) return "";
		return getHttp(bo)+bo.getHomePath();
	}

	@Override
	public String get3DAdminPath() {
		UrlBo bo = this.get3DInfo();
		if(bo == null) return "";
		return getHttp(bo)+bo.getAdminPath();
	}

	@Override
	public String get3DPicturePath() {
		UrlBo bo = this.get3DInfo();
		if(bo == null) return "";
		return getHttp(bo)+bo.getPicturePath();
	}
	private String getHttp(UrlBo bo){
		return "http://"+bo.getIp()+":"+bo.getPort();
	}

	@Override
	public UrlBo add() {
		UrlBo bo = new UrlBo();
		bo.setIp("");
		bo.setStatus("0");
		urlDao.add3DInfo(bo);
		return bo;
	}

	@Override
	public int update(UrlBo bo) {
		if(!StringUtils.isEmpty(bo.getIp())){
			Right rightBo = new Right();
			rightBo.setId(12l);
			UrlBo b = this.get3DInfo();
			rightBo.setUrl("http://"+bo.getIp()+":"+bo.getPort()+b.getHomePath());
			stm_system_right_impl.update(rightBo);
		}
		if(!StringUtils.isEmpty(bo.getStatus())){
			try{
				Right right = new Right();
				right.setId(12l);
				if("0".equals(bo.getStatus())){
					right.setStatus(0);
					stm_system_right_impl.updateStatus(right);
					moniterEngine.stopJob();
				}else{
					right.setStatus(1);
					stm_system_right_impl.updateStatus(right);
					moniterEngine.startJob();
				}
			}catch(Exception e){
				
			}
		}
		return urlDao.update(bo);
	}
}
