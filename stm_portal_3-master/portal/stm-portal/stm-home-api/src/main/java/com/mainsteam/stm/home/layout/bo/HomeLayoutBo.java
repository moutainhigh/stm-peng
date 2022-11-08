package com.mainsteam.stm.home.layout.bo;

import java.sql.Timestamp;

/**
 * 页面布局
 * <li>文件名称: HomeLayoutBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2017年1月13日 上午10:40:28
 * @author   dfw
 */
public class HomeLayoutBo {
	
    /** ID */
    private long id;

    /** 页面名称 */
    private String name;

    /** 自动刷新间隔时间，单位 秒 */
    private int refreshTime;

    /** 用户ID */
    private long userId;
    /***复制者id***/
    private long copyUserId;

    /** 类型（1:用户页面、2:模板等） */
    private byte layoutType;

    /** 创建时间 */
    private Timestamp createTime;
    
    private String createTimeStr;

    /** 布局 */
    private String layout;
    /**选择的模板id**/
    private long chooeseId;
    /**是否设为首页**/
    private int isDefault=0;
    
    private String tempSet;
    /**默认模板id**/
    private String tempId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getRefreshTime() {
		return refreshTime;
	}

	public void setRefreshTime(int refreshTime) {
		this.refreshTime = refreshTime;
	}

	public long getUserId() {
		return userId;
	}

	public void setUserId(long userId) {
		this.userId = userId;
	}

	public byte getLayoutType() {
		return layoutType;
	}

	public void setLayoutType(byte layoutType) {
		this.layoutType = layoutType;
	}

	public Timestamp getCreateTime() {
		return createTime;
	}

	public void setCreateTime(Timestamp createTime) {
		this.createTime = createTime;
	}

	public String getLayout() {
		return layout;
	}

	public void setLayout(String layout) {
		this.layout = layout;
	}

	public String getCreateTimeStr() {
		return createTimeStr;
	}

	public void setCreateTimeStr(String createTimeStr) {
		this.createTimeStr = createTimeStr;
	}

	public long getChooeseId() {
		return chooeseId;
	}

	public void setChooeseId(long chooeseId) {
		this.chooeseId = chooeseId;
	}

	public int getIsDefault() {
		return isDefault;
	}

	public void setIsDefault(int isDefault) {
		this.isDefault = isDefault;
	}

	

	public String getTempId() {
		return tempId;
	}

	public void setTempId(String tempId) {
		this.tempId = tempId;
	}

	public String getTempSet() {
		return tempSet;
	}

	public void setTempSet(String tempSet) {
		this.tempSet = tempSet;
	}

	public long getCopyUserId() {
		return copyUserId;
	}

	public void setCopyUserId(long copyUserId) {
		this.copyUserId = copyUserId;
	}

}
