package com.mainsteam.stm.portal.resource.web.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.common.CategoryDef;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.resource.api.ICustomResGroupApi;
import com.mainsteam.stm.portal.resource.api.ICustomResourceGroupApi;
import com.mainsteam.stm.portal.resource.bo.CustomGroupBo;
import com.mainsteam.stm.portal.resource.bo.CustomGroupEnumType;
import com.mainsteam.stm.portal.resource.web.vo.CustomGroupVo;
/**
 * 
 * <li>文件名称: CustomResGroupAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年7月24日
 * @author   wangxinghao
 */

@Controller
@RequestMapping("/portal/resource")
//@JsonIgnoreProperties(value = { "photos" })
public class CustomResGroupAction extends BaseAction {
	
	private static final Log logger = LogFactory.getLog(CustomResGroupAction.class);
	
	@Resource
	private CapacityService capacityService;
	
	@Resource
	private ResourceInstanceService resourceInstanceService;
	
	@Autowired
	private ICustomResourceGroupApi customResourceGroupApi;
	
	@Resource
	private ICustomResGroupApi customResGroupApi;
	
	
	/**
	 * 添加 自定义资源组
	 * @param categoryVo
	 * @return
	 */
	@RequestMapping("/addCustomGroup")
	public JSONObject addCustomGroup(CustomGroupVo customGroupVo,HttpSession session) {
		//获取当前登录用户
		ILoginUser user = getLoginUser(session);
		customGroupVo.setEntryId(user.getId());
		customGroupVo.setEntryDatetime(new Date());
		CustomGroupBo bo=toBo(customGroupVo);
		//获取当前资源组sort最大值
		int maxSort = customResourceGroupApi.getMaxSortByEntryId(user.getId());
		bo.setSort(maxSort + 1);
		int newGroupId = customResourceGroupApi.insert(bo);
		
		if(newGroupId == -1){
			return toFailForGroupNameExsit(null);
		}else{
			return toSuccess(newGroupId);
		}
		
	}
	
	/**
	 * 编辑 自定义资源组
	 * @param categoryVo
	 * @return
	 */
	@RequestMapping("/editCustomGroup")
	public JSONObject editCustomGroup(CustomGroupVo customGroupVo,HttpSession session) {
		//获取当前登录用户
		ILoginUser user = getLoginUser(session);
		customGroupVo.setEntryId(user.getId());
		customGroupVo.setEntryDatetime(new Date());
		
		CustomGroupBo bo = toBo(customGroupVo);
		
		int count = customResourceGroupApi.update(bo);
		
		if(count == -1){
			return toFailForGroupNameExsit(null);
		}else{
			// 更新首页关联的组信息
			customResGroupApi.operUpdateResGroupOthers(customGroupVo.getId(), customGroupVo.getName());
			return toSuccess(count);
		}
	}
	/**
	 * 获取所有 自定义资源组
	 * @return
	 */
	@RequestMapping("/getCustomGroupById")
	public JSONObject getCustomGroupById(long id, HttpSession session){
		List<CustomGroupVo> voList = new ArrayList<CustomGroupVo>();
		List<CustomGroupBo> list=customResourceGroupApi.getList(getLoginUser(session).getId());
		for(CustomGroupBo bo : list){
			CustomGroupVo vo = this.toVo(bo);
			voList.add(vo);
		}
		CustomGroupVo voById = null;
		for(CustomGroupVo vo : voList){
			if(vo.getPid() == null && vo.getId().longValue() == id){
				voList2Tree(vo, voList);
				voById = vo;
				break;
			}
		}
		return toSuccess(voById);
		
	}
	/**
	 * 获取所有 自定义资源组
	 * @return
	 */
	@RequestMapping("/getAllCustomGroup")
	public JSONObject getAllCustomGroup(HttpSession session){
		
		List<CustomGroupBo> list=customResourceGroupApi.getList(getLoginUser(session).getId());
		Collections.sort(list);
		
		List<CustomGroupVo> voList = new ArrayList<CustomGroupVo>();
		
		for(CustomGroupBo bo : list){
			CustomGroupVo vo = this.toVo(bo);
			voList.add(vo);
		}
		List<CustomGroupVo> newVoList = new ArrayList<CustomGroupVo>();
		for(CustomGroupVo vo : voList){
			if(vo.getPid() == null){
				voList2Tree(vo, voList);
				newVoList.add(vo);
			}
		}
		return toSuccess(newVoList);
		
	}
	
	
	private void voList2Tree(CustomGroupVo parentVo, List<CustomGroupVo> voList){
		List<CustomGroupVo> childCustomGroupVo = new ArrayList<CustomGroupVo>();
		for(int i = 0; i < voList.size(); i++){
			CustomGroupVo vo = voList.get(i);
			if(vo.getPid() != null && parentVo.getId().longValue() == vo.getPid().longValue()){
				childCustomGroupVo.add(vo);
				voList2Tree(vo, voList);
			}
		}
		parentVo.setChildCustomGroupVo(childCustomGroupVo);
	}
	
