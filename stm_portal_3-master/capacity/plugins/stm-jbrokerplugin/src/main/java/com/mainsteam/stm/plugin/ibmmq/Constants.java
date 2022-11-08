package com.mainsteam.stm.plugin.ibmmq;

/**
 * Websphere MQ常量类<br>
 * @author xiaopf@mainsteam.com<br>
 */
public final class Constants {
	/**
	 * MQ可用性
     */
    public static final String S_NORMAL = "available";

    /**
     * MQ不可用
     */
    public static final String S_UNNORMAL = "not available";
    
    public static final String S_CONNENTED = "connected";
 
    public static final String S_UNCONNECTED = "not connected";
   
    public static final String S_AVAIL = "Current";

    public static final String S_UNAVAIL = "Inactive";

    public static final int QUEUE_LOCAL = 1;
    
    public static final int QUEUE_MODEL = 2;
    
    public static final int QUEUE_ALIA = 3;
   
    public static final int QUEUE_REMOTE = 6;
   
    public static final int QUEUE_CLUSTER = 7;
    
    public static final String CHANNEL_STATUS_NORMAL = "Channel Available";

    public static final String CHANNEL_STATUS_ABNORMAL = "Channel Unavailable";
    
    public static final int TEMP_QUEUE_TYPE = 3;
   
}
