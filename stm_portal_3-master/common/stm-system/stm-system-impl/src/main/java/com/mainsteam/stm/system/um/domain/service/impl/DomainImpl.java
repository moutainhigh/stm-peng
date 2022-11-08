package com.mainsteam.stm.system.um.domain.service.impl;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.List;

import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import com.mainsteam.stm.node.Node;
import com.mainsteam.stm.node.NodeFunc;
import com.mainsteam.stm.node.NodeService;
import com.mainsteam.stm.node.NodeTable;
import com.mainsteam.stm.node.exception.NodeException;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.platform.sequence.service.SEQ;
import com.mainsteam.stm.platform.sequence.service.SequenceFactory;
import com.mainsteam.stm.system.um.domain.api.IDomainApi;
import com.mainsteam.stm.system.um.domain.api.IDomainReferencerRelationshipApi;
import com.mainsteam.stm.system.um.domain.bo.Domain;
import com.mainsteam.stm.system.um.domain.bo.DomainDcs;
import com.mainsteam.stm.system.um.domain.dao.IDomainDao;
import com.mainsteam.stm.system.um.relation.api.IUmRelationApi;
import com.mainsteam.stm.system.um.relation.bo.UmRelation;
import com.mainsteam.stm.system.um.relation.bo.UserDomain;
import com.mainsteam.stm.system.um.relation.bo.UserRole;
import com.mainsteam.stm.system.um.role.bo.Role;
import com.mainsteam.stm.system.um.user.bo.User;

@Service("stm_system_DomainApi")
public class DomainImpl implements IDomainApi,ApplicationContextAware {
	
	private ApplicationContext context;
	
	@Autowired
	private IDomainDao domainDao;
	@Autowired
	private IUmRelationApi umRelationApi;
	@Autowired
	private NodeService nodeService;
	private ISequence seq;
	@Autowired
	public DomainImpl(SequenceFactory sequenceFactory) {
		this.seq=sequenceFactory.getSeq(SEQ.SEQNAME_STM_SYSTEM_DOMAIN);
	}
	@Override
	public Domain insert(Domain domain) {
		long domainId = seq.next();
		domain.setId(domainId);
		domain.setCreatedTime(Calendar.getInstance().getTime());
		domainDao.insert(domain);
		return domain;

	}

	@Override
	public int addUserRoleByDomainRel(List<UmRelation> relations){
		return umRelationApi.addUmR(relations);
	}
	
	@Override
	public int updateUserRoleByDomainRel(List<UmRelation> relations,long domainId){
		if(relations!=null){
			UmRelation relation = new UmRelation();
			relation.setDomainId(domainId);
			List<UmRelation> nowRelations = umRelationApi.getAllUmRelations(relation);//通过域ID查询所有与该域相关的关联关系
			List<UmRelation> addRelations = new ArrayList<UmRelation>();//新添加的关联关系
			for (UmRelation newRelation : relations) {
				if(!this.relationContains(nowRelations, newRelation)){
					addRelations.add(newRelation);
				}
			}
			List<UmRelation> delRelations = new ArrayList<UmRelation>();//需要删除的关联关系
			for (UmRelation nowRelation : nowRelations) {
				if(!this.relationContains(relations, nowRelation)){
					delRelations.add(nowRelation);
				}
			}
			umRelationApi.batchDel(delRelations);
			umRelationApi.addUmR(addRelations);
		}
		return 1;
	}
	
	@Override
	public Domain get(long id) {
		return domainDao.get(id);
	}

	@Override
	public int update(Domain domain) {
		return domainDao.update(domain);
	}

	@Override
	public int queryByName(String name) {
		return domainDao.queryByName(name);
	}

	@Override
	public List<Domain> pageSelect(Page<Domain, User> page) {
		List<Domain> domains = domainDao.pageSelect(page);//查询出的域列表
		for (Domain domain : domains) {//遍历域列表，通过域域ID获取域管理员
			UmRelation relation = new UmRelation();
			relation.setDomainId(domain.getId());
			List<UserDomain> userDomains = this.getDomainAdmin(relation);//查询域下所有管理员
			String adminUsers = "";
			if(userDomains!=null){
				for (UserDomain userDomain : userDomains) {
					if(userDomain!=null && userDomain.getUserName()!=null){
						adminUsers+=userDomain.getUserName()+",";//拼接管理员姓名
					}
				}
				if(!StringUtils.isEmpty(adminUsers)){
					adminUsers = adminUsers.substring(0,adminUsers.length()-1);
				}
			}
			domain.setAdminUser(adminUsers);
		}
		page.setDatas(domains);
		return domains;
	}

