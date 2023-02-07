package reggieVersion1.config;

import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @projectName: Reggie_total
 * @package: reggieVersion1.config
 * @className: JacksonConfig
 * @author: White
 * @description: 重新构建Jackson序列化方式, 解决Long型数据发送给前端精度丢失
 * @date: 2023/1/31 12:38
 * @version: 1.0
 */
@Configuration
public class JacksonConfig {

    /**
     * 重新构建Jackson序列化方式
     * Jackson全局转化long类型为String，解决jackson序列化时传入前端Long类型缺失精度问题
     */
    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jackson2ObjectMapperBuilderCustomizer() {
        Jackson2ObjectMapperBuilderCustomizer customizer = jacksonObjectMapperBuilder -> {

            jacksonObjectMapperBuilder.serializerByType(Long.class, ToStringSerializer.instance);

            jacksonObjectMapperBuilder.serializerByType(Long.TYPE, ToStringSerializer.instance);
        };
        return customizer;
    }

    /**
     * 后端Long类型传到前端精度丢失的正确解决方式
     *   1.后端的Long类型的id转用String存储,不推荐,失去了其Long类型本身的意义。
     *
     *   2.在Long类型字段上使用@JsonSerialize(using = ToStringSerializer.class)注解标明序列化方式，代码量不大的情况可以考虑
     *
     *   3.实现WebMvcConfigurer接口，重写configureMessageConverters方法
     *        但是这种方式需要开启@EnableWebMvc注解。开启这个注解意味着springboot的mvc等自动配置失效，所以这个方式实际上也是不可取的。
     *        类似的还有继承WebMvcConfigurationSupport类，也会导致一些配置失效
     *
     *   4.重新注册ObjectMapper的Long类型序列化方式，推荐使用
     *
     *   5.重新构建Jackson序列化方式，与第四点类似的解决方式，推荐使用
     */
}
