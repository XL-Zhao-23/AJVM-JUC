package org.example.actor.test3;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CompletableFuture;

/**
 * test3性能对比测试：Synchronized方式 vs Actor方式
 * 测试test3架构下的性能差异
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
        System.out.println("=== test3性能对比测试：Synchronized vs Actor ===");
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
        
        // 测试Actor ask模式
        long actorAskTime = testActorAsk();
        
        // 输出结果
        printResults(synchronizedTime, actorTime, actorAskTime);
    }
    
    /**
     * 预热测试
     */
    private static void warmup() {
        System.out.println("开始预热...");
        
        // Synchronized预热
        SynchronizedBuffer syncBuffer = new SynchronizedBuffer();
        for (int i = 0; i < WARMUP_COUNT; i++) {
            syncBuffer.add();
            syncBuffer.remove();
        }
        
        // Actor预热
        ActorSystem system = new ActorSystem(new DefaultDispatcher());
        BufferActor bufferActor = new BufferActor();
        ActorRef bufferRef = system.register("buffer", bufferActor);
        
        for (int i = 0; i < WARMUP_COUNT; i++) {
            bufferRef.tell("add", null);
            bufferRef.tell("remove", null);
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
                        buffer.add();
                    } else {
                        buffer.remove();
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
     * 测试Actor tell方式
     */
    private static long testActor() {
        System.out.println("测试Actor tell方式...");
        
        ActorSystem system = new ActorSystem(new DefaultDispatcher());
        BufferActor bufferActor = new BufferActor();
        ActorRef bufferRef = system.register("buffer", bufferActor);
        
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger totalOperations = new AtomicInteger(0);
        
        long startTime = System.currentTimeMillis();
        
        // 创建多个线程发送消息
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            Thread thread = new Thread(() -> {
                int operationsPerThread = OPERATION_COUNT / THREAD_COUNT;
                for (int j = 0; j < operationsPerThread; j++) {
                    if (j % 2 == 0) {
                        bufferRef.tell("add", null);
                    } else {
                        bufferRef.tell("remove", null);
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
        
        // 等待所有消息处理完成
        try {
            Thread.sleep(100); // 给Actor一些时间处理剩余消息
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        long endTime = System.currentTimeMillis();
        long duration = endTime - startTime;
        
        System.out.println("Actor tell方式完成，总操作数: " + totalOperations.get());
        System.out.println("耗时: " + duration + "ms");
        System.out.println("吞吐量: " + String.format("%.2f", (double) totalOperations.get() / duration * 1000) + " ops/s");
        System.out.println();
        
        return duration;
    }
    
    /**
     * 测试Actor ask方式
     */
    private static long testActorAsk() {
        System.out.println("测试Actor ask方式...");
        
        ActorSystem system = new ActorSystem(new DefaultDispatcher());
        BufferActor bufferActor = new BufferActor();
        ActorRef bufferRef = system.register("buffer", bufferActor);
        
        CountDownLatch latch = new CountDownLatch(THREAD_COUNT);
        AtomicInteger totalOperations = new AtomicInteger(0);
        
        long startTime = System.currentTimeMillis();
        
        // 创建多个线程发送消息并等待响应
        for (int i = 0; i < THREAD_COUNT; i++) {
            final int threadId = i;
            Thread thread = new Thread(() -> {
                int operationsPerThread = OPERATION_COUNT / THREAD_COUNT;
                for (int j = 0; j < operationsPerThread; j++) {
                    if (j % 2 == 0) {
                        bufferRef.tell("add", null);
                    } else {
                        // 使用ask模式获取当前值
                        CompletableFuture<Object> future = bufferRef.ask("get", null);
                        try {
                            future.get(); // 等待响应
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
        
        System.out.println("Actor ask方式完成，总操作数: " + totalOperations.get());
        System.out.println("耗时: " + duration + "ms");
        System.out.println("吞吐量: " + String.format("%.2f", (double) totalOperations.get() / duration * 1000) + " ops/s");
        System.out.println();
        
        return duration;
    }
    
    /**
     * 输出测试结果
     */
    private static void printResults(long synchronizedTime, long actorTime, long actorAskTime) {
        System.out.println("=== 测试结果对比 ===");
        System.out.println("Synchronized方式耗时: " + synchronizedTime + "ms");
        System.out.println("Actor tell方式耗时: " + actorTime + "ms");
        System.out.println("Actor ask方式耗时: " + actorAskTime + "ms");
        
        double syncActorRatio = (double) synchronizedTime / actorTime;
        double syncAskRatio = (double) synchronizedTime / actorAskTime;
        double tellAskRatio = (double) actorTime / actorAskTime;
        
        System.out.println("\n性能对比:");
        if (syncActorRatio > 1) {
            System.out.println("Actor tell方式比Synchronized方式快 " + String.format("%.2f", syncActorRatio) + " 倍");
        } else {
            System.out.println("Synchronized方式比Actor tell方式快 " + String.format("%.2f", 1.0 / syncActorRatio) + " 倍");
        }
        
        if (syncAskRatio > 1) {
            System.out.println("Actor ask方式比Synchronized方式快 " + String.format("%.2f", syncAskRatio) + " 倍");
        } else {
            System.out.println("Synchronized方式比Actor ask方式快 " + String.format("%.2f", 1.0 / syncAskRatio) + " 倍");
        }
        
        if (tellAskRatio > 1) {
            System.out.println("Actor ask方式比Actor tell方式快 " + String.format("%.2f", tellAskRatio) + " 倍");
        } else {
            System.out.println("Actor tell方式比Actor ask方式快 " + String.format("%.2f", 1.0 / tellAskRatio) + " 倍");
        }
        
        System.out.println("\n=== test3架构性能分析 ===");
        System.out.println("Synchronized方式特点:");
        System.out.println("- 直接线程同步，开销较小");
        System.out.println("- 适合高频率、小数据量的操作");
        System.out.println("- 线程间直接竞争锁");
        
        System.out.println("\nActor tell方式特点:");
        System.out.println("- 消息传递，避免锁竞争");
        System.out.println("- 线程池管理，更好的资源利用");
        System.out.println("- 异步消息处理");
        System.out.println("- 消息队列开销");
        
        System.out.println("\nActor ask方式特点:");
        System.out.println("- 支持同步响应");
        System.out.println("- 可以获取处理结果");
        System.out.println("- 增加了Future开销");
        System.out.println("- 适合需要响应的场景");
    }
    
    /**
     * Synchronized方式的Buffer实现
     */
    static class SynchronizedBuffer {
        private int num = 1;
        
        public synchronized void add() {
            num++;
        }
        
        public synchronized void remove() {
            if (num > 0) {
                num--;
            }
        }
        
        public synchronized int get() {
            return num;
        }
    }
} 