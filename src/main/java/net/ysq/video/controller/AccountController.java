package net.ysq.video.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import net.ysq.video.common.StatusCode;
import net.ysq.video.utils.JwtUtil;
import net.ysq.video.utils.RedisUtil;
import net.ysq.video.vo.AccountRspVo;
import net.ysq.video.vo.RegisterReqVo;
import net.ysq.video.common.ResultModel;
import net.ysq.video.po.UserPo;
import net.ysq.video.service.UserService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.util.FileSystemUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author passerbyYSQ
 * @create 2020-11-01 20:58
 */
@RestController
@Api("用户账户相关的接口")
@RequestMapping("/api/account")
//@Validated
public class AccountController {

    @Autowired
    private UserService userService;

    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("/test")
    public String test() throws FileNotFoundException {
//        return redisUtil.get("user:20110507FCDCGRD4");
        File file = ResourceUtils.getFile("classpath:");
        return file.getAbsolutePath();
    }

    @ApiOperation("用户注册")
    @PostMapping("/register")
    public ResultModel<AccountRspVo> register(@Validated RegisterReqVo reqVo) {
        if (userService.isUserExist(reqVo.getUsername())) {
            return ResultModel.error(StatusCode.USER_IS_EXIST); //用户已存在
        }

        UserPo user = userService.register(reqVo.getUsername(), reqVo.getPassword());
        if (ObjectUtils.isEmpty(user)) {
            return ResultModel.error(StatusCode.UNKNOWN_ERROR); // 未知错误
        }

        // 将登录信息存储到Redis
        AccountRspVo rspVo = keepLoginStatus(user);
        return ResultModel.success(rspVo);
    }

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public ResultModel<AccountRspVo> login(@Validated RegisterReqVo reqVo) {
        UserPo user = userService.login(reqVo.getUsername(), reqVo.getPassword());
        if (ObjectUtils.isEmpty(user)) {
            return ResultModel.error(StatusCode.LOGIN_FAILED);
        }
        // 将登录信息存储到Redis
        AccountRspVo rspVo = keepLoginStatus(user);
        return ResultModel.success(rspVo);
    }

    @ApiOperation("退出登录")
    @PostMapping("/logout")
    public ResultModel logout(HttpServletRequest request) {
        String userId = (String) request.getAttribute("userId");
        redisUtil.del("user:" + userId);
        return ResultModel.success();
    }

    private AccountRspVo keepLoginStatus(UserPo user) {
        // jwt 载荷
        Map<String, String> map = new HashMap<>();
        map.put("userId", user.getId());
        map.put("username", user.getUsername());
        // 生成jwt
        String token = JwtUtil.generateJwt(map);
        map.put("token", token);

        // 将token和载荷信息存到redis
        long seconds = TimeUnit.MILLISECONDS.toSeconds(JwtUtil.EFFECTIVE_DURATION);
        redisUtil.set("user:"+ user.getId(), map, seconds);

        AccountRspVo rspVo = new AccountRspVo();
        rspVo.setToken(token);
        rspVo.setUser(user);

        return rspVo;
    }

}
