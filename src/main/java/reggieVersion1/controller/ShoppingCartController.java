package reggieVersion1.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.yaml.snakeyaml.events.Event;
import reggieVersion1.controller.utils.R;
import reggieVersion1.domain.ShoppingCart;
import reggieVersion1.service.ShoppingCartService;

import javax.servlet.http.HttpSession;
import java.awt.*;
import java.time.LocalDateTime;
import java.util.List;

/**
 * @projectName: Reggie_total
 * @package: reggieVersion1.controller
 * @className: ShoppingCartController
 * @author: White
 * @description: TODO
 * @date: 2023/1/30 14:07
 * @version: 1.0
 */
@RestController
@RequestMapping("/shoppingCart")
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService cartService;

    /**
     * 添加购物车功能
     * 详细说明：菜品相同且口味相同则数量+1
     * 菜品相同且口味不同，则分开计算
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/add")
    public R<ShoppingCart> addShoppingCart(@RequestBody ShoppingCart shoppingCart, HttpSession session) {

        ShoppingCart cart = cartService.saveShoppingCart(shoppingCart, session);

        return cart != null ? R.success(cart) : R.error("添加购物车失败，请稍候重试");
    }

    /**
     * 查看购物车
     *
     * @param session
     * @return
     */
    @GetMapping("/list")
    public R<List<ShoppingCart>> listAllCarts(HttpSession session) {
        Long userId = (Long) session.getAttribute("user");
        List<ShoppingCart> cartList = cartService
                .list(new LambdaQueryWrapper<ShoppingCart>()
                        .eq(ShoppingCart::getUserId, userId)
                        .orderByAsc(ShoppingCart::getCreateTime));

        return cartList != null ? R.success(cartList) : R.error("查看购物车失败，请重试");
    }

    /**
     * 删除一件商品
     * 前端只发送dishId或setmealId
     *
     * @param shoppingCart
     * @return
     */
    @PostMapping("/sub")
    public R<ShoppingCart> removeOneCart(@RequestBody ShoppingCart shoppingCart, HttpSession session) {

        ShoppingCart removeOneCart = cartService.removeOneCart(shoppingCart, session);

        return removeOneCart != null ? R.success(removeOneCart) : R.error("删除失败，请重试");
    }

    /**
     * 清空购物车
     *
     * @param session
     * @return
     */
    @DeleteMapping("/clean")
    public R<String> cleanShoppingCart(HttpSession session) {

        boolean cleanShoppingCart = cartService.cleanShoppingCart(session);

        return cleanShoppingCart ? R.success("购物车清空成功") : R.error("清空购物车失败");
    }

}
