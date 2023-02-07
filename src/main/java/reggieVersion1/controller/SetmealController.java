package reggieVersion1.controller;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reggieVersion1.controller.utils.R;
import reggieVersion1.domain.Setmeal;
import reggieVersion1.domain.SetmealDish;
import reggieVersion1.dto.DishDto;
import reggieVersion1.dto.SetmealDto;
import reggieVersion1.service.SetmealDishService;
import reggieVersion1.service.SetmealService;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @projectName: Reggie_total
 * @package: reggieVersion1.controller
 * @className: SetmealController
 * @author: White
 * @description: Setmeal表现层
 * @date: 2023/1/28 9:03
 * @version: 1.0
 */
@RestController
@RequestMapping("/setmeal")
public class SetmealController {

    @Autowired
    private SetmealService setmealService;

    @Autowired
    private SetmealDishService setmealDishService;

    /**
     * 新增套餐
     *
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String> saveSetmeal(@RequestBody SetmealDto setmealDto) {
        if (setmealService.saveWithDish(setmealDto)) {
            return R.success("新增套餐成功");
        } else {
            return R.error("新增套餐失败，请稍后重试");
        }
    }

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page/{page}/{pageSize}")
    public R<Page> getPages(@PathVariable Integer page, @PathVariable Integer pageSize) {
        Page<SetmealDto> dtoPage = setmealService.getPages(page, pageSize);

        return R.success(dtoPage);
    }

    /**
     * 分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page/{page}/{pageSize}/{name}")
    public R<Page> getPages(@PathVariable Integer page,
                            @PathVariable Integer pageSize,
                            @PathVariable String name) {
        Page<SetmealDto> dtoPage = setmealService.getPages(page, pageSize, name);

        return R.success(dtoPage);
    }

    /**
     * 删除套餐
     *
     * @param setmealIds@return
     */
    @DeleteMapping()
    public R<String> deleteSetmeal(@RequestParam("ids") List<Long> setmealIds) {

        if (setmealService.removeWithDish(setmealIds)) {
            return R.success("套菜删除成功");
        } else {
            return R.error("套餐删除失败，请稍候重试");
        }
    }

    /**
     * 前端根据条件查询对应套餐数据
     *
     * @param categoryId
     * @return
     */
    @GetMapping("/list/{categoryId}/{status}")
    public R<List<Setmeal>> listFrontDishes(@PathVariable String categoryId, @PathVariable Integer status) {
        Long tempId = Long.parseLong(categoryId);
        List<Setmeal> dishList = setmealService
                .list(new LambdaQueryWrapper<Setmeal>()
                        .eq(Setmeal::getCategoryId, tempId)
                        .eq(Setmeal::getStatus, status)
                        //查询在售状态的菜品
                        .orderByAsc(Setmeal::getUpdateTime));

        if (dishList != null) {
            return R.success(dishList);
        } else {
            return R.error("暂无菜品，请稍后重试！");
        }
    }

    /**
     * 前端展示套餐详细信息
     *
     * @param setMealId
     * @return
     */
    //@GetMapping("/dish/{setMealId}")
    public R<List<SetmealDish>> listFrontDishDto(@PathVariable String setMealId) {
        Long tempId = Long.parseLong(setMealId);

        List<SetmealDish> dishList = setmealService.getFrontSetMealWithDish(tempId);

        if (dishList != null) {
            return R.success(dishList);
        } else {
            return R.error("套餐查看失败，请重试");
        }
    }

    /**
     * 前端展示套餐详细信息改进版
     *
     * @param setMealId
     * @return
     */
    @GetMapping("/dish/{setMealId}")
    public R<List<DishDto>> listFrontDishDtoV2(@PathVariable String setMealId) {
        Long tempId = Long.parseLong(setMealId);

        List<DishDto> dishDtoList = setmealService.getFrontSetMealWithDishV2(tempId);

        if (dishDtoList != null) {
            return R.success(dishDtoList);
        } else {
            return R.error("套餐查看失败，请重试");
        }
    }

    /**
     * 套餐的批量起售、停售
     *
     * @param status
     * @param setmealIds
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> changeStatus(@PathVariable Integer status, @RequestParam("ids") List<Long> setmealIds) {

        boolean flag = setmealService.updateStatus(status, setmealIds);

        return flag ? R.success("套餐状态修改成功") : R.error("套餐状态修改失败");
    }

    /**
     * 页面回显套餐详情
     *
     * @param setmealId
     * @return
     */
    @GetMapping("/{setmealId}")
    public R<SetmealDto> getSetmeal(@PathVariable Long setmealId) {

        SetmealDto setmealDto = setmealService.getSetMealById(setmealId);

        if (setmealDto != null) {
            return R.success(setmealDto);
        } else {
            return R.error("套餐查看失败");
        }
    }

    /**
     * 修改套餐
     *
     * @param setmealDto
     * @return
     */
    @PutMapping
    public R<String> updateSetMeal(@RequestBody SetmealDto setmealDto) {

        boolean update = setmealService.updateSetMealWithDish(setmealDto);

        return update? R.success("修改成功"): R.error("修改失败，请重试");
    }
}
