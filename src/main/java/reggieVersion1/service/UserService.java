package reggieVersion1.service;

import reggieVersion1.domain.User;
import com.baomidou.mybatisplus.extension.service.IService;

/**
 * @author White
 * @description 针对表【user(用户信息)】的数据库操作Service
 * @createDate 2023-01-29 09:16:37
 */
public interface UserService extends IService<User> {

    /**
     * 向SIM卡发送验证码
     *
     * @param tele
     * @return
     */
    String sendCodeToSms(String tele);

    /**
     * 校验验证码操作
     * @param phone
     * @param code
     * @return
     */
    boolean checkCode(String phone ,String code);

}
