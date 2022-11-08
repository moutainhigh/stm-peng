package com.mainsteam.stm.plugin.http.collector;

import com.mainsteam.stm.pluginsession.PluginResultSet;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.Namespace;
import org.jdom2.input.SAXBuilder;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.List;
import java.util.Map;

/**
 * @author lich
 */
public class CiscoTelePresenceContentServerCollector implements HttpCollector {

    public static final String PARAM_IP = "IP";
    public static final String PARAM_PORT = "port";
    public static final String PARAM_PROTOCOL = "protocol";
    public static final String PARAM_USERNAME = "username";
    public static final String PARAM_PASSWORD = "password";
    public static final String PARAM_ENCODING = "encoding";
    public static final String PARAM_TIMEOUT = "timeout";

    private static final int DEFAULT_TIMEOUT = 5000;
    private static final String DEFAULT_ENCODING = "UTF-8";
    private static final Log LOGGER = LogFactory.getLog(CiscoTelePresenceContentServerCollector.class);
    private static final SSLConnectionSocketFactory factory;

    static {
        SSLConnectionSocketFactory tFactory = SSLConnectionSocketFactory.getSocketFactory();
        try {
            SSLContext context = SSLContexts.custom().loadTrustMaterial(new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            tFactory = new SSLConnectionSocketFactory(context, new NoopHostnameVerifier());
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        factory = tFactory;
    }

    private CloseableHttpClient client;
    private String protocol;
    private String ip;
    private int port;
    private String username;
    private String password;
    private String encoding;
    private int timeout;

    public PluginResultSet statusXml() throws IOException, JDOMException {
        Document document = getDocument("/tcs/status.xml");
        PluginResultSet resultSet = new PluginResultSet();
        int row = 0;
        int column = 0;

        Element status = document.getRootElement();
        Namespace namespace = status.getNamespace();

        Element systemUnit = status.getChild("SystemUnit", namespace);
        Element software = systemUnit.getChild("Software", namespace);
        resultSet.putValue(row, column++, software.getChildTextTrim("Name", namespace));
        Element versionInfo = software.getChild("VersionInfo", namespace);
        resultSet.putValue(row, column++, versionInfo.getChildTextTrim("MajorVersion", namespace));
        resultSet.putValue(row, column++, versionInfo.getChildTextTrim("MinorVersion", namespace));
        resultSet.putValue(row, column++, versionInfo.getChildTextTrim("BuildVersion", namespace));
        Element hardware = systemUnit.getChild("Hardware", namespace);
        resultSet.putValue(row, column++, hardware.getChildTextTrim("SerialNumber", namespace));
        resultSet.putValue(row, column++, systemUnit.getChildTextTrim("OfflineTranscodingState", namespace));

        Element h323Gatekeeper = status.getChild("H323Gatekeeper", namespace);
        resultSet.putValue(row, column++, h323Gatekeeper.getChildTextTrim("Status", namespace));
        resultSet.putValue(row, column++, h323Gatekeeper.getChildTextTrim("ActiveIP", namespace));

        Element sip = status.getChild("SIP", namespace);
        resultSet.putValue(row, column++, sip.getChildTextTrim("Status", namespace));
        resultSet.putValue(row, column++, sip.getChildTextTrim("ActiveIP", namespace));

        Element driveInfo = status.getChild("DriveInfo", namespace);
        List<Element> drives = driveInfo.getChildren("Drive", namespace);
        for (Element drive : drives) {
            resultSet.putValue(row, column, drive.getChildTextTrim("DriveLetter", namespace));
            resultSet.putValue(row, column + 1, drive.getChildTextTrim("Capacity", namespace));
            resultSet.putValue(row, column + 2, drive.getChildTextTrim("Used", namespace));
            resultSet.putValue(row++, column + 3, drive.getChildTextTrim("Free", namespace));
        }
        row = 0;
        column += 4;

        resultSet.putValue(row, column++, status.getChildTextTrim("CpuLoad", namespace));

        Element memory = status.getChild("Memory", namespace);
        resultSet.putValue(row, column++, memory.getChildTextTrim("TotalPhysical", namespace));
        resultSet.putValue(row, column++, memory.getChildTextTrim("FreePhysical", namespace));

        resultSet.putValue(row, column++, status.getChildTextTrim("MacAddress", namespace));
        resultSet.putValue(row, column++, status.getChildTextTrim("Uptime", namespace));

        Element capacity = status.getChild("Capacity", namespace);
        resultSet.putValue(row, column++, capacity.getChildTextTrim("CurrentCalls", namespace));
        resultSet.putValue(row, column++, capacity.getChildTextTrim("MaxCalls", namespace));
        resultSet.putValue(row, column++, capacity.getChildTextTrim("CurrentLiveCalls", namespace));
        resultSet.putValue(row, column++, capacity.getChildTextTrim("MaxLiveCalls", namespace));

        return resultSet;
    }

    public PluginResultSet clusterStatusXml() throws IOException, JDOMException {
        Document document = getDocument("/tcs/clusterstatus.xml");
        PluginResultSet resultSet = new PluginResultSet();
        int row = 0;
        int column = 0;

        Element clusterStatus = document.getRootElement();
        Namespace namespace = clusterStatus.getNamespace();

        List<Element> nodeList = clusterStatus.getChildren("Node", namespace);
        for (Element node : nodeList) {
            if (ip.equals(node.getChildTextTrim("IP", namespace))) {
                resultSet.putValue(row, column++, node.getChildTextTrim("IP", namespace));
                resultSet.putValue(row, column++, node.getChildTextTrim("ContentEngineStatus", namespace));
                break;
            }
        }

        return resultSet;
    }

    private Document getDocument(String path) throws IOException, JDOMException {
        HttpGet httpGet;
        try {
            httpGet = new HttpGet(new URIBuilder().setScheme(protocol)
                    .setHost(ip)
                    .setPort(port)
                    .setPath(path).build());
        } catch (URISyntaxException e) {
            throw new IllegalArgumentException("Invalid path: " + path, e);
        }
        CloseableHttpResponse response = null;
        try {
            response = client.execute(httpGet);
            if (response.getStatusLine().getStatusCode() == HttpStatus.SC_UNAUTHORIZED) {
                response.close();
                response = client.execute(httpGet);
            }
            SAXBuilder builder = new SAXBuilder();
            Document document = builder.build((new InputStreamReader(response.getEntity().getContent(), encoding)));
            response.close();
            return document;
        } finally {
            if (response != null) {
                response.close();
            }
        }
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

        CredentialsProvider provider = new BasicCredentialsProvider();
        provider.setCredentials(
                new AuthScope(ip, port),
                new UsernamePasswordCredentials(username, password));

        RequestConfig config = RequestConfig.custom().
                setConnectTimeout(timeout).
                setConnectionRequestTimeout(timeout).
                build();

        client = HttpClients.custom().
                setSSLSocketFactory(factory).
                setDefaultCredentialsProvider(provider).
                setDefaultRequestConfig(config).
                build();
    }

    @Override
    public void close() {
        if (client == null)
            return;
        try {
            client.close();
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }
}
