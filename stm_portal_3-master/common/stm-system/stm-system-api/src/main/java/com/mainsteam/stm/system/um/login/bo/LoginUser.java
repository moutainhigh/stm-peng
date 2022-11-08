package com.mainsteam.stm.system.um.login.bo;

import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.platform.web.vo.IRight;
import com.mainsteam.stm.platform.web.vo.IRole;
import com.mainsteam.stm.system.um.role.bo.Role;
import com.mainsteam.stm.system.um.user.bo.User;

/**
 * <li>文件名称: LoginUser.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月25日
 * @author   ziwenwen
 */
public class LoginUser extends User implements ILoginUser{

	private static final long serialVersionUID = -3526891904580191123L;
	
	private final Calendar loginTime=Calendar.getInstance();
	
	private List<IRight> rights;
	
	private Map<Long,Set<IDomain>> rightDomains;

	
	private boolean isCommonUser;
	
	private boolean isDomainUser;
	
	private boolean isSystemUser;
	
	private boolean isManagerUser;
	
	private Map<Long,Set<IDomain>> roleDomains;
	
	protected Set<IDomain> manageDomains;
	
	private Set<IDomain> domains;	
	
	protected Set<IDomain> commonDomains;

	protected Set<IDomain> domainManageDomains;
	
	@Override
	public Calendar getLoginTime() {
		return this.loginTime;
	}

	@Override
	public List<IRight> getRights() {
		return rights;
	}
	
	public void setRights(List<IRight> rights){
		rightDomains=new HashMap<Long, Set<IDomain>>();
		for(IRight right:rights){
			rightDomains.put(right.getId(),right.getDomains());
		}
		this.rights=rights;
	}

	@Override
	public Set<IDomain> getDomains(Long rightId) {
		return rightDomains.get(rightId);
	}

	@Override
	public Set<IDomain> getManageDomains() {
		return this.manageDomains;
	}

	@Override
	public Set<IDomain> getDomainManageDomains() {
		return this.domainManageDomains;
	}

	@Override
	public Set<IDomain> getCommonDomains() {
		return commonDomains;
	}

	public void setCommonDomains(Set<IDomain> commonDomains) {
		this.commonDomains=commonDomains;
	}

	public boolean isCommonUser() {
		return isCommonUser;
	}

	public boolean isDomainUser() {
		return isDomainUser;
	}

	public boolean isSystemUser() {
		return isSystemUser;
	}

	public boolean isManagerUser() {
		return isManagerUser;
	}
	
	public void setManageDomains(Set<IDomain> manageDomains) {
		this.manageDomains = manageDomains;
	}
	
	public void setDomainManageDomains(Set<IDomain> domainManageDomains) {
		this.domainManageDomains = domainManageDomains;
	}
	
	public void setUserType() {
		List<IRole> roles=this.getRoles();
		if(this.getUserType()==1){
			isCommonUser=true;
			long id;
			if(roles!=null){
				for(int i=0,size=roles.size();i<size;i++){
					if(isDomainUser&&isManagerUser){
						break;
					}
					id=roles.get(i).getId();
					if(id==Role.ROLE_MANAGER_ID){
						isManagerUser=true;
					}else if(id==Role.ROLE_DOMAIN_MANAGER_ID){
						isDomainUser=true;
					}
				}
			}
			
		}else{
			isSystemUser=true;
		}
	}

	public Map<Long,Set<IDomain>> getRoleDomains() {
		return roleDomains;
	}

	public void setRoleDomains(Map<Long,Set<IDomain>> roleDomains) {
		this.roleDomains = roleDomains;
	}

	public Set<IDomain> getDomains() {
		return domains;
	}

	public void setDomains(Set<IDomain> domains) {
		this.domains = domains;
	}

	private Map<String,Object> cached;
	
	@Override
	public Map<String, Object> getCache() {
		if(cached==null){
			cached=new HashMap<String, Object>();
		}
		return cached;
	}

}


