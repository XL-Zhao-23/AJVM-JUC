package org.example.test2;

import org.example.MessageType;

/**
 * ${description}
 *
 * @author DavyDavyTom Email:a@wk2.cn
 * @since 2025/05/16 16:04
 */
public class Test {
    public static void main(String[] args) {
        Actor producer = new Actor();
        producer.setName("producer");
        Actor consumer = new Actor();
        consumer.setName("consumer");
        Buffer buffer = new Buffer();

        buffer.start();

        for (int i = 0; i < 50; i++) {
            // 生产
            Message<Integer> m = new Message<>();
            m.setType(MessageType.Product.getMessageId());
            m.setPayload(3); // 生产3个
            producer.send(buffer, m);
        }

        for (int i = 0; i < 100; i++) {
            // 消费
            Message<Integer> c = new Message<>();
            c.setType(MessageType.Consumer.getMessageId());
            c.setPayload(2); // 消费2个
            consumer.send(buffer, c);
        }
    }
}