package reggieVersion1.service.impl;

import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CachePut;
import reggieVersion1.domain.User;
import reggieVersion1.service.UserService;
import reggieVersion1.mapper.UserMapper;
import org.springframework.stereotype.Service;
import reggieVersion1.utils.SmsCodeUtils;

/**
 * @author White
 * @description 针对表【user(用户信息)】的数据库操作Service实现
 * @createDate 2023-01-29 09:16:37
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Autowired
    private SmsCodeUtils smsCodeUtils;

    /**
     * 发送验证码
     *
     * @param tele
     * @return
     */
    @Override
    @CachePut(value = "smsCode", key = "#tele") //将验证码放入缓存，但并不从缓存中读取
    public String sendCodeToSms(String tele) {
        return smsCodeUtils.getCode(tele);
    }


    /**
     * 验证码校验
     *
     * @param phone
     * @param code
     * @return
     */
    @Override
    public boolean checkCode(String phone, String code) {
        //将smsCode.code和缓存中的验证码进行比较
        if (StringUtils.isNotBlank(code)) {
            return code.equals(
                    //从缓存中获取之前存储的验证码
                    smsCodeUtils.getCode(
                            //使用phone作为key从redis中获取验证码
                            phone));
        } else {
            return false;
        }
    }

}




