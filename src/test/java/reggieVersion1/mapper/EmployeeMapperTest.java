package reggieVersion1.mapper;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reggieVersion1.domain.Employee;

/**
 * @projectName: Reggie_total
 * @package: reggie_version1.mapper
 * @className: EmployeeMapperTest
 * @author: White
 * @description: TODO
 * @date: 2023/1/20 11:48
 * @version: 1.0
 */
@SpringBootTest
public class EmployeeMapperTest {
    @Autowired
    private EmployeeMapper employeeMapper;

    /*
        测试数据库连通性
     */
    @Test
    void testConnection(){
        System.out.println(employeeMapper.selectById(1));
    }

    /*
        测试查询一个
     */
    @Test
    void testSelectOne(){
        Employee emp =new Employee();
        emp.setUsername("admin");
        emp.setPassword("e10adc3949ba59abbe56e057f20f883e");
        LambdaQueryWrapper<Employee> lqw=new LambdaQueryWrapper<>();
        lqw.eq(Employee::getUsername,emp.getUsername()).and(empLqw -> empLqw.eq(Employee::getPassword,
                emp.getPassword()));
        System.out.println(employeeMapper.selectOne(lqw));
    }
}
