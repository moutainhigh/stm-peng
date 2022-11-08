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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lich
 */
public class CiscoTelePresenceServerCollector implements HttpCollector {

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
    private static final Log LOGGER = LogFactory.getLog(CiscoTelePresenceServerCollector.class);

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

    public PluginResultSet conferenceEnumerate() throws XmlRpcException {
        PluginResultSet resultSet = new PluginResultSet();
        List<Map<String, Object>> paraList = getAuthParamList();
        int row = 0;
        while (true) {
            Map<String, Object> response = (Map<String, Object>) client.execute(config, "conference.enumerate", paraList);
            Object conferences = response.get("conferences");
            if (conferences != null) {
                for (Object conferenceObject : (Object[]) conferences) {
                    int column = 0;
                    Map<String, Object> conference = (Map<String, Object>) conferenceObject;
                    resultSet.putValue(row, column++, conference.get("conferenceName"));
                    resultSet.putValue(row, column++, conference.get("conferenceGUID"));
                    resultSet.putValue(row, column++, conference.get("active"));
                    resultSet.putValue(row, column++, conference.get("persistent"));
                    resultSet.putValue(row, column++, conference.get("locked"));
                    resultSet.putValue(row++, column++, conference.get("numParticipants"));
                }
            }
            Object enumerateID = response.get("enumerateID");
            if (enumerateID != null) {
                paraList.get(0).put("enumerateID", Integer.valueOf(enumerateID.toString()));
            } else {
                break;
            }
        }
        return resultSet;
    }

    public PluginResultSet deviceQuery() throws XmlRpcException {
        Map<String, Object> response = (Map<String, Object>) client.execute(config, "device.query", getAuthParamList());
        PluginResultSet resultSet = new PluginResultSet();
        int column = 0;
        resultSet.putValue(0, column++, response.get("apiVersion"));
        resultSet.putValue(0, column++, response.get("shutdownStatus"));
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
        return resultSet;
    }

    public PluginResultSet systemInfo() throws XmlRpcException {
        Map<String, Object> response = (Map<String, Object>) client.execute(config, "system.info", getAuthParamList());
        PluginResultSet resultSet = new PluginResultSet();
        int column = 0;
        resultSet.putValue(0, column++, response.get("operationMode"));
        resultSet.putValue(0, column++, response.get("licenseMode"));
        resultSet.putValue(0, column++, response.get("portsVideoTotal"));
        resultSet.putValue(0, column++, response.get("portsVideoFree"));
        resultSet.putValue(0, column++, response.get("portsAudioTotal"));
        resultSet.putValue(0, column++, response.get("portsAudioFree"));
        resultSet.putValue(0, column++, response.get("portsContentTotal"));
        resultSet.putValue(0, column++, response.get("portsContentFree"));
        resultSet.putValue(0, column++, response.get("makeCallsOK"));
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
        resultSet.putValue(0, column++, system.getChildTextTrim("ipAddress"));
        resultSet.putValue(0, column++, system.getChildTextTrim("ipAddressV6"));
        resultSet.putValue(0, column++, system.getChildTextTrim("macAddress"));
        resultSet.putValue(0, column++, system.getChildTextTrim("totalVideoPorts"));
        resultSet.putValue(0, column++, system.getChildTextTrim("totalAudioOnlyPorts"));
        resultSet.putValue(0, column++, system.getChildTextTrim("totalContentPorts"));
        resultSet.putValue(0, column++, system.getChildTextTrim("uptimeSeconds"));
        resultSet.putValue(0, column++, system.getChildTextTrim("clusterType"));
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
