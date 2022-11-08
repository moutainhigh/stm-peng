package com.mainsteam.stm.system.ssoforthird.api;

import java.util.List;

import com.mainsteam.stm.platform.mybatis.plugin.pagination.Page;
import com.mainsteam.stm.system.ssoforthird.bo.SSOForThirdBo;

public interface ISSOForThirdApi {
	/**
	 * 保存第三方系统信息
	 * @param ssoForThirdBo
	 * @return
	 */
	int saveSSOForThird(SSOForThirdBo ssoForThirdBo);
	
	/**
	 * 查询第三方系统信息
	 * @param page
	 * @return
	 */
	List<SSOForThirdBo> querySSOForThird(Page<SSOForThirdBo, SSOForThirdBo> page);
	
	/**
	 * 查询所有的信息
	 * @return
	 */
	List<SSOForThirdBo> queryAllSSOForThird();
	
	/**
	 * 批量删除
	 * @param ids
	 * @return
	 */
	int batchDel(Long[] ids);
	
	/**
	 * 根据ID得到基本信息
	 * @param id
	 * @return
	 */
	SSOForThirdBo getSSOForThirdById(Long id);
	
	/**
	 * 更新基本信息
	 * @param ssoForThirdBo
	 * @return
	 */
	int updateSSOForThird(SSOForThirdBo ssoForThirdBo);
	
	/**
	 * 更新是开启还是关闭
	 * @return
	 */
	int updateSSOForThirdStartState(Long[] ids, int startState);
	
	/**
	 * 判断wsdlURL是否存在
	 * @param ssoForThirdBo
	 * @return
	 */
	boolean isWsdlURLExist(SSOForThirdBo ssoForThirdBo);
	
	
	
	int updateState4Right(Long[] ids, int startState);
}
