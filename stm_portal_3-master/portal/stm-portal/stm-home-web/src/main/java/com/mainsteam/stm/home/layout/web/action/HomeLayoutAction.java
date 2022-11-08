package com.mainsteam.stm.home.layout.web.action;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Resource;

import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.RequestMapping;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.home.layout.api.HomeLayoutApi;
import com.mainsteam.stm.home.layout.api.HomeLayoutDomainApi;
import com.mainsteam.stm.home.layout.api.HomeLayoutModuleApi;
import com.mainsteam.stm.home.layout.api.HomeLayoutSlideApi;
import com.mainsteam.stm.home.layout.bo.HomeLayoutBo;
import com.mainsteam.stm.home.layout.bo.HomeLayoutDefaultBo;
import com.mainsteam.stm.home.layout.bo.HomeLayoutDomainBo;
import com.mainsteam.stm.home.layout.bo.HomeLayoutModuleConfigBo;
import com.mainsteam.stm.home.layout.bo.HomeLayoutSlideBo;
import com.mainsteam.stm.home.layout.vo.HomeLayoutVo;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.vo.IDomain;
import com.mainsteam.stm.platform.web.vo.ILoginUser;

/**
 * <li>文件名称: HomeLayoutAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   2016年12月21日
 * @author   dengfuwei
 */
@Controller
@RequestMapping("system/home/layout")
public class HomeLayoutAction extends BaseAction {

    @Resource(name="homeLayoutApi")
    private HomeLayoutApi homeLayoutApi;
    
	@Resource
	private HomeLayoutSlideApi homeLayoutSlideApi;

    @Resource(name="ocProtalHomeLayoutSeq")
    private ISequence homeLayoutSeq;
    @Resource(name="ocProtalHomeLayoutDomainSeq")
    private ISequence homeDomainSeq;
    
    @Resource(name="ocProtalHomeLayoutModuleConfigSeq")
    private ISequence homeLayoutModuleConfigSeq;
	@Resource
	private HomeLayoutDomainApi homeLayoutDomainApi;
	
	@Resource
	private HomeLayoutModuleApi homeLayoutModuleApi;
    /**
     * @Title: getHomeLayout
     * @Description: 获取用户首页布局
     * @return JSONObject    返回类型
     * @throws
     */
    @RequestMapping("getHomeLayout")
    public JSONObject getHomeLayout(Long layoutId){
        ILoginUser user = getLoginUser();
        Map<String, Object> result = new HashMap<>();

        //获取默认首页布局
        if(layoutId == null){
        	HomeLayoutBo homeLayoutBo = homeLayoutApi.getUserDefaultLayout(user.getId());
        	List<HomeLayoutModuleConfigBo> homeLayoutModuleConfigList = null;
        	if(homeLayoutBo != null){
        		homeLayoutModuleConfigList = homeLayoutApi.getLayoutModuleConfig(homeLayoutBo.getId());
        	}
        	result.put("homeLayoutBo", homeLayoutBo);
        	result.put("homeLayoutModuleConfigList", homeLayoutModuleConfigList);
        	result.put("username", user.getName());
        	return toSuccess(result);
        }
        
        //通过Id来获取首页布局信息
        HomeLayoutBo homeLayoutBo = homeLayoutApi.getLayoutById(layoutId);
    	List<HomeLayoutModuleConfigBo> homeLayoutModuleConfigList = null;
    	homeLayoutModuleConfigList = homeLayoutApi.getLayoutModuleConfig(layoutId);
    	result.put("homeLayoutBo", homeLayoutBo);
    	result.put("homeLayoutModuleConfigList", homeLayoutModuleConfigList);
    	result.put("username", user.getName());
    	return toSuccess(result);
    }
    @RequestMapping("getDefaultLayout")
    public JSONObject getDefaultLayout(){
        ILoginUser user = getLoginUser();
    HomeLayoutDefaultBo homeLayoutBo=    homeLayoutApi.getLayoutDefault(user.getId());
  
		return toSuccess(homeLayoutBo);
    }
    
