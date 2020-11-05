package net.ysq.video;

import com.spring4all.swagger.EnableSwagger2Doc;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import tk.mybatis.spring.annotation.MapperScan;


@EnableSwagger2Doc
@SpringBootApplication
@MapperScan("net.ysq.video.mapper") // 注意不要导入导错包！！！
@ComponentScan(basePackages = {"org.n3r.idworker", "net.ysq.video"})
public class YsqVideoApplication {

    public static void main(String[] args) {
        SpringApplication.run(YsqVideoApplication.class, args);
    }

}
