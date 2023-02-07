package reggieVersion1.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @projectName: Reggie_total
 * @package: reggieVersion1.service
 * @className: DishServiceTest
 * @author: White
 * @description: TODO
 * @date: 2023/1/27 13:58
 * @version: 1.0
 */
@SpringBootTest
public class DishServiceTest {
    @Autowired
    private DishService dishService;

    @Test
    void test(){
        dishService.getPageByPageHelper(1,5);
    }
}
