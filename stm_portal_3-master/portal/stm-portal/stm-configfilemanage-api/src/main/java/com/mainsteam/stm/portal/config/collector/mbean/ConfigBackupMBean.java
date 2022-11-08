package com.mainsteam.stm.portal.config.collector.mbean;

import java.io.File;

import com.mainsteam.stm.portal.config.collector.mbean.bean.ConfigReq;
import com.mainsteam.stm.portal.config.collector.mbean.bean.ConfigRsp;

/**
 * 
 * <li>文件名称: ConfigBackupMBean.java</li>
 * <li>公　　司: 武汉美新翔盛科技有限公司</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>修改记录: ...</li>
 * <li>内容摘要: ...</li>
 * <li>其他说明: ...</li>
 * @version  ms.stm
 * @since    2019年10月27日
 * @author   liupeng
 */
public interface ConfigBackupMBean {
	/**
	 * 通过Ssh方式登录获取配置
	 * @param req
	 * @return
	 */
	public ConfigRsp getBySsh(ConfigReq req) throws Exception;
	/**
	 * 通过Telnet方式登录获取配置
	 * @param req
	 * @return
	 */
	public ConfigRsp getByTelnet(ConfigReq req) throws Exception;
	/**
	 * 通过snmp获取设备描述、OS版本等信息
	 * @param ip 
	 * @param port
	 * @param community 团体名
	 * @return
	 */
	public String getBySnmp(String ip,int port,String community) throws Exception;

	/**
	 * 测试连接信息中的用户名密码是否能成功登录
	 * @param ip
	 * @param userName
	 * @param password
	 * @param type 登录方式,1:ssh,2:telnet
	 * @return
	 * @throws Exception
	 */
	public boolean checkLoginStatus(String ip,String userName,String password,String type) throws Exception;
	/**
	 * telnet测试 保存前进行测试
	 * @param req
	 * @return
	 * @throws Exception
	 */
	public String testByTelnet(ConfigReq req) throws Exception;
	/**
	 * ssh测试 保存前进行测试
	 * @param req
	 * @return
	 * @throws Exception
	 */
	public String testBySsh(ConfigReq req) throws Exception;
	/**
	 * 保存文件到采集器
	 * @param file
	 * @param fileName
	 * @throws Exception
	 */
	public void copyFile(File file, String fileName) throws Exception;
	/**
	 * 通过Ssh方式登录恢复配置
	 * @param req
	 * @return
	 * @throws Exception
	 */
	public ConfigRsp getReBySsh(ConfigReq req) throws Exception;
	/**
	 * 通过Telnet方式登录恢复配置
	 * @param req
	 * @return
	 * @throws Exception
	 */
	public ConfigRsp getReByTelnet(ConfigReq req) throws Exception;
}
