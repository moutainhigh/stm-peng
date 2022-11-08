package com.mainsteam.stm.topo.bo;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.lang.StringUtils;


/*
 * 历史变更表 对应stm_topo_mac_history表
 */
public class MacHistoryBo extends MacRuntimeBo{
	private static final DateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	
	/*扩展属性-用于业务处理*/
	private Date startTime;	//查询开始时间
	private Date endTime;	//查询结束时间

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		try {
			if(StringUtils.isNotBlank(startTime)){
				this.startTime = format.parse(startTime);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		try {
			if(StringUtils.isNotBlank(endTime)){
				this.endTime = format.parse(endTime);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

}
