package com.mainteam.stm.portal.business.api;

import com.mainsteam.stm.platform.web.vo.ILoginUser;
import com.mainsteam.stm.portal.business.bo.BizMainNewBo;

public interface BizMainNewApi {


    public BizMainNewBo getDefaultView(ILoginUser user);



}
