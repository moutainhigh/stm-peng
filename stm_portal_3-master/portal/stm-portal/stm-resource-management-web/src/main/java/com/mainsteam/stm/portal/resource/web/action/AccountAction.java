package com.mainsteam.stm.portal.resource.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.BeanUtils;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.api.IAccountApi;
import com.mainsteam.stm.portal.resource.api.IReAccountInstanceApi;
import com.mainsteam.stm.portal.resource.bo.AccountBo;
import com.mainsteam.stm.portal.resource.bo.ReAccountInstanceBo;
import com.mainsteam.stm.portal.resource.web.vo.AccountVo;
import com.mainsteam.stm.portal.resource.web.vo.ReAccountInstanceVo;

/**
 * <li>文件名称: AccountAction.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: 完成资源发现中账户的新增查询</li> <li>
 * 其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年7月22日
 * @author lk
 */
@Controller
@RequestMapping("/portal/resource/account")
public class AccountAction extends BaseAction {

	@Resource(name="resourceAccountApi")
	private IAccountApi accountApi;

	@Resource
	private IReAccountInstanceApi reAccountInstanceApi;

	/**
	 * 账户新增 新增时状态默认为有效，新增时间为当前时间
	 * 
	 * @param accountVo
	 * @return
	 */
	@RequestMapping("/insert")
	public JSONObject insert(@ModelAttribute AccountVo accountVo,
			HttpServletResponse resp) {
		AccountBo accountBo = toBo(accountVo);
		int count = accountApi.insert(accountBo);
		accountVo.setAccount_id(accountBo.getAccount_id());
		return toSuccess(count);
	}

	/**
	 * 账户删除
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/del")
	public JSONObject del(int id) {
		return toSuccess(accountApi.del(id));
	}

	/**
	 * 账户修改
	 * 
	 * @param accountVo
	 * @return
	 */
	@RequestMapping("/update")
	public JSONObject update(@ModelAttribute AccountVo accountVo) {
		AccountBo accountBo = toBo(accountVo);
		int count = accountApi.update(accountBo);
		return toSuccess(count);
	}

	/**
	 * 通过ID查询用户信息
	 * 
	 * @param id
	 * @return
	 */
	@RequestMapping("/get")
	public JSONObject get(int id) {
		AccountBo accountBo = accountApi.get(id);
		return toSuccess(toVo(accountBo));
	}

	/**
	 * 查询所有用户信息
	 * 
	 * @return
	 */
	@RequestMapping("/getList")
	public JSONObject getList(HttpSession session) {
		
		ILoginUser user = getLoginUser(session);
		Set<IDomain> domains = user.getDomains(ILoginUser.RIGHT_RESOURCE);
		
		List<AccountBo> accountBoList = accountApi.getList(domains);
		List<AccountVo> accountVoList = new ArrayList<AccountVo>();
		for (int i = 0; i < accountBoList.size(); i++) {
			accountVoList.add(toVo(accountBoList.get(i)));
		}
		Map result = new HashMap();
		result.put("total", accountVoList.size());
		result.put("rows", accountVoList);
		return toSuccess(result);
	}

	/**
	 * 通过账户ID查询相关的资源实例信息
	 * @param account_id
	 * @return
	 */
	@RequestMapping("/getReAccountInstanceByAccountId")
	public JSONObject getReAccountInstanceByAccountId(Long account_id) {
		if(account_id != null){
			List<ReAccountInstanceBo> reAccountInstanceBoList = reAccountInstanceApi
					.getByAccountId(account_id);
			List<ReAccountInstanceVo> reAccountInstanceVoList = new ArrayList<ReAccountInstanceVo>();
			for (int i = 0; i < reAccountInstanceBoList.size(); i++) {
				ReAccountInstanceVo reAccountInstanceVo = new ReAccountInstanceVo();
				BeanUtils.copyProperties(reAccountInstanceBoList.get(i),
						reAccountInstanceVo);
				reAccountInstanceVoList.add(reAccountInstanceVo);
			}
			Map result = new HashMap();
			result.put("total", reAccountInstanceVoList.size());
			result.put("rows", reAccountInstanceVoList);
			return toSuccess(result);
		}else{
			return toFailForGroupNameExsit("传入的预置账户ID为空");
		}
	}

	private AccountBo toBo(AccountVo accountVo) {
		if (accountVo == null)
			return null;
		AccountBo accountBo = new AccountBo();
		BeanUtils.copyProperties(accountVo, accountBo);
		return accountBo;
	}

	private AccountVo toVo(AccountBo accountBo) {
		if (accountBo == null)
			return null;
		AccountVo accountVo = new AccountVo();
		BeanUtils.copyProperties(accountBo, accountVo);
		return accountVo;
	}
}
