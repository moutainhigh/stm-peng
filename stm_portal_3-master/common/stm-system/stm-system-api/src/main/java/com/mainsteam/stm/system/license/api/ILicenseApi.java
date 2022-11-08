package com.mainsteam.stm.system.license.api;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

import com.mainsteam.stm.license.LicenseCheckException;//zw
import com.mainsteam.stm.system.license.bo.LicenseBo;
import com.mainsteam.stm.system.license.bo.PortalModule;
import com.mainsteam.stm.system.license.exception.LicenseNotFoundException;

/**
 * <li>文件名称: ILicenseApi.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2015-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年1月7日
 * @author   ziwenwen
 */
public interface ILicenseApi {
	/**
	 * <pre>
	 * 检查license是否过期
	 * </pre>
	 * @return
	 * @throws FileNotFoundException 
	 */
	LicenseBo checkLicense() throws LicenseNotFoundException;
	
	/**
	 * <pre>
	 * 获取license详情
	 * </pre>
	 * @return
	 * @throws FileNotFoundException 
	 */
	LicenseBo getLicense() throws LicenseNotFoundException;

	/**
	 * <pre>
	 * 获取dat文件
	 * </pre>
	 * @throws FileNotFoundException 
	 */
	FileInputStream getDatFile() throws FileNotFoundException;

	/**
	 * <pre>
	 * 导入license
	 * </pre>
	 */
	void importLicense(MultipartFile file) throws LicenseNotFoundException;
	
	/**
	 * 导入lience
	 * @param file
	 * @throws LicenseNotFoundException
	 */
	
	Boolean importValiaLience(MultipartFile file) throws LicenseNotFoundException;

	String getDatFileName();

	String getLicenseFileName();

	FileInputStream getLicenseFile() throws FileNotFoundException;
	/**
	 * <pre>
	 * 获取拥有license权限的portal模块id
	 * key:模块id
	 * value:模块授权数量
	 * license文件读取错误返回null
	 * </pre>
	 * @return
	 */
	Map<Long,Integer> getAuthorPortal() throws LicenseNotFoundException;
	
	/**
	 * <pre>
	 * 获取portal模块的授权信息
	 * </pre>
	 * @return
	 * @throws LicenseCheckException
	 */
	List<PortalModule> getPortalModules() throws LicenseCheckException;
	
	/**
	 * <pre>
	 * 根据模块类型获取license授权数量
	 * </pre>
	 * @param model
	 * @return
	 * @throws LicenseNotFoundException 
	 */
	int getAuthor(String model) throws LicenseNotFoundException;
	/**
	 * 重新生成indentity.dat文件
	 */
	void getIndentity();
}


