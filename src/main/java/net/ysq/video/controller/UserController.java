package net.ysq.video.controller;

import io.swagger.annotations.Api;
import net.ysq.video.common.ResultModel;
import net.ysq.video.common.StatusCode;
import net.ysq.video.mapper.UserPoMapper;
import net.ysq.video.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.web.servlet.MultipartAutoConfiguration;
import org.springframework.boot.autoconfigure.web.servlet.MultipartProperties;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.util.unit.DataSize;
import org.springframework.util.unit.DataUnit;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import org.springframework.web.util.UriComponentsBuilder;

import javax.servlet.http.HttpServletRequest;
import javax.validation.constraints.NotNull;
import javax.xml.crypto.Data;
import java.io.File;

/**
 * @author passerbyYSQ
 * @create 2020-11-05 11:23
 */
@Api("用户相关业务的接口")
@RestController
@RequestMapping("/api/user")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping("/upload/photo")
    public ResultModel<String> uploadPhoto(@NotNull MultipartFile file, HttpServletRequest request) {
        // 检查文件格式是否合法
        boolean isValid = false;
        String[] allowSuffix = { ".jpg", ".png", ".jpeg" };
        for (String suffix : allowSuffix) {
            if (file.getOriginalFilename().endsWith(suffix)) {
                isValid = true;
                break;
            }
        }
        if (!isValid || !file.getContentType().startsWith("image/")) {
            return ResultModel.error(StatusCode.FILE_TYPE_INVALID);
        }

        // 图片的实际大小
        DataSize size = DataSize.of(file.getSize(), DataUnit.BYTES);
        // 限制5M
        DataSize limit = DataSize.of(5, DataUnit.MEGABYTES);
        if (size.compareTo(limit) > 0) {
            String msg = String.format("当前文件大小为 %d MB，最大允许大小为 %d MB",
                    size.toMegabytes(), limit.toMegabytes());
            return ResultModel.error(StatusCode.FILE_SIZE_EXCEEDED.getCode(), msg);
        }

        String userId = (String) request.getAttribute("userId");
        String path = userId + "/image";
        String dbPath = userService.storeFile(file, userId, path);
        if (StringUtils.isEmpty(dbPath)) {
            return ResultModel.error(StatusCode.UNKNOWN_ERROR);
        }

        // 返回完整的url
        String url = ServletUriComponentsBuilder
                .fromCurrentContextPath()
                .path("/upload/")
                .path(dbPath)
                .toUriString();
        return ResultModel.success(url);
    }

}
