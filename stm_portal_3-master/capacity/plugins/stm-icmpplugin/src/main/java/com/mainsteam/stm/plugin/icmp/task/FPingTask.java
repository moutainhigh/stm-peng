package com.mainsteam.stm.plugin.icmp.task;

import com.mainsteam.stm.plugin.icmp.config.PingConfig;
import com.mainsteam.stm.plugin.icmp.core.AbstractPingTask;
import com.mainsteam.stm.plugin.icmp.core.Target;
import org.apache.commons.lang.SystemUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Lich on 2017/5/25.
 */
public class FPingTask extends AbstractPingTask {

    public static final int DEFAULT_MAX_TARGETS = 100;
    public static final int DEFAULT_THREAD = 1;

    private static final int MAX_TIMEOUT = 60;//seconds
    private static final int INTERVAL = 10;
    private static final Pattern LINUX_SUMMARY = Pattern.compile("(\\S+)\\s+: xmt/rcv/%loss = \\d+/\\d+/(\\d+)%(?:, min/avg/max = (\\S+)/(\\S+)/(\\S+))?");
    private static final Pattern WINDOWS_ALIVE = Pattern.compile("Reply\\[\\d+\\] from (\\S+): bytes=\\d+ time=(\\S+) ms TTL=\\d+");
    private static final Log LOGGER = LogFactory.getLog(FPingTask.class);
    private static File fping;

    public FPingTask(int maxTargets, int threads, int index) {
        super(maxTargets, 1, index);
    }

