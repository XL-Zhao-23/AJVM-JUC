package org.example.test1;

import org.example.MessageType;
import org.example.test1.Actor;

import java.util.concurrent.LinkedBlockingQueue;

/**
 * @author DavyDavyTom Email:a@wk2.cn
 * @since 2025/05/16 16:20
 */
public class Buffer extends Actor {

    private int num;
    public Buffer() {
        this.setName("buffer");
        this.num = 1;
        this.behavior = new Runnable[2];
        this.queue = new LinkedBlockingQueue<>();
        behavior[MessageType.Product.getMessageId()] = new Runnable() {
            @Override
            public void run() {
                num++;
                System.out.println(Thread.currentThread().getName() + " num: " + num);
            }
        };
        behavior[MessageType.Consumer.getMessageId()] = new Runnable() {
            @Override
            public void run() {
                if(num > 0){
                    num--;
                    System.out.println(Thread.currentThread().getName() + " num: " + num);
                } else {
                    System.out.println(Thread.currentThread().getName() + " no more items");
                }

            }
        };
    }


}
