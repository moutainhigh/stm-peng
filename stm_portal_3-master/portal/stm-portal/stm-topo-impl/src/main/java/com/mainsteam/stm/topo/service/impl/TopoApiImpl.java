package com.mainsteam.stm.topo.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.home.screen.api.ITopoApi;
import com.mainsteam.stm.home.screen.bo.Biz;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.topo.bo.SubTopoBo;
import com.mainsteam.stm.topo.dao.INodeDao;
import com.mainsteam.stm.topo.dao.ISubTopoDao;
import com.mainsteam.stm.topo.dao.ITopoAuthSettingDao;
@Service("homeScreenTopoApi")
public class TopoApiImpl implements ITopoApi{
	@Autowired
	private ITopoAuthSettingDao adao;
	@Autowired
	private ISubTopoDao sdao;
	@Autowired
	private INodeDao ndao;
	public Biz getBiz(Long biz) {
		// TODO Auto-generated method stub
		return null;
	}
	@Override
	public List<Biz> getBizs(ILoginUser user, HttpServletRequest request) {
		List<Long> subtopoIds=null;
		if(user.isCommonUser() && !user.isDomainUser()){
			subtopoIds = adao.getAllReadOnlyTopo(user.getId());
		}else{
			subtopoIds=sdao.getAllTopoIds();
		}
		List<SubTopoBo> subTopos = sdao.getSubToposByIds(subtopoIds);
		List<Biz> bizes = new ArrayList<Biz>();
		for(SubTopoBo sb : subTopos){
			Biz biz = new Biz();
			biz.setBizType(Biz.BIZ_TYPE_TOP);
			biz.setBizId(sb.getId()==null||sb.getId()==0?10:sb.getId());
			biz.setTitle(sb.getName());
			biz.setContent("-1");
			bizes.add(biz);
		}
		return bizes;
	}
}
