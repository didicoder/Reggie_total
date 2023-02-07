package reggieVersion1.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import reggieVersion1.domain.DishFlavor;
import reggieVersion1.service.DishFlavorService;
import reggieVersion1.mapper.DishFlavorMapper;
import org.springframework.stereotype.Service;

/**
* @author White
* @description 针对表【dish_flavor(菜品口味关系表)】的数据库操作Service实现
* @createDate 2023-01-26 18:15:21
*/
@Service
public class DishFlavorServiceImpl extends ServiceImpl<DishFlavorMapper, DishFlavor>
    implements DishFlavorService{

}




