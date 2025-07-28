package org.example.actor.test2;



import org.example.actor.MessageType;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.function.Consumer;

public class Buffer extends Actor {
    private int num;

    public Buffer() {
        this.setName("buffer");
        this.num = 1;
        this.queue = new LinkedBlockingQueue<>();

        Map<Integer, Consumer<Message<?>>> handlers = new HashMap<>();

        handlers.put(MessageType.Product.getMessageId(), msg -> {
            num += (Integer) msg.getPayload();  // 可以携带生产数量
            System.out.println(getName() + " num: " + num);
        });

        handlers.put(MessageType.Consumer.getMessageId(), msg -> {
            int count = (Integer) msg.getPayload();
            if (num >= count) {
                num -= count;
                System.out.println(getName() + " consumed " + count + ", now num: " + num);
            } else {
                System.out.println(getName() + " no enough items. now num: " + num);
            }
        });

        this.behavior = handlers;
    }
}
