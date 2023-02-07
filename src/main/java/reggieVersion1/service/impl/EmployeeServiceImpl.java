package reggieVersion1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reggieVersion1.controller.utils.R;
import reggieVersion1.domain.Employee;
import reggieVersion1.mapper.EmployeeMapper;
import reggieVersion1.service.EmployeeService;

import javax.servlet.http.HttpSession;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.Objects;

/**
 * @projectName: Reggie_total
 * @package: reggie_version1.service.impl
 * @className: EmployeeServiceImpl
 * @author: White
 * @description: TODO
 * @date: 2023/1/20 11:03
 * @version: 1.0
 */
@Service
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {
    @Autowired
    private EmployeeMapper employeeMapper;

    /**
     * 员工登录
     *
     * @param employee
     * @return
     */
    @Override
    public R<Employee> login(Employee employee) {
        //1、将页面提交的密码password进行Base64加密处理
        String password = Base64.getEncoder().encodeToString(employee.getPassword().getBytes(StandardCharsets.UTF_8));

        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> empWrapper = new LambdaQueryWrapper<>();
        empWrapper.eq(Employee::getUsername, employee.getUsername());
        Employee emp = employeeMapper.selectOne(empWrapper);

        //3、如果没有查询到则返回登录失败结果
        if (Objects.isNull(emp)) {
            return R.error("登录失败！");
        }

        //4、密码比对，如果不一致则返回登录失败结果
        if (!password.equals(emp.getPassword())) {
            return R.error("登录失败！");
        }

        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0) {
            return R.error("账号已禁用！");
        }
        return R.success(emp);
    }

    /**
     * 添加员工功能
     *
     * @param session
     * @param employee
     * @return
     */
    @Override
    public R<Employee> insertEmp(HttpSession session, Employee employee) {
        //判断新增员工不为空
        if (employee != null) {
            employee.setPassword(Base64.getEncoder().encodeToString(employee.getPassword().getBytes()));
            /*
            //为新增员工添加其他属性
            employee.setCreateTime(LocalDateTime.now());
            employee.setUpdateTime(LocalDateTime.now());
            employee.setCreateUser(1l); //默认为管理员用户添加员工
            employee.setUpdateUser((Long) session.getAttribute("employee"));
             */

            //使用Mybatis-Plus的MetaObjectHandler，对数据的添加进行填充

            //判断添加员工是否成功
            if (employeeMapper.insert(employee) > 0) {
                return R.success(employee);
            } else {
                return R.error("添加员工失败");
            }
        }
        return R.error("添加员工失败");
    }

    @Override
    public R<Page> getPages(Integer page, Integer pageSize) {
        //构造分页构造器
        Page<Employee> tempPage = new Page(page, pageSize);

        //进行分页查询，使用lqw对象不查询管理员用户
        tempPage = employeeMapper.selectPage(tempPage, new LambdaQueryWrapper<Employee>().ne(Employee::getUsername,
                "admin"));

        return R.success(tempPage);
    }

    @Override
    public R<Page> getPages(Integer page, Integer pageSize, String name) {
        //构造分页构造器
        Page<Employee> tempPage = new Page(page, pageSize);

        //进行分页查询，并按更新时间排序
        tempPage = employeeMapper.selectPage(tempPage,
                new LambdaQueryWrapper<Employee>()
                        .ne(Employee::getUsername, "admin")
                        .like(StringUtils.isNotBlank(name), Employee::getName, name)
                        .orderByAsc(Employee::getId));

        return R.success(tempPage);
    }
}
