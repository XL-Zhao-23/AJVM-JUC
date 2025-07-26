package org.example.test4;

public class CrashActor extends Actor {
    @Override
    public void receive(Message message) {
        if ("boom".equals(message.payload)) {
            throw new RuntimeException("Simulated crash!");
        } else {
            System.out.println("Received: " + message.payload);
        }
    }

    @Override
    public void onError(Throwable e, Message message) {
        System.out.println("[CrashActor] handled error: " + e.getMessage());
    }
}
