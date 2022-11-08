package com.mainsteam.stm.portal.business.web.action;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.file.bean.FileConstantEnum;
import com.mainsteam.stm.platform.file.service.IFileClientApi;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.business.api.IBizDepApi;
import com.mainsteam.stm.portal.business.bo.BizDepBo;

/**
 * <li>文件名称: BizDepAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月23日
 * @author   caoyong
 */
@Controller
@RequestMapping("/portal/business/service/dep")
public class BizDepAction extends BaseAction {
	private Logger logger = Logger.getLogger(BizDepAction.class); 
	@Autowired
	private IBizDepApi bizDepApi;
	@Autowired
	private IFileClientApi fileClient;
	
	/**
	 * 根据业务单位id或者业务服务id查询所有关系数据用于生成拓扑图数据
	 * @param id 业务单位id或者业务服务id
	 * @param type 业务单位或者业务服务(0;1)
	 * @return
	 */
	@RequestMapping("/getAllRelationsByIdAndType")
	public JSONObject getAllRelationsByIdAndType(Long id,Integer type){
		try {
			Object object = bizDepApi.getAllRelationsByIdAndType(id,type);
			logger.info("portal.business.service.dep.getAllRelationsByIdAndType successful");
			return toSuccess(object);
		} catch (Exception e) {
			logger.error("portal.business.service.dep.getAllRelationsByIdAndType failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return null;
		}
	}
	/**
	 * 业务单位(业务服务)新增
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("/insert")
	public JSONObject insert(HttpSession session, @ModelAttribute BizDepBo bizDepBo){
		try {
			ILoginUser user = getLoginUser(session);
			bizDepBo.setEntryId(user.getId());
			if(bizDepBo.getFileId()==0){
				bizDepBo.setFileId(bizDepBo.getType()==0?FileConstantEnum.FILE_CONSTANT_BIZ_DEP_IMG.getFileId():
					FileConstantEnum.FILE_CONSTANT_BIZ_SER_IMG.getFileId());
			}
			bizDepApi.insert(bizDepBo);
			if(bizDepBo.getId()<0){
				logger.info("portal.business.service.dep.insert failure for same name");
				return toFailForGroupNameExsit(null);
			}else{
				logger.info("portal.business.service.dep.insert successful");
				return toSuccess(bizDepBo);
			}
		} catch (Exception e) {
			logger.error("portal.business.service.dep.insert failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299,"新增操作失败");
		}
	}

	/**
	 * 业务单位(业务服务)删除
	 * @param id
	 * @param type
	 * @return
	 * @throws Exception 
	 */
	@RequestMapping("/del")
	public JSONObject del(int id,int type){
		try {
			int count = bizDepApi.del(id,type);
			logger.info("portal.business.service.dep.del successful");
			return toSuccess(count);
		} catch (Exception e) {
			logger.error("portal.business.service.dep.del failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299,"删除操作失败");
		}
	}
	
	/**
	 * 查询所有业务单位(业务服务)信息
	 * @param type (0:业务单位;1:业务服务)
	 * @return
	 */
	@RequestMapping("/getList")
	public JSONObject getList(Integer type){
		try {
			List<BizDepBo> bizDepBos = bizDepApi.getList(type);
			logger.info("portal.business.service.dep.getList successful");
			return toSuccess(bizDepBos);
		} catch (Exception e) {
			logger.error("portal.business.service.dep.getList failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299,"查询list失败");
		}
	}
	/**
	 * 通过ids数组查询返回业务单位(业务服务)list，关联资源list,区域节点背景图片的集合
	 * @param ids id数组
	 * @param resourceIds 数组
	 * @param picIds 数组
	 * @return
	 */
	@SuppressWarnings("rawtypes")
	@RequestMapping("/getListByIds")
	public JSONObject getListByIds(Long[] ids,Long[] resourceIds,Long[] picIds){
		try {
			List list = bizDepApi.getListByIds(Arrays.asList(ids),Arrays.asList(resourceIds),Arrays.asList(picIds));
			logger.info("portal.business.service.dep.getListByIds successful");
			return toSuccess(list);
		} catch (Exception e) {
			logger.error("portal.business.service.dep.getListByIds failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299,"根据ids查询list失败");
		}
	}
	/**
	 * 根据业务单位或者业务服务id查询当前记录对象
	 * @param id
	 * @return
	 */
	@RequestMapping("/get")
	public JSONObject get(long id){
		try {
			BizDepBo bo = bizDepApi.get(id);
			logger.info("portal.business.service.dep.get successful");
			return toSuccess(bo);
		} catch (Exception e) {
			logger.error("portal.business.service.dep.get failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299,"根据id查询失败");
		}
	}
	/**
	 * 更新业务单位或者业务服务
	 * @param bo
	 * @return
	 */
	@RequestMapping("/update")
	public JSONObject update(BizDepBo bo){
		try {
			int count = bizDepApi.update(bo);
			if(count<0){
				logger.info("portal.business.service.dep.update failure for same name");
				return toFailForGroupNameExsit(null);
			}else {
				logger.info("portal.business.service.dep.update successful");
				return toSuccess(count);
			}
		} catch (Exception e) {
			logger.error("portal.business.service.dep.update failure");
			logger.error(e.getMessage(),e);
			e.printStackTrace();
			return toJsonObject(299,"更新业务"+("0".equals(bo.getType().toString())?"单位":"服务")+"失败");
		}
	}
}
