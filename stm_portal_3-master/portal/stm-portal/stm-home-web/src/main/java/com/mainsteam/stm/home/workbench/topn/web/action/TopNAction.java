package com.mainsteam.stm.home.workbench.topn.web.action;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.mainsteam.stm.home.workbench.resource.bo.WorkbenchResourceInstance;
import com.mainsteam.stm.instancelib.objenum.InstanceLifeStateEnum;
import com.mainsteam.stm.util.StringUtil;
import org.apache.log4j.Logger;

import javax.annotation.Resource;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.caplib.CapacityService;
import com.mainsteam.stm.caplib.resource.ResourceDef;
import com.mainsteam.stm.home.workbench.main.api.IUserWorkBenchApi;
import com.mainsteam.stm.home.workbench.resource.api.IResourceApi;
import com.mainsteam.stm.home.workbench.topn.api.IHomeTopnApi;
import com.mainsteam.stm.instancelib.ResourceInstanceService;
import com.mainsteam.stm.instancelib.exception.InstancelibException;
import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.business.api.BizMainApi;
import com.mainsteam.stm.portal.business.service.bo.BizMainBo;
import com.mainsteam.stm.portal.resource.api.IResourceApplyApi;
import com.mainsteam.stm.topo.api.IInstanceTableApi;
import com.mainsteam.stm.topo.bo.LinkBo;

/**
 * <li>文件名称: TopNAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2019年9月16日
 * @author   zhangjunfeng
 */
@Controller
@RequestMapping("system/home")
public class TopNAction extends BaseAction {
	
	private static final Logger logger = Logger.getLogger(TopNAction.class);

	@Autowired
	private IHomeTopnApi homeTopnApi;
	
	@Autowired
	@Qualifier("stm_home_workbench_resourceApi")
	private IResourceApi homeResourceApi;

	@Autowired
	private IUserWorkBenchApi userWorkBenchApi;
	
	@Resource
	private ResourceInstanceService resourceInstanceService;
	
	@Autowired
	private CapacityService capacityService;
	
	@Autowired
	private ResourceInstanceService RIService;
	
	@Autowired
	private BizMainApi bizMainApi;
	
	@Resource
	private IResourceApplyApi resourceApplyApi;
	@Autowired
	private IInstanceTableApi instanceTableApi;

