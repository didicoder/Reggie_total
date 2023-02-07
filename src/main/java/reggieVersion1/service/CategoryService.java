package reggieVersion1.service;

import com.baomidou.mybatisplus.extension.service.IService;
import reggieVersion1.controller.utils.R;
import reggieVersion1.domain.Category;

/**
 * <p>
 * 菜品及套餐分类 服务类
 * </p>
 *
 * @author White
 * @since 2023-01-21
 */
public interface CategoryService extends IService<Category> {
    /**
     * 分页查询
     * @return
     */
    R getPages(Integer page,Integer pageSize);

    boolean removeByCheck(Long id);

}
