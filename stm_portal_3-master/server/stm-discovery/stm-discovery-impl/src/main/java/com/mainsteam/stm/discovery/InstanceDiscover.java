/**
 * 
 */
package com.mainsteam.stm.discovery;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.mainsteam.stm.discovery.exception.InstanceDiscoveryException;
import com.mainsteam.stm.discovery.obj.DiscoveryParameter;
import com.mainsteam.stm.discovery.obj.ModelResourceInstance;
import com.mainsteam.stm.errorcode.ServerErrorCodeConstant;
import com.mainsteam.stm.exception.BaseException;

/**
 * 通过指标数据采集来实现资源实例<br />
 * 此接口供采集器（Collector）使用
 * 
 * @author ziw
 * 
 */
public class InstanceDiscover implements InstanceDiscoverMBean {

	private static final Log logger = LogFactory.getLog(InstanceDiscover.class);

	private InstanceCollectDiscover collectDiscover;

	public void setCollectDiscover(InstanceCollectDiscover collectDiscover) {
		this.collectDiscover = collectDiscover;
	}

	/**
	 * 
	 */
	public InstanceDiscover() {
	}

	@Override
	public ModelResourceInstance discovery(DiscoveryParameter p)
			throws InstanceDiscoveryException {
		try {
			//malachi in discovery2
			return collectDiscover.discovery(p);
		} catch (InstanceDiscoveryException e) {
			throw e;
		} catch (@SuppressWarnings("hiding") BaseException e) {
			throw new InstanceDiscoveryException(e);
		} catch (Throwable e) {
			if (logger.isErrorEnabled()) {
				logger.error("discovery", e);
			}
			throw new InstanceDiscoveryException(
					ServerErrorCodeConstant.ERR_SERVER_UNKOWN_ERROR, e);
		}
	}
}
