package org.example.memory;// 文件：ObjectInstance.java
import java.util.*;

public class ObjectInstance {
    private final int address;
    private final int size;
    private boolean reachable = false;  // GC 标记位
    private int age = 0;

    private final List<ObjectInstance> references = new ArrayList<>();

    public ObjectInstance(int address, int size) {
        this.address = address;
        this.size = size;
    }

    public int getSize() { return size; }
    public int getAddress() { return address; }

    public boolean isReachable() { return reachable; }
    public void markReachable() { this.reachable = true; }
    public void clearReachable() { this.reachable = false; }

    public int getAge() { return age; }
    public void incrementAge() { this.age++; }

    public void addReference(ObjectInstance obj) {
        if (!references.contains(obj)) {
            references.add(obj);
        }
    }
    public void removeReference(ObjectInstance obj) {
        references.remove(obj);
    }
    public List<ObjectInstance> getReferences() {
        return references;
    }

    @Override
    public String toString() {
        return "Object@" + address + "(size=" + size + ", age=" + age + ")";
    }
}
