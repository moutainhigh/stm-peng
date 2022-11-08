package com.mainsteam.stm.home.screen.service.impl;

import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import com.mainsteam.stm.home.screen.api.IScreenApi;
import com.mainsteam.stm.home.screen.bo.Biz;
import com.mainsteam.stm.home.screen.dao.IScreenDao;
import com.mainsteam.stm.home.workbench.main.api.IUserWorkBenchApi;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.platform.sequence.service.SEQ;
import com.mainsteam.stm.platform.sequence.service.SequenceFactory;
import com.mainsteam.stm.portal.business.api.IBizServiceApi;

/**
 * <li>文件名称: ScreenServiceImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月20日
 * @author   ziwenwen
 */
@Service
public class ScreenServiceImpl implements IScreenApi {
	Logger log=Logger.getLogger(IScreenApi.class);

	@Autowired
	private IScreenDao screenDao;
	
	@Autowired
	private IBizServiceApi bizServiceApi;
	
//	@Autowired
//	private IBizSnapshootApi bizSnapshootApi;
	
	@Autowired
	private IUserWorkBenchApi userWorkbenchApi;
	
	private ISequence seq;
	
	@Autowired
	public ScreenServiceImpl(@Qualifier(SequenceFactory.SPRING_BEAN_NAME)SequenceFactory sequenceFactory){
		seq=sequenceFactory.getSeq(SEQ.SEQNAME_STM_HOME_USER_SCREEN_REL);
	}

	@Override
	public List<Biz> getBizs(Long userId) {
		return screenDao.getUserBizRels(userId);
	}

	@Override
	public int saveBizs(Long userId, List<Biz> uBizRels) {
		screenDao.del(userId);
		for(Biz ubr:uBizRels){
			ubr.setId(seq.next());
		}
		return screenDao.batchInsert(uBizRels);
	}

	@Override
	public int updateBiz(Biz ubr) {
		return screenDao.update(ubr);
	}

	@Override
	public Biz getBizByID(Long id) {
		// TODO Auto-generated method stub
		return screenDao.getBiz(id);
	}
}


