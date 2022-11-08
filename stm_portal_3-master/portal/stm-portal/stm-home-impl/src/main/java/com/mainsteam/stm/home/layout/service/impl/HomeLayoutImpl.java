package com.mainsteam.stm.home.layout.service.impl;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Resource;




import org.springframework.util.CollectionUtils;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.home.layout.api.HomeLayoutApi;
import com.mainsteam.stm.home.layout.bo.HomeLayoutBo;
import com.mainsteam.stm.home.layout.bo.HomeLayoutDefaultBo;
import com.mainsteam.stm.home.layout.bo.HomeLayoutModuleConfigBo;
import com.mainsteam.stm.home.layout.dao.IHomeLayoutDao;
import com.mainsteam.stm.home.layout.dao.IHomeLayoutDefaultDao;
import com.mainsteam.stm.home.layout.dao.IHomeLayoutDomainDao;
import com.mainsteam.stm.home.layout.dao.IHomeLayoutModuleConfigDao;
import com.mainsteam.stm.home.layout.dao.IHomeLayoutSlideDao;
import com.mainsteam.stm.home.layout.vo.HomeLayoutVo;
import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.platform.sequence.service.ISequence;

/**
 * <li>文件名称: HomeLayoutImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since   下午3:03:16
 * @author
 */
public class HomeLayoutImpl implements HomeLayoutApi {
	
	@Resource(name="stm_home_homeLayoutDao")
	private IHomeLayoutDao homeLayoutDao;
	
	@Resource(name="stm_home_homeLayoutDefaultDao")
	private IHomeLayoutDefaultDao homeLayoutDefaultDao;
	
	@Resource(name="stm_home_homeLayoutModuleConfigDao")
	private IHomeLayoutModuleConfigDao homeLayoutModuleConfigDao;
	 @Resource(name="ocProtalHomeLayoutModuleConfigSeq")
	    private ISequence homeLayoutModuleConfigSeq;
	 @Resource(name="stm_home_homeLayoutDomainDao")
 private IHomeLayoutDomainDao homeLayoutDomainDao ;
	 @Resource(name="stm_home_homeLayoutSlideDao")
	 private IHomeLayoutSlideDao homeLayoutSlideDao;
	@Override
	public HomeLayoutBo getUserDefaultLayout(long userId) {
		HomeLayoutDefaultBo homeLayoutDefaultBo = homeLayoutDefaultDao.getByUserId(userId);
		if(homeLayoutDefaultBo != null){
			return homeLayoutDao.getById(homeLayoutDefaultBo.getDefaultLayoutId());
		}
		return null;
	}

	@Override
	public HomeLayoutDefaultBo getLayoutDefault(long userId) {
		return homeLayoutDefaultDao.getByUserId(userId);
	}

	@Override
	public HomeLayoutBo getLayout(HomeLayoutBo homeLayoutBo) {
		return homeLayoutDao.getOne(homeLayoutBo);
	}

	@Override
	public List<HomeLayoutBo> getLayout(long userId) {
		return homeLayoutDao.getByUserId(userId);
	}

	@Override
	public List<HomeLayoutBo> getTemplateLayout(long userId) {
		return homeLayoutDao.getTemplateByUserId(userId);
	}

	@Override
	public HomeLayoutBo getLayoutById(long id) {
		return homeLayoutDao.getById(id);
	}

