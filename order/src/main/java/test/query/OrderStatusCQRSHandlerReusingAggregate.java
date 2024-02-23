package test.query;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import org.axonframework.config.ProcessingGroup;
import org.axonframework.eventhandling.EventHandler;
import org.axonframework.queryhandling.QueryHandler;
import org.axonframework.queryhandling.QueryUpdateEmitter;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import test.aggregate.*;
import test.event.*;

@Service
@ProcessingGroup("orderStatus")
public class OrderStatusCQRSHandlerReusingAggregate {

    //<<< EDA / CQRS
    @Autowired
    private OrderReadModelRepository repository;

    //<<< Etc / RSocket
    @Autowired
    private QueryUpdateEmitter queryUpdateEmitter;

    //>>> Etc / RSocket

    @QueryHandler
    public List<OrderReadModel> handle(OrderStatusQuery query) {
        return repository.findAll();
    }

    @QueryHandler
    public Optional<OrderReadModel> handle(OrderStatusSingleQuery query) {
        return repository.findById(query.getOrderId());
    }

    @EventHandler
    public void whenOrderCreated_then_CREATE(OrderCreatedEvent event)
        throws Exception {
        OrderReadModel entity = new OrderReadModel();
        OrderAggregate aggregate = new OrderAggregate();
        aggregate.on(event);

        BeanUtils.copyProperties(aggregate, entity);

        repository.save(entity);

        //<<< Etc / RSocket
        queryUpdateEmitter.emit(OrderStatusQuery.class, query -> true, entity);
        //>>> Etc / RSocket

    }

    @EventHandler
    public void whenOrderConfirmed_then_UPDATE(OrderConfirmedEvent event)
        throws Exception {
        repository
            .findById(event.getOrderId())
            .ifPresent(entity -> {
                OrderAggregate aggregate = new OrderAggregate();

                BeanUtils.copyProperties(entity, aggregate);
                aggregate.on(event);
                BeanUtils.copyProperties(aggregate, entity);

                repository.save(entity);

                //<<< Etc / RSocket
                queryUpdateEmitter.emit(
                    OrderStatusSingleQuery.class,
                    query -> query.getOrderId().equals(event.getOrderId()),
                    entity
                );
                //>>> Etc / RSocket

            });
    }

    @EventHandler
    public void whenOrderCanceled_then_UPDATE(OrderCanceledEvent event)
        throws Exception {
        repository
            .findById(event.getOrderId())
            .ifPresent(entity -> {
                OrderAggregate aggregate = new OrderAggregate();

                BeanUtils.copyProperties(entity, aggregate);
                aggregate.on(event);
                BeanUtils.copyProperties(aggregate, entity);

                repository.save(entity);

                //<<< Etc / RSocket
                queryUpdateEmitter.emit(
                    OrderStatusSingleQuery.class,
                    query -> query.getOrderId().equals(event.getOrderId()),
                    entity
                );
                //>>> Etc / RSocket

            });
    }
    //>>> EDA / CQRS
}
