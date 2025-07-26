package org.example.test3;

public class BufferActor extends Actor {
    private int num = 1;

    @Override
    public void receive(Message message) {
        if ("add".equals(message.payload)) {
            num++;
            System.out.println("Produced. num = " + num);
        } else if ("remove".equals(message.payload)) {
            if (num > 0) {
                num--;
                System.out.println("Consumed. num = " + num);
            } else {
                System.out.println("No more items.");
            }
        } else if ("get".equals(message.payload)) {
            if (message.future != null) {
                message.future.complete(num);  // 回传值
            }
        }
    }
}
