# 📚 Logging Library — Machine Coding Round

A fully functional, thread-safe, in-memory logging library built in Java.  
Supports sync/async logging, multiple sinks, level-based filtering, and concurrent writes.

---

## 📖 Reading Order (Recommended)

Follow this order to build understanding **bottom-up** — from simple data models to the full system.  
Each step builds on the previous one.

---

### Step 1️⃣ — `model/LogLevel.java` *(Start Here)*

> **What**: Enum defining the 5 log severity levels.  
> **Why first**: This is the simplest file and the foundation everything else depends on.

**Key concepts to understand:**
- Each level has an integer `priority` (DEBUG=0, INFO=1, WARN=2, ERROR=3, FATAL=4)
- Filtering works by comparing priorities: `message.level.priority >= threshold.level.priority`
- This simple integer comparison drives ALL filtering logic in the entire library

```
Priority chain: DEBUG(0) < INFO(1) < WARN(2) < ERROR(3) < FATAL(4)
```

---

### Step 2️⃣ — `model/LogMessage.java`

> **What**: Immutable value object representing a single log entry.  
> **Why second**: It uses `LogLevel` and is used by everything else.

**Key concepts to understand:**
- **Immutable Object pattern** — all fields are `final`, no setters
- Why immutability matters: the same `LogMessage` object is passed between threads in async logging — immutability makes it inherently thread-safe without any synchronization
- The `timestamp` is set at **creation time** (when `log()` is called), NOT when the message reaches the sink — this is important for async loggers where there's a delay
- `toString()` produces the output format: `"timestamp [LEVEL] content"`

---

### Step 3️⃣ — `sink/Sink.java` *(Interface)*

> **What**: Strategy interface defining the contract for all output destinations.  
> **Why third**: Understand the abstraction before seeing the implementation.

**Key concepts to understand:**
- **Strategy Pattern** — different sink implementations (StdOut, File, DB) are interchangeable strategies
- Each sink has its own `LogLevel` threshold — this is the **second gate** of filtering
- The Logger doesn't know or care what type of sink it's writing to — it just calls `write()`
- To add a new sink type (e.g., FileSink), you just implement this interface — **zero changes** to Logger code

---

### Step 4️⃣ — `sink/StdOutSink.java`

> **What**: Concrete sink that prints log messages to `System.out`.  
> **Why fourth**: See how the Strategy interface is implemented.

**Key concepts to understand:**
- **Sink-level filtering**: checks `message.level.priority >= this.level.priority` before writing
- `System.out.println()` is internally synchronized — safe for concurrent calls
- This is deliberately simple — in a real system, you'd have `FileSink`, `DatabaseSink`, etc. with the same interface

---

### Step 5️⃣ — `config/LoggerConfig.java`

> **What**: Configuration object built using the Builder pattern.  
> **Why fifth**: Understand how the logger is configured before seeing the logger itself.

**Key concepts to understand:**
- **Builder Pattern** — solves the "telescoping constructor" problem (too many optional params)
- The `Builder` is a nested static class with fluent setter methods returning `this`
- `build()` performs **validation** (at least one sink, buffer size > 0 for async, etc.)
- The built config is **immutable** — `sinks` list is wrapped in `Collections.unmodifiableList()`
- `LoggerType` enum (SYNC/ASYNC) is defined here as a nested enum

**Builder usage pattern:**
```java
LoggerConfig config = new LoggerConfig.Builder("my-logger")
    .logLevel(LogLevel.INFO)
    .loggerType(LoggerType.ASYNC)
    .bufferSize(25)
    .addSink(new StdOutSink(LogLevel.INFO))
    .build();
```

---

### Step 6️⃣ — `logger/Logger.java` *(Interface)*

> **What**: The public API that client code interacts with.  
> **Why sixth**: Understand the contract before seeing implementations.

**Key concepts to understand:**
- **Dependency Inversion Principle** — clients program to this interface, not to `SyncLogger`/`AsyncLogger`
- Convenience methods: `info()`, `debug()`, `warn()`, `error()`, `fatal()`
- Core method: `log(LogLevel, String)` — all convenience methods delegate here
- `close()` is critical for async loggers — flushes buffered messages

---

### Step 7️⃣ — `logger/AbstractLogger.java` ⭐ *(Most Important File)*

> **What**: Base class implementing the common logging algorithm.  
> **Why seventh**: This is the heart of the library — the Template Method pattern.

