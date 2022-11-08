package com.mainsteam.stm.webService.performance;

/**
 * Created by Xiaopf on 2017/4/5.
 */
public enum ResourceCategoryEnum {

    WEBLOGIC("Weblogic"), ORACLE("Oracles");

    private String name;

    ResourceCategoryEnum(String name) {
        this.name = name;
    }

    public static boolean contains(String value) {
        try{
            for(ResourceCategoryEnum categoryEnum : ResourceCategoryEnum.values()) {
                if(categoryEnum == ResourceCategoryEnum.valueOf(value.toUpperCase())){
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
        System.out.println(ResourceCategoryEnum.ORACLE.getName());
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
