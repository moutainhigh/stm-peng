package com.mainsteam.stm.plugin.jdbc;

import com.mainsteam.stm.caplib.dict.AvailableStateEnum;
import com.mainsteam.stm.errorcode.CapcityErrorCodeConstant;
import com.mainsteam.stm.pluginsession.PluginResultSet;
import com.mainsteam.stm.pluginsession.PluginSession;
import com.mainsteam.stm.pluginsession.PluginSessionContext;
import com.mainsteam.stm.pluginsession.exception.PluginSessionRunException;
import com.mainsteam.stm.pluginsession.parameter.Parameter;
import com.mainsteam.stm.pluginsession.parameter.PluginArrayExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginExecutorParameter;
import com.mainsteam.stm.pluginsession.parameter.PluginInitParameter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.InputStream;
import java.net.SocketTimeoutException;
import java.sql.*;
import java.util.*;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * JDBC 插件
 *
 * @author
 */
public class JDBCPluginSession implements PluginSession {

    private static final String THE_NETWORK_ADAPTER_COULD_NOT = "The Network Adapter could not";

    private static final String ORACLE_SERVICE_NAME_URL = "Oracle_serviceName_url";

    private static final String ORACLE_RAC = "OracleRAC";

    static final String POSTGRE_SQL = "PostgreSQL";

    static final String DB2 = "DB2";

    static final String INFORMIX = "Informix";

    static final String SYBASE = "Sybase";

    static final String SQL_SERVER = "SQLServer";

    static final String ORACLE = "Oracle";

    static final String MY_SQL = "MySQL";

    static final String SHENZHOU_SQL = "ShenzhouSQL";

    static final String DM = "DM";

    static final String JDBC = "JDBC";

    private static final String PORT2 = "Port";

    private static final String REPLACEMENT = "\\(*\\)";

    private static final String REGEX = "\\(.*\\)";

    private static final String $_DBNAME = "${dbname}";

    private static final String $_PORT = "${port}";

    private static final String $_IPADDRESS = "${ipaddress}";

    private static final String $_OTHERPARAM = "${otherParams}";

    private static final String _URL = "_url";

    private static final String _DRIVER = "_driver";

    private static final String JDBC_DRIVERS_PROPERTIES = "com/mainsteam/stm/plugin/jdbc/JdbcDrivers.properties";

    private static final String SQL = "SQL";

    private static final String NOQUERY = "NOQUERY";

    // collect.xml文件中Parameter的type
    private static final String IS_AVAIL = "IS_AVAIL";

    private static final Log logger = LogFactory
            .getLog(JDBCPluginSession.class);
    // 用户名或者密码错误
    private static final String DEFAULT_INVALID_AUTH = "28000";
    // PostgreSQL用户或者密码错误
    private static final String POSTGRESAL_INVALID_AUTH = "28P01";
    // 权限不足
    private static final String POSTGRESAL_NO_PERMISSIONS = "42501";
    // 服务器拒绝连接
    private static final String POSTGRESAL_REJECTED_CONNECTION = "08004";

    private static final String SQLSERVER_CONNECTION_TIMEOUT = "HYT00";

    //Oracle SID 错误
    private static final String ORACLE_SID_ERROR = "ORA-12505";
    //Oracle Service Name 错误
    private static final String ORACLE_SERVICENAME_ERROR = "ORA-12514";

    public static final String JDBCPLUGIN_OTHER_PARAMS = "otherParams";

    public static final String JDBCPLUGIN_DB_NAME = "dbName";

    public static final String JDBCPLUGIN_DB_TYPE = "dbType";

    public static final String JDBCPLUGIN_DB_PASSWORD = "dbPassword";

    public static final String JDBCPLUGIN_DB_USERNAME = "dbUsername";

    public static final String JDBCPLUGIN_JDBC_PORT = "jdbcPort";

    public static final String JDBCPLUGIN_IP = "IP";


