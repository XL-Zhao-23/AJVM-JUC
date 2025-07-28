package org.example.actor.test4;


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

    // 🔴 新增：默认的容错处理逻辑
    public void handleActorFailure(Actor actor, Throwable e) {
        System.err.println("[ActorSystem] Actor [" + actor.getName() + "] failed with error: " + e);
        // 可拓展为：重启、告警、杀死、替换等策略
    }
}
