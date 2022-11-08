package com.mainsteam.stm.plugin.icmp.core;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.util.Date;

/**
 * Created by Lich on 2017/5/31.
 */
public class Target implements Cloneable {

    public static final int DEFAULT_TIMEOUT = 3000;    //ms
    public static final int DEFAULT_RETRY = 3;    //times

    public static final int UNKNOWN = -1;
    public static final int AVAILABLE = 1;
    public static final int UNAVAILABLE = 0;

    private static final int FULL_HEALTH = 2;

    private static final Log LOGGER = LogFactory.getLog(Target.class);

    private final String ip;
    private int timeout;
    private int retry;
    private int state = UNKNOWN;
    private int latency;
    private int packetLoss;
    private int jitter;
    private long lastExecuteTime;
    private long lastQueryTime = System.currentTimeMillis();
    private int health = FULL_HEALTH;

    public Target(String ip) {
        this.ip = ip;
    }

    public Target(String ip, int timeout, int retry) {
        this.ip = ip;
        this.timeout = timeout;
        this.retry = retry;
    }

    public String getIp() {
        return ip;
    }

    public int getTimeout() {
        return timeout;
    }

    void setTimeout(int timeout) {
        this.timeout = timeout;
    }

    public int getRetry() {
        return retry;
    }

    void setRetry(int retry) {
        this.retry = retry;
    }

    public int getState() {
        return state;
    }

    public int getLatency() {
        return latency;
    }

    public void setLatency(int latency) {
        this.latency = latency;
    }

    public int getPacketLoss() {
        return packetLoss;
    }

    public void setPacketLoss(int packetLoss) {
        this.packetLoss = packetLoss;
    }

    public int getJitter() {
        return jitter;
    }

    public void setJitter(int jitter) {
        this.jitter = jitter;
    }

    long getLastExecuteTime() {
        return lastExecuteTime;
    }

    public void setLastExecuteTime(long lastExecuteTime) {
        this.lastExecuteTime = lastExecuteTime;
    }

    long getLastQueryTime() {
        return lastQueryTime;
    }

    void setLastQueryTime(long lastQueryTime) {
        this.lastQueryTime = lastQueryTime;
    }

    private void checkState() {
        if (health >= FULL_HEALTH && state != AVAILABLE) {
            state = AVAILABLE;
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Set target available " + ip);
        } else if (health <= 0 && state != UNAVAILABLE) {
            state = UNAVAILABLE;
            if (LOGGER.isInfoEnabled())
                LOGGER.info("Set target unavailable " + ip);
        }
    }

    public void increaseHealth() {
        if (health < FULL_HEALTH)
            health++;
        checkState();
    }

    public void decreaseHealth() {
        if (health > 0)
            health--;
        checkState();
    }

    public void restoreHealth() {
        health = FULL_HEALTH;
        checkState();
    }

    public void setHealth(int health) {
        this.health = health;
        checkState();
    }

    @Override
    public Target clone() {
        try {
            return (Target) super.clone();
        } catch (CloneNotSupportedException e) {
            return null;
        }
    }

    @Override
    public String toString() {
        return ip;
    }

    public String toDetailString() {
        return "Target{" +
                "ip='" + ip + '\'' +
                ", timeout=" + timeout +
                ", retry=" + retry +
                ", state=" + state +
                ", health=" + health +
                ", latency=" + latency +
                ", packetLoss=" + packetLoss +
                ", jitter=" + jitter +
                ", lastExecuteTime=" + new Date(lastExecuteTime) +
                ", lastQueryTime=" + new Date(lastQueryTime) +
                '}';
    }
}
