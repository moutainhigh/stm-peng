package com.mainsteam.stm.system.um.relation.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mainsteam.stm.system.um.relation.api.IUmRelationApi;
import com.mainsteam.stm.system.um.relation.bo.DomainRole;
import com.mainsteam.stm.system.um.relation.bo.UmRelation;
import com.mainsteam.stm.system.um.relation.bo.UserDomain;
import com.mainsteam.stm.system.um.relation.bo.UserRole;
import com.mainsteam.stm.system.um.relation.dao.IUmRelationDao;
//@Service("stm_system_UmRelationApi")
public class UmRelationImpl implements IUmRelationApi{
//	@Autowired
	private IUmRelationDao umRelationDao;
	

	public void setUmRelationDao(IUmRelationDao umRelationDao) {
		this.umRelationDao = umRelationDao;
	}

	@Override
	public int addUmR(List<UmRelation> umRs) {
		return umRelationDao.batchInsert(umRs);
	}

	
	@Deprecated
	@Override
	public int checkUpdate(List<UmRelation> umRs) {
		return umRelationDao.batchUpdate(umRs);
	}


	


	@Override
	public int batchDel(List<UmRelation> umRelations) {
		
		return umRelationDao.batchDel(umRelations);
	}
	
	@Override
	public int delRelation(UmRelation umRelation){
		List<UmRelation> relations = new ArrayList<UmRelation>();
		if(umRelation!=null)relations.add(umRelation);
		return umRelationDao.batchDel(relations);
	}

	@Override
	public List<UserRole> getUserRoleByDomain() {
		
		return umRelationDao.getUserRole();
	}

	@Override
	public List<UserDomain> getUserDomainByRole() {
		
		return umRelationDao.getUserDomain();
	}

	@Override
	public List<DomainRole> getDomainRoleByUser() {
		
		return umRelationDao.getDomainRole();
	}

	@Override
	public List<UserRole> getUserRoleByDomainId(UserDomain userDomain) {
		
		return umRelationDao.getUserRoleByDomainId(userDomain);
	}

	@Override
	public List<UserDomain> getUserDomainByRoleId(Long roleId) {
		
		return umRelationDao.getUserDomainByRoleId(roleId);
	}

	@Override
	public List<DomainRole> getDomainRoleByUserId(Long userId) {
		
		return umRelationDao.getDomainRoleByUserId(userId);
	}

	@Override
	public List<UserRole> getUserRoleForAdmin() {
		
		return umRelationDao.getUserRoleForAdmin();
	}

	@Override
	public List<UserDomain> getUserDomainForAdmin() {
		
		return umRelationDao.getUserDomainForAdmin();
	}

	@Override
	public List<DomainRole> getDomainRoleForAdmin() {
		
		return umRelationDao.getDomainRoleForAdmin();
	}

	@Override
	public List<UmRelation> getAllUmRelations(UmRelation relation) {
		
		return umRelationDao.getAllUmRelations(relation);
	}

}
