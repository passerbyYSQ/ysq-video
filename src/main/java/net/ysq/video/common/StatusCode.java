package net.ysq.video.common;

/**
 * 2000 - 成功处理请求
 * 3*** - 重定向，需要进一步的操作已完成请求
 * 4*** - 客户端错误，请求参数错误，语法错误等等
 * 5*** - 服务器内部错误
 * ...
 *
 * @author passerbyYSQ
 * @create 2020-11-02 16:26
 */
// 不加上此注解，Jackson将对象序列化为json时，直接将枚举类转成它的名字
//@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum StatusCode {
    SUCCESS(2000, "成功"),

    // 服务器内部错误
    UNKNOWN_ERROR(5000, "未知错误"),

    // 参数相关
    PARAM_NOT_COMPLETED(6001, "参数缺失"),
    PARAM_IS_INVALID(6002, "参数无效"),
    FILE_TYPE_INVALID(6003, "文件类型非法"),
    FILE_SIZE_EXCEEDED(6004, "文件大小超出限制"),

    // 用户相关
    USER_IS_EXIST(6101, "用户已存在"),
    USER_NOT_EXIST(6102, "用户不存在"),

    // 账户相关
    TOKEN_IS_MISSING(6200, "token缺失"),
    FORCED_OFFLINE(6201, "异地登录，当前账户被迫下线"),
    TOKEN_IS_EXPIRED(6202, "token已过期"),
    TOKEN_IS_INVALID(6203, "无效token"),
    LOGIN_FAILED(6204, "登录失败，用户名或密码错误")
    ;

    // 状态码数值
    private Integer code;
    // 状态码描述信息
    private String msg;

    StatusCode(Integer code, String msg) {
        this.code = code;
        this.msg = msg;
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
}
