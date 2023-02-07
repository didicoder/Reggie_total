package reggieVersion1.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.StringHttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import reggieVersion1.interceptor.LoginInterceptor;

import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 * @projectName: Reggie_total
 * @package: reggie_version1.config
 * @className: SpringMvcConfig
 * @author: White
 * @description: 注册拦截器
 * @date: 2023/1/20 16:57
 * @version: 1.0
 */
@Configuration
public class SpringMvcConfig implements WebMvcConfigurer {

    /**
     * 拦截器配置
     *
     * @param registry
     */
    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new LoginInterceptor())
                .addPathPatterns("/**")
                .excludePathPatterns("/employee/login",
                        "/employee/logout",
                        "/backend/**",
                        "/front/**",
                        "common/**",
                        "/user/sendMsg",
                        "user/login");
    }
}
