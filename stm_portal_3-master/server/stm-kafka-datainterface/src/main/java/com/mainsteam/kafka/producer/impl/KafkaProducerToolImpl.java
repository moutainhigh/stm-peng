package com.mainsteam.kafka.producer.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.URL;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mainsteam.kafka.producer.inter.KafkaProducerTool;
import kafka.javaapi.producer.Producer;
import kafka.producer.KeyedMessage;
import kafka.producer.ProducerConfig;

public class KafkaProducerToolImpl
        implements KafkaProducerTool
{
    private Producer<String, String> producer;
    private Properties configProperties;
    private Properties producerProperties;
    private ProducerConfig producerConfig;

    public KafkaProducerToolImpl()
    {
        this("default"); }

    public KafkaProducerToolImpl(String configFilePath) {
        this.configProperties = generateConfigProperties(configFilePath);
        this.producerProperties = new Properties();
        for (String configStr : this.configProperties.stringPropertyNames())
        {
            this.producerProperties.put(configStr, this.configProperties.getProperty(configStr));
        }

        this.producerConfig = new ProducerConfig(this.producerProperties);
        this.producer = new Producer(this.producerConfig);
    }

    private Properties generateConfigProperties(String configFilePath)
    {
        Properties properties = new Properties();
        try
        {
            InputStream inputStream;
            if (configFilePath.equals("default")) {
                String pathIn = super.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
                String[] strs = pathIn.split("/");
                String path = "";
                for (int i = 1; i < strs.length - 1; ++i) {
                    path = path + strs[i] + "/";
                }
                File file = new File(path + "kafkaConfig.properties");
                if (file.exists())
                    inputStream = new FileInputStream(path + "kafkaConfig.properties");
                else
                    inputStream = super.getClass().getClassLoader().getResourceAsStream("kafkaConfig.properties");
            }
            else {
                inputStream = new FileInputStream(configFilePath); }
            properties.load(inputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return properties;
    }

    public boolean publishMessage(String topic, String message) {
        KeyedMessage data = new KeyedMessage(topic, message);
        return putData(data);
    }

    public boolean publishMessage(String message) {
        String topic = this.producerProperties.getProperty("itba.topic", "itba-default-topic").trim();
        return publishMessage(topic, message);
    }

    public boolean publishPartitionedMessage(String topic, String partitionKey, String message) {
        KeyedMessage data = new KeyedMessage(topic, partitionKey, message);
        return putData(data);
    }

    public boolean publishPartitionedMessage(String partitionKey, String message) {
        String topic = this.producerProperties.getProperty("itba.topic", "itba-default-topic").trim();
        return publishPartitionedMessage(topic, partitionKey, message);
    }

    public void setProducerProperties(Properties producerProperties) {
        this.producerProperties = producerProperties;
    }

    public Properties getProducerProperties() {
        return this.producerProperties;
    }

    public void setConfigProperties(Properties configProperties) {
        this.configProperties = configProperties;
    }

    public Properties getConfigProperties() {
        return this.configProperties;
    }

    public Producer<String, String> getProducer()
    {
        return this.producer;
    }

    public void setProducer(Producer<String, String> producer) {
        this.producer = producer;
    }

    public ProducerConfig getProducerConfig() {
        return this.producerConfig;
    }

    public void setProducerConfig(ProducerConfig producerConfig) {
        this.producerConfig = producerConfig;
    }

    public String getSourceId() {
        return getProducerProperties().getProperty("itba.sourceId", "sourceId").trim();
    }

    public boolean testConnect()
    {
        return putData(new KeyedMessage("test", "testMessage"));
    }

    public boolean updateConf(String ip, String port, String sourceId) {
        String ipPort = "";
        if (port.equals("")) port = "9092";
        String[] ips = ip.split(",");
        for (String ipStr : ips) {
            if (isBoolIp(ipStr)) {
                ipPort = ipPort + ipStr + ":" + port + ",";
            } else {
                System.out.println("请输入正确的IP 格式，多IP 请用英文半角逗号分开！");
                return false;
            }
        }
        String isIpPort = ipPort.substring(0, ipPort.length() - 1);
        if ((isBoolPort(port)) && (!(sourceId.equals("")))) { this.producerProperties.setProperty("itba.sourceId", sourceId);
            this.producerProperties.setProperty("metadata.broker.list", isIpPort);
            Object fos = null;
            String[] strs;
            try {
                String pathIn = super.getClass().getProtectionDomain().getCodeSource().getLocation().getPath();
                strs = pathIn.split("/");
                String path = "";
                for (int i = 1; i < strs.length - 1; ++i) {
                    path = path + strs[i] + "/";
                }
                File file = new File(path, "kafkaConfig.properties");
                if (!(file.exists())) {
                    file.createNewFile();
                }
                fos = new FileOutputStream(path + "kafkaConfig.properties");
                this.producerProperties.store((OutputStream)fos, "==========Do not delete the file!This is a latest configuration file.===========");
                ((OutputStream)fos).close();
                KafkaProducerTool kafkaProducerTool2 = new KafkaProducerToolImpl();
                boolean k = false;
                if (sourceId.equals(kafkaProducerTool2.getConfigProperties().getProperty("itba.sourceId"))) {
                    this.producerConfig = new ProducerConfig(this.producerProperties);
                    this.producer = new Producer(this.producerConfig);
                    System.out.println("修改成功！ sourceId = " + kafkaProducerTool2
                            .getConfigProperties().getProperty("itba.sourceId") + " ; brokers = " + kafkaProducerTool2
                            .getConfigProperties().getProperty("metadata.broker.list"));
                    k = true;
                    return k;
                }
                System.out.println("修改失败！");

                return k;
            }
            catch (FileNotFoundException e)
            {
                e.printStackTrace();

                return false;
            }
            catch (IOException e)
            {
                e.printStackTrace();

                return false;
            }
            finally
            {
                try
                {
                    ((OutputStream)fos).close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        System.out.println("请输入正确的参数，并注意顺序！");
        return false;
    }

    private boolean putData(KeyedMessage<String, String> data)
    {
        try
        {
            this.producer.send(data);
            return true;
        } catch (Exception e) {
            String sysTime = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date());
            System.out.println(sysTime + "  kafkaService 有异常，请根据提示异常信息尽快排查问题！");
            e.printStackTrace(); }
        return false;
    }

    private boolean isBoolIp(String ipAddress)
    {
        String ipPat = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pattern = Pattern.compile(ipPat);
        Matcher matcher = pattern.matcher(ipAddress);
        return matcher.matches();
    }

    private boolean isBoolPort(String inPort)
    {
        boolean isNum = inPort.matches("[0-9]+");
        if (isNum) {
            int portNum = Integer.valueOf(inPort).intValue();
            return (portNum <= 65536);
        }
        return false;
    }
}
