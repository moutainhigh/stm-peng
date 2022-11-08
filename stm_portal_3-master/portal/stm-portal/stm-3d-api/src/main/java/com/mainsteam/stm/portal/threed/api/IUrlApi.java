package com.mainsteam.stm.portal.threed.api;

import com.mainsteam.stm.portal.threed.bo.UrlBo;
/**
 * 
 * <li>文件名称: IUrlApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年11月26日
 * @author   liupeng
 */
public interface IUrlApi {
	/**
	 * 向数据库添加一条3D信息记录
	 * @return
	 */
	public UrlBo add();
	/**
	 * 更新集成状态或者IP地址
	 * @param bo
	 * @return
	 */
	public int update(UrlBo bo);
	/**
	 * 获取3D相关信息
	 * @return
	 */
	public UrlBo get3DInfo();
	/**
	 * 获取3Dwebservice地址
	 * @return
	 */
	public String get3DWebservicePath();
	/**
	 * 获取3D主页地址
	 * @return
	 */
	public String get3DHomePath();
	/**
	 * 获取3D后台地址
	 * @return
	 */
	public String get3DAdminPath();
	/**
	 * 获取3D访问图片路径
	 * @return
	 */
	public String get3DPicturePath();
	
}
