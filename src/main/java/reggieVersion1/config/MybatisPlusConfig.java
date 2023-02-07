package reggieVersion1.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @projectName: Reggie_total
 * @package: reggieVersion1.config
 * @className: MybatisPlusConfig
 * @author: White
 * @description: 配置Mybatis-Plus分页插件
 * @date: 2023/1/21 9:41
 * @version: 1.0
 */
@Configuration //标识为配置类
public class MybatisPlusConfig {
    @Bean //配置为bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();

        //添加分页插件
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));

        return interceptor;
    }
}
