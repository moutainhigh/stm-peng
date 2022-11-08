/**
 *
 */
package com.mainsteam.stm.plugin.snmp;

import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.snmp4j.*;
import org.snmp4j.event.ResponseEvent;
import org.snmp4j.log.Log4jLogFactory;
import org.snmp4j.mp.MPv1;
import org.snmp4j.mp.MPv2c;
import org.snmp4j.mp.MPv3;
import org.snmp4j.mp.SnmpConstants;
import org.snmp4j.security.*;
import org.snmp4j.smi.*;
import org.snmp4j.transport.DefaultUdpTransportMapping;
import org.snmp4j.util.*;

import java.io.IOException;
import java.util.*;

/**
 * @author xiaop_000
 */
@Deprecated
public class SnmpAgent {

    /**
     * SNMP Walk 代码
     */
    public static final int PDU_CODE_WALK = -1027;
    public static final int GETSUBTREE = -92;
    public static final int GETTABLE = -93;
    /**
     * 不正确尝试次数
     */
    public static final int INVALID_TRY_COUNT = 1;
    /**
     * 针对snmpget的行为的返回值，将oid中的索引提取出来，放到返回值的该key的值里。
     */
    public static final String INDEX_COLUMN_NAME = "snmp_index";
    private static final int NONREPEATERS = 0;
    private static final int MAXREPETITIONS = 10;
    private static final String AES256 = "aes256";
    private static final String AES192 = "aes192";
    private static final String AES128 = "aes128";
    private static final String _3DES = "3des";
    private static final String DES = "des";
    private static final String SHA = "sha";
    private static final String MD5 = "md5";
    private static final char CHAR = '.';
    private static final String GBK = "GBK";
    private static final String A1_67 = "a1:67";
    private static final Log logger = LogFactory.getLog(SnmpAgent.class);
    private static final String REGEX_TEXT = ":";
    /**
     * 十六进制整数
     */
    private static final int HEX_TEXT = 16;
    /**
     * walk成功
     */
    private static final int WALK_SUCCESS = 0;
    /**
     * walk超时
     */
    private static final int WALK_TIMEOUT = 2;
    /**
     * OID无效
     */
    private static final int WALK_OID_INVALID = 1;
    private static SnmpAgent _self = new SnmpAgent();
    private static String[] convertToString = new String[]{
            "1.3.6.1.2.1.2.2.1.2.", "1.3.6.1.2.1.25.2.3.1.3.",
            "1.3.6.1.2.1.1.5.", "1.3.6.1.2.1.17.7.1.4.3.1.1.",
            "1.3.6.1.2.1.31.1.1.1.18.", "1.3.6.1.4.1.4881.1.1.10.2.9.1.7.1.3.",
            "1.3.6.1.4.1.4881.1.1.10.2.9.1.5.1.4."};

    /**
     * SNMPAgent单例
     */
    static {
        org.snmp4j.log.LogFactory.setLogFactory(new Log4jLogFactory());
        //Log4jLogAdapter.setDebugEnabled(true);
    }

    private TransportMapping<UdpAddress> transportMapping;
    private Snmp snmp = null;
    private PDUFactory getNextPDUFactory;
    private PDUFactory getBulkPDUFactory;

    public SnmpAgent() {
        try {
            org.snmp4j.log.LogFactory.setLogFactory(Log4jLogFactory
                    .getLogFactory());
            transportMapping = new DefaultUdpTransportMapping();
            ((DefaultUdpTransportMapping) transportMapping)
                    .setReceiveBufferSize(50 * 1024 * 1024); // 50MB
            USM usm = new USM(SecurityProtocols.getInstance(), new OctetString(
                    MPv3.createLocalEngineID(new OctetString("MyUniqueID" + System.currentTimeMillis()))), 0);
            SecurityModels.getInstance().addSecurityModel(usm);
//            transportMapping.listen();
//            snmp = new Snmp(transportMapping);
            snmp = new Snmp();
            snmp.addTransportMapping(transportMapping);
            SecurityProtocols.getInstance().addDefaultProtocols();
            snmp.getMessageDispatcher().addMessageProcessingModel(new MPv1());
            snmp.getMessageDispatcher().addMessageProcessingModel(new MPv2c());
            snmp.getMessageDispatcher().addMessageProcessingModel(new MPv3(usm));
            snmp.getMessageDispatcher().addCommandResponder(snmp);
            snmp.listen();

            getNextPDUFactory = new DefaultPDUFactory(PDU.GETNEXT);
            getBulkPDUFactory = new DefaultPDUFactory(PDU.GETBULK);
        } catch (IOException e) {
            logger.error(e.getMessage(), e);
        }
    }

//	public static SnmpAgent getInstance() {
//		return _self;
//	}