    // 登录连接超时时间
    private int loginTimeout = 30;
    // 查询超时时间
    private int queryTimeout = 90;

    private static final Set<String> dbTypeSet = new HashSet<String>();

    static {
        dbTypeSet.add(MY_SQL);
        dbTypeSet.add(ORACLE);
        dbTypeSet.add(ORACLE_RAC);
        dbTypeSet.add(SQL_SERVER);
        dbTypeSet.add(SYBASE);
        dbTypeSet.add(INFORMIX);
        dbTypeSet.add(DB2);
        dbTypeSet.add(POSTGRE_SQL);
        dbTypeSet.add(SHENZHOU_SQL);
        dbTypeSet.add(JDBC);
        dbTypeSet.add(DM);
    }

    private static Properties jdbcDrivers = new Properties();

    private String dbType;

    private String ipaddress;

    private int port;

    private String username;

    private String password;

    private String dbname;

    private String otherParams;

    protected Connection connection;

    private boolean isConnect = false;

    private boolean isAlive = false;
    /**
     * 锁
     */
    protected ReentrantReadWriteLock uselock = new ReentrantReadWriteLock(false);

    public JDBCPluginSession() {
    }

    /**
     * 构造函数
     *
     * @param dbType
     * @param ipaddress
     * @param port
     * @param username
     * @param password
     * @param dbname
     * @param othString
     */
    public JDBCPluginSession(String dbType, String ipaddress, int port,
                             String username, String password, String dbname, String othString) {
        this.dbType = dbType;
        this.ipaddress = ipaddress;
        this.port = port;
        this.password = password;
        this.username = username;
        this.otherParams = othString;
        this.dbname = dbname;
    }