	@Override
	public void addLayout(HomeLayoutBo homeLayoutBo, List<HomeLayoutModuleConfigBo> list, HomeLayoutDefaultBo homeLayoutDefaultBo) {
	int i=	homeLayoutDao.insert(homeLayoutBo);

	List<HomeLayoutModuleConfigBo> listtemp=new ArrayList<HomeLayoutModuleConfigBo>();
		if(!CollectionUtils.isEmpty(list)){
			for(HomeLayoutModuleConfigBo item : list){
				HomeLayoutModuleConfigBo bo= new HomeLayoutModuleConfigBo();
				bo.setId(homeLayoutModuleConfigSeq.next());
				bo.setLayoutId(homeLayoutBo.getId());
				bo.setModuleCode(item.getModuleCode());
				bo.setModuleId(item.getModuleId());
				bo.setProps(item.getProps());
				bo.setUserId(item.getUserId());
				listtemp.add(bo);
				homeLayoutModuleConfigDao.insert(bo);
			//	homeLayoutModuleConfigDao.insert(item);
			}
			
			List<HomeLayoutModuleConfigBo>	 bos=	homeLayoutModuleConfigDao.getByLayoutId(listtemp.get(0).getLayoutId());
			 updateLayout(listtemp.get(0).getLayoutId(),bos,homeLayoutBo.getTempId());
	/*		if(bos.size()!=0){
				System.out.println(bos);
			HomeLayoutBo bo=	homeLayoutDao.getById(list.get(0).getLayoutId());
		String layout=	bo.getLayout();
		JSONObject obj= JSONObject.parseObject(layout);
			JSONArray widgets=obj.getJSONArray("widgets");
			JSONObject  widgetObj= new JSONObject();
			for (int i = 0; i < widgets.size(); i++) {
			 widgetObj=widgets.getJSONObject(i);
				System.out.println(bos.get(i).getId()+"====");
				widgetObj.put("id", bos.get(i).getId());
			
				}
	
			HomeLayoutBo  layoutBo= new HomeLayoutBo();
		String newLayout=JSON.toJSONString(obj);
		layoutBo.setLayout(newLayout);
		layoutBo.setId(list.get(0).getLayoutId());
		homeLayoutDao.updateLayoutInfo(layoutBo);
		}*/
		}
	
		if(homeLayoutDefaultBo != null){
			homeLayoutDefaultDao.delete(homeLayoutDefaultBo.getUserId());
			homeLayoutDefaultBo.setDefaultLayoutId(homeLayoutBo.getId());
			homeLayoutDefaultDao.insert(homeLayoutDefaultBo);
		}

	}
	
	@Override
	public void addTempLayout(HomeLayoutBo homeLayoutBo, List<HomeLayoutModuleConfigBo> list, HomeLayoutDefaultBo homeLayoutDefaultBo) {
		int i=	homeLayoutDao.insert(homeLayoutBo);

		List<HomeLayoutModuleConfigBo> listtemp=new ArrayList<HomeLayoutModuleConfigBo>();
			if(!CollectionUtils.isEmpty(list)){
				for(HomeLayoutModuleConfigBo item : list){
					HomeLayoutModuleConfigBo bo= new HomeLayoutModuleConfigBo();
					bo.setId(homeLayoutModuleConfigSeq.next());
					bo.setLayoutId(homeLayoutBo.getId());
					bo.setModuleCode(item.getModuleCode());
					bo.setModuleId(item.getModuleId());
					bo.setProps(item.getProps());
					bo.setUserId(item.getUserId());
					listtemp.add(bo);
					homeLayoutModuleConfigDao.insert(bo);
				//	homeLayoutModuleConfigDao.insert(item);
				}
				
				List<HomeLayoutModuleConfigBo>	 bos=	homeLayoutModuleConfigDao.getByLayoutId(listtemp.get(0).getLayoutId());
				 updateLayout(listtemp.get(0).getLayoutId(),bos,homeLayoutBo.getTempId());
		/*		if(bos.size()!=0){
					System.out.println(bos);
				HomeLayoutBo bo=	homeLayoutDao.getById(list.get(0).getLayoutId());
			String layout=	bo.getLayout();
			JSONObject obj= JSONObject.parseObject(layout);
				JSONArray widgets=obj.getJSONArray("widgets");
				JSONObject  widgetObj= new JSONObject();
				for (int i = 0; i < widgets.size(); i++) {
				 widgetObj=widgets.getJSONObject(i);
					System.out.println(bos.get(i).getId()+"====");
					widgetObj.put("id", bos.get(i).getId());
				
					}
		
				HomeLayoutBo  layoutBo= new HomeLayoutBo();
			String newLayout=JSON.toJSONString(obj);
			layoutBo.setLayout(newLayout);
			layoutBo.setId(list.get(0).getLayoutId());
			homeLayoutDao.updateLayoutInfo(layoutBo);
			}*/
			}
		
			if(homeLayoutDefaultBo != null){
				homeLayoutDefaultDao.delete(homeLayoutDefaultBo.getUserId());
				homeLayoutDefaultBo.setDefaultLayoutId(homeLayoutBo.getId());
				homeLayoutDefaultDao.insert(homeLayoutDefaultBo);
			}

	}
	

