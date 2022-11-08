package com.mainsteam.stm.ipmanage.impl.dao.impl;

import java.util.List;

import com.mainsteam.stm.ipmanage.impl.dao.SegmentMapper;
import com.mainsteam.stm.ipmanage.bo.Segment;
import org.mybatis.spring.SqlSessionTemplate;

import com.mainsteam.stm.platform.dao.BaseDao;

public class SegmentMapperImpl extends BaseDao<Segment> implements SegmentMapper {

	public SegmentMapperImpl(SqlSessionTemplate session) {
		super(session, SegmentMapper.class.getName());
		// TODO Auto-generated constructor stub
	}

	
	public List<Segment> getSegmentsByNodeID(Integer parentNodeId) {
		// TODO Auto-generated method stub
		return getSession().selectList(getNamespace()+"getSegmentsByNodeID", parentNodeId);
	}

	
	public List<Segment> getSegmentsByID(Integer parentId) {
		// TODO Auto-generated method stub
		return getSession().selectList(getNamespace()+"getSegmentsByID", parentId);
	}

	
	public int insertOne(Segment segment) {
		// TODO Auto-generated method stub
		return super.insert(segment);
	}

	
	public int delete(Integer id) {
		// TODO Auto-generated method stub
		return super.del(id);
	}

	
	public int updateSegment(Segment segment) {
		// TODO Auto-generated method stub
		return super.update(segment);
	}

	
	public int selectSameSegment(Segment segment) {
		// TODO Auto-generated method stub
		List<Object> list = getSession().selectList(getNamespace()+"selectSameSegment", segment);
		if(list!=null){
			if(list.size()>0){
				return 1;
			}
		}
		return 0;
	}


	@Override
	public List<Segment> getSegmentsByGroupID(Integer parentGroupId) {
		// TODO Auto-generated method stub
		return getSession().selectList(getNamespace()+"getSegmentsByGroupID", parentGroupId);
	}


	
}
