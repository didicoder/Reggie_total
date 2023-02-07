package reggieVersion1.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reggieVersion1.domain.Employee;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

/**
 * @projectName: Reggie_total
 * @package: reggieVersion1.config
 * @className: MyMetaObjectHandler
 * @author: White
 * @description: 元数据处理器——解决公共字段填充问题
 * @date: 2023/1/21 19:20
 * @version: 1.0
 */

/**
 * 自定义元数据对象处理器
 */
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {
    //自动装配session对象，从session中获取当前登录用户信息
    @Autowired
    HttpSession session;

    /**
     * 插入操作自动填充
     *
     * @param metaObject
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        metaObject.setValue("createTime", LocalDateTime.now());
        metaObject.setValue("updateTime", LocalDateTime.now());

        //存在问题，不能通过session获取修改操作的用户的id
        metaObject.setValue("createUser",
                session.getAttribute("employee") != null
                        ? session.getAttribute("employee")
                        : session.getAttribute("user"));

        metaObject.setValue("updateUser",
                session.getAttribute("employee") != null
                        ? session.getAttribute("employee")
                        : session.getAttribute("user"));
    }

    /**
     * 更新操作自动填充
     *
     * @param metaObject
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        metaObject.setValue("updateTime", LocalDateTime.now());
        metaObject.setValue("updateUser",
                session.getAttribute("employee") != null
                        ? session.getAttribute("employee")
                        : session.getAttribute("user"));
    }

}
