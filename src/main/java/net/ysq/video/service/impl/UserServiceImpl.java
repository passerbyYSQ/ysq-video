package net.ysq.video.service.impl;

import net.ysq.video.mapper.UserPoMapper;
import net.ysq.video.po.UserPo;
import net.ysq.video.service.UserService;

import org.n3r.idworker.Sid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import org.springframework.util.ResourceUtils;
import org.springframework.web.multipart.MultipartFile;
import tk.mybatis.mapper.entity.Example;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * @author passerbyYSQ
 * @create 2020-11-01 21:44
 */
@Service
public class UserServiceImpl implements UserService {

    @Autowired
    private UserPoMapper userPoMapper;

    @Autowired
    private Sid sid;

    @Override
    public String storeFile(MultipartFile file, String userId, String path) { // 233824/image
//         destDir = null;
        try {
            File classpath = ResourceUtils.getFile("classpath:");
            File destDir = new File(classpath, "upload/" + path);
            if (!destDir.exists()) {
                destDir.mkdirs();
            }

            // 原本的名文件名
            String originName = file.getOriginalFilename();
            // 截取后缀
            String suffix = originName.substring(originName.lastIndexOf("."));
            // 新的随机文件名
            String randFilename = sid.nextShort() + suffix;
            File destFile = new File(destDir, randFilename);

            // 从临时目录复制到指定目录
            file.transferTo(destFile);

            // 保存到数据库
            String dbPath = path + "/" + randFilename;
            UserPo user = new UserPo();
            user.setId(userId);
            user.setFaceImage(dbPath);
            int count = userPoMapper.updateByPrimaryKeySelective(user);
            return count == 1 ? dbPath : null;

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isUserExist(String username) {
        UserPo user = new UserPo();
        user.setUsername(username);

        UserPo resUser = userPoMapper.selectOne(user);
        return resUser != null;
    }

    @Override
    public UserPo register(String username, String password) {
        String userId = sid.nextShort();
        UserPo user = new UserPo();
        user.setId(userId);
        user.setUsername(username);
        // 密码进行md5加密
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());
        user.setPassword(md5Password);
        user.setNickname(username);
        user.setFansCounts(0);
        user.setFollowCounts(0);

        int count = userPoMapper.insert(user);
        return count == 1 ? user : null;
    }

    @Override
    public UserPo login(String username, String password) {
        // 密码进行md5加密
        String md5Password = DigestUtils.md5DigestAsHex(password.getBytes());

        Example userExample = new Example(UserPo.class);
        userExample.createCriteria()
                .andEqualTo("username", username)
                .andEqualTo("password", md5Password);

        UserPo user = userPoMapper.selectOneByExample(userExample);
        return user;
    }
}
