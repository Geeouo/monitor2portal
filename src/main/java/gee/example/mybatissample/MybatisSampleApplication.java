package gee.example.mybatissample;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;

/**
 * @author Gee
 */
@MapperScan(basePackages = "gee.example.mybatissample.**.mapper")
@SpringBootApplication
public class MybatisSampleApplication extends SpringBootServletInitializer {
    public static void main(String[] args) {
        SpringApplication.run(MybatisSampleApplication.class, args);
    }

//    @Override
//    protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
//        return builder.sources(this.getClass());
//    }
}
