package com.mainsteam.stm.system.image.api;

import com.mainsteam.stm.system.image.bo.ImageBo;

/**
 * <li>文件名称: IImageApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: 用于系统管理-系统设置-图片管理</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月5日
 * @author   ziwenwen
 */
public interface IImageApi {
	/**
	 * 获取图片管理对象
	 * @return
	 */
	ImageBo get();

	/**
	 * 修改图片管理对象
	 * @param imageBo
	 * @return
	 */
	boolean update(ImageBo imageBo);
	
	/**
	 * 获取系统LOGO
	 * */
	String getSystemLogo();
	
	
	/**
	 * 获取Login LOGO
	 * */
	String getLoginLogo();
	
	
	/**
	 * 获取系统copyright
	 * */
	String getCopyright();
	
	String getLogoPsd();
}