    /**
     * 获取用户所有页面布局Page（不包含模板）
     * @return
     */
    @RequestMapping("getPageLayout")
    public JSONObject getAllLayout(Page<HomeLayoutBo, HomeLayoutVo> page,HomeLayoutVo vo){
        ILoginUser user = getLoginUser();
        Set<IDomain> domains=  user.getDomains();
        List<Long> domainids=new ArrayList<Long>();
        Iterator<IDomain> it= domains.iterator();
        while (it.hasNext()) {  
        	IDomain domain = it.next();
        	domainids.add(domain.getId());
        }
      //  vo.setDomainids(domainids); 
       if(user.isSystemUser()==true){//admin
    		vo.setDomainids(domainids); 
        }else{
        	vo.setDomainids(domainids); 
        //	vo.setDomainids(null); 
        }
        vo.setUserId(user.getId());
        page.setCondition(vo);
        homeLayoutApi.selectByPage(page);
        HomeLayoutDefaultBo defaultBo=    homeLayoutApi.getLayoutDefault(user.getId());
       if( page.getRows()!=null){
    	  List<HomeLayoutBo> dtas= page.getRows();
    	  if(defaultBo!=null){
    		  for (int i = 0; i < dtas.size(); i++) {
    				if(dtas.get(i).getId()==defaultBo.getDefaultLayoutId()){
    					dtas.get(i).setIsDefault(0);
    				}else{
    					dtas.get(i).setIsDefault(1);
    				}
    			} 
    	  }
    	
       }
        return toSuccess(page);
    }
    /**
     * 获取用户所有页面布局List（不包含模板）
     * @return
     */
    @RequestMapping("getAllLayouts")
    public JSONObject getAllLayouts(){
    	ILoginUser user = getLoginUser();
    	HomeLayoutVo layoutVo= new HomeLayoutVo();
      	List<Long> domainlist= new ArrayList<Long>();
    	Set<IDomain> domains=user.getDomains();	
    	if(domains.size()!=0){
    		for(IDomain domain:domains){
    			//domain.getId();
    			domainlist.add(domain.getId());
    		}
    	   }
    	layoutVo.setDomainids(domainlist);	

    	layoutVo.setUserId(user.getId());
    	List<HomeLayoutBo> lists =homeLayoutApi.getTempsById(layoutVo);// homeLayoutApi.getLayout(user.getId());
    	List<HomeLayoutSlideBo> slideBos = homeLayoutSlideApi.get(user.getId());
    /*	for (int i = 0; i < lists.size(); i++) {
    		for (int j = 0; j < slideBos.size(); j++) {
    			if(lists.get(i).getId()==slideBos.get(j).getLayoutId()){
    				lists.remove(i);
    			}
    		}
    	}*/
    	Map<String, Object> result = new HashMap<>();
    	result.put("list", lists);
		return toSuccess(result);
 
    }
    /**
     * 获取登录用户所有布局
     * @return List
     * @author zhangkai
     */
    @RequestMapping("getAllLayoutList")
    public JSONObject getAllLayoutList(){
    	ILoginUser user = getLoginUser();
    	List<HomeLayoutBo> layoutList = homeLayoutApi.getLayout(user.getId());
    	return toSuccess(layoutList);
    }
    
    /**
     * 获取所有的模板数据
     * @return
     */
    @RequestMapping("getAllTempLayout")
    public JSONObject getAllTempLayout(){
    	 List<HomeLayoutBo> homeLayoutBo=homeLayoutApi.getTemplates();
    	 
		return toSuccess(homeLayoutBo);
    	
    }
    /**
     * 获取用户所有布局模板
     * @return
     */
    @RequestMapping("getTemplateLayout")
    public JSONObject getTemplateLayout(){
        ILoginUser user = getLoginUser();
        List<HomeLayoutBo> homeLayoutBo = homeLayoutApi.getTemplateLayout(user.getId());
        Map<String, Object> result = new HashMap<>();
        result.put("list", homeLayoutBo);
        return toSuccess(result);
    }