    static {
        try {
            InputStream input = JDBCPluginSession.class.getClassLoader()
                    .getResourceAsStream(JDBC_DRIVERS_PROPERTIES);
            jdbcDrivers.load(input);

            for (String dbType : dbTypeSet) {
                String jdbcDriverKey = dbType + _DRIVER;
                String jdbcDriver = jdbcDrivers.getProperty(jdbcDriverKey);
                if (StringUtils.isNotBlank(jdbcDriver)) {
                    Class.forName(jdbcDriver);
                }
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }

    /**
     * 连接数据库
     *
     * @return
     * @throws Exception
     */
    public boolean connect(int retry) throws PluginSessionRunException {
        //先断开连接
        disconnect();
        String jdbcUrlKey = null;
        if (StringUtils.isNotBlank(this.dbType) && this.ipaddress != null
                && this.port > 0) {
            jdbcUrlKey = dbType + _URL;
        }
        if (jdbcUrlKey == null) {
            logger.error("NOT SUPPORT dbType:" + this.dbType);
            return false;
        }
        String jdbcurl = jdbcDrivers.getProperty(jdbcUrlKey);

        if (jdbcurl == null || "".equals(jdbcurl)) {
            jdbcUrlKey = dbType.replaceAll(REGEX, REPLACEMENT) + _URL;
            jdbcurl = jdbcDrivers.getProperty(jdbcUrlKey);
        }

        if (StringUtils.isNotBlank(jdbcurl)) {
            try {
                if (jdbcurl.contains($_IPADDRESS)) {
                    jdbcurl = jdbcurl.replace($_IPADDRESS, this.ipaddress);
                }
                if (jdbcurl.contains($_PORT)) {
                    jdbcurl = jdbcurl.replace($_PORT, this.port + "");
                }
                if (jdbcurl.contains($_DBNAME)) {
                    jdbcurl = jdbcurl.replace($_DBNAME, this.dbname);
                }
                if (otherParams != null && !"".equals(otherParams)) {
                    jdbcurl = jdbcurl.replace($_OTHERPARAM, this.otherParams);
                }

            } catch (Exception e) {
                this.isConnect = false;

            }
        }

        try {
            uselock.writeLock().lock();
            DriverManager.setLoginTimeout(this.loginTimeout);
            connection = DriverManager.getConnection(jdbcurl, this.username, this.password);
            if (connection != null) {
                this.isConnect = true;
                return true;
            }
        } catch (SQLException e) {
            if (logger.isErrorEnabled()) {
                logger.error("JDBC logon error. DBType is " + this.dbType + ",ip is " + this.ipaddress + ",port is " +
                        this.port + ", username is " + this.username + ", dbname is " + this.dbname +
                        ". Error Message:" + e.getMessage(), e);

            }
            String errorMessage = e.getMessage();
            SQLException te = e;
            Throwable throwable = e.getCause();
            //Oracle需要根据Service Name重新建立连接,需特殊处理一下
            if (StringUtils.equals(this.dbType, ORACLE)) {
                if (e.getMessage().contains(ORACLE_SID_ERROR)) { //sid连接错误
                    String OracleServiceUrl = jdbcDrivers.getProperty(ORACLE_SERVICE_NAME_URL);
                    OracleServiceUrl = StringUtils.replaceEach(OracleServiceUrl, new String[]{$_IPADDRESS, $_PORT, $_DBNAME},
                            new String[]{this.ipaddress, this.port + "", this.dbname});
                    try {
                        DriverManager.setLoginTimeout(this.loginTimeout);
                        connection = DriverManager.getConnection(OracleServiceUrl, this.username, this.password);
                        if (connection != null) {
                            this.isConnect = true;
                            return true;
                        }
                    } catch (SQLException e1) {
                        if (logger.isErrorEnabled()) {
                            logger.error("Oracle logon error. ip is " + this.ipaddress + ",port is " +
                                    this.port + ", username is " + this.username + ", dbname is " + this.dbname +
                                    ". Error Message:" + e.getMessage(), e);

                        }
                        errorMessage = e1.getMessage();
                        te = e1;
                        throwable = e1.getCause();
                    }

                }

            }

            if (throwable != null && throwable instanceof SocketTimeoutException) {//socket超时
                throw new PluginSessionRunException(
                        CapcityErrorCodeConstant.ERR_CAPCITY_JDBC_CONNECTION_TIMEOUT,
                        "JDBC Logon timeout, time is " + this.loginTimeout
                                + "seconds,please make sure the input is correct.", te);
            }
            if (StringUtils.containsIgnoreCase(errorMessage, ORACLE_SERVICENAME_ERROR)) {
                throw new PluginSessionRunException(
                        CapcityErrorCodeConstant.ERR_CAPCITY_ORACLE_SID_OR_SERVICENAME, te.getMessage(), te);
            }
            //Oracle 针对The Network Adapter could not establish the connection异常处理
            if (StringUtils.equals(this.dbType, ORACLE) && StringUtils.contains(errorMessage, THE_NETWORK_ADAPTER_COULD_NOT)) {
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e1) {
                }
                //重连一次
                if (retry <= 1)
                    connect(++retry);
            }

            int capcityErrorCode = this.dealWithSQLState(te.getSQLState(), te.getErrorCode());
            throw new PluginSessionRunException(capcityErrorCode, te.getMessage(), te);
        } finally {
            uselock.writeLock().unlock();
        }
        return false;
    }

    /**
     * 发送sql
     *
     * @param sql
     * @return
     * @throws Exception
     */
    public JDBCResultSet sendCommand(String sql) throws Exception {
        return sendCommand(sql, false);
    }

    /**
     * 发送SQL查询
     *
     * @param sql
     * @param printTile
     * @return
     * @throws Exception
     */
    public JDBCResultSet sendCommand(String sql, boolean printTile)
            throws Exception {
        if (sql == null || "".equals(sql)) {
            throw new NullPointerException("the sql can not be null!");
        }
        uselock.readLock().lock();
        ResultSet rs = null;
        boolean hasMoreResult = false;
        JDBCResultSet jdbcResultSet = new JDBCResultSet();
        try {
            if (this.isConnect) {
                PreparedStatement sta = null;
                try {
                    sta = connection.prepareStatement(sql);
                    sta.setQueryTimeout(this.queryTimeout);
                    rs = sta.executeQuery();
                    if (rs != null) {
                        hasMoreResult = true;
                    }
                    while (hasMoreResult) {
                        ResultSetMetaData rsmd = rs.getMetaData();
                        int columnCount = rsmd.getColumnCount();
//						jdbcResultSet.setColumnCount(columnCount);
                        List<List<String>> result = new ArrayList<List<String>>();
                        while (rs.next()) {
                            List<String> columnData = new ArrayList<String>();
                            for (int i = 1; i <= columnCount; i++) {
                                // String str = rs.getString(i);
                                String str = null;
                                try {
                                    str = rs.getString(i);
                                } catch (SQLException e) {
                                    Object obj = rs.getObject(i);
                                    if (obj != null)
                                        str = obj.toString();
                                }
                                columnData.add(str);
                            }
                            result.add(columnData);
                        }
                        jdbcResultSet.addData(result);
                        hasMoreResult = sta.getMoreResults();
                        rs = sta.getResultSet();
                    }


                    return jdbcResultSet;
                } catch (Exception e) {
                    if (logger.isErrorEnabled()) {
                        logger.error("[JDBC_SQL_COLLECT_ERR]db_ip:"
                                + this.ipaddress + ",db_type:" + this.dbType
                                + ",db_user:" + this.username + ",db_name:" + this.dbname + ",db_sql:" + sql
                                + ",error message:" + e);
                    }
                    throw e;
                } finally {
                    if (rs != null) {
                        try {
                            rs.close();
                        } catch (Exception e) {
                        }
                    }
                    if (sta != null) {
                        try {
                            sta.close();
                        } catch (Exception e) {
                        }
                    }
                }
            } else {
                if (logger.isWarnEnabled()) {
                    logger.warn("[JDBC_NOT_ALIVE]db_ip:" + this.ipaddress
                            + ",db_type:" + this.dbType + ",db_user:"
                            + this.username + ",db_name:" + this.dbname + ",db_sql:" + sql);
                }
                throw new RuntimeException("the jdbc has not connected!");
            }
        } finally {
            uselock.readLock().unlock();
            if (logger.isInfoEnabled()) {
                logger.info("db_ip:" + this.ipaddress + ",db_type:"
                        + this.dbType + ",db_user:" + this.username + ",db_name:" + this.dbname + ",db_sql:"
                        + sql + ",JDBCretsultset:" + jdbcResultSet.toString());
            }
        }
    }

    public void sendNoQueryCommand(String sql) throws Exception {
        PreparedStatement ps = null;
        try {
            ps = connection.prepareStatement(sql);
            boolean has = ps.execute();
        } catch (Exception e) {
            if (logger.isErrorEnabled()) {
                logger.error("[JDBC_SQL_COLLECT_ERR]db_ip:"
                        + this.ipaddress + ",db_type:" + this.dbType
                        + ",db_user:" + this.username + ",db_name:" + this.dbname + ",db_sql:" + sql
                        + ",error message:" + e);
            }
            throw e;
        } finally {
            if (ps != null) {
                try {
                    ps.close();
                } catch (Exception e) {
                }
            }
        }
    }

    /**
     * 断开连接
     */
    public void disconnect() {
        try {
            uselock.writeLock().lock();
            if (this.connection != null) {
                try {
                    this.connection.close();
                } catch (SQLException e) {
                    logger.error(e.getMessage(), e);
                } finally {
                    this.isConnect = false;
                }
            }
        } finally {
            uselock.writeLock().unlock();
        }
    }

    /**
     * 处理SQLException的错误状态码，返回能力库特定的错误码
     *
     * @param sqlState
     * @return
     */
    private int dealWithSQLState(String sqlState, int errorCode) {
        int capcityErrorCode = CapcityErrorCodeConstant.ERR_CAPCITY_CONNECTION_FAILED;
        switch (this.dbType) {

            case POSTGRE_SQL:
                if (StringUtils.equals(POSTGRESAL_INVALID_AUTH, sqlState))// 用户名或者密码错误
                    capcityErrorCode = CapcityErrorCodeConstant.ERR_CAPCITY_USERNAME_OR_PASSWORD;
                else if (StringUtils.equals(POSTGRESAL_NO_PERMISSIONS, sqlState))// 权限不足
                    capcityErrorCode = CapcityErrorCodeConstant.ERR_CAPCITY_USER_NO_PERMISSIONS;
                else if (StringUtils.equals(POSTGRESAL_REJECTED_CONNECTION,
                        sqlState))// 服务器拒绝连接
                    capcityErrorCode = CapcityErrorCodeConstant.ERR_CAPCITY_REJECTED_CONNECTION;

                break;
            case SQL_SERVER:
                if (StringUtils.equals(SQLSERVER_CONNECTION_TIMEOUT, sqlState)) {// 登录连接超时
                    capcityErrorCode = CapcityErrorCodeConstant.ERR_CAPCITY_JDBC_CONNECTION_TIMEOUT;
                }
                break;
            default:
                if (StringUtils.equals(sqlState, DEFAULT_INVALID_AUTH))// 用户或者密码错误
                    capcityErrorCode = CapcityErrorCodeConstant.ERR_CAPCITY_USERNAME_OR_PASSWORD;
        }
        return capcityErrorCode;
    }

    @Override
    public void init(PluginInitParameter initP)
            throws PluginSessionRunException {
        Parameter[] initParameters = initP.getParameters();
        for (int i = 0; i < initParameters.length; i++) {
            switch (initParameters[i].getKey()) {
                case JDBCPLUGIN_IP:
                    this.ipaddress = initParameters[i].getValue();
                    break;
                case JDBCPLUGIN_JDBC_PORT:
                    this.port = Integer.parseInt(initParameters[i].getValue());
                    break;
                case JDBCPLUGIN_DB_USERNAME:
                    this.username = initParameters[i].getValue();
                    break;
                case JDBCPLUGIN_DB_PASSWORD:
                    this.password = initParameters[i].getValue();
                    break;
                case JDBCPLUGIN_DB_TYPE:
                    this.dbType = initParameters[i].getValue();
                    break;
                case JDBCPLUGIN_DB_NAME:
                    this.dbname = initParameters[i].getValue();
                    break;
                case JDBCPLUGIN_OTHER_PARAMS:
                    this.otherParams = oracleRacUtil(initParameters[i].getValue());
                    break;
                default:
                    if (logger.isWarnEnabled()) {
                        logger.warn("warn:unkown initparameter "
                                + initParameters[i].getKey() + "="
                                + initParameters[i].getValue());
                    }
                    break;
            }
        }
        if (!dbTypeSet.contains(this.dbType)) {
            PluginSessionRunException pluginException = new PluginSessionRunException(
                    CapcityErrorCodeConstant.ERR_CAPCITY_CONVERT_DBTYPEERROR,
                    "DBType not supported:" + this.dbType, null);
            throw pluginException;
        }

        try {
            connect(1);
            this.isAlive = true;
        } catch (PluginSessionRunException e) {
            if (logger.isWarnEnabled()) {
                logger.warn(e.getMessage(), e);
            }
            this.isAlive = false;
            throw e;
        }
    }

    @Override
    public void destory() {
        this.isAlive = false;
        disconnect();
    }

    @Override
    public void reload() {
    }

    @Override
    public boolean isAlive() {
        return this.isAlive;
    }

    @Override
    public PluginResultSet execute(
            PluginExecutorParameter<?> executorParameter,
            PluginSessionContext context) throws PluginSessionRunException {

        if (logger.isTraceEnabled()) {
            logger.trace("execute start");
        }
        PluginResultSet result = new PluginResultSet();
        boolean isAvail = false;
        String eSql = "";
        if (executorParameter instanceof PluginArrayExecutorParameter) {
            try {
                PluginArrayExecutorParameter arrayP = (PluginArrayExecutorParameter) executorParameter;
                Parameter[] parameters = arrayP.getParameters();
                List<String> sqls = new ArrayList<String>(parameters.length);
                List<String> noQuerySqls = new ArrayList<String>(parameters.length);
                // 判断参数
                Map<String, String> replaces = new HashMap<String, String>(2); // 参数替换
                for (Parameter parameter : parameters) {
                    if (parameter.getKey().equalsIgnoreCase(IS_AVAIL)) {//采集数据库可用性
                        isAvail = true;
                    } else if (parameter.getKey().equalsIgnoreCase(SQL)) {
                        sqls.add(parameter.getValue());
                    } else if (parameter.getKey().equalsIgnoreCase(NOQUERY)) {
                        noQuerySqls.add(parameter.getValue());
                    } else {
                        replaces.put(parameter.getKey(), parameter.getValue());
                    }
                }
                // 没有SQL查询语句，如果发现参数是ip+port的话，直接拼接后返回
                if (sqls.size() == 0) {
                    if (replaces.size() == 2) {
                        Set<String> keySet = replaces.keySet();
                        if (keySet.contains(JDBCPLUGIN_IP)
                                && keySet.contains(PORT2)) {
                            PluginResultSet resultSet = new PluginResultSet();
                            resultSet.putValue(0, 0, this.ipaddress + ":"
                                    + this.port);
                            return resultSet;
                        }
                    }
                }

                // 当执行的sql没有结果集时，执行NOQUERY;
                for (String noQuerySql : noQuerySqls) {
                    eSql = noQuerySql;
                    if (replaces.size() > 0) {
                        Set<String> keySet = replaces.keySet();
                        Iterator<String> iterator = keySet.iterator();
                        while (iterator.hasNext()) {
                            String key = iterator.next();
                            eSql = StringUtils.replace(noQuerySql, "${" + key
                                    + "}", replaces.get(key));
                        }
                    }

                    this.sendNoQueryCommand(eSql);
                }

                // 执行SQL
                for (String sql : sqls) {
                    eSql = sql;
                    if (replaces.size() > 0) {
                        Set<String> keySet = replaces.keySet();
                        Iterator<String> iterator = keySet.iterator();
                        while (iterator.hasNext()) {
                            String key = iterator.next();
                            eSql = StringUtils.replace(sql, "${" + key
                                    + "}", replaces.get(key));
                        }
                    }

                    JDBCResultSet ret = this.sendCommand(eSql);

                    if (isAvail) {
                        result.putValue(0, 0, String
                                .valueOf(AvailableStateEnum.Normal
                                        .getStateVal()));
                    } else {
                        putValues(result, ret);
                    }
                }

            } catch (Exception e) {
                logger.error("db_ip:" + this.ipaddress + ",db_type:"
                        + this.dbType + ",db_user:" + this.username + ",db_name:" + this.dbname + ",db_sql:"
                        + eSql + ",error message:" + e.getMessage(), e);
                if (isAvail) {
                    result.putValue(0, 0, String
                            .valueOf(AvailableStateEnum.Critical.getStateVal()));
                    try {
                        if (connect(1)) {
                            result.putValue(0, 0, String
                                    .valueOf(AvailableStateEnum.Normal.getStateVal()));
                        }
                    } catch (PluginSessionRunException ee) {
                        logger.warn("the second connects failed, db_ip:" + this.ipaddress + ",db_type:"
                                + this.dbType + ",db_user:" + this.username + ",db_name:" + this.dbname + ",db_sql:"
                                + eSql + ",error message: " + ee.getMessage(), ee);
                    }
                    return result;
                }

//				throw new PluginSessionRunException(
//						CapcityErrorCodeConstant.ERR_CAPCITY_COLLECT_FAILED,
//						e.getMessage(), e);
            }
        }

        return result;
    }

    private void putValues(PluginResultSet result, JDBCResultSet ret) {
        // 因为可能执行多条SQL，所以需要在处理结果集数据前先判断，如果当前结果集已经存在数据则表示之前已经执行过数据，这时仅新增列即可。实际上多条命令的执行也是为了生成多列
        boolean isNewInsert = true;
        if (result.getRowLength() > 0 || result.getColumnLength() > 0)
            isNewInsert = false;

        if (ret == null) {
            if (isNewInsert)
                result.putValue(0, 0, null);
        } else {
            int resultColumnCount = ret.getColumnCount();
            int resultRowIndex = 0; // SQL结果集的行下标
            int ColumnIndex = result.getColumnLength(); // 当前结果集的列数
            while (ret.next()) {
                for (int i = 0; i < resultColumnCount; i++) {
                    String str = ret.getString(i);
                    if (isNewInsert)
                        result.putValue(resultRowIndex, i, str);
                    else {
                        result.putValue(resultRowIndex, ColumnIndex + i, str);
                    }
                }
                resultRowIndex++;
            }

        }

    }

    // 把jdbc的返回值封装进这个类中
    class JDBCResultSet {

        private int columnCount;

        private List<List<String>> data;

        private int currentRow; // 当前行

        public JDBCResultSet() {
            data = new ArrayList<List<String>>();
        }

        public int getColumnCount() {
            return columnCount;
        }

        public void setColumnCount(int columnCount) {
            this.columnCount = columnCount;
        }

        public List<List<String>> getData() {
            return data;
        }

        public void setData(List<List<String>> data) {
            this.data = data;
        }

        public boolean next() {
            if (data == null)
                return false;
            else {
                if (currentRow == data.size()) {
                    currentRow = 0;
                    return false;
                } else {
                    currentRow++;
                    return true;
                }
            }
        }

        // 根据列取值
        public String getString(int columnIndex) {
            try {
                List<String> result = this.data.get(currentRow - 1);
                return result.get(columnIndex);
            } catch (Exception e) {
                logger.warn(e);
                return null;
            }

        }

        @Override
        public String toString() {
            if (this.data != null) {
                StringBuffer sb = new StringBuffer();
                for (List<String> rowList : this.data) {
                    if (rowList != null) {
                        for (String str : rowList) {
                            sb.append(str).append(",");
                        }
                        sb.append("\r\n");
                    }
                }
                return sb.toString();
            } else
                return "JDBCResultSet [columnCount=" + columnCount + ", data="
                        + data + ", currentRow=" + currentRow + "]";
        }

        public void addData(List<List<String>> result) {
            if (result == null)
                return;
            if (this.data == null) {
                this.data = new ArrayList<>();
            }
            while (data.size() < result.size()) {
                data.add(new ArrayList<String>(columnCount));
            }
            for (int i = 0; i < result.size(); ++i) {
                data.get(i).addAll(result.get(i));
            }
            int column = 0;
            if (result.size() > 0)
                column = result.get(0).size();
            for (int i = result.size(); i < data.size(); ++i) {
                data.get(i).addAll(Collections.nCopies(column, ""));
            }
            columnCount += column;
        }
    }

    @Override
    public boolean check(PluginInitParameter initParameters)
            throws PluginSessionRunException {
        this.init(initParameters);
        return this.isAlive;
    }

    /**
     * 给oraclerac的表单做处理用
     *
     * @return
     */
    private String oracleRacUtil(String otherParam) {
        String param = "";
        String[] url = StringUtils.split(otherParam, ";");
        for (int i = 0; i < url.length; i++) {
            String ip = StringUtils.substringBefore(url[i], ":");
            String port = StringUtils.substringAfter(url[i], ":");
            param += "(ADDRESS=(PROTOCOL=TCP)(HOST=" + ip + ")(PORT=" + port
                    + "))";
        }
        param += "(FAILOVER=yes)(LOAD_BALANCE=no))";
        return param;
    }

}
