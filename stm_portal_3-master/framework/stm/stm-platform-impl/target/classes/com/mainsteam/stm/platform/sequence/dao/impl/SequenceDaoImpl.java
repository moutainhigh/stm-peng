package com.mainsteam.stm.platform.sequence.dao.impl;

import java.util.List;

import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;
import com.mainsteam.stm.platform.sequence.dao.ISequenceDao;
import com.mainsteam.stm.platform.sequence.po.SequencePo;

/**
 * <li>文件名称: SequenceDaoImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年6月26日
 * @author   ziwenwen
 */
public class SequenceDaoImpl extends BaseDao<SequencePo> implements ISequenceDao{
	
	private final String SQL_GET=getNamespace()+SQL_COMMOND_GET;
	
	private final String SQL_GET_ALL=getNamespace()+"getAll";
	
	private final String SQL_INSERT=getNamespace()+"insert";
	
	public SequenceDaoImpl(SqlSessionTemplate session) {
		super(session, ISequenceDao.class.getName());
	}

	@Override
	public SequencePo get(String seqName) {
		return getSession().selectOne(SQL_GET,seqName);
	}

	@Override
	public List<SequencePo> getAll() {
		return getSession().selectList(SQL_GET_ALL);
	}

	@Override
	public int insert(String seqName) {
		return getSession().insert(SQL_INSERT,seqName);
	}

}


