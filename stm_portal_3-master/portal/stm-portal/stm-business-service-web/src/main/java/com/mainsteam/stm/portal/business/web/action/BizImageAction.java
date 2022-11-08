package com.mainsteam.stm.portal.business.web.action;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.business.api.IBizImageApi;
import com.mainsteam.stm.portal.business.bo.BizPicBo;


/**
 * <li>业务管理的图片处理action</li>
 * <li>文件名称: BizImageAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since  2016年9月9日
 * @author zwx
 */
@Controller
@RequestMapping(value="portal/biz/image")
public class BizImageAction extends BaseAction{
	
	private final Logger logger = LoggerFactory.getLogger(BizImageAction.class);
	@Autowired
	private IBizImageApi bizImageApi;
	
	/**
	 * 上传图片
	 * @param pic 	图片信息
	 * @return
	 */
	@RequestMapping("/save")
	public JSONObject save(BizPicBo pic){
		try {
			if(pic.getFileId() != 0){
				ILoginUser user = getLoginUser();
				pic.setUploadId(user.getId());	//上传用户Id
				
				long fileId = bizImageApi.saveImage(pic);
				return toSuccess(fileId);
			}else{
				return toJsonObject(700, "请选择图片后再上传");
			}
		} catch (Exception e) {
			logger.error("图片上传失败!",e);
			return toJsonObject(700, "图片上传失败");
		}
	}
	
	/**
	 * 分类型获取图片
	 * @param type 类型
	 * @return
	 */
	@RequestMapping(value="/list/{type}")
	public JSONObject getImagesByType(@PathVariable(value="type") int type){
		return toSuccess(bizImageApi.getImagesByType(type));
	}
	
	/**
	 * 删除图片
	 * @param ids
	 * @return
	 */
	@RequestMapping(value="del", method=RequestMethod.POST)
	public JSONObject deleteImagesByFiledIds(Long[] ids) {
		try {
			bizImageApi.deleteImgesByFiledIds(ids);
			return toSuccess("删除成功");
		} catch (Exception e) {
			logger.error("删除图片失败!",e);
			return toJsonObject(700, "删除图片失败");
		}
	}
}
