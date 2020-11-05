package net.ysq.video.vo;

import net.ysq.video.po.UserPo;

/**
 * @author passerbyYSQ
 * @create 2020-11-01 21:43
 */
public class AccountRspVo {

    private String token;

    private UserPo user;

    public String getToken() {
        return token;
    }

    public void setToken(String token) {
        this.token = token;
    }

    public UserPo getUser() {
        return user;
    }

    public void setUser(UserPo user) {
        this.user = user;
    }
}
