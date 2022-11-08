package com.mainsteam.stm.portal.resource.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.beans.BeanUtils;

import com.mainsteam.stm.instancelib.DiscoverPropService;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.DiscoverProp;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.portal.resource.api.IAccountApi;
import com.mainsteam.stm.portal.resource.api.IDiscoverResourceApi;
import com.mainsteam.stm.portal.resource.api.IReAccountInstanceApi;
import com.mainsteam.stm.portal.resource.bo.AccountBo;
import com.mainsteam.stm.portal.resource.bo.ReAccountInstanceBo;
import com.mainsteam.stm.portal.resource.dao.IAccountDao;
import com.mainsteam.stm.portal.resource.po.AccountPo;
import com.mainsteam.stm.system.um.domain.api.IDomainReferencerRelationshipApi;
import com.mainsteam.stm.util.SecureUtil;

public class AccountImpl implements IAccountApi, IDomainReferencerRelationshipApi {
	Logger logger = Logger.getLogger(AccountImpl.class);
	@Resource(name="protalResourceAccountDao")
	private IAccountDao accountDao;
	@Resource(name="ocProtalResourceAccountSeq")
	private ISequence seq;
	@Resource(name="resourceReAccountInstanceApi")
	private IReAccountInstanceApi reAccountInstance;
	@Resource
	private DiscoverPropService discoverPropService;
	@Resource
	private ResourceInstanceService resourceInstanceService;
	@Resource
	private IDiscoverResourceApi discoverResourceApi;
	/**
	 * 账户新增
	 */
	@Override
	public int insert(AccountBo accountBo) {
		AccountPo accountPo = toPo(accountBo);
		accountPo.setAccount_id(seq.next());
		accountPo.setStatus("1");
		accountPo.setEntry_datetime(new Date());
		int count = accountDao.insert(accountPo);
		if (count == 1)
			accountBo.setAccount_id(accountPo.getAccount_id());
		return count;
	}

	/**
	 * 账户删除
	 */
	@Override
	public int del(long id) {
		AccountPo accountPo = new AccountPo();
		accountPo.setAccount_id(id);
		accountPo.setStatus("0");
		accountPo.setDelete_datetime(new Date());
		int count = accountDao.update(accountPo);
		return count;
	}

	/**
	 * 账户修改
	 */
	@Override
	public int update(AccountBo accountBo) {
		AccountPo accountPo = toPo(accountBo);
		accountPo.setEntry_datetime(new Date());
		int count = accountDao.update(accountPo);
		// 同步更新资源的发现属性
		List<DiscoverProp> dpList = new ArrayList<DiscoverProp>();
		List<ReAccountInstanceBo> reAccountInstList = reAccountInstance.getByAccountId(accountBo.getAccount_id());
		for(int i = 0; i < reAccountInstList.size(); i ++){
			ReAccountInstanceBo raib = reAccountInstList.get(i);
			// 测试是否能发现
			if(i == 0){
				try {
					Map paramter = new HashMap();
					ResourceInstance ri = resourceInstanceService.getResourceInstance(raib.getInstanceid());
					List<DiscoverProp> discoverPropList = ri.getDiscoverProps();
					for(int j = 0; discoverPropList != null && j < discoverPropList.size(); j ++){
						DiscoverProp discoverProp = discoverPropList.get(j);
						paramter.put(discoverProp.getKey(), discoverProp.getValues()[0]);
					}
					if(!paramter.isEmpty()){
						paramter.put("username", accountPo.getUsername());
						paramter.put("password", accountPo.getPassword());
						int discoverResult = discoverResourceApi.testDiscover(paramter, ri.getId());
						if(discoverResult != 1){
							break;
						}
					}else{
						break;
					}
				} catch (Exception e) {
					logger.error("account update:", e);
					break;
				}
			}
			// 更新发现属性
			DiscoverProp usernameDp = new DiscoverProp();
			usernameDp.setInstanceId(raib.getInstanceid());
			usernameDp.setKey("username");
			String[] userValues = {accountPo.getUsername()};
			usernameDp.setValues(userValues);
			dpList.add(usernameDp);
			
			DiscoverProp passwordDp = new DiscoverProp();
			passwordDp.setInstanceId(raib.getInstanceid());
			passwordDp.setKey("password");
			String[] passwordValues = {accountPo.getPassword()};
			passwordDp.setValues(passwordValues);
			dpList.add(passwordDp);
		}
		if(!dpList.isEmpty()){
			try {
				discoverPropService.updateProps(dpList);
			} catch (Exception e) {
				logger.error("update discoverPropService.updateProps", e);
			}
		}
		return count;
	}

	/**
	 * 通过ID查询用户信息
	 */
	@Override
	public AccountBo get(long id) {
		AccountPo accountPo = accountDao.get(id);
		return toBoDontDecrypt(accountPo);
	}

	/**
	 * 查询所有用户信息 把所有的po对象转换成bo对象
	 */
	@Override
	public List<AccountBo> getList(Set<IDomain> domains) {
		List<AccountPo> accountPoList = accountDao.getList(domains);
		List<AccountBo> accountBoList = new ArrayList<AccountBo>();
		for (int i = 0; i < accountPoList.size(); i++) {
			accountBoList.add(toBo(accountPoList.get(i)));
		}
		return accountBoList;
	}

	private AccountBo toBo(AccountPo accountPo) {
		if (accountPo == null)
			return null;
		AccountBo accountBo = new AccountBo();
		BeanUtils.copyProperties(accountPo, accountBo);
		// 解密
		accountBo.setPassword(SecureUtil.pwdDecrypt(accountBo.getPassword()));
		return accountBo;
	}
	
	private AccountBo toBoDontDecrypt(AccountPo accountPo) {
		if (accountPo == null)
			return null;
		AccountBo accountBo = new AccountBo();
		BeanUtils.copyProperties(accountPo, accountBo);
		return accountBo;
	}

	private AccountPo toPo(AccountBo accountBo) {
		if (accountBo == null)
			return null;
		AccountPo accountPo = new AccountPo();
		BeanUtils.copyProperties(accountBo, accountPo);
		// 加密
		accountPo.setPassword(SecureUtil.pwdEncrypt(accountPo.getPassword()));
		return accountPo;
	}

	@Override
	public boolean checkDomainIsRel(long domainId) {
		return accountDao.countAccountByDomainId(domainId) > 0 ? true : false;
	}
}
