package com.mainsteam.stm.system.ssoforthird.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.platform.sequence.service.SequenceFactory;
import com.mainsteam.stm.system.ssoforthird.api.ISSOForThirdApi;
import com.mainsteam.stm.system.ssoforthird.bo.SSOForThirdBo;
import com.mainsteam.stm.system.ssoforthird.dao.ISSOForThirdDao;
import com.mainsteam.stm.system.um.right.api.IRightApi;
import com.mainsteam.stm.system.um.right.bo.Right;
import com.mainsteam.stm.system.um.right.constants.RightConstants;
@Service
public class ISSOForThirdImpl implements ISSOForThirdApi {
	
	@Autowired
	private ISSOForThirdDao ssoForThirdDao;
	
	private ISequence sequence;
	
//	@Resource(name = "stm_system_right_impl")
	@Autowired
	IRightApi stm_system_right_impl;
	
	public void setSsoForThirdDao(ISSOForThirdDao ssoForThirdDao) {
		this.ssoForThirdDao = ssoForThirdDao;
	}
	
	@Autowired
	public ISSOForThirdImpl(SequenceFactory sequenceFactory) {
		this.sequence = sequenceFactory.getSeq("STM_SYSTEM_SSOFORTHIRD");
	}

	@Override
	public int saveSSOForThird(SSOForThirdBo ssoForThirdBo) {
		
		ssoForThirdBo.setId(sequence.next());
		int result = ssoForThirdDao.saveSSOForThird(ssoForThirdBo);
		if(result>0){
			ssoForThirdBo.setIsOpen(0);
		}
		this.right(ssoForThirdBo);
		return result;
	}

	@Override
	public List<SSOForThirdBo> querySSOForThird(
			Page<SSOForThirdBo, SSOForThirdBo> page) {
		return ssoForThirdDao.querySSOForThird(page);
	}

	@Override
	public List<SSOForThirdBo> queryAllSSOForThird() {
		// TODO Auto-generated method stub
		return ssoForThirdDao.queryAllSSOForThird();
	}

	@Override
	public int batchDel(Long[] ids) {
		int result = this.ssoForThirdDao.batchDel(ids);
		if(result>0){
			for(int i=0;i<ids.length;i++){
				stm_system_right_impl.del(ids[i]);
			}
		}
		return result;
	}

	@Override
	public SSOForThirdBo getSSOForThirdById(Long id) {
		// TODO Auto-generated method stub
		return this.ssoForThirdDao.getSSOForThirdById(id);
	}

	@Override
	public int updateSSOForThird(SSOForThirdBo ssoForThirdBo) {
		// TODO Auto-generated method stub
		this.right(ssoForThirdBo);
		return this.ssoForThirdDao.updateSSOForThird(ssoForThirdBo);
	}

	@Override
	public int updateSSOForThirdStartState(Long[] ids, int startState) {
		// TODO Auto-generated method stub
		for(int i=0;i<ids.length;i++){
			SSOForThirdBo ssoForThirdBo = new SSOForThirdBo();
			ssoForThirdBo = this.getSSOForThirdById(ids[i]);
			if(ssoForThirdBo!=null&&ssoForThirdBo.getIsOpen()!=startState){
				this.right(ssoForThirdBo);
			}
		}
		return this.ssoForThirdDao.updateSSOForThirdStartState(ids, startState);
	}

	@Override
	public boolean isWsdlURLExist(SSOForThirdBo ssoForThirdBo) {
		// TODO Auto-generated method stub
		return this.ssoForThirdDao.isWsdlURLExist(ssoForThirdBo);
	}
	
	public void right(SSOForThirdBo ssoForThirdBo){
		try {
			Right rightBo = new Right();
			rightBo.setId(ssoForThirdBo.getId());
			rightBo.setName(ssoForThirdBo.getName());
			rightBo.setDescription(ssoForThirdBo.getDescrible());
			rightBo.setFileId(RightConstants.DEFAULT_IMAGE_FILE_ID);
			rightBo.setUrl(ssoForThirdBo.getWsdlURL());
			rightBo.setSort(stm_system_right_impl.getAll().size() + 100);
			rightBo.setPid(0L);
			if (ssoForThirdBo.getIsOpen()==0||"".equals(ssoForThirdBo.getIsOpen())) {
				rightBo.setStatus(1);
			} else {
				rightBo.setStatus(0);
			}
			stm_system_right_impl.save(rightBo);
				
			stm_system_right_impl.updateStatus(rightBo);

		} catch (Exception e) {
//			LOGGER.error("转换ITSM页签URL失败", e);
		}
	}

	@Override
	public int updateState4Right(Long[] ids, int startState) {
		// TODO Auto-generated method stub
		return this.ssoForThirdDao.updateSSOForThirdStartState(ids, startState);
	}

}
