package com.mainsteam.kafka.producer.inter;

import java.util.Properties;

public interface KafkaProducerTool
{
    public boolean publishMessage(String paramString1, String paramString2);

    public boolean publishMessage(String paramString);

    public boolean publishPartitionedMessage(String paramString1, String paramString2, String paramString3);

    public boolean publishPartitionedMessage(String paramString1, String paramString2);

    public void setProducerProperties(Properties paramProperties);

    public Properties getProducerProperties();

    public void setConfigProperties(Properties paramProperties);

    public Properties getConfigProperties();

    public String getSourceId();

    public boolean testConnect();

    public boolean updateConf(String paramString1, String paramString2, String paramString3);
}
