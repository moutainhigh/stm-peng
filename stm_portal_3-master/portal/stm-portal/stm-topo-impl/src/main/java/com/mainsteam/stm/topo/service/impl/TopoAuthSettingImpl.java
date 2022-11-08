package com.mainsteam.stm.topo.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.system.um.role.api.IRoleApi;
import com.mainsteam.stm.system.um.role.bo.Role;
import com.mainsteam.stm.system.um.user.api.IUserApi;
import com.mainsteam.stm.system.um.user.bo.User;
import com.mainsteam.stm.topo.api.ITopoAuthSettingApi;
import com.mainsteam.stm.topo.bo.TopoAuthSettingBo;
import com.mainsteam.stm.topo.dao.ITopoAuthSettingDao;

@Service
public class TopoAuthSettingImpl implements ITopoAuthSettingApi{
	@Autowired
	private IUserApi userApi;
	@Autowired
	private IRoleApi roleApi;
	@Autowired
	private ITopoAuthSettingDao topoAuthSettingDao;
	
	/**
	 * 保存子拓扑的用户权限设置信息
	 * 1. 根据拓扑id删除已配置的权限信息
	 * 2. 保存新的拓扑权限设置信息
	 * @param subtopoId
	 * @param settingBos
	 * @return
	 */
	@Override
	public void save(Long subtopoId, List<TopoAuthSettingBo> settingBos) {
		//1. 根据拓扑id删除已配置的权限信息
		topoAuthSettingDao.deleteBySubtopoId(subtopoId);
		
		// 2. 保存新的拓扑权限设置信息
		for(TopoAuthSettingBo settingBo:settingBos){
			settingBo.setSubtopoId(subtopoId);
			topoAuthSettingDao.save(settingBo);
		}
	}

	/**
	 * 根据拓扑id查询该拓扑的用户权限设置信息
	 * 1. 查询所有用户
	 * 2. 根据topoId查询拓扑权限设置用户信息
	 * 3. 封装成页面需要的权限设置信息列表
	 * @param topoId
	 * @return
	 */
	@Override
	public List<TopoAuthSettingBo> getTopoAuthSetting(Long topoId) {
		// 1. 查询所有用户
		List<User> users = userApi.queryAllUserNoPage();
		for(int i=users.size()-1;i>=0;i--){
			User user = users.get(i);
			//过滤掉系统管理员、超级管理员、域管理员、管理者
			if(user.getUserType() != 1){	//1:普通用户，3:系统管理员，4：超级管理员
				users.remove(user);
			}else{
				List<Role> roles = roleApi.getRoles(user.getId());
				long id;
				for(int j=0,size=roles.size();j<size;j++){
					id = roles.get(j).getId();
					if(id == Role.ROLE_MANAGER_ID || id == Role.ROLE_DOMAIN_MANAGER_ID){
						users.remove(user);
					}
				}
			}
		}
		
		// 2. 根据topoId查询拓扑权限设置用户信息
		List<TopoAuthSettingBo> authSettingBos = topoAuthSettingDao.getAuthSettingBosByTopoId(topoId);
		
		// 3. 封装成页面需要的权限设置信息列表
		return parseTopoAuthSettingBos(authSettingBos, users);
	}
	
	/**
	 * 转换系统用户和拓扑权限设置用户信息
	 * @param authSettingBos
	 * @param users
	 * @return List<TopoAuthSettingBo>
	 */
	private List<TopoAuthSettingBo> parseTopoAuthSettingBos(List<TopoAuthSettingBo> authSettingBos,List<User> users){
		List<TopoAuthSettingBo> settings = new ArrayList<TopoAuthSettingBo>() ;
		for(TopoAuthSettingBo auth:authSettingBos){
			for(int i=users.size()-1; i>=0 ;i--){
				User user = users.get(i);
				if((long)auth.getUserId() == (long)user.getId()){
					auth.setUserName(user.getName());
					settings.add(auth);		//加入新list：处理系统用户被减少，但拓扑以前使用过的情况
					users.remove(i);		//提高查询效率，同时过滤出新增系统用户
					break;
				}
			}
		}
		//处理新增加或未设置过权限的系统用户（默认都有查看权限）
		for(User user:users){
			TopoAuthSettingBo newBo = new TopoAuthSettingBo();
			newBo.setUserId(user.getId());
			newBo.setUserName(user.getName());
			newBo.setEditAuthInt(0);	//0：无权限，1：有权限
			newBo.setEditAuth(false);
			newBo.setSelectAuthInt(1);
			newBo.setSelectAuth(true);
			settings.add(newBo);
		}
		return settings;
	}

	@Override
	public boolean hasAuth(Long topoId, String[] modes) {
		ILoginUser user = getCurrentUser();
		return checkAuth(user, topoId, modes);
	}
	public boolean checkAuth(ILoginUser user,Long topoId,String[] modes){
		//匹配数据库字段
		List<String> tmpModes = new ArrayList<String>();
		if(modes!=null){
			tmpModes.addAll(Arrays.asList(modes));
		}
		//查询数据库是否有相应的权限(对于缺省的域管理员，系统管理员，管理者都有权限)
		return user.isSystemUser() || user.isManagerUser() || user.isDomainUser() || topoAuthSettingDao.hasAuth(user.getId(),topoId,tmpModes);
	}
	@Override
	public boolean hasAuth(ILoginUser user, Long topoId, String[] modes) {
		return checkAuth(user, topoId, modes);
	}
	@Override
	public TopoAuthSettingBo getAuthSetting(Long topoId) {
		return topoAuthSettingDao.getAuthSetting(getCurrentUser().getId(), topoId);
	}
	
	private ILoginUser getCurrentUser(){
		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
		return (ILoginUser) request.getSession().getAttribute(ILoginUser.SESSION_LOGIN_USER);
	}
}
