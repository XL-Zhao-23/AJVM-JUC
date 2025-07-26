package org.example.test3;

import java.util.concurrent.CompletableFuture;

public class Test {
    public static void main(String[] args) throws Exception {
        ActorSystem system = new ActorSystem(new DefaultDispatcher());
        BufferActor buffer = new BufferActor();
        ActorRef ref = system.register("buffer", buffer);

        ref.tell("add", null);
        ref.tell("remove", null);

        // ask 模式：同步获取 num
        CompletableFuture<Object> future = ref.ask("get", null);
        int currentNum = (int) future.get();  // 阻塞等待
        System.out.println("Current num: " + currentNum);
    }
}