    /**
     * 获取指定布局的模块属性配置
     * @param layoutId
     * @return
     */
    @RequestMapping("getLayoutModuleConfig")
    public JSONObject addLayout(long layoutId){
        List<HomeLayoutModuleConfigBo> list = homeLayoutApi.getLayoutModuleConfig(layoutId);
        Map<String, Object> result = new HashMap<>();
        result.put("list", list);
        return toSuccess(result);
    }
    /**
     * 获取指定模板基本信息
     * @param layoutId
     * @return
     */
    @RequestMapping("getLayout")
    public JSONObject getLayout(long layoutId){
    	ILoginUser user = getLoginUser();
    	HomeLayoutBo homeLayoutBo = new HomeLayoutBo();
    	homeLayoutBo.setId(layoutId);
    	HomeLayoutDefaultBo defaultbo=	homeLayoutApi.getLayoutDefault(user.getId());
    	HomeLayoutBo bo=  	homeLayoutApi.getLayout(homeLayoutBo);
    	if(defaultbo!=null){
    		if(defaultbo.getDefaultLayoutId()==bo.getId()){//判断是否是默认模板
    			bo.setTempSet("1");;
    			
    		}
    	}
        Map<String, Object> result = new HashMap<>();
        result.put("bo", bo);
        return toSuccess(result);
    }  
    
    
    /**
     * 新增页面布局
     * @param homeLayoutBo
     * @return
     */
    @RequestMapping("addLayout")
    public JSONObject addLayout(HomeLayoutBo homeLayoutBo){
    	ILoginUser user = getLoginUser();
    	List<HomeLayoutModuleConfigBo> list = null;
 /*   	if(Long.parseLong(homeLayoutBo.getTempId()) > 0){
    		HomeLayoutBo template = homeLayoutApi.getLayoutById(Long.parseLong(homeLayoutBo.getTempId()));
    		if(template != null){
    			homeLayoutBo.setLayout(template.getLayout());
    			list = homeLayoutApi.getLayoutModuleConfig(template.getId());
    		}
    	}*/
    
    	homeLayoutBo.setTempId("1");//需求未设计，暂时设置为空白模板
    	homeLayoutBo.setCreateTime(new Timestamp(System.currentTimeMillis()));
    	homeLayoutBo.setId(homeLayoutSeq.next());
    	homeLayoutBo.setUserId(user.getId());
    	homeLayoutBo.setLayoutType((byte) 1);
    	
    	HomeLayoutDefaultBo homeLayoutDefaultBo = null;
    	if(homeLayoutBo.getTempSet().equals("1")){
    		homeLayoutDefaultBo = new HomeLayoutDefaultBo();
    		homeLayoutDefaultBo.setUserId(user.getId());
    		homeLayoutDefaultBo.setDefaultLayoutId(homeLayoutBo.getId());
    	}
    	homeLayoutApi.addLayout(homeLayoutBo, list, homeLayoutDefaultBo);
    
    	/*Set<IDomain> domains=user.getDomains();	
    	List<HomeLayoutDomainBo> listdomains= new ArrayList<HomeLayoutDomainBo>();
    	if(domains.size()!=0){
    		for(IDomain domain:domains){
    			//domain.getId();
        		HomeLayoutDomainBo domainBo= new HomeLayoutDomainBo();
        		domainBo.setId(this.homeDomainSeq.next());
            	domainBo.setLayoutId(homeLayoutBo.getId());
            	domainBo.setDomainId(domain.getId());
            	domainBo.setCreateTime(new Timestamp(System.currentTimeMillis()));
            	listdomains.add(domainBo);
    		}
        	homeLayoutDomainApi.saveLayoutDomain(listdomains, homeLayoutBo.getId(), user.getId());
    	}*/
    
    	Map<String, Object> result = new HashMap<>();
        result.put("homeLayoutBo", homeLayoutBo);
    	result.put("homeLayoutModuleConfigList", list);
        return toSuccess(result);
    }
    
