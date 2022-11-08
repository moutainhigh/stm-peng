package com.mainsteam.stm.platform.web.vo;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * <li>文件名称: LoginUser.java</li> <li>公　　司: 武汉美新翔盛科技有限公司</li> <li>版权所有:
 * 版权所有(C)2019-2020</li> <li>修改记录: ...</li> <li>内容摘要: ...</li> <li>其他说明: ...</li>
 * 
 * @version ms.stm
 * @since 2019年6月14日
 * @author ziwenwen
 */
public interface ILoginUser {
	/**
	 * 存储用户信息键
	 */
	public static final String SESSION_LOGIN_USER = "SESSION_LOGIN_USER";

	/**
	 * 首页页签码
	 */
	Long RIGHT_HOME=1L;

	/**
	 * 系统页签码
	 */
	Long RIGHT_SYSTEM=2L;

	/**
	 * 资源页签码
	 */
	Long RIGHT_RESOURCE=3L;

	/**
	 * 业务页签码
	 */
	Long RIGHT_BIZ=4L;

	/**
	 * 拓扑页签码
	 */
	Long RIGHT_TOPO=5L;

	/**
	 * 告警页签码
	 */
	Long RIGHT_ALARM=6L;
	/**
	 * 流量分析
	 */
	Long RIGHT_NET_FLOW=7L;
	/**
	 * 配置文件管理
	 */
	Long RIGHT_CONFIG_FILE=8L;
	/**
	 * 知识库
	 */
	Long RIGHT_KNOWLEDGE=9L;
	/**
	 * 巡检管理
	 */
	Long RIGHT_PLAN=10L;
	/**
	 * 报表管理
	 */
	Long RIGHT_REPORT=11L;
	/**
	 * 极简模式
	 */
	Long RIGHT_SIMPLE=12L;
	/**
	 * 登陆管理
	 */
	Long RIGHT_LOGIN=13L;
	/**
	 * ITSM
	 */
	Long RIGHT_ITSM=14L;
	/**
	 * 3D机房设置
	 */
	Long RIGHT_3D=15L;

	/**
	 * 本次登陆时间
	 */
	Calendar getLoginTime();

	public Long getId();

	public Long getCreatorId();

	public int getSex();

	public int getUserType();

	public int getStatus();

	public Date getCreatedTime();

	public String getName();

	public String getAccount();

	public String getPassword();

	public String getMobile();

	public String getEmail();

	public List<IRight> getRights();

	public Set<IDomain> getDomains(Long rightId);
	
	public List<IRole> getRoles();
	
	/**
	 * <pre>
	 * 获取用户作为管理者的域集合
	 * </pre>
	 * @return
	 */
	Set<IDomain> getManageDomains();
	
	/**
	 * <pre>
	 * 获取用户作为普通用户的域集合
	 * </pre>
	 * @return
	 */
	Set<IDomain> getCommonDomains();
	
	/**
	 * <pre>
	 * 获取用户作为域管理员的域的集合
	 * </pre>
	 * @return
	 */
	Set<IDomain> getDomainManageDomains();
	
	/**
	 * <pre>
	 * 获取用户所有的域集合
	 * </pre>
	 * @return
	 */
	Set<IDomain> getDomains();
	
	Map<String,Object> getCache();
	
	public boolean isCommonUser();

	public boolean isDomainUser();

	public boolean isSystemUser();

	public boolean isManagerUser();
}
