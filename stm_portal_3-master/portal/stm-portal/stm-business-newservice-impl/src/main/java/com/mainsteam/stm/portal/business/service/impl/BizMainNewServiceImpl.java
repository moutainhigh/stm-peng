package com.mainsteam.stm.portal.business.service.impl;

import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.business.bo.BizMainNewBo;
import com.mainsteam.stm.portal.business.dao.IBizMainNewDao;
import com.mainteam.stm.portal.business.api.BizMainNewApi;
import org.springframework.beans.factory.annotation.Autowired;

public class BizMainNewServiceImpl implements BizMainNewApi {

    @Autowired
    private IBizMainNewDao bizMainNewDao;

    @Override
    public BizMainNewBo getDefaultView(ILoginUser user) {
        return null;
    }


}
