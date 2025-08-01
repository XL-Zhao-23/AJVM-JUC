package org.example.actor.test1;


import org.example.actor.MessageType;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 性能对比测试：Synchronized方式 vs Actor方式
 * 
 * @author DavyDavyTom Email:a@wk2.cn
 * @since 2025/05/16 16:05
 */
public class PerformanceTest {
    
    // 测试参数
    private static final int OPERATION_COUNT = 100000; // 操作次数
    private static final int THREAD_COUNT = 10; // 线程数
    private static final int WARMUP_COUNT = 10000; // 预热次数
    
    public static void main(String[] args) {
        System.out.println("=== 性能对比测试：Synchronized vs Actor ===");
        System.out.println("操作次数: " + OPERATION_COUNT);
        System.out.println("线程数: " + THREAD_COUNT);
        System.out.println("预热次数: " + WARMUP_COUNT);
        System.out.println();
        
        // 预热
        warmup();
        
        // 测试Synchronized方式
        long synchronizedTime = testSynchronized();
        
        // 测试Actor方式
        long actorTime = testActor();
        
        // 输出结果
        printResults(synchronizedTime, actorTime);
    }
    
    /**
     * 预热测试
     */
    private static void warmup() {
        System.out.println("开始预热...");
        
        // Synchronized预热
        SynchronizedBuffer syncBuffer = new SynchronizedBuffer();
        for (int i = 0; i < WARMUP_COUNT; i++) {
            syncBuffer.produce();
            syncBuffer.consume();
        }
        
        // Actor预热
        ActorBuffer actorBuffer = new ActorBuffer();
        actorBuffer.start();
        for (int i = 0; i < WARMUP_COUNT; i++) {
            Message produceMsg = new Message();
            produceMsg.setType(MessageType.Product.getMessageId());
            actorBuffer.send(actorBuffer, produceMsg);
            
            Message consumeMsg = new Message();
            consumeMsg.setType(MessageType.Consumer.getMessageId());
            actorBuffer.send(actorBuffer, consumeMsg);
        }
        
        System.out.println("预热完成\n");
    }
    
    /**
     * 测试Synchronized方式
     */
    private static long testSynchronized() {
        System.out.println("测试Synchronized方式...");
        
        SynchronizedBuffer buffer = new SynchronizedBuffer();
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger totalOperations = new AtomicInteger(0);
        
        long startTime = System.currentTimeMillis();
        
        // 创建多个线程进行并发测试
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            Thread thread = new Thread(() -> {
                int operationsPerThread = OPERATION_COUNT / THREAD_COUNT;
                for (int j = 0; j < operationsPerThread; j++) {
                    if (j % 2 == 0) {
                        buffer.produce();
                    } else {
                        buffer.consume();
                    }
                    totalOperations.incrementAndGet();
                }
                latch.countDown();
            });
            thread.start();
        }
        
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("Synchronized方式完成，总操作数: " + totalOperations.get());
        System.out.println("耗时: " + duration + "ms");
        System.out.println("吞吐量: " + String.format("%.2f", (double) totalOperations.get() / duration * 1000) + " ops/s");
        System.out.println();
        
        return duration;
    }
    
    /**
     * 测试Actor方式
     */
    private static long testActor() {
        System.out.println("测试Actor方式...");
        
        ActorBuffer buffer = new ActorBuffer();
        buffer.start();
        
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger totalOperations = new AtomicInteger(0);
        
        long startTime = System.currentTimeMillis();
        
        // 创建多个线程发送消息
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            Thread thread = new Thread(() -> {
                int operationsPerThread = OPERATION_COUNT / THREAD_COUNT;
                for (int j = 0; j < operationsPerThread; j++) {
                    Message message = new Message();
                    if (j % 2 == 0) {
                        message.setType(MessageType.Product.getMessageId());
                    } else {
                        message.setType(MessageType.Consumer.getMessageId());
                    }
                    buffer.send(buffer, message);
                    totalOperations.incrementAndGet();
                }
                latch.countDown();
            });
            thread.start();
        }
        
        try {
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        // 等待Actor处理完所有消息
        while (!buffer.queue.isEmpty()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("Actor方式完成，总操作数: " + totalOperations.get());
        System.out.println("耗时: " + duration + "ms");
        System.out.println("吞吐量: " + String.format("%.2f", (double) totalOperations.get() / duration * 1000) + " ops/s");
        System.out.println();
        
        return duration;
    }
    
    /**
     * 输出测试结果
     */
    private static void printResults(long synchronizedTime, long actorTime) {
        System.out.println("=== 测试结果对比 ===");
        System.out.println("Synchronized方式耗时: " + synchronizedTime + "ms");
        System.out.println("Actor方式耗时: " + actorTime + "ms");
        
        double ratio = (double) synchronizedTime / actorTime;
        if (ratio > 1) {
            System.out.println("Actor方式比Synchronized方式快 " + String.format("%.2f", ratio) + " 倍");
        } else {
            System.out.println("Synchronized方式比Actor方式快 " + String.format("%.2f", 1.0 / ratio) + " 倍");
        }
        
        System.out.println("\n=== 性能分析 ===");
        System.out.println("Synchronized方式特点:");
        System.out.println("- 直接线程同步，开销较小");
        System.out.println("- 适合高频率、小数据量的操作");
        System.out.println("- 线程间直接竞争锁");
        
        System.out.println("\nActor方式特点:");
        System.out.println("- 消息传递，避免锁竞争");
        System.out.println("- 适合复杂的状态管理");
        System.out.println("- 更好的并发隔离");
        System.out.println("- 消息队列开销");
    }
    
    /**
     * Synchronized方式的Buffer实现
     */
    static class SynchronizedBuffer {
        private int num = 0;
        
        public synchronized void produce() {
            num++;
        }
        
        public synchronized void consume() {
            if (num > 0) {
                num--;
            }
        }
        
        public synchronized int getNum() {
            return num;
        }
    }
    
    /**
     * Actor方式的Buffer实现
     */
    static class ActorBuffer extends Actor {
        private int num = 0;
        
        public ActorBuffer() {
            this.setName("ActorBuffer");
            this.behavior = new Runnable[2];
            this.queue = new LinkedBlockingQueue<>();
            
            behavior[MessageType.Product.getMessageId()] = () -> {
                num++;
            };
            
            behavior[MessageType.Consumer.getMessageId()] = () -> {
                if (num > 0) {
                    num--;
                }
            };
        }
        
        public int getNum() {
            return num;
        }
    }
} 