    /**
     * 修改页面布局基本信息
     * @param homeLayoutBo
     * @param layoutModuleConfigIds
     * @return
     */
    @RequestMapping("updateLayoutBaseInfo")
    public JSONObject updateLayoutBaseInfo(HomeLayoutBo homeLayoutBo){
    	ILoginUser user = getLoginUser();
    	long userId = user.getId();
    	homeLayoutBo.setUserId(userId);
    	HomeLayoutDefaultBo homeLayoutDefaultBo = null;
    	if(homeLayoutBo.getTempSet().equals("1")){
    		homeLayoutDefaultBo = new HomeLayoutDefaultBo();
    		homeLayoutDefaultBo.setDefaultLayoutId(homeLayoutBo.getId());
    		homeLayoutDefaultBo.setUserId(userId);
    	}else{
    		homeLayoutDefaultBo = new HomeLayoutDefaultBo();
    		homeLayoutDefaultBo.setUserId(userId);
    		  HomeLayoutDefaultBo defaultBo= 	homeLayoutApi.getLayoutDefault(userId);
    		   	if(defaultBo!=null){
    			   if(defaultBo.getDefaultLayoutId()==homeLayoutBo.getId()){
    				   return toSuccess("不允许取消唯一的首页");
    			   }
    		   }
    	}
 
    	homeLayoutApi.updateLayoutBaseInfo(homeLayoutBo, homeLayoutDefaultBo);
        return toSuccess("保存成功");
    }
    
    /**
     * 修改页面布局
     * @param homeLayoutBo 页面布局信息
     * @param layoutModuleConfigIds 要删除的模块配置ID
     * @return
     */
    @RequestMapping("updateLayout")
    public JSONObject updateLayout(HomeLayoutBo homeLayoutBo, long[] delModuleConfigIds){
    	ILoginUser user = getLoginUser();
    	long userId = user.getId();
    	
    	//这个做法不妥哈，暂时为了处理 BUG #37848首页：其他用户编辑非本人创建的页面，提示保存成功，但实际未生效
    	HomeLayoutBo hlb = homeLayoutApi.getLayoutById(homeLayoutBo.getId());
    	if(hlb != null){
    		if(hlb.getUserId() != user.getId()){
    			homeLayoutBo.setUserId(hlb.getUserId());
    		}
    	}
    	//end BUG #37848//
    	
    	homeLayoutBo.setUserId(userId);
    	
    	List<HomeLayoutModuleConfigBo> list = null;
    	if(delModuleConfigIds != null && delModuleConfigIds.length > 0){
    		list = new ArrayList<>();
    		for(long item : delModuleConfigIds){
    			HomeLayoutModuleConfigBo homeLayoutModuleConfigBo = new HomeLayoutModuleConfigBo();
    			homeLayoutModuleConfigBo.setId(item);
    			homeLayoutModuleConfigBo.setLayoutId(homeLayoutBo.getId());
    			homeLayoutModuleConfigBo.setUserId(userId);
    		}
    	}
    	homeLayoutApi.updateLayout(homeLayoutBo, list);
        return toSuccess("保存成功");
    }
    
    /**
     * 新增页面模块属性配置
     * @param homeLayoutModuleConfigBo
     * @return
     */
    @RequestMapping("updateHomeLayoutDefault")
    public JSONObject updateHomeLayoutDefault(long layoutId){
    	ILoginUser user = getLoginUser();
    	
    	HomeLayoutDefaultBo homeLayoutDefaultBo = new HomeLayoutDefaultBo();
    	homeLayoutDefaultBo.setDefaultLayoutId(layoutId);
    	homeLayoutDefaultBo.setUserId(user.getId());
    	homeLayoutApi.updateLayoutDefault(homeLayoutDefaultBo);
    	return toSuccess("保存成功");
    }
    
