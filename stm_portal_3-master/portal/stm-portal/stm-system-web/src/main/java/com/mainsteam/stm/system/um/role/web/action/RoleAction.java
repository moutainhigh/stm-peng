package com.mainsteam.stm.system.um.role.web.action;

import java.util.Collections;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.license.LicenseCheckException;//zw
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.system.um.relation.api.IUmRelationApi;
import com.mainsteam.stm.system.um.role.api.IRoleApi;
import com.mainsteam.stm.system.um.role.bo.Role;
import com.mainsteam.stm.system.um.role.web.vo.RoleRightRelParameterVo;

/**
 * <li>文件名称: RoleAction.java</li>
 * <li>文件描述: 本类描述</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年8月19日
 */
@SuppressWarnings("unchecked")
@Controller
@RequestMapping(value="/system/role")
public class RoleAction extends BaseAction{
	
	@Resource(name="stm_system_role_impl")
	private IRoleApi roleApi;
	
	@Resource(name="stm_system_UmRelationApi")
	private IUmRelationApi iUmRelationApi;
	/**
	 * 角色分页信息
	 * @param rolePage
	 * @return
	 * @author  ziwen
	 * @date	2019年8月19日
	 */
	@RequestMapping(value="/rolePage")
	public JSONObject rolePage(Page<Role, Role> page, Role role){
		page.setCondition(role);
		roleApi.pageSelect(page);
		return toSuccess(page);
	}
	/**
	 * 写入角色信息
	 * @param role
	 * @return
	 * @author  ziwen
	 * @date	2019年8月19日
	 */
	@RequestMapping(value="/insert")
	public JSONObject insert(Role role, HttpSession session){
		ILoginUser iLoginUser = this.getLoginUser(session);
		role.setCreatorId(iLoginUser.getId());	//此处需要从Session中获取
		roleApi.insert(role);
		return toSuccess(role);
	}
	/**
	 * 删除用户信息，此处为批量删除
	 * @param roles
	 * @return
	 * @author  ziwen
	 * @date	2019年8月19日
	 */
	@RequestMapping(value="/del")
	public JSONObject del(Long[] ids){
		return toSuccess(roleApi.batchDel(ids));
	}
	/**
	 * 通过主键获取角色信息
	 * @param id
	 * @return
	 * @author  ziwen
	 * @date	2019年8月19日
	 */
	@RequestMapping(value="/get")
	public JSONObject get(Long id){
		return toSuccess(roleApi.get(id));
	}
	/**
	 * 更新角色信息
	 * @param role
	 * @return
	 * @author  ziwen
	 * @date	2019年8月19日
	 */
	@RequestMapping(value="/update")
	public JSONObject update(Role role){
		return toSuccess(roleApi.update(role));
	}
	
	/**
	 * 更新角色信息
	 * @param ids
	 * @return
	 * @author  ziwen
	 * @date	2019年8月19日
	 */
//	@Deprecated
//	@RequestMapping(value="/userDomainUpdate")
//	public JSONObject userDomainUpdate(UserDomainRefVo userDomainRefVo){
//		return toSuccess(iUmRelationApi.checkUpdate(userDomainRefVo.getUmRelations()==null ? Collections.EMPTY_LIST : userDomainRefVo.getUmRelations()));
//	}
	
	/**
	 * 判断用户名是否可用
	 * @param role
	 * @return
	 * @author  ziwen
	 * @date	2019年8月19日
	 */
	@RequestMapping(value="/checkName")
	public JSONObject checkName(Role role){
		return toSuccess(roleApi.checkName(role));
	}
	/**
	 * 通过角色id获取用户角色关联关系
	 * @param id
	 * @return
	 * @author  ziwen
	 * @date	2019年8月19日
	 */
	@RequestMapping(value="/getUserDomain")
	public JSONObject getUserDomain(Long id){
		return toSuccess(roleApi.getUserDomainByRoleId(id));
	}
	/**
	 * 通过角色获取权限信息
	 * @param id
	 * @return
	 * @author  ziwen
	 * @throws LicenseCheckException 
	 * @date	2019年8月23日
	 */
	@RequestMapping(value="/getRights")
	public JSONObject getRights(Long id) throws LicenseCheckException{
		return toSuccess(roleApi.getRight(id));
	}
	
	@RequestMapping(value="/rightUpdate")
	public JSONObject rightUpdate(RoleRightRelParameterVo vo){
		return toSuccess(roleApi.batchUpdateRight(vo.getRoleRightRels()==null ? Collections.EMPTY_LIST : vo.getRoleRightRels()));
	}
	
	/**
	 * 检测角色是否已经关联到用户
	 * @param ids
	 * @return
	 * @author	ziwen
	 * @date	2019年10月9日
	 */
	@RequestMapping(value="/checkRelUser")
	public JSONObject checkRelUser(Long [] ids){
		return toJsonObject(roleApi.checkRelUser(ids) ? CODE_SUCCESS_GOURPNAMEEXSIT : CODE_SUCCESS, "");
	}
	
	/**
	* @Title: queryAllRoles
	* @Description:获取所有角色
	* @return  JSONObject
	* @throws
	*/
	@RequestMapping("queryAllRoles")
	public JSONObject queryAllRoles(){
		return toSuccess(roleApi.queryAllRoleNoPage());
	}
}
