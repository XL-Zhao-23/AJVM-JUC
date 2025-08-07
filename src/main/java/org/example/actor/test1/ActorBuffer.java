package org.example.actor.test1;

import org.example.actor.MessageType;

import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class ActorBuffer extends Actor {
      private int num = 0;

      public ActorBuffer() {
          this.setName("ActorBuffer");
          this.behavior = new Runnable[2];
          this.queue = new ConcurrentLinkedQueue<>();

          behavior[MessageType.Product.getMessageId()] = () -> {
              num++;
          };

          behavior[MessageType.Consumer.getMessageId()] = () -> {
              if (num > 0) {
                  num--;
              }
          };
      }
  }