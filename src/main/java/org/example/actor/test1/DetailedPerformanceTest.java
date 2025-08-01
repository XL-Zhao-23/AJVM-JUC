package org.example.actor.test1;

import org.example.actor.MessageType;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * 详细性能对比测试：Synchronized方式 vs Actor方式
 * 包含不同场景和参数配置的对比
 * 
 * @author DavyDavyTom Email:a@wk2.cn
 * @since 2025/05/16 16:05
 */
public class DetailedPerformanceTest {
    
    public static void main(String[] args) {
        System.out.println("=== 详细性能对比测试 ===");
        
        // 测试不同操作次数
        testDifferentOperationCounts();
        
        // 测试不同线程数
        testDifferentThreadCounts();
        
        // 测试不同负载类型
        testDifferentLoadTypes();
        
        // 测试内存使用情况
        testMemoryUsage();
    }
    
    /**
     * 测试不同操作次数对性能的影响
     */
    private static void testDifferentOperationCounts() {
        System.out.println("\n=== 测试不同操作次数 ===");
        int[] operationCounts = {10000, 50000, 100000, 200000};
        int threadCount = 8;
        
        for (int count : operationCounts) {
            System.out.println("\n操作次数: " + count);
            
            long syncTime = runSynchronizedTest(count, threadCount);
            long actorTime = runActorTest(count, threadCount);
            
            double ratio = (double) syncTime / actorTime;
            System.out.println("性能比 (Sync/Actor): " + String.format("%.2f", ratio));
        }
    }
    
    /**
     * 测试不同线程数对性能的影响
     */
    private static void testDifferentThreadCounts() {
        System.out.println("\n=== 测试不同线程数 ===");
        int[] threadCounts = {2, 4, 8, 16, 32};
        int operationCount = 100000;
        
        for (int threads : threadCounts) {
            System.out.println("\n线程数: " + threads);
            
            long syncTime = runSynchronizedTest(operationCount, threads);
            long actorTime = runActorTest(operationCount, threads);
            
            double syncThroughput = (double) operationCount / syncTime * 1000;
            double actorThroughput = (double) operationCount / actorTime * 1000;
            
            System.out.println("Synchronized吞吐量: " + String.format("%.2f", syncThroughput) + " ops/s");
            System.out.println("Actor吞吐量: " + String.format("%.2f", actorThroughput) + " ops/s");
        }
    }
    
    /**
     * 测试不同负载类型
     */
    private static void testDifferentLoadTypes() {
        System.out.println("\n=== 测试不同负载类型 ===");
        int operationCount = 50000;
        int threadCount = 8;
        
        // 测试1: 高频率小操作
        System.out.println("\n1. 高频率小操作测试");
        long syncTime1 = runSynchronizedTest(operationCount, threadCount);
        long actorTime1 = runActorTest(operationCount, threadCount);
        
        // 测试2: 包含复杂计算的操作
        System.out.println("\n2. 包含复杂计算的操作测试");
        long syncTime2 = runSynchronizedComplexTest(operationCount, threadCount);
        long actorTime2 = runActorComplexTest(operationCount, threadCount);
        
        // 测试3: 读写混合操作
        System.out.println("\n3. 读写混合操作测试");
        long syncTime3 = runSynchronizedMixedTest(operationCount, threadCount);
        long actorTime3 = runActorMixedTest(operationCount, threadCount);
        
        System.out.println("\n=== 负载类型测试结果 ===");
        System.out.println("高频率小操作 - Sync: " + syncTime1 + "ms, Actor: " + actorTime1 + "ms");
        System.out.println("复杂计算操作 - Sync: " + syncTime2 + "ms, Actor: " + actorTime2 + "ms");
        System.out.println("读写混合操作 - Sync: " + syncTime3 + "ms, Actor: " + actorTime3 + "ms");
    }
    
    /**
     * 测试内存使用情况
     */
    private static void testMemoryUsage() {
        System.out.println("\n=== 内存使用情况测试 ===");
        
        Runtime runtime = Runtime.getRuntime();
        
        // 测试Synchronized方式的内存使用
        System.gc();
        long syncMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
        
        SynchronizedBuffer syncBuffer = new SynchronizedBuffer();
        for (int i = 0; i < 100000; i++) {
            syncBuffer.produce();
            syncBuffer.consume();
        }
        
        System.gc();
        long syncMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long syncMemoryUsed = syncMemoryAfter - syncMemoryBefore;
        
        // 测试Actor方式的内存使用
        System.gc();
        long actorMemoryBefore = runtime.totalMemory() - runtime.freeMemory();
        
        ActorBuffer actorBuffer = new ActorBuffer();
        actorBuffer.start();
        for (int i = 0; i < 100000; i++) {
            Message produceMsg = new Message();
            produceMsg.setType(MessageType.Product.getMessageId());
            actorBuffer.send(actorBuffer, produceMsg);
            
            Message consumeMsg = new Message();
            consumeMsg.setType(MessageType.Consumer.getMessageId());
            actorBuffer.send(actorBuffer, consumeMsg);
        }
        
        System.gc();
        long actorMemoryAfter = runtime.totalMemory() - runtime.freeMemory();
        long actorMemoryUsed = actorMemoryAfter - actorMemoryBefore;
        
        System.out.println("Synchronized方式内存使用: " + syncMemoryUsed + " bytes");
        System.out.println("Actor方式内存使用: " + actorMemoryUsed + " bytes");
        System.out.println("内存使用比 (Actor/Sync): " + String.format("%.2f", (double) actorMemoryUsed / syncMemoryUsed));
    }
    
