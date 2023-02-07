package reggieVersion1.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reggieVersion1.controller.utils.R;
import reggieVersion1.domain.User;
import reggieVersion1.service.UserService;
import reggieVersion1.utils.SmsCodeUtils;

import javax.servlet.http.HttpSession;
import java.util.Map;

/**
 * @projectName: Reggie_total
 * @package: reggieVersion1.controller
 * @className: UserController
 * @author: White
 * @description: 验证码登录
 * @date: 2023/1/29 9:27
 * @version: 1.0
 */
@Slf4j
@RestController
@RequestMapping("/user")
public class UserController {

    @Autowired
    private UserService userService;

    @Autowired
    private SmsCodeUtils smsCodeUtils;

    /**
     * 发送验证码
     *
     * @param session
     * @param user
     * @return
     */
    @PostMapping("/sendMsg")
    public R<String> sendMsg(HttpSession session, @RequestBody User user) {
        //获取手机号
        String phone = user.getPhone();
        if (StringUtils.isNotBlank(phone)) {
            //获取验证码
            String code = userService.sendCodeToSms(user.getPhone());
            log.info("验证码为：{}", code);

            return R.success("验证码发送成功");
        }
        return R.error("验证码发送失败，请稍候重试");
    }

    /**
     * 登录验证
     *
     * @param map
     * @param session
     * @return
     */
    @PostMapping("/login")
    public R<User> login(@RequestBody Map<String, String> map, HttpSession session) {

        //从map中获取验证码和手机号
        String tele = map.get("phone");
        String code = map.get("code");

        //从缓存中获取验证码
        String cacheCode = smsCodeUtils.getCache(tele);

        //进行验证码校验
        if (code.equals(cacheCode)) {
            //校验成功，判断是否为新用户
            User tempUser = userService.getOne(new LambdaQueryWrapper<User>()
                    .eq(User::getPhone, tele));

            if (tempUser == null) {
                //是新用户添加到用户表中
                tempUser = new User();
                tempUser.setPhone(tele);
                userService.save(tempUser);
            }
            //将用户保存到session
            session.setAttribute("user", tempUser.getId());

            return R.success(tempUser);
        } else {
            return R.error("验证失败，请重试");
        }
    }

    /**
     * 用户退出登录
     *
     * @param session
     * @return
     */
    @PostMapping("/loginout")
    public R<String> logout(HttpSession session) {
        //清理session中的用户id
        session.removeAttribute("user");
        return R.success("退出成功");
    }
}
