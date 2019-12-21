package com.feihong.bean;

public class CommandExecutionResult {
    private String responseResult;
    private int responseStatusCode;
    private String exception;
    private String errorMsg;

    public String getResponseResult() {
        return responseResult;
    }

    public void setResponseResult(String responseResult) {
        this.responseResult = responseResult;
    }

    public int getResponseStatusCode() {
        return responseStatusCode;
    }

    public void setResponseStatusCode(int responseStatusCode) {
        this.responseStatusCode = responseStatusCode;
    }

    public String getException() {
        return exception;
    }

    public void setException(String exception) {
        this.exception = exception;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public void setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
    }

    @Override
    public String toString() {
        return "CommandExecutionResult{" +
                "responseResult='" + responseResult + '\'' +
                ", responseStatusCode=" + responseStatusCode +
                ", exception='" + exception + '\'' +
                ", errorMsg='" + errorMsg + '\'' +
                '}';
    }
}
