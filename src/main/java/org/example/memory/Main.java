package org.example.memory;

// 文件：Main.java
public class Main {
  public static void main(String[] args) throws InterruptedException {
    HeapMemory heap = new HeapMemory(1024 * 1024, 1024 * 1024);

    ThreadAllocator[] threads = new ThreadAllocator[4];
    for (int i = 0; i < threads.length; i++) {
      threads[i] = new ThreadAllocator(heap);
      threads[i].setName("Thread-" + i);
      threads[i].start();
    }

    for (ThreadAllocator thread : threads) {
      thread.join();
    }

    System.out.println("[Main] Eden objects remaining after GC: " + heap.getEdenObjects().size());
    heap.printHeapUsage();
  }
}
