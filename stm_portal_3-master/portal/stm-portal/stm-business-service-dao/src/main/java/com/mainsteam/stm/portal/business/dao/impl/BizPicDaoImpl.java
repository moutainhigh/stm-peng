package com.mainsteam.stm.portal.business.dao.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.portal.business.bo.BizPicBo;
import com.mainsteam.stm.portal.business.dao.IBizPicDao;

/**
 * 业务图片dao
 * @author zwx
 *
 */
public class BizPicDaoImpl extends BaseDao<BizPicBo> implements IBizPicDao {
	private ISequence seq;

	public BizPicDaoImpl(SqlSessionTemplate session,ISequence seq) {
		super(session, IBizPicDao.class.getName());
		this.seq = seq;
	}
	
	public List<BizPicBo> getImagesByType(int type){
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("imgType", type);
		return getSession().selectList(getNamespace()+"getByType",map);
	}
	
	@Override
	public List<BizPicBo> getAllImages() {
		return getSession().selectList(getNamespace()+"getList");
	}

	@Override
	public int deleteImgesByFiledIds(Long[] ids) {
		Map<String, Object> map = new HashMap<String,Object>();
		map.put("ids", ids);
		return this.del("delByFiledIds", map);
	}

	@Override
	public int save(BizPicBo pic) {
		pic.setId(seq.next());
		return getSession().insert(getNamespace()+"insert", pic);
	}

}
