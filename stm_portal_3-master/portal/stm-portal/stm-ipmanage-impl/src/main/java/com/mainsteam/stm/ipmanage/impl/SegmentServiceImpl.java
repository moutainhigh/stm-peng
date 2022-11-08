package com.mainsteam.stm.ipmanage.impl;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import javax.annotation.Resource;

import com.mainsteam.stm.ipmanage.util.SubnetUtil;
import org.apache.log4j.Logger;

import com.mainsteam.stm.ipmanage.api.SegmentService;
import com.mainsteam.stm.ipmanage.bo.Segment;
import com.mainsteam.stm.ipmanage.bo.SubnetBo;
import com.mainsteam.stm.ipmanage.impl.dao.SegmentMapper;

public class SegmentServiceImpl implements SegmentService {

	Logger logger=Logger.getLogger(SegmentServiceImpl.class);
	private SimpleDateFormat sdf=new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	@Resource
	private SegmentMapper segmentMapper;
	
	public List<Segment> getSegmentsByNodeID(Integer parentNodeId) {
		// TODO Auto-generated method stub
		try {
			return segmentMapper.getSegmentsByNodeID(parentNodeId);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e);
			logger.error(e.getStackTrace());
			return null;
		}
		
	}
	public List<Segment> getSegmentsByGroupID(Integer parentGroupId) {
		// TODO Auto-generated method stub
		try {
			return segmentMapper.getSegmentsByGroupID(parentGroupId);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e);
			logger.error(e.getStackTrace());
			return null;
		}
		
	}
	
	public List<Segment> getSegmentsByID(Integer parentId) {
		// TODO Auto-generated method stub
		try {
			return segmentMapper.getSegmentsByID(parentId);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e);
			logger.error(e.getStackTrace());
			return null;
		}
		
	}

	
	public int insertOne(Segment segment) {
		// TODO Auto-generated method stub
		try {
			return segmentMapper.insertOne(segment);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e);
			logger.error(e.getStackTrace());
			return 0;
		}
		
	}

	
	public int delete(Integer id) {
		// TODO Auto-generated method stub
		
		try {
			return segmentMapper.delete(id);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e);
			logger.error(e.getStackTrace());
			return 0;
		}
	}

	
	public int updateSegment(Segment segment) {
		// TODO Auto-generated method stub
		try {
			return segmentMapper.updateSegment(segment);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e);
			logger.error(e.getStackTrace());
			return 0;
		}
		
	}

	
	public SubnetBo Subnetting(SubnetBo subnetBo) {
		// TODO Auto-generated method stub
		try {
			return SubnetUtil.listsubnets(subnetBo);
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e);
			logger.error(e.getMessage());
			return null;
		}
		
	}

	
	public int addSegments(List<Segment> list) {
		// TODO Auto-generated method stub
		try {
			for(Segment segment:list){
				logger.error(segment.toString());
//				int i=segmentMapper.selectSameSegment(segment);
//				if(i<1){
					segment.setCreate_time(sdf.format(new Date()));
					segment.setHas_childseg(0);
					segmentMapper.insertOne(segment);
//				}
			}
			return 1;
		} catch (Exception e) {
			// TODO: handle exception
			logger.error(e);
			logger.error(e.getStackTrace());
			e.printStackTrace();
			return 0;
		}
		
	}

}