    @Autowired
    IResourceApi resourceApi;

	
	/** 
	* @Title: getHomeTopnData 
	* @param domainId 当domainId[0] 等于-1时表示查询当前用户的所有域
	* @return JSONObject    返回类型 
	* @throws 
	*/
	@RequestMapping("getHomeTopnData")
	public JSONObject getHomeTopnData(HttpSession session, String resource, String metric, String resourceIds, boolean sortAll,
              long groupId, int top, String sortMethod, String showPattern, String subMetrics, Long ... domainId){
		ILoginUser user = getLoginUser(session);
		logger.info("malachi getHomeTopnData ............");
		if(domainId != null){
			//domainId[0] ==-1 表示是获取当前用户所有的域
			if(domainId[0] == -1){
				Set<IDomain> dms = user.getDomains();
				Long dmIds[] = new Long[dms.size()];
				int i=0;
				for (IDomain dm : dms) {
					dmIds[i++] = dm.getId();
				}
				domainId = dmIds;
			}
		}
		logger.info("malachi domainId ............" + domainId);
		if(sortMethod == null || sortMethod.equals("")){
			sortMethod = "desc";
		}
		if(!sortAll){
			sortAll = true;
		}
		List<Long> instanceIds = new ArrayList<>();
		
		
		if(sortAll){//是否对所有的资源进行topn排序
			if(groupId != 0){
				instanceIds = userWorkBenchApi.getInstanceIdByGroupId(groupId, user, resource,domainId);
			}else{
				instanceIds =  homeResourceApi.queryGroupParentResourceByDomain(resource, user, groupId, domainId);
			}
			//查询链路相关的数据
			if(resource.equals("Link")){
				List<ResourceInstance> links = RIService.getAllResourceInstancesForLink();
				for(ResourceInstance ri : links){
					for(long did :domainId){
						if(ri.getLifeState() == InstanceLifeStateEnum.MONITORED && ri.getDomainId() == did){
							instanceIds.add(ri.getId());
							break;
						}
					}
				}
			}
		}
		logger.info("malachi sortAll ............" + sortAll);
		logger.info("malachi groupId ............" + groupId);
		logger.info("malachi instanceIds ............" + instanceIds.size());
		Set<Long> instanceIdSet = new HashSet<Long>();
		instanceIdSet.addAll(instanceIds);
		/*for(Long id : instanceIds){
			instanceIdSet.add(id);
		}*/
//		if(!resource.equals("Link")){
//			List<Long> instanceIdsTemp = new ArrayList<>();
//			for(Long id : instanceIds){
//				try {
//					List<ResourceInstance> childInstanceByParentId = resourceInstanceService.getChildInstanceByParentId(id);
//					if(childInstanceByParentId != null && childInstanceByParentId.size() > 0){
//						for(ResourceInstance re : childInstanceByParentId){
////							if("NetInterface".equals(re.getChildType())){
//								instanceIdsTemp.add(re.getId());
////							}
//						}
//					}
//				} catch (InstancelibException e) {
//					logger.error("查询网络接口错误",e);
//				}
//			}
//			instanceIds.addAll(instanceIdsTemp);
//		}
		logger.info("malachi resource ............" + resource);
		if(!resource.equals("Link") && (!"".equals(subMetrics))&& instanceIdSet.size() > 0){
		    if("接口".equals(subMetrics)){
                List<Long> allChildrenInstanceIdbyParentId = resourceInstanceService.getAllChildrenInstanceIdbyParentId(instanceIdSet);
                if(allChildrenInstanceIdbyParentId != null && allChildrenInstanceIdbyParentId.size() > 0){
                    instanceIds.addAll(allChildrenInstanceIdbyParentId);
                }
            }else{
                List<WorkbenchResourceInstance> instanceByResource = resourceApi.getInstanceByResource(subMetrics, domainId, 0, Integer.MAX_VALUE, null, user);
                for (int i = 0; i < instanceByResource.size(); i++) {
                    instanceIds.add(instanceByResource.get(i).getId());
                }
            }
        }
		logger.info("malachi resourceIds ............" + resourceIds);
		String[] rids = resourceIds.split(",");
		List<Long> instanceIdsLink = new ArrayList<>();
		for(String rid : rids){
			if("-1".equals(rid)){
				continue;
			}
			for(long id : instanceIds){
				if(Long.parseLong(rid) == id){
					instanceIdsLink.add(id);
				}
			}
		}
		if(rids.length == 1 && "-1".equals(rids[0])){
			instanceIdsLink = instanceIds;
		}
		instanceIds = instanceIdsLink;
		logger.info("malachi instanceIds ............" + instanceIds);
		//判断是否存在子资源
        boolean isSubMetrics = true;
        if(StringUtil.isNull(subMetrics)){
            isSubMetrics = false;
        }
		logger.info("malachi isSubMetrics ............" + isSubMetrics);
		Map<String, Object> result = homeTopnApi.getHomeTopnData( metric,instanceIds,top,sortMethod,showPattern,isSubMetrics);
		logger.info("malachi result ............" + result);
		if(result != null){
			for (String key :result.keySet()){
				logger.info(key + " ==== " + result.get(key));
			}
		}
		logger.info("malachi end ............");
		return toSuccess(result);
	}
	
	/**
	 * 
	 * @param startNum
	 * @param pageSize
	 * @param content 用于搜索的内容
	 * @param domainId 域Id
	 * @return
	 */
	@RequestMapping("getHomeLinkData")
	public JSONObject getHomeLinkData(int startNum, int pageSize, String content, Long... domainId){
		List<Map<String, Object>> output = new ArrayList<>();
		ILoginUser user = getLoginUser();
		try {
			List<ResourceInstance> trs = resourceInstanceService.getParentInstanceByCategoryId("Link");
			List<ResourceInstance> rs = new  ArrayList<>();
			if(content == null){
				content = "";
			}
			if(domainId !=null ){
				if(domainId[0] == -1){
					Set<IDomain> dms = user.getDomains();
					Long dmIds[] = new Long[dms.size()];
					int i=0;
					for (IDomain dm : dms) {
						dmIds[i++] = dm.getId();
					}
					domainId = dmIds;
				}
				for (ResourceInstance ri : trs) {
					if(ri.getShowName().indexOf(content) <0 && !content.equals("链路")){
						continue;
					}
					
					for (int i = 0; i < domainId.length; i++) {
						Long did = domainId[i];
						if(did != null && ri.getDomainId() == did){
							rs.add(ri);
							break;
						}
					}
				}
			}else{
				rs = trs;
			}
			int toindex = startNum+pageSize;
			if(startNum>=rs.size()){
				startNum = rs.size();
				toindex = startNum;
			}
			if(toindex > rs.size()){
				toindex = rs.size();
			}
				
			List<ResourceInstance> subRs = rs.subList(startNum, toindex);
			
			for (ResourceInstance ri : subRs) {
				HashMap<String, Object> map = new HashMap<>();
				map.put("id", ri.getId());
				map.put("discoverIP","--");
				map.put("resourceId", ri.getResourceId());
				ResourceDef cdef = capacityService.getResourceDefById(ri.getResourceId());
				map.put("resourceName", cdef.getName());
				map.put("showName",ri.getShowName());
				output.add(map);
			}
		} catch (InstancelibException e) {
			logger.error(e.getMessage(),e);
		}
		return toSuccess(output);
	}
	
