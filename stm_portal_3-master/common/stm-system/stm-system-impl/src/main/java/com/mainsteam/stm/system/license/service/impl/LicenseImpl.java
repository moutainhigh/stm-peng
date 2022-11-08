package com.mainsteam.stm.system.license.service.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.mainsteam.stm.license.License;
import com.mainsteam.stm.license.LicenseCheckException;//zw
import com.mainsteam.stm.license.LicenseModelEnum;
import com.mainsteam.stm.system.image.api.IImageApi;
import com.mainsteam.stm.system.license.api.ILicenseApi;
import com.mainsteam.stm.system.license.api.ILicenseRemainCountApi;
import com.mainsteam.stm.system.license.bo.Collector;
import com.mainsteam.stm.system.license.bo.LicenseBo;
import com.mainsteam.stm.system.license.bo.MonitorModule;
import com.mainsteam.stm.system.license.bo.PortalModule;
import com.mainsteam.stm.system.license.exception.LicenseNotFoundException;
import com.mainsteam.stm.system.um.right.api.IRightApi;
import com.mainsteam.stm.system.um.right.bo.Right;
import com.mainsteam.stm.util.FileUtil;
/**
 * <li>文件名称: LicenseImpl.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2015-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年1月7日
 * @author   ziwenwen
 */
@Service
public class LicenseImpl implements ILicenseApi, ApplicationContextAware {
	private  final Log logger = LogFactory.getLog(LicenseImpl.class);
	private ApplicationContext context;
	
	private Map<String,ILicenseRemainCountApi> remains;
	
	@Autowired
	private IRightApi rightApi;
	
	@Autowired
	private IImageApi imageApi;
	
	private final String datFileName="identity.dat";
	//标示数据库文件生成文件名
	private final String datFileGenerateFile = "STMReader.exe";
	
	//license验证目录名
	private final String licenseValidateDirName = "temp";

	/**
	 * 提前提醒时间
	 */
	@Value("${stm.license.tiptime}")
	private int tiptime;
	
	/**
	 * 产品名称
	 */
	@Value("${stm.name}")
	private String productName;
	
	@Value("${stm.license.path}")
	private String licensePath;
	
	/**
	 * 产品版本
	 */
	@Value("${stm.version}")
	private String version;

	@Value("${stm.license.filename}")
	private String licenseName;

	@Override
	public String getLicenseFileName() {
		return this.licenseName;
	}

	@Override
	public FileInputStream getLicenseFile() throws FileNotFoundException{
		return new FileInputStream(licensePath+licenseName);
	}

	@Override
	public String getDatFileName() {
		return datFileName;
	}

	public String getLicensePath() {
		return licensePath;
	}
	public String getDatFileGenerateFile(){
		return datFileGenerateFile;
	}

	@Override
	public FileInputStream getDatFile() throws FileNotFoundException{
		return new FileInputStream(licensePath+datFileName);
	}

	@Override
	public void importLicense(MultipartFile file) throws LicenseNotFoundException {
		try {
			FileUtil.inputStreamToFile(file.getInputStream(),getLicensePath()+file.getOriginalFilename(),1024);
			License.checkLicense().reload();
		} catch (Exception e) {
			throw new LicenseNotFoundException(e);
		}
	}

	@Override
	public LicenseBo checkLicense() throws LicenseNotFoundException {
		try {
			return getBaseLicense(License.checkLicense());
		} catch (Exception e) {
			throw new LicenseNotFoundException(e);
		}
	}

	@Override
	public LicenseBo getLicense() throws LicenseNotFoundException{
		License license;
		try {
			license = License.checkLicense();
		} catch (Exception e) {
			
			throw new LicenseNotFoundException(e);
		}
		LicenseBo bo=this.getBaseLicenseContainVersion(license);
		
		bo.setCollector(new Collector(LicenseModelEnum.stmCollector.getCode(),
				license.checkModelAvailableNum(LicenseModelEnum.stmCollector)));
		
		bo.setMonitorModule(getMonitorModules(license));
		
		List<PortalModule> portalModules=getPortalModules(license);
		bo.setPortalModules(portalModules);
		
		return bo;
	}
	
