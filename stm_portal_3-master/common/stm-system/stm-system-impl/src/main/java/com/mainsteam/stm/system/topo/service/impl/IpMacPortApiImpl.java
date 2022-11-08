package com.mainsteam.stm.system.topo.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.system.topo.api.IIpMacPortApi;
import com.mainsteam.stm.system.topo.dao.IIpMacPortDao;

/**
 * <li>文件名称: ResourceImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月20日
 * @author   ziwenwen
 */
@Service("stm_system_ipmacportApi")
public class IpMacPortApiImpl implements IIpMacPortApi {
	@Autowired()@Qualifier("stm_system_ipmacportDao")
	private IIpMacPortDao ipMacPortDao;
	
	@Override
	public List<Long> getMacBaseIds() {
		return ipMacPortDao.getMacBaseIds();
	}
}


