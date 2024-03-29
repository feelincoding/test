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
public class ProductController {

    private final CommandGateway commandGateway;
    private final QueryGateway queryGateway;

    public ProductController(
        CommandGateway commandGateway,
        QueryGateway queryGateway
    ) {
        this.commandGateway = commandGateway;
        this.queryGateway = queryGateway;
    }

    @RequestMapping(value = "/products", method = RequestMethod.POST)
    public CompletableFuture registerProduct(
        @RequestBody RegisterProductCommand registerProductCommand
    ) throws Exception {
        System.out.println("##### /product/registerProduct  called #####");

        // send command
        return commandGateway
            .send(registerProductCommand)
            .thenApply(id -> {
                ProductAggregate resource = new ProductAggregate();
                BeanUtils.copyProperties(registerProductCommand, resource);

                resource.setProductId((String) id);

                return new ResponseEntity<>(hateoas(resource), HttpStatus.OK);
            });
    }

    @RequestMapping(
        value = "/products/{id}/decreaseproduct",
        method = RequestMethod.PUT,
        produces = "application/json;charset=UTF-8"
    )
    public CompletableFuture decreaseProduct(
        @PathVariable("id") String id,
        @RequestBody DecreaseProductCommand decreaseProductCommand
    ) throws Exception {
        System.out.println("##### /product/decreaseProduct  called #####");

        decreaseProductCommand.setProductId(id);
        // send command
        return commandGateway.send(decreaseProductCommand);
    }

    @RequestMapping(
        value = "/products/{id}/increaseproduct",
        method = RequestMethod.PUT,
        produces = "application/json;charset=UTF-8"
    )
    public CompletableFuture increaseProduct(
        @PathVariable("id") String id,
        @RequestBody IncreaseProductCommand increaseProductCommand
    ) throws Exception {
        System.out.println("##### /product/increaseProduct  called #####");

        increaseProductCommand.setProductId(id);
        // send command
        return commandGateway.send(increaseProductCommand);
    }

    @Autowired
    EventStore eventStore;

    @GetMapping(value = "/products/{id}/events")
    public ResponseEntity getEvents(@PathVariable("id") String id) {
        ArrayList resources = new ArrayList<ProductAggregate>();
        eventStore.readEvents(id).asStream().forEach(resources::add);

        CollectionModel<ProductAggregate> model = CollectionModel.of(resources);

        return new ResponseEntity<>(model, HttpStatus.OK);
    }

    EntityModel<ProductAggregate> hateoas(ProductAggregate resource) {
        EntityModel<ProductAggregate> model = EntityModel.of(resource);

        model.add(
            Link.of("/products/" + resource.getProductId()).withSelfRel()
        );

        model.add(
            Link
                .of("/products/" + resource.getProductId() + "/decreaseproduct")
                .withRel("decreaseproduct")
        );

        model.add(
            Link
                .of("/products/" + resource.getProductId() + "/increaseproduct")
                .withRel("increaseproduct")
        );

        model.add(
            Link
                .of("/products/" + resource.getProductId() + "/events")
                .withRel("events")
        );

        return model;
    }
}
//>>> Clean Arch / Inbound Adaptor
