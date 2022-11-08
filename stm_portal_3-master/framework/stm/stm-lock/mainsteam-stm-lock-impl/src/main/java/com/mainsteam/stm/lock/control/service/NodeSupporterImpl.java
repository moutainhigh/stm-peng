package com.mainsteam.stm.lock.control.service;

import java.util.UUID;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * @author 作者：ziw
 * @date 创建时间：2017年1月3日 下午4:30:10
 * @version 1.0
 */
public class NodeSupporterImpl implements NodeSupporter {
	private String node = UUID.randomUUID().toString();

	public NodeSupporterImpl() {
	}

	public void start() {
		Log logger = LogFactory.getLog(NodeSupporter.class);
		logger.info("Lock Support.Current Lock Node Id is " + node);
	}

	@Override
	public String getCurrentNode() {
		return node;
	}
}
