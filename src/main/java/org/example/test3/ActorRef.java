package org.example.test3;

import java.util.concurrent.CompletableFuture;

/***
 *  封装了 actor，便于 ActorSystem 管理
 *  提供了 tell 和 ask两种方式，其区别在于 是否传入future， 如果传入了future，则可以等待得到future再继续执行
 */
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
