package com.mainsteam.stm.portal.business.bo;

import java.io.Serializable;
/**
 * <li>文件名称: BizSelfBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年9月20日
 * @author   liupeng
 */
public class BizSelfBo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -537449409988709644L;
	/**主键ID*/
	private long id;
	/**图片名称*/
	private String imgName;
	/**文件服务器图片ID*/
	private long fileId;
	/**图片类型   0：流程 1：区域与节点 2：地图 3：背景*/
	private int type;
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}

	public String getImgName() {
		return imgName;
	}
	public void setImgName(String imgName) {
		this.imgName = imgName;
	}
	public long getFileId() {
		return fileId;
	}
	public void setFileId(long fileId) {
		this.fileId = fileId;
	}
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	
	
}
