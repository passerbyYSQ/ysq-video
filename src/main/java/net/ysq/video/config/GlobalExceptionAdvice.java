package net.ysq.video.config;

import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import jdk.net.SocketFlow;
import net.ysq.video.common.StatusCode;
import net.ysq.video.common.ResultModel;
import org.springframework.http.HttpStatus;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * @author passerbyYSQ
 * @create 2020-11-02 23:27
 */
@ControllerAdvice
@ResponseBody
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class GlobalExceptionAdvice {

    // 参数校验不通过的异常统一捕获处理
//    BindException
    @ExceptionHandler(BindException.class)
    public ResultModel handleValidationException(
            HttpServletRequest request, BindException e) {
        StringBuffer errorMsg = new StringBuffer();
        List<FieldError> errors = e.getFieldErrors();
        for (FieldError error : errors) {
            errorMsg.append(error.getField())
                    .append(error.getDefaultMessage())
                    .append(";");
        }
        return ResultModel.error(6000, errorMsg.toString());
    }

    @ExceptionHandler(JWTVerificationException.class)
    public ResultModel handlerJwtVerificationException(JWTVerificationException e) {
        // 主要分为两类错误
        if (e instanceof TokenExpiredException) {
            // token过期，登录状态过期
            return ResultModel.error(StatusCode.TOKEN_IS_EXPIRED);
        }
        // 无效token（有可能被修改了等原因导致验证失败）
        return ResultModel.error(StatusCode.TOKEN_IS_INVALID);
    }


    // 前后端联调时和正式上线后开启
    // 后端编码时，为了方便测试，先注释掉
//    @ExceptionHandler({Exception.class})
//    public ResultModel handleOtherException(Exception e) {
//        // 判断异常中是否有错误信息，如果存在就使用异常中的消息，否则使用默认消息
//        if (!StringUtils.isEmpty(e.getMessage())) {
//            return ResultModel.error(5000, e.getMessage());
//        }
//        return ResultModel.error(StatusCode.UNKNOWN_ERROR);
//    }

}
