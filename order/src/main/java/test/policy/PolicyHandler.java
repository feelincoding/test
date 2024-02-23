package test.policy;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.DisallowReplay;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import test.aggregate.*;
import test.command.*;
import test.event.*;

//<<< Clean Arch / Inbound Adaptor
//<<< EDA / Event Handler
@Service
@ProcessingGroup("order")
public class PolicyHandler {

    @Autowired
    CommandGateway commandGateway;

    @EventHandler
    //@DisallowReplay
    public void wheneverOrderCreated_OrderSaga(OrderCreatedEvent orderCreated) {
        System.out.println(orderCreated.toString());

        OrderSagaCommand command = new OrderSagaCommand();
        //TODO: mapping attributes (anti-corruption)
        commandGateway.send(command);
    }

    @EventHandler
    //@DisallowReplay
    public void wheneverProductDecreased_OrderSaga(
        ProductDecreasedEvent productDecreased
    ) {
        System.out.println(productDecreased.toString());

        OrderSagaCommand command = new OrderSagaCommand();
        //TODO: mapping attributes (anti-corruption)
        commandGateway.send(command);
    }

    @EventHandler
    //@DisallowReplay
    public void wheneverOrderConfirmed_OrderSaga(
        OrderConfirmedEvent orderConfirmed
    ) {
        System.out.println(orderConfirmed.toString());

        OrderSagaCommand command = new OrderSagaCommand();
        //TODO: mapping attributes (anti-corruption)
        commandGateway.send(command);
    }
}
//>>> EDA / Event Handler
//>>> Clean Arch / Inbound Adaptor
