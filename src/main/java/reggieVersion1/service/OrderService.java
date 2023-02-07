package reggieVersion1.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.transaction.annotation.Transactional;
import reggieVersion1.domain.Order;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpSession;

/**
 * @author White
 * @description 针对表【orders(订单表)】的数据库操作Service
 * @createDate 2023-01-31 11:22:23
 */
public interface OrderService extends IService<Order> {

    /**
     * 用户下单
     *
     * @param order
     * @param session
     * @return
     */
    @Transactional
    //进行事务管理
    boolean submitOrder(Order order, HttpSession session);

    /**
     * 后台分页查询订单
     *
     * @param page
     * @param pageSize
     * @return
     */
    Page getPages(Integer page, Integer pageSize);

    /**
     * 后台分页查询订单，根据订单号查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    Page getPages(Integer page, Integer pageSize,String orderNumber);

    /**
     * 后台分页查询订单，根据时间段查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    Page getPages(Integer page, Integer pageSize,String beginTime,String endTime);

    /**
     * 后台分页查询订单，根据订单号和时间段查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    Page getPages(Integer page, Integer pageSize,String orderNumber,String beginTime,String endTime);

    /**
     * 前端用户查看自己的订单
     * @param page
     * @param pageSize
     * @param session
     * @return
     */
    Page frontGetPages(Integer page, Integer pageSize,HttpSession session);

    /**
     * 再下一单
     * @param order
     * @return
     */
    @Transactional
    boolean againSubmitOrder(Order order,HttpSession session);
}
