package com.mainsteam.stm.portal.threed.xfire.client;

import java.net.URL;

import javax.xml.namespace.QName;
import javax.xml.ws.Service;
import javax.xml.ws.WebEndpoint;
import javax.xml.ws.WebServiceClient;
import javax.xml.ws.WebServiceFeature;

@WebServiceClient(name = "DataStream", targetNamespace = "http://service.webservice.monitoring.uinv.com")
public class DataStreamService extends Service {

	public final static QName SERVICE = new QName(
			"http://service.webservice.monitoring.uinv.com", "DataStream");
	public final static QName DataStreamHttpPort = new QName(
			"http://service.webservice.monitoring.uinv.com", "DataStreamHttpPort");

	public DataStreamService(URL wsdlDocumentLocation) {
		this(wsdlDocumentLocation, SERVICE);
	}

	public DataStreamService(URL wsdlDocumentLocation, QName serviceName) {
		super(wsdlDocumentLocation, serviceName);
	}

	public DataStreamService(URL wsdlDocumentLocation, QName serviceName,
			WebServiceFeature... features) {
		super(wsdlDocumentLocation, serviceName, features);
	}

	@WebEndpoint(name = "DataStreamHttpPort")
	public DataStreamPortType getDataStreamHttpPort() {
		return super.getPort(DataStreamHttpPort, DataStreamPortType.class);
	}

	@WebEndpoint(name = "DataStreamHttpPort")
	public DataStreamPortType getDataStreamHttpPort(
			WebServiceFeature... features) {
		return super.getPort(DataStreamHttpPort, DataStreamPortType.class,
				features);
	}
}
