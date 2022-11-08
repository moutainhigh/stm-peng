package com.mainsteam.stm.portal.business.service.impl;

import java.util.List;

import javax.annotation.Resource;

import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.file.bean.FileConstantEnum;
import com.mainsteam.stm.platform.file.service.IFileClientApi;
import com.mainsteam.stm.portal.business.api.BizCanvasApi;
import com.mainsteam.stm.portal.business.api.IBizImageApi;
import com.mainsteam.stm.portal.business.bo.BizCanvasNodeBo;
import com.mainsteam.stm.portal.business.bo.BizPicBo;
import com.mainsteam.stm.portal.business.dao.IBizCanvasDao;
import com.mainsteam.stm.portal.business.dao.IBizPicDao;

/**
 * <li>业务管理图片处理实现</li>
 * <li>文件名称: BizImageImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @author zwx
 */
@Service
public class BizImageImpl implements IBizImageApi{
	//图片上传服务
	@Resource(name="fileClient")
	private IFileClientApi fileClient;
	@Resource
	private IBizPicDao bizPicDao;
	
	@Resource
	private IBizCanvasDao bizCanvasDao;

	@Override
	public void deleteImgesByFiledIds(Long[] ids) {
		bizPicDao.deleteImgesByFiledIds(ids);
		
		for(Long deleteId : ids){
			//修改使用了该图片的节点
			List<BizCanvasNodeBo> attrs = bizCanvasDao.getCanvasNodesByFileId(deleteId);
			
			for(BizCanvasNodeBo attr : attrs){
				attr.setFileId(FileConstantEnum.FILE_CONSTANT_BIZ_MAIN_IMG_1.getFileId());
				JSONObject json = JSONObject.parseObject(attr.getAttr());
				String src = json.getString("src");
				if(src != null && !src.equals("")){
					if(src.contains(deleteId + "")){
						String newSrc = src.replace(deleteId + "", FileConstantEnum.FILE_CONSTANT_BIZ_MAIN_IMG_1.getFileId() + "");
						json.remove("src");
						json.put("src", newSrc);
						attr.setAttr(json.toString());
					}
				}
				bizCanvasDao.updateCanvasFileIdInfo(attr);
			}
			
		}
		
	}
	
	@Override
	public List<BizPicBo> getImagesByType(int type) {
		return bizPicDao.getImagesByType(type);
	}

	@Override
	public long saveImage(BizPicBo icon) throws Exception {
		bizPicDao.save(icon);
		return icon.getFileId();
	}
	
}
