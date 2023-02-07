package reggieVersion1;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@MapperScan("reggieVersion1.mapper") //扫描mapper
@EnableTransactionManagement //开启事务管理
@EnableCaching //开启缓存功能
public class ReggieVersion1Application {

    public static void main(String[] args) {
        SpringApplication.run(ReggieVersion1Application.class, args);
    }

}
