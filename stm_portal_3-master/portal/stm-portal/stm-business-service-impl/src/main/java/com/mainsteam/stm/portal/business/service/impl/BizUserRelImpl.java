package com.mainsteam.stm.portal.business.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;

import com.mainsteam.stm.portal.business.api.BizUserRelApi;
import com.mainsteam.stm.portal.business.bo.BizMainBo;
import com.mainsteam.stm.portal.business.bo.BizUserRelBo;
import com.mainsteam.stm.portal.business.dao.IBizUserRelDao;

public class BizUserRelImpl implements BizUserRelApi {
	@Autowired
	private IBizUserRelDao ibizUserRelDao;

	@Override
	public List<BizUserRelBo> getUserlistByBizId(long bizID, String name, long domainId) {
		// TODO Auto-generated method stub
		return ibizUserRelDao.getUserlistByBizId(bizID, name, domainId);
	}

	@Override
	public List<BizMainBo> getBizlistByUserId(long user_id) {
		// TODO Auto-generated method stub
		return ibizUserRelDao.getBizlistByUserId(user_id);
	}

	@Override
	public boolean checkUserView(long user_id, long biz_id) {
		// TODO Auto-generated method stub
		List<BizUserRelBo> list = new ArrayList<>();
		list = ibizUserRelDao.checkUserView(user_id, biz_id);
		if (list!=null && list.size()>0) {
			return false;
		}else{
			return true;
		}
	}

	@Override
	public String deleteBizById(List<Long> biz_ids, List<Long> user_ids) {
		// TODO Auto-generated method stub
		if (ibizUserRelDao.deleteByBizId(biz_ids,user_ids)>0) {
			return "success";
		}else {
			return "error or delete none";
		}
	}

	@Override
	public boolean update(long biz_id, List<BizUserRelBo> list, List<Long> user_ids) {
		// TODO Auto-generated method stub
		int num = list.size();
		List<Long> biz_idList = new ArrayList<>();
		biz_idList.add(biz_id);
        ibizUserRelDao.deleteByBizId(biz_idList,user_ids);
        try {
            for(BizUserRelBo bb : list){
                if(bb.getView() == 0){
                    ibizUserRelDao.insertSet(bb);
                }
            }
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
	}
}
