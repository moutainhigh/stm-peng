package com.mainsteam.stm.profilelib.obj;

import java.io.Serializable;
import java.util.Date;

import com.mainsteam.stm.profilelib.objenum.TimelineTypeEnum;

public class TimelineInfo implements Serializable{

	private static final long serialVersionUID = 5544196822142282733L;

	private long profileId;
	
	private long timeLineId;

	private String name;
	
	private Date startTime;

    @Override
	public boolean equals(Object obj) {
    	boolean result = true;
    	TimelineInfo info = (TimelineInfo)obj;
    	if(obj == null){
    		return false;
    	}
    	if(!info.getName().equals(name)){
    		result = false;
    	}else if(info.getStartTime() != startTime){
    		result = false;
    	}else if(info.getEndTime() != endTime){
    		result = false;
    	}
    	return result;
	}

	private Date endTime;
    
    private TimelineTypeEnum timeLineType;
    
    
    public long getTimeLineId() {
  		return timeLineId;
  	}

  	public void setTimeLineId(long timeLineId) {
  		this.timeLineId = timeLineId;
  	}

	public Date getStartTime() {
		return startTime;
	}
	
	public void setStartTime(Date startTime) {
		this.startTime = startTime;
	}
	
	public Date getEndTime() {
		return endTime;
	}
	
	public void setEndTime(Date endTime) {
		this.endTime = endTime;
	}

	public TimelineTypeEnum getTimeLineType() {
		return timeLineType;
	}

	public void setTimeLineType(TimelineTypeEnum timeLineType) {
		this.timeLineType = timeLineType;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	public long getProfileId() {
		return profileId;
	}

	public void setProfileId(long profileId) {
		this.profileId = profileId;
	}
	
}
