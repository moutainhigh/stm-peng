package com.mainsteam.stm.plugin.url;

import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSession;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginArrayExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.http.HttpStatus;
import org.apache.http.client.CookieStore;
import org.apache.http.client.config.CookieSpecs;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContexts;
import org.apache.http.ssl.TrustStrategy;

import javax.net.ssl.SSLContext;
import java.io.IOException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

public class UrlPluginSession implements PluginSession {
    private static final Log LOGGER = LogFactory.getLog(UrlPluginSession.class);

    public static final String URL = "urlSite";
    public static final String TIMEOUT = "urlTimeout";

    private static final SSLConnectionSocketFactory factory;

    static {
        SSLConnectionSocketFactory tFactory = SSLConnectionSocketFactory.getSocketFactory();
        try {
            SSLContext context = SSLContexts.custom().useProtocol("TLSv1.2").loadTrustMaterial(new TrustStrategy() {
                @Override
                public boolean isTrusted(X509Certificate[] chain, String authType) throws CertificateException {
                    return true;
                }
            }).build();
            tFactory = new SSLConnectionSocketFactory(context, new NoopHostnameVerifier());
        } catch (Exception e) {
            LOGGER.error("Fail to create trust-all ssl socket factory", e);
        }
        factory = tFactory;
    }

    private CloseableHttpClient client;
    private CookieStore cookieStore;
    private String url;
    private int timeout = 20000;

    @Override
    public void init(PluginInitParameter initParameter) throws PluginSessionRunException {
        for (Parameter parameter : initParameter.getParameters()) {
            switch (parameter.getKey()) {
                case URL:
                    url = parameter.getValue();
                    if (!url.startsWith("http"))
                        url = "http://" + url;
                    break;
                case TIMEOUT:
                    try {
                        timeout = Integer.parseInt(parameter.getValue());
                    } catch (Exception e) {
                        if (LOGGER.isWarnEnabled())
                            LOGGER.warn("Invalid parameter: " + parameter.getKey() + " = " + parameter.getValue());
                    }
            }
        }
        RequestConfig config = RequestConfig.custom().
                setCookieSpec(CookieSpecs.DEFAULT).
                setConnectTimeout(timeout).
                setConnectionRequestTimeout(timeout).
                setSocketTimeout(timeout).
                build();
        cookieStore = new BasicCookieStore();
        client = HttpClients.custom().
                setUserAgent("Mozilla/5.0 (Windows NT 6.1; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/65.0.3325.181 Safari/537.36").
                setDefaultCookieStore(cookieStore).
                setSSLSocketFactory(factory).
                setDefaultRequestConfig(config).
                build();
    }

    @Override
    public boolean isAlive() {
        return true;
    }

    @Override
    public void destory() {
        try {
            client.close();
        } catch (IOException e) {
            LOGGER.error("Fail to close client", e);
        }
    }

    @Override
    public PluginResultSet execute(PluginExecutorParameter<?> executorParameter, PluginSessionContext context) throws PluginSessionRunException {
        PluginResultSet resultSet = new PluginResultSet();
        resultSet.addRow(new String[]{url, "16"});
        if (executorParameter instanceof PluginArrayExecutorParameter) {
            HttpGet httpGet = new HttpGet(url);
//            httpGet.addHeader("Accept-Language", "zh-CN,zh;q=0.8,ja;q=0.6,en;q=0.4,zh-TW;q=0.2\\r\\n");
            long startTime = System.currentTimeMillis();
            try (CloseableHttpResponse response = client.execute(httpGet)) {
                long responseTime = System.currentTimeMillis() - startTime;
                int statusCode = response.getStatusLine().getStatusCode();
                if (LOGGER.isInfoEnabled())
                    LOGGER.info(url + ", statusCode = " + statusCode + ", responseTime = " + responseTime);
//                if(responseTime > timeout){
//                	resultSet.putValue(0, 1, getAvail(504));
//                }else{
                resultSet.putValue(0, 1, getAvail(statusCode));
//                }
                resultSet.putValue(0, 2, responseTime);
            } catch (Exception e) {
                if (LOGGER.isInfoEnabled())
                    LOGGER.info(url + ", fail.", e);
            }
            cookieStore.clear();
        }
        return resultSet;
    }

    private int getAvail(int statusCode) {
        switch (statusCode) {
            case HttpStatus.SC_OK:
            case HttpStatus.SC_ACCEPTED:
            case HttpStatus.SC_NOT_MODIFIED:
                return 1;
            default:
                return 0;
        }
    }

    @Override
    public void reload() {
    }

    @Override
    public boolean check(PluginInitParameter initParameters)
            throws PluginSessionRunException {
        // TODO Auto-generated method stub
        return false;
    }

}
