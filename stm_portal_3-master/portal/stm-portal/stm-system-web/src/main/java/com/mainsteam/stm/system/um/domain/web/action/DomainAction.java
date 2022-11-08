package com.mainsteam.stm.system.um.domain.web.action;


import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.system.um.domain.api.IDomainApi;
import com.mainsteam.stm.system.um.domain.bo.Domain;
import com.mainsteam.stm.system.um.domain.bo.DomainDcs;
import com.mainsteam.stm.system.um.relation.api.IUmRelationApi;
import com.mainsteam.stm.system.um.relation.bo.UmRelation;
import com.mainsteam.stm.system.um.relation.bo.UserDomain;
import com.mainsteam.stm.system.um.relation.bo.UserRole;
import com.mainsteam.stm.system.um.role.api.IRoleApi;
import com.mainsteam.stm.system.um.role.bo.Role;
import com.mainsteam.stm.system.um.user.api.IUserApi;
import com.mainsteam.stm.system.um.user.bo.User;
import com.mainsteam.stm.system.um.user.constants.UserConstants;

@Controller
@RequestMapping("/system/domain")
public class DomainAction extends BaseAction {
	@Autowired
	private IDomainApi stm_system_DomainApi;
	@Resource(name="stm_system_UmRelationApi")
	private IUmRelationApi umRelationApi;
	
	@Autowired
	private IUserApi userApi;
	@Autowired
	private IRoleApi roleApi;
	
	
	/**
	 * 新增域基本信息
	 * @param domain 域基本信息
	 * @return domain 新增成功后的域基本信息
	 * */
	@RequestMapping("/insert")
	public JSONObject insert(Domain domain,HttpSession session,String userRoleStr,String dcsStr) {
		domain.setCreatedTime(Calendar.getInstance().getTime());
		domain.setCreatorId(getLoginUser(session) == null ? 0 : getLoginUser(session).getId());//设置域创建人IP
		domain = stm_system_DomainApi.insert(domain);
		List<UmRelation> userRoleRelations = JSONObject.parseArray(userRoleStr, UmRelation.class);
		if(userRoleRelations!=null){
			for (UmRelation umRelation : userRoleRelations) {
				umRelation.setDomainId(domain.getId());
			}
			stm_system_DomainApi.updateUserRoleByDomainRel(userRoleRelations,domain.getId());
		}
		List<DomainDcs> domainDcs = JSONObject.parseArray(dcsStr,DomainDcs.class);
		if(domainDcs!=null){
			for (DomainDcs dcs : domainDcs) {
				dcs.setDomainId(domain.getId());
			}
			stm_system_DomainApi.updateDomainDcs(domainDcs,domain.getId());
		}
		return toSuccess(domain);
	}

	/**
	 * 通过ID获取域基本信息
	 * @param id 域ID
	 * */
	@RequestMapping("/get")
	public JSONObject get(long id,HttpSession session) {
		Domain domain = stm_system_DomainApi.get(id);
		return toSuccess(domain);
	}
	
	/**
	 * 通过域ID批量删除域基本信息
	 * @param ids 要删除的域ID数组
	 * @return boolean
	 * */
	@RequestMapping("/isDelete")
	public JSONObject isDelete(Long[] ids) {
		return toSuccess(stm_system_DomainApi.batchDel(ids));
	}


	/**
	 * 新增域的时候检查域名称是数据是否存在，不允许重复添加域名
	 * @param name 域名
	 * */
	@RequestMapping("/queryByname")
	public JSONObject queryByname(String name) {
		int count = stm_system_DomainApi.queryByName(name);
		return toSuccess(count);
	}

	/**
	 * 更新域基本信息
	 * @param domain 更新后的域基本信息
	 * */
	@RequestMapping("/update")
	public JSONObject update(Domain domain,String userRoleStr,String dcsStr) {
		stm_system_DomainApi.update(domain);
		List<UmRelation> userRoleRelations = JSONObject.parseArray(userRoleStr, UmRelation.class);
		if(userRoleRelations!=null){
			for (UmRelation umRelation : userRoleRelations) {
				umRelation.setDomainId(domain.getId());
			}
			stm_system_DomainApi.updateUserRoleByDomainRel(userRoleRelations,domain.getId());
		}
		List<DomainDcs> domainDcs = JSONObject.parseArray(dcsStr,DomainDcs.class);
		if(domainDcs!=null){
			for (DomainDcs dcs : domainDcs) {
				dcs.setDomainId(domain.getId());
			}
			stm_system_DomainApi.updateDomainDcs(domainDcs,domain.getId());
		}
		return toSuccess(domain);
	}

	
	/**
	 * 分页查询所有域列表
	 * */
	@RequestMapping("/domainPage")
	public JSONObject domainPage(Page<Domain,User> page,HttpSession session) {
		page.setCondition((User)getLoginUser(session));
		stm_system_DomainApi.pageSelect(page);
		return toSuccess(page);
	}

