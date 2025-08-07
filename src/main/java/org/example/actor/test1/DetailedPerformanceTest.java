package org.example.actor.test1;

import org.example.actor.MessageType;
import org.openjdk.jmh.annotations.*;

import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

@BenchmarkMode(Mode.Throughput)
@OutputTimeUnit(TimeUnit.SECONDS)
@State(Scope.Thread)
@Warmup(iterations = 3, time = 3)
@Measurement(iterations = 5, time = 5)
@Fork(1)
public class DetailedPerformanceTest {
    private SynchronizedBuffer buffer1;
    private ActorBuffer buffer2;
    private Message comsumerMessage;
    private Message produceMessage;

    @Setup(Level.Iteration)
    public void setup() throws Exception {
        buffer1 = new SynchronizedBuffer();
        comsumerMessage = new Message();
        comsumerMessage.setType(MessageType.Consumer.getMessageId());
        produceMessage = new Message();
        produceMessage.setType(MessageType.Product.getMessageId());
        buffer2 = new ActorBuffer();
        buffer2.start();
    }


    @Benchmark
    @Threads(1)
    public void test_1_thread() {
        if (ThreadLocalRandom.current().nextBoolean()) {
            buffer1.produce();
        } else {
            buffer1.consume();
        }
    }

    @Benchmark
    @Threads(2)
    public void test_2_Sync() {
        if (ThreadLocalRandom.current().nextBoolean()) {
            buffer1.produce();
        } else {
            buffer1.consume();
        }
    }

    @Benchmark
    @Threads(4)
    public void test_4_Sync() {
        if (ThreadLocalRandom.current().nextBoolean()) {
            buffer1.produce();
        } else {
            buffer1.consume();
        }
    }

    @Benchmark
    @Threads(8)
    public void test_8_Sync() {
        if (ThreadLocalRandom.current().nextBoolean()) {
            buffer1.produce();
        } else {
            buffer1.consume();
        }
    }
    @Benchmark
    @Threads(16)
    public void test_16_Sync() {
        if (ThreadLocalRandom.current().nextBoolean()) {
            buffer1.produce();
        } else {
            buffer1.consume();
        }
    }
    @Benchmark
    @Threads(32)
    public void test_32_Sync() {
        if (ThreadLocalRandom.current().nextBoolean()) {
            buffer1.produce();
        } else {
            buffer1.consume();
        }
    }

    @Benchmark
    @Threads(1)
    public void test_1_Actor() {
        if (ThreadLocalRandom.current().nextBoolean()) {
            buffer2.send(produceMessage);
        } else {
            buffer2.send(comsumerMessage);
        }
    }

    @Benchmark
    @Threads(2)
    public void test_2_Actor() {
        if (ThreadLocalRandom.current().nextBoolean()) {
            buffer2.send(produceMessage);
        } else {
            buffer2.send(comsumerMessage);
        }
    }

    @Benchmark
    @Threads(4)
    public void test_4_Actor() {
        if (ThreadLocalRandom.current().nextBoolean()) {
            buffer2.send(produceMessage);
        } else {
            buffer2.send(comsumerMessage);
        }
    }

    @Benchmark
    @Threads(8)
    public void test_8_Actor() {
        if (ThreadLocalRandom.current().nextBoolean()) {
            buffer2.send(produceMessage);
        } else {
            buffer2.send(comsumerMessage);
        }
    }
    @Benchmark
    @Threads(16)
    public void test_16_Actor() {
        if (ThreadLocalRandom.current().nextBoolean()) {
            buffer2.send(produceMessage);
        } else {
            buffer2.send(comsumerMessage);
        }
    }
    @Benchmark
    @Threads(32)
    public void test_32_Actor() {
        if (ThreadLocalRandom.current().nextBoolean()) {
            buffer2.send(produceMessage);
        } else {
            buffer2.send(comsumerMessage);
        }
    }
}
