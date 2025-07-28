package org.example.actor.test4;


import java.util.concurrent.CompletableFuture;

public class Message {
    public final Object payload;
    public final ActorRef sender;
    public final CompletableFuture<Object> future;  // 用于 ask 模式

    public Message(Object payload, ActorRef sender, CompletableFuture<Object> future) {
        this.payload = payload;
        this.sender = sender;
        this.future = future;
    }

    public static Message oneWay(Object payload, ActorRef sender) {
        return new Message(payload, sender, null);
    }

    public static Message ask(Object payload, ActorRef sender, CompletableFuture<Object> future) {
        return new Message(payload, sender, future);
    }
}