	private void voTree2List(CustomGroupVo rootVo, List<CustomGroupVo> voList){
		voList.add(rootVo);
		if(rootVo.getChildCustomGroupVo() != null && !rootVo.getChildCustomGroupVo().isEmpty()){
			for(int i = 0; i < rootVo.getChildCustomGroupVo().size(); i++){
				voTree2List(rootVo.getChildCustomGroupVo().get(i), voList);
			}
		}
	}

	
	/**
	 * 删除  自定义资源组
	 * @param id
	 * @return
	 */
	@RequestMapping("/delCustomGroup")
	public JSONObject delCustomGroup(CustomGroupVo customGroupVo, HttpSession session) {
		List<CustomGroupVo> voList = new ArrayList<CustomGroupVo>();
		List<CustomGroupBo> list=customResourceGroupApi.getList(getLoginUser(session).getId());
		for(CustomGroupBo bo : list){
			CustomGroupVo vo = this.toVo(bo);
			voList.add(vo);
		}
		// 转换成树
		CustomGroupVo voTree = null;
		for(CustomGroupVo vo : voList){
			if(vo.getId().longValue() == customGroupVo.getId().longValue()){
				voList2Tree(vo, voList);
				voTree = vo;
				break;
			}
		}
		int count = 0;
		if(voTree != null){
			voList = new ArrayList<CustomGroupVo>();
			voTree2List(voTree, voList);
			for(int i = 0; i < voList.size(); i++){
				count += customResourceGroupApi.del(voList.get(i).getId());
			}
		}else{
			count = customResourceGroupApi.del(customGroupVo.getId());
		}
		// 删除首页关联的组信息
		customResGroupApi.operDelResGroupOthers(customGroupVo.getId());
		return toSuccess(count);
		
		
	}
	
	/**
	 * 从自定义资源组删除资源
	 * @param id
	 * @return
	 */
	@RequestMapping("/delResourceFromCustomGroup")
	public JSONObject delResourceFromCustomGroup(CustomGroupBo customGroupBo, HttpSession session) {
		List<CustomGroupVo> voList = new ArrayList<CustomGroupVo>();
		List<CustomGroupBo> list = customResourceGroupApi.getList(getLoginUser(session).getId());
		for(CustomGroupBo bo : list){
			CustomGroupVo vo = this.toVo(bo);
			voList.add(vo);
		}
		CustomGroupVo voTree = null;
		for(CustomGroupVo vo : voList){
			if(vo.getId().longValue() == customGroupBo.getId().longValue()){
				voList2Tree(vo, voList);
				voTree = vo;
				break;
			}
		}
		int count = 0;
		if(voTree != null){
			count = delResourceFromCustomGroupTree(voTree, customGroupBo.getResourceInstanceIds());
		}else{
			count = customResourceGroupApi.deleteResourceFromCustomGroup(customGroupBo);
		}
		return toSuccess(count);
	}
	
