package reggieVersion1.exception;


import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import reggieVersion1.controller.utils.R;

import java.sql.SQLIntegrityConstraintViolationException;

/**
 * @projectName: Reggie_total
 * @package: reggieVersion1.utils
 * @className: GlobalExceptionHandler
 * @author: White
 * @description: 全局异常处理器
 * @date: 2023/1/21 8:11
 * @version: 1.0
 */
@RestControllerAdvice //拦截 @RestController注解
public class GlobalExceptionHandler {
    /**
     * 处理SQLIntegrityConstraintViolationException异常——重复添加员工
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = {SQLIntegrityConstraintViolationException.class})
    public R<String> handleException(SQLIntegrityConstraintViolationException ex) {
        if (ex.getMessage().contains("Duplicate entry")) {
            return R.error("数据已存在，请勿重复添加");
        } else {
            System.out.println(ex.getMessage());
            return R.error("服务器繁忙，清稍后重试");
        }
    }

    /**
     * 处理CustomException异常——不能删除分类
     *
     * @param ex
     * @return
     */
    @ExceptionHandler(value = {CustomException.class})
    public R<String> handleException(CustomException ex) {
        if (ex.getClass() == CustomException.class) {
            return R.error(ex.getMessage());
        } else {
            System.out.println(ex.getMessage());
            return R.error("服务器繁忙，清稍后重试");
        }
    }
}
