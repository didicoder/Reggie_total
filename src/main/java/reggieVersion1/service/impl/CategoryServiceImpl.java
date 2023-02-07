package reggieVersion1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Service;
import reggieVersion1.exception.CustomException;
import reggieVersion1.controller.utils.R;
import reggieVersion1.domain.Dish;
import reggieVersion1.domain.Setmeal;
import reggieVersion1.service.CategoryService;
import reggieVersion1.mapper.CategoryMapper;
import reggieVersion1.domain.Category;
import reggieVersion1.service.DishService;
import reggieVersion1.service.SetmealService;

/**
 * <p>
 * 菜品及套餐分类 服务实现类
 * </p>
 *
 * @author White
 * @since 2023-01-21
 */
@Service
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {
    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    /**
     * 菜品分类分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public R<Page> getPages(Integer page, Integer pageSize) {
        Page<Category> tempPage = new Page<>(page, pageSize);
        tempPage = categoryMapper.selectPage(tempPage, new LambdaQueryWrapper<Category>().orderByAsc(Category::getSort));

        return R.success(tempPage);
    }

    /**
     * 根据id删除分类，删除前检查分类是否关联菜品或套餐
     *
     * @param id
     * @return
     */
    @Override
    public boolean removeByCheck(Long id) {
        //0.获取该分类下的菜品数量
        long dishCount = dishService.count(new LambdaQueryWrapper<Dish>().eq(Dish::getCategoryId, id));

        //1.查询当前分类是否关联菜品，如果关联则抛出业务异常
        if (dishCount > 0) {
            //已经关联菜品，抛出业务异常
            throw new CustomException("当前分类已经关联菜品，不能删除！");
        }

        long setMealCount = setmealService.count(new LambdaQueryWrapper<Setmeal>().eq(Setmeal::getCategoryId, id));
        //2.查询当前分类是否关联套餐，如果关联则抛出业务异常
        if (setMealCount > 0) {
            //已经关联套餐，抛出业务异常
            throw new CustomException("当前分类已经关联套餐，不能删除！");
        }

        //3.正常删除该分类
        return categoryMapper.deleteById(id) > 0;
    }
}
