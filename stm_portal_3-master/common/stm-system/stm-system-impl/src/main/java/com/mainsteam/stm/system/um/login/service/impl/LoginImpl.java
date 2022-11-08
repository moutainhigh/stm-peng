package com.mainsteam.stm.system.um.login.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.cache.IMemcache;
import com.mainsteam.stm.cache.MemCacheFactory;
import com.mainsteam.stm.license.LicenseCheckException;//zw
import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.platform.web.vo.IRight;
import com.mainsteam.stm.system.license.api.ILicenseApi;
import com.mainsteam.stm.system.license.exception.LicenseNotFoundException;
import com.mainsteam.stm.system.um.domain.api.IDomainApi;
import com.mainsteam.stm.system.um.login.api.ILoginApi;
import com.mainsteam.stm.system.um.login.bo.LoginUser;
import com.mainsteam.stm.system.um.relation.api.IUmRelationApi;
import com.mainsteam.stm.system.um.right.api.IRightApi;
import com.mainsteam.stm.system.um.right.bo.Right;
import com.mainsteam.stm.system.um.role.api.IRoleApi;
import com.mainsteam.stm.system.um.role.bo.Role;
import com.mainsteam.stm.system.um.user.api.IUserApi;
import com.mainsteam.stm.system.um.user.bo.User;
import com.mainsteam.stm.system.um.user.constants.UserConstants;
import com.mainsteam.stm.util.ClassPathUtil;

/**
 * <li>文件名称: LoginImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月29日
 * @author   ziwenwen
 */
@Service("stm_system_login_api")
public class LoginImpl implements ILoginApi{
	@Autowired
	private IRoleApi roleApi;

	@Autowired
	private IUserApi userApi;

	@Autowired
	private IDomainApi domainApi;

	@Autowired
	private IRightApi rightApi;
	
	@Autowired
	private IUmRelationApi umRelationApi;
	
	@Autowired
	private ILicenseApi licenseApi;

	private static final Logger logger= LoggerFactory.getLogger(LoginImpl.class);
	/**
	 * 根据账号获取用户及权限域信息<br/>
	 * 支持普通、域管理员、超级管理员
	 * @param
	 * @return
	 * @throws LicenseNotFoundException 
	 * @throws
	 */
	@Override
	public void setLoginUserRight(LoginUser loginUser) throws LicenseNotFoundException, LicenseCheckException {
		if(loginUser.getUserType()==1){//普通用户
			getCommonUser(loginUser);
			
			Map<Long, Set<IDomain>> rolDomain=loginUser.getRoleDomains();
			Set<Long> roles=rolDomain.keySet();
			Set<IDomain> commonDomains=new HashSet<IDomain>(),managerDomains=new HashSet<IDomain>(),
					domainManagerDomains=new HashSet<IDomain>();
			for(long role:roles){
				if(role==Role.ROLE_DOMAIN_MANAGER_ID){
					domainManagerDomains.addAll(rolDomain.get(role));
				}else if(role==Role.ROLE_MANAGER_ID){
					managerDomains.addAll(rolDomain.get(role));
				}else{
					commonDomains.addAll(rolDomain.get(role));
				}
			}
			loginUser.setDomainManageDomains(domainManagerDomains);
			loginUser.setManageDomains(managerDomains);
			loginUser.setCommonDomains(commonDomains);
		}else if(loginUser.getUserType()==2){//域用户
			
			getDomainAdministrator(loginUser);
			
		}else if(loginUser.getUserType()==3||loginUser.getUserType()==4){//系统管理员
			
			getSuperAdministrator(loginUser);
			
		}

		filterLicense(loginUser.getRights());
		
		filterRegisterPortal(loginUser.getRights());
	}

	/**
	 * <pre>
	 * malachi in
	 * license授权控制
	 * </pre>
	 * @param rights
	 * @throws LicenseNotFoundException 
	 */
	private void filterLicense(List<IRight> rights) throws LicenseNotFoundException{
		Map<Long,Integer> portals=licenseApi.getAuthorPortal();
		if(portals==null){
			rights.removeAll(rights);
		}else{
			for(int i=0,len=rights.size();i<len;i++){
				IRight right=rights.get(i);
				Integer num=portals.get(right.getId());
				if(num!=null&&(num.intValue()==0)){
					rights.remove(i--);
					len--;
				}
			}
		}
	}
	
	/**
	 * 注册门户屏蔽菜单
	 * @param rights
	 * @throws LicenseNotFoundException
	 */
	private void filterRegisterPortal(List<IRight> rights) throws LicenseNotFoundException{
		
		IMemcache<Boolean> icache=MemCacheFactory.getLocalMemCache(Boolean.class);
		
		Boolean isRegisterPortal=icache.get("ITM_REGISER");
		//System.out.println(isRegisterPortal);
		
		if(isRegisterPortal!=null && isRegisterPortal){
			
			Right right=new Right();
			//用户同步
			right.setId(234L);
			rights.remove(right);
			//单点登录
			right.setId(235L);
			rights.remove(right);
			//图片管理
			right.setId(219L);
			rights.remove(right);
		}
		
	}
	
	
	
