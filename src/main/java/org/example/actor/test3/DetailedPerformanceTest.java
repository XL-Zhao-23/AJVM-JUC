package org.example.actor.test3;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * test3详细性能对比测试
 * 专门针对test3架构的特点进行性能对比
 * 
 * @author DavyDavyTom Email:a@wk2.cn
 * @since 2025/05/16 16:05
 */
public class DetailedPerformanceTest {
    
    public static void main(String[] args) {
        System.out.println("=== test3详细性能对比测试 ===");
        
        // 测试不同操作次数
        testDifferentOperationCounts();
        
        // 测试不同线程数
        testDifferentThreadCounts();
        
        // 测试不同消息类型
        testDifferentMessageTypes();
        
        // 测试Actor系统开销
        testActorSystemOverhead();
        
        // 测试并发场景
        testConcurrencyScenarios();
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
            long actorTellTime = runActorTellTest(count, threadCount);
            long actorAskTime = runActorAskTest(count, threadCount);
            
            System.out.println("性能比 (Sync/ActorTell): " + String.format("%.2f", (double) syncTime / actorTellTime));
            System.out.println("性能比 (Sync/ActorAsk): " + String.format("%.2f", (double) syncTime / actorAskTime));
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
            long actorTellTime = runActorTellTest(operationCount, threads);
            long actorAskTime = runActorAskTest(operationCount, threads);
            
            double syncThroughput = (double) operationCount / syncTime * 1000;
            double actorTellThroughput = (double) operationCount / actorTellTime * 1000;
            double actorAskThroughput = (double) operationCount / actorAskTime * 1000;
            
            System.out.println("Synchronized吞吐量: " + String.format("%.2f", syncThroughput) + " ops/s");
            System.out.println("Actor tell吞吐量: " + String.format("%.2f", actorTellThroughput) + " ops/s");
            System.out.println("Actor ask吞吐量: " + String.format("%.2f", actorAskThroughput) + " ops/s");
        }
    }
    
    /**
     * 测试不同消息类型
     */
    private static void testDifferentMessageTypes() {
        System.out.println("\n=== 测试不同消息类型 ===");
        int operationCount = 50000;
        int threadCount = 8;
        
        // 测试1: 简单字符串消息
        System.out.println("\n1. 简单字符串消息测试");
        long syncTime1 = runSynchronizedTest(operationCount, threadCount);
        long actorTime1 = runActorTellTest(operationCount, threadCount);
        
        // 测试2: 复杂对象消息
        System.out.println("\n2. 复杂对象消息测试");
        long syncTime2 = runSynchronizedComplexTest(operationCount, threadCount);
        long actorTime2 = runActorComplexTest(operationCount, threadCount);
        
        // 测试3: 混合消息类型
        System.out.println("\n3. 混合消息类型测试");
        long syncTime3 = runSynchronizedMixedTest(operationCount, threadCount);
        long actorTime3 = runActorMixedTest(operationCount, threadCount);
        
        System.out.println("\n=== 消息类型测试结果 ===");
        System.out.println("简单消息 - Sync: " + syncTime1 + "ms, Actor: " + actorTime1 + "ms");
        System.out.println("复杂消息 - Sync: " + syncTime2 + "ms, Actor: " + actorTime2 + "ms");
        System.out.println("混合消息 - Sync: " + syncTime3 + "ms, Actor: " + actorTime3 + "ms");
    }
    
    /**
     * 测试Actor系统开销
     */
    private static void testActorSystemOverhead() {
        System.out.println("\n=== Actor系统开销测试 ===");
        int operationCount = 100000;
        int threadCount = 8;
        
        // 测试Actor系统创建开销
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            ActorSystem system = new ActorSystem(new DefaultDispatcher());
            BufferActor actor = new BufferActor();
            ActorRef ref = system.register("actor" + i, actor);
        }
        long actorSystemCreationTime = System.currentTimeMillis() - startTime;
        
        // 测试Synchronized对象创建开销
        startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            SynchronizedBuffer buffer = new SynchronizedBuffer();
        }
        long syncCreationTime = System.currentTimeMillis() - startTime;
        
        System.out.println("Actor系统创建时间: " + actorSystemCreationTime + "ms (1000个)");
        System.out.println("Synchronized对象创建时间: " + syncCreationTime + "ms (1000个)");
        System.out.println("创建开销比 (Actor/Sync): " + String.format("%.2f", (double) actorSystemCreationTime / syncCreationTime));
    }
    
    /**
     * 测试并发场景
     */
    private static void testConcurrencyScenarios() {
        System.out.println("\n=== 并发场景测试 ===");
        int operationCount = 50000;
        int threadCount = 16;
        
        // 测试1: 高并发写操作
        System.out.println("\n1. 高并发写操作测试");
        long syncWriteTime = runSynchronizedWriteTest(operationCount, threadCount);
        long actorWriteTime = runActorWriteTest(operationCount, threadCount);
        
        // 测试2: 高并发读操作
        System.out.println("\n2. 高并发读操作测试");
        long syncReadTime = runSynchronizedReadTest(operationCount, threadCount);
        long actorReadTime = runActorReadTest(operationCount, threadCount);
        
        // 测试3: 读写混合操作
        System.out.println("\n3. 读写混合操作测试");
        long syncMixedTime = runSynchronizedMixedTest(operationCount, threadCount);
        long actorMixedTime = runActorMixedTest(operationCount, threadCount);
        
        System.out.println("\n=== 并发场景测试结果 ===");
        System.out.println("写操作 - Sync: " + syncWriteTime + "ms, Actor: " + actorWriteTime + "ms");
        System.out.println("读操作 - Sync: " + syncReadTime + "ms, Actor: " + actorReadTime + "ms");
        System.out.println("混合操作 - Sync: " + syncMixedTime + "ms, Actor: " + actorMixedTime + "ms");
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
                        buffer.add();
                    } else {
                        buffer.remove();
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
    
    private static long runActorTellTest(int operationCount, int threadCount) {
        ActorSystem system = new ActorSystem(new DefaultDispatcher());
        BufferActor bufferActor = new BufferActor();
        ActorRef bufferRef = system.register("buffer", bufferActor);
        
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(() -> {
                int operationsPerThread = operationCount / threadCount;
                for (int j = 0; j < operationsPerThread; j++) {
                    if (j % 2 == 0) {
                        bufferRef.tell("add", null);
                    } else {
                        bufferRef.tell("remove", null);
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
        
        // 等待Actor处理完所有消息
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        return System.currentTimeMillis() - startTime;
    }
    
    private static long runActorAskTest(int operationCount, int threadCount) {
        ActorSystem system = new ActorSystem(new DefaultDispatcher());
        BufferActor bufferActor = new BufferActor();
        ActorRef bufferRef = system.register("buffer", bufferActor);
        
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(() -> {
                int operationsPerThread = operationCount / threadCount;
                for (int j = 0; j < operationsPerThread; j++) {
                    if (j % 2 == 0) {
                        bufferRef.tell("add", null);
                    } else {
                        CompletableFuture<Object> future = bufferRef.ask("get", null);
                        try {
                            future.get(1, TimeUnit.SECONDS);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
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
    
    // 复杂消息测试
    private static long runSynchronizedComplexTest(int operationCount, int threadCount) {
        SynchronizedComplexBuffer buffer = new SynchronizedComplexBuffer();
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(() -> {
                int operationsPerThread = operationCount / threadCount;
                for (int j = 0; j < operationsPerThread; j++) {
                    if (j % 2 == 0) {
                        buffer.add(new ComplexMessage("add", j));
                    } else {
                        buffer.remove(new ComplexMessage("remove", j));
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
        ActorSystem system = new ActorSystem(new DefaultDispatcher());
        ComplexBufferActor bufferActor = new ComplexBufferActor();
        ActorRef bufferRef = system.register("buffer", bufferActor);
        
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(() -> {
                int operationsPerThread = operationCount / threadCount;
                for (int j = 0; j < operationsPerThread; j++) {
                    if (j % 2 == 0) {
                        bufferRef.tell(new ComplexMessage("add", j), null);
                    } else {
                        bufferRef.tell(new ComplexMessage("remove", j), null);
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
        
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        return System.currentTimeMillis() - startTime;
    }
    
    // 混合操作测试
    private static long runSynchronizedMixedTest(int operationCount, int threadCount) {
        SynchronizedBuffer buffer = new SynchronizedBuffer();
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            Thread thread = new Thread(() -> {
                int operationsPerThread = operationCount / threadCount;
                for (int j = 0; j < operationsPerThread; j++) {
                    if (j % 3 == 0) {
                        buffer.add();
                    } else if (j % 3 == 1) {
                        buffer.remove();
                    } else {
                        buffer.get();
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
        ActorSystem system = new ActorSystem(new DefaultDispatcher());
        BufferActor bufferActor = new BufferActor();
        ActorRef bufferRef = system.register("buffer", bufferActor);
        
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            final int threadId = i;
            Thread thread = new Thread(() -> {
                int operationsPerThread = operationCount / threadCount;
                for (int j = 0; j < operationsPerThread; j++) {
                    if (j % 3 == 0) {
                        bufferRef.tell("add", null);
                    } else if (j % 3 == 1) {
                        bufferRef.tell("remove", null);
                    } else {
                        bufferRef.tell("get", null);
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
        
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        return System.currentTimeMillis() - startTime;
    }
    
    // 写操作测试
    private static long runSynchronizedWriteTest(int operationCount, int threadCount) {
        SynchronizedBuffer buffer = new SynchronizedBuffer();
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(() -> {
                int operationsPerThread = operationCount / threadCount;
                for (int j = 0; j < operationsPerThread; j++) {
                    buffer.add();
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
    
    private static long runActorWriteTest(int operationCount, int threadCount) {
        ActorSystem system = new ActorSystem(new DefaultDispatcher());
        BufferActor bufferActor = new BufferActor();
        ActorRef bufferRef = system.register("buffer", bufferActor);
        
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(() -> {
                int operationsPerThread = operationCount / threadCount;
                for (int j = 0; j < operationsPerThread; j++) {
                    bufferRef.tell("add", null);
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
        
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        return System.currentTimeMillis() - startTime;
    }
    
    // 读操作测试
    private static long runSynchronizedReadTest(int operationCount, int threadCount) {
        SynchronizedBuffer buffer = new SynchronizedBuffer();
        // 先添加一些数据
        for (int i = 0; i < 1000; i++) {
            buffer.add();
        }
        
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(() -> {
                int operationsPerThread = operationCount / threadCount;
                for (int j = 0; j < operationsPerThread; j++) {
                    buffer.get();
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
    
    private static long runActorReadTest(int operationCount, int threadCount) {
        ActorSystem system = new ActorSystem(new DefaultDispatcher());
        BufferActor bufferActor = new BufferActor();
        ActorRef bufferRef = system.register("buffer", bufferActor);
        
        // 先添加一些数据
        for (int i = 0; i < 1000; i++) {
            bufferRef.tell("add", null);
        }
        
        CountDownLatch latch = new CountDownLatch(threadCount);
        
        long startTime = System.currentTimeMillis();
        
        for (int i = 0; i < threadCount; i++) {
            Thread thread = new Thread(() -> {
                int operationsPerThread = operationCount / threadCount;
                for (int j = 0; j < operationsPerThread; j++) {
                    bufferRef.tell("get", null);
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
        
        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        
        return System.currentTimeMillis() - startTime;
    }
    
    // 复杂消息类
    static class ComplexMessage {
        private String type;
        private int value;
        
        public ComplexMessage(String type, int value) {
            this.type = type;
            this.value = value;
        }
        
        public String getType() { return type; }
        public int getValue() { return value; }
    }
    
    // 复杂Buffer实现
    static class SynchronizedComplexBuffer {
        private int num = 1;
        
        public synchronized void add(ComplexMessage msg) {
            num++;
            // 模拟复杂处理
            for (int i = 0; i < 10; i++) {
                Math.sqrt(msg.getValue());
            }
        }
        
        public synchronized void remove(ComplexMessage msg) {
            if (num > 0) {
                num--;
                // 模拟复杂处理
                for (int i = 0; i < 10; i++) {
                    Math.sqrt(msg.getValue());
                }
            }
        }
    }
    
    static class ComplexBufferActor extends Actor {
        private int num = 1;

        @Override
        public void receive(Message message) {
            ComplexMessage msg = (ComplexMessage) message.payload;
            if ("add".equals(msg.getType())) {
                num++;
                // 模拟复杂处理
                for (int i = 0; i < 10; i++) {
                    Math.sqrt(msg.getValue());
                }
            } else if ("remove".equals(msg.getType())) {
                if (num > 0) {
                    num--;
                    // 模拟复杂处理
                    for (int i = 0; i < 10; i++) {
                        Math.sqrt(msg.getValue());
                    }
                }
            }
        }
    }
    
    // 基础Buffer实现
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