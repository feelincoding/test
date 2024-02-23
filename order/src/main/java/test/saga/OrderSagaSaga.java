package test.saga;

import java.util.UUID;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.modelling.saga.EndSaga;
import org.axonframework.modelling.saga.SagaEventHandler;
import org.axonframework.modelling.saga.StartSaga;
import org.axonframework.spring.stereotype.Saga;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import test.command.*;
import test.event.*;

@Component
@Saga
@ProcessingGroup("OrderSagaSaga")
public class OrderSagaSaga {

    @Autowired
    private transient CommandGateway commandGateway;

    @StartSaga
    @SagaEventHandler(associationProperty = "#correlation-key")
    public void onOrderCreated(OrderCreatedEvent event) {
        DecreaseProductCommand command = new DecreaseProductCommand();

        commandGateway
            .send(command)
            .exceptionally(ex -> {
                CancelOrderCommand cancelOrderCommand = new CancelOrderCommand();
                //
                return commandGateway.send(cancelOrderCommand);
            });
    }

    @SagaEventHandler(associationProperty = "#correlation-key")
    public void onProductDecreased(ProductDecreasedEvent event) {
        ConfirmOrderCommand command = new ConfirmOrderCommand();

        commandGateway.send(command);
    }

    @EndSaga
    @SagaEventHandler(associationProperty = "#correlation-key")
    public void onOrderConfirmed(OrderConfirmedEvent event) {}
}
