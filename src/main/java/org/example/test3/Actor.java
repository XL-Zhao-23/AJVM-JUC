package org.example.test3;

import java.util.concurrent.LinkedBlockingQueue;

/***
 *  主要是增加了 Actor 的的统一管理 和 ask-reply 机制
 *  为了隔离访问，将actor的字段全设置为private，然后调用方法由 actorRef 统一管理
 *  并且解耦了 runnable 和 Thread， Thread交给了 线程池管理
 */
public abstract class Actor implements Runnable {
    private final LinkedBlockingQueue<Message> mailbox = new LinkedBlockingQueue<>();
    private ActorSystem system;
    private String name;

    public void setSystem(ActorSystem system) {
        this.system = system;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void enqueue(Message msg) {
        mailbox.offer(msg);
        system.getDispatcher().dispatch(this);  // 提醒 dispatcher 执行我
    }

    public String getName() {
        return name;
    }

    public abstract void receive(Message message);

    @Override
    public void run() {
        Message msg;
        while ((msg = mailbox.poll()) != null) {
            try {
                receive(msg);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