    public static void main(String[] args) throws IOException {
        SnmpParameter p = new SnmpParameter();
        p.setIp("172.16.7.73");
        p.setPort(161);
        // p.setCommunity("public");
        p.setOids(new String[]{"1.3.6.1.2.1.2.2.1.190:0"}); // "1.3.6.1.2.1.1.3.0"
        p.setCommunity("public");
        // p.setAuthProtocol(SHA);
        // p.setAuthPassphrase("password123");
        // p.setPrivacyProtocol(AES192);
        // p.setPrivacyPassphrase("password321");
        // p.setSecurityName("snmpv3AuthNoPriv");
        // p.setSecurityLevel(SecurityLevel.AUTH_NOPRIV);
        // p.setVersion(SnmpConstants.version3);
        // 1.3.6.1.2.1.2.2.1.6
        // SnmpAgent.getInstance().getSnmpResult(p, PDU.GETNEXT);
        // p.setOids(new String[]
        // {"1.3.6.1.4.1.9.9.109.1.1.1.1.4","1.3.6.1.4.1.9.9.109.1.1.1.1.4.1"});//
        // "1.3.6.1.2.1.1.3.0"
//		SnmpAgent.getInstance().getSnmpResult(p);

    }

    /**
     * 执行采集入口
     *
     * @param p
     * @return
     * @throws IOException
     */
    public Object getSnmpResult(SnmpParameter p) throws Exception {
        Target target = null;

        if (p.getVersion() == SnmpConstants.version3) { // snmpv3
            target = new UserTarget();
            target.setSecurityLevel(p.getSecurityLevel());
            final OctetString securityName = new OctetString(
                    p.getSecurityName());
            OctetString authPassphrase = null;
            OctetString privacyPassphrase = null;
            OID authProtocol = null;
            OID privacyProtocol = null;
            // 认证加密
            if (StringUtils.isNotBlank(p.getAuthPassphrase())
                    && p.getSecurityLevel() != SecurityLevel.NOAUTH_NOPRIV) {
                if (logger.isInfoEnabled()) {
                    logger.info("snmp v3 starts to authPassphrase. ip/port:"
                            + p.ip + "/" + p.port);
                }
                authPassphrase = new OctetString(p.getAuthPassphrase());
                if (StringUtils.equalsIgnoreCase(p.getAuthProtocol(), MD5)) {
                    authProtocol = AuthMD5.ID;
                } else if (StringUtils.equalsIgnoreCase(p.getAuthProtocol(),
                        SHA)) {
                    authProtocol = AuthSHA.ID;
                }
            }
            // 加密算法
            if (StringUtils.isNotBlank(p.getPrivacyPassphrase())
                    && p.getSecurityLevel() == SecurityLevel.AUTH_PRIV) {
                if (logger.isInfoEnabled()) {
                    logger.info("snmp v3 starts to privacyPassphrase. ip/port:"
                            + p.ip + "/" + p.port);
                }
                privacyPassphrase = new OctetString(p.getPrivacyPassphrase());
                if (StringUtils.equalsIgnoreCase(p.getPrivacyProtocol(), DES)) {
                    privacyProtocol = PrivDES.ID;
                } else if (StringUtils.equalsIgnoreCase(p.getPrivacyProtocol(),
                        _3DES)) {
                    privacyProtocol = Priv3DES.ID;
                } else if (StringUtils.equalsIgnoreCase(p.getPrivacyProtocol(),
                        AES128)) {
                    privacyProtocol = PrivAES128.ID;
                } else if (StringUtils.equalsIgnoreCase(p.getPrivacyProtocol(),
                        AES192)) {
                    privacyProtocol = PrivAES192.ID;
                } else if (StringUtils.equalsIgnoreCase(p.getPrivacyProtocol(),
                        AES256)) {
                    privacyProtocol = PrivAES256.ID;
                }
            }
            target.setSecurityName(securityName);
            // If the specified SNMP engine id is specified, this user can only
            // be used with the specified engine ID
            // So if it's not correct, will get an error that can't find a user
            // from the user table.
            UsmUser user = new UsmUser(securityName, authProtocol,
                    authPassphrase, privacyProtocol, privacyPassphrase);
            snmp.getUSM().addUser(securityName, user);

        } else {// snmpv1 or v2c
            target = new CommunityTarget();
            ((CommunityTarget) target).setCommunity(new OctetString(p
                    .getCommunity()));
        }
        Address address = new UdpAddress(p.getIp() + "/" + p.getPort());
        if (logger.isDebugEnabled()) {
            logger.debug("SNMP getSnmpResult ip=" + p.getIp() + "address="
                    + address);
        }
        target.setAddress(address);
        // 通信不成功时的重试次数
        target.setRetries(p.getSnmpRetry());
        // 超时时间
        target.setTimeout(p.getSnmpTimeout());
        // 版本
        target.setVersion(p.getVersion());

        Map<String, List<String>> oidResultValueMaps = new HashMap<String, List<String>>();
        // set PDU
        String[] oids = p.getOids();
        if (p.getPduCode() == PDU.SET) {
            PDU pdu = (p.getVersion() == SnmpConstants.version3) ? (new ScopedPDU()) : (new PDU());
            pdu.setType(PDU.SET);
            String[] variables = p.getVariables();
            String[] types = p.getTypes();
            for (int i = 0; i < oids.length; ++i) {
                String oid = oids[i];
                String variable = variables[i];
                String type = types[i];
                pdu.add(new VariableBinding(new OID(oid), getVariable(variable, type)));
            }
            ResponseEvent responseEvent = snmp.send(pdu, target);
            if (responseEvent.getError() != null)
                throw responseEvent.getError();
            PDU response = responseEvent.getResponse();
            if (response.getErrorStatus() != PDU.noError)
                throw new RuntimeException(response.getErrorStatus() + ":" + response.getErrorStatusText());
            List<? extends VariableBinding> variableBindings = response.getVariableBindings();
            for (VariableBinding variableBinding : variableBindings) {
                if (!variableBinding.isException()) {
                    String oid = variableBinding.getOid().toString();
                    if (oidResultValueMaps.get(oid) == null)
                        oidResultValueMaps.put(oid, new ArrayList<String>());
                    List<String> result = oidResultValueMaps.get(oid);
                    result.add(variableBinding.getVariable().toString());
                }
            }
        } else if (p.getPduCode() == PDU.GET || p.getPduCode() == PDU.GETNEXT) {
            for (int i = 0; i < oids.length; i++) {
                // Get或者GetNext分别去请求,因为可能存在多个oid以：进行顺序尝试的情况
                String[] singleOids = oids[i].split(REGEX_TEXT);
                for (String singleOid : singleOids) {
                    if (singleOid.matches("(\\d+\\.)+\\d+")) {
                        PDU pdu = (p.getVersion() == SnmpConstants.version3) ? (new ScopedPDU())
                                : (new PDU());
                        pdu.add(new VariableBinding(new OID(singleOid)));
                        pdu.setType(p.getPduCode());
                        boolean isSuccess = snmpGetOrGetNext(pdu, target,
                                oidResultValueMaps, singleOid, oids[i]);
                        List<String> oidListValuesList = oidResultValueMaps
                                .get(oids[i]);
                        if (oidListValuesList != null) {
                            for (Iterator<String> iterator = oidListValuesList
                                    .iterator(); iterator.hasNext(); ) {
                                String string = iterator.next();
                                if (StringUtils.isBlank(string)) {
                                    iterator.remove();
                                }
                            }
                        }
                        if (isSuccess) {
                            continue;
                        }

                    } else { // 设置默认值
                        List<String> values = new ArrayList<String>(1);
                        values.add(singleOid);
                        oidResultValueMaps.put(oids[i], values);
                    }

                }
            }

        } else if (p.getPduCode() == PDU.GETBULK) {// snmp块读取
            List<String> list = new ArrayList<String>(oids.length);
            Collections.addAll(list, oids);
            Map<String, Map<String, String>> datas = new HashMap<String, Map<String, String>>();
            snmpGetBulk(target, datas, list, oids);
            if (!datas.isEmpty()) {
                Set<String> keySet = datas.keySet();
                Iterator<String> iterator = keySet.iterator();
                while (iterator.hasNext()) {
                    String next = iterator.next();
                    if (StringUtils.isNotBlank(next)) {
                        try {
                            Collection<String> collection = datas.get(next)
                                    .values();
                            List<String> valueList = new ArrayList<String>(
                                    collection);
                            oidResultValueMaps.put(next, valueList);
                        } catch (Exception e) {
                            if (logger.isWarnEnabled()) {
                                logger.warn(
                                        "SNMP bulk data operation exception."
                                                + p.toString(), e);
                            }
                            continue;
                        }

                    }
                }

            } else {
                if (logger.isWarnEnabled()) {
                    logger.warn("SNMP get no datas with bulk operation.Reference parameters is "
                            + p.toString());
                }
            }

        } else if (p.getPduCode() == GETSUBTREE) {
            for (String oid : oids) {
                getSubtree(target, oid, oidResultValueMaps);
            }
        } else if (p.getPduCode() == GETTABLE) {
            return getTable(target, oids);
        } else {
            snmpWalk(target, oidResultValueMaps, oids);
        }
        if (logger.isDebugEnabled()) {
            logger.debug("SNMP getSnmpResult map=" + oidResultValueMaps
                    + ".Reference parameters is " + p.toString());
        }
        return oidResultValueMaps;
    }