	@Override
	public LoginUser login(String account) {
		//获取用户详情
		User user=userApi.getByAccount(account);
		
		LoginUser loginUser=null;
		
		if(user!=null){
			loginUser=new LoginUser();
			BeanUtils.copyProperties(user,loginUser);
		}
		
		return loginUser;
	}

	@Override
	public boolean loginOut(String account) {
		return true;
	}

	/**
	 * 获取超级管理员权限域信息
	 * @param
	 * @return
	 * @throws
	 */
	private void getCommonUser(LoginUser loginUser) throws LicenseCheckException{
		//获取用户下面所有的角色
		List<Role> roles=roleApi.getRoles(loginUser.getId());
		loginUser.setRoles(roles);
		//File regFile=new File(ClassPathUtil.getTomcatHome()+"ITM_REGISER.CODE");
		//System.out.println(regFile.exists());
		//用于存储用户权限信息
		Map<String,IRight> userRights=new HashMap<String,IRight>();

		Role role;
		List<Right> rights;
		Right right;
		IRight tempRight;
		Set<IDomain> domains;
		loginUser.setDomains(new HashSet<IDomain>());
		Map<Long, Set<IDomain>> rolMap=new HashMap<Long, Set<IDomain>>();
		loginUser.setRoleDomains(rolMap);
		
		List<Right> allRights=rightApi.getAll();
		
		
		for(int i=0,len=roles.size();i<len;i++){
			role=roles.get(i);
			
			//用户角色下的域
			domains=new HashSet<IDomain>(domainApi.getDomains(loginUser.getId(),role.getId()));
			loginUser.getDomains().addAll(domains);
			rolMap.put(role.getId(), domains);
			//角色下的权限
			if((role.getId()==Role.ROLE_DOMAIN_MANAGER_ID)||(role.getId()==Role.ROLE_MANAGER_ID)){
				rights=allRights;
			}else{
				rights=rightApi.getRights(role.getId());
			}
			//避免下面的改动影响rolMap值
			Set<IDomain> domainsTemp = new HashSet<IDomain>();
			domainsTemp.addAll(domains);
			
			for(int j=0,jlen=rights.size();j<jlen;j++){
				right=rights.get(j);
				//域管理员（系统管理内无人员管理权限，系统设置没有换肤，安全设置）
				int id = right.getId().intValue();
				
				/*if(regFile.exists() && regFile.isFile()){
					switch(id){
					case 219:
					continue;
					}
				};*/
				switch(id){
				case 118://人员管理
				case 220://换肤
				case 221://安全设置
					continue;
				}
				tempRight=userRights.get(right.getName());
				if(tempRight==null){
					right.setDomains(domainsTemp);
					userRights.put(right.getId().toString(),right);
				}else{
					tempRight.getDomains().addAll(domainsTemp);
				}
				
			}
			
		}
	
		loginUser.setRights(new ArrayList<IRight>(userRights.values()));
	}
	
	/**
	 * 获取超级管理员权限域信息
	 * @param
	 * @return
	 * @throws
	 */
	private void getSuperAdministrator(LoginUser loginUser) throws LicenseCheckException{
		
		//用于存储用户权限信息
		Map<String,IRight> userRights=new HashMap<String,IRight>();
		
		List<Right> rights=rightApi.getAll();
		Right right;
		
		Set<IDomain> domains=new HashSet<IDomain>(domainApi.getAllDomains());
		loginUser.setDomains(domains);
		
		for(int i=0,len=rights.size();i<len;i++){
			right=rights.get(i);
			right.setDomains(domains);
			//避免名字相同
//			userRights.put(right.getName(),right);
			userRights.put(right.getId().toString(),right);
		}

		loginUser.setRights(new ArrayList<IRight>(userRights.values()));
	}
	
	/**
	 * 获取域管理员及权限域信息
	 * @param
	 * @return
	 * @throws LicenseCheckException 
	 */
	private void getDomainAdministrator(LoginUser loginUser) throws LicenseCheckException{
		//用于存储用户权限信息
		Map<String,IRight> userRights=new HashMap<String,IRight>();
		
		List<Right> rights=rightApi.getAll();
		Right right;
		
		Set<IDomain> domains=new HashSet<IDomain>(domainApi.getDomains(loginUser.getId(),UserConstants.DOMAIN_USER_DEFAULT_ROLE_ID));
		//File regFile=new File(ClassPathUtil.getTomcatHome()+"ITM_REGISER.CODE");
		//System.out.println(regFile.exists());
		for(int i=0,len=rights.size();i<len;i++){
			right=rights.get(i);
			//域管理员（系统管理内无人员管理权限，系统设置没有换肤，安全设置）
			int id = right.getId().intValue();
			
			/*if(regFile.exists() && regFile.isFile()){
				switch(id){
				case 219:
				continue;
				}
			};*/
			switch(id){
			case 118://人员管理
			case 220://换肤
			case 221://安全设置
				continue;
			}
			
			right.setDomains(domains);
			userRights.put(right.getId().toString(),right);
		}

		loginUser.setRights(new ArrayList<IRight>(userRights.values()));
	}
}
