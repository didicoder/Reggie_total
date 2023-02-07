package reggieVersion1.dto;


import lombok.Data;
import reggieVersion1.domain.Setmeal;
import reggieVersion1.domain.SetmealDish;

import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