	private List<PortalModule> getPortalModules(License license){
		List<PortalModule> portalModules=new ArrayList<PortalModule>();
		List<LicenseModelEnum> licenEnums = LicenseModelEnum.getAuthorizationModelModuleEnum();
		LicenseModelEnum base = null;
		LicenseModelEnum stor = null;
		LicenseModelEnum topoMr = null;
		Iterator<LicenseModelEnum> it = licenEnums.iterator();
		while(it.hasNext()){
			LicenseModelEnum enum1 = it.next();
			switch (enum1.name()) {
			case "stmModelBase":
				base=enum1;
				break;
			case "stmModelStor":
				stor = enum1;
				break;
			case "stmModelTopoMr":
				topoMr = enum1;
				break;

			default:
				break;
			}
		}
		portalModules.add(new PortalModule(20L,"基础平台",license.checkModelAvailableNum(base)>0));
		portalModules.add(new PortalModule(13L,"存储",license.checkModelAvailableNum(stor)>0));
		portalModules.add(this.getPortalModule(15L, license));
		portalModules.add(this.getPortalModule(16L, license));
		portalModules.add(this.getPortalModule(4L, license));
		portalModules.add(this.getPortalModule(8L, license));
		portalModules.add(this.getPortalModule(10L, license));
		//malachi in add
		portalModules.add(this.getPortalModule(244L, license));
		//用达梦数据库时 删除了此项
//		portalModules.add(this.getPortalModule(245L, license));

		PortalModule module = this.getPortalModule(100500L, license);
		if(module != null){
			portalModules.add(module);
		}
		module = this.getPortalModule(100501L, license);
		if(module != null){
			portalModules.add(module);
		}
		portalModules.add(new PortalModule(21L,"3D机房",license.checkModelAvailableNum(topoMr)>0));
		return portalModules;
	}
	
	private MonitorModule getMonitorModules(License license){
		Map<String,ILicenseRemainCountApi> remains=getRemains();
		int authorCount=license.checkModelAvailableNum(LicenseModelEnum.stmMonitorSh);//stmMonitorRd
		int usedCount = remains.get(LicenseModelEnum.stmMonitorSh.toString()).getUsed();//stmMonitorRd
		return new MonitorModule(authorCount, authorCount-usedCount, usedCount);
	}
	
	
	List<Right> rights;
	private List<Right> getRights(){
		if(rights==null){
			rights=rightApi.getRightByType(0);
		}
		return rights;
	}
	/**
	 * 查询模块
	 * @param id
	 * @return
	 */
	private Right getById(Long id){
		Right right = rightApi.get(id);
		return right;
	}
	/**
	 * 组装PortalModule对象
	 * @param id
	 * @param license
	 * @return
	 */
	private PortalModule getPortalModule(Long id,License license){
		LicenseModelEnum licenseModelEnum = LicenseModelEnum.getLicenseModelEnumByCode(id.toString());
		System.out.println("malachi right id = " + id);
		Right right = getById(id);
		System.out.println("malachi right = " + right);
		PortalModule portalModules = null;
		if(right!=null){
			System.out.println("malachi rightget = " + right.getId() + right.getName());

			portalModules = new PortalModule(right.getId(),right.getName() , (licenseModelEnum==null)?false:(license.checkModelAvailableNum(licenseModelEnum)>0));
		}else{
			logger.error("Right is null,Pls check stm_sys_right table.");
		}
		
		return portalModules;
	}
	
	private LicenseBo getBaseLicense(License license){
		Date validDate=license.getValidDate();
		System.out.println(validDate);
		LicenseModelEnum simple=LicenseModelEnum.stmModelBase;
		LicenseBo bo=new LicenseBo(
				license.isOverTime(), 
				(validDate.getTime()-System.currentTimeMillis())<(24*60*60*1000l*tiptime), 
				validDate,
				this.productName,
				imageApi.getCopyright(),
				license.checkModelAvailableNum(simple)>0,
				license.isIfMatch()
				);
		return bo;
	}
	
	private LicenseBo getBaseLicenseContainVersion(License license){
		Date validDate=license.getValidDate();
		LicenseModelEnum simple=LicenseModelEnum.stmModelBase;
		LicenseBo bo=new LicenseBo(
				license.isOverTime(), 
				(validDate.getTime()-System.currentTimeMillis())<(24*60*60*1000l*tiptime), 
				validDate,
				this.productName,
				imageApi.getCopyright(),
				license.checkModelAvailableNum(simple)>0,
				this.version,
				license.isIfMatch()
				);
		return bo;
	}
	
