package org.example.actor.test2;

public class Message<T> {
    int type;
    T payload;
    Actor sender;
    Actor receiver;

    public int getType() { return type; }
    public void setType(int type) { this.type = type; }

    public T getPayload() { return payload; }
    public void setPayload(T payload) { this.payload = payload; }

    public Actor getSender() { return sender; }
    public void setSender(Actor sender) { this.sender = sender; }

    public Actor getReceiver() { return receiver; }
    public void setReceiver(Actor receiver) { this.receiver = receiver; }
}
