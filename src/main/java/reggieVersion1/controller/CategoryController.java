package reggieVersion1.controller;


import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reggieVersion1.controller.utils.R;
import reggieVersion1.domain.Category;
import reggieVersion1.service.CategoryService;

import java.util.List;

/**
 * <p>
 * 菜品及套餐分类 前端控制器
 * </p>
 *
 * @author White
 * @since 2023-01-21
 */
@RestController
@RequestMapping("/category")
public class CategoryController {
    @Autowired
    private CategoryService categoryService;

    /**
     * 分页查询分类数据
     *
     * @param page
     * @param pageSize
     * @return
     */
    @GetMapping("/page/{page}/{pageSize}")
    public R<Page> getPages(@PathVariable Integer page, @PathVariable Integer pageSize) {
        return categoryService.getPages(page, pageSize);
    }

    /**
     * 添加分类
     *
     * @param category
     * @return
     */
    @PostMapping
    public R<Category> insertCategory(@RequestBody Category category) {
        if (category != null) {
            if (categoryService.save(category)) {
                return R.success(category);
            }
        }
        return R.error("菜品添加失败");
    }

    /**
     * 根绝id删除分类，并且检查是否关联菜品或套餐
     *
     * @param id
     * @return
     */
    @DeleteMapping("/{id}")
    public R<String> deleteCategoryById(@PathVariable String id) {
        Long tempId = Long.parseLong(id);
        if (categoryService.removeByCheck(tempId)) {
            return R.success("删除成功");
        } else {
            return R.error("删除失败");
        }
    }

    /**
     * 修改分类
     *
     * @param category
     * @return
     */
    @PutMapping
    public R<String> updateCategory(@RequestBody Category category) {
        if (categoryService.updateById(category)) {
            return R.success("修改分类成功");
        } else {
            return R.error("修改分类失败");
        }
    }

    /**
     * 根据条件查询分类数据
     *
     * @param type
     * @return
     */
    @GetMapping("/list/{type}")
    public R<List<Category>> getList(@PathVariable String type) {
        Integer tempType = Integer.parseInt(type);

        List<Category> list = categoryService.list(new LambdaQueryWrapper<Category>()
                .eq(Category::getType, tempType)
                .orderByAsc(Category::getSort)
                .orderByDesc(Category::getUpdateTime));

        if (list != null) {
            return R.success(list);
        } else {
            return R.error("该分类不存在");
        }
    }

    @GetMapping("/list")
    public R<List<Category>> getAllList() {
        List<Category> list = categoryService.list(new LambdaQueryWrapper<Category>()
                .orderByAsc(Category::getSort)
                .orderByDesc(Category::getUpdateTime));

        if (list != null) {
            return R.success(list);
        } else {
            return R.error("该分类不存在");
        }
    }
}