	@Override
	public List<String> batchDel(Long[] ids) {
		Collection<IDomainReferencerRelationshipApi> drr = context.getBeansOfType(IDomainReferencerRelationshipApi.class).values();
		//允许删除的域ID
		List<Long> isDelIds = new ArrayList<Long>();
		
		//不允许删除的域ID
		List<Long> isNotAllowedDelIds = new ArrayList<>();
		if(drr!=null && drr.size()>0){
			for (Long did : ids) {
				boolean flag = false;
				//检查域是其他地方是否存在引用关系
				for (IDomainReferencerRelationshipApi drrApi : drr) {
					if(drrApi.checkDomainIsRel(did)){
						flag = true;
					}
				}
				//如果flag=true说明域在其他发方存在引用关系，不允许删除
				if(flag){
					isNotAllowedDelIds.add(did);
				}else{
					isDelIds.add(did);
				}
			}
		}else{
			isDelIds = Arrays.asList(ids);
		}
		
		List<UmRelation> relations = new ArrayList<UmRelation>();
		for (Long id : isDelIds) {
			UmRelation relation = new UmRelation();
			relation.setDomainId(id);
			relations.add(relation);
			domainDao.batchDeleteDomainDcsRel(id);//删除所有域与DCS的关系
		}
		int count = domainDao.batchDel(isDelIds.toArray(new Long[]{}));
		umRelationApi.batchDel(relations);//删除所有域与用户角色的关系
		
		List<String> result = new ArrayList<>();
		for (Long ndid : isNotAllowedDelIds) {
			Domain d = this.get(ndid);
			if(d!=null){
				result.add(d.getName());
			}
		}
		return result;
	}

	/**
	 * 获取域下所有管理员
	 */
	@Override
	public List<UserDomain> getDomainAdmin(UmRelation relation) {
		relation.setRoleId(Role.ROLE_DOMAIN_MANAGER_ID);
		return domainDao.getDomainAdmin(relation);
	}

	/**
	 * 根据domainId获取所有选中的UserRoles组合
	 */
	@Override
	public List<UserRole> getUserRolesByDomainId(Long domainId) {
		return domainDao.getUserRolesByDomainId(domainId);
	}

