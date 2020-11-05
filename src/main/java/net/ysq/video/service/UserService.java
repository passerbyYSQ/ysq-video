package net.ysq.video.service;

import net.ysq.video.po.UserPo;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;

/**
 * @author passerbyYSQ
 * @create 2020-11-01 21:40
 */
public interface UserService {

    String storeFile(MultipartFile file, String userId, String path);

    // 用户名是否已经注册
    boolean isUserExist(String username);

    // 注册用户
    UserPo register(String username, String password);

    // 登录
    UserPo login(String username, String password);
}
