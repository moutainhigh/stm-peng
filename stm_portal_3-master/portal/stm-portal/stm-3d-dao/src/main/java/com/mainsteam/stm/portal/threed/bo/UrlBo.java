package com.mainsteam.stm.portal.threed.bo;

import java.io.Serializable;

/**
 * 
 * <li>文件名称: UrlBo.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月26日
 * @author   liupeng
 */
public class UrlBo implements Serializable {
	private static final long serialVersionUID = 1061098689028676117L;
	private String ip;
	private int port;
	/**
	 * 3D机房集成状态
	 */
	private String status;
	/**
	 * 3D webservice地址
	 */
	private String webservicePath;
	/**
	 * 3D 主页地址
	 */
	private String homePath;
	/**
	 * 3D 后台地址
	 */
	private String adminPath;
	/**
	 * 3D 设备型号图片访问地址
	 */
	private String picturePath;
	/**
	 * 机柜结构
	 */
	private String nodeTree;
	/**
	 * 产品信息
	 */
	private String productInfo;
	
	public String getNodeTree() {
		return nodeTree;
	}

	public void setNodeTree(String nodeTree) {
		this.nodeTree = nodeTree;
	}

	public String getProductInfo() {
		return productInfo;
	}

	public void setProductInfo(String productInfo) {
		this.productInfo = productInfo;
	}

	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	public String getWebservicePath() {
		return webservicePath;
	}

	public void setWebservicePath(String webservicePath) {
		this.webservicePath = webservicePath;
	}

	public String getHomePath() {
		return homePath;
	}

	public void setHomePath(String homePath) {
		this.homePath = homePath;
	}

	public String getAdminPath() {
		return adminPath;
	}

	public void setAdminPath(String adminPath) {
		this.adminPath = adminPath;
	}

	public String getPicturePath() {
		return picturePath;
	}

	public void setPicturePath(String picturePath) {
		this.picturePath = picturePath;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	
}