    @Override
    public void init() {
        PingConfig pingConfig = PingConfig.getDefaultConfig();
        File directory;
        try {
            directory = new File(pingConfig.getProperty(PingConfig.FPING_PATH));
        } catch (NullPointerException e) {
            directory = new File(pingConfig.getProperty(PingConfig.CAPLIBS_PATH), "tools");
        }
        String fileName = "fping";
        String archSuffix = "x32";
        if (SystemUtils.OS_ARCH.contains("64")) {
            archSuffix = "x64";
        }
        if (SystemUtils.IS_OS_WINDOWS) {
            fileName += "_win_" + archSuffix + ".exe";
        } else {
            fileName += "_linux_" + archSuffix;
        }
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("fping directory = " + directory + ", fileName = " + fileName);
        }
        fping = new File(directory, fileName);
        if (!fping.canExecute())
            throw new IllegalArgumentException("fping can't execute.");
    }

    @Override
    public void ping(List<Target> targets) {
        Thread.currentThread().setName("Fping");
        if (SystemUtils.IS_OS_WINDOWS)
            pingWin(targets);
        else
            pingLinux(targets);
    }

    @Override
    public boolean validate(Target target) {
        return target != null && target.getIp() != null &&
                target.getIp().matches("([0,1]?\\d{0,2}|2[0-4]\\d|25[0-5])\\.([0,1]?\\d{0,2}|2[0-4]\\d|25[0-5])\\.([0,1]?\\d{0,2}|2[0-4]\\d|25[0-5])\\.([0,1]?\\d{0,2}|2[0-4]\\d|25[0-5])") &&
                target.getTimeout() > 0 && target.getRetry() > 0;
    }

    /*
    Usage: fping [options] [targets...]
      -a         show targets that are alive
      -A         show targets by address
      -b n       amount of ping data to send, in bytes (default 56)
      -B f       set exponential backoff factor to f
      -c n       count of pings to send to each target (default 1)
      -C n       same as -c, report results in verbose format
      -D         print timestamp before each output line
      -e         show elapsed time on return packets
      -f file    read list of targets from a file ( - means stdin) (only if no -g specified)
      -g         generate target list (only if no -f specified)
      (specify the start and end IP in the target list, or supply a IP netmask)
      (ex. fping -g 192.168.1.0 192.168.1.255 or fping -g 192.168.1.0/24)
      -H n       Set the IP TTL value (Time To Live hops)
      -i n       interval between sending ping packets (in millisec) (default 25)
      -I if      bind to a particular interface
      -l         loop sending pings forever
      -m         ping multiple interfaces on target host
      -n         show targets by name (-d is equivalent)
      -O n       set the type of service (tos) flag on the ICMP packets
      -p n       interval between ping packets to one target (in millisec)
      (in looping and counting modes, default 1000)
      -q         quiet (don't show per-target/per-ping results)
      -Q n       same as -q, but show summary every n seconds
      -r n       number of retries (default 3)
      -R         random packet data (to foil link data compression)
      -s         print final stats
      -S addr    set source address
      -t n       individual target initial timeout (in millisec) (default 500)
      -T n       ignored (for compatibility with fping 2.4)
      -u         show targets that are unreachable
      -v         show version
      targets    list of targets to check (if no -f specified)

      Example:
      fping -i 1 -p 1 -c 100 -t 150 -q  www.baidu.com www.google.com www.bucunzai.com
      www.baidu.com    : xmt/rcv/%loss = 100/89/11%, min/avg/max = 187/187/194
      www.google.com   : xmt/rcv/%loss = 100/100/0%, min/avg/max = 2.89/2.98/5.30
      www.bucunzai.com : xmt/rcv/%loss = 100/0/100%
     */
    private void pingLinux(List<Target> targets) {
        int retry = 0;
        int timeout = 0;
        ArrayList<String> ipList = new ArrayList<>();
        HashMap<String, Target> targetMap = new HashMap<>();
        for (Target target : targets) {
            targetMap.put(target.getIp(), target);
            ipList.add(target.getIp());
            retry = Math.max(target.getRetry(), retry);
            timeout = Math.max(target.getTimeout(), timeout);
        }
        ArrayList<String> commandList = new ArrayList<>();
        commandList.add(fping.toString());
        commandList.add("-i");
        commandList.add(String.valueOf(INTERVAL));
        commandList.add("-p");
        commandList.add(String.valueOf(INTERVAL));
        commandList.add("-c");
        commandList.add(String.valueOf(retry));
        commandList.add("-t");
        commandList.add(String.valueOf(timeout));
        commandList.add("-q");
        commandList.addAll(ipList);
        if (LOGGER.isInfoEnabled()) {
            LOGGER.info("Fping is ready to execute " + commandList);
        }
        Process process = null;
        try {
            process = new ProcessBuilder(commandList).redirectErrorStream(true).start();
            InputStream in = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(in));
            String line;
            StringBuilder sb = new StringBuilder();
            try {
                while ((line = reader.readLine()) != null) {
                    if (LOGGER.isDebugEnabled())
                        sb.append(System.lineSeparator()).append(line);
                    Matcher matcher = LINUX_SUMMARY.matcher(line);
                    if (matcher.find()) {
                        try {
                            Target target = targetMap.get(matcher.group(1));
                            if (target != null) {
                                target.setLastExecuteTime(System.currentTimeMillis());
                                int packetLoss = Integer.valueOf(matcher.group(2));
                                if (packetLoss == 100) {
                                    target.decreaseHealth();
                                } else {
                                    double min = Double.valueOf(matcher.group(3));
                                    double avg = Double.valueOf(matcher.group(4));
                                    double max = Double.valueOf(matcher.group(5));
                                    target.restoreHealth();
                                    target.setPacketLoss(packetLoss);
                                    target.setLatency((int) avg);
                                    target.setJitter((int) Math.sqrt((Math.pow(avg - min, 2) + Math.pow(max - avg, 2)) / 2));
                                }
                            }
                        } catch (Exception e) {
                            if (LOGGER.isWarnEnabled())
                                LOGGER.warn("line parse error = " + line);
                        }
                    }
                }
                if (LOGGER.isInfoEnabled())
                    LOGGER.info("Fping finish executing " + commandList + ", exitCode = " + process.waitFor());
            } finally {
                if (LOGGER.isDebugEnabled())
                    LOGGER.debug("Fping finish executing " + commandList + ", result = " + sb);
            }
        } catch (IOException e) {
            LOGGER.error("Fping fails to execute " + commandList, e);
        } catch (InterruptedException e) {
            LOGGER.error("Fping executing interrupts, " + commandList, e);
        } finally {
            if (process != null)
                process.destroy();
        }
    }

    /*
      Usage:
      fping_win_x64 <host(-list)> [-t time] [-w timeout] [-c] [-n count] [-s data_size]
      [-S size1/size2] [-R min/max] [-d ping_data] [-h TTL] [-v TOS]
      [-r routes] [-f] [-j] [-g host1/host2] [-H filename] [-a] [-A]
      [-p(x)] [-i] [-b(-)] [-T] [-D] [-l] [-o] [-L filename]

      Options:
      -t : time between 2 pings in ms up to 1000000
      -w : timeout in ms to wait for each reply
      -c : continuous ping (higher priority than -n)
      to see statistics and continue - type Control-Break;
      to stop - type Control-C.
      -n : number of pings to send to each host
      -s : amount of data in bytes up to 65500
      -S : size sweep: ping with size1, size1 + 1, ..., size2 bytes
      -R : random length between min and max (disabled when using -S)
      -d : ping with specified data
      -h : number of hops (TTL: 1 to 128) + print hops
      -v : Type Of Service (0 to 255) (IPv4-only)
      -r : record route (1 to 9 routes) (IPv4-only)
      -f : set Don't Fragment flag in packet (IPv4-only)
      -j : print jitter with each reply (only when pinging one host)
      -g : ping IP range from host1 to host2 (IPv4-only)
      -H : get hosts from filename (comma delimited, filename with full path)
      -a : resolve addresses to hostname
      -A : print addresses with each reply
      -p : use a thread pool to ping multiple hosts (enables ICMP dll)
      x is optional and allows you to choose the number of threads
      e.g. -p uses a thread for every host
      -p5 uses a pool of 5 threads/core
      -i : use ICMP dll instead of raw socket (disables -r)
      -b : beep on every successful reply (-b- to beep on timeout)
      -T : print timestamp with each reply
      -D : print datestamp with each reply
      -l : limit the output to ping results and errors
      -o : limit the output to ping statistics
      -L : logging to a text file

      Example:
      fping_win_x64 www.baidu.com 192.168.1.1 www.google.com -t 1 -n 10 -w 100 -p -l
Using ICMP dll to ping with a thread pool
Reply[2] from 192.168.1.1: bytes=32 time=0.9 ms TTL=255
Reply[1] from www.baidu.com: bytes=32 time=36.4 ms TTL=54
93.46.8.89: request timed out

Reply[5] from 192.168.1.1: bytes=32 time=0.8 ms TTL=255
Reply[4] from www.baidu.com: bytes=32 time=36.8 ms TTL=54
93.46.8.89: request timed out

Reply[8] from 192.168.1.1: bytes=32 time=5.7 ms TTL=255
Reply[7] from www.baidu.com: bytes=32 time=62.9 ms TTL=54
93.46.8.89: request timed out

Reply[11] from 192.168.1.1: bytes=32 time=0.7 ms TTL=255
Reply[10] from www.baidu.com: bytes=32 time=36.1 ms TTL=54
93.46.8.89: request timed out

Reply[14] from 192.168.1.1: bytes=32 time=0.9 ms TTL=255
Reply[13] from www.baidu.com: bytes=32 time=39.3 ms TTL=54
93.46.8.89: request timed out

Reply[17] from 192.168.1.1: bytes=32 time=0.8 ms TTL=255
Reply[16] from www.baidu.com: bytes=32 time=36.4 ms TTL=54
93.46.8.89: request timed out

Reply[20] from 192.168.1.1: bytes=32 time=0.8 ms TTL=255
Reply[19] from www.baidu.com: bytes=32 time=36.6 ms TTL=54
93.46.8.89: request timed out

Reply[23] from 192.168.1.1: bytes=32 time=0.8 ms TTL=255
Reply[22] from www.baidu.com: bytes=32 time=37.1 ms TTL=54
93.46.8.89: request timed out

Reply[26] from 192.168.1.1: bytes=32 time=1.0 ms TTL=255
Reply[25] from www.baidu.com: bytes=32 time=36.3 ms TTL=54
93.46.8.89: request timed out

Reply[29] from 192.168.1.1: bytes=32 time=0.7 ms TTL=255
Reply[28] from www.baidu.com: bytes=32 time=36.7 ms TTL=54
93.46.8.89: request timed out

     */
    private void pingWin(final List<Target> targets) {
        int retry = 0;
        int timeout = 0;
        ArrayList<String> ipList = new ArrayList<>();
        HashMap<String, Target> targetMap = new HashMap<>();
        final HashMap<String, ArrayList<Double>> dataMap = new HashMap<>();
        for (Target target : targets) {
            targetMap.put(target.getIp(), target);
            dataMap.put(target.getIp(), new ArrayList<Double>());
            ipList.add(target.getIp());
            retry = Math.max(target.getRetry(), retry);
            timeout = Math.max(target.getTimeout(), timeout);
        }
        final ArrayList<String> commandList = new ArrayList<>();
        commandList.add(fping.toString());
        commandList.addAll(ipList);
        commandList.add("-t");
        commandList.add(String.valueOf(INTERVAL));
        commandList.add("-n");
        commandList.add(String.valueOf(retry));
        commandList.add("-w");
        commandList.add(String.valueOf(timeout));
        commandList.add("-p");
        commandList.add("-l");
        Process process = null;
        if (LOGGER.isInfoEnabled())
            LOGGER.info("Fping is ready to execute " + commandList);
        try {
            process = new ProcessBuilder(commandList).redirectErrorStream(true).start();
            final InputStream in = process.getInputStream();
            final Object lock = new Object();
            synchronized (lock) {
                Thread readerThread = new Thread("Fping-Reader") {
                    @Override
                    public void run() {
                        BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                        String line;
                        StringBuilder sb = new StringBuilder();
                        int debugLine = 0;
                        try {
                            while (!isInterrupted() && (line = reader.readLine()) != null) {
                                if (LOGGER.isDebugEnabled() && debugLine++ <= targets.size())
                                    sb.append(System.lineSeparator()).append(line);
                                Matcher matcher = WINDOWS_ALIVE.matcher(line);
                                if (matcher.find()) {
                                    try {
                                        ArrayList<Double> data = dataMap.get(matcher.group(1));
                                        if (data != null) {
                                            data.add(Double.valueOf(matcher.group(2)));
                                        }
                                    } catch (Exception e) {
                                        if (LOGGER.isWarnEnabled())
                                            LOGGER.warn("line parse error = " + line);
                                    }
                                }
                            }
                        } catch (IOException e) {
                            LOGGER.error("Fping executing encounters exception", e);
                        } finally {
                            synchronized (lock) {
                                lock.notify();
                            }
                            if (LOGGER.isDebugEnabled())
                                LOGGER.debug("Fping finish executing " + commandList + ", result = " + sb);

                        }
                    }
                };
                readerThread.start();
                lock.wait(MAX_TIMEOUT * 1000);
                readerThread.interrupt();
                process.destroy();
                if (LOGGER.isInfoEnabled())
                    LOGGER.info("Fping finish executing " + commandList + ", exitCode = " + process.waitFor());
            }
            for (Map.Entry<String, ArrayList<Double>> entry : dataMap.entrySet()) {
                String ip = entry.getKey();
                ArrayList<Double> rawDataList = entry.getValue();
                Target target = targetMap.get(ip);
                target.setLastExecuteTime(System.currentTimeMillis());
                if (rawDataList.isEmpty()) {
                    target.decreaseHealth();
                } else {
                    double sum = 0;
                    int count = rawDataList.size();
                    for (double rawData : rawDataList) {
                        sum += rawData;
                    }
                    double avg = sum / count;
                    double jitter = 0;
                    for (double rawData : rawDataList) {
                        jitter += Math.pow(rawData - avg, 2);
                    }
                    jitter = Math.pow(jitter / count, 0.5);
                    target.restoreHealth();
                    target.setLatency((int) avg);
                    target.setPacketLoss((retry - count) * 100 / count);
                    target.setJitter((int) jitter);
                }
            }
        } catch (IOException e) {
            LOGGER.error("Fping fails to execute " + commandList, e);
        } catch (InterruptedException e) {
            LOGGER.error("Fping executing interrupts, " + commandList, e);
        } finally {
            if (process != null)
                process.destroy();
        }
    }
}
