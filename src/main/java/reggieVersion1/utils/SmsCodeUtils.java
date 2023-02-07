package reggieVersion1.utils;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Component;

import java.util.Date;

/**
 * @projectName: Reggie_total
 * @package: reggieVersion1.utils
 * @className: SmsCodeUtils
 * @author: White
 * @description: 生成验证码工具类
 * @date: 2023/1/28 19:00
 * @version: 1.0
 */
@Component
public class SmsCodeUtils {
    //对验证码进行补零的数组
    private static String[] zeroArr = new String[]{"000000", "00000", "0000", "000", "00", "0", ""};

    /**
     * 根据电话号码生成验证码
     *
     * @param tele
     * @return
     */
    public String getCode(String tele) {
        //1.获取电话号码的hashcode值
        int teleHash = tele.hashCode();

        //2.设置加密明文
        int encryption = 20230128;

        //3.进行异或操作
        long result = teleHash ^ encryption;

        //4.获取当前时间
        long nowTime = System.currentTimeMillis();
        result = result ^ nowTime;

        //5.获取六位验证码
        long code = Math.abs(result % 1000000);

        //6.问题解决——不足六位
        String codeStr=String.format("%06d",code);

        return codeStr;
    }

    /**
     * 从缓存中获取验证码
     *
     * @param tele
     * @return
     */
    @Cacheable(value = "smsCode",key = "#tele")
    public String getCache(String tele) {
        return null;
    }
}