**Key concepts to understand:**
- **Template Method Pattern** — defines the skeleton algorithm in `log()`:
  1. Check if logger is closed → `IllegalStateException`
  2. Validate message (null/empty) → silently skip
  3. **Logger-level filtering** (Gate 1) → skip if `level < config.logLevel`
  4. Enrich with timestamp → create immutable `LogMessage`
  5. Call `publish(message)` → **abstract hook** overridden by subclasses
- Steps 1-4 are **shared** across all logger types (DRY principle)
- Step 5 is the **variable part** — sync writes immediately, async enqueues
- `writeToSinks()` utility method fans out to all sinks with try-catch per sink (fail gracefully)
- `volatile boolean isClosed` — ensures cross-thread visibility

**Two-gate filtering architecture:**
```
log() called
    │
    ▼
[Gate 1: Logger Level] ── message.level < config.level? ──► DISCARD (no object created)
    │
    ▼
  Create LogMessage (with timestamp)
    │
    ▼
  publish() → writeToSinks() → for each sink:
    │
    ▼
[Gate 2: Sink Level] ── message.level < sink.level? ──► DISCARD (per-sink)
    │
    ▼
  WRITE to destination
```

---

### Step 8️⃣ — `logger/SyncLogger.java`

> **What**: Synchronous logger — writes immediately on the caller's thread.  
> **Why eighth**: Simpler of the two implementations; understand this before async.

**Key concepts to understand:**
- `publish()` is `synchronized` — ensures mutual exclusion and ordering
- The caller's thread is **blocked** during sink I/O
- `close()` is a no-op — nothing to flush since writes are immediate
- Trade-off: simple & predictable, but can be a bottleneck under high concurrency

---

### Step 9️⃣ — `logger/AsyncLogger.java` ⭐ *(Second Most Important File)*

> **What**: Asynchronous logger using Producer-Consumer pattern with a bounded queue.  
> **Why ninth**: The most complex file — builds on everything you've read so far.

**Key concepts to understand:**

**Architecture:**
```
[Thread 1] ──log()──┐
[Thread 2] ──log()──┤──► [LinkedBlockingQueue] ──► [Consumer Thread] ──► [Sinks]
[Thread 3] ──log()──┘     (bounded buffer)          (single thread)
```

**Critical design decisions:**

| Decision | Choice | Why |
|---|---|---|
| Queue type | `LinkedBlockingQueue` | Thread-safe, bounded, FIFO, separate put/take locks |
| Enqueue method | `put()` (not `offer()`) | `put()` blocks when full → **no data loss**. `offer()` would silently drop messages |
| Consumer threads | **Single** thread | Guarantees strict FIFO ordering to sinks |
| Thread type | Daemon thread | Won't prevent JVM shutdown |
| Shutdown | Poison pill style | `close()` → set flag → interrupt → drain → join |

**Graceful shutdown sequence (`close()`):**
1. `isClosed = true` → prevents new messages
2. `consumerThread.interrupt()` → wakes from blocking `take()`
3. `drainRemainingMessages()` → flushes anything left in queue
4. `consumerThread.join(5000)` → waits for completion (with timeout)

**Interview question you might get:** *"What happens if the buffer is full?"*  
Answer: `put()` blocks the caller thread (backpressure). This is intentional — it slows down producers rather than losing messages. If you wanted non-blocking behavior with potential loss, you'd use `offer()`.

---

### Step 🔟 — `factory/LoggerFactory.java`

> **What**: Factory that creates the right Logger type based on config.  
> **Why tenth**: Simple glue code — ties config to implementation.

**Key concepts to understand:**
- **Factory Pattern** — client says "give me a logger" without knowing which concrete class
- Switch on `LoggerType` → returns `SyncLogger` or `AsyncLogger`
- Adding a new logger type (e.g., `BatchLogger`) only requires adding a case here

---

### Step 1️⃣1️⃣ — `LoggingLibraryDriver.java` *(Read Last)*

> **What**: Driver class with 6 comprehensive test cases.  
> **Why last**: Now you understand the full system — see it all working together.

**Test cases and what they validate:**

| # | Test | What to Look For |
|---|---|---|
| 1 | **Sync Logging** | DEBUG is filtered out (logger level = INFO). 4 of 5 messages printed. |
| 2 | **Async Logging** | Messages appear in order. `close()` flushes all. DEBUG filtered. |
| 3 | **Concurrent Logging** | 5 threads × 10 msgs = 50 total. All 50 appear. Per-thread ordering preserved. |
| 4 | **Sink-Level Filtering** | Logger level=DEBUG, Sink level=ERROR. Only ERROR+FATAL appear (2 of 5). |
| 5 | **Multiple Sinks** | 2 sinks at different levels. ERROR/FATAL appear twice. Others once. |
| 6 | **Edge Cases** | null, empty, blank msgs skipped. Log-after-close throws. Invalid configs caught. |