    private Variable getVariable(String variable, String type) {
        switch (type) {
            case "OctetString":
                return new OctetString(variable);
            case "Counter32":
                return new Counter32(Long.valueOf(variable));
            case "Counter64":
                return new Counter64(Long.valueOf(variable));
            case "Integer32":
                return new Integer32(Integer.valueOf(variable));
            case "Gauge32":
                return new Gauge32(Long.valueOf(variable));
            default:
                throw new IllegalArgumentException("Invalid type: " + type);
        }
    }

    /**
     * snmp get or getnext operation
     *
     * @param pdu
     * @param target
     * @param oidResultValueMaps
     * @param requestOid
     * @param initOid
     * @return
     * @throws IOException
     */
    private boolean snmpGetOrGetNext(PDU pdu, Target target,
                                     Map<String, List<String>> oidResultValueMaps, String requestOid,
                                     String initOid) throws IOException {
        // 向Agent发送PDU，并返回Response
        ResponseEvent e = snmp.send(pdu, target);
        if (e == null || e.getResponse() == null) {
            if (logger.isWarnEnabled()) {
                logger.warn("requests timeout,oid is " + requestOid
                        + ",retry soon.ip is" + target.getAddress().toString());
            }
            // 重新采集一次
            ResponseEvent again = snmp.send(pdu, target);
            if (again == null || again.getResponse() == null) {
                if (logger.isWarnEnabled()) {
                    logger.warn("request timeout again,oid is " + requestOid
                            + ",ip is " + target.getAddress().toString());
                }
                return false;
            }

        }
        if (e != null && e.getResponse() != null) {
            Vector<? extends VariableBinding> bindings = e.getResponse()
                    .getVariableBindings();
            resolveSnmpReturnValue(oidResultValueMaps, pdu.getType(), bindings,
                    requestOid, initOid);
            return true;
        } else {
            return false;
        }

    }

