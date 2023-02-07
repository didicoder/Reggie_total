package reggieVersion1.utils;

import com.baomidou.mybatisplus.generator.FastAutoGenerator;
import com.baomidou.mybatisplus.generator.config.OutputFile;
import com.baomidou.mybatisplus.generator.engine.FreemarkerTemplateEngine;

import java.util.Collections;

/**
 * @projectName: Reggie_total
 * @package: reggieVersion1.utils
 * @className: CodeGenerator
 * @author: White
 * @description: 使用Mybatis-Plus代码生成器逆向生成代码
 * @date: 2023/1/21 21:25
 * @version: 1.0
 */
public class CodeGenerator {

    public static void main(String[] args) {
        FastAutoGenerator
                //获取MySQL数据库连接
                .create("jdbc:mysql://localhost:3306/reggie", "root", "76360127")
                .globalConfig(builder -> {
                    builder
                            .author("White") // 设置作者
                            // .enableSwagger() // 开启 swagger 模式
                            .fileOverride() // 覆盖已生成文件
                            .outputDir("D:\\Tools\\Languages\\Code_Total\\Reggie_total\\reggie_version1\\src\\main" + "\\java\\reggieVersion1"); // 指定输出目录
                })
                .packageConfig(builder -> {
                    builder.parent("codeGenerator") // 设置父包名
                            .pathInfo(Collections.singletonMap(OutputFile.xml, "D:\\Tools\\Languages" +
                                    "\\Code_Total\\MyBatisPlus\\mp_06_CodeGenerator\\src\\main\\resources\\mapper"));// 设置mapperXml生成路径
                })
                .strategyConfig(builder -> {
                    builder.addInclude("category") // 设置需要生成的表名
                            .addTablePrefix("t_", "c_"); // 设置过滤表前缀
                }).templateEngine(new FreemarkerTemplateEngine()) // 使用Freemarker 引擎模板，默认的是Velocity引擎模板
                .execute();
    }
}
