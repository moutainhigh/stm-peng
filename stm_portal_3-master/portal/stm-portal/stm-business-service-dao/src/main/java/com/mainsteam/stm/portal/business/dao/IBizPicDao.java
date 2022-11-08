package com.mainsteam.stm.portal.business.dao;

import java.util.List;

import com.mainsteam.stm.portal.business.bo.BizPicBo;

public interface IBizPicDao {
	
	/**
	 * 分类型获取图片
	 * @param type	图片类型(0:业务,1:资源,2:基本形状,3:背景)
	 * @return List<BizPicBo>
	 */
	public List<BizPicBo> getImagesByType(int type);
	
	/**
	 * 根据业务所有上传的图片
	 * @param ids
	 * @return List<BizPicBo>
	 */
	public List<BizPicBo> getAllImages();
	
	/**
	 * 根据文件ids删除图片数据
	 * @return int 影响行数
	 */
	public int deleteImgesByFiledIds(Long ids[]);
	
	/**
	 * 新增图片
	 * @param pic
	 * @return
	 */
	public int save(BizPicBo pic);
}
