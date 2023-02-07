package reggieVersion1.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.github.pagehelper.PageInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reggieVersion1.controller.utils.R;
import reggieVersion1.domain.Dish;
import reggieVersion1.dto.DishDto;
import reggieVersion1.service.DishFlavorService;
import reggieVersion1.service.DishService;

import java.util.List;

/**
 * @projectName: Reggie_total
 * @package: reggieVersion1.controller
 * @className: DishController
 * @author: White
 * @description: 菜品DishController
 * @date: 2023/1/26 18:11
 * @version: 1.0
 */
@RestController
@RequestMapping("/dish")
public class DishController {
    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService flavorService;

    /**
     * 新增菜品
     *
     * @param dishDto
     * @return
     */
    @PostMapping
    public R<String> insertDish(@RequestBody DishDto dishDto) {
        if (dishService.saveWithFlavor(dishDto)) {
            return R.success("菜品新增成功");
        } else {
            return R.error("添加菜品失败");
        }
    }

    /**
     * 使用mybatis进行多表联查，但是不能进行分页
     *
     * @return
     */
    //@GetMapping("/page/{page}/{pageSize}")
    public R<Page> getPages(@PathVariable Integer page, @PathVariable Integer pageSize) {
        Page pages = dishService.getPageByMybatis(page, pageSize);

        return R.success(pages);
    }

    /**
     * 使用mybatis分页插件PageHelper进行分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    //@GetMapping("/page/{page}/{pageSize}")
    public R<PageInfo> getPageByPageHelper(@PathVariable Integer page, @PathVariable Integer pageSize) {
        PageInfo<DishDto> pageInfo = dishService.getPageByPageHelper(page, pageSize);

        return R.success(pageInfo);
    }

    /**
     * 使用mybatis分页插件PageHelper进行分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page/{page}/{pageSize}")
    public R<Page> getPageByMP(@PathVariable Integer page, @PathVariable Integer pageSize) {
        Page<DishDto> dtoPage = dishService.getPageByMybatisPlus(page, pageSize);

        return R.success(dtoPage);
    }

    /**
     * 完全使用mybatis-plus进行分页查询,并且按菜品名称查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page/{page}/{pageSize}/{name}")
    public R<Page> getPageByMPAndName(@PathVariable Integer page,
                                      @PathVariable Integer pageSize,
                                      @PathVariable String name) {
        Page<DishDto> dtoPage = dishService.getPageByMybatisPlus(page, pageSize, name);

        return R.success(dtoPage);
    }

    /**
     * 根据id查询菜品进行回显
     *
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getDishById(@PathVariable String id) {
        Long tempId = Long.parseLong(id);

        return R.success(dishService.getByIdWithFlavor(tempId));
    }

    /**
     * 修改菜品
     *
     * @return
     */
    @PutMapping
    public R<String> updateDish(@RequestBody DishDto dishDto) {
        if (dishService.updateWithFlavor(dishDto)) {
            return R.success("修改成功");
        } else {
            return R.error("修改失败，请稍后重试！");
        }
    }

    /**
     * 根据条件查询对应菜品数据
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list/{categoryId}")
    public R<List<Dish>> listDishes(@PathVariable String categoryId) {
        Long tempId = Long.parseLong(categoryId);
        List<Dish> dishList = dishService.list(new LambdaQueryWrapper<Dish>()
                .eq(Dish::getCategoryId, tempId)
                .and(dishWrapper -> dishWrapper.eq(Dish::getStatus, "1")) //查询在售状态的菜品
                .orderByAsc(Dish::getSort));

        if (dishList != null) {
            return R.success(dishList);
        } else {
            return R.error("暂无菜品，请稍后重试！");
        }
    }

    /**
     * 前端根据条件查询对应菜品数据
     *
     * @param categoryId
     * @return
     */
    //@GetMapping("/list/{categoryId}/{status}")
    public R<List<Dish>> listFrontDishes(@PathVariable String categoryId, @PathVariable Integer status) {
        Long tempId = Long.parseLong(categoryId);
        List<Dish> dishList = dishService
                .list(new LambdaQueryWrapper<Dish>()
                        .eq(Dish::getCategoryId, tempId)
                        .and(dishWrapper -> dishWrapper.eq(Dish::getStatus, status)) //查询在售状态的菜品
                        .orderByAsc(Dish::getSort));

        if (dishList != null) {
            return R.success(dishList);
        } else {
            return R.error("暂无菜品，请稍后重试！");
        }
    }

    /**
     * 前端根据条件查询对应菜品数据
     * 改进：可以展示口味信息
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list/{categoryId}/{status}")
    public R<List<DishDto>> listFrontDishDto(@PathVariable String categoryId, @PathVariable Integer status) {
        Long tempId = Long.parseLong(categoryId);

        List<DishDto> dtoList = dishService.getListByDishDto(tempId, status);
        if (dtoList != null) {
            return R.success(dtoList);
        } else {
            return R.error("暂无菜品，请稍后重试！");
        }
    }

    /**
     * 处理菜品起售、停售，批量起售、批量停售
     *
     * @param statusCode
     * @param dishIds
     * @return
     */
    @PostMapping("/status/{statusCode}")
    public R<String> changeStatus(@PathVariable Integer statusCode, @RequestParam("ids") List<Long> dishIds) {

        boolean flag = dishService.updateStatus(statusCode, dishIds);

        return flag ? R.success("售卖状态修改成功") : R.error("售卖状态修改失败");
    }

    /**
     * 删除菜品还要删除对应口味，或批量删除
     *
     * @return
     */
    @DeleteMapping()
    public R<String> deleteBatch(@RequestParam("ids") List<Long> dishIds) {

        boolean delete = dishService.removeByList(dishIds);

        if (delete) {
            return R.success("删除成功");
        } else {
            return R.error("删除失败，请重试");
        }
    }
}
