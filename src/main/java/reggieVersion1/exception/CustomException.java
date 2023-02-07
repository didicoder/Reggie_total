package reggieVersion1.exception;

import org.springframework.stereotype.Component;

/**
 * @projectName: Reggie_total
 * @package: reggieVersion1.config
 * @className: CustomException
 * @author: White
 * @description: 自定义业务异常类
 * @date: 2023/1/26 10:06
 * @version: 1.0
 */

public class CustomException extends RuntimeException {
    /**
     * 抛出带有提示信息的异常
     *
     */
    public CustomException(String message) {
        super(message);
    }
}