    /**
     * SNMP walk方法,只walk一层节点
     *
     * @param target
     * @param oidResultValueMaps
     * @param oids
     * @throws IOException
     */
    private void snmpWalk(Target target,
                          Map<String, List<String>> oidResultValueMaps, String[] oids)
            throws IOException {
        // Walk多个oid
        for (int k = 0; k < oids.length; k++) {
            // 可能存在多个oid以：进行分割的情况
            // if(StringUtils.equals(oids[k],
            // "1.3.6.1.2.1.31.1.1.1.2:1.3.6.1.2.1.31.1.1.1.8") && oids.length
            // == 1){
            // System.out.println("1.3.6.1.2.1.31.1.1.1.2:1.3.6.1.2.1.31.1.1.1.8");
            // }
            String[] singleOids = oids[k].split(REGEX_TEXT);
            /*
             * 关于默认值的设置问题。如果是oid无效的情况下，则设置默认值。如果是因为超时等问题，设置默认值为null。
			 * successType: 0表示成功；1表示oid无效；2表示walk超时等问题
			 */
            int successType = WALK_SUCCESS; // snmp walk是否成功
            for (String singleOid : singleOids) {
                int getNextCount = 0; // 用于统计执行GetNext的操作次数。一般情况下至少两次操作才能判断操作正常结束。如果只有一次即结束，表明该oid无效。需要设置默认值
                if (singleOid.matches("(\\d+\\.)+\\d+")) {

                    PDU requestPDU = null;
                    if (target.getVersion() == SnmpConstants.version3) {// snmpv3
                        requestPDU = new ScopedPDU();
                    } else
                        requestPDU = new PDU();
                    requestPDU.add(new VariableBinding(new OID(singleOid)));
                    OID targetOID = requestPDU.get(0).getOid();
                    requestPDU.setType(PDU.GETNEXT);
                    try {
                        boolean finished = false;
                        while (!finished) {
                            ResponseEvent responseEvent = snmp.send(requestPDU,
                                    target);
                            getNextCount++;
                            if (responseEvent != null
                                    && responseEvent.getResponse() != null) {
                                PDU responsePDU = responseEvent.getResponse();
                                VariableBinding firstColumnVar = responsePDU
                                        .get(0);
                                finished = isEndWalk(firstColumnVar, targetOID,
                                        responsePDU);
                                if (!finished) {
                                    // Dump response.
                                    Vector<? extends VariableBinding> bindings = responsePDU
                                            .getVariableBindings();
                                    // Set up the variable binding for the next
                                    // entry.
                                    resolveSnmpReturnValue(oidResultValueMaps,
                                            PDU.GETNEXT, bindings, singleOid,
                                            oids[k]);

                                    requestPDU.setRequestID(new Integer32(0));
                                    StringBuffer debugInfo = new StringBuffer();
                                    debugInfo.append("Request id is "
                                            + responsePDU.getRequestID()
                                            + ".address is "
                                            + target.getAddress().toString()
                                            + ". Request oid is "
                                            + targetOID.toString()
                                            + ". Orginal data is \n[");
                                    for (int i = 0; i < bindings.size(); i++) {
                                        VariableBinding variableBinding = bindings
                                                .get(i);
                                        debugInfo
                                                .append(variableBinding
                                                        .getOid().toString())
                                                .append(" : ")
                                                .append(variableBinding
                                                        .getVariable()
                                                        .toString())
                                                .append("\n");
                                        requestPDU.set(i, variableBinding);
                                    }
                                    if (logger.isDebugEnabled()) {
                                        logger.debug(debugInfo.append("].")
                                                .toString());
                                    }
                                } else if (getNextCount == INVALID_TRY_COUNT) {// 表示只get
                                    // next一次即结束，oid无效
                                    if (logger.isWarnEnabled()) {
                                        logger.warn("Current oid is invalid or maybe use the 'get' operation retrying. The oid is ["
                                                + singleOid
                                                + "] ip:"
                                                + target.getAddress()
                                                .toString());
                                    }
                                    successType = WALK_OID_INVALID;
                                }

                            } else {
                                if (logger.isWarnEnabled()) {
                                    logger.warn("snmpWalk responsePDU == null,request timeout,OID is "
                                            + targetOID.toString()
                                            + ","
                                            + "Address is "
                                            + target.getAddress().toString());
                                }
                                successType = WALK_TIMEOUT;
                                finished = true;
                            }

                        }

                    } catch (IOException e) {
                        if (logger.isErrorEnabled()) {
                            logger.error("snmpWalk error! Address:"
                                    + target.getAddress().toString(), e);
                        }
                        throw e;
                    }
                } else { // 设置默认值,再有默认值的情况下
                    // 索引值个数,根据索引值得个数来设置默认值的个数
                    int indexSize = 1;
                    if (oidResultValueMaps.get(INDEX_COLUMN_NAME) != null)
                        indexSize = oidResultValueMaps.get(INDEX_COLUMN_NAME)
                                .size();
                    List<String> defaultValues = new ArrayList<String>(
                            indexSize);
                    for (int i = 0; i < indexSize; i++)
                        defaultValues.add(successType == WALK_TIMEOUT ? null
                                : singleOid);
                    oidResultValueMaps.put(oids[k], defaultValues);
                }

                if (successType == WALK_SUCCESS)
                    break;
            }

        }

    }

