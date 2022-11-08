package com.mainsteam.stm.portal.resource.web.vo;

import java.io.Serializable;
import java.util.List;

/**
 * <li>文件名称: ReceiveAlarmQueryVo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月21日
 * @author   tpl
 */
public class ReceiveAlarmQueryVo implements Serializable{

	private static final long serialVersionUID = 6079523659879098649L;
	
	private long userID;
	private long profileID;
	private String userName;
	private String profileNameArray;
	private List<String> profileNameList;
	
	public long getProfileID() {
		return profileID;
	}
	public void setProfileID(long profileID) {
		this.profileID = profileID;
	}
	public Long getUserID() {
		return userID;
	}
	public void setUserID(Long userID) {
		this.userID = userID;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getProfileNameArray() {
		return profileNameArray;
	}
	public void setProfileNameArray(String profileNameArray) {
		this.profileNameArray = profileNameArray;
	}
	public List<String> getProfileNameList() {
		return profileNameList;
	}
	public void setProfileNameList(List<String> profileNameList) {
		this.profileNameList = profileNameList;
	}
	public void setUserID(long userID) {
		this.userID = userID;
	}
	

}
