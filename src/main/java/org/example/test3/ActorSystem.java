package org.example.test3;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ActorSystem {
    private final Map<String, ActorRef> actors = new ConcurrentHashMap<>();
    private final Dispatcher dispatcher;

    public ActorSystem(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    public Dispatcher getDispatcher() {
        return dispatcher;
    }

    public ActorRef register(String name, Actor actor) {
        actor.setSystem(this);
        actor.setName(name);
        ActorRef ref = new ActorRef(actor);
        actors.put(name, ref);
        return ref;
    }

    public ActorRef get(String name) {
        return actors.get(name);
    }
}
