package net.ysq.video.component;

import com.fasterxml.jackson.databind.ObjectMapper;
import net.ysq.video.common.ResultModel;
import net.ysq.video.common.StatusCode;
import net.ysq.video.utils.JwtUtil;
import net.ysq.video.utils.RedisUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.jackson.JacksonAutoConfiguration;
import org.springframework.boot.autoconfigure.jackson.JacksonProperties;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;

/**
 * @author passerbyYSQ
 * @create 2020-11-04 22:05
 */
@Component // 加入IOC容器中，以用于在配置类中注册拦截器
public class LoginInterceptor extends HandlerInterceptorAdapter {

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private RedisUtil redisUtil;

    @SuppressWarnings("unchecked")
    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response,
                             Object handler) throws Exception {
        // 从请求头中获取
        String token = request.getHeader("Authorization");
        if (StringUtils.isEmpty(token)) {
            // 从请求头中的token字段（自定义字段）尝试获取jwt token
            token = request.getHeader("token");
        }
        if (StringUtils.isEmpty(token)) {
            // 从url参数中尝试获取jwt token
            token = request.getParameter("token");
        }

        ResultModel<Object> result = null;
        String jsonResult = null;
        response.setContentType("application/json; charset=utf-8");

        if (StringUtils.isEmpty(token)) {
            result = ResultModel.error(StatusCode.TOKEN_IS_MISSING);
            jsonResult = objectMapper.writeValueAsString(result);
            response.getWriter().write(jsonResult);
            return false; // 拦截

        } else {
            // 解析失败会抛出异常，全局捕获
            String userId = JwtUtil.getClaimByKey(token, "userId");

            // 如果没有抛出异常，在Redis中寻找对应的登录信息
            Map<String, String> map = (Map<String, String>) redisUtil.get("user:" + userId);
            if (StringUtils.isEmpty(map)) {
                // 一份未过期的token重复提交过来，会造成map为null。因为redis中的key已被删除
                return false;
            }
            String redisToken = map.get("token");
            map.remove("token");

            // 验证失败（过期，无效等）会抛出异常，全局捕获
            JwtUtil.verifyJwt(token, map);

            // 如果验证成功，将来自客户端的token与redis中的token进行比较
            // 如果不一致，说明账号被其他人在其他地方登录了。拦截该请求，要求重新登录
            if (!token.equals(redisToken)) {
                result = ResultModel.error(StatusCode.FORCED_OFFLINE);
                jsonResult = objectMapper.writeValueAsString(result);
                response.getWriter().write(jsonResult);
                return false;
            }

            // 如果最终来到这里，则放行请求
            // 在放行之前，将用户信息放到request，方便controller中取用
            request.setAttribute("userId", map.get("userId"));
            return true;
        }

    }
}
