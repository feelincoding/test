package test.aggregate;

import static org.axonframework.modelling.command.AggregateLifecycle.*;

import java.util.List;
import java.util.UUID;
import lombok.Data;
import lombok.ToString;
import org.axonframework.commandhandling.CommandHandler;
import org.axonframework.eventsourcing.EventSourcingHandler;
import org.axonframework.modelling.command.AggregateIdentifier;
import org.axonframework.spring.stereotype.Aggregate;
import org.springframework.beans.BeanUtils;
import test.command.*;
import test.event.*;
import test.query.*;

//<<< DDD / Aggregate Root
@Aggregate
@Data
@ToString
public class OrderAggregate {

    @AggregateIdentifier
    private String orderId;

    private String productName;
    private String productId;
    private String status;
    private Integer qty;
    private Integer userId;

    public OrderAggregate() {}

    @CommandHandler
    public OrderAggregate(CreateOrderCommand command) {
        OrderCreatedEvent event = new OrderCreatedEvent();
        BeanUtils.copyProperties(command, event);

        //TODO: check key generation is properly done
        if (event.getOrderId() == null) event.setOrderId(createUUID());

        apply(event);
    }

    @CommandHandler
    public void handle(ConfirmOrderCommand command) {
        OrderConfirmedEvent event = new OrderConfirmedEvent();
        BeanUtils.copyProperties(command, event);

        apply(event);
    }

    @CommandHandler
    public void handle(CancelOrderCommand command) {
        OrderCanceledEvent event = new OrderCanceledEvent();
        BeanUtils.copyProperties(command, event);

        apply(event);
    }

    //<<< Etc / ID Generation
    private String createUUID() {
        return UUID.randomUUID().toString();
    }

    //>>> Etc / ID Generation

    //<<< EDA / Event Sourcing

    @EventSourcingHandler
    public void on(OrderCreatedEvent event) {
        BeanUtils.copyProperties(event, this);
        //TODO: business logic here

    }

    @EventSourcingHandler
    public void on(OrderConfirmedEvent event) {
        //TODO: business logic here

    }

    @EventSourcingHandler
    public void on(OrderCanceledEvent event) {
        //TODO: business logic here

    }
    //>>> EDA / Event Sourcing

}
//>>> DDD / Aggregate Root
