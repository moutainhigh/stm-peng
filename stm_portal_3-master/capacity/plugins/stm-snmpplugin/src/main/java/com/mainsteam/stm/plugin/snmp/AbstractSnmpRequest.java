package com.mainsteam.stm.plugin.snmp;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.snmp4j.*;
import org.snmp4j.log.Log4jLogFactory;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.*;
import org.snmp4j.smi.Address;
import org.snmp4j.smi.OID;
import org.snmp4j.smi.OctetString;
import org.snmp4j.smi.UdpAddress;
import org.snmp4j.transport.DefaultUdpTransportMapping;

import java.util.Map;

import static com.mainsteam.stm.plugin.snmp.SnmpParamConst.*;

/**
 * Created by Xiaopf on 2017/4/28.
 */
public abstract class AbstractSnmpRequest implements SnmpRequest{

    private static final String DEFAULT_SNMP_PORT = "161";
    private static final String DEFAULT_SNMP_RETRY = "1";
    private static final String DEFAULT_SNMP_TIMEOUT = "10000";

    static { //开启snmp4j日志
        org.snmp4j.log.LogFactory.setLogFactory(new Log4jLogFactory());
    }
    private static final Log logger = LogFactory.getLog(AbstractSnmpRequest.class);

    private TransportMapping<UdpAddress> transportMapping;
    Snmp snmp;
    Target target;

    public AbstractSnmpRequest(Map<String, String> config) {
        try {
            transportMapping = new DefaultUdpTransportMapping();
            org.snmp4j.log.LogFactory.setLogFactory(Log4jLogFactory.getLogFactory());
            ((DefaultUdpTransportMapping) transportMapping).setReceiveBufferSize(50 * 1024 * 1024); // 50MB
            USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(
                    MPv3.createLocalEngineID(new OctetString("MyUniqueID" + System.currentTimeMillis()))), 0);
            SecurityModels.getInstance().addSecurityModel(usm);
            snmp = new Snmp();
            snmp.addTransportMapping(transportMapping);
            SecurityProtocols.getInstance().addDefaultProtocols();
            snmp.getMessageDispatcher().addMessageProcessingModel(new MPv1());
            snmp.getMessageDispatcher().addMessageProcessingModel(new MPv2c());
            snmp.getMessageDispatcher().addMessageProcessingModel(new MPv3(usm));
            snmp.getMessageDispatcher().addCommandResponder(snmp);
            snmp.listen();
            target = configTarget(config);

        } catch (Exception e) {
            logger.error(e.getMessage(), e);
        }
    }

    /**
     * 设置SNMP Target配置属性
     * @param config
     * @return
     * @throws Exception
     */
     Target configTarget(Map<String, String> config) throws Exception {
        try {

            int snmpVersion = Integer.valueOf(config.get(SNMPPLUGIN_SNMP_VERSION));
            if (snmpVersion == SnmpConstants.version3) { // snmpv3
                target = new UserTarget();
                final int securityLevel = Integer.valueOf(config.get(SNMPPLUGIN_SNMP_SECURITYLEVEL));
                target.setSecurityLevel(securityLevel);
                final OctetString securityName = new OctetString(config.get(SNMPPLUGIN_SNMP_SECURITYNAME));
                OctetString authPassphrase = null;
                OctetString privacyPassphrase = null;
                OID authProtocol = null;
                OID privacyProtocol = null;
                // 认证加密
                String authPassPhraseStr = config.get(SNMPPLUGIN_SNMP_AUTHPASSPHRASE);
                String authProtocolStr = config.get(SNMPPLUGIN_SNMP_AUTHPROTOCOL);
                if (StringUtils.isNotBlank(authPassPhraseStr) && securityLevel != SecurityLevel.NOAUTH_NOPRIV) {
                    authPassphrase = new OctetString(authPassPhraseStr);
                    if (StringUtils.equalsIgnoreCase(authProtocolStr, MD5)) {
                        authProtocol = AuthMD5.ID;
                    } else if (StringUtils.equalsIgnoreCase(authProtocolStr, SHA)) {
                        authProtocol = AuthSHA.ID;
                    }
                }
                // 加密算法
                String privacyPassphraseStr = config.get(SNMPPLUGIN_SNMP_PRIVACYPASSPHRASE);
                if (StringUtils.isNotBlank(privacyPassphraseStr) && securityLevel == SecurityLevel.AUTH_PRIV) {

                    privacyPassphrase = new OctetString(privacyPassphraseStr);
                    String privacyProtocolStr = config.get(SNMPPLUGIN_SNMP_PRIVACYPROTOCOL);
                    switch (privacyProtocolStr) {
                        case DES:
                            privacyProtocol = PrivDES.ID;
                            break;
                        case _3DES:
                            privacyProtocol = Priv3DES.ID;
                            break;
                        case AES128:
                            privacyProtocol = PrivAES128.ID;
                            break;
                        case AES192:
                            privacyProtocol = PrivAES192.ID;
                            break;
                        case AES256:
                            privacyProtocol = PrivAES256.ID;
                            break;
                    }

                }
                target.setSecurityName(securityName);
            /* If the specified SNMP engine id is specified, this user can only
            be used with the specified engine ID
            So if it's not correct, will get an error that can't find a user from the user table.
            */
                UsmUser user = new UsmUser(securityName, authProtocol, authPassphrase, privacyProtocol, privacyPassphrase);
                snmp.getUSM().addUser(securityName, user);

            } else {
                target = new CommunityTarget();
                ((CommunityTarget) target).setCommunity(new OctetString(config.get(SNMPPLUGIN_COMMUNITY)));
            }
            String port = config.get(SNMPPLUGIN_PORT);
            if(StringUtils.isBlank(port))
                port = DEFAULT_SNMP_PORT;
            Address address = new UdpAddress(config.get(SNMPPLUGIN_IP) + "/" + port);
            target.setAddress(address);
            String snmpRetry = config.get(SNMPPLUGIN_SNMP_RETRY);
            if(StringUtils.isBlank(snmpRetry))
                snmpRetry = DEFAULT_SNMP_RETRY;
            target.setRetries(Integer.valueOf(snmpRetry));
            String snmpTimeout = config.get(SNMPPLUGIN_SNMP_TIMEOUT);
            if(StringUtils.isBlank(snmpTimeout))
                snmpTimeout = DEFAULT_SNMP_TIMEOUT;
            target.setTimeout(Long.valueOf(snmpTimeout));
            target.setVersion(snmpVersion);

            return target;
        }catch (Exception e) {
            throw e;
        }
    }

    @Override
    public void close() {
        try{
            target = null;
            if(null != snmp){
                snmp.close();
                snmp = null;
            }
            transportMapping = null;
        }catch (Exception e){
            if(logger.isErrorEnabled()) {
                logger.error(e.getMessage(), e);
            }
            target = null;
            snmp = null;
            transportMapping = null;
        }
    }
}
