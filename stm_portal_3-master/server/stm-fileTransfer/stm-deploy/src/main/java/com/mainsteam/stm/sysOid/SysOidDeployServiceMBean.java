package com.mainsteam.stm.sysOid;

import com.mainsteam.stm.caplib.dict.CaplibAPIResult;
import com.mainsteam.stm.sysOid.obj.ModuleBo;

public interface SysOidDeployServiceMBean {

	public CaplibAPIResult SysOidReload(ModuleBo moduleBo);
}
