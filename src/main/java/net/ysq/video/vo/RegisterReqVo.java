package net.ysq.video.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * @author passerbyYSQ
 * @create 2020-11-02 10:55
 */
@ApiModel("注册请求的表单参数")
public class RegisterReqVo {

    @ApiModelProperty(value = "用户名", example = "ysq", required = true)
    @NotBlank
    @Size(min = 3, max = 32)
    private String username;

    @ApiModelProperty(value = "密码", example = "123456", required = true)
    @NotBlank
    @Size(min = 3, max = 32)
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