    /**
     * The GETBULK operation merely requests a number of GETNEXT responses to be
     * returned in a single packet rather than having to issue multiple GETNEXTs
     * to retrieve all the data that is needed. This is generally more efficient
     * with network bandwidth and also allows an agent to optimize how it
     * retrieves the data from the MIB instrumentation. However, there is also
     * the possibility of an overrun, which means to get back more data than was
     * needed because parts of the MIB tree that wasn't required were returned
     * as well.The expected returned PDU will be a RESPONSE, although a REPORT
     * may be issued as well in certain SNMPv3 circumstances.
     *
     * @param target   目前机器封装信息
     * @param datas    oid对应采集回来的结果集
     * @param rootOids 根oid
     * @loopOids 循环子oid，因为需要是递归去取oid，所以这个集合是动态的
     */
    private void snmpGetBulk(Target target,
                             Map<String, Map<String, String>> datas,
                             Collection<String> loopOids, String[] rootOids) throws IOException {

        PDU requestPDU = null;
        if (target.getVersion() == SnmpConstants.version3) {// snmpv3
            requestPDU = new ScopedPDU();
        } else
            requestPDU = new PDU();
        for (String oid : loopOids) {
            requestPDU.add(new VariableBinding(new OID(oid)));
        }

        requestPDU.setType(PDU.GETBULK);
        requestPDU.setMaxRepetitions(MAXREPETITIONS);
        requestPDU.setNonRepeaters(NONREPEATERS);

        Map<String, String> currentOids = new HashMap<String, String>();// 用于下次循环的oid
        for (String rootOID : rootOids) {
            if (!datas.containsKey(rootOID)) {
                datas.put(rootOID, new LinkedHashMap<String, String>());
            }
            currentOids.put(rootOID, rootOID);
        }

        if (!datas.containsKey(INDEX_COLUMN_NAME)) {
            datas.put(INDEX_COLUMN_NAME, new LinkedHashMap<String, String>());
        }

        try {
            long curr = System.currentTimeMillis();
            ResponseEvent responseEvent = snmp.send(requestPDU, target);
            PDU response = responseEvent.getResponse();
            if (logger.isDebugEnabled()) {
                long af = System.currentTimeMillis();
                StringBuffer debugInfo = new StringBuffer();
                debugInfo.append("SNMP GETBULK send request costs "
                        + (af - curr) + " ms. oids is " + loopOids.toString()
                        + ". Address is " + target.getAddress().toString());
                if (response != null)
                    debugInfo.append(".Request id is ["
                            + response.getRequestID() + "].");

                logger.debug(debugInfo);
            }
            if (response != null && response.getErrorIndex() == PDU.noError
                    && response.getErrorStatus() == PDU.noError) {
                // Dump response.
                Vector<? extends VariableBinding> bindings = response
                        .getVariableBindings();
                if (logger.isDebugEnabled()) {
                    StringBuffer variableBindings = new StringBuffer();
                    for (VariableBinding bind : bindings) {
                        String key = bind.getOid().toString();
                        variableBindings.append(key).append(" : ")
                                .append(bind.getVariable()).append("\n");
                    }
                    logger.debug("Request id is " + response.getRequestID()
                            + " and the orignal data" + " is \n["
                            + variableBindings.toString() + "]");
                }
                /*
                 * get bullk返回块数据，所以需要和原始OID进行挨个对比，如果当前返回块没有原始OID的数据，那么就丢弃当前OID，
				 * 直到所有的原始OID都walk结束。
				 */
                List<String> overRootOids = new ArrayList<String>(
                        rootOids.length);
                for (String rootOid : rootOids) {
                    boolean isEnd = true; // 标志当前rootOid是否walk结束
                    for (VariableBinding bind : bindings) {
                        String key = bind.getOid().toString();
                        if (StringUtils.startsWith(key, rootOid)
                                && !isEndWalk(bind, new OID(rootOid), response)) {
                            isEnd = false;
                            String value = getValue(key, bind.getVariable());
                            String index = key.substring(rootOid.length() + 1);
                            if (!datas.get(INDEX_COLUMN_NAME)
                                    .containsKey(index))
                                datas.get(INDEX_COLUMN_NAME).put(index, index);
                            datas.get(rootOid).put(index, value);
                            currentOids.put(rootOid, key);
                        }
                    }
                    if (!isEnd) {
                        overRootOids.add(rootOid);
                    }
                }

                if (overRootOids.isEmpty())
                    return;

                if (currentOids.isEmpty())
                    return;
                String[] loopRootOIDs = new String[overRootOids.size()];
                snmpGetBulk(target, datas, currentOids.values(),
                        overRootOids.toArray(loopRootOIDs));

            } else {
                if (logger.isWarnEnabled()) {
                    logger.warn("SNMP get bulk,request timeout,OIDs is "
                            + rootOids.toString() + ". Address is "
                            + target.getAddress().toString());
                }
            }

        } catch (IOException e) {
            if (logger.isErrorEnabled()) {
                logger.error("snmpWalk error! Address:"
                        + target.getAddress().toString(), e);
            }
            throw e;
        }
    }

