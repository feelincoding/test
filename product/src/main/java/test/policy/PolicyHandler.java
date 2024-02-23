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
@ProcessingGroup("product")
public class PolicyHandler {

    @Autowired
    CommandGateway commandGateway;
}
//>>> EDA / Event Handler
//>>> Clean Arch / Inbound Adaptor
