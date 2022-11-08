package com.mainsteam.stm.portal.resource.web.vo;

import java.io.Serializable;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.portal.resource.bo.MetricSettingBo;
import com.mainsteam.stm.portal.resource.bo.ProfileMetricBo;
import com.mainsteam.stm.portal.resource.bo.TimeLineBo;
import com.mainsteam.stm.profilelib.obj.ProfileMetric;
import com.mainsteam.stm.profilelib.objenum.TimelineTypeEnum;

/**
 * 
 * <li>文件名称: TimelineVo.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年8月25日
 * @author wangxinghao
 * @tags
 */
public class TimelineVo implements Serializable {

	/**
	 * serialVersion UID
	 */
	private static final long serialVersionUID = -3951053700326922052L;

	private static final DateFormat format = new SimpleDateFormat(
			"yyyy-MM-dd HH:mm:ss");

	private long profileId;
	private long timeLineId;
	private String name;
	private TimelineTypeEnum timeLineType;
	private Date startTime;
	private Date endTime;
	//datagridFormVal
	private String metricsString;
	
	private List<ProfileMetricBo> baseMetrics;
	
	private ProfileMetricPageVo profileMetricPageVo;

	public long getProfileId() {
		return profileId;
	}

	public void setProfileId(long profileId) {
		this.profileId = profileId;
	}

	public long getTimeLineId() {
		return timeLineId;
	}

	public void setTimeLineId(long timeLineId) {
		this.timeLineId = timeLineId;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public TimelineTypeEnum getTimeLineType() {
		return timeLineType;
	}

	public void setTimeLineType(TimelineTypeEnum timeLineType) {
		this.timeLineType = timeLineType;
	}

	public Date getStartTime() {
		return startTime;
	}

	public void setStartTime(String startTime) {
		try {
			if (startTime != null)
				this.startTime = format.parse(startTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public Date getEndTime() {
		return endTime;
	}

	public void setEndTime(String endTime) {
		try {
			if (endTime != null)
				this.endTime = format.parse(endTime);
		} catch (ParseException e) {
			e.printStackTrace();
		}
	}

	public String getMetricsString() {
		return metricsString;
	}

	public void setMetricsString(String metricsString) {
		this.metricsString = metricsString;
	}

	public List<ProfileMetricBo> getBaseMetrics() {
		return baseMetrics;
	}

	public void setBaseMetrics(String baseMetrics) {
		List<ProfileMetricBo> metrics=JSONArray.parseArray(baseMetrics, ProfileMetricBo.class);
		
		this.baseMetrics = metrics;
	}
	
	
	

	public ProfileMetricPageVo getProfileMetricPageVo() {
		return profileMetricPageVo;
	}

	public void setProfileMetricPageVo(ProfileMetricPageVo profileMetricPageVo) {
		this.profileMetricPageVo=profileMetricPageVo;
	}

}
