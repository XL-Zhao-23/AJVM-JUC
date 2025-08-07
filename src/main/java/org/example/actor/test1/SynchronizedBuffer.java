package org.example.actor.test1;

public class SynchronizedBuffer {
        private int num = 0;
        
        public synchronized void produce() {
            num++;
        }
        
        public synchronized void consume() {
            if (num > 0) {
                num--;
            }
        }
    }