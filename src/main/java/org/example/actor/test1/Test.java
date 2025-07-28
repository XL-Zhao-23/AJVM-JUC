package org.example.actor.test1;


import org.example.actor.MessageType;

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
            Message message = new Message();
            message.setType(MessageType.Product.getMessageId());
            producer.send(buffer, message);
        }

        for (int i = 0; i < 100; i++) {
            // 消费
            Message message = new Message();
            message.setType(MessageType.Consumer.getMessageId());
            consumer.send(buffer, message);
        }
    }
}