package com.mainsteam.stm.system.license.api;

import java.io.FileNotFoundException;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.license.License;
import com.mainsteam.stm.license.LicenseCheckException;//zw
import com.mainsteam.stm.system.license.exception.LicenseNotFoundException;

/**
 * <li>文件名称: ILicenseRemainCountApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2015-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年1月7日
 * @author   ziwenwen
 */
//malachi lic
public abstract class ILicenseRemainCountApi {
	private  final Log logger = LogFactory.getLog(ILicenseRemainCountApi.class);
	/**
	 * <pre>
	 * 获取还剩余的可监控的数量
	 * </pre>
	 * @return
	 * @throws LicenseNotFoundException 
	 * @throws FileNotFoundException 
	 */
	public int getRemain(){
		int remain = 0;
		try {
			remain = License.checkLicense().checkModelAvailableNum(this.getType())-this.getUsed();
		} catch (LicenseCheckException e) {
			remain = 0;
			logger.error("ILicenseRemainCountApi getRemain:", e);
		}
		return remain;
	}
	
	/**
	 * <pre>
	 * 获取模块类型
	 * </pre>
	 * @return
	 */
	public abstract String getType();
	
	/**
	 * <pre>
	 * 获取已使用的已监控的数量
	 * </pre>
	 * @return
	 */
	public abstract int getUsed();
	
}


