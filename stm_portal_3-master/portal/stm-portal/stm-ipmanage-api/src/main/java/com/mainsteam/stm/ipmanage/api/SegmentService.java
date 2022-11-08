package com.mainsteam.stm.ipmanage.api;

import java.util.List;

import com.mainsteam.stm.ipmanage.bo.Segment;
import com.mainsteam.stm.ipmanage.bo.SubnetBo;

public interface SegmentService {
	
	public List<Segment> getSegmentsByNodeID(Integer parentNodeId);
    
    public List<Segment> getSegmentsByID(Integer parentId);
    
    public int insertOne(Segment segment);
    
    public int delete(Integer id);
    
    public int updateSegment(Segment segment);
    
    public SubnetBo Subnetting(SubnetBo subnetBo);

	public int addSegments(List<Segment> list);

	public List<Segment> getSegmentsByGroupID(Integer parentGroupId);
    
}
