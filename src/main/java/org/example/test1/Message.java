package org.example.test1;

/**
 * @author DavyDavyTom Email:a@wk2.cn
 * @since 2025/05/16 16:08
 */
public class Message {

    int type;
    Actor sender;
    Actor receiver;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;

    }

    public Actor getSender() {
        return sender;
    }

    public void setSender(Actor sender) {
        this.sender = sender;
    }

    public Actor getReceiver() {
        return receiver;
    }

    public void setReceiver(Actor receiver) {
        this.receiver = receiver;
    }
}
