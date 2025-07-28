package org.example.actor.test3;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/***
 * 统一管理actor，其实只需要管理 有共享变量的 actor， 比如bufferActor
 * 线程管理用 线程池
 */
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
