package test.api;

import java.util.ArrayList;
import java.util.concurrent.CompletableFuture;
import org.axonframework.commandhandling.gateway.CommandGateway;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.eventsourcing.eventstore.EventStore;
import org.axonframework.queryhandling.QueryGateway;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.CollectionModel;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import test.aggregate.*;
import test.command.*;

//<<< Clean Arch / Inbound Adaptor
@RestController
public class OrderController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    public OrderController(
        CommandGateway commandGateway,
        QueryGateway queryGateway
    ) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @RequestMapping(value = "/orders", method = RequestMethod.POST)
    public CompletableFuture createOrder(
        @RequestBody CreateOrderCommand createOrderCommand
    ) throws Exception {
        System.out.println("##### /order/createOrder  called #####");

        // send command
        return commandGateway
            .send(createOrderCommand)
            .thenApply(id -> {
                OrderAggregate resource = new OrderAggregate();
                BeanUtils.copyProperties(createOrderCommand, resource);

                resource.setOrderId((String) id);

                return new ResponseEntity<>(hateoas(resource), HttpStatus.OK);
            });
    }

    @RequestMapping(
        value = "/orders/{id}/confirmorder",
        method = RequestMethod.PUT,
        produces = "application/json;charset=UTF-8"
    )
    public CompletableFuture confirmOrder(@PathVariable("id") String id)
        throws Exception {
        System.out.println("##### /order/confirmOrder  called #####");
        ConfirmOrderCommand confirmOrderCommand = new ConfirmOrderCommand();
        confirmOrderCommand.setOrderId(id);
        // send command
        return commandGateway.send(confirmOrderCommand);
    }

    @RequestMapping(
        value = "/orders/{id}/cancelorder",
        method = RequestMethod.PUT,
        produces = "application/json;charset=UTF-8"
    )
    public CompletableFuture cancelOrder(@PathVariable("id") String id)
        throws Exception {
        System.out.println("##### /order/cancelOrder  called #####");
        CancelOrderCommand cancelOrderCommand = new CancelOrderCommand();
        cancelOrderCommand.setOrderId(id);
        // send command
        return commandGateway.send(cancelOrderCommand);
    }

    @Autowired
    EventStore eventStore;

    @GetMapping(value = "/orders/{id}/events")
    public ResponseEntity getEvents(@PathVariable("id") String id) {
        ArrayList resources = new ArrayList<OrderAggregate>();
        eventStore.readEvents(id).asStream().forEach(resources::add);

        CollectionModel<OrderAggregate> model = CollectionModel.of(resources);

        return new ResponseEntity<>(model, HttpStatus.OK);
    }

    EntityModel<OrderAggregate> hateoas(OrderAggregate resource) {
        EntityModel<OrderAggregate> model = EntityModel.of(resource);

        model.add(Link.of("/orders/" + resource.getOrderId()).withSelfRel());

        model.add(
            Link
                .of("/orders/" + resource.getOrderId() + "/confirmorder")
                .withRel("confirmorder")
        );

        model.add(
            Link
                .of("/orders/" + resource.getOrderId() + "/cancelorder")
                .withRel("cancelorder")
        );

        model.add(
            Link
                .of("/orders/" + resource.getOrderId() + "/events")
                .withRel("events")
        );

        return model;
    }
}
//>>> Clean Arch / Inbound Adaptor