    // 获取一层子树
    private void getSubtree(Target target, String oidStr,
                            Map<String, List<String>> oidResultValueMaps) {
        OID oid = new OID(oidStr);
        TreeUtils utils = null;
        if (target.getVersion() == SnmpConstants.version1)
            utils = new TreeUtils(snmp, getNextPDUFactory);
        else
            utils = new TreeUtils(snmp, getBulkPDUFactory);
        long curr = System.currentTimeMillis();
        List<TreeEvent> treeEventList = utils.getSubtree(target, oid);
        long af = System.currentTimeMillis();
        if (logger.isDebugEnabled()) {
            logger.debug("SNMP GETSUBTREE sends request costs " + (af - curr)
                    + " ms. oids is " + oidStr + ". Address is "
                    + target.getAddress().toString());
        }
        if (!treeEventList.isEmpty()) {
            for (TreeEvent treeEvent : treeEventList) {
                VariableBinding[] variables = treeEvent.getVariableBindings();
                if (variables != null) {
                    for (VariableBinding variable : variables) {
                        String responseOid = variable.getOid().toString();
                        String value = getValue(responseOid,
                                variable.getVariable());
                        String index = responseOid
                                .substring(oidStr.length() + 1);
                        if (oidResultValueMaps.get(INDEX_COLUMN_NAME) == null) {
                            List<String> indexList = new ArrayList<String>();
                            indexList.add(index);
                            oidResultValueMaps
                                    .put(INDEX_COLUMN_NAME, indexList);
                        } else if (!oidResultValueMaps.get(INDEX_COLUMN_NAME)
                                .contains(index))
                            oidResultValueMaps.get(INDEX_COLUMN_NAME)
                                    .add(index);

                        if (oidResultValueMaps.get(oidStr) == null) {
                            List<String> valueList = new ArrayList<String>();
                            valueList.add(value);
                            oidResultValueMaps.put(oidStr, valueList);
                        } else {
                            oidResultValueMaps.get(oidStr).add(value);
                        }
                    }
                }
            }
            if (logger.isDebugEnabled()) {
                long aff = System.currentTimeMillis();
                logger.debug("SNMP GETSUBTREE dealing with responses costs "
                        + (aff - af) + " ms. oids is " + oidStr
                        + ". Address is " + target.getAddress().toString());
            }
        } else {
            logger.warn("SNMP get subtree empty responses. oid is " + oidStr
                    + ",address is " + target.getAddress().toString());
        }
    }

