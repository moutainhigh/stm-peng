package com.mainsteam.stm.webService.cmdb.server;

/**
 * User: kevins
 * Date:2012-12-07
 * Time: 17-04
 */
public class Result<T> {
	/**
     * 方法返回码，异常时为异常码表中定义的异常代码
     */
    private String resultCode;
    /**
     * 异常信息，由各厂商实现时自己定义
     */
    private String errorMsg;
    /**
     * 方法返回的数据集，可以是字符对象、数组对象的格式串
     */
    private T data;

    public Result() {
        //To change body of created methods use File | Settings | File Templates.
    }

    public String getResultCode() {
        return resultCode;
    }

    public void setResultCode(String resultCode) {
        this.resultCode = resultCode;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public Result(String resultCode, String errorMsg, T data) {
        this.resultCode = resultCode;
        this.errorMsg = errorMsg;
        this.data = data;
    }

    public Result(String resultCode, String errorMsg) {
        this.resultCode = resultCode;
        this.errorMsg = errorMsg;
    }

    public Result(String resultCode) {
        this.resultCode = resultCode;
    }
}
