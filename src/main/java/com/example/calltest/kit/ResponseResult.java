package com.example.calltest.kit;

public class ResponseResult<T> {

    //状态码，0：失败；1：成功
    private Integer status;

    //返回的消息
    private String msg;

    //返回的数据
    private T data;

    //不带返回数据
    public ResponseResult(Integer status, String msg) {
        this.status = status;
        this.msg = msg;
    }

    //带返回数据
    public ResponseResult(Integer status, String msg, T data) {
        this.status = status;
        this.msg = msg;
        this.data = data;
    }

    /**
     * 状态码 + 成功提示信息
     */
    public static <T> ResponseResult<T> success(String msg) {
        return new ResponseResult<>(1, msg);
    }

    /**
     * 状态码 + 成功提示信息 + 数据
     */
    public static <T> ResponseResult<T> success(String msg, T data) {
        return new ResponseResult<>(1, msg, data);
    }

    /**
     * 状态码 + 错误信息
     */
    public static <T> ResponseResult<T> error(String msg) {
        return new ResponseResult<>(0, msg);
    }

    public Integer getStatus() {
        return status;
    }

    public void setStatus(Integer status) {
        this.status = status;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }
}
