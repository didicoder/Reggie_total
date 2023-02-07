package reggieVersion1.dto;

import lombok.Data;
import reggieVersion1.domain.Dish;
import reggieVersion1.domain.DishFlavor;

import java.util.ArrayList;
import java.util.List;

/**
 * DTO，数据传输对象，一般用于展示层和服务层之间的数据传输
 */
@Data
public class DishDto extends Dish {

    //扩展口味做法
    private List<DishFlavor> flavors = new ArrayList<>();

    private String categoryName;

    private Integer copies;
}
