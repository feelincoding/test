package test.event;

import lombok.Data;
import lombok.ToString;

//<<< DDD / Domain Event

@Data
@ToString
public class OrderCanceledEvent {

    private String orderId;
    private String productName;
    private String productId;
    private String status;
    private Integer qty;
    private Integer userId;
}
//>>> DDD / Domain Event
