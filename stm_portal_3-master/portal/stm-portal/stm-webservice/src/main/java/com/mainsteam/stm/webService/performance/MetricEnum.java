package com.mainsteam.stm.webService.performance;

/**
 * Created by Xiaopf on 2017/4/5.
 */
public enum MetricEnum {

    JVM_MEM_RATE("JVMMEMRate"), THROUGHPUT("Throughput"), SESSION_PER_SECOND("sessionPerSecond"), BLOCK_LOCK_COUNT("blockLockCount");

    private String name;

    private MetricEnum(String name) {
        this.name = name;
    }

    public static boolean contains(String value) {
        try{
            for(MetricEnum metricEnum : MetricEnum.values()) {
                if(metricEnum == MetricEnum.valueOf(value.toUpperCase())){
                    return true;
                }
            }
        }catch (Exception e){
            return false;
        }
        return false;
    }

    public String getName() {
        return name;
    }

    public static void main(String[] args) {
        System.out.println(MetricEnum.valueOf("jvm_mem_rate"));
    }
}
