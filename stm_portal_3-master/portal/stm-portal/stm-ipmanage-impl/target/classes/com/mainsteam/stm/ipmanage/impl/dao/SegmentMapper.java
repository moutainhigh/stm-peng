package com.mainsteam.stm.ipmanage.impl.dao;

import java.util.List;

import org.apache.ibatis.annotations.Param;

import com.mainsteam.stm.ipmanage.bo.Segment;



public interface SegmentMapper {
	
    public List<Segment> getSegmentsByNodeID(@Param("parentNodeId")Integer parentNodeId);
    
    public List<Segment> getSegmentsByID(@Param("parentId")Integer parentId);
    
    public int insertOne(@Param("segment")Segment segment);
    
    public int delete(@Param("id")Integer id);
    
    public int updateSegment(@Param("segment")Segment segment);

	public int selectSameSegment(@Param("segment")Segment segment);

	public List<Segment> getSegmentsByGroupID(@Param("parentGroupId")Integer parentGroupId);


    
}