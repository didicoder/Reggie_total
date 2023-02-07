package reggieVersion1.service.impl;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import reggieVersion1.domain.Category;
import reggieVersion1.domain.Dish;
import reggieVersion1.domain.DishFlavor;
import reggieVersion1.dto.DishDto;
import reggieVersion1.mapper.CategoryMapper;
import reggieVersion1.service.DishFlavorService;
import reggieVersion1.service.DishService;
import reggieVersion1.mapper.DishMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author White
 * @description 针对表【dish(菜品管理)】的数据库操作Service实现
 * @createDate 2023-01-26 09:32:14
 */
@Service
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish>
        implements DishService {
    @Autowired
    private DishFlavorService flavorService;
    @Autowired
    private DishMapper dishMapper;

    //为避免循环依赖，只能使用mapper
    @Autowired
    private CategoryMapper categoryMapper;

    /**
     * 新增菜品，并且同时插入菜品对应的口味，需要操作两张表(dish、dishFlavor)
     *
     * @param dishDto
     */
    @Override
    public boolean saveWithFlavor(DishDto dishDto) {
        //1.保存菜品的基本信息到dish表
        this.save(dishDto);

        List<DishFlavor> flavors = dishDto.getFlavors();
        /*
            需要对flavors集合的dishId进行赋值
                方式一：使用增强for
                方式二：使用stream流
         */
        flavors = flavors.stream()
                .map(item -> {
                    item.setDishId(dishDto.getId());
                    return item;
                })
                .collect(Collectors.toList());

        //2.保存菜品的口味到dishFlavor表
        flavorService.saveBatch(flavors);

        return true;
    }


    /**
     * 使用mybatis的mapper查询数据，配合mybatis-plus进行分页查询
     * 结果：只能将菜品分类显示，没有分页效果
     *
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Page<DishDto> getPageByMybatis(Integer page, Integer pageSize) {
        Page<DishDto> dtoPage = new Page<>(page, pageSize);
        dtoPage = dtoPage.setRecords(dishMapper.selectByDishAndCategory());

        return dtoPage;
    }


    /**
     * 使用自定义sql解决多表联查,使用mybatis分页插件pageHelper完成分页功能
     * 结果：不仅可以展示菜品分类，还可以实现分页
     *
     * @param page     当前页
     * @param pageSize 每页显示条数
     * @return 返回PageInfo对象
     */
    @Override
    public PageInfo<DishDto> getPageByPageHelper(Integer page, Integer pageSize) {
        //pageHelper分页的方法，传入当前页和每页条数
        PageHelper.startPage(page, pageSize);
        PageInfo dtoPage = new PageInfo(dishMapper.selectByDishAndCategory());

        return dtoPage;
    }

    /**
     * 完全使用mybatis-plus进行分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public Page<DishDto> getPageByMybatisPlus(Integer page, Integer pageSize) {
        //构造分页构造器对象
        Page<Dish> dishPage = new Page<>(page, pageSize);
        Page<DishDto> dtoPage = new Page<>(page, pageSize);

        //执行分页查询
        this.page(dishPage, new LambdaQueryWrapper<Dish>().orderByDesc(Dish::getUpdateTime));

        //将分页查询结果拷贝到dtoPage中
        BeanUtils.copyProperties(dishPage, dtoPage, "records");

        //使用list集合保存records数据
        List<Dish> dishList = dishPage.getRecords();
        List<DishDto> dtoList = this.getDtoListUtils(dishList);

        //将结果设置到dtoPage中
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    /**
     * 完全使用mybatis-plus进行分页查询,并且按菜品名称查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @Override
    public Page getPageByMybatisPlus(Integer page, Integer pageSize, String name) {
        Page<Dish> dishPage = new Page<>(page, pageSize);
        Page<DishDto> dtoPage = new Page<>(page, pageSize);

        this.page(dishPage, new LambdaQueryWrapper<Dish>()
                .like(StringUtils.isNotBlank(name), Dish::getName, name)
                .orderByDesc(Dish::getUpdateTime));

        BeanUtils.copyProperties(dishPage, dtoPage, "records");

        List<Dish> dishList = dishPage.getRecords();
        List<DishDto> dtoList = this.getDtoListUtils(dishList);

        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    /**
     * 根据id查询菜品进行回显
     *
     * @param id
     * @return
     */
    @Override
    public DishDto getByIdWithFlavor(Long id) {
        DishDto dishDto = new DishDto();
        Dish dish = this.getById(id);
        BeanUtils.copyProperties(dish, dishDto);

        dishDto.setFlavors(flavorService.list(new LambdaQueryWrapper<DishFlavor>()
                .eq(DishFlavor::getDishId, dish.getId())));

        return dishDto;
    }

    /**
     * 更新菜品信息
     *
     * @param dishDto
     * @return
     */
    @Override
    public boolean updateWithFlavor(DishDto dishDto) {
        //更新dish表中的基本信息
        this.updateById(dishDto);

        //删除之前的菜品对应的口味信息
        flavorService.remove(new LambdaQueryWrapper<DishFlavor>()
                .eq(DishFlavor::getDishId, dishDto.getId()));

        //将新提交的口味信息保存到dish_flavor表
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors.stream()
                .map(dishFlavor -> {
                    dishFlavor.setDishId(dishDto.getId());
                    return dishFlavor;
                }).collect(Collectors.toList());

        flavorService.saveBatch(flavors);

        return true;
    }

    /**
     * 页面展示菜品信息，包括菜品分类
     *
     * @param categoryId
     * @param status
     * @return
     */
    @Override
    public List<DishDto> getListByDishDto(Long categoryId, Integer status) {
        List<Dish> dishList = dishMapper.selectList(new LambdaQueryWrapper<Dish>()
                .eq(Dish::getCategoryId, categoryId)
                .eq(Dish::getStatus, status));

        List<DishDto> dtoList = this.getDtoListUtils(dishList);

        return dtoList;
    }

    /**
     * 处理菜品起售、停售，批量起售、批量停售
     *
     * @param statusCode
     * @param dishIds
     * @return
     */
    @Override
    public boolean updateStatus(Integer statusCode, List<Long> dishIds) {
        //获取批量处理的菜品列表
        List<Dish> dishList = this.list(new LambdaQueryWrapper<Dish>()
                .in(Dish::getId, dishIds));

        boolean flag = false;
        //对菜品列表的状态进行更改
        for (Dish dish : dishList) {
            dish.setStatus(statusCode);
            flag = this.updateById(dish);
        }

        return flag;
    }

    /**
     * 删除菜品(正在售卖的菜品不能删除)还要删除对应口味，
     * 或批量删除(能删就删，不能删就不删)——只能一个一个删除
     *
     * @param dishIds
     * @return
     */
    public boolean removeByList(List<Long> dishIds) {

        List<Dish> dishList = this.list(new LambdaQueryWrapper<Dish>()
                .in(Dish::getId, dishIds)
                .and(dishWrapper -> {
                    dishWrapper.eq(Dish::getStatus, 0);
                }));

        boolean flag = false;

        for (Dish dish : dishList) {
            //从dish表中删除菜品
            flag = this.removeById(dish);

            //删除菜品对应口味，没有口味就不管
            flavorService.remove(new LambdaQueryWrapper<DishFlavor>()
                    .eq(DishFlavor::getDishId, dish.getId()));
        }

        return flag;
    }


    /**
     * 自定义工具方法，完成将dishlist的数据拷贝到dtolist,且将菜品名称查询并保存到dtoList
     *
     * @param oldList
     * @return
     */
    public List getDtoListUtils(List<Dish> oldList) {
        List<DishDto> newList = oldList
                .stream()
                .map(dish -> {
                    //设置分类名称
                    DishDto tempDto = new DishDto();
                    Long categoryId = dish.getCategoryId();
                    Category category = categoryMapper.selectById(categoryId);
                    tempDto.setCategoryName(category.getName());

                    BeanUtils.copyProperties(dish, tempDto);

                    //--------------------------------------------------
                    //设置菜品口味
                    tempDto.setFlavors(flavorService
                            .list(new LambdaQueryWrapper<DishFlavor>()
                                    .eq(DishFlavor::getDishId, dish.getId())));

                    return tempDto;
                })
                .collect(Collectors.toList());
        return newList;
    }
}




