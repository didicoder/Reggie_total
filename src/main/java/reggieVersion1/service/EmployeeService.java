package reggieVersion1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import reggieVersion1.controller.utils.R;
import reggieVersion1.domain.Employee;

import javax.servlet.http.HttpSession;

/**
 * @projectName: Reggie_total
 * @package: reggie_version1.service
 * @className: EmployeeService
 * @author: White
 * @description: TODO
 * @date: 2023/1/20 11:01
 * @version: 1.0
 */
public interface EmployeeService extends IService<Employee> {
    /**
     * 登录功能
     *
     * @param employee
     * @return
     */
    R login(Employee employee);

    /**
     * 添加员工功能
     *
     * @param session
     * @param employee
     * @return
     */
    R insertEmp(HttpSession session, Employee employee);

    /**
     * 分页功能
     * @param page
     * @param pageSize
     * @return
     */
    R getPages(Integer page,Integer pageSize);

    /**
     * 根据员工姓名分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    R getPages(Integer page,Integer pageSize,String name);

}
