package com.mainsteam.stm.system.um.resources.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;

import com.mainsteam.stm.system.um.resources.api.IUserResourcesApi;
import com.mainsteam.stm.system.um.resources.bo.UserResourceBo;

/**
 * <li>文件名称: UserResourceImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月15日
 * @author   ziwenwen
 */
@Service("userResourcesApi")
public class UserResourceImpl implements IUserResourcesApi {

	@Override
	public int batchInsert(List<UserResourceBo> urBos) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int batchDel(List<UserResourceBo> urBos) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public List<UserResourceBo> getByUserId(Long userId) {
		List<UserResourceBo> urs=new ArrayList<UserResourceBo>();
		UserResourceBo ur=new UserResourceBo();
		ur.setResourceId(2955645l);
		ur.setUserId(userId);
		urs.add(ur);
		return urs;
	}

	@Override
	public int resetUserResource(Long userId) {
		// TODO Auto-generated method stub
		return 0;
	}

}


