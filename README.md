# AJVM-JUC 项目

这是一个基于Java实现的JVM核心组件项目，主要包含Actor模型和内存管理两个核心模块。

## 项目结构

```
AJVM-JUC/
├── src/main/java/org/example/
│   ├── actor/           # Actor模型实现
│   │   ├── test1/      # 基础Actor实现
│   │   ├── test2/      # 改进版Actor实现
│   │   ├── test3/      # 完整Actor系统实现
│   │   └── test4/      # 崩溃恢复Actor实现
│   │   
│   └── memory/         # 内存管理实现
│       ├── HeapMemory.java      # 堆内存管理
│       ├── ThreadAllocator.java # 线程分配器
│       ├── ObjectInstance.java  # 对象实例
│       └── Main.java           # 主程序入口
```

## 核心组件介绍

### 1. Actor模型组件 (`org.example.actor`)

Actor模型是一种并发编程模型，通过消息传递实现线程间通信，避免共享状态带来的并发问题。

#### 主要特性：

- **消息驱动**: 使用`MessageType`枚举定义消息类型（Product/Consumer）
- **异步通信**: Actor之间通过消息队列进行异步通信
- **状态隔离**: 每个Actor维护独立的状态，避免共享状态
- **线程安全**: 基于`LinkedBlockingQueue`实现线程安全的消息传递

#### 实现版本：

1. **test1**: 基础Actor实现
   - 简单的消息队列和Runnable行为数组
   - 支持发送和处理消息

2. **test2**: 改进版Actor实现
   - 增强的消息处理机制

3. **test3**: 完整Actor系统
   - `ActorSystem`: 统一管理Actor
   - `ActorRef`: Actor引用封装
   - `Dispatcher`: 消息分发器
   - `BufferActor`: 缓冲区Actor

4. **test4**: 崩溃恢复Actor
   - `CrashActor`: 支持崩溃恢复的Actor实现

#### 核心类说明：

- `Actor`: 基础Actor类，继承Thread，包含消息队列和行为数组
- `Message`: 消息封装类，包含发送者、接收者和消息类型
- `ActorSystem`: Actor系统管理器，负责注册和管理Actor
- `Dispatcher`: 消息分发器，处理消息路由

### 2. 内存管理组件 (`org.example.memory`)

实现了JVM堆内存管理的核心功能，包括内存分配、垃圾回收等。

#### 主要特性：

- **分代内存模型**: 实现Eden区和Old区的分代管理
- **TLAB分配**: 线程本地分配缓冲区，提高分配效率
- **标记-清除GC**: 实现标记可达对象和清除不可达对象的垃圾回收算法
- **对象晋升**: 支持对象从Eden区晋升到Old区
- **根集合管理**: 维护根对象集合，支持可达性分析

#### 核心类说明：

- `HeapMemory`: 堆内存管理器
  - 管理Eden区和Old区内存
  - 实现TLAB分配策略
  - 执行标记-清除垃圾回收
  - 支持对象晋升机制

- `ThreadAllocator`: 线程分配器
  - 模拟多线程并发分配内存
  - 管理本地引用集合
  - 模拟引用建立和断开

- `ObjectInstance`: 对象实例
  - 封装对象的基本信息（地址、大小、年龄）
  - 维护对象引用关系
  - 支持可达性标记

#### 内存管理流程：

1. **内存分配**: 线程优先从TLAB分配，空间不足时触发GC
2. **可达性分析**: 从根集合开始标记所有可达对象
3. **垃圾回收**: 清理不可达对象，存活对象年龄+1
4. **对象晋升**: 年龄达到阈值的对象晋升到Old区

## TODOLIST

### 当前进度

- ✅ Actor模型基础实现
- ✅ 内存管理基础实现
- ✅ 解释器、类文件加载实现（未同步）

### 下一步计划

#### 1. 整合类文件加载器，解释器
#### 2. 设计测试用例，打通三大模块（指令执行、内存管理、并发控制）

### 长期目标

- [ ] 完整的JVM实现
- [ ] 性能优化
- [ ] 文档完善
- [ ] 社区贡献

## 贡献指南

欢迎提交Issue和Pull Request来改进项目。

## 许可证

本项目采用MIT许可证。 