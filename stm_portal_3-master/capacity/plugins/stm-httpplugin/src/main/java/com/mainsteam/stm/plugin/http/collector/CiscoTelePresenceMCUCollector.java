package com.mainsteam.stm.plugin.http.collector;

import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.sun.org.apache.xerces.internal.jaxp.SAXParserFactoryImpl;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.xmlrpc.XmlRpcException;
import org.apache.xmlrpc.client.XmlRpcClient;
import org.apache.xmlrpc.client.XmlRpcClientConfigImpl;
import org.apache.xmlrpc.util.SAXParsers;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import javax.net.ssl.*;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lich
 */
public class CiscoTelePresenceMCUCollector implements HttpCollector {

    public static final String PARAM_IP = "IP";
    public static final String PARAM_PORT = "port";
    public static final String PARAM_PROTOCOL = "protocol";
    public static final String PARAM_USERNAME = "username";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_ENCODING = "encoding";
    public static final String PARAM_TIMEOUT = "timeout";

    private static final XmlRpcClient client = new XmlRpcClient();
    private static final int DEFAULT_TIMEOUT = 5000;
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final Log LOGGER = LogFactory.getLog(CiscoTelePresenceMCUCollector.class);

    static {
        try {
            SSLContext context = SSLContext.getInstance("SSL");
            TrustManager[] trustManagers = {new AllTrustManager()};
            context.init(null, trustManagers, null);
            SSLSocketFactory socketFactory = context.getSocketFactory();
            HostnameVerifier verifier = new HostnameVerifier() {
                @Override
                public boolean verify(String s, SSLSession sslSession) {
                    return true;
                }
            };
            HttpsURLConnection.setDefaultHostnameVerifier(verifier);
            HttpsURLConnection.setDefaultSSLSocketFactory(socketFactory);
            SAXParserFactory saxParserFactory = new SAXParserFactoryImpl();
            saxParserFactory.setValidating(false);
            saxParserFactory.setNamespaceAware(true);
            SAXParsers.setSAXParserFactory(saxParserFactory);
        } catch (Throwable e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    private SAXBuilder builder;
    private URL systemURL;
    private XmlRpcClientConfigImpl config;
    private String username;
    private String password;

    public PluginResultSet conferenceMeQuery() throws XmlRpcException {
        Map<String, Object> response = (Map<String, Object>) client.execute(config, "conferenceme.query", getAuthParamList());
        PluginResultSet resultSet = new PluginResultSet();
        int column = 0;
        resultSet.putValue(0, column++, response.get("enabled"));
        resultSet.putValue(0, column++, response.get("setting"));
        resultSet.putValue(0, column++, response.get("maxParticipants"));
        return resultSet;
    }

    public PluginResultSet streamingQuery() throws XmlRpcException {
        Map<String, Object> response = (Map<String, Object>) client.execute(config, "streaming.query", getAuthParamList());
        PluginResultSet resultSet = new PluginResultSet();
        int column = 0;
        resultSet.putValue(0, column++, response.get("enabled"));
        resultSet.putValue(0, column++, response.get("setting"));
        return resultSet;
    }

    public PluginResultSet deviceQuery() throws XmlRpcException {
        Map<String, Object> response = (Map<String, Object>) client.execute(config, "device.query", getAuthParamList());
        PluginResultSet resultSet = new PluginResultSet();
        int column = 0;
        resultSet.putValue(0, column++, response.get("apiVersion"));
        resultSet.putValue(0, column++, response.get("maxConferenceSize"));
        resultSet.putValue(0, column++, response.get("shutdownStatus"));
        resultSet.putValue(0, column++, response.get("rebootRequired"));
        resultSet.putValue(0, column++, response.get("finishedBooting"));
        Object activatedFeatures = response.get("activatedFeatures");
        if (activatedFeatures != null) {
            int row = 0;
            for (Object activatedFeatureObject : (Object[]) activatedFeatures) {
                Map<String, Object> activatedFeature = (Map<String, Object>) activatedFeatureObject;
                resultSet.putValue(row++, column, activatedFeature.get("feature"));
//                resultSet.putValue(row, column + 1, activatedFeature.get("key"));
            }
        }
        column += 1;
        Object activatedLicenses = response.get("activatedLicenses");
        if (activatedLicenses != null) {
            int row = 0;
            for (Object activatedLicenseObject : (Object[]) activatedLicenses) {
                Map<String, Object> activatedLicense = (Map<String, Object>) activatedLicenseObject;
                resultSet.putValue(row, column, activatedLicense.get("license"));
                resultSet.putValue(row++, column + 1, activatedLicense.get("ports"));
//                resultSet.putValue(row, column + 2, activatedLicense.get("key"));
            }
        }
        column += 2;
        return resultSet;
    }

    public PluginResultSet deviceEncryptionQuery() throws XmlRpcException {
        Map<String, Object> response = (Map<String, Object>) client.execute(config, "device.encryption.query", getAuthParamList());
        PluginResultSet resultSet = new PluginResultSet();
        int column = 0;
        resultSet.putValue(0, column++, response.get("enabled"));
        resultSet.putValue(0, column++, response.get("setting"));
        return resultSet;
    }

    public PluginResultSet deviceHealthQuery() throws XmlRpcException {
        Map<String, Object> response = (Map<String, Object>) client.execute(config, "device.health.query", getAuthParamList());
        PluginResultSet resultSet = new PluginResultSet();
        int column = 0;
        resultSet.putValue(0, column++, response.get("cpuLoad"));
        resultSet.putValue(0, column++, response.get("mediaLoad"));
        resultSet.putValue(0, column++, response.get("audioLoad"));
        resultSet.putValue(0, column++, response.get("videoLoad"));
        resultSet.putValue(0, column++, response.get("fanStatus"));
        resultSet.putValue(0, column++, response.get("temperatureStatus"));
        resultSet.putValue(0, column++, response.get("rtcBatteryStatus"));
        resultSet.putValue(0, column++, response.get("voltagesStatus"));
        resultSet.putValue(0, column++, response.get("operationalStatus"));
        return resultSet;
    }

    public PluginResultSet deviceNetworkQuery() throws XmlRpcException {
        Map<String, Object> response = (Map<String, Object>) client.execute(config, "device.network.query", getAuthParamList());
        PluginResultSet resultSet = new PluginResultSet();
        int row = 0;
        if (response.get("portA") != null) {
            int column = 0;
            Map<String, Object> portA = (Map<String, Object>) response.get("portA");
            resultSet.putValue(row, column++, "portA");
            resultSet.putValue(row, column++, portA.get("macAddress"));
            resultSet.putValue(row, column++, portA.get("enabled"));
            resultSet.putValue(row, column++, portA.get("linkStatus"));
            resultSet.putValue(row, column++, portA.get("ipv4Address"));
            resultSet.putValue(row, column++, portA.get("ipv6Address"));
            row++;
        }
        if (response.get("portB") != null) {
            int column = 0;
            Map<String, Object> portB = (Map<String, Object>) response.get("portB");
            resultSet.putValue(row, column++, "portB");
            resultSet.putValue(row, column++, portB.get("macAddress"));
            resultSet.putValue(row, column++, portB.get("enabled"));
            resultSet.putValue(row, column++, portB.get("linkStatus"));
            resultSet.putValue(row, column++, portB.get("ipv4Address"));
            resultSet.putValue(row, column++, portB.get("ipv6Address"));
            row++;
        }
        return resultSet;
    }

    public PluginResultSet getSystemXml() throws JDOMException, IOException {
        Document document = builder.build(systemURL);
        Element system = document.getRootElement();
        PluginResultSet resultSet = new PluginResultSet();
        int column = 0;
        resultSet.putValue(0, column++, system.getChildTextTrim("manufacturer"));
        resultSet.putValue(0, column++, system.getChildTextTrim("model"));
        resultSet.putValue(0, column++, system.getChildTextTrim("serial"));
        resultSet.putValue(0, column++, system.getChildTextTrim("softwareVersion"));
        resultSet.putValue(0, column++, system.getChildTextTrim("buildVersion"));
        resultSet.putValue(0, column++, system.getChildTextTrim("hostName"));
        resultSet.putValue(0, column++, system.getChildTextTrim("totalVideoPorts"));
        resultSet.putValue(0, column++, system.getChildTextTrim("totalAudioOnlyPorts"));
        resultSet.putValue(0, column++, system.getChildTextTrim("totalStreamingAndContentPorts"));
        resultSet.putValue(0, column++, system.getChildTextTrim("uptimeSeconds"));
        resultSet.putValue(0, column++, system.getChildTextTrim("clusterType"));
        Element videoPortAllocation = system.getChild("videoPortAllocation");
        if (videoPortAllocation != null) {
            int row = 0;
            for (Element videoPort : videoPortAllocation.getChildren()) {
                resultSet.putValue(row, column, videoPort.getName());
                resultSet.putValue(row++, column + 1, videoPort.getTextTrim());
            }
        }
        column += 2;
        return resultSet;
    }

    private List<Map<String, Object>> getAuthParamList() {
        List<Map<String, Object>> authParamList = new ArrayList<>();
        HashMap<String, Object> paramMap = new HashMap<>();
        if (username != null)
            paramMap.put("authenticationUser", username);
        if (password != null)
            paramMap.put("authenticationPassword", password);
        authParamList.add(paramMap);
        return authParamList;
    }

    @Override
    public void init(Map<String, String> initMap) throws MalformedURLException {
        username = StringUtils.isNotEmpty(initMap.get(PARAM_USERNAME)) ? initMap.get(PARAM_USERNAME) : null;
        password = StringUtils.isNotEmpty(initMap.get(PARAM_PASSWORD)) ? initMap.get(PARAM_PASSWORD) : null;
        URL serverURL = new URL(initMap.get(PARAM_PROTOCOL), initMap.get(PARAM_IP), Integer.valueOf(initMap.get(PARAM_PORT)), "/RPC2");
        config = new XmlRpcClientConfigImpl();
        config.setServerURL(serverURL);
        config.setEncoding(StringUtils.isNotEmpty(initMap.get(PARAM_ENCODING)) ? initMap.get(PARAM_ENCODING) : DEFAULT_ENCODING);
        config.setConnectionTimeout(StringUtils.isNotEmpty(initMap.get(PARAM_TIMEOUT)) ? Integer.valueOf(initMap.get(PARAM_TIMEOUT)) : DEFAULT_TIMEOUT);
        config.setReplyTimeout(StringUtils.isNotEmpty(initMap.get(PARAM_TIMEOUT)) ? Integer.valueOf(initMap.get(PARAM_TIMEOUT)) : DEFAULT_TIMEOUT);
        builder = new SAXBuilder();
        systemURL = new URL(serverURL.getProtocol(), serverURL.getHost(), serverURL.getPort(), "/system.xml");
    }

    @Override
    public void close() {

    }
}

class AllTrustManager implements X509TrustManager {

    @Override
    public void checkClientTrusted(X509Certificate[] ax509certificate, String s) throws CertificateException {
    }

    @Override
    public void checkServerTrusted(X509Certificate[] ax509certificate, String s) throws CertificateException {
    }

    @Override
    public X509Certificate[] getAcceptedIssuers() {
        return null;
    }
}
