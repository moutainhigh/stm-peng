package com.mainsteam.stm.system.license.web.action;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.license.calc.api.ILicenseCalcService;
import com.mainsteam.stm.platform.web.action.BaseAction;
import com.mainsteam.stm.platform.web.util.WebUtil;
import com.mainsteam.stm.system.license.api.ILicenseApi;
import com.mainsteam.stm.system.license.bo.LicenseBo;
import com.mainsteam.stm.system.license.bo.LicenseUseInfoBo;
import com.mainsteam.stm.system.license.exception.LicenseNotFoundException;

/**
 * <li>文件名称: LicenseAction.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2015-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年1月9日
 * @author   ziwenwen
 */
@Controller("systemLicenseAction")
@RequestMapping("/system/license/")
public class LicenseAction extends BaseAction {
	
	@Autowired
	ILicenseApi licenseApi;
	
	@Resource
	private ILicenseCalcService licenseCalcService;

	/**
	 * <pre>
	 * 检查license是否过期
	 * </pre>
	 * @return
	 * @throws LicenseNotFoundException 
	 */
	@RequestMapping("checkLicense")
	public JSONObject checkLicense() throws LicenseNotFoundException{
		
		
		return toSuccess(licenseApi.checkLicense());
	};
	
	/**
	 * <pre>
	 * 获取license详情
	 * </pre>
	 * @return
	 * @throws LicenseNotFoundException 
	 */
	@RequestMapping("getLicense")
	public JSONObject getLicense() throws LicenseNotFoundException{
		List<LicenseUseInfoBo> licenseUseInfoBos = licenseCalcService.getAllLicenseUseInfo();
		LicenseBo licenseBo = licenseApi.getLicense();
		licenseBo.setLicenseUseInfoBo(licenseUseInfoBos);
		return toSuccess(licenseBo);
	}

	/**
	 * <pre>
	 * 获取dat文件
	 * </pre>
	 * @throws FileNotFoundException 
	 * @throws IOException 
	 * @throws LicenseNotFoundException 
	 */
	@RequestMapping("getDatFile")
	public void getDatFile(HttpServletRequest request,HttpServletResponse response) throws FileNotFoundException, IOException{
		//新版取消dat
		//licenseApi.getIndentity();//生成identity.dat文件
		WebUtil.download(licenseApi.getLicenseFileName(), licenseApi.getLicenseFile(), request, response);
	}

	/**
	 * <pre>
	 * 导入license
	 * </pre>
	 * @throws LicenseNotFoundException 
	 */
	@RequestMapping("importLicense")
	public JSONObject importLicense(@RequestParam("licenseFile")MultipartFile file) throws LicenseNotFoundException{
		licenseApi.importLicense(file);
		return toSuccess(1);
	}

	/**
	 * <pre>
	 * 导入license
	 * </pre>
	 * @throws LicenseNotFoundException 
	 */
	@RequestMapping("getAuthor")
	public JSONObject getAuthor(String modelType) throws LicenseNotFoundException{
		return toSuccess(licenseApi.getAuthor(modelType));
	}
	
	/**
	  * <pre>
	 * 验证导入license   重新注册
	 * </pre>
	 * @throws LicenseNotFoundException 
	 */
	@RequestMapping("importLicenseV")
	public @ResponseBody JSONObject importValiaLicense(@RequestParam("licenseFile")MultipartFile file) throws LicenseNotFoundException{
		  if(file.isEmpty()){
			  return toSuccess(0);   
	      }else{
			Boolean bl=licenseApi.importValiaLience(file);
			if(bl==true){
				return toSuccess(1);
			}
			 return toSuccess(0);    
	      }
	
	}	
	
}


