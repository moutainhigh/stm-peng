package com.mainsteam.stm.portal.business.service.impl;

import java.util.ArrayList;
import java.util.List;

import com.mainsteam.stm.instancelib.obj.ResourceInstance;
import com.mainsteam.stm.platform.sequence.service.ISequence;
import com.mainsteam.stm.portal.business.api.BizCapMetricApi;
import com.mainsteam.stm.portal.business.bo.BizCapMetricBo;
import com.mainsteam.stm.portal.business.bo.BizCapMetricDataBo;
import com.mainsteam.stm.portal.business.dao.IBizCapMetricDao;
import com.mainsteam.stm.portal.business.service.util.BizMetricDefine;

public class BizCapMetricImp implements BizCapMetricApi {
private IBizCapMetricDao bizCapMetricDao;
private ISequence seq;
	@Override
	public List<BizCapMetricBo> getAllByBizIdAndMetric(long bizid, int type) {
		String name=BizMetricDefine.HOST_CAPACITY;//主机
		if(type==0){
			name=BizMetricDefine.HOST_CAPACITY;
		}else if(type==1){//存储容量
			name=BizMetricDefine.STORAGE_CAPACITY;
		}else if(type==2){//数据库容量
			name=BizMetricDefine.DATABASE_CAPACITY;
		}else if(type==3){//带宽容量
			name=BizMetricDefine.BANDWIDTH_CAPACITY;
		}
		List<BizCapMetricBo> list=bizCapMetricDao.getAllByBizIdAndMetric(bizid, name);
		
		return list;
	}
	
	@Override
	public List<BizCapMetricBo> getAllByBizIdAndMetric(long bizid, String type) {
		List<BizCapMetricBo> list=bizCapMetricDao.getAllByBizIdAndMetric(bizid, type);
		
		return list;
	}
	public IBizCapMetricDao getBizCapMetricDao() {
		return bizCapMetricDao;
	}
	public void setBizCapMetricDao(IBizCapMetricDao bizCapMetricDao) {
		this.bizCapMetricDao = bizCapMetricDao;
	}
	public ISequence getSeq() {
		return seq;
	}
	public void setSeq(ISequence seq) {
		this.seq = seq;
	}
	@Override
	public List<Long> getInfoByBizIdAndMetric(long bizid,int type) {
		// TODO Auto-generated method stub
		String name=BizMetricDefine.HOST_CAPACITY;//主机
		if(type==0){
			name=BizMetricDefine.HOST_CAPACITY;
		}else if(type==1){//存储容量
			name=BizMetricDefine.STORAGE_CAPACITY;
		}else if(type==2){//数据库容量
			name=BizMetricDefine.DATABASE_CAPACITY;
		}else if(type==3){//带宽容量
			name=BizMetricDefine.BANDWIDTH_CAPACITY;
		}else if(type==4){
			name=BizMetricDefine.RESPONSE_TIME;
		}
		List<Long> list=bizCapMetricDao.getInfoByBizIdAndMetric(bizid, name);
		return list;
	}
	@Override
	public int insertInfo(BizCapMetricBo infoBo) {
		// TODO Auto-generated method stub
		infoBo.setId(this.seq.next());
		return bizCapMetricDao.insertInfo(infoBo);
	}
	@Override
	public int deleteInfo(List<Long> id) {
		// TODO Auto-generated method stub
		return bizCapMetricDao.deleteInfo(id);
	}

	@Override
	public int deleteByInfo(long bizid, String metric) {
		// TODO Auto-generated method stub
		return bizCapMetricDao.deleteByInfo(bizid, metric);
	}

	@Override
	public List<Long> getByBizIdAndMetric(long bizid, String type) {
		// TODO Auto-generated method stub
		List<Long> list = bizCapMetricDao.getInfoByBizIdAndMetric(bizid, type);
		return list;
	}

