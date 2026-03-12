# Logging Library — SDE-3 Machine Coding Round

A lightweight, production-quality logging library written in Java 17.
Supports synchronous and asynchronous logging, multiple sinks, per-level
filtering, thread-safe concurrent writes, and graceful shutdown.

---

## How to Read This Project (Recommended Order)

Follow this sequence to build a complete mental model of the library,
from the simplest building blocks up to the full working system.

---

### Step 1 — Understand the Data Model
**`core/LogLevel.java`**
Start here. Understand how log levels are ordered by priority and how
`isAtLeast()` drives all filtering decisions throughout the library.

**`core/LogMessage.java`**
A single log entry: content + level + timestamp. Timestamps are captured
at construction time — this is intentional (preserves ordering in async queues).

---

### Step 2 — Understand Configuration
**`core/LoggerType.java`**
Two words: SYNC and ASYNC. Understand what each means before going further.

**`core/LoggerConfig.java`**
The full configuration contract. Read the Builder carefully — this is how
clients wire everything together. Notice how `build()` enforces invariants.

---

### Step 3 — Understand the Sink Contract
**`sink/Sink.java`**
The interface that all output destinations implement. Read the Javadoc —
it explains the two-layer filtering model (logger-level + sink-level).

**`sink/StdoutSink.java`**
The simplest sink implementation. One method, one responsibility.

**`sink/FileSink.java`**
The file sink. Pay attention to the `synchronized write()` and the
`append=true` flag on the writer — both are intentional design choices.

---

### Step 4 — Understand the Logger Contract
**`logger/Logger.java`**
The interface all client code depends on. Note the `shutdown()` contract —
it must be safe to call on both sync and async loggers.

**`logger/AbstractLogger.java`**
The most important class to understand deeply. It contains:
- The two-layer level filtering logic
- Message formatting (timestamp pattern)
- Sink dispatch loop
- All convenience shorthand methods (debug/info/warn/error/fatal)

Everything else builds on top of this.

---

### Step 5 — Understand the Implementations
**`logger/SyncLogger.java`**
Read after AbstractLogger. Notice how thin it is — `synchronized log()`
plus a no-op `shutdown()`. All the real work is in the base class.

**`logger/AsyncLogger.java`**
The most complex class. Read it in this order:
1. Fields — understand `ArrayBlockingQueue` and `AtomicBoolean running`
2. Constructor — see how the worker thread is started
3. `log()` — the producer side; back-pressure via `offer()` with timeout
4. `processMessages()` — the consumer loop; why it checks both `running` and `!isEmpty()`
5. `shutdown()` — how graceful drain works via `join()`

---

### Step 6 — Understand the Wiring
**`factory/LoggerFactory.java`**
See how `ConcurrentHashMap.computeIfAbsent` gives you singleton-per-name
for free, thread-safely. This is where `LoggerType` selects the implementation.

**`LoggingLibrary.java`**
The public façade. This is the only class a consuming application should
import. Two methods: `initialize()` and `shutdownAll()`. That's the entire API.

---

### Step 7 — See It All in Action
**`driver/Main.java`**
Six demos, each isolated. Read them in order — each one isolates a
specific behaviour: sync filtering, async buffering, concurrent writes,
sink-level filtering, multiple sinks, and edge cases.

---

### Step 8 — Verify the Guarantees
**`test/LoggerTest.java`**
Seven tests that prove the correctness of every guarantee the library makes.
Read each test name, then read the assertion — together they form the
specification of the library's behaviour.

---

## Package Structure (Quick Reference)

```
src/
├── main/java/com/logging/
│   ├── LoggingLibrary.java         ← Public façade (Step 6)
│   ├── core/
│   │   ├── LogLevel.java           ← Step 1
│   │   ├── LogMessage.java         ← Step 1
│   │   ├── LoggerConfig.java       ← Step 2
│   │   └── LoggerType.java         ← Step 2
│   ├── sink/
│   │   ├── Sink.java               ← Step 3
│   │   ├── StdoutSink.java         ← Step 3
│   │   └── FileSink.java           ← Step 3
│   ├── logger/
│   │   ├── Logger.java             ← Step 4
│   │   ├── AbstractLogger.java     ← Step 4 (read carefully)
│   │   ├── SyncLogger.java         ← Step 5
│   │   └── AsyncLogger.java        ← Step 5 (most complex)
│   ├── factory/
│   │   └── LoggerFactory.java      ← Step 6
│   └── driver/
│       └── Main.java               ← Step 7
└── test/java/com/logging/
    └── LoggerTest.java             ← Step 8
```

---

## How to Build & Run

### Option A — Plain javac (no Maven)

```bash
mkdir -p out
find src/main -name "*.java" | xargs javac -d out/
find src/test -name "*.java" | xargs javac -cp out/ -d out/

# Run demo
java -cp out/ com.logging.driver.Main

# Run tests
java -cp out/ com.logging.LoggerTest
```

### Option B — Maven

```bash
mvn compile exec:java -Dexec.mainClass="com.logging.driver.Main"
mvn compile exec:java -Dexec.mainClass="com.logging.LoggerTest"
```

---

## Expected Output

### Demo Driver (`Main.java`)

```
--- Demo 1: Sync Logging (logger=INFO, sink=INFO) ---
11-03-2026-22-10-00 [INFO]  Info message
11-03-2026-22-10-00 [WARN]  Warn message
11-03-2026-22-10-00 [ERROR] Error message
11-03-2026-22-10-00 [FATAL] Fatal message

--- Demo 4: Sink-Level Filtering (logger=DEBUG, sink=ERROR) ---
11-03-2026-22-10-00 [ERROR] Error — passes sink filter
11-03-2026-22-10-00 [FATAL] Fatal — passes sink filter
```

### Test Suite (`LoggerTest.java`)

```
[PASS] Sync filters levels below config
[PASS] Sync allows levels at/above config
[PASS] Sink level filters independently
[PASS] Null message ignored without exception
[PASS] Blank message ignored
[PASS] Async delivers all messages before shutdown
[PASS] Concurrent logging no data loss [expected=200, got=200]

============================
Passed: 7  |  Failed: 0
============================
```

---

## Extending the Library

- **New sink type** (e.g. `DatabaseSink`): Implement `Sink` → attach via `LoggerConfig.Builder.addSink()`. Zero changes elsewhere.
- **New logger type** (e.g. `BatchLogger`): Extend `AbstractLogger` → implement `log()` + `shutdown()` → add enum value to `LoggerType` → add case in `LoggerFactory`.
- **Custom timestamp format**: Set `.timestampFormat("yyyy/MM/dd HH:mm:ss.SSS")` in the builder. Any valid `DateTimeFormatter` pattern works.
