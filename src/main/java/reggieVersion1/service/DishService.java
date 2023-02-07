package reggieVersion1.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.github.pagehelper.PageInfo;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestParam;
import reggieVersion1.domain.Dish;
import reggieVersion1.dto.DishDto;

import java.util.List;

/**
 * @author White
 * @description 针对表【dish(菜品管理)】的数据库操作Service
 * @createDate 2023-01-26 09:32:14
 */
public interface DishService extends IService<Dish> {

    /**
     * 新增菜品，并且同时插入菜品对应的口味，需要操作两张表(dish、dishFlavor)
     *
     * @param dishDto
     * @return
     */
    @Transactional
    //对多张表操作，应使用事务
    boolean saveWithFlavor(DishDto dishDto);

    /**
     * 使用mybatis的mapper查询数据，配合mybatis-plus进行分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    Page getPageByMybatis(Integer page, Integer pageSize);

    /**
     * 使用mybatis分页插件PageHelper进行分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    PageInfo getPageByPageHelper(Integer page, Integer pageSize);

    /**
     * 完全使用mybatis-plus进行分页查询
     *
     * @param page
     * @param pageSize
     * @return
     */
    Page getPageByMybatisPlus(Integer page, Integer pageSize);

    /**
     * 完全使用mybatis-plus进行分页查询,并且按菜品名称查询
     *
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    Page getPageByMybatisPlus(Integer page, Integer pageSize, String name);


    /**
     * 根据id查询菜品进行回显
     *
     * @param id
     * @return
     */
    DishDto getByIdWithFlavor(Long id);

    /**
     * 更新菜品信息
     *
     * @param dishDto
     * @return
     */
    @Transactional
    boolean updateWithFlavor(DishDto dishDto);

    /**
     * 前端根据id获取菜品信息及口味信息
     *
     * @param categoryId
     * @param status
     * @return
     */
    List<DishDto> getListByDishDto(Long categoryId, Integer status);

    /**
     * 处理菜品起售、停售，批量起售、批量停售
     * @return
     * @param statusCode
     * @param dishIds
     */
    @Transactional
    boolean updateStatus(Integer statusCode, @RequestParam("ids") List<Long> dishIds);

    /**
     * 删除菜品还要删除对应口味，或批量删除
     *
     * @param dishIds
     * @return
     */
    @Transactional
    boolean removeByList(List<Long> dishIds);
}
