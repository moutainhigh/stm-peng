package com.mainsteam.stm.system.um.user.service.impl;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;

import com.mainsteam.stm.export.excel.ExcelUtil;
import com.mainsteam.stm.export.excel.IExcelConvert;
import com.mainsteam.stm.export.excel.ReadExcelArgs;
import com.mainsteam.stm.instancelib.interceptor.InstancelibEvent;
import com.mainsteam.stm.instancelib.interceptor.InstancelibListener;
import com.mainsteam.stm.instancelib.objenum.EventEnum;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.system.um.domain.bo.Domain;
import com.mainsteam.stm.system.um.relation.api.IUmRelationApi;
import com.mainsteam.stm.system.um.relation.bo.UmRelation;
import com.mainsteam.stm.system.um.role.bo.Role;
import com.mainsteam.stm.system.um.user.api.IUserApi;
import com.mainsteam.stm.system.um.user.bo.DomainRole;
import com.mainsteam.stm.system.um.user.bo.User;
import com.mainsteam.stm.system.um.user.bo.UserResourceRel;
import com.mainsteam.stm.system.um.user.constants.UserConstants;
import com.mainsteam.stm.system.um.user.dao.IUserDao;
import com.mainsteam.stm.system.um.user.vo.UserConditionVo;
import com.mainsteam.stm.util.CipherUtil;
/**
 * 
 * <li>文件名称: com.mainsteam.stm.system.um.user.service.impl.UserImpl.java</li>
 * <li>文件描述: 本类描述</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 用户管理模块</li>
 * <li>其他说明:此类做为公共接口的实现类，所以不允许使用注解</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年10月24日
 */
@SuppressWarnings("unchecked")
public class UserImpl implements IUserApi, InstancelibListener{
	
	//关联表的id序列
	
//	private ISequence umRelationSeq;
	
	private IUmRelationApi umRelationApi;

	private IUserDao userDao;
	
	private ISequence seq;
	
	private static final String DEFAULT_PASSWORD = CipherUtil.MD5Encode("111111");
	
	public void setUserDao(IUserDao userDao){
		this.userDao = userDao;
	}
	
//	public void setUmRelationSeq(ISequence umRelationSeq) {
//		this.umRelationSeq = umRelationSeq;
//	}

	public void setUmRelationApi(IUmRelationApi umRelationApi) {
		this.umRelationApi = umRelationApi;
	}

	public void setSeq(ISequence seq){
		this.seq = seq;
	}
	
	@Override
	public int add(User user) {
		user.setCreatedTime(new Date());
		user.setId(seq.next());
		user.setPassword(CipherUtil.MD5Encode(user.getPassword()));
		user.setUpPassTime(new Date());
		int count = userDao.insert(user);
		return count;
	}

	@Override
	public int update(User user) {
		User oldUser = userDao.get(user.getId());
		if(oldUser.getUserType()!=user.getUserType()){
			userDao.delUserRelations(new Long[]{user.getId()});
			
			if(oldUser.getUserType()==UserConstants.USER_TYPE_ORDINARY_USERS){
				userDao.deleteUserResourceRel(user.getId());	//删除其资源权限
			}
		}
		user.setCreatedTime(new Date());
		String password = user.getPassword();
		user.setPassword(StringUtils.isNotBlank(password) ? CipherUtil.MD5Encode(password) : "");
		return userDao.update(user);
	}

	@Override
	public User get(Long id) {
		return userDao.get(id);
	}

	@Override
	public void selectByPage(Page<User, UserConditionVo> userPage) {
		userDao.pageSelect(userPage);
	}

	@Override
	public int checkByAccount(User user) {
		return userDao.queryByAccount(user);
	}

	@Override
	public int batchDel(Long[] ids) {
		if(ids.length==0){
			return 0;
		}
		int count=userDao.batchDel(ids);
		
		userDao.delUserRelations(ids);
		return count;
	}

	@Override
	public int batchUpdate(List<User> users) {
		int count=userDao.batchUpdate(users);
		return count;
	}

	@Override
	public User getByAccount(String account) {
		List<User> users = userDao.getByAccount(account);
		return users.isEmpty()? null : users.get(0);
	}

	@Override
	public int updatePassword(String account, String password) {
		User user = new User();
		user.setAccount(account);
		user.setPassword(CipherUtil.MD5Encode(password));
		user.setUpPassTime(new Date());
		return userDao.updatePassword(user);
	}

	@Override
	public void getAllUserByPage(Page<User, User> page) {
		 userDao.getAllUserByPage(page);
	}

	@Override
	public List<UserResourceRel> getUserResources(Long userId) {
		return userDao.getUserResourceRel(userId);
	}