	/**
	 * 
	 * @param startNum
	 * @param pageSize
	 * @param content 用于搜索的内容
	 * @param domainId 域Id
	 * @return
	 */
	@RequestMapping("getNewHomeLinkData")
	public JSONObject getNewHomeLinkData(int startNum, int pageSize, String content, Long... domainId){
		List<Map<String, Object>> output = new ArrayList<>();
		try {
			ILoginUser user = getLoginUser();
			Set<Long> domains = new HashSet<Long>();
			Set<IDomain> dms = user.getDomains();
			if(domainId[0] == -1){
				for (IDomain dm : dms) {
					domains.add(dm.getId());
				}
			}else{
				for(Long id : domainId){
					domains.add(id);
				}
			}
			//封装查询条件
			Page<LinkBo, LinkBo> page = new Page<LinkBo, LinkBo>();
			LinkBo params = new LinkBo();
			page.setRowCount(pageSize);
			page.setStartRow(startNum);
			params.setDomainSet(domains);
			params.setSearchVal(content);
			page.setCondition(params);
			instanceTableApi.homeSelectLinkByPage(page);
			
			List<LinkBo> linkBos = page.getDatas();
			for(LinkBo lb : linkBos){
				HashMap<String, Object> map = new HashMap<>();
				map.put("id", lb.getInstanceId());
				map.put("discoverIP",lb.getSrcMainInstIP());
				map.put("resourceId", "Layer2Link");
				map.put("resourceName", "链路");
				map.put("showName", lb.getSrcMainInstIP() +":" +lb.getSrcMainInstName() + "-" + 
				lb.getDestMainInstIP() + ":" + lb.getDestMainInsName());
				output.add(map);
			}
			
		} catch (Exception e) {
			logger.error("查询资源链路列表数据失败!",e);
		}
		return super.toSuccess(output);
	}
	
	@RequestMapping("getHomeBusinessData")
	public JSONObject getBusinessData(int startNum, int pageSize, String content, Long... domainId){
		List<Map<String, Object>> output = new ArrayList<>();
		ILoginUser user=getLoginUser();
		List<BizMainBo> trs = bizMainApi.getBizsForHome(user);
		List<BizMainBo> rs = new ArrayList<>();
		if(content == null){
			content = "";
		}
		if(domainId !=null ){
			for (BizMainBo ri : trs) {
				if(ri.getName().indexOf(content) <0 && !content.equals("业务系统")){
					continue;
				}
				/*这里不进行域过滤
				for (int i = 0; i < domainId.length; i++) {
					Long did = domainId[i];
					if(ri.getDomainId() == did){
						rs.add(ri);
						break;
					}
				}//*/
				rs.add(ri);
			}
		}else{
			rs = trs;
		}
		int toindex = startNum+pageSize;
		if(startNum>=rs.size()){
			startNum = rs.size();
			toindex = startNum;
		}
		if(toindex > rs.size()){
			toindex = rs.size();
		}
			
		List<BizMainBo> subRs = rs.subList(startNum, toindex);
		
		for (BizMainBo ri : subRs) {
			HashMap<String, Object> map = new HashMap<>();
			map.put("id", ri.getId());
			map.put("discoverIP","--");
			map.put("resourceId", "Business");
			map.put("resourceName","业务系统");
			map.put("showName",ri.getName());
			output.add(map);
		}
		
		return toSuccess(output);
	}
}
