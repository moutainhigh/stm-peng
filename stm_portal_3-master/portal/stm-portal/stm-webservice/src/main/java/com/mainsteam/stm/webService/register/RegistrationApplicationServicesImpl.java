package com.mainsteam.stm.webService.register;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.Resource;
import javax.jws.WebService;
import javax.servlet.http.HttpServletRequest;
import javax.xml.ws.WebServiceContext;
import javax.xml.ws.handler.MessageContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mainsteam.stm.cache.IMemcache;
import com.mainsteam.stm.cache.MemCacheFactory;
import com.mainsteam.stm.platform.file.bean.FileConstantEnum;
import com.mainsteam.stm.platform.system.config.bean.SystemConfigBo;
import com.mainsteam.stm.platform.system.config.bean.SystemConfigConstantEnum;
import com.mainsteam.stm.platform.system.config.service.ISystemConfigApi;
import com.mainsteam.stm.system.image.api.IImageApi;
import com.mainsteam.stm.system.image.bo.ImageBo;
import com.mainsteam.stm.system.um.right.api.IRightApi;
import com.mainsteam.stm.system.um.right.bo.Right;
import com.mainsteam.stm.system.um.user.api.IUserApi;
import com.mainsteam.stm.system.um.user.bo.User;
import com.mainsteam.stm.util.ClassPathUtil;

@WebService
public class RegistrationApplicationServicesImpl implements RegistrationApplicationServices{

	private static final Logger logger = LoggerFactory.getLogger(RegistrationApplicationServicesImpl.class);
	
	@Resource
	private WebServiceContext wsContext;
	
	@Resource
	private IUserApi userApi;
	
	@Resource
	private ISystemConfigApi configApi;
	
	@Resource(name="systemImageApi")
	private IImageApi imageApi;
	
	@Resource(name="stm_system_right_impl")
	IRightApi rightApi;
	
	@Override
	public String registrationApplication(String registerJson){
		
		MessageContext mc = wsContext.getMessageContext();
		HttpServletRequest request = (HttpServletRequest)mc.get(MessageContext.SERVLET_REQUEST);
		
		System.out.println(request.getLocalAddr());
		System.out.println(request.getLocalName());
		System.out.println(request.getLocalPort());
		System.out.println(request.getPathInfo());
		
		return null;
	}
	
	@Override
	public void cancellationRegistration(String registerCode) {

		//移除注册文件
		File file = new File(ClassPathUtil.getTomcatHome() +  "ITM_REGISER.CODE");
		if(file.exists() && file.isFile()){
			logger.info("remove register file.");
			file.delete();
		}
		
		//系统
		IMemcache<Boolean> icache=MemCacheFactory.getLocalMemCache(Boolean.class);
		icache.set("ITM_REGISER", false);
		
	}
	
	@Override
	public String addUser(String userJson) {
		
		JSONObject result=new JSONObject();
		
		List<User> users=convertJSONString(userJson);
		
		try{
			for(User user:users){
				String account = user.getAccount();
				User sysUser=userApi.getUserByAccount(account);
				if(sysUser==null){
					userApi.add(user);
				}else{
					sysUser.setName(user.getName());
					sysUser.setSex(user.getSex());
					sysUser.setEmail(user.getEmail());
					sysUser.setMobile(user.getMobile());
					sysUser.setPhone(user.getPhone());
					sysUser.setPassword(user.getPassword());
					sysUser.setStatus(user.getStatus());
					userApi.update(sysUser);
				}	
			}
			
		}catch(Exception e){
			result.put("result", "同步失败");
			result.put("code", "500");
			return result.toJSONString();
		}

		

		result.put("result", "同步成功");
		result.put("code", "200");
		
		return result.toJSONString();
	}

	@Override
	public String deleteUser(String userJson) {
		// TODO Auto-generated method stub
		List<User> users=convertJSONString(userJson);
		
		Long[] ids=new Long[users.size()];
		
		for(int i=0;i < users.size();i++){
			String account =users.get(i).getAccount();
			User user=userApi.getUserByAccount(account);
			if(user!=null && user.getId()!=null){
				ids[i]=user.getId();
			}
		}
		
		userApi.batchDel(ids);

		
		
		JSONObject obj=new JSONObject();
		obj.put("result", "删除成功");
		obj.put("code", "200");
		
		return obj.toJSONString();
	}
	
