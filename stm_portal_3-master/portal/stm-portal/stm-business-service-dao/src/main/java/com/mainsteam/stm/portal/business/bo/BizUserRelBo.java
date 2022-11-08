package com.mainsteam.stm.portal.business.bo;

import java.io.Serializable;

public class BizUserRelBo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -5159348051904123834L;

	//业务ID
	private long biz_id;
	
	//用户ID
	private long user_id;
	
	//用户名
	private String name;
	
	//用户账号
	private String account;
	
	//查看权限
	private int view;
	
	//编辑权限
	private int edit;

	public long getBiz_id() {
		return biz_id;
	}

	public void setBiz_id(long biz_id) {
		this.biz_id = biz_id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getAccount() {
		return account;
	}

	public void setAccount(String account) {
		this.account = account;
	}

	public int getView() {
		return view;
	}

	public void setView(int view) {
		this.view = view;
	}

	public long getUser_id() {
		return user_id;
	}

	public void setUser_id(long user_id) {
		this.user_id = user_id;
	}

	public int getEdit() {
		return edit;
	}

	public void setEdit(int edit) {
		this.edit = edit;
	}
}
