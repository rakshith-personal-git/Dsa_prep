# Low-Level Design — Logging Library

## Class Diagram (text representation)

```
LoggingLibrary
    └── uses ──► LoggerFactory
                    └── creates/caches ──► Logger (interface)
                                              ├── SyncLogger
                                              └── AsyncLogger
                                                    both extend AbstractLogger
                                                        └── dispatches to ──► Sink (interface)
                                                                                  ├── StdoutSink
                                                                                  └── FileSink

LoggerConfig (built via Builder)
    ├── LogLevel
    ├── LoggerType
    └── List<Sink>

LogMessage
    ├── content : String
    ├── level   : LogLevel
    └── timestamp : LocalDateTime
```

## Key Interfaces

| Interface | Methods | Purpose |
|---|---|---|
| `Logger` | log, debug/info/warn/error/fatal, shutdown, getName | Client-facing contract |
| `Sink` | write, getSinkLevel, getDestination | Output destination contract |

## Design Patterns Used

| Pattern | Where | Why |
|---|---|---|
| Builder | LoggerConfig | Optional fields, readable construction, validation at build() |
| Factory + Registry | LoggerFactory | Singleton-per-name, centralised creation |
| Façade | LoggingLibrary | Single entry point, hides internal complexity |
| Template Method | AbstractLogger | Common steps (format, dispatch) in base; log() varies |
| Producer-Consumer | AsyncLogger | Decouples write rate from sink I/O speed |
| Strategy | Sink interface | Swap sink implementations without touching logger |

## Concurrency Guarantees

| Guarantee | Mechanism |
|---|---|
| Thread-safe enqueue | BlockingQueue (intrinsic) |
| FIFO ordering | Single consumer thread |
| No data loss | Bounded offer with timeout + graceful drain on shutdown |
| Sync thread safety | synchronized on SyncLogger.log() |
| File sink safety | synchronized on FileSink.write() |