	/**
	 * JSONString 2 User List
	 * @param jsonUsers
	 * @return
	 */
	private List<User> convertJSONString(String jsonUsers){
		List<User> userList = new ArrayList<User>();
		JSONArray jsonArr=JSONArray.parseArray(jsonUsers);

		for(int i=0;i<jsonArr.size();i++){
			User user=new User();
			
			JSONObject json=jsonArr.getJSONObject(i);
			
			String account = json.getString("loginName");

			String name = json.getString("name")==null ? "" : json.getString("name");
			String sex = json.getString("sex")==null ? "0" : json.getString("sex");
			String email = json.getString("email")==null ? "" : json.getString("email");
			String phone = json.getString("phone")==null ? "" : json.getString("phone");
			String mobile = json.getString("mobile")==null ? "" : json.getString("mobile");
			String status = json.getString("status")==null ? "1" : json.getString("status");
			String userType = json.getString("userType")==null ? "1" : json.getString("userType");
			String password=json.getString("password")==null ? "111111" : json.getString("password");

			
			if(account==null || "".equals(account) || account.equals("admin")){
				logger.error("can't convert this user.");
			}else{
				user.setAccount(account);
				user.setName(name);
				user.setSex(Integer.parseInt(sex));
				user.setStatus(Integer.parseInt(status));
				user.setEmail(email);
				user.setMobile(mobile);
				user.setPhone(phone);
				user.setPassword(password);
				user.setCreatorId(-1L);
				int registerUserType = Integer.parseInt(userType) == 0 ? 1 : Integer.parseInt(userType);
				user.setUserType(registerUserType);
				userList.add(user);
			}
		}
		
		return userList;
	}

	@Override
	public String changeSkin(String userJson) {
		
		JSONObject user = JSONObject.parseObject(userJson);
		
		JSONObject result = new JSONObject();
		if(user != null && !user.isEmpty()){
			String face = user.getString("face");
			if(face == null || face.equals("")){
				result.put("result", "face参数获取失败");
				result.put("code", "500");
			}else{
				String skin = null;
				if(face.equals("skin0")){
					//蓝色皮肤
					skin ="{\"selected\":\"blue\",\"skins\":[{\"style\":\"darkgreen\",\"name\":\"墨绿\"},{\"style\":\"blue\",\"name\":\"蓝色\"}]}";
				}else if(face.equals("skin1")){
					//浅色皮肤
					skin ="{\"selected\":\"darkgreen\",\"skins\":[{\"style\":\"darkgreen\",\"name\":\"墨绿\"},{\"style\":\"blue\",\"name\":\"蓝色\"}]}";
				//	skin = "{'selected':'darkgreen','skins':[{'style':'darkgreen','name':'墨绿'},{'style':'blue','name':'蓝色'}]}";
				}
				if(skin == null || skin.equals("")){
					result.put("result", "未提供该皮肤");
					result.put("code", "500");
				}else{
					try {
						changeImg(skin);
						SystemConfigBo config=new SystemConfigBo();
						config.setId(SystemConfigConstantEnum.SYSTEM_CONFIG_SKIN.getCfgId());
						config.setContent(skin);
						configApi.updateSystemConfig(config);
						result.put("result", "修改成功");
						result.put("code", "200");
					} catch (Exception e) {
						logger.error(e.getMessage(),e);
						result.put("result", e.getMessage());
						result.put("code", "500");
					}
				}
			}
		}else{
			result.put("result", "参数获取失败");
			result.put("code", "500");
		}
		
		return result.toJSONString();
	}
	
	private void changeImg(String skin){
		List<Right> newRightList = new ArrayList<Right>();
		Map skinMap = JSONObject.parseObject(skin, Map.class);
		Map oldSkinMap = JSONObject.parseObject(configApi.getSystemConfigById(SystemConfigConstantEnum.SYSTEM_CONFIG_SKIN.getCfgId()).getContent(), Map.class);
		if(skinMap.containsKey("selected")){
			List<Right> rightList = rightApi.getAll4Skin();
			Map<Long, Right> rightMap = new HashMap<Long, Right>();
			for(int i = 0; rightList != null && i < rightList.size(); i++){
				Right right = rightList.get(i);
				rightMap.put(right.getId(), right);
			}
			String selectedSkin = skinMap.get("selected").toString(), oldSelectedSkin = oldSkinMap.get("selected").toString();
			List<FileConstantEnum> fileConstantEnumList = FileConstantEnum.getFileConstantEnum(selectedSkin);
			for(int i = 0; fileConstantEnumList != null && i < fileConstantEnumList.size(); i++){
				FileConstantEnum fileConstantEnum = fileConstantEnumList.get(i);
				if(rightMap.containsKey(fileConstantEnum.getMenuId())){
					Right right = rightMap.get(fileConstantEnum.getMenuId());
					right.setFileId(fileConstantEnum.getFileId());
					rightApi.update(right);
					newRightList.add(right);
				}
			}
			// 系统图片管理配置文件
			ImageBo imgBo = imageApi.get();
			if("darkgreen".equals(selectedSkin)){
				imgBo.setSystemDefaultLogo("resource/themes/blue/images/logo.png");
				imgBo.setLoginDefaultLogo("resource/themes/blue/images/comm/table/login-logo.png");
			}else{
				imgBo.setSystemDefaultLogo("resource/themes/" + selectedSkin + "/images/logo.png");
				imgBo.setLoginDefaultLogo("resource/themes/" + selectedSkin + "/images/comm/table/login-logo.png");
			}
			imageApi.update(imgBo);
		}
	}

}
