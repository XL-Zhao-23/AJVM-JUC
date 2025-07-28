package org.example.actor.test4;


public class Test {
    public static void main(String[] args) throws Exception {
        ActorSystem system = new ActorSystem(new DefaultDispatcher());

        ActorRef ref = system.register("crasher", new CrashActor());
        ref.tell("hello", null);
        ref.tell("boom", null);     // 会抛异常但主线程不会挂
        ref.tell("world", null);

        Thread.sleep(1000); // 等待异步输出
    }
}
