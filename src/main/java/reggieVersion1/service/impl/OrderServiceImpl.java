package reggieVersion1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import reggieVersion1.domain.*;
import reggieVersion1.dto.OrderDto;
import reggieVersion1.exception.CustomException;
import reggieVersion1.service.*;
import reggieVersion1.mapper.OrderMapper;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author White
 * @description 针对表【orders(订单表)】的数据库操作Service实现
 * @createDate 2023-01-31 11:22:23
 */
@Service
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order>
        implements OrderService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     *
     * @param order
     * @param session
     * @return
     */
    @Override
    public boolean submitOrder(Order order, HttpSession session) {
        //1.从session中获取用户id
        Long userId = (Long) session.getAttribute("user");

        //2.根据用户ID获取购物车数据
        LambdaQueryWrapper<ShoppingCart> cartWrapper = new LambdaQueryWrapper<ShoppingCart>()
                .eq(ShoppingCart::getUserId, userId);
        List<ShoppingCart> cartList = shoppingCartService.list();

        //购物车中没有数据
        if (cartList == null || cartList.size() == 0) {
            throw new CustomException("购物车为空，无法下单");
        }

        //查询用户数据
        User userInfo = userService.getById(userId);
        //查询用户地址信息,不能根据用户id查询,会有多个地址
        AddressBook userAddressBook = addressBookService.getById(order.getAddressBookId());

        //地址信息中没有数据
        if (userAddressBook == null) {
            throw new CustomException("用户地址信息有误，无法下单");
        }


        //设置订单id，为保证点单明细中一样，直接赋值，不能通过雪花算法生成
        long orderId = IdWorker.getId();
        //创建原子操作类计算总金额，保证线程安全(private变量不太需要)
        AtomicInteger amount = new AtomicInteger(0);

        //遍历购物车，获取总金额
        List<OrderDetail> orderDetailList = cartList.stream()
                .map(shoppingCart -> {

                    //创建订单明细类，并为其赋值
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setOrderId(orderId);
                    orderDetail.setNumber(shoppingCart.getNumber());
                    orderDetail.setDishFlavor(shoppingCart.getDishFlavor());
                    orderDetail.setDishId(shoppingCart.getDishId());
                    orderDetail.setSetmealId(shoppingCart.getSetmealId());
                    orderDetail.setName(shoppingCart.getName());
                    orderDetail.setImage(shoppingCart.getImage());
                    orderDetail.setAmount(shoppingCart.getAmount());

                    //累加金额=单品金额*数量
                    amount.addAndGet(shoppingCart.getAmount().multiply(new BigDecimal(shoppingCart.getNumber())).intValue());

                    return orderDetail;
                }).collect(Collectors.toList());

        //对订单数据进行封装
        //设置订单号
        order.setId(orderId);
        order.setNumber(String.valueOf(orderId));
        //设置其他属性
        order.setOrderTime(LocalDateTime.now());
        order.setCheckoutTime(LocalDateTime.now());
        order.setStatus(2);
        order.setAmount(new BigDecimal(String.valueOf(amount)));//总金额
        order.setUserId(userId);
        order.setUserName(userInfo.getName());
        order.setConsignee(userAddressBook.getConsignee());
        order.setPhone(userAddressBook.getPhone());
        //设置详细的地址信息
        order.setAddress((userAddressBook.getProvinceName() == null ? "" : userAddressBook.getProvinceName())
                + (userAddressBook.getCityName() == null ? "" : userAddressBook.getCityName())
                + (userAddressBook.getDistrictName() == null ? "" : userAddressBook.getDistrictName())
                + (userAddressBook.getDetail() == null ? "" : userAddressBook.getDetail()));

        //3.向订单表插入数据
        boolean save = this.save(order);

        //4.向订单明细表插入数据，多条数据
        boolean saveBatch = orderDetailService.saveBatch(orderDetailList);

        if (save && saveBatch) {
            //清空购物车数据
            shoppingCartService.remove(cartWrapper);

            return true;
        } else {
            throw new CustomException("未知错误，请重试");
        }
    }

    /**
     * 后台分页查询订单
     *
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Page<Order> getPages(Integer page, Integer pageSize) {

        Page<Order> pageInfo = new Page<>(page, pageSize);

        pageInfo = this
                .page(pageInfo, new LambdaQueryWrapper<Order>()
                        .orderByAsc(Order::getOrderTime));

        return pageInfo;
    }

    /**
     * 后台分页查询订单，根据订单号查询
     *
     * @param page
     * @param pageSize
     * @param orderNumber
     * @return
     */
    @Override
    public Page<Order> getPages(Integer page, Integer pageSize, String orderNumber) {
        Page<Order> pageInfo = new Page<>(page, pageSize);

        pageInfo = this
                .page(pageInfo, new LambdaQueryWrapper<Order>()
                        .eq(Order::getNumber, orderNumber)
                        .orderByAsc(Order::getOrderTime));

        return pageInfo;
    }

    /**
     * 后台分页查询订单，根据订单号查询
     *
     * @param page
     * @param pageSize
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public Page<Order> getPages(Integer page, Integer pageSize, String beginTime, String endTime) {
        Page<Order> pageInfo = new Page<>(page, pageSize);

        pageInfo = this
                .page(pageInfo, new LambdaQueryWrapper<Order>()
                        .gt(Order::getOrderTime, beginTime)
                        .lt(Order::getOrderTime, endTime)
                        .orderByAsc(Order::getOrderTime));

        return pageInfo;
    }

    /**
     * 后台分页查询订单，根据订单号查询
     *
     * @param page
     * @param pageSize
     * @param orderNumber
     * @param beginTime
     * @param endTime
     * @return
     */
    @Override
    public Page<Order> getPages(Integer page, Integer pageSize, String orderNumber, String beginTime, String endTime) {
        Page<Order> pageInfo = new Page<>(page, pageSize);

        pageInfo = this
                .page(pageInfo, new LambdaQueryWrapper<Order>()
                        .eq(Order::getNumber, orderNumber)
                        .gt(Order::getOrderTime, beginTime)
                        .lt(Order::getOrderTime, endTime)
                        .orderByAsc(Order::getOrderTime));

        return pageInfo;
    }

    /**
     * 前端用户查看自己的订单
     *
     * @param page
     * @param pageSize
     * @param session
     * @return
     */
    @Override
    public Page<OrderDto> frontGetPages(Integer page, Integer pageSize, HttpSession session) {
        Long userId = (Long) session.getAttribute("user");

        Page<Order> pageInfo = new Page<>(page, pageSize);
        Page<OrderDto> pageDto = new Page<>(page, pageSize);

        //构造条件查询对象
        LambdaQueryWrapper<Order> orderWrapper = new LambdaQueryWrapper<Order>()
                .eq(Order::getUserId, userId)
                .orderByDesc(Order::getOrderTime);

        //这里是直接把当前用户分页的全部结果查询出来，要添加用户id作为查询条件，否则会出现用户可以查询到其他用户的订单情况
        pageInfo = this.page(pageInfo, orderWrapper);

        //通过OrderId查询对应的OrderDetail
        LambdaQueryWrapper<OrderDetail> detailWrapper = new LambdaQueryWrapper<>();

        //对OrderDto进行需要的属性赋值
        List<Order> records = pageInfo.getRecords();
        List<OrderDto> orderDtoList = records.stream()
                .map(new Function<Order, OrderDto>() {
                    @Override
                    public OrderDto apply(Order order) {
                        OrderDto orderDto = new OrderDto();

                        //根据订单id获取orderDetail数据
                        List<OrderDetail> orderDetailList = orderDetailService.list(new LambdaQueryWrapper<OrderDetail>()
                                .eq(OrderDetail::getOrderId, order.getId()));

                        BeanUtils.copyProperties(order, orderDto);

                        //对orderDto进行OrderDetails属性的赋值
                        orderDto.setOrderDetails(orderDetailList);

                        return orderDto;
                    }
                }).collect(Collectors.toList());

        //使用dto的分页有点难度.....需要重点掌握
        BeanUtils.copyProperties(pageInfo, pageDto, "records");
        pageDto.setRecords(orderDtoList);

        return pageDto;
    }

    @Override
    public boolean againSubmitOrder(Order order, HttpSession session) {

        //根据订单号获取订单明细中的所有数据
        List<OrderDetail> detailList = orderDetailService.list(new LambdaQueryWrapper<OrderDetail>()
                .eq(OrderDetail::getOrderId, order.getId()));

        //清空购物车
        shoppingCartService.cleanShoppingCart(session);

        //获取用户id
        Long userId = (Long) session.getAttribute("user");

        //设置购物车数据
        List<ShoppingCart> shoppingCartList = detailList.stream()
                .map(orderDetail -> {
                    ShoppingCart shoppingCart = new ShoppingCart();

                    //为购物车数据进行赋值
                    shoppingCart.setUserId(userId);
                    shoppingCart.setImage(orderDetail.getImage());

                    //判断是套餐还是菜品
                    if (orderDetail.getDishId() != null) {
                        //判断为菜品
                        shoppingCart.setDishId(orderDetail.getDishId());
                    } else if (orderDetail.getSetmealId() != null) {
                        shoppingCart.setSetmealId(orderDetail.getSetmealId());
                    } else {
                        throw new CustomException("订单数据有误，无法再来一单，请重试");
                    }
                    shoppingCart.setName(orderDetail.getName());
                    shoppingCart.setDishFlavor(orderDetail.getDishFlavor());
                    shoppingCart.setNumber(orderDetail.getNumber());
                    shoppingCart.setAmount(orderDetail.getAmount());
                    shoppingCart.setCreateTime(LocalDateTime.now());


                    return shoppingCart;
                }).collect(Collectors.toList());

        boolean saveBatch = shoppingCartService.saveBatch(shoppingCartList);

        return saveBatch;
    }
}




