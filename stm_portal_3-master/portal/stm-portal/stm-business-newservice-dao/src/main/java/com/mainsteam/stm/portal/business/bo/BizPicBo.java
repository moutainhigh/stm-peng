package com.mainsteam.stm.portal.business.bo;

import java.io.Serializable;

public class BizPicBo implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -795823731427086021L;

	//id
	private long id;
	
	//图片类型(0:业务,1:资源,2:基本形状,3:背景)
	private int imgType;
	
	//文件服务器文件ID
	private long fileId;
	
	//上传用户ID
	private long uploadId;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public int getImgType() {
		return imgType;
	}

	public void setImgType(int imgType) {
		this.imgType = imgType;
	}

	public long getFileId() {
		return fileId;
	}

	public void setFileId(long fileId) {
		this.fileId = fileId;
	}

	public long getUploadId() {
		return uploadId;
	}

	public void setUploadId(long uploadId) {
		this.uploadId = uploadId;
	}
	
}