    /**
     * 获取表格数据,例如获取arp表，ip路由表等
     *
     * @param target
     * @param oidStrs
     */
    private List<List<String>> getTable(Target target, String[] oidStrs) {
        OID[] oids = new OID[oidStrs.length];
        for (int i = 0; i < oidStrs.length; i++) {
            oids[i] = new OID(oidStrs[i]);
        }

        TableUtils utils = null;
        if (target.getVersion() == SnmpConstants.version1)
            utils = new TableUtils(snmp, getNextPDUFactory);
        else
            utils = new TableUtils(snmp, getBulkPDUFactory);
        long curr = System.currentTimeMillis();
        List<TableEvent> tableEventList = utils.getTable(target, oids, null,
                null);
        long af = System.currentTimeMillis();
        if (logger.isDebugEnabled()) {
            logger.debug("SNMP GETTABLE sends request costs " + (af - curr)
                    + " ms. oids is " + oidStrs.toString() + ". Address is "
                    + target.getAddress().toString());
        }
        if (!tableEventList.isEmpty()) {
            List<List<String>> datas = new ArrayList<List<String>>(50);
            for (TableEvent tableEvent : tableEventList) {
                VariableBinding[] variables = tableEvent.getColumns();
                if (variables != null) {
                    String rowOID = tableEvent.getIndex().toString();
                    List<String> rows = new ArrayList<String>(
                            variables.length + 1);
                    rows.add(rowOID);
                    for (VariableBinding variable : variables) {
                        String value = getValue(variable.getOid().toString(),
                                variable.getVariable());
                        rows.add(value);
                    }
                    datas.add(rows);
                }
            }
            if (logger.isDebugEnabled()) {
                long aff = System.currentTimeMillis();
                logger.debug("SNMP GETTABLE dealing with responses costs "
                        + (aff - af) + " ms. oids is " + oidStrs.toString()
                        + ". Address is " + target.getAddress().toString());
            }
            return datas;
        }
        return null;
    }

    /**
     * 判断SNMP walk是否已经结束，只walk一层节点
     *
     * @return
     */
    private boolean isEndWalk(VariableBinding responseVariableBinding,
                              OID requestOID, PDU responsePDU) {

        boolean isFinished = false;
        if (responsePDU.getErrorStatus() != PDU.noError) {
            isFinished = true;
        } else if (responseVariableBinding.getOid() == null) {
            isFinished = true;
        } else if (responseVariableBinding.getOid().size() < requestOID.size()) {
            isFinished = true;
        } else if (requestOID.leftMostCompare(requestOID.size(),
                responseVariableBinding.getOid()) != 0) {
            isFinished = true;
        } else if (Null.isExceptionSyntax(responseVariableBinding.getVariable()
                .getSyntax())) {
            isFinished = true;
        } else if (responseVariableBinding.getOid().compareTo(requestOID) <= 0) {
            if (logger.isDebugEnabled()) {
                StringBuilder b = new StringBuilder(
                        "snmpWalk Variable received is not lexicographic successor of requested  one:");
                b.append(responseVariableBinding.toString()).append(" <= ")
                        .append(requestOID);
                logger.debug(b.toString());
            }
            isFinished = true;
        }

        return isFinished;

    }

    private String getSourceOid(String lastSourceOid, String requestOid,
                                String snmpResponseOid) {
        if (lastSourceOid != null && snmpResponseOid.startsWith(lastSourceOid)) {
            return lastSourceOid;
        } else {
            if (snmpResponseOid.startsWith(requestOid)) {
                return requestOid;
            }
        }
        return null;
    }

