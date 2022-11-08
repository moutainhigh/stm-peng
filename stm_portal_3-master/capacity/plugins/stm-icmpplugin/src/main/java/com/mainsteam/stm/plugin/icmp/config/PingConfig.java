package com.mainsteam.stm.plugin.icmp.config;

import com.mainsteam.stm.plugin.icmp.task.CPingTask;
import com.mainsteam.stm.plugin.icmp.task.FPingTask;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

/**
 * Created by Lich on 2017/5/25.
 */
public class PingConfig {

    public static final String FPING_PATH = "fping.path";
    public static final String CAPLIBS_PATH = "caplibs.path";
    private static final PingConfig defaultConfig = new PingConfig() {

        {
            addTaskConfig(new PingTaskConfig(FPingTask.class, FPingTask.DEFAULT_THREAD, FPingTask.DEFAULT_MAX_TARGETS));
//            addTaskConfig(new PingTaskConfig(CPingTask.class, 100, 1));
        }

        @Override
        public void load(InputStream inputStream) {
            throw new UnsupportedOperationException();
        }

        @Override
        public void setTaskConfigList(List<PingTaskConfig> taskConfigList) {
            throw new UnsupportedOperationException();
        }

        @Override
        public String getProperty(String key) {
            return System.getProperty(key, System.getenv(key));
        }
    };
    private ArrayList<PingTaskConfig> taskConfigList = new ArrayList<>();
    private Properties properties = new Properties();


    public PingConfig() {
    }

    public static PingConfig getDefaultConfig() {
        return defaultConfig;
    }

    public void load(InputStream inputStream) {
        //TODO
    }

    public List<PingTaskConfig> getTaskConfigList() {
        return new ArrayList<>(taskConfigList);
    }

    public void setTaskConfigList(List<PingTaskConfig> taskConfigList) {
        this.taskConfigList.clear();
        this.taskConfigList.addAll(taskConfigList);
    }

    public void addTaskConfig(PingTaskConfig taskConfig) {
        taskConfigList.add(taskConfig);
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public void setProperty(String key, String value) {
        properties.setProperty(key, value);
    }


    @Override
    public String toString() {
        return "PingConfig{" +
                "taskConfigList=" + taskConfigList +
                '}';
    }
}
