package test.event;

import lombok.Data;
import lombok.ToString;

//<<< DDD / Domain Event

@Data
@ToString
public class ProductIncreasedEvent {

    private String productId;
    private String productName;
    private Integer stock;
    private String orderId;
}
//>>> DDD / Domain Event
