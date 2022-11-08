package com.mainsteam.stm.system.um.role.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.mainsteam.stm.license.LicenseCheckException;//zw
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.system.license.api.ILicenseApi;
import com.mainsteam.stm.system.license.bo.PortalModule;
import com.mainsteam.stm.system.um.relation.bo.UserDomain;
import com.mainsteam.stm.system.um.right.bo.Right;
import com.mainsteam.stm.system.um.right.dao.IRightDao;
import com.mainsteam.stm.system.um.role.api.IRoleApi;
import com.mainsteam.stm.system.um.role.bo.Role;
import com.mainsteam.stm.system.um.role.bo.RoleRight;
import com.mainsteam.stm.system.um.role.bo.RoleRightRel;
import com.mainsteam.stm.system.um.role.dao.IRoleDAO;

/**
 * <li>文件名称: RoleImpl.java</li>
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
public class RoleImpl implements IRoleApi{

	private IRightDao rightDao;
	private IRoleDAO iRoleDAO;
	private ISequence roleSeq;
	@Autowired
	private ILicenseApi licenseApi;
	//关联表的id序列
//	private ISequence umRelationSeq;
//	private IUmRelationApi umRelationApi;
	
//	public ISequence getUmRelationSeq() {
//		return umRelationSeq;
//	}
//
//	public void setUmRelationSeq(ISequence umRelationSeq) {
//		this.umRelationSeq = umRelationSeq;
//	}

//	public IUmRelationApi getUmRelationApi() {
//		return umRelationApi;
//	}

//	public void setUmRelationApi(IUmRelationApi umRelationApi) {
//		this.umRelationApi = umRelationApi;
//	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.role.api.IRoleApi#PageSelect(com.mainsteam.stm.system.um.role.bo.RolePage)
	 */
	@Override
	public void pageSelect(Page<Role, Role> page) {
		iRoleDAO.pageSelect(page);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.role.api.IRoleApi#insert(com.mainsteam.stm.system.um.role.bo.Role)
	 */
	@Override
	public Long insert(Role role) {
		Long roleId=roleSeq.next();
		role.setId(roleId);
		role.setCreatedTime(new Date());
		iRoleDAO.insert(role);
		return role.getId();
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.role.api.IRoleApi#batchDel(java.util.List)
	 */
	@Override
	public int batchDel(Long[] ids) {
		if(ids.length==0){
			return 0;
		}
		int count=iRoleDAO.batchDel(ids);
		iRoleDAO.delRelationByRoleIds(ids);
		return count;
	}

	public void setiRoleDAO(IRoleDAO iRoleDAO) {
		this.iRoleDAO = iRoleDAO;
	}

	public void setRoleSeq(ISequence roleSeq) {
		this.roleSeq = roleSeq;
	}


	public void setRightDao(IRightDao rightDao) {
		this.rightDao = rightDao;
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.role.api.IRoleApi#get(long)
	 */
	@Override
	public Role get(long id) {
		return iRoleDAO.get(id);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.role.api.IRoleApi#update(com.mainsteam.stm.system.um.role.bo.Role)
	 */
	@Override
	public int update(Role role) {
		return iRoleDAO.update(role);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.role.api.IRoleApi#checkName(com.mainsteam.stm.system.um.role.bo.Role)
	 */
	@Override
	public int checkName(Role role) {
		return iRoleDAO.getRoleCountByNameId(role);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.role.api.IRoleApi#getRight(java.lang.Long)
	 */
	@Override
	public List<RoleRight> getRight(Long roleId) throws LicenseCheckException {
		return filterRights(iRoleDAO.getRightByRoleId(roleId));
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.role.api.IRoleApi#getRight(java.lang.Long[])
	 */
	@Override
	public List<RoleRight> getRight(Long[] roleIds) throws LicenseCheckException {
		return filterRights(iRoleDAO.getRightByRoleIds(roleIds));
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.role.api.IRoleApi#batchUpdateRight(java.util.List)
	 */
	@Override
	public int batchUpdateRight(List<RoleRightRel> roles) {
		List<List<Long>> list = new ArrayList<List<Long>>();
		Long RoleId = roles.get(0).getRoleId();
		for(RoleRightRel rel: roles){
			try{
			List<Long> rights=  rightDao.findIdByPid(rel.getRightId());
			
			if(!rights.isEmpty()){
				if(rights.contains((long)103)||rights.contains((long)102)){
					rights.add((long) 209);
					rights.add((long) 210);
					rights.add((long) 238);
					rights.remove((long)102);
				}
				list.add(rights);
				
				/*for(long right:rights){
					RoleRightRel RoleRight = new RoleRightRel();
					RoleRight.setRoleId(RoleId);
					RoleRight.setRightId(right);
					roles.add(RoleRight);
				}*/
			}
			
			}catch(Exception e){
				 e.printStackTrace();
				
			}
		};
		for(List<Long> rightlsit : list){
			for(long rightId : rightlsit){
				RoleRightRel RoleRight = new RoleRightRel();
				RoleRight.setRoleId(RoleId);
				RoleRight.setRightId(rightId);
				roles.add(RoleRight);
			}
			
		}
		
		if(roles.size()>0){
			iRoleDAO.delRoleRightRelByRoleId(roles.get(0).getRoleId());
			iRoleDAO.addRoleRightRels(roles);
		}
		return -1;
	}


	@Override
	public List<Role> getRoles(Long userId) {
		return iRoleDAO.getRoles(userId);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.role.api.IRoleApi#checkRelUser(java.lang.Long[])
	 */
	@Override
	public boolean checkRelUser(Long[] ids) {
		for(Long id : ids){
			if(iRoleDAO.getRelByRoleId(id)>0){
				return true;
			}
		}
		return false;
	}

	@Override
	public List<Role> queryAllRoleNoPage() {
		return iRoleDAO.queryAllRoles();
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.role.api.IRoleApi#getUserDomainByRoleId(java.lang.Long)
	 */
	@Override
	public List<UserDomain> getUserDomainByRoleId(Long id) {
		return iRoleDAO.getUserDomainByRoleId(id);
	}
	private List<RoleRight> filterRights(List<RoleRight> rights) throws LicenseCheckException{
		List<PortalModule> modules = licenseApi.getPortalModules();
		List<RoleRight> results = new ArrayList<RoleRight>();
		List<Long> exceptIds = new ArrayList<Long>();
		
		for(PortalModule module : modules){
			if(!module.isAuthor()){
				exceptIds.add(module.getId());
			}
		}
		
		for(RoleRight right : rights){
			if(exceptIds.indexOf(right.getRightId())==-1&&right.getpId()==0){
				results.add(right);
			}
		}
		return results;
	}
}
