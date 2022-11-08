package com.mainsteam.stm.ct.bo;

import java.util.Date;
import com.mainsteam.stm.ct.bo.MsWebsiteMetric;
import com.mainsteam.stm.ct.bo.MsPingMetric;

public class MsResourceMain {

    private static final long serialVersionUID = 1L;

    private String id;
    private String test_name;
    private String test_way;
    private String resource_type;
    private String test_ip;
    private Integer repeat_time;
    private String profilelib_id;
    private Integer probe_id;
    private Date create_time;
    private Date update_time;
    private Integer status;
    private Integer success_count;
    private Integer fail_count;
    private MsWebsiteMetric msWebsiteMetric;
    private MsPingMetric msPingMetric;
    
    
	
	public Integer getSuccess_count() {
		return success_count;
	}
	public void setSuccess_count(Integer success_count) {
		this.success_count = success_count;
	}
	public Integer getFail_count() {
		return fail_count;
	}
	public void setFail_count(Integer fail_count) {
		this.fail_count = fail_count;
	}
	public MsWebsiteMetric getMsWebsiteMetric() {
		return msWebsiteMetric;
	}
	public void setMsWebsiteMetric(MsWebsiteMetric msWebsiteMetric) {
		this.msWebsiteMetric = msWebsiteMetric;
	}
	public MsPingMetric getMsPingMetric() {
		return msPingMetric;
	}
	public void setMsPingMetric(MsPingMetric msPingMetric) {
		this.msPingMetric = msPingMetric;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTest_name() {
		return test_name;
	}
	public void setTest_name(String test_name) {
		this.test_name = test_name;
	}
	public String getTest_way() {
		return test_way;
	}
	public void setTest_way(String test_way) {
		this.test_way = test_way;
	}
	public String getResource_type() {
		return resource_type;
	}
	public void setResource_type(String resource_type) {
		this.resource_type = resource_type;
	}
	public String getTest_ip() {
		return test_ip;
	}
	public void setTest_ip(String test_ip) {
		this.test_ip = test_ip;
	}
	public Integer getRepeat_time() {
		return repeat_time;
	}
	public void setRepeat_time(Integer repeat_time) {
		this.repeat_time = repeat_time;
	}
	public String getProfilelib_id() {
		return profilelib_id;
	}
	public void setProfilelib_id(String profilelib_id) {
		this.profilelib_id = profilelib_id;
	}
	public Integer getProbe_id() {
		return probe_id;
	}
	public void setProbe_id(Integer probe_id) {
		this.probe_id = probe_id;
	}
	public Date getCreate_time() {
		return create_time;
	}
	public void setCreate_time(Date create_time) {
		this.create_time = create_time;
	}
	public Date getUpdate_time() {
		return update_time;
	}
	public void setUpdate_time(Date update_time) {
		this.update_time = update_time;
	}
	
    
    
}
