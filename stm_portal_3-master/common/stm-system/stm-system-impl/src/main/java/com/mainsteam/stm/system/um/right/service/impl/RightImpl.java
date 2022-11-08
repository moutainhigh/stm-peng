package com.mainsteam.stm.system.um.right.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.license.LicenseCheckException;//zw
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.platform.system.config.bean.SystemConfigBo;
import com.mainsteam.stm.platform.system.config.bean.SystemConfigConstantEnum;
import com.mainsteam.stm.platform.system.config.service.ISystemConfigApi;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.system.itsm.bo.ItsmBo;
import com.mainsteam.stm.system.itsmuser.dao.IItsmSystemDao;
import com.mainsteam.stm.system.license.api.ILicenseApi;
import com.mainsteam.stm.system.license.bo.PortalModule;
//import com.mainsteam.stm.system.ssoforthird.api.ISSOForThirdApi;
//import com.mainsteam.stm.system.ssoforthird.bo.SSOForThirdBo;
//import com.mainsteam.stm.system.ssoforthird.dao.ISSOForThirdDao;
import com.mainsteam.stm.system.um.right.api.IRightApi;
import com.mainsteam.stm.system.um.right.bo.Right;
import com.mainsteam.stm.system.um.right.bo.SSOForRight;
import com.mainsteam.stm.system.um.right.constants.RightConstants;
import com.mainsteam.stm.system.um.right.dao.IRightDao;

/**
 * <li>文件名称: RightImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月6日
 * @author   ziwenwen
 */
public class RightImpl implements IRightApi {
	private IRightDao rightDao;
	private ISequence seq;
	@Autowired
	private ILicenseApi licenseApi;
	
	@Autowired
	private IItsmSystemDao itsmSystemDao;
	
//	@Autowired
//	@Resource(name = "stm_sysmanage_Api")
//	private ISSOForThirdApi stm_sysmanage_Api;
//	private ISSOForThirdDao SSOForThirdDao;
	
	@Autowired
	private ISystemConfigApi configApi;
	
	/**
	 * 得到ITSM的ID
	 */
	private static final long configId = SystemConfigConstantEnum.SYSTEM_CONFIG_ITSM
			.getCfgId();
	
	@Override
	public List<Right> getAll() throws LicenseCheckException {
		return filterRights(rightDao.getAll());
	}

	@Override
	public List<Right> getRightByType(int type) {
		return rightDao.getRightByType(type);
	}

	@Override
	public Right get(Long id) {
		return rightDao.get(id);
	}

	@Override
	public int update(Right rightBo) {
		int result = rightDao.update(rightBo);
		if(result>0){
			SSOForRight ssoForRight = new SSOForRight();
			ssoForRight.setId(rightBo.getId());
			ssoForRight.setName(rightBo.getName());
			ssoForRight.setWsdlURL(rightBo.getUrl());
			ssoForRight.setDescrible(rightBo.getDescription());
			if(ssoForRight.getId()>100000){
				rightDao.updateSSOForThird(ssoForRight);
			}
		}
		return result;
	}

	@Override
	public int del(Long id) {
		return rightDao.del(id);
	}

	@Override
	public int insert(Right rightBo) {
		rightBo.setId(seq.next());
		if(rightBo.getFileId()==null){
			rightBo.setFileId(RightConstants.DEFAULT_IMAGE_FILE_ID);
		}
		//将已有模块的sort置后
		if(rightDao.updateSortForInsert() != getCount()){
			return -1;
		}
		return rightDao.insert(rightBo);
	}

	public void setRightDao(IRightDao rightDao) {
		this.rightDao = rightDao;
	}

	public void setSeq(ISequence seq) {
		this.seq = seq;
	}

	@Override
	public int updateSort(List<Right> rs) {
		return rightDao.updateSort(rs);
	}

	@Override
	public List<Right> getRights(Long roleId) throws LicenseCheckException {
		return filterRights(rightDao.getRights(roleId));
	}

	@Override
	public int save(Right right) {
		if(right!=null&&right.getId()==ILoginUser.RIGHT_ITSM){
			this.del(ILoginUser.RIGHT_ITSM);
			rightDao.insert(right);
			return 1;
		}if(right!=null){
			if(right.getId()!=null){
				this.del(right.getId());
			}
			rightDao.insert(right);
			return 1;
		}
		else{
			return -1;
		}
	}
	
	private List<Right> filterRights(List<Right> rights) throws LicenseCheckException{
		List<PortalModule> modules = licenseApi.getPortalModules();
		List<Right> results = new ArrayList<Right>();
		List<Long> exceptIds = new ArrayList<Long>();
		for(PortalModule module : modules){
			if(!module.isAuthor()){
				exceptIds.add(module.getId());
			}
		}
		
		for(Right right : rights){
			if(exceptIds.indexOf(right.getId())==-1){
				results.add(right);
			}
		}
		return results;
	}

	/* (non-Javadoc)
	 * @see com.mainsteam.stm.system.um.right.api.IRightApi#updateStatus(com.mainsteam.stm.system.um.right.bo.Right)
	 */
	@Override
	public int updateStatus(Right right) {
		return rightDao.updateStatus(right);
	}

	@Override
	public List<Right> getAll4Skin() {
		return rightDao.getAll4Skin();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return rightDao.getCount();
	}

	@Override
	public int updateDelStatus(Long id) {
		Long[] ids = new Long[1];
		ids[0] = id;
		if(id==14){
			itsmSystemDao.updateSystemStartState(ids, 0);
			SystemConfigBo configBo = configApi.getSystemConfigById(configId);
			ItsmBo itsmBo = JSONObject.parseObject(configBo.getContent(),
					ItsmBo.class);
			itsmBo.setOpen(false);
			SystemConfigBo bo = new SystemConfigBo();
			bo.setId(configId);
			bo.setContent(JSONObject.toJSONString(itsmBo));
			configApi.updateSystemConfig(bo);
		}else{
			rightDao.updateSSOForThirdStartState(ids, 0);
		}
		return rightDao.updateDelStatus(id);
	}

//	/* (non-Javadoc)
//	 * @see com.mainsteam.stm.system.um.right.api.IRightApi#getRoleRights()
//	 */
//	@Override
//	public List<Right> getRoleRights() {
//		return rightDao.getAllRightsByRoleUsedType(RightConstants.ROLE_USED_STATUS);
//	}
}


