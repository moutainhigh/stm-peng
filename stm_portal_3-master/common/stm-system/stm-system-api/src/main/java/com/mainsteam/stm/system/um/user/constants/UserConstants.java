package com.mainsteam.stm.system.um.user.constants;

/**
 * 用户常量类
 * <li>文件名称: UserConstants.java</li>
 * <li>文件描述: 本类描述</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年9月2日
 */
public class UserConstants {
	/**
	 * 用户类型-超级管理员
	 */
	public static final int USER_TYPE_SUPER_ADMIN = 4;
	/**
	 * 用户类型-系统管理员
	 */
	public static final int USER_TYPE_SYSTEM_ADMIN = 3;
	
	/**
	 * 用户类型-域管理员
	 */
	@Deprecated
	public static final int USER_TYPE_DOMAIN_ADMIN = 2;
	/**
	 * 用户类型-普通用户
	 */
	public static final int USER_TYPE_ORDINARY_USERS = 1;
	/**
	 * 域管理员默认关联关系角色ID
	 */
	public static final long DOMAIN_USER_DEFAULT_ROLE_ID = 0L;
	/**
	 * 用户状态启用
	 */
	public static final int USER_STATUS_ENABLE = 1;
	/**
	 * 用户状态停用
	 */
	public static final int USER_STATUS_DISABLE = 0;
	/**
	 * 账户锁定类型
	 */
	public static final int USER_LOCK_TYPE_MANUAL = 1;
	/**
	 * 账户密码修改时间
	 */
	public static final int USER_LOCK_TYPE_ERRORPASS = 2;
	/**
	 * 账户锁定时间
	 */
	public static final int USER_LOCK_TYPE_OVERDUEPASS = 3;
}
