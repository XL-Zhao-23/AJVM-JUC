package org.example.test1;

import java.util.concurrent.LinkedBlockingQueue;
/**
 * @author DavyDavyTom Email:a@wk2.cn
 * @since 2025/05/16 16:05
 */
public class Actor extends Thread{

    // 消息队列
    public LinkedBlockingQueue<Message> queue; // 存储消息，消息中包含要执行的动作信息，比如序号
    // 数组
    public Runnable[] behavior; // 存储可执行的动作
    public Actor(){

    }
    public Actor(LinkedBlockingQueue<Message> queue, Runnable[] behavior){
        this.behavior = behavior;
        this.queue = queue;
    }
    // 发消息

    public void send(Actor actor, Message message){
        message.setSender(this);
        message.setReceiver(actor);
        actor.queue.add(message);
    }

    // 处理消息
    @Override
    public void run(){
        // 业务逻辑
        while(true){
            if(!queue.isEmpty()){
                Message message = queue.poll();
                behavior[message.getType()].run();
            }
        }
    }


}
