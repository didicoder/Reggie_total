package reggieVersion1.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.netty.util.internal.EmptyPriorityQueue;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;
import reggieVersion1.controller.utils.R;
import reggieVersion1.domain.Employee;
import reggieVersion1.service.EmployeeService;

import javax.servlet.http.HttpSession;
import javax.xml.transform.Source;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.util.Base64;
import java.util.List;

/**
 * @projectName: Reggie_total
 * @package: reggie_version1.controller
 * @className: EmployeeController
 * @author: White
 * @description: TODO
 * @date: 2023/1/20 11:04
 * @version: 1.0
 */
@RestController
@RequestMapping("/employee")
public class EmployeeController {
    @Autowired
    private EmployeeService employeeService;

    /**
     * 员工登录
     *
     * @param session  用来存放用户登录id
     * @param employee 将前端数据封装成JSON对象
     * @return 返回查询后的结果
     */
    @PostMapping("/login")
    public R<Employee> login(HttpSession session, @RequestBody Employee employee) {

        R<Employee> login = employeeService.login(employee);
        System.out.println(login);
        if (login != null) {
            Employee sessionEmp = login.getData();
            //6、登录成功，将员工id存入Session并返回登录成功结果
            session.setAttribute("employee", sessionEmp.getId());
        }
        return login;
    }

    /**
     * 员工退出功能
     *
     * @param session
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpSession session) {
        //1、清理Session中的用户id
        session.removeAttribute("employee");

        //2、返回结果
        return R.success("退出成功！");
    }

    /**
     * 添加员工功能
     *
     * @param session
     * @param employee
     * @return
     */
    @PostMapping
    public R<Employee> insertEmployee(HttpSession session, @RequestBody Employee employee) {
        return employeeService.insertEmp(session, employee);

    }

    /**
     * 员工信息分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page/{page}/{pageSize}")
    public R<Page> getPages(@PathVariable Integer page, @PathVariable Integer pageSize) {
        return employeeService.getPages(page, pageSize);
    }

    /**
     * 根据姓名进行员工信息分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page/{page}/{pageSize}/{name}")
    public R<Page> getPagesWithName(@PathVariable Integer page, @PathVariable Integer pageSize, @PathVariable(required = false) String name) {
        return employeeService.getPages(page, pageSize, name);
    }

    /**
     * 根据id禁用员工
     *
     * @param session
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> disableEmployee(HttpSession session, @RequestBody Employee employee) {
        Long empId = (Long) session.getAttribute("employee");

        //判断是否禁用管理员
        if (employee.getId() != 1) {
            employee.setUpdateTime(LocalDateTime.now());
            employee.setUpdateUser(empId);
            //status状态在前端时已经进行取反，后端不需要修改
            employeeService.updateById(employee);
            return R.success("员工信息修改成功");
        } else {
            return R.error("禁用失败");
        }
    }

    /**
     * 根据id查询员工
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getEmployeeById(@PathVariable String id){
        Long tempId= Long.parseLong(id);
        Employee tempEmp = employeeService.getById(tempId);
        if (tempEmp!=null){
            tempEmp.setPassword(new String(Base64.getDecoder().decode(tempEmp.getPassword()), StandardCharsets.UTF_8));
            return R.success(tempEmp);
        }
        return R.error("未查询到该员工信息");
    }
}
