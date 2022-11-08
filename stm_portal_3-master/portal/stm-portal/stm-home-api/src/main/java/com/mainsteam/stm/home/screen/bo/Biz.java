package com.mainsteam.stm.home.screen.bo;

import java.io.Serializable;

/**
 * <li>文件名称: Biz.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 大屏要展示的业务对象</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月20日
 * @author   ziwenwen
 */
public class Biz implements Serializable{
	private static final long serialVersionUID = 4871587810014513365L;
	
	public static final int BIZ_TYPE_TOP=2;
	
	public static final int BIZ_TYPE_BIZ=1;
	
	public static final int BIZ_TYPE_WORKBENCH=3;
	
	private Long id;

	/**
	 * 用户id
	 */
	private Long userId;

	/**
	 * 排序
	 */
	private int sort;
	
	/**
	 * 扩展字段
	 */
	private String selfExt;
	
	/**
	 * 业务id
	 */
	private Long bizId;
	
	/**
	 * 业务名称
	 */
	private String title;
	
	/**
	 * 业务类型 1-业务管理；2-拓扑管理 3-首页工作台
	 */
	private int bizType=1;
	
	/**
	 * 缩略图id或者class类名
	 */
	private String thumbnail;
	
	/**
	 * 拓扑图json数据
	 */
	private String content;
	
	private Long domainId;
	private String showlevel;



	public String getShowlevel() {
		return showlevel;
	}

	public void setShowlevel(String showlevel) {
		this.showlevel = showlevel;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public int getSort() {
		return sort;
	}

	public void setSort(int sort) {
		this.sort = sort;
	}

	public String getSelfExt() {
		return selfExt;
	}

	public void setSelfExt(String selfExt) {
		this.selfExt = selfExt;
	}

	public Long getBizId() {
		return bizId;
	}

	public void setBizId(Long bizId) {
		this.bizId = bizId;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getBizType() {
		return bizType;
	}

	public void setBizType(int bizType) {
		this.bizType = bizType;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

	public Long getDomainId() {
		return domainId;
	}

	public void setDomainId(Long domainId) {
		this.domainId = domainId;
	}
	
}