    /**
     * 删除布局
     * @param layoutId
     * @return
     */
    @RequestMapping("deleteLayout")
    public JSONObject deleteLayout(String ids){
    	ILoginUser user = getLoginUser();
    	if(ids == null || ids.equals("")){
			return toSuccess(false);
		}
    	HomeLayoutDefaultBo homeLayoutDefaultBo = homeLayoutApi.getLayoutDefault(user.getId());
		String[] idArray = ids.split(",");
		for(int i = 0 ; i < idArray.length ; i ++){
	    	if(homeLayoutDefaultBo != null && homeLayoutDefaultBo.getDefaultLayoutId() ==Long.parseLong(idArray[i])){
	    		return toSuccess("此页面为首页，不能被删除！");
	    	}
	    	homeLayoutApi.deleteLayout(Long.parseLong(idArray[i]), user.getId());
	    	
		}
	 	return toSuccess("删除成功");
    	
    	
    	

    }
    
    /**
     * 新增页面模块属性配置
     * @param homeLayoutModuleConfigBo
     * @return
     */
    @RequestMapping("addLayoutModuleConfig")
    public JSONObject addLayoutModuleConfig(HomeLayoutModuleConfigBo homeLayoutModuleConfigBo){
    	ILoginUser user = getLoginUser();
    	homeLayoutModuleConfigBo.setUserId(user.getId());
    	homeLayoutModuleConfigBo.setId(homeLayoutModuleConfigSeq.next());
    	homeLayoutApi.addLayoutModuleConfig(homeLayoutModuleConfigBo);
    	Map<String, Object> result = new HashMap<>();
        result.put("homeLayoutModuleConfig", homeLayoutModuleConfigBo);
        return toSuccess(result);
    }
    
    /**
     * 修改页面模块属性配置
     * @param homeLayoutModuleConfigBo
     * @return
     */
    @RequestMapping("updateLayoutModuleConfig")
    public JSONObject updateLayoutModuleConfig(HomeLayoutModuleConfigBo homeLayoutModuleConfigBo){
    	ILoginUser user = getLoginUser();
    	homeLayoutModuleConfigBo.setUserId(user.getId());
    	homeLayoutApi.updateLayoutModuleConfig(homeLayoutModuleConfigBo);
        return toSuccess("保存成功");
    }
    
    /**
     * 复制 页面
     * @param homeLayoutBo
     * @return
     */
    @RequestMapping("copyLayout")
    public JSONObject copyLayout(long layoutId){
    	ILoginUser user = getLoginUser();
    	long userId = user.getId();
    	HomeLayoutBo homeLayoutBo = homeLayoutApi.getLayoutById(layoutId);
    	List<HomeLayoutModuleConfigBo> list = null;
    	Long newid=0l;
    	if(homeLayoutBo != null){
    		list = homeLayoutApi.getLayoutModuleConfig(layoutId);
    		homeLayoutBo.setId(homeLayoutSeq.next());
    		newid=homeLayoutSeq.next();
    		homeLayoutBo.setUserId(userId);
    		homeLayoutBo.setCopyUserId(userId);
    		homeLayoutBo.setName(homeLayoutBo.getName()+"(复制)");
    		homeLayoutBo.setCreateTime(new Timestamp(System.currentTimeMillis()));
    		homeLayoutBo.setLayoutType((byte) 1);
    		if(!CollectionUtils.isEmpty(list)){
    			for(HomeLayoutModuleConfigBo item : list){
    				item.setId(homeLayoutModuleConfigSeq.next());
    				item.setLayoutId(homeLayoutBo.getId());
    				item.setUserId(userId);
    			}
    		}
    		homeLayoutApi.addLayout(homeLayoutBo, list, null);
    	}
    	HomeLayoutDomainBo domainBo= new HomeLayoutDomainBo();
    	domainBo.setLayoutId(layoutId);
    	/*	Set<IDomain> domains=user.getDomains();	
    	List<HomeLayoutDomainBo> listdomains= new ArrayList<HomeLayoutDomainBo>();
    	if(domains.size()!=0){
    		for(IDomain domain:domains){
    			//domain.getId();
        		HomeLayoutDomainBo Bo= new HomeLayoutDomainBo();
        		Bo.setId(this.homeDomainSeq.next());
        		Bo.setLayoutId(homeLayoutBo.getId());
        		Bo.setDomainId(domain.getId());
        		Bo.setCreateTime(new Timestamp(System.currentTimeMillis()));
            	listdomains.add(Bo);
    		}
        	homeLayoutDomainApi.saveLayoutDomain(listdomains, homeLayoutBo.getId(), user.getId());
    	}*/
/*    List<HomeLayoutDomainBo> bos=	homeLayoutDomainApi.getLayoutDomain(domainBo);
    if(bos.size()!=0){
    	homeLayoutDomainApi.copyLayoutDomain(bos, homeLayoutBo.getId(), userId);
    	//homeLayoutDomainApi.
    }*/
    Map<String, Object> result = new HashMap<>();
        result.put("homeLayoutBo", homeLayoutBo);
    	result.put("homeLayoutModuleConfigList", list);
        return toSuccess(result);
    }
    