	@Override
	public void addLayoutModuleConfig(HomeLayoutModuleConfigBo homeLayoutModuleConfigBo) {
		homeLayoutModuleConfigDao.insert(homeLayoutModuleConfigBo);
	}

	@Override
	public void updateLayoutBaseInfo(HomeLayoutBo homeLayoutBo, HomeLayoutDefaultBo homeLayoutDefaultBo) {
	//	HomeLayoutBo layoutBo=	homeLayoutDao.getById(Long.parseLong(homeLayoutBo.getTempId()));
	//	HomeLayoutBo layoutSelfBo=	homeLayoutDao.getById(homeLayoutBo.getId());
		
		/*if(homeLayoutBo.getTempId().equals("1")){
			homeLayoutBo.setLayout(layoutSelfBo.getLayout());
		}else{
			homeLayoutBo.setLayout(layoutBo.getLayout());
		}*/

	//	homeLayoutBo.setChooeseId(Long.parseLong(homeLayoutBo.getTempId()));
		homeLayoutDao.updateBaseInfo(homeLayoutBo);
/*		System.out.println("layout: "+homeLayoutBo.getId());
		List<HomeLayoutModuleConfigBo> configBos=homeLayoutModuleConfigDao.getByLayoutId(homeLayoutBo.getId());
		*//**
		 * 根据tempid查找到更改的模板数据，查找相应的config数据，插入数据，变更id再更新主表的layout字段
		 *//*
		System.out.println("====");
System.out.println(homeLayoutBo.getTempId());
		if(!CollectionUtils.isEmpty(configBos)){
			List<HomeLayoutModuleConfigBo> tempConfigBos=new ArrayList<HomeLayoutModuleConfigBo>();
			if(homeLayoutBo.getTempId().equals("1")){//空白模板不清空数据，以免数据丢失
				for(HomeLayoutModuleConfigBo item : configBos){
					homeLayoutModuleConfigDao.delete(item);
				}
				tempConfigBos=	homeLayoutModuleConfigDao.getByLayoutId(homeLayoutBo.getId());//修改改模板的layout字段数据
			}else{//非空白模板先清空所有的，再根据选择的模板进行数据切换
				for(HomeLayoutModuleConfigBo item : configBos){
					homeLayoutModuleConfigDao.delete(item);
				}
				 tempConfigBos=	homeLayoutModuleConfigDao.getByLayoutId(Long.parseLong(homeLayoutBo.getTempId()));//切换选择的模板原始模块数据
			}
			
			
			if(!CollectionUtils.isEmpty(tempConfigBos)){
				for(HomeLayoutModuleConfigBo item : tempConfigBos){
					HomeLayoutModuleConfigBo bo= new HomeLayoutModuleConfigBo();
					bo.setId(homeLayoutModuleConfigSeq.next());
					bo.setLayoutId(homeLayoutBo.getId());
					bo.setModuleCode(item.getModuleCode());
					bo.setModuleId(item.getModuleId());
					bo.setProps(item.getProps());
					bo.setUserId(item.getUserId());
					homeLayoutModuleConfigDao.insert(bo);
				}
			}
			List<HomeLayoutModuleConfigBo> currentConfigBos=homeLayoutModuleConfigDao.getByLayoutId(homeLayoutBo.getId());
			updateLayout(homeLayoutBo.getId(),currentConfigBos,homeLayoutBo.getTempId());
			
		}*/
	

		if(homeLayoutDefaultBo != null){
			homeLayoutDefaultDao.delete(homeLayoutDefaultBo.getUserId());
			homeLayoutDefaultDao.insert(homeLayoutDefaultBo);
		}else{
			homeLayoutDefaultDao.delete(homeLayoutDefaultBo.getUserId());
		}
	}
public void updateLayout(long id,List<HomeLayoutModuleConfigBo> configBos,String tempid){
	if(configBos.size()!=0){
	
		HomeLayoutBo bo=	homeLayoutDao.getById(id);
		List<HomeLayoutModuleConfigBo> bos=	homeLayoutModuleConfigDao.getByLayoutId(id);
		
		if(bos.size()!=0){
			String layout=	bo.getLayout();
			
			JSONObject obj= JSONObject.parseObject(layout);
			JSONArray widgets=obj.getJSONArray("widgets");
	
			JSONObject  widgetObj= new JSONObject();
			for (int i = 0; i < widgets.size(); i++) {
				widgetObj=widgets.getJSONObject(i);
				widgetObj.put("id", configBos.get(i).getId());
			}
			HomeLayoutBo  layoutBo= new HomeLayoutBo();
			String newLayout=JSON.toJSONString(obj);
			layoutBo.setLayout(newLayout);
			layoutBo.setId(id);
			homeLayoutDao.updateLayoutInfo(layoutBo);	
		}

	}
	
}
	@Override
	public void updateLayout(HomeLayoutBo homeLayoutBo, List<HomeLayoutModuleConfigBo> list) {
		homeLayoutDao.updateLayout(homeLayoutBo);
		if(!CollectionUtils.isEmpty(list)){
			for(HomeLayoutModuleConfigBo item : list){
				homeLayoutModuleConfigDao.delete(item);
			}
		}
	}
	
