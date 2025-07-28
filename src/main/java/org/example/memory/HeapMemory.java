package org.example.memory;// 文件：HeapMemory.java
import java.util.*;

public class HeapMemory {
  private final byte[] eden;
  private final byte[] old;
  private final int TLAB_SIZE = 1024 * 8; // 8KB
  private final Map<Thread, Integer> tlabPointers = new HashMap<>();

  private int edenPointer = 0;
  private int oldPointer = 0;

  private final List<ObjectInstance> edenObjects = new ArrayList<>();
  private final List<ObjectInstance> oldObjects = new ArrayList<>();
  private final int PROMOTION_AGE = 3;

  // 根集合
  private final List<ObjectInstance> rootSet = new ArrayList<>();

  public HeapMemory(int edenSize, int oldSize) {
    this.eden = new byte[edenSize];
    this.old = new byte[oldSize];
  }

  // 分配TLAB空间
  private synchronized int allocateTLAB(Thread t, int size) {
    int base = tlabPointers.getOrDefault(t, edenPointer);
    if (base + size > eden.length) return -1;
    edenPointer = base + TLAB_SIZE;
    tlabPointers.put(t, edenPointer);
    return base;
  }

  // 线程优先从TLAB分配
  public synchronized ObjectInstance allocateInEden(int size) {
    Thread t = Thread.currentThread();
    int tlabBase = tlabPointers.getOrDefault(t, -1);
    if (tlabBase == -1 || tlabBase + size > eden.length) {
      int newBase = allocateTLAB(t, size);
      if (newBase == -1) return null;
      tlabBase = newBase;
    }
    ObjectInstance obj = new ObjectInstance(tlabBase, size);
    edenObjects.add(obj);
    tlabPointers.put(t, tlabBase + size);
    return obj;
  }

  private synchronized ObjectInstance promoteToOld(ObjectInstance obj) {
    if (oldPointer + obj.getSize() > old.length) return null;
    ObjectInstance newObj = new ObjectInstance(oldPointer, obj.getSize());
    oldObjects.add(newObj);
    oldPointer += obj.getSize();
    System.out.println("[GC] Promoted " + obj + " to Old@" + newObj.getAddress());
    return newObj;
  }

  public synchronized void addRoot(ObjectInstance obj) {
    if (!rootSet.contains(obj)) rootSet.add(obj);
  }

  public synchronized void removeRoot(ObjectInstance obj) {
    rootSet.remove(obj);
  }

  // 标记可达对象
  private void mark() {
    Deque<ObjectInstance> stack = new ArrayDeque<>();
    for (ObjectInstance root : rootSet) {
      if (!root.isReachable()) {
        root.markReachable();
        stack.push(root);
      }
    }
    while (!stack.isEmpty()) {
      ObjectInstance obj = stack.pop();
      for (ObjectInstance ref : obj.getReferences()) {
        if (!ref.isReachable()) {
          ref.markReachable();
          stack.push(ref);
        }
      }
    }
  }

  // 清理不可达对象，存活对象年龄+1，年龄到达阈值晋升到Old区
  private void sweep() {
    List<ObjectInstance> survivors = new ArrayList<>();
    edenPointer = 0;

    for (ObjectInstance obj : edenObjects) {
      if (obj.isReachable()) {
        obj.incrementAge();
        obj.clearReachable();

        if (obj.getAge() >= PROMOTION_AGE) {
          ObjectInstance promoted = promoteToOld(obj);
          if (promoted == null) {
            System.out.println("[GC] Promotion failed - Old space full.");
            survivors.add(obj);
            edenPointer += obj.getSize();
          }
        } else {
          ObjectInstance survivor = new ObjectInstance(edenPointer, obj.getSize());
          survivor.incrementAge();
          survivors.add(survivor);
          edenPointer += obj.getSize();
        }
      }
    }
    edenObjects.clear();
    edenObjects.addAll(survivors);
  }

  public synchronized void performGC() {
    System.out.println("[GC] Performing Mark-Sweep GC...");
    mark();
    sweep();
  }

  public List<ObjectInstance> getEdenObjects() {
    return edenObjects;
  }

  public void printHeapUsage() {
    System.out.println("\n[Heap Visualization - Eden]");
    int visualSize = 100;
    char[] heapVis = new char[visualSize];
    Arrays.fill(heapVis, '-');
    for (ObjectInstance obj : edenObjects) {
      int start = (int) (((double) obj.getAddress() / eden.length) * visualSize);
      int len = Math.max(1, (int) (((double) obj.getSize() / eden.length) * visualSize));
      for (int i = start; i < start + len && i < visualSize; i++) {
        heapVis[i] = '#';
      }
    }
    System.out.println(new String(heapVis));

    System.out.println("\n[Heap Visualization - Old]");
    char[] oldVis = new char[visualSize];
    Arrays.fill(oldVis, '-');
    for (ObjectInstance obj : oldObjects) {
      int start = (int) (((double) obj.getAddress() / old.length) * visualSize);
      int len = Math.max(1, (int) (((double) obj.getSize() / old.length) * visualSize));
      for (int i = start; i < start + len && i < visualSize; i++) {
        oldVis[i] = '#';
      }
    }
    System.out.println(new String(oldVis));
  }


  public List<ObjectInstance> getOldObjects() {
    return oldObjects;
  }

  public int getEdenSize() {
    return eden.length;
  }

  public int getOldSize() {
    return old.length;
  }
}
