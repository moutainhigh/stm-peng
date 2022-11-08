package com.mainsteam.stm.platform.sequence.service;


/**
 * <li>文件名称: com.mainsteam.stm.platform.sequence.service.SQE.java</li>
 * <li>文件描述: 本类描述</li>
 * <li>版权所有: 版权所有(C)2019-2020</li>
 * <li>公    司: 武汉美新翔盛科技有限公司</li>
 * <li>内容摘要: 无</li>
 * <li>其他说明:无</li>
 * <li>完成日期：@date</li>
 * <li>修改记录: 无</li>
 * @version 3.0
 * @author  ziwen
 * @date	2019年10月16日
 */
public interface SEQ {

	/**
	 * 本地知识表序列名称
	 */
	String SEQNAME_STM_KNOWLEDGE = "STM_KNOWLEDGE";
	/**
	 * 本地知识解决方案序列名称
	 */
	String SEQNAME_STM_KNOWLEDGE_SOURCE_RESOLVE_REL="SEQ_STM_KNOWLEDGE_SOURCE_RESOLVE_REL";
	/**
	 * 本地知识解决方案序列名称
	 */
	String SEQNAME_STM_RESOURCE_GROUP="STM_PORTAL_CUSTOM_RESOURCE_GROUP";
	/**
	 * 本地知识解决方案序列名称
	 */
	String SEQNAME_STM_SYSTEM_DOMAIN="STM_SYSTEM_DOMAIN";
	
	String SEQNAME_STM_KNOWLEDGE_CAPACITY = "SEQ_STM_KNOWLEDGE_CAPACITY";
	
	/**
	 * 首页用户工作台
	 */
	String SEQNAME_STM_HOME_USER_SCREEN_REL="STM_HOME_USER_SCREEN_REL";
	
	/**
	 * syslog、trap
	 */
	String SEQNAME_STM_SYSLOG_SEQ="stm_syslog_seq";
	
	String SEQNAME_STM_SYSTEM_AUDITLOG="STM_SYSTEM_AUDITLOG";
	
	String SEQNAME_STM_SIMPLE_MANAGER_EXPECT = "SEQNAME_STM_SIMPLE_MANAGER_EXPECT";
	
	/**
	 * 用户表序列名称（暂时未使用）
	 */
//	String SEQNAME_STM_SYSTEM_USER = "STM_SYSTEM_USER";
}
