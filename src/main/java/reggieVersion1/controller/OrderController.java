package reggieVersion1.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reggieVersion1.controller.utils.R;
import reggieVersion1.domain.Order;
import reggieVersion1.service.OrderService;
import reggieVersion1.service.ShoppingCartService;

import javax.servlet.http.HttpSession;

/**
 * @projectName: Reggie_total
 * @package: reggieVersion1.controller
 * @className: OrderController
 * @author: White
 * @description: 用户订单控制层
 * @date: 2023/1/31 13:04
 * @version: 1.0
 */
@RestController
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrderService ordersService;

    @Autowired
    private ShoppingCartService shoppingCartService;

    /**
     * 前端用户提交订单
     *
     * @param order
     * @return
     */
    @PostMapping("/submit")
    public R<String> frontSubmitOrder(@RequestBody Order order, HttpSession session) {

        boolean submitOrder = ordersService.submitOrder(order, session);

        return submitOrder ? R.success("创建订单成功") : R.error("创建订单失败");
    }

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page/{page}/{pageSize}")
    public R<Page<Order>> getPages(@PathVariable Integer page, @PathVariable Integer pageSize) {

        Page orderPages = ordersService.getPages(page, pageSize);

        if (orderPages != null) {
            return R.success(orderPages);
        } else {
            return R.error("数据查询失败，请重试");
        }
    }

    /**
     * 分页查询,根据订单号查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page/{page}/{pageSize}/{orderNumber}")
    public R<Page<Order>> getPages(@PathVariable Integer page,
                                   @PathVariable Integer pageSize,
                                   @PathVariable String orderNumber) {

        Page orderPages = ordersService.getPages(page, pageSize, orderNumber);

        if (orderPages != null) {
            return R.success(orderPages);
        } else {
            return R.error("数据查询失败，请重试");
        }
    }

    /**
     * 分页查询，根据时间查询
     *
     * @param page
     * @param pageSize
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page/{page}/{pageSize}/{beginTime}/{endTime}")
    public R<Page<Order>> getPages(@PathVariable Integer page,
                                   @PathVariable Integer pageSize,
                                   @PathVariable String beginTime,
                                   @PathVariable String endTime) {

        Page orderPages = ordersService.getPages(page, pageSize, beginTime, endTime);

        if (orderPages != null) {
            return R.success(orderPages);
        } else {
            return R.error("数据查询失败，请重试");
        }
    }

    /**
     * 分页查询，根据订单号、时间查询
     *
     * @param page
     * @param pageSize
     * @param orderNumber
     * @param beginTime
     * @param endTime
     * @return
     */
    @GetMapping("/page/{page}/{pageSize}/{orderNumber}/{beginTime}/{endTime}")
    public R<Page<Order>> getPages(@PathVariable Integer page,
                                   @PathVariable Integer pageSize,
                                   @PathVariable String orderNumber,
                                   @PathVariable String beginTime,
                                   @PathVariable String endTime) {

        Page orderPages = ordersService.getPages(page, pageSize, orderNumber, beginTime, endTime);

        if (orderPages != null) {
            return R.success(orderPages);
        } else {
            return R.error("数据查询失败，请重试");
        }
    }

    /**
     * 修改订单的派送状态
     *
     * @return
     */
    @PutMapping
    public R<String> updateOrderStatus(@RequestBody Order order) {

        if (ordersService.updateById(order)) {
            return R.success("修改订单状态成功");
        } else {
            return R.error("修改订单状态失败，请重试");
        }
    }

    /**
     * 前端用户查看自己的订单
     * @param page
     * @param pageSize
     * @param session
     * @return
     */
    @GetMapping("/userPage/{page}/{pageSize}")
    public R<Page<Order>> frontGetPages(@PathVariable Integer page,@PathVariable Integer pageSize,HttpSession session){

        Page<Order> orderPage = ordersService.frontGetPages(page, pageSize, session);

        if (orderPage!=null){
            return R.success(orderPage);
        } else {
            return R.error("还没有创建订单，请创建后在查看");
        }

    }


    /**
     * 再来一单功能
     * @return
     */
    @PostMapping("/again")
    public R<String> againSubmitOrder(@RequestBody Order order,HttpSession session){

        boolean againSubmitOrder = ordersService.againSubmitOrder(order, session);

        return againSubmitOrder? R.success("成功再来一单"): R.error("操作失败，请重试");
    }
}
