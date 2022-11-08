package com.mainsteam.stm.plugin.sharepoint;

import com.mainsteam.stm.plugin.apache.ApacheBo;
import com.mainsteam.stm.plugin.parameter.JBrokerParameter;
import com.mainsteam.stm.plugin.session.BaseSession;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;

public class SharePoint2013PluginSession extends BaseSession {

	@Override
	public void init(PluginInitParameter initParameters)
			throws PluginSessionRunException {
		if (initParameters.getParameters() != null) {
			for (Parameter p : initParameters.getParameters()) {
				JBrokerParameter jbp = super.getParameter();
				switch (p.getKey()) {
				case "sharepoint2013Username":
					jbp.setUsername(p.getValue());
					break;
				case "sharepoint2013Password":
					jbp.setPassword(p.getValue());
					break;
				case "sharepoint2013Timeout":
					jbp.setTimeout(Integer.parseInt(p.getValue()));
					break;
				case "sharepoint2013UrlBase":
					jbp.setApacheBo(new ApacheBo());
					String[] tmp = p.getValue().split("/");
					String[] two = tmp[2].split(":");
					jbp.setIp(two[0]);
					jbp.setPort(two.length == 2 ? Integer.parseInt(two[1]) : 80);
					int index = p.getValue().indexOf("/", 8);
					if (index != -1) {
						jbp.getApacheBo().setUrlParam(
								p.getValue().substring(index + 1));
					}
					if (p.getValue().startsWith("http")) {
						jbp.getApacheBo().setSSL(false);
					} else if (p.getValue().startsWith("https")) {
						jbp.getApacheBo().setSSL(true);
					}
					break;
				}
			}
		}
	}

	@Override
	public PluginResultSet execute(
			PluginExecutorParameter<?> executorParameter,
			PluginSessionContext context) throws PluginSessionRunException {
		SharePoint2013Collector c = new SharePoint2013Collector();
		JBrokerParameter jbp = super.getParameter();
		SharePoint2013Collector.RESLUTS_MAP = c.getFarmServers(jbp.getIp(), jbp
				.getPort(), jbp.getUsername(), jbp.getPassword(), jbp
				.getApacheBo().getUrlParam(), jbp.getTimeout(), jbp
				.getApacheBo().isSSL());
		PluginResultSet prs = super.execute(executorParameter, context);
		SharePoint2013Collector.RESLUTS_MAP = null;
		return prs;
	}

	@Override
	public boolean check(PluginInitParameter initParameters)
			throws PluginSessionRunException {
		return "1".equals(new SharePoint2013Collector().getAvailability(super
				.getParameter()));
	}
}
