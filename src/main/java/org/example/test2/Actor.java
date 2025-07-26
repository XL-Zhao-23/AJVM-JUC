package org.example.test2;

import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class Actor extends Thread {

    public LinkedBlockingQueue<Message<?>> queue;
    public Map<Integer, Consumer<Message<?>>> behavior;

    public Actor() {}

    public Actor(LinkedBlockingQueue<Message<?>> queue, Map<Integer, Consumer<Message<?>>> behavior) {
        this.queue = queue;
        this.behavior = behavior;
    }

    public void send(Actor actor, Message<?> message) {
        message.setSender(this);
        message.setReceiver(actor);
        actor.queue.add(message);
    }

    @Override
    public void run() {
        while (true) {
            try {
                Message<?> message = queue.take(); // 阻塞等待
                Consumer<Message<?>> handler = behavior.get(message.getType());
                if (handler != null) {
                    handler.accept(message);
                } else {
                    System.err.println(getName() + " 无 handler 处理类型: " + message.getType());
                }
            } catch (Exception e) {
                System.err.println(getName() + " 处理消息时异常: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }
}
