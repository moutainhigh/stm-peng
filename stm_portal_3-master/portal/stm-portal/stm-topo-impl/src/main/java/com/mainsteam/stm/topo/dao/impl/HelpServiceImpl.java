package com.mainsteam.stm.topo.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.topo.api.HelperService;
import com.mainsteam.stm.topo.bo.SettingBo;
import com.mainsteam.stm.topo.dao.ISettingDao;
import com.mainsteam.stm.topo.dao.ISubTopoDao;
import com.mainsteam.stm.topo.enums.SettingKey;
@Service
public class HelpServiceImpl implements HelperService{
	@Autowired
	private ISubTopoDao subTopodao;
	@Autowired
	private ISettingDao settingDao;
	@Override
	public void initTopo() {
		SettingBo sp = settingDao.getCfg(SettingKey.TOPO_INIT_FLAG.getKey());
		if(sp==null){
			subTopodao.init();
			SettingBo sb=new SettingBo();
			sb.setKey(SettingKey.TOPO_INIT_FLAG.getKey());
			sb.setValue("{\"flag\":true}");
			settingDao.save(sb);
		}
	}
}
