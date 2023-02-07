package reggieVersion1.service;

import org.springframework.transaction.annotation.Transactional;
import reggieVersion1.domain.ShoppingCart;
import com.baomidou.mybatisplus.extension.service.IService;

import javax.servlet.http.HttpSession;

/**
* @author White
* @description 针对表【shopping_cart(购物车)】的数据库操作Service
* @createDate 2023-01-30 14:04:06
*/
public interface ShoppingCartService extends IService<ShoppingCart> {

    /**
     * 添加到购物车
     * @param shoppingCart
     * @param session
     * @return
     */
    @Transactional
    ShoppingCart saveShoppingCart(ShoppingCart shoppingCart, HttpSession session);

    /**
     * 删除一件菜品或套餐
     * @param shoppingCart
     * @param session
     * @return
     */
    @Transactional
    ShoppingCart removeOneCart(ShoppingCart shoppingCart, HttpSession session);

    /**
     * 清空购物车
     * @param session
     * @return
     */
    boolean cleanShoppingCart(HttpSession session);
}