	@Override
	public List<DomainDcs> getDomainDcs(Long domainId) {
		try {
			NodeTable table = nodeService.getNodeTable();
			List<DomainDcs> newDomainDcs = new ArrayList<DomainDcs>();
			if (null != table) {
				List<Node> nodes = table.selectNodesByType(NodeFunc.collector);
				List<Integer> ids = new ArrayList<Integer>();
				List<DomainDcs> domainDcs = new ArrayList<DomainDcs>();
				if(domainId!=null){
					domainDcs = domainDao.getDomainDcsRel(domainId);
					if(domainDcs!=null){
						for (DomainDcs dcs : domainDcs) {
							ids.add(dcs.getDcsId());
						}
					}
				}
				for (Node node : nodes) {
					Node parNode = nodeService.getNodeById(node.getParentNodeId());
					if (ids==null || !ids.contains(node.getId())) {
						DomainDcs dDcs = new DomainDcs();
						dDcs.setDcsId(node.getId());
						dDcs.setDcsIp(node.getIp());
						dDcs.setDcsName(node.getName());
						dDcs.setDcsState(node.isAlive() ? 1 : 0);
						dDcs.setDomainId(domainId);
						dDcs.setIsChecked(0);
						dDcs.setDhs(parNode == null ? "" : parNode.getName());
						newDomainDcs.add(dDcs);
					} else {
						if(domainDcs!=null){
							for (DomainDcs dcs : domainDcs) {
								if (dcs.getDcsId() == node.getId()) {
									dcs.setDcsIp(node.getIp());
									dcs.setDcsIp(node.getIp());
									dcs.setDcsName(node.getName());
									dcs.setDcsState(node.isAlive() ? 1 : 0);
									dcs.setDomainId(domainId);
									dcs.setIsChecked(1);
									dcs.setDhs(parNode == null ? "" : parNode
											.getName());
									newDomainDcs.add(dcs);
								}
							}
						}
					}
				}
			}
			return newDomainDcs;
		} catch (NodeException e) {
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public int updateDomainDcs(List<DomainDcs> domainDcs,Long domainId) {
		int result = domainDao.batchDeleteDomainDcsRel(domainId);
		if (result >= 0) {
			return domainDao.batchInsertDomainDcsRel(domainDcs);
		} else
			return result;
	}

	@Override
	public List<DomainDcs> getDomainDcsIsChecked(Long domainId) {
		try {
			List<DomainDcs> newDomainDcs = new ArrayList<DomainDcs>();
			NodeTable table = nodeService.getNodeTable();
			if (null != table && null!=domainId) {
				List<Node> nodes = table.selectNodesByType(NodeFunc.collector);// 获取采集器列表
				List<DomainDcs> domainDcs = domainDao.getDomainDcsRel(domainId);// 获取域采集器关联关系表
				for (Node node : nodes) {
					Node parNode = nodeService.getNodeById(node.getParentNodeId());
					for (DomainDcs dcs : domainDcs) {
						if (dcs.getDcsId() == node.getId()) {
							dcs.setDcsIp(node.getIp());
							dcs.setDcsIp(node.getIp());
							dcs.setDcsName(node.getName());
							dcs.setDcsState(node.isAlive() ? 1 : 0);
							dcs.setDomainId(domainId);
							dcs.setDhs(parNode == null ? "" : parNode.getName());
							dcs.setIsChecked(1);
							newDomainDcs.add(dcs);
						}
					}
				}
			}
			return newDomainDcs;
		} catch (NodeException e) {
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public List<Domain> getAllDomains() {
		List<Domain> domains=domainDao.getAllDomains();
		Domain domain;
		for(int i=0,len=domains.size();i<len;i++){
			domain=domains.get(i);
			domain.setDcsNodes(this.getDomainNodesById(domain.getId()));
		}
		return domains;
	}

	@Override
	public List<Domain> getDomains(Long userId, Long roleId) {
		List<Domain> domains=domainDao.getDomains(userId, roleId);
		Domain domain;
		for(int i=0,len=domains.size();i<len;i++){
			domain=domains.get(i);
			domain.setDcsNodes(this.getDomainNodesById(domain.getId()));
		}
		return domains;
	}

	@Override
	public List<Node> getDomainNodesById(Long domainId) {
		try {
			List<Node> newNodes = null;
			if(domainId!=null && domainId>0){
				NodeTable table = nodeService.getNodeTable();
				if (null != table) {
					List<Node> nodes = table.selectNodesByType(NodeFunc.collector);// 获取采集器列表
					List<DomainDcs> domainDcs = domainDao.getDomainDcsRel(domainId);// 获取域采集器关联关系表
					newNodes = new ArrayList<Node>();
					for (Node node : nodes) {
						for (DomainDcs dcs : domainDcs) {
							if (dcs.getDcsId() == node.getId()) {
								newNodes.add(node);
							}
						}
					}
				}
			}
			return newNodes;
		} catch (NodeException e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/**
	* @Title: relationContains
	* @Description: 验证关联关系在集合中是否存在
	* @param srcRelations
	* @param relation
	* @return  boolean
	* @throws
	*/
	private boolean relationContains(List<UmRelation> srcRelations,UmRelation relation){
		for (UmRelation umRelation : srcRelations) {
			if(umRelation!=null && relation!=null){
				if(umRelation.getDomainId().equals(relation.getDomainId())&& umRelation.getUserId().equals(relation.getUserId())&& umRelation.getRoleId().equals(relation.getRoleId())){
					return true;
				}
			}
		}
		return false;
	}
	@Override
	public List<User> queryUserByDomains(Long[] ids) {
		if(ids!=null && ids.length>0){
			return domainDao.queryDomainUsers(ids);
		}
		return null;
	}
	@Override
	public void setApplicationContext(ApplicationContext arg0) throws BeansException {
		this.context = arg0;
	}
}