	@Override
	public List<BizCapMetricDataBo> getData(List<ResourceInstance> instances) {
		List<BizCapMetricDataBo> vos= new ArrayList<BizCapMetricDataBo>();
		List<BizCapMetricDataBo> childrens=new ArrayList<BizCapMetricDataBo>();
		BizCapMetricDataBo parentVo= new BizCapMetricDataBo();
		List<Long> idsTemp=new ArrayList<Long>();
		//有资源
		for (int j=0;j<instances.size();j++) {
			ResourceInstance parentInstance	= instances.get(j).getParentInstance();
			if(instances.get(j).getParentId()!=0){//子资源
				if(vos.size()!=0){
					for (int i = 0; i < vos.size(); i++) {
						if(parentInstance.getId()!=Long.parseLong(vos.get(i).getId()) && parentInstance.getParentId()==0 && idsTemp.contains(parentInstance.getId())==false){//new
						childrens=new ArrayList<BizCapMetricDataBo>();
							parentVo= new BizCapMetricDataBo();
							parentVo.setId(String.valueOf(parentInstance.getId()));
							parentVo.setName(parentInstance.getName());
							parentVo.setPId(String.valueOf(parentInstance.getParentId()));
							parentVo.setIsParent(true);
							BizCapMetricDataBo children = new BizCapMetricDataBo();
							children.setId(String.valueOf(instances.get(j).getId()));
							children.setName(instances.get(j).getShowName()==null?instances.get(j).getName():instances.get(j).getShowName());
							children.setPId(String.valueOf(instances.get(j).getParentId()));
							children.setIsParent(false);
							childrens.add(children);//装载子资源
							parentVo.setChildren(childrens);
							vos.add(parentVo);
							
							idsTemp.add(parentInstance.getId());
						}else{//原有
							
							List<BizCapMetricDataBo> childs= new ArrayList<BizCapMetricDataBo>();
							for (int k = 0; k < vos.size(); k++) {
								if(instances.get(j).getParentId()==Long.parseLong(vos.get(k).getId())){
									childs=vos.get(k).getChildren();
								}
							}
							BizCapMetricDataBo children = new BizCapMetricDataBo();
							children.setId(String.valueOf(instances.get(j).getId()));
							children.setName(instances.get(j).getShowName()==null?instances.get(j).getName():instances.get(j).getShowName());
							children.setPId(String.valueOf(instances.get(j).getParentId()));
							children.setIsParent(false);
							//childrens.add(children);//装载子资源
							childs.add(children);
						}
						break;
					}
				}else{
					 childrens=new ArrayList<BizCapMetricDataBo>();
					parentVo= new BizCapMetricDataBo();
					parentVo.setId(String.valueOf(parentInstance.getId()));
					parentVo.setName(parentInstance.getShowName()==null?parentInstance.getName():parentInstance.getShowName());
					parentVo.setPId(String.valueOf(parentInstance.getParentId()));
					parentVo.setIsParent(true);
					BizCapMetricDataBo children = new BizCapMetricDataBo();
					children.setId(String.valueOf(instances.get(j).getId()));
					children.setName(instances.get(j).getShowName()==null?instances.get(j).getName():instances.get(j).getShowName());
					children.setPId(String.valueOf(instances.get(j).getParentId()));
					children.setIsParent(false);
					childrens.add(children);//装载子资源
					parentVo.setChildren(childrens);
					vos.add(parentVo);	
					idsTemp.add(parentInstance.getId());
					
				}
				
				
			}else{//主资源
				BizCapMetricDataBo vo = new  BizCapMetricDataBo();
				vo.setId(String.valueOf(instances.get(j).getId()));
				vo.setName(instances.get(j).getShowName()==null?instances.get(j).getName():instances.get(j).getShowName());
				vo.setPId(String.valueOf(instances.get(j).getParentId()));
				vo.setIsParent(true);
				vos.add(vo);
			}
		}
		

		return vos;
	}

}
