package com.mainsteam.stm.home.layout.web.action;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;

import org.apache.log4j.Logger;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.home.layout.api.HomeLayoutApi;
import com.mainsteam.stm.home.layout.api.HomeLayoutDomainApi;
import com.mainsteam.stm.home.layout.bo.HomeLayoutBo;
import com.mainsteam.stm.home.layout.bo.HomeLayoutDomainBo;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.system.um.domain.bo.Domain;

/**
 * <li>文件名称: HomeLayoutDomainAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   下午2:18:53
 * @author   dengfuwei
 */
@Controller
@RequestMapping("system/home/layout/domain")
public class HomeLayoutDomainAction extends BaseAction {
	
	private static final Logger logger = Logger.getLogger(HomeLayoutDomainAction.class);
	
	@Resource
	private HomeLayoutDomainApi homeLayoutDomainApi;
	
	@Resource
	private HomeLayoutApi homeLayoutApi;
	
	@Resource(name="ocProtalHomeLayoutDomainSeq")
	private ISequence homeLayoutDomainSeq;

	/**
	 * 获取某布局的已授权域ID
	 * @param layoutId
	 * @return
	 */
	@RequestMapping("getLayoutDomain")
    public JSONObject getHomeLayout(long layoutId){
        ILoginUser user = getLoginUser();
        
        HomeLayoutDomainBo homeLayoutDomainBo = new HomeLayoutDomainBo();
		homeLayoutDomainBo.setUserId(user.getId());
		homeLayoutDomainBo.setLayoutId(layoutId);
        List<HomeLayoutDomainBo> list = homeLayoutDomainApi.getLayoutDomain(homeLayoutDomainBo);
        long [] domainIds = new long[list.size()];
        for(int i = 0; i < list.size(); i++){
        	domainIds[i] = list.get(i).getDomainId();
        }
        Map<String, Object> result = new HashMap<>();
        result.put("domainIds", domainIds);
        return toSuccess(result);
    }
	
	/**
	 * 保存布局授权域的关系
	 * @param layoutId
	 * @param domainIds
	 * @return
	 */
	@RequestMapping("saveLayoutDomain")
    public JSONObject getHomeLayout(long layoutId, long[] domainIds){
        ILoginUser user = getLoginUser();
        long userId = user.getId();
        if(layoutId < 1){
        	logger.error("布局ID为空,layoutId:" + layoutId + "; userId:" + userId);
        	return toSuccess("布局ID为空");
        }
        HomeLayoutBo homeLayoutBo = new HomeLayoutBo();
        homeLayoutBo.setId(layoutId);
        homeLayoutBo.setUserId(userId);
        homeLayoutBo = homeLayoutApi.getLayout(homeLayoutBo);
        if(homeLayoutBo == null){
        	logger.error("没有查询到布局数据，layoutId：" + layoutId + "; userId:" + userId);
        	return toSuccess("没有查询到布局数据");
        }
        
        List<HomeLayoutDomainBo> list = null;
        if(domainIds != null && domainIds.length > 0){
        	list = new ArrayList<>();
        	for(long item : domainIds){
        		HomeLayoutDomainBo homeLayoutDomainBo = new HomeLayoutDomainBo();
        		homeLayoutDomainBo.setCreateTime(new Timestamp(System.currentTimeMillis()));
        		homeLayoutDomainBo.setDomainId(item);
        		homeLayoutDomainBo.setLayoutId(layoutId);
        		homeLayoutDomainBo.setUserId(userId);
        		homeLayoutDomainBo.setId(homeLayoutDomainSeq.next());
        		list.add(homeLayoutDomainBo);
        	}
        }
        homeLayoutDomainApi.saveLayoutDomain(list, layoutId, userId);
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        return toSuccess(result);
    }
	
	/**
	 * 保存布局授权域的关系
	 * @param layoutId
	 * @param domainIds
	 * @return
	 */
	@RequestMapping("saveDomainsByLayoutId")
    public JSONObject saveDomainsByLayoutId(long layoutId, String domainsString){
        if(layoutId < 1){
        	logger.error("布局ID为空,layoutId:" + layoutId + ";");
        	return toSuccess("布局ID为空");
        }
        String[] domainIds = null;
        if (domainsString!=null && !"".equals(domainsString)) {
        	domainIds = domainsString.split(",");
		}
        List<HomeLayoutDomainBo> list = new ArrayList<>();
        if(domainIds != null && domainIds.length > 0){
        	list = new ArrayList<>();
        	for(String item : domainIds){
        		HomeLayoutDomainBo homeLayoutDomainBo = new HomeLayoutDomainBo();
        		homeLayoutDomainBo.setCreateTime(new Timestamp(System.currentTimeMillis()));
        		homeLayoutDomainBo.setDomainId(Long.valueOf(item));
        		homeLayoutDomainBo.setLayoutId(layoutId);
        		homeLayoutDomainBo.setId(homeLayoutDomainSeq.next());
        		list.add(homeLayoutDomainBo);
        	}
        }
        homeLayoutDomainApi.saveLayoutDomain(list, layoutId, 0);
        return toSuccess("提交完成");
    }
	
	/**
	 * 获取某布局的已授权域信息
	 * @param layoutId,seachContent
	 * @return
	 */
	@RequestMapping("getRightDomains")
	public JSONObject getRightDomains(long layoutId, String searchContent){
		return toSuccess(homeLayoutDomainApi.getDomainByLayoutId(layoutId, searchContent));
	}
	
	/**
	 * 获取某布局的未授权域信息
	 * @param layoutId,seachContent
	 * @return
	 */
	@RequestMapping("getLeftDomains")
	public JSONObject getLeftDomains(long layoutId, String searchContent){
		return toSuccess(homeLayoutDomainApi.getUnDomainByLayoutId(layoutId, searchContent));
	}
}