	@Override
	public List<UserResourceRel> getUserResources(Long userId,Long domainId) {
		return userDao.getUserResourceRel(userId,domainId);
	}
	
	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.user.api.IUserApi#saveUserResources(java.util.List, java.lang.Long)
	 */
	@Override
	public int saveUserResources(List<UserResourceRel> userResources, Long userId) throws Exception{
		if(userId!=null){
			userDao.deleteUserResourceRel(userId);
		}else{
			throw new IllegalArgumentException("userId is not allowed null");
		}
		
		if(userResources!=null&&userResources.size()>0){
			return userDao.saveUserResourceRels(userResources);
		}
		return 0;
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.user.api.IUserApi#updateDomainRole(java.util.List)
	 */
	@Override
	public int updateDomainRole(List<UmRelation> relations, Long id) {
		if(id==null){
			return -1;
		}
		if(relations.size()>0){
			UserResourceRel rel = new UserResourceRel();
			List<Long> domainIds = new ArrayList<Long>();
			List<Long> exceptIds = new ArrayList<Long>();
			for(UmRelation relation : relations){
				Long roleId = relation.getRoleId();
				Long domainId = relation.getDomainId();
				if((roleId==Role.ROLE_MANAGER_ID||roleId==Role.ROLE_DOMAIN_MANAGER_ID)&&!exceptIds.contains(domainId)){
					exceptIds.add(domainId);
				}
				if(!domainIds.contains(domainId)){
					domainIds.add(domainId);
				}
			}
			domainIds.removeAll(exceptIds);
			rel.setUserId(relations.get(0).getUserId());
			rel.setDomainIds(domainIds);
			userDao.deleteUserResourceRelByDomains(rel);	//更新关联关系时  需要删除用户关联的资源信息（避免用户不在之前的域，还拥有资源权限的问题）
		}
		UmRelation um = new UmRelation();
		um.setUserId(id);
		umRelationApi.delRelation(um);
		return umRelationApi.addUmR(relations);
	}
	
	@Override
	public Map<Long, User> getAll() {
		List<User> users=userDao.getAll(null);
		Map<Long,User> uMap=new HashMap<Long, User>(64);
		User user;
		for(int i=0,len=users.size();i<len;i++){
			user=users.get(i);
			uMap.put(user.getId(),user);
		}
		return uMap;
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.user.api.IUserApi#getDomainRole()
	 */
	@Override
	public List<DomainRole> getDomainRole() {
		List<DomainRole> domainRoles = userDao.getAllDomains();
		List<Role> roles = userDao.getAllRoles();
		for(DomainRole domainRole : domainRoles){
			domainRole.setRoles(roles);
		}
		return domainRoles;
	}
	
	@Override
	public List<User> queryAllUserNoPage(User user) {
		return userDao.getAll(user);
	}
	
	@Override
	public List<User> queryAllUserNoPage() {
		return userDao.getAll(null);
	}

	@Override
	public List<UmRelation> getDomainRoleRel(Long id) {
		return umRelationApi.getAllUmRelations(new UmRelation(id));
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.user.api.IUserApi#getDomainRoleByUserId(java.lang.Long)
	 */
	@Override
	public List<DomainRole> getDomainRoleByUserId(Long id) {
		return userDao.getDomainRoleByUserId(id);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.user.api.IUserApi#getUserResources(java.lang.Long, java.util.List)
	 */
	
	@Override
	public List<UserResourceRel> getUserResources(Long userId,
			Collection<Long> domainIds) {
		domainIds = domainIds!=null ? domainIds : Collections.EMPTY_LIST;
		return userDao.getUserResourceRelByDomains(userId,domainIds);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.user.api.IUserApi#getUserCommonDomains(java.lang.Long)
	 */
	@Override
	public List<Domain> getUserCommonDomains(Long id) {
		return userDao.getUserCommonDomains(id);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.user.api.IUserApi#getDomainUsersDomains(java.lang.Long)
	 */
	@Override
	public List<Domain> getDomainUsersDomains(Long id) {
		return userDao.getDomainUsersDomains(id);
	}

	@Override
	public List<User> getUsersByType(int userType) {
		return userDao.getUsersByType(userType);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.user.api.IUserApi#exportUsers(int)
	 */
	@Override
	public List<User> exportUsers(UserConditionVo conditions) {
		return userDao.getUsers(conditions);
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.user.api.IUserApi#saveUsers(java.io.InputStream)
	 */
	@Override
	public String saveUsers(InputStream inputStream, ILoginUser loginuser) {
		final StringBuilder emsg = new StringBuilder();
		final long creatorId = loginuser.getId();
		ExcelUtil<User> exportUtil = new ExcelUtil<User>();
		List<User> users = null;
		try{
			ReadExcelArgs args = new ReadExcelArgs(inputStream, new IExcelConvert() {
				@Override
				public Object convert(List<String> cellsValue, int rowIndex) {
					User user = new User();
					
					user.setId(seq.next());
					user.setPassword(DEFAULT_PASSWORD);
					user.setCreatedTime(new Date());
					user.setUpPassTime(new Date());
					user.setCreatorId(creatorId);
					rowIndex += 1;
					String userType = cellsValue.get(0);
					if(StringUtils.isEmpty(userType)){
						return null;
					}else if("普通用户".equals(userType)||"系统管理员".equals(userType)){
						user.setUserType("普通用户".equals(userType) ? 1 : 3);
					}else{
						emsg.append("第" + rowIndex + "行:用户类型【" + userType + "】不合格");
						return null;
					}
					String account = cellsValue.get(1);
					if(StringUtils.isEmpty(account)){
						return null;
					}else if(account.length()>=3&&account.length()<=32){
						user.setAccount(account);
					}else{
						emsg.append("第" + rowIndex + "行:用户名【" + account + "】长度需为3-32");
						return null;
					}
					String name = cellsValue.get(2);
					if(StringUtils.isEmpty(name)){
						return null;
					}else if(name.length()>=1&&name.length()<=20){
						user.setName(name);
					}else{
						emsg.append("第" + rowIndex + "行:姓名【" + name + "】长度需为1-20");
						return null;
					}
					String sex = cellsValue.get(3);
					if(StringUtils.isEmpty(sex)){
						return null;
					}else if("男".equals(sex)||"女".equals(sex)){
						user.setSex("男".equals(sex) ? 0 : 1);
					}else{
						emsg.append("第" + rowIndex + "行:性别【" + sex + "】不合格");
						return null;
					}
					String mobile = cellsValue.get(4);
					if (!StringUtils.isEmpty(mobile)) {
						if(mobile.matches("[0-9]+")){
							user.setMobile(mobile);
						}else{
							emsg.append("第" + rowIndex + "行:手机号【" + mobile + "】不合法");
							return null;
						}
					
					} else {
						user.setMobile("");
					}
					
					String email = cellsValue.get(5);
					if (!StringUtils.isEmpty(email)) {
						if(email.matches("^\\s*\\w+(?:\\.{0,1}[\\w-]+)*@[a-zA-Z0-9]+(?:[-.][a-zA-Z0-9]+)*\\.[a-zA-Z]+\\s*$")){
							user.setEmail(email);
						}else{
							emsg.append("第" + rowIndex + "行:邮件【" + email + "】地址不合法");
							return null;
						}
					} else {
						user.setEmail("");
					}
					
					String status = cellsValue.get(6);
					if(StringUtils.isEmpty(status)){
						return null;
					}else if("启用".equals(status)||"停用".equals(status)){
						user.setStatus("启用".equals(status) ? 1 : 0);
					}else{
						emsg.append("第" + rowIndex + "行:状态【" + status + "】不合法");
						return null;
					}
					return user;
				}
			});
			args.setIgnoreRows(2);
			users = exportUtil.readExcelContent(args);
		}catch(Exception e){
			e.printStackTrace();
			emsg.append("导入失败！！");
		}
		String errorMsg = emsg.toString();
		if(StringUtils.isNotBlank(errorMsg)){
			return errorMsg;
		}
		StringBuilder warings = new StringBuilder("[");
		List<User> userList = new ArrayList<User>();
		for(User user : users){
			userList = userDao.getByAccount(user.getAccount());
			if(userList.size() < 1){
				userDao.insert(user);
			}else{
				warings.append(user.getAccount()+",");
			}
		}
		warings.append("]");
		String waringMsg = warings.toString().replace(",]", "]");
		return waringMsg.length()==2 ? "success" : waringMsg;
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.user.api.IUserApi#checkResourceGroupIsRelUser(java.lang.Long)
	 */
	@Override
	public Boolean checkResourceGroupIsRelUser(Long resourceGroupId) {
		int result = userDao.getUserResourceGroupRelCount(resourceGroupId);
		return result!=0;
	}

	@Override
	public User getUserByAccount(String account) {
		List<User> users = userDao.getByAccount(account);
		return (users==null || users.size()<=0 ) ? null:users.get(0);
	}

	@Override
	public void listen(InstancelibEvent instancelibEvent) throws Exception {
		if(instancelibEvent.getEventType() == EventEnum.INSTANCE_DELETE_EVENT){
			List<Long> deleteIds = (List<Long>)instancelibEvent.getSource();
			if(deleteIds != null && deleteIds.size() > 0){
				Long[] ids = new Long[deleteIds.size()];
				for (int i = 0; i < deleteIds.size(); i++) {
					ids[i] = deleteIds.get(i);
				}
				userDao.deleteUserResourceRelByResourceIds(ids);
			}
		}
	}

	@Override
	public int updatePassErrorCnt2Zero() {
		return userDao.updatePassErrorCnt2Zero();
	}

	@Override
	public int updateUpPassTime2Now() {
		return userDao.updateUpPassTime2Now();
	}
}
