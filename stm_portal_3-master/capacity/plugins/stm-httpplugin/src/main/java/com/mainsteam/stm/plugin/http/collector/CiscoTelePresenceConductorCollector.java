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
import org.jdom2.filter.Filters;
import org.jdom2.input.SAXBuilder;
import org.jdom2.xpath.XPathExpression;
import org.jdom2.xpath.XPathFactory;
import sun.misc.BASE64Encoder;

import javax.net.ssl.*;
import javax.xml.parsers.SAXParserFactory;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author lich
 */
public class CiscoTelePresenceConductorCollector implements HttpCollector {

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
    private static final Log LOGGER = LogFactory.getLog(CiscoTelePresenceConductorCollector.class);

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

    private XmlRpcClientConfigImpl config;
    private String protocol;
    private String ip;
    private int port;
    private String username;
    private String password;
    private String encoding;
    private int timeout;


    public PluginResultSet deviceQuery() throws XmlRpcException {
        Map<String, Object> response = (Map<String, Object>) client.execute(config, "device.query", getAuthParamList());
        PluginResultSet resultSet = new PluginResultSet();
        int column = 0;
        resultSet.putValue(0, column++, response.get("model"));
        resultSet.putValue(0, column++, response.get("serial"));
        resultSet.putValue(0, column++, response.get("softwareVersion"));
        resultSet.putValue(0, column++, response.get("buildVersion"));
        resultSet.putValue(0, column++, response.get("apiVersion"));
        resultSet.putValue(0, column++, response.get("uptimeSeconds"));
        return resultSet;
    }

    public PluginResultSet systemUnitXml() throws IOException, JDOMException {
        Document document = getDocument("/systemunit.xml");
        XPathExpression<Element> expression = XPathFactory.instance().compile("/SystemUnit/Name", Filters.element());
        Element name = expression.evaluateFirst(document);
        PluginResultSet resultSet = new PluginResultSet();
        resultSet.putValue(0, 0, name.getTextTrim());
        return resultSet;
    }

    public PluginResultSet statusXml() throws IOException, JDOMException {
        Document document = getDocument("/status");
        XPathExpression<Element> expression = XPathFactory.instance().compile("/status/info", Filters.element());
        Element info = expression.evaluateFirst(document);
        PluginResultSet resultSet = new PluginResultSet();
        resultSet.putValue(0, 0, info.getTextTrim());
        return resultSet;
    }

    public PluginResultSet networkInterfaceXml() throws IOException, JDOMException {
        Document document = getDocument("/api/external/status/networkinterface?peer=local");
        XPathExpression<Element> expression = XPathFactory.instance().compile("/array/peerResults/records/array/networkInterfaceStatus", Filters.element());
        List<Element> networkInterfaceList = expression.evaluate(document);
        PluginResultSet resultSet = new PluginResultSet();
        int row = 0;
        for (Element networkInterface : networkInterfaceList) {
            int column = 0;
            resultSet.putValue(row, column++, networkInterface.getChildTextTrim("uuid"));
            resultSet.putValue(row, column++, networkInterface.getChildTextTrim("name"));
            resultSet.putValue(row, column++, networkInterface.getChildTextTrim("device"));
            resultSet.putValue(row, column++, networkInterface.getChildTextTrim("type"));
            resultSet.putValue(row, column++, networkInterface.getChildTextTrim("mac_address"));
            resultSet.putValue(row++, column++, networkInterface.getChildTextTrim("ipv4_address"));
        }
        return resultSet;
    }

    public PluginResultSet clusterPeerXml() throws IOException, JDOMException {
        Document document = getDocument("/api/external/status/clusterpeer?peer=local");
        XPathExpression<Element> expression = XPathFactory.instance().compile("/array/peerResults/records/array/clusterPeerStatus", Filters.element());
        List<Element> clusterPeerList = expression.evaluate(document);
        PluginResultSet resultSet = new PluginResultSet();
        int row = 0;
        for (Element clusterPeer : clusterPeerList) {
            int column = 0;
            resultSet.putValue(row, column++, clusterPeer.getChildTextTrim("uuid"));
            resultSet.putValue(row, column++, clusterPeer.getChildTextTrim("peer"));
            resultSet.putValue(row++, column++, clusterPeer.getChildTextTrim("state"));
        }
        return resultSet;
    }

    private Document getDocument(String file) throws IOException, JDOMException {
        URL url = new URL(protocol, ip, port, file);
        URLConnection connection = url.openConnection();
        connection.setRequestProperty("Accept", "application/xml");
        connection.setConnectTimeout(timeout);
        connection.setReadTimeout(timeout);
        if (username != null) {
            String s = username + ':';
            if (password != null)
                s += password;
            BASE64Encoder encoder = new BASE64Encoder();
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            encoder.encode(s.getBytes(encoding), out);
            String auth = out.toString(encoding);
            connection.setRequestProperty("Authorization", "Basic " + auth);
        }
        SAXBuilder builder = new SAXBuilder();
        Document document = builder.build(new InputStreamReader(connection.getInputStream(), encoding));
        return document;
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
        protocol = initMap.get(PARAM_PROTOCOL);
        ip = initMap.get(PARAM_IP);
        port = Integer.valueOf(initMap.get(PARAM_PORT));
        username = StringUtils.isNotEmpty(initMap.get(PARAM_USERNAME)) ? initMap.get(PARAM_USERNAME) : null;
        password = StringUtils.isNotEmpty(initMap.get(PARAM_PASSWORD)) ? initMap.get(PARAM_PASSWORD) : null;
        encoding = StringUtils.isNotEmpty(initMap.get(PARAM_ENCODING)) ? initMap.get(PARAM_ENCODING) : DEFAULT_ENCODING;
        timeout = StringUtils.isNotEmpty(initMap.get(PARAM_TIMEOUT)) ? Integer.valueOf(initMap.get(PARAM_TIMEOUT)) : DEFAULT_TIMEOUT;
        URL serverURL = new URL(protocol, ip, port, "/RPC2");
        config = new XmlRpcClientConfigImpl();
        config.setServerURL(serverURL);
        config.setEncoding(encoding);
        config.setConnectionTimeout(timeout);
        config.setReplyTimeout(timeout);
    }

    @Override
    public void close() {

    }
}
