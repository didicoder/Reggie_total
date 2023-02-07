package reggieVersion1.interceptor;


import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;
import reggieVersion1.controller.utils.R;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


/**
 * @projectName: Reggie_total
 * @package: reggie_version1.filter
 * @className: LoginInterceptor
 * @author: White
 * @description: 检查用户是否登录的过滤器，防止直接访问主页
 * @date: 2023/1/20 16:28
 * @version: 1.0
 */
@Component
public class LoginInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        //1.获取请求路径
        String uri = request.getRequestURI();
        //判断是否登录请求
        if (uri.contains("login")) {
            return true;
        } else {
            //判断用户是否登录
            if (request.getSession().getAttribute("employee") != null) {
                //已经登录过，放行
                return true;
            } else if (request.getSession().getAttribute("user") != null) {
                return true;
            }
            //判断用户是否后端登录
            else {
                //使用jackson替代fastjson
                response.getWriter().write(new ObjectMapper()
                        .writeValueAsString(R.error("NOTLOGIN")));
            }
        }
        //默认拦截
        return false;
    }
}
