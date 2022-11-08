package com.mainsteam.stm.restful;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.system.um.user.api.IUserApi;
import com.mainsteam.stm.system.um.user.bo.User;
import com.mainsteam.stm.webService.obj.ItsmUserBean;
import com.mainsteam.stm.webService.obj.UserOperatetype;

public class SyncAllUserServiceImpl implements SyncAllUserService{
	
	@Resource
	private IUserApi userApi;
	
	 @Override
	public String getAllUser(){
		// 查询所有的用户信息
			List<User> listUser = userApi.queryAllUserNoPage();
			List<ItsmUserBean> list = new ArrayList<ItsmUserBean>();
			for (User user : listUser) {
				ItsmUserBean userBean = new ItsmUserBean(user);
				// 操作类型 新增
				userBean.setOperatetype(UserOperatetype.INSERT.toString());
				list.add(userBean);
			}
		return JSONObject.toJSONString(list);
	}
}