	@Override
	public List<HomeLayoutModuleConfigBo> getLayoutModuleConfig(long layoutId) {
		return homeLayoutModuleConfigDao.getByLayoutId(layoutId);
	}

	@Override
	public void updateLayoutModuleConfig(HomeLayoutModuleConfigBo homeLayoutModuleConfigBo) {
		homeLayoutModuleConfigDao.updateProps(homeLayoutModuleConfigBo);
	}

	@Override
	public void updateLayoutDefault(HomeLayoutDefaultBo homeLayoutDefaultBo) {
		homeLayoutDefaultDao.delete(homeLayoutDefaultBo.getUserId());
		homeLayoutDefaultDao.insert(homeLayoutDefaultBo);
	}

	@Override
	public void deleteLayout(long layoutId, long userId) {
		homeLayoutDao.delete(layoutId, userId);
		List<HomeLayoutModuleConfigBo> list=	homeLayoutModuleConfigDao.getByLayoutId(layoutId);
		if(list!=null){
			for (int i = 0; i < list.size(); i++) {
				homeLayoutModuleConfigDao.delete(list.get(i));
				
			}
		}
		homeLayoutSlideDao.deleteByLyoutId(layoutId);
		homeLayoutDomainDao.delete(userId, layoutId);
		
	}

	@Override
	public List<HomeLayoutBo> getTemplates() {
		// TODO Auto-generated method stub
		return homeLayoutDao.getTemplates();
	}

	@Override
	public void selectByPage(Page<HomeLayoutBo, HomeLayoutVo> page) {
		homeLayoutDao.selectByPage(page);		
	}

	@Override
	public void updateLayoutInfo(HomeLayoutBo homeLayoutBo) {
		homeLayoutDao.updateLayout(homeLayoutBo);
		
	}

	@Override
	public List<HomeLayoutBo> getTempsById(HomeLayoutVo layoutVo) {
		// TODO Auto-generated method stub
		return homeLayoutDao.getTempsById(layoutVo);
	}

	@Override
	public void deleteLayoutOnly(long layoutId, long userId) {
		homeLayoutDao.delete(layoutId, userId);
	}
}