    /**
     * 应用模板于当前页面
     * @param homeLayoutBo
     * @return
     */
    @RequestMapping("updateCurrentHome")
    public JSONObject updateCurrentHome(long layoutId,long oldLayoutId){
    	ILoginUser user = getLoginUser();
    	long userId = user.getId();
    	HomeLayoutBo homeLayoutBo = homeLayoutApi.getLayoutById(layoutId);
    	List<HomeLayoutModuleConfigBo> list = null;
    	if(homeLayoutBo != null){
    		list = homeLayoutApi.getLayoutModuleConfig(layoutId);
    		homeLayoutBo.setId(homeLayoutSeq.next());
    		homeLayoutBo.setUserId(userId);
    		homeLayoutBo.setCopyUserId(userId);
    		homeLayoutBo.setName(homeLayoutBo.getName()+"(复制)");
    		homeLayoutBo.setCreateTime(new Timestamp(System.currentTimeMillis()));
    		homeLayoutBo.setLayoutType((byte) 1);
    		if(!CollectionUtils.isEmpty(list)){
    			for(HomeLayoutModuleConfigBo item : list){
    				item.setId(homeLayoutModuleConfigSeq.next());
    				item.setLayoutId(homeLayoutBo.getId());
    				item.setUserId(userId);
    			}
    		}
    		homeLayoutApi.addTempLayout(homeLayoutBo, list, null);
    	}
    	//查询新创建的模板id
    	HomeLayoutBo copyHomeLayoutBo = homeLayoutApi.getLayoutById(homeLayoutBo.getId());
    	//查询现有修改模板
    	HomeLayoutBo currHomeLayoutBo = homeLayoutApi.getLayoutById(oldLayoutId);
    	//设置新的layout
    	currHomeLayoutBo.setLayout(copyHomeLayoutBo.getLayout());
    	//保存设置
    	homeLayoutApi.updateLayoutInfo(currHomeLayoutBo);
    	//修改新增布局的layouId
    	HomeLayoutModuleConfigBo hb = new HomeLayoutModuleConfigBo();
    	hb.setLayoutId(copyHomeLayoutBo.getId());
    	hb.setModuleId(currHomeLayoutBo.getId());
    	homeLayoutModuleApi.updateCurrLayoutId(hb);
    	//删除复制的模板布局
    	homeLayoutApi.deleteLayoutOnly(copyHomeLayoutBo.getId(), userId);
    
    	
    	Map<String, Object> result = new HashMap<>();
        result.put("homeLayoutBo", currHomeLayoutBo);
        return toSuccess(result);
    }
}
