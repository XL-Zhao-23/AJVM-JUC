package org.example.memory;// 文件：ThreadAllocator.java
import java.util.*;

public class ThreadAllocator extends Thread {
  private final HeapMemory heap;
  private final List<ObjectInstance> localRefs = new ArrayList<>();
  private final Random random = new Random();

  public ThreadAllocator(HeapMemory heap) {
    this.heap = heap;
  }

  @Override
  public void run() {
    for (int i = 0; i < 1000; i++) {
      ObjectInstance obj = heap.allocateInEden(16);
      if (obj == null) {
        heap.performGC();
        obj = heap.allocateInEden(16);
        if (obj == null) {
          System.out.println(getName() + " - Allocation failed after GC.");
          break;
        }
      }

      localRefs.add(obj);
      heap.addRoot(obj);

      // 模拟随机建立引用，增强引用图连通性
      if (!localRefs.isEmpty() && random.nextBoolean()) {
        ObjectInstance refTo = localRefs.get(random.nextInt(localRefs.size()));
        obj.addReference(refTo);
      }

      // 模拟断开引用，移除根集合中的对象
      if (i % 100 == 0 && !localRefs.isEmpty()) {
        ObjectInstance lost = localRefs.remove(0);
        heap.removeRoot(lost);
        System.out.println(getName() + " lost reference to " + lost);
      }
    }
  }
}