	private Map<String,ILicenseRemainCountApi> getRemains(){
		if(this.remains==null || this.remains.size()==0){
			Collection<ILicenseRemainCountApi> tempRemains=context.getBeansOfType(ILicenseRemainCountApi.class).values();
			this.remains=new HashMap<String, ILicenseRemainCountApi>();
			for(ILicenseRemainCountApi api:tempRemains){
				this.remains.put(api.getType(),api);
			}
			LicenseModelEnum monitorEnum = LicenseModelEnum.stmMonitorSh;//stmMonitorRd
			if(this.remains.get(monitorEnum.toString())==null){
				this.remains.put(monitorEnum.toString(),new DefaultLicenseRemainCountImpl(monitorEnum.toString()));
			}
		}
		return this.remains;
	}
	
	@Override
	public void setApplicationContext(ApplicationContext context)
			throws BeansException {
		this.context=context;
	}

	@Override
	public Map<Long,Integer> getAuthorPortal() throws LicenseNotFoundException {
		try {
			License license = License.checkLicense();
			Map<Long,Integer> sets=new HashMap<Long, Integer>();
			if(!license.isOverTime()){
				for(LicenseModelEnum module:LicenseModelEnum.getPortalEnum()){
					if(module.equals(LicenseModelEnum.stmModelBase)){
						String[] baseModelCodes = LicenseModelEnum.getBaseModelCode();
						for (String baseCode : baseModelCodes) {
							sets.put(Long.valueOf(baseCode), license.checkModelAvailableNum(module));
						}
					}else
						sets.put(Long.valueOf(module.getCode()), license.checkModelAvailableNum(module));
				}
			}
			return sets;
		} catch (Exception e) {
			throw new LicenseNotFoundException(e);
		}
	}

	@Override
	public List<PortalModule> getPortalModules() throws LicenseCheckException {
		License license=License.checkLicense();
		return getPortalModules(license);
	}

	@Override
	public int getAuthor(String model) throws LicenseNotFoundException {
		try {
			return License.checkLicense().checkModelAvailableNum(LicenseModelEnum.getLicenseModelEnumByCode(model));
		} catch (LicenseCheckException e) {
			throw new LicenseNotFoundException(e);
		}
	}

	@Override
	public Boolean importValiaLience(MultipartFile file)
			throws LicenseNotFoundException {
				String realpath=null;
				String finalPath=null;
				try {
//			 	     realpath=licensePath + File.separator + licenseValidateDirName;
					realpath=licensePath + licenseValidateDirName + "/";
					finalPath =file.getOriginalFilename();
					FileUtils.copyInputStreamToFile(file.getInputStream(), new File(realpath,finalPath));
					License.checkLicense().reloadValia(realpath);
//					FileUtil.inputStreamToFile(file.getInputStream(),getLicensePath()+file.getOriginalFilename(),1024);
//					FileUtil.removeFile(new File(realpath+File.separator+finalPath));
					FileUtil.inputStreamToFile(file.getInputStream(),getLicensePath()+getLicenseFileName(),1024);
					FileUtil.removeFile(new File(realpath+finalPath));
					return true;
				} catch (Exception e) {
					logger.error(e.getMessage(),e);
				}
				try {
//					FileUtil.removeFile(new File(realpath+File.separator+finalPath));
					FileUtil.removeFile(new File(realpath+finalPath));
				} catch (IOException e) {
					logger.error(e.getMessage(),e);
				}
				return false;
		
		
	}

	@Override
	public void getIndentity() {

		
		//判断操作系统类型
		if(System.getProperty("os.name").toLowerCase().indexOf("win") >= 0){
			
				
				
				//拿到exe执行文件
				File exeFile = new File(licensePath + datFileGenerateFile);
					
					//执行文件存在
					Runtime runtime = Runtime.getRuntime();
					Process process = null;
					try {
						//获取盘符
						String partition = licensePath.substring(0,licensePath.indexOf(":") + 1);
						
						process = runtime.exec("cmd /c " + partition + " && cd " + licensePath + " && " + exeFile.getAbsolutePath());
						process.waitFor();
					} catch (Exception e) {
						logger.error(e.getMessage(),e);
					}
					

		}
		
	
		
	}

	
}


