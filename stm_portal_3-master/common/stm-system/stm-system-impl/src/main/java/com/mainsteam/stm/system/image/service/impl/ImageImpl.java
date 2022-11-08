package com.mainsteam.stm.system.image.service.impl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.util.StringUtils;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.platform.system.config.bean.SystemConfigBo;
import com.mainsteam.stm.platform.system.config.bean.SystemConfigConstantEnum;
import com.mainsteam.stm.platform.system.config.service.ISystemConfigApi;
import com.mainsteam.stm.system.image.api.IImageApi;
import com.mainsteam.stm.system.image.bo.ImageBo;

/**
 * <li>文件名称: IImageImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年8月6日
 * @author   ziwenwen
 */
public class ImageImpl implements IImageApi {
	
	private static final long IMG_CONFIG_ID = SystemConfigConstantEnum.SYSTEM_CONFIG_SYSTEMIMAGE_CFG.getCfgId();
	
	@Autowired
	@Qualifier("systemConfigApi")
	private ISystemConfigApi systemConfigApi;
	
	@Override
	public ImageBo get() {
		SystemConfigBo configBo = systemConfigApi.getSystemConfigById(IMG_CONFIG_ID);
		ImageBo imageBo = new ImageBo();
		if(null!=configBo){
			String content = configBo.getContent();
			if(content!=null && !content.isEmpty()){
				imageBo = JSONObject.parseObject(content,ImageBo.class); 
				imageBo.setLogoPsdName("STM-System-Logo-psd.zip");
			}
		}else{
			configBo = new SystemConfigBo();
			configBo.setId(IMG_CONFIG_ID);
			imageBo = setDefaultImage(imageBo);
			configBo.setContent(JSONObject.toJSONString(imageBo));
			configBo.setDescription("系统图片管理配置文件管理");
			systemConfigApi.insertSystemConfig(configBo);
			configBo = systemConfigApi.getSystemConfigById(IMG_CONFIG_ID);
		}
		return imageBo;
	}

	@Override
	public boolean update(ImageBo image) {
		ImageBo imageBo = this.get();
		Boolean isAdd = false;
		if(imageBo==null){
			imageBo = new ImageBo();
			isAdd = true;
		}
		image.setLogoPsdName(imageBo.getLogoPsdName());
		BeanUtils.copyProperties(image, imageBo);
		SystemConfigBo configBo = new SystemConfigBo();
		configBo.setId(IMG_CONFIG_ID);
		configBo.setDescription("系统图片管理配置文件管理");
		int result = 0;
		if(isAdd){
			imageBo = setDefaultImage(imageBo);
			configBo.setContent(JSONObject.toJSONString(imageBo));
			result = systemConfigApi.insertSystemConfig(configBo);
		}else {
			configBo.setContent(JSONObject.toJSONString(imageBo));
			result = systemConfigApi.updateSystemConfig(configBo);
		}
		return result>0?true:false;
	}
	
	private ImageBo setDefaultImage(ImageBo image) {
		if(image==null)image = new ImageBo();
		image.setLoginDefaultLogo("resource/themes/blue/images/comm/table/login-logo.png");
		image.setSystemDefaultLogo("resource/themes/blue/images/logo.png");
		image.setDefaultCopyRight("Copyright &copy; 2009-2020 MainSteam Technologies Co., Ltd.All rights reserved.");
		image.setLogoPsdName("STM-System-Logo-psd.zip");
		return image;
	} 

	@Override
	public String getSystemLogo() {
		ImageBo image = this.get();
		if(StringUtils.isEmpty(image.getSystemCurrentLogo())){
			return image.getSystemDefaultLogo();
		}else{
			return "platform/file/getFileInputStream.htm?fileId="+image.getSystemCurrentLogo();
		}
	}

	@Override
	public String getLoginLogo() {
		ImageBo image = this.get();
		if(StringUtils.isEmpty(image.getLoginCurrentLogo())){
			return image.getLoginDefaultLogo();
		}else{
			return "system/image/getLoginLofiInputStream.htm";
		}
	}

	@Override
	public String getCopyright() {
		ImageBo image = this.get();
		if(StringUtils.isEmpty(image.getCurrentCopyRight())){
			return image.getDefaultCopyRight();
		}else{
			return image.getCurrentCopyRight();
		}
	}

	@Override
	public String getLogoPsd() {
		ImageBo image = this.get();
		if(image!=null){
			return image.getLogoPsdName();
		}
		return "";
	}
	
}


