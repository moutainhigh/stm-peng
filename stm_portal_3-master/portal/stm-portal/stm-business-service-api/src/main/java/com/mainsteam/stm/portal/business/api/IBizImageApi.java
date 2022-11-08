package com.mainsteam.stm.portal.business.api;

import java.util.List;

import com.mainsteam.stm.portal.business.bo.BizPicBo;

/**
 * <li>业务管理图片操作接口定义</li>
 * <li>文件名称: IBizImageApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @author zwx
 */
public interface IBizImageApi {
	/**
	 * 上传图片
	 * @param pic
	 * @throws Exception
	 */
	public long saveImage(BizPicBo pic) throws Exception;
	
	/**
	 * 分类型获取图片
	 * @param type	图片类型(0:业务,1:资源,2:基本形状,3:背景)
	 * @return List<BizPicBo>
	 */
	public List<BizPicBo> getImagesByType(int type);
	
	/**
	 * 根据文件ids删除图片
	 * @param ids
	 * @return
	 */
	public void deleteImgesByFiledIds(Long[] ids) throws Exception;

}
