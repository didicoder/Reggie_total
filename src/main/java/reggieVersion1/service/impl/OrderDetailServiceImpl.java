package reggieVersion1.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import reggieVersion1.domain.OrderDetail;
import reggieVersion1.service.OrderDetailService;
import reggieVersion1.mapper.OrderDetailMapper;
import org.springframework.stereotype.Service;

/**
* @author White
* @description 针对表【order_detail(订单明细表)】的数据库操作Service实现
* @createDate 2023-01-31 11:22:29
*/
@Service
public class OrderDetailServiceImpl extends ServiceImpl<OrderDetailMapper, OrderDetail>
    implements OrderDetailService{

}




