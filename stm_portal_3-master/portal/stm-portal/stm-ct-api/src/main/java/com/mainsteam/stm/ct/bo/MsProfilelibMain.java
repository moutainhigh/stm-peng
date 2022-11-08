package com.mainsteam.stm.ct.bo;

import java.util.Date;

public class MsProfilelibMain {

    private static final long serialVersionUID = 1L;

    private String id;
    private String profile_name;
    private String profilelib_code;
    private String test_way;
    private Integer status;
    private String test_value;
    private Integer compar_type;
    private Integer is_default;
    private String remark;
    private Date create_time;
    private Date update_time;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getProfile_name() {
		return profile_name;
	}
	public void setProfile_name(String profile_name) {
		this.profile_name = profile_name;
	}
	public String getProfilelib_code() {
		return profilelib_code;
	}
	public void setProfilelib_code(String profilelib_code) {
		this.profilelib_code = profilelib_code;
	}
	public String getTest_way() {
		return test_way;
	}
	public void setTest_way(String test_way) {
		this.test_way = test_way;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	public String getTest_value() {
		return test_value;
	}
	public void setTest_value(String test_value) {
		this.test_value = test_value;
	}
	public Integer getCompar_type() {
		return compar_type;
	}
	public void setCompar_type(Integer compar_type) {
		this.compar_type = compar_type;
	}
	public Integer getIs_default() {
		return is_default;
	}
	public void setIs_default(Integer is_default) {
		this.is_default = is_default;
	}
	public String getRemark() {
		return remark;
	}
	public void setRemark(String remark) {
		this.remark = remark;
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
