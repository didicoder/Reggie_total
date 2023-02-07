package reggieVersion1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import reggieVersion1.controller.utils.R;
import reggieVersion1.domain.ShoppingCart;
import reggieVersion1.service.ShoppingCartService;
import reggieVersion1.mapper.ShoppingCartMapper;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpSession;
import java.time.LocalDateTime;

/**
 * @author White
 * @description 针对表【shopping_cart(购物车)】的数据库操作Service实现
 * @createDate 2023-01-30 14:04:06
 */
@Service
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart>
        implements ShoppingCartService {
    /**
     * 添加到购物车
     *
     * @param shoppingCart
     * @param session
     * @return
     */
    @Override
    public ShoppingCart saveShoppingCart(ShoppingCart shoppingCart, HttpSession session) {

        //1.获取下单用户id
        Long userId = (Long) session.getAttribute("user");
        shoppingCart.setUserId(userId);

        //设置条件为当前用户
        LambdaQueryWrapper<ShoppingCart> cartWrapper = new LambdaQueryWrapper<ShoppingCart>()
                .eq(ShoppingCart::getUserId, userId);

        //2.判断当前订单是套餐或菜品
        if (shoppingCart.getDishId() != null) {
            //添加的是菜品
            cartWrapper
                    //判断是否为相同菜品
                    .eq(ShoppingCart::getDishId, shoppingCart.getDishId())
                    //判断是否为相同口味
                    .eq(ShoppingCart::getDishFlavor, shoppingCart.getDishFlavor());
        } else if (shoppingCart.getSetmealId() != null) {
            //添加的是套餐
            cartWrapper
                    //判断是否为相同套菜
                    .eq(ShoppingCart::getSetmealId, shoppingCart.getSetmealId());
        }

        //3.判断当前是否在购物车中出现
        ShoppingCart getOne = this.getOne(cartWrapper);

        if (getOne != null) {
            //在购物车中出现
            getOne.setNumber(getOne.getNumber() + 1);
            this.updateById(getOne);
        } else {
            //购物车中不存在
            shoppingCart.setNumber(1);
            shoppingCart.setCreateTime(LocalDateTime.now());
            this.save(shoppingCart);

            //统一返回getOne实例
            getOne = shoppingCart;
        }

        return getOne;
    }

    @Override
    public ShoppingCart removeOneCart(ShoppingCart shoppingCart, HttpSession session) {
        Long userId = (Long) session.getAttribute("user");

        LambdaQueryWrapper<ShoppingCart> cartWrapper = new LambdaQueryWrapper<ShoppingCart>()
                //添加查询条件：用户ID为当前用户
                .eq(ShoppingCart::getUserId, userId);

        //判断是菜品还是套餐
        if (shoppingCart.getDishId()!=null){
            //减少菜品数量
            cartWrapper.eq(ShoppingCart::getDishId,shoppingCart.getDishId());
        } else if (shoppingCart.getSetmealId()!=null){
            //减少套餐数量
            cartWrapper.eq(ShoppingCart::getSetmealId,shoppingCart.getSetmealId());
        }

        ShoppingCart one = this.getOne(cartWrapper);

        if (one.getNumber()>1){
            one.setNumber(one.getNumber()-1);
            this.updateById(one);
        } else {
            this.removeById(one);
        }

        return one;
    }

    /**
     * 清空购物车
     * @param session
     * @return
     */
    @Override
    public boolean cleanShoppingCart(HttpSession session) {
        Long userId = (Long) session.getAttribute("user");

        boolean flag = this.remove(new LambdaQueryWrapper<ShoppingCart>()
                .eq(ShoppingCart::getUserId, userId));

        return flag;
    }
}