	/**
	 * 获取域、用户角色关联关系数据
	 * */
	@RequestMapping("getAllUserRole")
	public JSONObject getDomainUserRole(UserDomain userDomain){
		User cuser = new User();
		cuser.setName(userDomain.getUserName());
		List<User> resultUsers = new ArrayList<User>();
		List<User> users = userApi.queryAllUserNoPage(cuser);
		List<Role> roles = roleApi.queryAllRoleNoPage();
		for (User user : users) {
			if(user.getUserType()!=UserConstants.USER_TYPE_SYSTEM_ADMIN &&
					user.getUserType()!=UserConstants.USER_TYPE_SUPER_ADMIN){
				user.setRoles(roles);
				resultUsers.add(user);
			}
		}
		return toSuccess(resultUsers);
	}
	
	
	/**
	* @Title: queryUserRoleByDomain
	* @Description: 通过域ID查询域下用户角色
	* @param domainId
	* @return  JSONObject
	* @throws
	*/
	@RequestMapping("queryUserRoleByDomain")
	public JSONObject queryUserRoleByDomain(long domainId){
		UmRelation relation = new UmRelation();
		relation.setDomainId(domainId);
		return toSuccess(umRelationApi.getAllUmRelations(relation));
	}
	/**
	 * 获取域、DCS关联关系数据，设置是否被选中
	 * */
	@RequestMapping("getDomainDcs")
	public JSONObject getDomainDcs(Long domainId){
		Page<DomainDcs, DomainDcs> page = new Page<DomainDcs, DomainDcs>();
		page.setDatas(stm_system_DomainApi.getDomainDcs(domainId));
		return toSuccess(page);
	}
	
	
	/**
	 * 更新域、用户角色关联关系
	 * */
	@RequestMapping("updateDomainUserRoleRe")
	public JSONObject updateDomainUserRoleRe(String relationstr){
		List<UmRelation> relations = JSONObject.parseArray(relationstr,UmRelation.class);
		int result = umRelationApi.checkUpdate(relations);
		if(result>=relations.size())
			return toSuccess(true);
		else
			return toSuccess(false);
	}
	
	/**
	 * 更新域、DCS关联关系
	 * */
	@RequestMapping("updateDomainDcsRe")
	public JSONObject updateDomainDcsRe(String domainDcsStr,Long domainId){
		try {
			List<DomainDcs> domainDcs = JSONObject.parseArray(domainDcsStr,DomainDcs.class);
			if(domainDcs!=null){
				int result = stm_system_DomainApi.updateDomainDcs(domainDcs,domainId);
				if(result>=domainDcs.size())
					return toSuccess(true);
			}
			return toSuccess(false);
		} catch (Exception e) {
			e.printStackTrace();
			return toSuccess(false);
		}
	}
	
	/**
	 * 获取域下选择的DCS列表
	 * */
	@RequestMapping("getDomainDcsIsChecked")
	public JSONObject getDomainDcsIsChecked(long domainId){
		Page<DomainDcs, DomainDcs> page = new Page<DomainDcs, DomainDcs>();
		page.setDatas(stm_system_DomainApi.getDomainDcsIsChecked(domainId));
		return toSuccess(page);
	}

	
	/**
	 * 获取域下选择的所有用户列表，包括域管理员
	 * */
	@RequestMapping("getDomainUserIsChecked")
	public JSONObject getDomainUserIsChecked(Domain domain){
		Page<UserRole, Domain> page = new Page<UserRole, Domain>();
		page.setDatas(stm_system_DomainApi.getUserRolesByDomainId(domain.getId()));
		return toSuccess(page);
	}
	
	@RequestMapping("queryAllDomain")
	public JSONObject queryAllDomain(){
		ILoginUser user = getLoginUser();
		if (user.getUserType()!=UserConstants.USER_TYPE_SYSTEM_ADMIN &&
				user.getUserType()!=UserConstants.USER_TYPE_SUPER_ADMIN) {//判断当前登录用户是否为系统管理员，系统管理员可以查询所有域
			List<Role> roles = roleApi.getRoles(user.getId());
			for(Role role : roles){
				if(role.getId().equals(Role.ROLE_DOMAIN_MANAGER_ID)){//如果不是系统管理员，查询域管理员下的域
					return toSuccess(stm_system_DomainApi.getDomains(user.getId(), Role.ROLE_DOMAIN_MANAGER_ID));
				}
			}
		}
		return toSuccess(stm_system_DomainApi.getAllDomains());//系统管理员，查询所有域
	}
}