    private void resolveSnmpReturnValue(
            Map<String, List<String>> oidResultValueMaps, int pduCode,
            Vector<? extends VariableBinding> bindings, String requestOid,
            String initOid) {
        boolean parseIndex = (pduCode == PDU.GETNEXT);
        if (parseIndex) {
            if (!oidResultValueMaps.containsKey(INDEX_COLUMN_NAME)) {
                oidResultValueMaps.put(INDEX_COLUMN_NAME,
                        new ArrayList<String>());
            }
        }
        String lastSourceOid = null;
        for (int i = 0; i < bindings.size(); i++) {
            VariableBinding variableBinding = bindings.get(i);
            String snmpReturnoid = variableBinding.getOid().toString();
            lastSourceOid = getSourceOid(lastSourceOid, requestOid,
                    snmpReturnoid);

            try {
                if (lastSourceOid == null) {//
                    if (logger.isWarnEnabled()) {
                        logger.warn("SNMP OID can't find source oid. RequestOid is "
                                + requestOid
                                + ",ResponseOid is "
                                + snmpReturnoid);
                    }
                    continue;
                }
                int lastIndex = lastSourceOid.length();
                if (parseIndex) {
                    if (i == 0) {
                        String index = snmpReturnoid.substring(lastIndex + 1);
                        if (!oidResultValueMaps.get(INDEX_COLUMN_NAME)
                                .contains(index))
                            oidResultValueMaps.get(INDEX_COLUMN_NAME)
                                    .add(index);
                    }
                    snmpReturnoid = lastSourceOid;
                }
                if (oidResultValueMaps.containsKey(initOid)) {
                    oidResultValueMaps.get(initOid).add(
                            getValue(variableBinding.getOid().toString(),
                                    variableBinding.getVariable()));
                } else {
                    List<String> varValues = new ArrayList<String>();
                    varValues.add(getValue(variableBinding.getOid().toString(),
                            variableBinding.getVariable()));
                    oidResultValueMaps.put(initOid, varValues);
                }
                // if (logger.isDebugEnabled()) {
                // logger.debug("getSnmpResult "
                // + variableBinding.getOid()
                // + ":"
                // + getValue(variableBinding.getOid().toString(),
                // variableBinding.getVariable()));
                // }

            } catch (Exception e) {
                logger.warn(e.getMessage(), e);
            }
        }
    }

    public String getValue(final String oid, Variable v) {
        int syntax = v.getSyntax();
        String value = null;
        switch (syntax) {
            case SnmpConstant.SNMP_SYNTAX_INT:
            case SnmpConstant.SNMP_SYNTAX_UINT32:
                value = String.valueOf(v.toInt());
                break;
            case SnmpConstant.SNMP_SYNTAX_TIMETICKS:
                value = String.valueOf(v.toLong());
                break;
            default:
                value = v.toString();
                break;
        }
        return convertToGBK(oid, value);
    }

    /**
     * {chinese covert}.
     *
     * @param oid String
     * @param str String
     * @return String
     */
    protected String convertToGBK(final String oid, final String str) {
        // 八进制转String
        if (isOct(str) && StringUtils.startsWithAny(oid, convertToString)) {
            // 特殊字符特殊处理
            if (str.indexOf(A1_67) != -1) {
                final String[] split = str.split(A1_67);
                final StringBuffer buffer = new StringBuffer();
                for (String str2 : split) {
                    if (str2.startsWith(":")) {
                        str2 = str2.substring(1);
                    }
                    buffer.append(fmtHexStr(str2));
                    buffer.append("　");
                }
                if (buffer.length() > 0) {
                    return buffer.substring(0, buffer.length() - 1);
                }
                return buffer.toString();
            }

            return fmtHexStr(str);
        }
        if (str != null) {
            return str.trim();
        }
        return str;
    }

    private boolean isOct(final String s) {
        if (StringUtils.isEmpty(s) || s.indexOf(REGEX_TEXT) < 0) {
            return false;
        }
        final String[] t_strs = s.split(REGEX_TEXT);
        for (int t_i = 0; t_i < t_strs.length; t_i++) {
            try {
                Integer.parseInt(t_strs[t_i], HEX_TEXT);
            } catch (final NumberFormatException t_e) {
                return false;
            }
        }
        return true;
    }

    private String fmtHexStr(final String paramString) {
        String t_str = null;
        try {
            if (paramString != null) {
                final String[] t_strs = paramString.split(REGEX_TEXT);
                byte[] t_bytes;
                if (t_strs != null) {
                    final int t_i = t_strs.length;
                    t_bytes = new byte[t_i];
                    for (int t_j = 0; t_j < t_i; t_j++) {
                        t_bytes[t_j] = new Integer(Integer.parseInt(
                                t_strs[t_j], HEX_TEXT)).byteValue();
                    }
                    t_str = new String(t_bytes, GBK);
                } else {
                    t_str = paramString;
                }
            }
        } catch (final Throwable t_e) {
            t_str = paramString;
        }
        if (t_str != null) {
            t_str = t_str.trim();
        }
        return t_str;
    }

    public OID getOid(String s) {
        String[] oids = StringUtils.split(s, CHAR);
        int[] oidValues = new int[oids.length];
        for (int i = 0; i < oids.length; i++) {
            oidValues[i] = Integer.parseInt(oids[i]);
        }
        return new OID(oidValues);
    }

    public void close() throws IOException {
        snmp.close();
    }
}
