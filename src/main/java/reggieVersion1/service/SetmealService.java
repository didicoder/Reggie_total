package reggieVersion1.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import org.springframework.transaction.annotation.Transactional;
import reggieVersion1.domain.Setmeal;
import reggieVersion1.domain.SetmealDish;
import reggieVersion1.dto.DishDto;
import reggieVersion1.dto.SetmealDto;

import java.util.List;

/**
 * @author White
 * @description 针对表【setmeal(套餐)】的数据库操作Service
 * @createDate 2023-01-26 09:34:56
 */
public interface SetmealService extends IService<Setmeal> {

    /**
     * 新增套餐并且同时将套餐和菜品关系保存到数据库
     * @param setmealDto
     * @return
     */
    @Transactional //使用事务
    boolean saveWithDish(SetmealDto setmealDto);

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    Page getPages(Integer page,Integer pageSize);

    /**
     * 前端获取套餐详细信息
     * @param setMealId
     * @return
     */
    List getFrontSetMealWithDish(Long setMealId);

    /**
     * 前端获取套餐详细信息改进版——可以查看图片
     * @param setMealId
     * @return
     */
    List getFrontSetMealWithDishV2(Long setMealId);

    /**
     * 分页查询
     * @param page
     * @param pageSize
     * @return
     */
    Page getPages(Integer page,Integer pageSize,String name);

    /**
     * 批量删除
     * @param setmealIds
     * @return
     */
    @Transactional
    boolean removeWithDish(List<Long> setmealIds);

    /**
     * 批量起售、停售
     * @param status
     * @param setmealIds
     * @return
     */
    @Transactional
    boolean updateStatus(Integer status,List<Long> setmealIds);

    /**
     * 根绝id展示套餐数据
     * @param id
     * @return
     */
    SetmealDto getSetMealById(Long id);

    /**
     * 更新套餐
     * @param setmealDto
     * @return
     */
    @Transactional
    boolean updateSetMealWithDish(SetmealDto setmealDto);
}
