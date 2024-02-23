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
public class ProductAggregate {

    @AggregateIdentifier
    private String productId;

    private String productName;
    private Integer stock;
    private String orderId;

    public ProductAggregate() {}

    @CommandHandler
    public ProductAggregate(RegisterProductCommand command) {
        ProductRegisteredEvent event = new ProductRegisteredEvent();
        BeanUtils.copyProperties(command, event);

        //TODO: check key generation is properly done
        if (event.getProductId() == null) event.setProductId(createUUID());

        apply(event);
    }

    @CommandHandler
    public void handle(DecreaseProductCommand command) {
        ProductDecreasedEvent event = new ProductDecreasedEvent();
        BeanUtils.copyProperties(command, event);

        apply(event);
    }

    @CommandHandler
    public void handle(IncreaseProductCommand command) {
        ProductIncreasedEvent event = new ProductIncreasedEvent();
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
    public void on(ProductRegisteredEvent event) {
        BeanUtils.copyProperties(event, this);
        //TODO: business logic here

    }

    @EventSourcingHandler
    public void on(ProductDecreasedEvent event) {
        //TODO: business logic here

    }

    @EventSourcingHandler
    public void on(ProductIncreasedEvent event) {
        //TODO: business logic here

    }
    //>>> EDA / Event Sourcing

}
//>>> DDD / Aggregate Root