    // 基础测试方法
    private static long runSynchronizedTest(int operationCount, int threadCount) {
        SynchronizedBuffer buffer = new SynchronizedBuffer();
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(() -> {
                int operationsPerThread = operationCount / threadCount;
                for (int j = 0; j < operationsPerThread; j++) {
                    if (j % 2 == 0) {
                        buffer.produce();
                    } else {
                        buffer.consume();
                    }
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
        
        return System.currentTimeMillis() - startTime;
    }
    
    private static long runActorTest(int operationCount, int threadCount) {
        ActorBuffer buffer = new ActorBuffer();
        buffer.start();
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(() -> {
                int operationsPerThread = operationCount / threadCount;
                for (int j = 0; j < operationsPerThread; j++) {
                    Message message = new Message();
                    if (j % 2 == 0) {
                        message.setType(MessageType.Product.getMessageId());
                    } else {
                        message.setType(MessageType.Consumer.getMessageId());
                    }
                    buffer.send(buffer, message);
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
        
        return System.currentTimeMillis() - startTime;
    }
    
    // 复杂计算测试
    private static long runSynchronizedComplexTest(int operationCount, int threadCount) {
        SynchronizedComplexBuffer buffer = new SynchronizedComplexBuffer();
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(() -> {
                int operationsPerThread = operationCount / threadCount;
                for (int j = 0; j < operationsPerThread; j++) {
                    if (j % 2 == 0) {
                        buffer.complexProduce();
                    } else {
                        buffer.complexConsume();
                    }
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
        
        return System.currentTimeMillis() - startTime;
    }
    
    private static long runActorComplexTest(int operationCount, int threadCount) {
        ActorComplexBuffer buffer = new ActorComplexBuffer();
        buffer.start();
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(() -> {
                int operationsPerThread = operationCount / threadCount;
                for (int j = 0; j < operationsPerThread; j++) {
                    Message message = new Message();
                    if (j % 2 == 0) {
                        message.setType(MessageType.Product.getMessageId());
                    } else {
                        message.setType(MessageType.Consumer.getMessageId());
                    }
                    buffer.send(buffer, message);
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
        
        while (!buffer.queue.isEmpty()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        return System.currentTimeMillis() - startTime;
    }
    
    // 读写混合测试
    private static long runSynchronizedMixedTest(int operationCount, int threadCount) {
        SynchronizedMixedBuffer buffer = new SynchronizedMixedBuffer();
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            Thread thread = new Thread(() -> {
                int operationsPerThread = operationCount / threadCount;
                for (int j = 0; j < operationsPerThread; j++) {
                    if (j % 3 == 0) {
                        buffer.produce();
                    } else if (j % 3 == 1) {
                        buffer.consume();
                    } else {
                        buffer.read();
                    }
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
        
        return System.currentTimeMillis() - startTime;
    }
    
    private static long runActorMixedTest(int operationCount, int threadCount) {
        ActorMixedBuffer buffer = new ActorMixedBuffer();
        buffer.start();
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            Thread thread = new Thread(() -> {
                int operationsPerThread = operationCount / threadCount;
                for (int j = 0; j < operationsPerThread; j++) {
                    Message message = new Message();
                    if (j % 3 == 0) {
                        message.setType(MessageType.Product.getMessageId());
                    } else if (j % 3 == 1) {
                        message.setType(MessageType.Consumer.getMessageId());
                    } else {
                        message.setType(2); // 读取操作
                    }
                    buffer.send(buffer, message);
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
        
        while (!buffer.queue.isEmpty()) {
            try {
                Thread.sleep(1);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        
        return System.currentTimeMillis() - startTime;
    }
    
    // 复杂计算Buffer实现
    static class SynchronizedComplexBuffer {
        private int num = 0;
        
        public synchronized void complexProduce() {
            num++;
            // 模拟复杂计算
            for (int i = 0; i < 100; i++) {
                Math.sqrt(i);
            }
        }
        
        public synchronized void complexConsume() {
            if (num > 0) {
                num--;
                // 模拟复杂计算
                for (int i = 0; i < 100; i++) {
                    Math.sqrt(i);
                }
            }
        }
    }
    
    static class ActorComplexBuffer extends Actor {
        private int num = 0;
        
        public ActorComplexBuffer() {
            this.setName("ActorComplexBuffer");
            this.behavior = new Runnable[2];
            this.queue = new LinkedBlockingQueue<>();
            
            behavior[MessageType.Product.getMessageId()] = () -> {
                num++;
                // 模拟复杂计算
                for (int i = 0; i < 100; i++) {
                    Math.sqrt(i);
                }
            };
            
            behavior[MessageType.Consumer.getMessageId()] = () -> {
                if (num > 0) {
                    num--;
                    // 模拟复杂计算
                    for (int i = 0; i < 100; i++) {
                        Math.sqrt(i);
                    }
                }
            };
        }
    }
    
    // 读写混合Buffer实现
    static class SynchronizedMixedBuffer {
        private int num = 0;
        
        public synchronized void produce() {
            num++;
        }
        
        public synchronized void consume() {
            if (num > 0) {
                num--;
            }
        }
        
        public synchronized int read() {
            return num;
        }
    }
    
    static class ActorMixedBuffer extends Actor {
        private int num = 0;
        
        public ActorMixedBuffer() {
            this.setName("ActorMixedBuffer");
            this.behavior = new Runnable[3];
            this.queue = new LinkedBlockingQueue<>();
            
            behavior[MessageType.Product.getMessageId()] = () -> {
                num++;
            };
            
            behavior[MessageType.Consumer.getMessageId()] = () -> {
                if (num > 0) {
                    num--;
                }
            };
            
            behavior[2] = () -> {
                // 读取操作，不做任何修改
                int currentNum = num;
            };
        }
    }
    
    // 基础Buffer实现
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
    }
    
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
    }
} 