package com.mainsteam.stm.home.layout.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.util.CollectionUtils;

import com.mainsteam.stm.home.layout.api.HomeLayoutDomainApi;
import com.mainsteam.stm.home.layout.bo.HomeLayoutDomainBo;
import com.mainsteam.stm.home.layout.dao.IHomeLayoutDomainDao;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.system.um.domain.bo.Domain;

/**
 * <li>文件名称: HomeLayoutDomainImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   下午2:56:32
 * @author   dengfuwei
 */
public class HomeLayoutDomainImpl implements HomeLayoutDomainApi {
	
	@Resource(name="stm_home_homeLayoutDomainDao")
	private IHomeLayoutDomainDao homeLayoutDomainDao;
	 @Resource(name="ocProtalHomeLayoutDomainSeq")
	    private ISequence HomeLayoutDomainSeq;
	

	@Override
	public List<HomeLayoutDomainBo> getLayoutDomain(HomeLayoutDomainBo homeLayoutDomainBo) {
		return homeLayoutDomainDao.get(homeLayoutDomainBo);
	}

	@Override
	public void saveLayoutDomain(List<HomeLayoutDomainBo> list, long layoutId, long userId) {
		homeLayoutDomainDao.delete(userId, layoutId);
		if(!CollectionUtils.isEmpty(list)){
			for(HomeLayoutDomainBo homeLayoutDomainBo : list){
				homeLayoutDomainDao.insert(homeLayoutDomainBo);
			}
		}
	}

	@Override
	public List<Domain> getDomainByLayoutId(long layoutId, String content) {
		// TODO Auto-generated method stub
		return homeLayoutDomainDao.getDomainByLayoutId(layoutId, content);
	}

	@Override
	public List<Domain> getUnDomainByLayoutId(long layoutId, String content) {
		// TODO Auto-generated method stub
		return homeLayoutDomainDao.getUnDomainByLayoutId(layoutId, content);
	}

	@Override
	public void copyLayoutDomain(List<HomeLayoutDomainBo> list, long layoutId,
			long userId) {
	//	homeLayoutDomainDao.delete(userId, layoutId);
		if(!CollectionUtils.isEmpty(list)){
			for(HomeLayoutDomainBo homeLayoutDomainBo : list){
				homeLayoutDomainBo.setId(HomeLayoutDomainSeq.next());
				homeLayoutDomainBo.setLayoutId(layoutId);
				homeLayoutDomainDao.insert(homeLayoutDomainBo);
			}
		}
	}


}