---

## 🏗️ Design Patterns Summary

| Pattern | Where | One-Line Explanation |
|---|---|---|
| **Strategy** | `Sink` interface + `StdOutSink` | Swap output destinations without changing logger code |
| **Builder** | `LoggerConfig.Builder` | Clean construction of complex config with validation |
| **Template Method** | `AbstractLogger.log()` → `publish()` | Common algorithm skeleton; subclasses override only the dispatch step |
| **Factory** | `LoggerFactory.createLogger()` | Encapsulate which concrete Logger to instantiate |
| **Producer-Consumer** | `AsyncLogger` (queue + consumer thread) | Decouple producers (app threads) from consumer (sink writer) |
| **Immutable Object** | `LogMessage` | Thread-safe data transfer without synchronization |

---

## 🧵 Thread Safety Cheat Sheet

| Component | How It's Thread-Safe |
|---|---|
| `LogMessage` | Immutable — all fields `final`, no setters |
| `SyncLogger.publish()` | `synchronized` method — mutual exclusion |
| `AsyncLogger` queue | `LinkedBlockingQueue` — internally thread-safe |
| `isClosed` flag | `volatile` — ensures cross-thread visibility |
| `StdOutSink` | `System.out.println()` is internally synchronized |
| `LoggerConfig` | Immutable after construction (unmodifiable list) |

---

## ▶️ How to Compile & Run

```bash
# From the DSA project root:
javac -d out src/MC/LoggingLibrary/model/LogLevel.java \
             src/MC/LoggingLibrary/model/LogMessage.java \
             src/MC/LoggingLibrary/sink/Sink.java \
             src/MC/LoggingLibrary/sink/StdOutSink.java \
             src/MC/LoggingLibrary/config/LoggerConfig.java \
             src/MC/LoggingLibrary/logger/Logger.java \
             src/MC/LoggingLibrary/logger/AbstractLogger.java \
             src/MC/LoggingLibrary/logger/SyncLogger.java \
             src/MC/LoggingLibrary/logger/AsyncLogger.java \
             src/MC/LoggingLibrary/factory/LoggerFactory.java \
             src/MC/LoggingLibrary/LoggingLibraryDriver.java

java -cp out MC.LoggingLibrary.LoggingLibraryDriver
```

---

## 🔮 Extensibility — How to Add New Features

| Feature | What to Do |
|---|---|
| **New Sink (File)** | Create `FileSink implements Sink` → pass into config. Zero logger changes. |
| **New Logger Type (Batch)** | Extend `AbstractLogger` → implement `publish()` → add case in `LoggerFactory`. |
| **New Log Level** | Add to `LogLevel` enum with appropriate priority. Everything else auto-works. |
| **Log formatting** | Modify `LogMessage.toString()` or add a `Formatter` strategy interface. |
| **Multiple loggers** | Create a `LoggerRegistry` (Map<String, Logger>) for named logger lookup. |

---

## 🎯 Common Interview Questions & Answers

**Q: Why `put()` instead of `offer()` in AsyncLogger?**  
A: `put()` blocks when the queue is full (backpressure), guaranteeing no data loss. `offer()` returns false and the message is silently dropped.

**Q: How do you guarantee message ordering?**  
A: Single consumer thread + FIFO queue. Messages are dequeued and written in exactly the order they were enqueued.

**Q: What happens if a sink throws an exception?**  
A: `writeToSinks()` wraps each sink call in try-catch. One sink's failure doesn't affect others or crash the logger.

**Q: Why two levels of filtering (logger + sink)?**  
A: Logger-level filtering is an optimization — it avoids creating `LogMessage` objects for messages that will definitely be discarded. Sink-level filtering allows different destinations to capture different severities independently.

**Q: How does graceful shutdown work?**  
A: `close()` sets `isClosed=true` → interrupts consumer → drains remaining queue → joins thread. After `close()` returns, all messages are guaranteed written.

**Q: Why is LogMessage immutable?**  
A: It's passed between threads (producer → queue → consumer). Immutability makes it inherently thread-safe without any synchronization overhead.
