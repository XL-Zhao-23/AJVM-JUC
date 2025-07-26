package org.example.test4;

import java.util.concurrent.CompletableFuture;


public class ActorRef {
    private final Actor actor;

    public ActorRef(Actor actor) {
        this.actor = actor;
    }

    public void tell(Object msg, ActorRef sender) {
        actor.enqueue(Message.oneWay(msg, sender));
    }

    public CompletableFuture<Object> ask(Object msg, ActorRef sender) {
        CompletableFuture<Object> future = new CompletableFuture<>();
        actor.enqueue(Message.ask(msg, sender, future));
        return future;
    }

    public String getName() {
        return actor.getName();
    }
}
