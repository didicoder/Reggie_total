package reggieVersion1.dto;


import lombok.Data;
import reggieVersion1.domain.Order;
import reggieVersion1.domain.OrderDetail;

import java.util.List;

/**
 * 订单dto类
 */
@Data
public class OrderDto extends Order {

    private String userName;

    private String phone;

    private String address;

    private String consignee;

    private List<OrderDetail> orderDetails;
	
}
