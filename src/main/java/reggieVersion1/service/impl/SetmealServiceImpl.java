package reggieVersion1.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import reggieVersion1.domain.*;
import reggieVersion1.dto.DishDto;
import reggieVersion1.dto.SetmealDto;
import reggieVersion1.exception.CustomException;
import reggieVersion1.mapper.CategoryMapper;
import reggieVersion1.service.DishService;
import reggieVersion1.service.SetmealDishService;
import reggieVersion1.service.SetmealService;
import reggieVersion1.mapper.SetmealMapper;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author White
 * @description 针对表【setmeal(套餐)】的数据库操作Service实现
 * @createDate 2023-01-26 09:34:56
 */
@Service
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal>
        implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private SetmealMapper setmealMapper;

    @Autowired
    private CategoryMapper categoryMapper;

    @Autowired
    private DishService dishService;

    /**
     * 新增套餐并且同时将套餐和菜品关系保存到数据库
     *
     * @param setmealDto
     * @return
     */
    @Override
    public boolean saveWithDish(SetmealDto setmealDto) {
        //保存套餐的基本信息
        this.save(setmealDto);

        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes
                .stream()
                .map(setmealDish -> {
                    setmealDish.setSetmealId(setmealDto.getId());

                    return setmealDish;
                })
                .collect(Collectors.toList());

        return setmealDishService.saveBatch(setmealDishes);
    }

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Page getPages(Integer page, Integer pageSize) {
        //构造分页构造器对象
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>(page, pageSize);

        setmealPage = this.page(setmealPage);

        BeanUtils.copyProperties(setmealPage, setmealDtoPage);

        List<Setmeal> setmealList = setmealPage.getRecords();
        List<SetmealDto> setmealDtoList = this.getDtoListUtils(setmealList);

        setmealDtoPage.setRecords(setmealDtoList);

        return setmealDtoPage;
    }

    /**
     * 前端展示套餐的详细信息方法
     *
     * @param setMealId
     * @return
     */
    @Override
    public List<SetmealDish> getFrontSetMealWithDish(Long setMealId) {
        List<SetmealDish> dishList = setmealDishService.list(new LambdaQueryWrapper<SetmealDish>()
                .eq(SetmealDish::getSetmealId, setMealId));

        return dishList;
    }

    /**
     * 前端套餐展示菜品详细信息
     *
     * @param setMealId
     * @return
     */
    @Override
    public List<DishDto> getFrontSetMealWithDishV2(Long setMealId) {

        //根据套餐id查询对应的套餐信息
        List<SetmealDish> dishList = setmealDishService.list(new LambdaQueryWrapper<SetmealDish>()
                .eq(SetmealDish::getSetmealId, setMealId));

        List<DishDto> dishDtoList = dishList.stream()
                .map(setmealDish -> {
                    DishDto dishDto = new DishDto();

                    BeanUtils.copyProperties(setmealDish, dishDto);

                    Dish dish = dishService.getById(setmealDish.getDishId());

                    BeanUtils.copyProperties(dish, dishDto);

                    return dishDto;
                }).collect(Collectors.toList());

        return dishDtoList;
    }

    @Override
    public Page getPages(Integer page, Integer pageSize, String name) {
        //构造分页构造器对象
        Page<Setmeal> setmealPage = new Page<>(page, pageSize);
        Page<SetmealDto> setmealDtoPage = new Page<>(page, pageSize);

        setmealPage = this.page(setmealPage, new LambdaQueryWrapper<Setmeal>()
                .like(StringUtils.isNotBlank(name), Setmeal::getName, name));

        BeanUtils.copyProperties(setmealPage, setmealDtoPage);

        List<Setmeal> setmealList = setmealPage.getRecords();
        List<SetmealDto> setmealDtoList = this.getDtoListUtils(setmealList);

        setmealDtoPage.setRecords(setmealDtoList);

        return setmealDtoPage;
    }

    /**
     * 批量删除，同时删除套餐和套餐对应的菜品
     *
     * @param setmealIds
     * @return
     */
    @Override
    public boolean removeWithDish(List<Long> setmealIds) {

        List<Setmeal> setmealList = this.list(new LambdaQueryWrapper<Setmeal>()
                .in(Setmeal::getId, setmealIds)
                .and(setmealWrapper -> setmealWrapper.eq(Setmeal::getStatus, 0)));

        boolean flag = false;

        for (Setmeal setmeal : setmealList) {
            //从dish表中删除菜品
            flag = this.removeById(setmeal);

            //删除菜品对应口味，没有口味就不管
            setmealDishService.remove(new LambdaQueryWrapper<SetmealDish>()
                    .eq(SetmealDish::getSetmealId, setmeal.getId()));
        }

        return flag;
    }

    /**
     * 批量起售、停售
     *
     * @param status
     * @param setmealIds
     * @return
     */
    @Override
    public boolean updateStatus(Integer status, List<Long> setmealIds) {

        //获取批量处理的菜品列表
        List<Setmeal> setmealList = this.list(new LambdaQueryWrapper<Setmeal>()
                .in(Setmeal::getId, setmealIds));

        //批量起售、停售
        boolean flag = false;
        //对菜品列表的状态进行更改
        for (Setmeal setmeal : setmealList) {
            setmeal.setStatus(status);
            flag = this.updateById(setmeal);
        }
        return flag;
    }

    /**
     * 根据套餐id展示套餐信息
     *
     * @param id
     * @return
     */
    @Override
    public SetmealDto getSetMealById(Long id) {

        //根据id查询套餐数据
        Setmeal setmeal = this.getById(id);
        SetmealDto setmealDto = new SetmealDto();

        //根据套餐id查询口味信息并用SetmealDto保存
        //将套餐信息拷贝到setmealDto
        if (setmeal != null) {
            BeanUtils.copyProperties(setmeal, setmealDto);

            List<SetmealDish> setmealDishList = setmealDishService.list(new LambdaQueryWrapper<SetmealDish>()
                    .eq(SetmealDish::getSetmealId, id));

            if (setmealDishList != null) {
                setmealDto.setSetmealDishes(setmealDishList);
            }

            return setmealDto;
        } else {
            return null;
        }
    }

    /**
     * 更新套餐
     *
     * @param setmealDto
     * @return
     */
    @Override
    public boolean updateSetMealWithDish(SetmealDto setmealDto) {

        if (this.updateById(setmealDto)) {

            //获取前端传来的setMealDish
            List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();

            //为setmealDishes设置套餐id
            setmealDishes = setmealDishes.stream()
                    .map(setmealDish -> {
                        //设置要修改套餐内餐品的套餐id
                        setmealDish.setSetmealId(setmealDto.getId());

                        return setmealDish;
                    }).collect(Collectors.toList());

            //删除套餐之前的菜品
            boolean removeDish = setmealDishService.removeBatchByIds(Collections.singleton(setmealDto.getId()));

            //成功删除之前的菜品
            if (removeDish) {
                //添加新的菜品
                setmealDishService.saveBatch(setmealDishes);
                return true;
            } else {
                return false;
            }
        } else {
            throw new CustomException("更新失败，请重试");
        }
    }

    /**
     * 自定义工具方法，完成将dishlist的数据拷贝到dtolist,且将菜品名称查询并保存到dtolist
     *
     * @param oldList
     * @return
     */
    public List getDtoListUtils(List<Setmeal> oldList) {
        List<SetmealDto> newList = oldList
                .stream()
                .map(setmeal -> {
                    SetmealDto setmealDto = new SetmealDto();
                    Long categoryId = setmeal.getCategoryId();
                    Category category = categoryMapper.selectById(categoryId);
                    setmealDto.setCategoryName(category.getName());

                    BeanUtils.copyProperties(setmeal, setmealDto);
                    return setmealDto;
                })
                .collect(Collectors.toList());
        return newList;
    }
}