	public int delResourceFromCustomGroupTree(CustomGroupVo voTree, List<String> resourceInstanceIds){
		int count = 0;
		if(voTree.getResourceInstanceIds() != null && !voTree.getResourceInstanceIds().isEmpty()){
			List<String> delResourceInstanceIds = new ArrayList<String>();
			for (int i = 0; i < voTree.getResourceInstanceIds().size(); i++) {
				if(resourceInstanceIds.contains(voTree.getResourceInstanceIds().get(i))){
					delResourceInstanceIds.add(voTree.getResourceInstanceIds().get(i));
				}
			}
			if(!delResourceInstanceIds.isEmpty()){
				CustomGroupBo customGroupBo = new CustomGroupBo();
				customGroupBo.setId(voTree.getId());
				customGroupBo.setResourceInstanceIds(delResourceInstanceIds);
				count += customResourceGroupApi.deleteResourceFromCustomGroup(customGroupBo);
			}
		}
		if(voTree.getChildCustomGroupVo() != null && !voTree.getChildCustomGroupVo().isEmpty()){
			for (int i = 0; i < voTree.getChildCustomGroupVo().size(); i++) {
				count += delResourceFromCustomGroupTree(voTree.getChildCustomGroupVo().get(i), resourceInstanceIds);
			}
		}
		return count;
	}
	
	/**
	 * 从自定义资源组id查询
	 * @param id
	 * @return
	 */
	@RequestMapping("/getCustomGroup")
	public JSONObject getCustomGroup(long id){
		CustomGroupBo customGroupBo = customResourceGroupApi.getCustomGroup(id);
		return toSuccess(toVo(customGroupBo));
	}
	
	@RequestMapping("/getcapacity")
	public JSONObject getCapacity() {
		CategoryDef categoryDef = capacityService.getRootCategory();

		try {
			ResourceInstance a = resourceInstanceService
					.getResourceInstance(1L);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			if(logger.isErrorEnabled()){
				logger.error(e.getMessage(),e);
			}
		}

		// 第一级 菜单
		CategoryDef[] baseCategoryDef = categoryDef.getChildCategorys();

		List<String> list = new ArrayList<String>();
		for (CategoryDef c : baseCategoryDef) {
			list.add(c.getName());
		}

		return toSuccess(list);
	}
	
	
	/**
	 * 新增资源与自定义资源组关系
	 * @return
	 */
	@RequestMapping("/insertGroupRelation")
	public JSONObject insertGroupAndResourceRelation(long groupId, String resourceID){
		int count =  customResourceGroupApi.insertGroupAndResourceRelation(groupId, resourceID);
		return toSuccess(count);
		
	}
	
	/**
	 * 批量移入资源组
	 */
	@RequestMapping("/insertIntoGroupForInstanceList")
	public JSONObject insertIntoGroupForInstanceList(String instanceIds,String groupIds){
		boolean result = customResourceGroupApi.insertIntoGroupForInstances(instanceIds, groupIds);
		return toSuccess(result);
	}
	
	@RequestMapping("/changeCustomGroupSort")
	public JSONObject changeCustomGroupSort(Long groupId, String direction){
		 Map<String, Object> result = customResGroupApi.changeGroupSort(groupId, direction);
		return toSuccess(result);
	}
	
	/**
	 * 对象转化
	 * @param vo
	 * @return
	 */
	public CustomGroupBo toBo(CustomGroupVo vo){
		CustomGroupBo bo=new CustomGroupBo();
		bo.setId(vo.getId());
		bo.setName(vo.getName());
		bo.setResourceInstanceIds(vo.getResourceInstanceIds());
		bo.setEntryId(vo.getEntryId());
		bo.setEntryDatetime(vo.getEntryDatetime());
		bo.setGroupType(CustomGroupEnumType.USER);
		bo.setPid(vo.getPid());
		bo.setSort(vo.getSort());
		return bo;
	}
	
	/**
	 * 对象转化
	 * @param bo
	 * @return
	 */
	public CustomGroupVo toVo(CustomGroupBo bo){
		CustomGroupVo vo=new CustomGroupVo();
		vo.setId(bo.getId());
		vo.setName(bo.getName());
		vo.setResourceInstanceIds(bo.getResourceInstanceIds());
		vo.setEntryId(bo.getEntryId());
		vo.setEntryDatetime(bo.getEntryDatetime());
		vo.setGroupType(bo.getGroupType().toString());
		vo.setPid(bo.getPid());
		vo.setSort(bo.getSort());
		return vo;
	}
	
	
}
