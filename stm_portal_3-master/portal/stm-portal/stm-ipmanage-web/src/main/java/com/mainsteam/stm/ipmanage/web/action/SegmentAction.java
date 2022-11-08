package com.mainsteam.stm.ipmanage.web.action;

import java.util.List;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.ipmanage.api.SegmentService;
import com.mainsteam.stm.ipmanage.bo.Segment;
import com.mainsteam.stm.ipmanage.bo.SubnetBo;
import com.mainsteam.stm.platform.web.action.BaseAction;

@Controller
@RequestMapping({ "/portal/ipmanage/segment" })
public class SegmentAction extends BaseAction{
	Logger logger=Logger.getLogger(SegmentAction.class);

	@Resource
	private SegmentService segmentService;
	@RequestMapping("/getSegmentsByNodeID")
	@ResponseBody
	public JSONObject getSegmentsByNodeID(Integer parentNodeId){
		
		return toSuccess(segmentService.getSegmentsByNodeID(parentNodeId));
	}
	@RequestMapping("/getSegmentsByGroupID")
	@ResponseBody
	public JSONObject getSegmentsByGroupID(Integer parentGroupId){
		
		return toSuccess(segmentService.getSegmentsByGroupID(parentGroupId));
	}
	@RequestMapping("/getSegmentsByID")
	@ResponseBody
	public JSONObject getSegmentsByID(Integer parentId){
		return toSuccess(segmentService.getSegmentsByID(parentId));
	}
	@RequestMapping("/insertOneSegment")
	@ResponseBody
	public JSONObject insertOneSegment(Segment segment){
		return toSuccess(segmentService.insertOne(segment));
	}
	
	@RequestMapping("/deleteSegment")
	@ResponseBody
	public JSONObject deleteSegment(Integer id){
		return toSuccess(segmentService.delete(id));
	}
	
	@RequestMapping("/updateSegment")
	@ResponseBody
	public JSONObject updateSegment(Segment segment){
		return toSuccess(segmentService.updateSegment(segment));
	}
	
	@RequestMapping("/subnetting")
	@ResponseBody
	public JSONObject subnetting(SubnetBo bo){
		return toSuccess(segmentService.Subnetting(bo));
	}
	@RequestMapping("/addSegments")
	@ResponseBody
	public JSONObject addSegments(String list){
		List<Segment> parseArray = JSONObject.parseArray(list,Segment.class);
		logger.error(list);
		for (Segment segment : parseArray) {
			logger.error(segment);
		}
		return toSuccess(segmentService.addSegments(parseArray));
	}
}
