package com.mainsteam.ms.util.protocol.RemotePing;

import com.mainsteam.stm.plugin.icmp.core.Target;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

public class CResponse {

    private static final int DEFAULT_SOCKET_BUFFER = 256 * 1024;    //byte
    private static final int INIT_SEQ_ID = 1000;
    private static final int MAX_SEQ_ID = 32765;
    private static final Log logger = LogFactory.getLog(CResponse.class);

    private static int seqId = INIT_SEQ_ID;

    private static synchronized int nextSeqId() {
        seqId++;
        if (seqId > MAX_SEQ_ID)
            seqId = INIT_SEQ_ID;
        return seqId;
    }

    /**
     * @return latency(ms), available(times), packetLoss(%),total(times), jitter(ms)
     */
    public static int[] GetTime(String ip) {
        return GetTime(ip, Target.DEFAULT_RETRY);
    }

    /**
     * @return latency(ms), available(times), packetLoss(%),total(times), jitter(ms)
     */
    public static int[] GetTime(String ip, int retry) {
        return GetTime(ip, retry, Target.DEFAULT_TIMEOUT);
    }

    /**
     * @return latency(ms), available(times), packetLoss(%),total(times), jitter(ms)
     */
    public static int[] GetTime(String ip, int retry, int timeout) {
        int[] values = {-1, -1, -1, -1, -1};
        int seqId = nextSeqId();
        try {
            values = GetTime(ip, retry, timeout, seqId, DEFAULT_SOCKET_BUFFER);
        } catch (Throwable e) {
            logger.error(e.getMessage(), e);
        }
        return values;
    }


    /**
     * @return latency(ms), available(times), packetLoss(%),total(times), jitter(ms)
     */
    public static native int[] GetTime(String ip, int retry, int timeout, int seqId, int socket_buffer);
}