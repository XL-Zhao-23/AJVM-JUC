package org.example.test4;

import java.util.concurrent.LinkedBlockingQueue;

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
        system.getDispatcher().dispatch(this); // 交由 dispatcher 执行
    }

    public abstract void receive(Message message);

    // 🔴 新增：可选的错误处理钩子
    public void onError(Throwable e, Message message) {
        System.err.println("Actor [" + name + "] error: " + e.getMessage());
    }

    @Override
    public void run() {
        Message msg;
        while ((msg = mailbox.poll()) != null) {
            try {
                receive(msg);
            } catch (Throwable e) {
                onError(e, msg); // 调用本地钩子
                system.handleActorFailure(this, e); // 🔴 通知 system
            }
        }
    }

    public String getName() {
        return name;
    }
}
