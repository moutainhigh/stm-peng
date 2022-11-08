package com.mainsteam.stm.plugin.wmi.deprecated;


/**
 * 当应用程序接收到WMI angent服务器返回的数据后，执行的操作
 */
public interface TransportHandle 
{
   
   /**
    * 获取到wmiagent端数据后，执行的操作
    * @param message	接收到的数据
    */
   public void operation(String message);
}
