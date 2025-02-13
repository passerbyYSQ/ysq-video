package net.ysq.video.common;

import java.time.LocalDateTime;

/**
 * @author passerbyYSQ
 * @create 2020-11-01 21:04
 */
public class ResultModel<T>  {

    // 状态码
    private Integer code;
    // 状态描述信息
    private String msg;
    // 返回的数据
    private T data;

    private LocalDateTime time = LocalDateTime.now();

    // 全参构造
    public ResultModel(Integer code, String msg, T data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }
    // 没有data的构造
    public ResultModel(Integer code, String msg) {
        this(code, msg, null);
    }

    // 成功。带有结果集
    public static <T> ResultModel<T> success(T data) {
        return new ResultModel<>(StatusCode.SUCCESS.getCode(), StatusCode.SUCCESS.getMsg(), data);
    }
    // 成功。没有结果集
    public static <T> ResultModel<T> success() {
        return success(null);
    }

    // 具体错误
    public static <T> ResultModel<T> error(Integer code, String msg) {
        return new ResultModel<>(code, msg);
    }

    // 具体错误，在枚举类中集中定义，然后传入指定枚举类对象
    public static <T> ResultModel<T> error(StatusCode code) {
        return error(code.getCode(), code.getMsg());
    }

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
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

    public LocalDateTime getTime() {
        return time;
    }

    public void setTime(LocalDateTime time) {
        this.time = time;
    }

}
