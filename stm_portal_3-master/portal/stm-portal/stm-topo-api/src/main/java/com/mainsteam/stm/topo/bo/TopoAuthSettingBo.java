package com.mainsteam.stm.topo.bo;


/**
 * <li>拓扑权限设置Bo</li>
 * <li>文件名称: TopoAuthSettingBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * @version  ms.stm
 * @since  2019年11月21日
 * @author zwx
 */
public class TopoAuthSettingBo {
	//编辑列
	public static final String EDIT="edit_auth";
	//查看列
	public static final String SELECT="select_auth";
	
	private Long id;
	
	//子拓扑id
	private Long subtopoId;
	
	//用户id
	private Long userId;
	
	//编辑权限
	private boolean editAuth;
	
	//查看权限
	private boolean selectAuth;
	
	/**------扩展属性，用于查询、显示--------*/
	//系统用户名称
	private String userName;
	
	//用户权限设置
	private String userAuthInfo;
	
	//编辑权限
	private int editAuthInt;
	
	//查看权限
	private int selectAuthInt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getSubtopoId() {
		return subtopoId;
	}

	public void setSubtopoId(Long subtopoId) {
		this.subtopoId = subtopoId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public boolean isEditAuth() {
		return editAuth;
	}

	public void setEditAuth(boolean editAuth) {
		this.editAuth = editAuth;
	}

	public boolean isSelectAuth() {
		return selectAuth;
	}

	public void setSelectAuth(boolean selectAuth) {
		this.selectAuth = selectAuth;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getUserAuthInfo() {
		return userAuthInfo;
	}

	public void setUserAuthInfo(String userAuthInfo) {
		this.userAuthInfo = userAuthInfo;
	}

	public int getEditAuthInt() {
		return editAuthInt;
	}

	public void setEditAuthInt(int editAuthInt) {
		this.editAuthInt = editAuthInt;
		this.setEditAuth(editAuthInt==0?false:true);
	}

	public int getSelectAuthInt() {
		return selectAuthInt;
	}

	public void setSelectAuthInt(int selectAuthInt) {
		this.selectAuthInt = selectAuthInt;
		this.setSelectAuth(selectAuthInt==0?false:true);
	}

}
