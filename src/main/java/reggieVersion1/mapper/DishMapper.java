package reggieVersion1.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import reggieVersion1.domain.Dish;
import reggieVersion1.dto.DishDto;

import java.util.List;

/**
* @author White
* @description 针对表【dish(菜品管理)】的数据库操作Mapper
* @createDate 2023-01-26 09:32:14
* @Entity generator.domain.Dish
*/
public interface DishMapper extends BaseMapper<Dish> {
    /**
     * sql多表查询，同时查询菜品表和分类表
     * @return
     */
    List<DishDto> selectByDishAndCategory();
}




