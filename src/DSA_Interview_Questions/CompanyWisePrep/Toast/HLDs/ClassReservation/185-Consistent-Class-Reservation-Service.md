# 185. Design a Consistent Class Reservation Service
### SDE-3 / SSE level answer — end to end, with interview narration and cross-questions

> **Round format:** 1 hour, HLD. Tags: System Design, Scalability, Consistency, Booking System, Distributed Systems.
> **Problem:** Users reserve seats for classes (fitness, educational). A class must *never* be over-booked. State must stay valid under high concurrency.
> **Constraints given:** capacity 5–200 seats/class · peak writes "thousands of RPS" · reservation latency < 200 ms · multi-datacenter · durable.

---

## 0. The one-sentence thesis (say this early, it wins the round)

> **"This is not a storage-scale problem — the data is tiny. It's a *contention* problem on a single hot key: one class's seat counter. So my design spends its budget on (a) making the decrement atomic and authoritative in exactly one place, and (b) keeping the stampede of *failed* attempts away from that place."**

Everything else in the design hangs off that sentence. Interviewers at this level are scoring whether you can *identify the real constraint* rather than reciting a cache-CDN-shard checklist.

---

## 1. Requirement clarification (first 5 minutes — do NOT skip)

Ask these out loud. Then state the assumption you'll proceed with, so you don't stall waiting for answers.

| # | Question I'd ask | Assumption I'll proceed with | Why it matters |
|---|---|---|---|
| 1 | Is a seat generic, or does the user pick a *specific* seat (bike #12, desk A4)? | **Generic, fungible seat** (count-based). I'll note the seat-map variant. | Count-based → one counter. Seat-map → per-seat row + optimistic lock. Totally different core. |
| 2 | Is there a payment step? | **Yes, optional** — so I need a *hold* (temporary reservation) with TTL, not just a confirm. | Adds the HELD state + expiry reclaim, the most commonly missed piece. |
| 3 | Can one user book the same class twice? | **No** — one active reservation per (user, class). | Needs a second uniqueness invariant, separate from capacity. |
| 4 | Waitlist when full? | **Yes, v2.** Design for it, build it second. | Shows roadmap thinking. |
| 5 | Is "real-time availability" a hard consistency requirement, or best-effort display? | **Best-effort for browsing; strictly consistent at booking time.** | Critical. If you promise linearizable reads globally you've signed up for a system nobody builds. |
| 6 | Multi-DC = active-active writes, or active-passive with regional affinity? | **Regional write affinity + async cross-region replication.** A class is physically located somewhere. | Prevents the "multi-master counter" trap. |
| 7 | What's the actual read:write ratio? | **~20:1 reads to writes.** | Justifies splitting read/write paths. |
| 8 | Overbooking tolerance — truly zero, or can we over-sell and compensate (airline style)? | **Zero.** A fitness class physically has 20 bikes. | Zero-tolerance kills "eventually consistent counter" designs. Say this explicitly. |

**Narration tip:** after asking, say *"I'll assume X and Y; stop me if that's wrong."* Then move. Candidates lose 10 minutes here.

---

## 2. Requirements, restated crisply

### Functional
- **FR1** Browse/search classes with current availability (by time, location, category).
- **FR2** Create a reservation (optionally two-phase: hold → confirm).
- **FR3** Cancel a reservation; seat returns to the pool.
- **FR4** Update/move a reservation to a different class (= atomic cancel + book).
- **FR5** View my reservations.
- **FR6** *(v2)* Waitlist + auto-promotion on cancellation.

### Non-functional (these are the scored ones)
- **NFR1 — Safety invariant:** `active_reservations(class) ≤ capacity(class)` **always**. No exceptions, no windows.
- **NFR2 — Idempotency:** a retried/duplicated client request must never create a second reservation or consume a second seat.
- **NFR3 — Latency:** p99 < 200 ms for a booking write; p99 < 100 ms for availability read.
- **NFR4 — Availability:** 99.95% for reads. Writes may degrade (fail-closed) before they may over-book. **I will trade availability for correctness on the write path — this is a CP choice on the inventory key.**
- **NFR5 — Durability:** a confirmed reservation survives node/AZ loss. RPO = 0 in-region.
- **NFR6 — Elasticity:** enrollment-open spikes of 10–50× baseline for a few minutes.
- **NFR7 — Observability:** over-booking must be *detectable*, not just *prevented*.

### Explicitly out of scope (say this — it buys you time)
Payments internals, auth/identity, instructor scheduling CRUD, recommendations, notifications delivery guarantees beyond at-least-once.

---

## 3. Back-of-the-envelope (2 minutes, and make a point with it)

```
Assume a mid-size platform:
  100k classes/day                        (studios × slots)
  avg capacity 40, avg 70% fill           → ~2.8M reservations/day
  → ~32 writes/sec average
  → peak burst: 5,000 writes/sec          (given: "thousands of RPS")
  → reads at 20:1 peak                    ≈ 100,000 reads/sec

Storage:
  reservation row ≈ 250 bytes
  2.8M/day × 365 × 250B                   ≈ 255 GB/year
  class/inventory rows                    ≈ negligible (100k/day × 300B ≈ 11 GB/yr)
  → a single well-provisioned Postgres holds years of this.

Hot-key math (THE important number):
  A class has ≤ 200 seats.
  ⇒ at most 200 SUCCESSFUL writes ever touch one class's counter.
  ⇒ but at enrollment-open, 10,000 users may hit the SAME class in 2 seconds
     → ~5,000 QPS against ONE row, of which ≥ 9,800 must be rejected.
```

**The point to make:** *"Storage is a rounding error. 255 GB/year fits on one box. What will kill me is 5,000 QPS of mostly-doomed writes against a single row. So the design is: one authoritative serialization point per class, fronted by a cheap rejector."*

---

## 4. API contract

RESTful, versioned, idempotent writes. (Mention gRPC internally, REST at the edge.)

### 4.1 Browse / availability

```http
GET /v1/classes?city=BLR&category=fitness&from=2026-10-04T06:00Z&to=2026-10-04T12:00Z&cursor=<opaque>&limit=20
200 OK
Cache-Control: public, max-age=5
ETag: "W/cls-9f2a"
{
  "items": [
    { "classId":"cls_123", "title":"HIIT 45", "startsAt":"2026-10-04T06:00Z",
      "durationMin":45, "capacity":20, "available":3, "status":"OPEN",
      "availabilityAsOf":"2026-10-04T05:12:31.220Z" }
  ],
  "nextCursor":"eyJ0IjoiMjAy..."
}
```

```http
GET /v1/classes/{classId}/availability
200 OK   { "classId":"cls_123", "capacity":20, "available":3, "version":178,
           "asOf":"2026-10-04T05:12:31.220Z" }
```
> `available` is **advisory**. I return `availabilityAsOf` + `version` so clients can show staleness and so the UI can reconcile push updates. The binding decision happens only in the write path.

### 4.2 Create reservation (the core call)

```http
POST /v1/classes/{classId}/reservations
Authorization: Bearer <jwt>
Idempotency-Key: 8c1b...-client-generated-uuid     # REQUIRED
Content-Type: application/json

{ "userId":"usr_77", "seats":1, "mode":"CONFIRM" }   # mode: HOLD | CONFIRM
```

Responses:

| Code | Meaning | Body |
|---|---|---|
| `201 Created` | New reservation made | `{ reservationId, classId, userId, status:"CONFIRMED"\|"HELD", expiresAt?, seatsRemaining, version }` |
| `200 OK` | **Idempotent replay** — same key, same payload | identical body to the original `201` |
| `409 Conflict` `code=CLASS_FULL` | capacity exhausted | `{ error:{code:"CLASS_FULL", available:0, waitlistEligible:true} }` |
| `409 Conflict` `code=ALREADY_RESERVED` | user already holds a seat in this class | `{ error:{code:"ALREADY_RESERVED", reservationId:"..."} }` |
| `422` `code=IDEMPOTENCY_KEY_REUSED` | same key, **different** payload | — |
| `410 Gone` `code=BOOKING_CLOSED` | class started / cancelled | — |
| `429` | rate-limited / shed at the gate | `Retry-After: 2` |
| `503` `code=INVENTORY_UNAVAILABLE` | shard down — **fail closed** | `Retry-After` |

### 4.3 Cancel / move

```http
DELETE /v1/reservations/{reservationId}
Idempotency-Key: <uuid>
→ 200 { "reservationId":"...", "status":"CANCELLED", "refund":{...}, "seatsRemaining":4 }
→ 200 (replay, already cancelled — idempotent, NOT 404)
→ 409 code=CANCELLATION_WINDOW_CLOSED
```

```http
POST /v1/reservations/{reservationId}:move
Idempotency-Key: <uuid>
{ "toClassId":"cls_456" }
→ 200 { newReservationId, oldStatus:"CANCELLED", newStatus:"CONFIRMED" }
→ 409 code=TARGET_CLASS_FULL   # source reservation untouched
```
> **Say this:** *"Move is book-then-release, not release-then-book. If I release first and the target is full, I've destroyed the user's seat. Acquire the new seat, then free the old one; if the second step fails, a compensating job reconciles — but the user is never left with nothing."*

### 4.4 Hold → confirm (when payment is in the loop)

```http
POST /v1/classes/{id}/reservations   {"mode":"HOLD"}  → 201 {status:"HELD", expiresAt:+5m}
POST /v1/reservations/{rid}:confirm  {"paymentToken":"..."} → 200 {status:"CONFIRMED"}
# no confirm before expiresAt → seat auto-reclaimed
```

### 4.5 Waitlist (v2)
```http
POST   /v1/classes/{id}/waitlist      → 201 { position: 7 }
DELETE /v1/classes/{id}/waitlist      → 204
# promotion is pushed: user gets a 10-minute CLAIM window
```

### 4.6 Real-time availability stream
```
GET /v1/stream/classes?ids=cls_123,cls_456     (Server-Sent Events)
event: availability
data: {"classId":"cls_123","available":2,"version":179}
```

---

## 5. Data model

**Shard key everywhere: `class_id`.** Co-locating inventory + reservations in one shard is what lets me use a plain single-shard ACID transaction and completely avoid 2PC. State that reason out loud.

```sql
-- The hot row. One per class. This is the serialization point.
CREATE TABLE class_inventory (
  class_id        UUID PRIMARY KEY,
  capacity        INT  NOT NULL CHECK (capacity > 0),
  booked_count    INT  NOT NULL DEFAULT 0,        -- HELD + CONFIRMED
  version         BIGINT NOT NULL DEFAULT 0,      -- bumped per mutation, used by cache + clients
  booking_opens   TIMESTAMPTZ,
  booking_closes  TIMESTAMPTZ,
  state           TEXT NOT NULL DEFAULT 'OPEN',   -- OPEN|CLOSED|CANCELLED
  CONSTRAINT no_overbook CHECK (booked_count >= 0 AND booked_count <= capacity)  -- belt & braces
);

CREATE TABLE reservation (
  reservation_id  UUID PRIMARY KEY,
  class_id        UUID NOT NULL,
  user_id         UUID NOT NULL,
  seats           INT  NOT NULL DEFAULT 1,
  status          TEXT NOT NULL,                  -- HELD|CONFIRMED|CANCELLED|EXPIRED
  idempotency_key TEXT NOT NULL,
  expires_at      TIMESTAMPTZ,                    -- only for HELD
  created_at      TIMESTAMPTZ NOT NULL DEFAULT now(),
  updated_at      TIMESTAMPTZ NOT NULL DEFAULT now()
);

-- Invariant #2: one active reservation per user per class, enforced by the DB, not by app code.
CREATE UNIQUE INDEX uq_active_user_class
  ON reservation (class_id, user_id)
  WHERE status IN ('HELD','CONFIRMED');

-- Invariant #3: idempotency, enforced by the DB.
CREATE UNIQUE INDEX uq_idem ON reservation (user_id, idempotency_key);

-- Hold reclaim sweeper index
CREATE INDEX ix_expiring ON reservation (expires_at) WHERE status = 'HELD';

-- Read path: "my reservations"
CREATE INDEX ix_user ON reservation (user_id, created_at DESC);

-- Transactional outbox: events published in the SAME txn as the state change.
CREATE TABLE outbox (
  id BIGSERIAL PRIMARY KEY, aggregate_id UUID, type TEXT,
  payload JSONB, created_at TIMESTAMPTZ DEFAULT now(), published_at TIMESTAMPTZ
);

-- Audit / reconciliation ledger (append-only, never updated)
CREATE TABLE seat_ledger (
  id BIGSERIAL PRIMARY KEY, class_id UUID, reservation_id UUID,
  delta INT,                      -- +1 on book, -1 on cancel/expire
  reason TEXT, at TIMESTAMPTZ DEFAULT now()
);
```

**Point to make:** *"I'm pushing all three invariants — capacity, one-per-user, idempotency — into DB constraints. Application-level checks are advisory; under concurrency only the storage engine can actually enforce them. If my service has a bug or I deploy two versions at once, the constraint still holds."*

---

## 6. High-level architecture

```
                                 ┌──────────────┐
 Mobile / Web ──► CDN ──► API GW │ authN/Z      │
                        (edge)   │ rate limit   │
                                 │ req coalesce │
                                 └──────┬───────┘
             ┌──────────────────────────┼───────────────────────────┐
             ▼                          ▼                           ▼
     ┌───────────────┐          ┌────────────────┐          ┌──────────────┐
     │ Search/Browse │          │  Reservation   │          │   Stream     │
     │   Service     │          │    Service     │          │  (SSE/WS)    │
     │ (stateless)   │          │  (stateless)   │          │  fanout      │
     └───────┬───────┘          └───────┬────────┘          └──────▲───────┘
             │                          │  ① admission gate        │
             │                          ▼                          │
             │                 ┌─────────────────┐                 │
             │                 │ Redis Cluster   │                 │
             │                 │ avail counters  │  (gate, NOT     │
             │                 │ + Lua CAS       │   source of     │
             │                 └───────┬─────────┘   truth)        │
             │                         │ ② authoritative write     │
     ┌───────▼───────┐        ┌────────▼────────────────────┐      │
     │ Read replicas │◄───────┤  Inventory Store (sharded)  │      │
     │ + ES/OpenSrch │  repl  │  Postgres/Spanner, shard by │      │
     │ (browse index)│        │  class_id. SINGLE-SHARD TXN │      │
     └───────────────┘        └────────┬────────────────────┘      │
                                       │ CDC (Debezium) / outbox   │
                                       ▼                           │
                                ┌─────────────┐                    │
                                │   Kafka     │────────────────────┘
                                └──┬───┬───┬──┘
                   ┌───────────────┘   │   └──────────────┐
                   ▼                   ▼                  ▼
           ┌──────────────┐   ┌────────────────┐  ┌───────────────┐
           │ Cache updater│   │ Waitlist svc   │  │ Notification  │
           │ (Redis+ES)   │   │ (promotion)    │  │ + Analytics   │
           └──────────────┘   └────────────────┘  └───────────────┘

           ┌───────────────────────────────────────────────────┐
           │ Hold Reaper (expiry sweep) · Invariant Auditor     │
           │ (reconcile booked_count vs ledger) · Alerting      │
           └───────────────────────────────────────────────────┘
```

**Components, one line each:**
- **API Gateway** — authN, per-user & per-class rate limiting, request coalescing for availability reads, TLS termination.
- **Browse/Search Service** — reads from an OpenSearch index + read replicas; *never* touches the hot row.
- **Reservation Service** — stateless; owns the two-step write (gate → authoritative commit).
- **Redis Cluster** — per-class available-seat counters used as an **admission gate** + idempotency fast-path + hold tracking. Explicitly *not* the source of truth.
- **Inventory Store** — sharded RDBMS (Postgres w/ Citus, Vitess/MySQL, or CockroachDB/Spanner). Source of truth. Shard by `class_id`.
- **Kafka** — CDC + outbox events; drives cache updates, SSE fanout, waitlist, notifications, analytics.
- **Reaper / Auditor** — background correctness machinery (section 11).

---

## 7. ★ The core: preventing over-booking ★

This is 60% of your score. Walk through the options, then commit.

### 7.0 First, name the failure you're preventing

```
 T1: Alice reads available = 1            T2: Bob reads available = 1
 T3: Alice checks 1 > 0  → OK             T4: Bob checks 1 > 0  → OK
 T5: Alice writes booked = 20             T6: Bob writes booked = 20   ← lost update
 Result: 21 reservations against capacity 20.
```
> *"Read-modify-write without serialization. Any design where the check and the decrement aren't a single atomic step over the same authority will over-book. So my job is to pick that single atomic step."*

---

### 7.1 Option A — **Atomic conditional update (recommended)** ⭐

One statement does check + decrement. No read first. No application-level lock.

```sql
BEGIN;  -- single shard, so this is a local ACID txn, no 2PC

-- (1) Conditional decrement: the WHERE clause IS the capacity check.
UPDATE class_inventory
   SET booked_count = booked_count + :seats,
       version      = version + 1
 WHERE class_id = :classId
   AND state = 'OPEN'
   AND booked_count + :seats <= capacity;
-- rows_affected == 0  →  ROLLBACK, return 409 CLASS_FULL

-- (2) Create the reservation. Unique indexes enforce the other two invariants.
INSERT INTO reservation(reservation_id, class_id, user_id, seats, status,
                        idempotency_key, expires_at)
VALUES (:rid, :classId, :userId, :seats, :status, :idemKey, :expiry);
-- unique violation on uq_idem          → 200 idempotent replay (re-read & return original)
-- unique violation on uq_active_user_class → 409 ALREADY_RESERVED

-- (3) Audit + event, same transaction. No dual-write problem.
INSERT INTO seat_ledger(class_id, reservation_id, delta, reason) VALUES (:classId,:rid,+:seats,'BOOK');
INSERT INTO outbox(aggregate_id, type, payload) VALUES (:classId,'ReservationCreated', :json);

COMMIT;
```

**Why this is right:**
- The DB's row lock serializes concurrent updaters on that single row. Exactly one of Alice/Bob wins. **No lost update is possible.**
- No `SELECT ... FOR UPDATE` round-trip → the lock is held for microseconds, not for a network RTT.
- No distributed lock, no 2PC, no consensus service to operate.
- `rows_affected` is your answer. Zero ambiguity.
- Outbox in the same transaction ⇒ events are exactly as durable as the booking. Solves dual-write.

**Throughput of one row:** a single-row update on a warm Postgres is ~0.2–0.5 ms including lock hold. That's **2,000–5,000 serialized ops/sec on one class**. And since a class only ever has ≤ 200 *successful* bookings, the DB only needs to survive the *rejections* — which the Redis gate absorbs (next section).

**Say the trade-off:** *"I'm deliberately choosing a pessimistic, single-writer-per-class design. It caps per-class throughput at a few thousand TPS. Given capacity ≤ 200, that is enormously more headroom than I need. If capacity were a million — a stadium — I'd shard the counter or move to a partitioned log; I'll cover that."*

---

### 7.2 Option B — `SELECT ... FOR UPDATE` (pessimistic row lock)

```sql
BEGIN;
SELECT booked_count, capacity FROM class_inventory WHERE class_id = :c FOR UPDATE; -- lock
-- app-side check
UPDATE class_inventory SET booked_count = booked_count + 1 WHERE class_id = :c;
COMMIT;
```
Correct, but **strictly worse than A**: the lock is held across an application round-trip (read → think → write), so lock hold time goes from µs to ~1 ms+. Under a 5k-QPS stampede that's a lock convoy and connection-pool exhaustion.

**Use it only when** you need to read extra state to decide (e.g. per-user entitlement checks that can't be expressed in the WHERE clause). Then: always lock rows in a **globally consistent order** (by `class_id`) to avoid deadlock on multi-class operations like `move`.

---

### 7.3 Option C — Optimistic concurrency (version CAS)

```sql
UPDATE class_inventory SET booked_count = :newCount, version = :v + 1
 WHERE class_id = :c AND version = :v;     -- 0 rows → someone beat me → retry
```
Correct, and great for low contention. **Bad for this workload:** at 5k QPS on one row the conflict rate approaches 100%, so you burn CPU on retries and your p99 explodes. Option A is strictly better here because the conditional `WHERE` makes the *operation itself* commutative-ish — there's nothing to retry, the DB just serializes.

> Nice line: *"Optimistic locking is the right tool when conflicts are rare. On a hot inventory row conflicts are the common case, so I use the DB's own row lock via a conditional update instead."*

---

### 7.4 Option D — Redis atomic counter as the **source of truth**

```lua
-- Lua = atomic on a single Redis node
local avail = tonumber(redis.call('GET', KEYS[1]))
if avail == nil then return -2 end          -- cache miss, fall through to DB
if avail < tonumber(ARGV[1]) then return -1 end
return redis.call('DECRBY', KEYS[1], ARGV[1])
```
Blazing fast (100k+ ops/sec on one key), single-threaded so naturally serialized.

**Why I will NOT make it the source of truth:** Redis replication is **asynchronous**. A primary failover can lose the last N decrements — and those decrements *are* seats. You'd silently over-book, which is the one thing we promised never happens. Redis Cluster explicitly does not guarantee strong consistency. (Redlock doesn't fix this and is contested for exactly this reason.)

**What I DO use it for:** an admission gate in front of the DB (next).

---

### 7.5 ★ The recommended composite: Redis gate + DB authority ★

```
 request ──► ① Redis Lua: DECRBY class:{id}:avail
             │
             ├── returns < 0  ──► 409 CLASS_FULL, ~1 ms, never touches DB   ← kills 98% of the herd
             │
             └── returns ≥ 0  ──► ② DB conditional UPDATE (Option A)
                                   │
                                   ├── rows=1 ──► 201 CREATED  ✔ authoritative
                                   └── rows=0 ──► INCRBY (give the token back)
                                                  409 CLASS_FULL
```

**The safety argument — rehearse this, it's the money moment:**

| Redis state | Effect | Over-book? |
|---|---|---|
| Redis **over-counts** (lost DECRs after failover) | Too many requests let through to the DB | **No.** DB conditional update rejects the surplus. We just do a bit of wasted DB work. |
| Redis **under-counts** (lost INCRs from cancellations) | Some genuinely-free seats get wrongly rejected | **No.** We *under-sell*. Self-heals on the next periodic resync. |
| Redis **down entirely** | Gate bypassed (fail-open on the gate, with a circuit breaker) | **No.** All traffic hits the DB; latency rises, correctness holds. Degrade gracefully. |

> **"Redis can be wrong in either direction and the invariant still holds, because Redis is an optimization, not an authority. That's the whole point of the split."**

**Resync:** the cache-updater consumer tails CDC and writes `avail = capacity - booked_count` **only if the incoming `version` > the cached `version`** (store counter + version in a Redis hash, update via Lua CAS). This makes out-of-order CDC events safe. Plus a full rebuild for a class on cache miss, and a low-frequency sweep for classes near full.

**When do I skip the gate?** If measured per-class QPS is low (most classes), skip Redis entirely — a single DB round-trip is already under 5 ms. Apply the gate only to classes flagged **hot** (enrollment opening in the next 10 min, or observed QPS > threshold). Adaptive, not blanket.

---

### 7.6 Option E — Partitioned log / single-writer actor (the "if you need 100× more" answer)

```
POST → Kafka topic `reservations`, partitioned by class_id  (ordering per class guaranteed)
        │
        ▼
   Consumer owns partition P. Holds in-memory inventory for all classes in P.
   Single-threaded per class ⇒ serialization is free, no locks at all.
   Decides ACCEPT/REJECT → writes to DB (batched) → emits result to `reservation-results`
        │
        ▼
   Client: 202 Accepted + ticket → polls GET /reservations/{ticket} or receives a WS push
```
- **Pros:** 10k+ decisions/sec per class; natural fairness (log order = arrival order); absorbs spikes in the log instead of in your DB; replayable.
- **Cons:** booking becomes **asynchronous** — a worse UX and a much bigger API surface. Partition rebalance must be fenced (two consumers briefly owning a partition = over-book) — needs a fencing token / epoch written to the DB. Recovery requires replaying from the last checkpointed offset.
- **Verdict:** **over-engineering for capacity ≤ 200.** Name it, explain when you'd reach for it (flash sales, 100k-seat stadium drops, Ticketmaster-class), and move on. *Knowing when not to use the fancy thing is an SDE-3 signal.*

---

### 7.7 Decision table (draw this on the whiteboard)

| Approach | Correct? | Per-class TPS | Ops cost | Latency | Verdict |
|---|---|---|---|---|---|
| App-level check-then-write | ❌ | — | low | low | Never |
| Distributed lock (Redlock/ZK) | ⚠️ fencing-dependent | ~1k | high | +RTT | No — DB already gives me this for free |
| `SELECT FOR UPDATE` | ✅ | ~1k | low | medium | Fallback when extra reads are needed |
| Optimistic version CAS | ✅ | collapses under contention | low | spiky p99 | No — conflicts are the norm here |
| **Conditional UPDATE (A)** | ✅ | **2–5k** | **low** | **low** | **✔ Chosen core** |
| **+ Redis gate (A+D)** | ✅ | **~100k attempts** | medium | **lowest** | **✔ Chosen for hot classes** |
| Partitioned log single-writer | ✅ | 10k+ | high | async | v3, only if needed |

**Commit out loud:** *"I'm going with the conditional UPDATE as the authority, plus an adaptive Redis admission gate for hot classes."*

---

## 8. Idempotency & exactly-once semantics

A booking POST that times out at the client is **indistinguishable** from one that was never processed. Retries are guaranteed. So:

1. Client generates a UUID `Idempotency-Key` **once per logical intent** (not per retry) and sends it on every retry.
2. Server flow:
   ```
   a. Redis SET idem:{userId}:{key} = "IN_PROGRESS" NX EX 120
      - if NX fails and value is IN_PROGRESS → 409 Retry-After:1   (concurrent duplicate)
      - if NX fails and value is a reservationId → return that reservation, 200
   b. Run the DB transaction (which also has uq_idem as the real guard)
   c. On unique-violation of uq_idem → SELECT the existing row, return it with 200
   d. Redis SET idem:{userId}:{key} = reservationId EX 24h
   ```
3. **Redis is again only a fast path.** The unique index `uq_idem` is the actual guarantee. If Redis loses the key, the DB still rejects the duplicate.
4. Hash the request body into the stored record; if the same key arrives with a *different* body → `422 IDEMPOTENCY_KEY_REUSED`. Prevents a client bug from silently rebinding a key.
5. Key TTL ≥ max client retry window (24 h is generous and cheap at this volume).

> *"Idempotency is not a nice-to-have in a booking system — it's half of 'never over-book'. A duplicate request consumes a real seat."*

---

## 9. Read path — scaling availability

### 9.1 Layers (cheapest first)
1. **Client-side:** poll `GET /availability` at 3–5 s with `ETag`/`If-None-Match` → mostly `304`s. Or subscribe to SSE.
2. **CDN / edge:** `Cache-Control: max-age=5` on the *browse list*. Availability numbers are advisory anyway.
3. **API-GW request coalescing:** 5,000 concurrent readers of the same hot class collapse into **one** backend read ("single-flight"). This alone removes most read pressure on a viral class.
4. **Redis:** availability counters already live there (they're the gate) — reads are a `HGET`. Sub-ms.
5. **Read replicas:** `GET /my-reservations`, history, admin. Route by a `read-your-writes` rule: for ~2 s after a user's own write, pin that user to the primary (or serve from a write-through cache) so they immediately see their new booking.
6. **OpenSearch/Elasticsearch:** the browse/filter index (city, category, time, instructor). Updated from CDC. Availability in the index is a *hint*; it is refreshed on a few-second lag and that is fine.

### 9.2 "Reflected in real time to other users"
Be precise and honest here — a lot of candidates over-promise:

> *"I'll give sub-second propagation, but not linearizability, on the browse path. Concretely: a successful booking commits → CDC event → Kafka → fanout service → SSE push to clients subscribed to that class. Typical end-to-end 200–500 ms. The UI shows '3 left' with a freshness timestamp. It is impossible — and unnecessary — to make a browse list globally linearizable; what the user actually needs is that **the moment they press Book, the answer is authoritative**. That's guaranteed by the write path. I'd rather show a slightly stale number and be correct at commit than pretend to a consistency level I can't deliver."*

Fanout service: stateless WS/SSE servers; subscription registry in Redis (`class:{id}:subscribers`); Kafka consumer group per fanout node; backpressure by dropping intermediate updates and sending only the latest value per class per 250 ms window (conflation — nobody needs every tick).

---

## 10. Write path — scaling & the hot-class problem

### 10.1 Sharding
- **Shard key = `class_id`** (hash). Gives (a) co-location of inventory + reservations ⇒ single-shard transactions, (b) even spread across shards, (c) blast radius = one shard.
- Why not shard by `user_id`? Then a class's inventory and its reservations live on different shards → every booking becomes a distributed transaction. Explicitly reject this.
- Why not by `studio_id`? Mega-studios create hot shards.
- Use **virtual buckets** (e.g. 4096 slots → N physical shards) so resharding is a bucket move, not a rehash of the world.

### 10.2 The hot class
A celebrity class = one hot row = one hot shard. Mitigations in order of cost:
1. **Redis admission gate** (§7.5) — absorbs ~98% of doomed traffic. Usually sufficient.
2. **Per-class rate limiting + jitter at the gateway**: `429 Retry-After: <random 1–5s>`. Prevents synchronized retry storms (thundering herd on retry).
3. **Virtual waiting room** for scheduled enrollment opens: issue signed, time-ordered entry tokens; admit K users/sec to the booking endpoint. Converts a 50× spike into a flat line. This is what real systems do for drop events.
4. **Counter sharding** (only if a *single* class genuinely needs > 5k TPS, i.e. huge capacity): split capacity into `M` sub-counters `class:{id}:bucket:{0..M-1}`, each its own row. A request hashes to a random bucket; on `rows_affected = 0`, try the next bucket (bounded probes), then declare full. Trade-off: a user can be rejected while seats exist in another bucket → you need a **rebalancer** and a final "last seats" phase that collapses to a single counter. **Explicitly not needed at capacity ≤ 200 — say that.**

### 10.3 Connection management
At 5k write QPS, naive connection-per-request kills Postgres. Use **PgBouncer in transaction mode**, bounded pools per service instance, and a **bulkhead** per shard so one hot shard can't starve the pool for all others. Add a circuit breaker: when shard p99 > 150 ms, shed with `503` rather than queue (queuing just converts latency into timeouts).

---

## 11. Holds, expiry, cancellation, waitlist

### 11.1 Holds
`booked_count` counts **HELD + CONFIRMED** — a held seat is unavailable. `reservation.expires_at = now() + 5 min`.

**Reclaiming expired holds — do both:**
1. **Lazy:** when a booking attempt would fail with `CLASS_FULL`, first run an opportunistic sweep for that class:
   ```sql
   UPDATE reservation SET status='EXPIRED'
    WHERE class_id=:c AND status='HELD' AND expires_at < now() RETURNING seats;
   UPDATE class_inventory SET booked_count = booked_count - :freed, version = version + 1 WHERE class_id=:c;
   ```
   then retry the booking once. Self-healing exactly where it matters.
2. **Eager:** a Reaper job scanning `ix_expiring` every 10 s, partitioned by shard, with a leader lease (so two reapers don't double-decrement — guard with `WHERE status='HELD'` so the transition is idempotent).

> **Never** rely on Redis key TTL alone for hold expiry — TTL fires on the *cache*, not on the *truth*, and if Redis flushes you lose all pending expirations. Redis TTL can be the *trigger*; the DB transition is the *effect*.

**Race to call out:** payment confirms at the exact moment the hold expires. Fix: `confirm` is a conditional update — `UPDATE reservation SET status='CONFIRMED' WHERE reservation_id=:r AND status='HELD'`. If 0 rows, the hold already expired → try to re-acquire a seat; if full, **refund** and return `409 HOLD_EXPIRED`. State the refund path explicitly — interviewers love that you thought about money.

### 11.2 Cancellation
```sql
BEGIN;
UPDATE reservation SET status='CANCELLED', updated_at=now()
 WHERE reservation_id=:r AND status IN ('HELD','CONFIRMED');   -- 0 rows → already cancelled → 200 idempotent
UPDATE class_inventory SET booked_count = booked_count - :seats, version = version + 1
 WHERE class_id=:c;
INSERT INTO seat_ledger(...) VALUES (:c,:r,-:seats,'CANCEL');
INSERT INTO outbox(...) VALUES (:c,'ReservationCancelled', :json);
COMMIT;
-- then (best-effort) Redis INCRBY class:{c}:avail
```
The `status IN (...)` predicate makes cancellation **idempotent** — double-cancel can't double-credit a seat. That's the bug 80% of candidates ship.

### 11.3 Waitlist (v2)
- `waitlist(class_id, user_id, position, state, claim_expires_at)`; ordering by a monotonic sequence (or Redis `ZADD` with a server timestamp score).
- On `ReservationCancelled` (from the outbox → Kafka), the Waitlist Service pops the head and **immediately converts the freed seat into a HELD reservation** for them with a 10-minute claim window — otherwise the freed seat gets sniped by a random browser before the waitlisted user is notified. Then notify.
- If they don't claim, expire → promote the next. The Kafka consumer is idempotent on `(class_id, cancellation_event_id)`.

---

## 12. Fault tolerance, durability, multi-DC

### 12.1 In-region durability (RPO = 0)
- Postgres **synchronous_commit = on** with `synchronous_standby_names` = at least 1 replica in a **different AZ** (quorum commit: `ANY 1 (s1, s2)`). A commit acknowledged to the user has hit ≥ 2 AZs' WAL.
- Automated failover (Patroni/RDS Multi-AZ) with **fencing**: the old primary is demoted/STONITH'd before the new one accepts writes. Split-brain here = double-booking, so fencing is non-negotiable — call that out.
- `503 INVENTORY_UNAVAILABLE` during the ~30 s failover window. **Fail closed.** *"I will take a booking outage before I take an over-booking."*

### 12.2 Multi-datacenter
```
Region A (home for classes in A)        Region B (home for classes in B)
  ┌──────────────┐                        ┌──────────────┐
  │ Primary      │ ───── async repl ────► │ Standby (RO) │
  │ shards 0..N  │ ◄──── async repl ───── │ Primary      │
  └──────────────┘                        └──────────────┘
  Reads served locally everywhere. Writes routed to the class's HOME region.
```
- **Regional write affinity.** A class physically happens in a place; route its writes to that region's home shard. Cross-region booking (user in SG booking a class in BLR) pays one WAN RTT (~60–120 ms) — still inside the 200 ms budget if the rest of the path is tight. Mention this budget explicitly.
- **Never multi-master the inventory row.** Last-write-wins on a counter = silent over-booking. Two regions both decrementing their own copy will each think there's a seat.
- If true active-active is a hard requirement → move to **Spanner / CockroachDB** (Paxos/Raft per range, external consistency). Cost: higher write latency (TrueTime commit-wait / cross-region consensus, ~10–100 ms). State the trade honestly: *"Spanner buys me globally-consistent writes and removes the whole class of failover-correctness bugs, at the price of latency and money. For capacity-200 classes with regional affinity, sharded Postgres is cheaper and fast enough — but Spanner is the right call if the business needs any class bookable-at-equal-latency from anywhere."*
- **Region failover:** async replication ⇒ non-zero RPO cross-region. Promote the standby only with an explicit, operator-gated runbook, and reconcile from the `seat_ledger` + outbox afterward. Automated cross-region promotion of a CP inventory store is a foot-gun.

### 12.3 Degradation ladder (name this — it reads as production experience)
| Failure | Behavior |
|---|---|
| Redis gate down | Circuit-break the gate, all traffic → DB. Higher latency, correctness intact. |
| Read replica lag > 10 s | Serve browse from cache; mark availability `stale: true` in the response. |
| Kafka down | Bookings still succeed (outbox rows accumulate). Notifications/waitlist/SSE lag and catch up on recovery. **The outbox is why bookings don't fail when Kafka does.** |
| One shard down | Only classes on that shard fail (`503`). Other classes book normally. Blast radius = 1/N. |
| Search index down | Fall back to DB-backed browse with reduced filters. |
| Payment provider down | Degrade to `HOLD`-only mode; extend hold TTL; confirm when the provider recovers. |

---

## 13. Observability — "monitoring & alerting for over-booking anomalies"

The requirement literally asks for this. Prevention is not enough; you must **detect**.

### 13.1 Continuous invariant auditor (runs every minute, per shard)
```sql
SELECT i.class_id, i.capacity, i.booked_count, r.actual, l.ledger_sum
  FROM class_inventory i
  LEFT JOIN (SELECT class_id, SUM(seats) actual FROM reservation
              WHERE status IN ('HELD','CONFIRMED') GROUP BY class_id) r USING (class_id)
  LEFT JOIN (SELECT class_id, SUM(delta) ledger_sum FROM seat_ledger GROUP BY class_id) l USING (class_id)
 WHERE i.booked_count > i.capacity                       -- ❶ OVER-BOOKED  → PAGE
    OR i.booked_count <> COALESCE(r.actual,0)            -- ❷ counter drift → PAGE
    OR COALESCE(l.ledger_sum,0) <> COALESCE(r.actual,0); -- ❸ ledger drift  → ticket
```
Three independent representations of the same truth (counter, row count, append-only ledger). Any disagreement is a bug — and you find out in 60 seconds, not from a customer tweet.

### 13.2 Metrics / SLOs
| Metric | Alert |
|---|---|
| `overbooking_violations_total` | **> 0 → page immediately.** This is the P0 SLI. |
| `inventory_counter_drift_total` | > 0 → page |
| `reservation_latency_p99` | > 200 ms for 5 min → page |
| `conditional_update_zero_rows_rate` | sudden spike → a class went hot, or a bug |
| `redis_gate_false_reject_rate` (gate said full, DB had seats) | > 1% → resync is lagging |
| `hold_expiry_lag_seconds` | > 60 s → reaper is stuck, seats are stranded |
| `outbox_unpublished_age_seconds` | > 30 s → CDC/Kafka pipeline stalled |
| `shard_connection_pool_saturation` | > 80% → scale or shed |

### 13.3 Other
- **Structured audit log** on every state transition: who, when, from→to, request id. Non-repudiation when a user disputes.
- **Distributed tracing** (OpenTelemetry) — trace id propagated from gateway through Redis, DB, Kafka. Makes "why was this 400 ms" a 2-minute investigation.
- **Chaos drills:** kill the Redis primary mid-enrollment, kill a DB primary mid-transaction, inject 10 s replica lag — and assert `overbooking_violations_total == 0` throughout. *"I'd gate releases on a load test that fires 10,000 concurrent bookings at a 20-seat class and asserts exactly 20 succeed."* That sentence is worth a lot.

---

## 14. Security & abuse (30 seconds, but mention it)
- JWT auth; authorization check that `userId` in the body == subject in the token (or an admin scope).
- Rate limit per user **and** per IP **and** per class — scalpers/bots scripting enrollment opens are a real threat on class platforms.
- Bot defense on the enrollment-open endpoint (device attestation / proof-of-work / CAPTCHA in the waiting room).
- Booking-then-cancel abuse (denying others a seat): cancellation fee policy + reputation scoring.
- PII: encrypt at rest, tokenize payment refs, GDPR delete = anonymize the reservation row but keep the ledger entry (financial record).

---

## 15. Variant: restaurant **table** booking (the round summary mentions it)

If they pivot mid-round, map it in 60 seconds:

| Class reservation | Restaurant table |
|---|---|
| counter per class | inventory per `(restaurant, time_slot, table_size_bucket)` |
| seats fungible | tables are discrete + **party size must fit** → bin-packing, not a counter |
| fixed slot | **overlapping time windows** — a 7:00 booking for 90 min blocks 7:00–8:30 |
| — | needs an **interval/exclusion constraint**, not just a count |

```sql
-- Postgres btree_gist makes "no two bookings on the same table overlap" a DB constraint.
ALTER TABLE table_booking ADD CONSTRAINT no_overlap
  EXCLUDE USING gist (table_id WITH =, tstzrange(starts_at, ends_at) WITH &&)
  WHERE (status IN ('HELD','CONFIRMED'));
```
Same philosophy — *push the invariant into the storage engine*. The core answer (conditional/constraint-enforced atomic write, idempotency key, hold+reap, outbox, auditor) is unchanged; only the inventory *shape* changes from a scalar to an interval set.

---

## 16. Evolution roadmap (close with this — shows product judgment)

| Phase | Scope |
|---|---|
| **v1 (ship in weeks)** | Single sharded Postgres, conditional UPDATE, idempotency key, holds + reaper, outbox + Kafka, invariant auditor, polling-based availability. **No Redis gate.** It is correct and handles ~2k write QPS. |
| **v2** | Redis admission gate for hot classes, SSE push availability, waitlist with claim windows, OpenSearch browse index, read replicas. |
| **v3** | Virtual waiting room for scheduled drops, multi-region with write affinity, counter sharding *only if measurement demands it*, partitioned-log writer for mega-capacity events. |

> *"I'd deliberately not build the Redis gate on day one. It's the kind of optimization that should be driven by a measured hot-key metric, not by a guess — and every extra moving part in the write path is a new way to be wrong."*

---
---

# PART B — How to NARRATE this in the interview

## Time budget for a 60-minute round

| Minutes | What you do | What you must say |
|---|---|---|
| **0–5** | Clarify | The 8 questions in §1. End with "I'll assume X; stop me if wrong." |
| **5–8** | Requirements + the thesis | FR/NFR list. Then **the one-sentence thesis from §0.** Then BOTE showing storage is trivial but contention isn't. |
| **8–14** | API contract | Write `POST /reservations` with the `Idempotency-Key` header on the board. The header alone signals seniority. |
| **14–20** | Data model | Three tables + the **three unique/check constraints**. Say "invariants live in the DB." |
| **20–28** | High-level diagram | Draw §6. Keep it to ~8 boxes. Narrate the write path as a single arrow first, then decorate. |
| **28–42** | ★ Deep dive: consistency ★ | Draw the lost-update race. Then options A→E with the decision table. **Commit** to A + adaptive Redis gate. Explain *why Redis can be wrong and we're still safe*. This is the heart. |
| **42–50** | Scale + failure | Sharding by class_id → single-shard txns. Hot class → gate/waiting room. Degradation ladder. Multi-DC write affinity, "never multi-master a counter." |
| **50–56** | Holds, cancel, waitlist, monitoring | Idempotent cancel predicate. Lazy+eager reaper. The invariant auditor query + `overbooking_violations_total > 0 → page`. |
| **56–60** | Trade-offs + roadmap | v1/v2/v3. "What I'd measure before building v2." Ask *them* a question. |

**Pace check:** if you're at minute 30 and haven't started the consistency deep-dive, **cut the API and data model short and jump**. The consistency section is the one they're grading.

---

## The actual script for the 5 highest-value moments

**① Opening the design (minute 8)**
> "Before I draw anything, let me frame the problem. We'll do about 2.8 million reservations a day — that's 255 GB a year, which fits comfortably on a single database box. So this isn't a storage-scale problem. The hard part is that at enrollment-open, ten thousand people hit the *same* class in two seconds, and that class has twenty seats. So the entire design is about one question: where is the single point that atomically decides 'yes' or 'no' for a given class, and how do I keep ninety-eight percent of that traffic from ever reaching it? Let me build outward from that."

**② Introducing the core write (minute 30)**
> "The naive flow is: read availability, check it's greater than zero, write. That's a read-modify-write, and under concurrency two requests both read 'one seat left' and both write — classic lost update, and we've over-booked.
>
> My fix is to never read first. One statement does the check and the decrement together: `UPDATE class_inventory SET booked_count = booked_count + 1 WHERE class_id = ? AND booked_count + 1 <= capacity`. The database's row lock serializes everyone on that row, and `rows_affected` is my answer — one means I got a seat, zero means it's full. No application lock, no distributed lock, no two-phase commit. And because I shard by `class_id`, the inventory row and the reservation row live on the same shard, so the whole thing is one local ACID transaction."

**③ Justifying Redis (minute 35)**
> "A single row gives me a few thousand transactions a second, which is plenty for a twenty-seat class — but I don't want five thousand doomed requests a second burning database connections. So I put a Redis counter in front as an *admission gate*. Crucially it is not the source of truth, because Redis replication is asynchronous and a failover can silently lose decrements — and a lost decrement is a lost seat.
>
> Here's why that's safe anyway: if Redis over-counts, it lets too many through, and the database rejects the surplus — we just waste some work. If Redis under-counts, we under-sell, and the resync job fixes it. If Redis dies, the circuit breaker opens and everything goes straight to the database — slower, still correct. **Redis can be wrong in either direction and the invariant holds**, because it's an optimization, not an authority. And honestly, I'd ship v1 without it and add it when a hot-key metric tells me to."

**④ On "real-time availability" (minute 46)**
> "The requirement says reservations should be reflected in real time to other users. I want to be precise about what I'm promising. I'll give sub-second propagation via change-data-capture into Kafka and an SSE push — typically two to five hundred milliseconds. But I'm explicitly *not* promising that a browse list is linearizable, because that would mean a globally-coordinated read on every page view and it isn't buildable at this scale.
>
> What the user actually needs is different: that the instant they press Book, the answer is authoritative. That's guaranteed by the atomic write, not by the browse page. So I'll show '3 seats left' with a freshness timestamp and accept that it can be a second stale — and be exactly right at commit time."

**⑤ Closing (minute 57)**
> "To summarize the trade-offs I chose: I picked CP over AP on the inventory key — during a shard failover I return 503 and refuse bookings rather than risk double-selling a seat, because an over-booking is a person turned away at the door and a refund, while a thirty-second booking outage is a retry. I pushed all three invariants into database constraints rather than application code, because under concurrency only the storage engine can actually enforce them. And I deliberately left the Redis gate, counter sharding, and the partitioned-log writer out of v1 — they're measured optimizations, not guesses, and each one is a new way to be wrong.
>
> The thing I'd want to validate first is the hot-key distribution: if ninety percent of load is on one percent of classes, the waiting room becomes v1.1 instead of v3. Is that the shape of the traffic you actually see?"

---

## Delivery rules
- **Drive, don't wait.** State the assumption and keep moving. Silence is scored as uncertainty.
- **Write the invariant on the board and circle it:** `active_reservations ≤ capacity`. Refer back to it every time you make a choice. It gives your whole answer a spine.
- **Always say the trade-off, unprompted.** "I'm choosing X; the cost is Y; I'd accept that because Z." That single pattern is the biggest separator between SDE-2 and SDE-3.
- **Use numbers.** "~0.3 ms row lock, so ~3,000 TPS on one row" beats "it's fast."
- **Admit limits proactively.** "A cross-region booking costs me ~80 ms of my 200 ms budget; if the real traffic were globally mixed I'd move to Spanner." Volunteering your own weak spot preempts the gotcha.
- **Don't over-build.** Explicitly rejecting counter-sharding and the Kafka writer *because the capacity is 200* scores higher than building them.
- **If stuck:** go back to the invariant. "Let me re-derive this from the constraint that booked_count can never exceed capacity…" — it always produces a next sentence.

---
---

# PART C — Cross-questions you WILL get, with answers

### A. Consistency & correctness

**Q1. Two users click Book at the exact same microsecond for the last seat. Walk me through it.**
> Both hit the Redis gate; `DECRBY` is atomic because Redis is single-threaded, so one gets 0 and one gets -1. The -1 is rejected in ~1 ms. The winner reaches the DB, where `UPDATE ... WHERE booked_count + 1 <= capacity` takes the row lock, succeeds, `rows_affected = 1`, we insert the reservation and commit. If somehow both reached the DB (e.g. Redis was stale), the row lock serializes them: the first commits, the second's `WHERE` predicate is re-evaluated against the committed value, matches zero rows, and gets `409 CLASS_FULL`. Under Postgres read-committed, a blocked `UPDATE` re-checks the predicate against the new row version after the lock is released — that's exactly the behavior I'm relying on.

**Q2. What isolation level, and does it matter?**
> `READ COMMITTED` is sufficient *for this specific statement*, because the conditional UPDATE's predicate is re-evaluated after the lock is acquired. I don't need `SERIALIZABLE` — which matters, since `SERIALIZABLE` on a hot row would produce serialization failures and forced retries. If I had used `SELECT` then `UPDATE` as separate statements, read-committed would *not* be enough and I'd need `REPEATABLE READ`/`SERIALIZABLE` or `FOR UPDATE`. That's another reason to prefer the single-statement form.

**Q3. Why not just use a distributed lock (Redlock / ZooKeeper)?**
> Three reasons. (1) The database already gives me mutual exclusion on that row for free — adding a lock service is a redundant component with its own failure modes. (2) Redlock without fencing tokens is unsafe under GC pauses and clock drift: a client can believe it holds a lock that has expired, and then two writers proceed. (3) Even done correctly with ZooKeeper, I pay an extra network round-trip inside my 200 ms budget. The right rule is: if your source of truth can enforce the invariant, don't put a lock in front of it.

**Q4. You're trusting Redis. What if it loses data?**
> I'm explicitly not trusting it for correctness. Both error directions are safe: over-counting lets surplus requests through and the DB rejects them; under-counting under-sells and the CDC resync corrects it. The only thing I lose is efficiency. The property I rely on is that Redis is a *filter*, and a filter that occasionally passes too much or too little can't violate an invariant enforced downstream.

**Q5. Doesn't a single hot row cap your throughput?**
> Yes, at roughly 2,000–5,000 serialized operations per second for that row. I'm accepting that deliberately, because a class has at most 200 seats — so at most 200 successful writes ever touch it. The gate handles the rejections. If capacity were 100,000, I'd shard the counter into M sub-counters, accept that a request may be rejected while another bucket has seats, add a rebalancer, and collapse to a single counter for the final few seats. I'd only do that with a measurement forcing me to.

**Q6. CAP — where do you sit?**
> On the inventory write path, firmly **CP**. During a partition or failover I return 503 and refuse to book, because an over-booking is an unrecoverable real-world failure — someone shows up and there's no bike — whereas a brief booking outage is a retry. The read path is **AP**: I'll happily serve a stale seat count from cache during a partition, because it's advisory. Different paths get different answers; that's the actual insight, not picking one letter for the whole system.

**Q7. How do you handle a user double-clicking Book?**
> The client sends the same `Idempotency-Key` for both. The first acquires a Redis `SET NX` sentinel; the second sees `IN_PROGRESS` and gets a 409 with Retry-After. If Redis missed it, the unique index on `(user_id, idempotency_key)` rejects the second insert and I return the original reservation with 200. Separately, the partial unique index on `(class_id, user_id) WHERE status IN ('HELD','CONFIRMED')` means even two *different* keys can't give one user two seats in a class.

**Q8. What if the DB commits but the response is lost in the network?**
> The client retries with the same idempotency key, hits the unique index, and I return the already-created reservation with 200. This is exactly why the key is mandatory rather than optional — without it, every network timeout is a potential double-booking, and network timeouts are guaranteed at scale.

**Q9. Why not event sourcing / a pure append-only log of bookings?**
> You can, and the partitioned-log design in §7.6 is essentially that. It gives great auditability and replay. The cost is that the current seat count becomes a derived projection, so either every read is a fold (expensive) or you maintain a snapshot (and now you're back to a counter, plus the sync problem). I keep an append-only `seat_ledger` to get the audit and reconciliation benefits, while keeping the counter as the authoritative, cheap-to-read state. Best of both at this scale.

---

### B. Scale & performance

**Q10. Enrollment opens at 9:00 AM and 50,000 people hit at once. What happens?**
> Layered defense. (1) Virtual waiting room: pre-issue signed entry tokens with staggered admit times, so I admit maybe 500/sec to the booking endpoint and the other 49,500 sit in a queue with an honest position indicator. (2) Gateway rate limits per user and per class, with `Retry-After` **jitter** so the rejected retries don't re-synchronize into a second stampede. (3) Redis gate rejects anything past capacity in ~1 ms. (4) Autoscaling on the stateless tier, pre-warmed before 9:00 because cold-start is slower than the spike. (5) Circuit breaker sheds with 503 before the DB pool saturates. The key framing: for a 20-seat class, 49,980 of those requests are *going to fail no matter what* — my job is to make them fail cheaply and quickly, not to try to serve them all.

**Q11. How do you keep p99 under 200 ms?**
> Budget it explicitly: TLS+gateway 10 ms, auth 5 ms (cached JWT verify), Redis gate 2 ms, DB txn 5–15 ms, serialization + network 20 ms → ~50 ms typical, leaving ~150 ms of headroom for GC pauses, retries, and a cross-region hop. The things that would blow it are connection-pool waits (fix: PgBouncer + bulkheads + shed instead of queue), synchronous fanout (fix: everything after the commit is async via the outbox), and replica lag on read-your-writes (fix: pin to primary for 2 s after a user's write).

**Q12. 20:1 read:write — how do you scale reads?**
> Five layers, each absorbing the previous one's misses: client polling with ETags → mostly 304s; CDN caching the browse list for 5 s; **gateway request coalescing** so 5,000 simultaneous readers of one hot class collapse into a single backend read; Redis for the counter; read replicas plus an OpenSearch index for filtered browse. The coalescing one is the highest-leverage and the one most candidates miss.

**Q13. How do you shard, and why that key?**
> Hash on `class_id` into 4,096 virtual buckets mapped onto N physical shards. The reason is specifically that it co-locates `class_inventory` and `reservation` for the same class, so a booking is a single-shard ACID transaction and I never need 2PC. Sharding by `user_id` would put inventory and reservation on different shards and turn every booking into a distributed transaction — strictly worse. Virtual buckets mean resharding moves buckets rather than rehashing everything.

**Q14. A query needs all of a user's reservations across shards. How?**
> That's the cross-shard read that `class_id` sharding costs me. Two options: scatter-gather with a bounded fan-out (fine — a user has tens of reservations, not millions), or maintain a **secondary index table sharded by `user_id`** containing `(user_id, reservation_id, class_id, status)`, kept in sync via the outbox/CDC. The index is eventually consistent, which is acceptable for a history view, and I can fall back to the authoritative shard for a single-reservation read. I'd start with scatter-gather and add the index when latency demands it.

**Q15. Why not just put everything in one big Postgres?**
> Honestly, for v1 I would — 255 GB/year and 5k write QPS is within reach of one well-provisioned primary with replicas. I'd design the schema with `class_id` as the shard key from day one so that sharding later is a migration, not a rewrite. Sharding prematurely costs you cross-shard queries, operational complexity, and a harder time with transactions, for capacity you don't need yet.

---

### C. Failures & operations

**Q16. The DB primary dies mid-transaction. What does the user see?**
> Uncommitted transactions roll back — there's no partial state, so no half-booked seat. The user gets a 503 and the client retries with the same idempotency key. Failover takes ~20–30 s with Patroni; during that window bookings fail and reads continue from replicas. Because I commit synchronously to a replica in another AZ, nothing acknowledged is lost (RPO = 0 in-region). The critical operational detail is **fencing** the old primary before promoting — split-brain on an inventory row means two writers both selling the same seat.

**Q17. Kafka is down for an hour. Are bookings broken?**
> No, and that's the whole reason for the transactional outbox. Bookings write their event to the `outbox` table inside the same transaction as the state change, so the booking commits regardless of Kafka. The relay drains the outbox when Kafka recovers. What degrades: SSE availability pushes go stale, waitlist promotions are delayed, notifications queue up. All recoverable, none of it blocks the core flow. The alert I care about is `outbox_unpublished_age_seconds > 30`.

**Q18. How would you detect an over-booking that already happened?**
> The invariant auditor in §13.1, running per shard every minute, comparing three independent representations: the counter, a `COUNT(*)` of active reservations, and the sum of the append-only ledger deltas. Any disagreement pages immediately on `overbooking_violations_total`. Plus a DB-level `CHECK (booked_count <= capacity)` as a last-resort backstop that turns a logic bug into a failed transaction instead of a sold-twice seat.

**Q19. You find a class with 21 bookings against capacity 20. What now?**
> Incident response, in order: (1) Immediately mark the class `state = 'CLOSED'` to stop the bleeding. (2) Use the `seat_ledger` to establish arrival order — it's append-only and timestamped, so it's the authority on who was first. (3) Apply a *predeclared* policy: last-in is bumped, proactively notified, auto-refunded, offered a free transfer plus a credit. Having the policy pre-agreed with the business matters more than the technical fix. (4) Root-cause from traces; add a regression test to the concurrency load suite. (5) If the root cause is counter drift rather than a true double-sell, recompute `booked_count` from the reservation rows under a lock.

**Q20. How do you test that this actually can't over-book?**
> Three levels. **Unit/integration:** fire 1,000 concurrent bookings against a 10-seat class on a real Postgres (testcontainers), assert exactly 10 succeed and 990 get `CLASS_FULL`. **Chaos:** run that load while killing the Redis primary, killing the DB primary, and injecting 10 s of replica lag — assert the invariant still holds. **Production:** the continuous auditor, plus a synthetic canary class that books and cancels continuously. I'd gate releases on the concurrency test; it's cheap and it catches exactly the regressions that matter.

**Q21. How do you deploy a schema change to the hot inventory table?**
> Expand–migrate–contract, never a blocking DDL. Add nullable columns, dual-write, backfill in batches with throttling, switch reads, then drop. For Postgres specifically: `ALTER TABLE ... ADD COLUMN` with a default is fast on modern versions, but index builds must be `CREATE INDEX CONCURRENTLY`. Schedule anything risky outside enrollment windows — and have the enrollment calendar wired into the deploy pipeline so you *can't* deploy at 8:55 AM on a Monday.

---

### D. Design judgment / the "why not" questions

**Q22. Why a relational DB? Why not DynamoDB or Cassandra?**
> DynamoDB is actually viable: a conditional write (`UpdateExpression` with `ConditionExpression: booked_count < capacity`) gives the same atomicity on a single item, and it's great at elastic spikes. I'd consider it seriously. Where it costs me: multi-item transactions (`TransactWriteItems`) are limited and more expensive, cross-entity queries need careful GSI design, and I lose the partial unique indexes that enforce my other two invariants for free. **Cassandra I'd reject** — it's AP with last-write-wins; lightweight transactions exist but use Paxos per operation, which is slow and still subtly limited. For a strict-invariant workload with modest data volume, a relational store is the lower-risk choice.

**Q23. Would Spanner/CockroachDB be better?**
> For a globally active-active requirement, yes — they give externally-consistent distributed transactions and remove the entire class of failover-correctness bugs I otherwise have to engineer around. The costs are write latency (cross-region consensus, plus TrueTime commit-wait in Spanner), money, and less operational familiarity for most teams. My call: with regional write affinity, sharded Postgres is cheaper and faster. I'd switch to Spanner the moment the business says a class must be bookable at equal latency from any continent.

**Q24. Why not make the whole thing async — 202 Accepted and a ticket?**
> It's the right answer for extreme contention (§7.6) and it's how ticketing platforms handle drops. I'm not choosing it here because it meaningfully degrades UX — the user doesn't get a yes/no, they get a "we'll tell you" — and it roughly doubles the API and client complexity. With capacity ≤ 200, synchronous is comfortably achievable at under 200 ms, so the async complexity buys me nothing. I'd adopt it for a specific high-demand event type rather than for the whole platform.

**Q25. What breaks first if traffic 10×'s?**
> Database connections, before anything else — the stateless tier autoscales, but Postgres doesn't like 10,000 connections. First fix is PgBouncer in transaction mode plus per-shard bulkheads. Second pressure point is the hot shard if load is concentrated; mitigated by the Redis gate and, if needed, splitting that shard. Third is Redis hot-key bandwidth on a single key — mitigated by local in-process caching of "this class is definitely full" with a 1 s TTL, which is safe because full-is-sticky until a cancellation.

**Q26. What's the weakest part of your design?**
> *(Always have an answer — it reads as maturity.)* The Redis gate is the most complex moving part for the least guaranteed benefit, and it adds a compensating `INCR` path that can leak tokens if the service crashes between the DECR and the DB write. That leak makes us under-sell until resync, which is safe but sloppy. That's precisely why I'd keep it out of v1 and gate it on a measured hot-key metric. The second weakest is cross-region RPO — async replication means a regional disaster can lose recent bookings, so promotion is operator-gated with a ledger-based reconciliation rather than automated.

**Q27. If you had one more week, what would you build?**
> The chaos test harness that asserts the invariant under failure injection. Everything else in this design is a reasonable bet; that harness is what tells me whether the bets are *true*. Right after that, the virtual waiting room — because enrollment-open is the one predictable, schedulable spike, and it's much cheaper to flatten a spike you know is coming than to scale for it.

---

### E. Curveballs

**Q28. A class is cancelled by the instructor. 40 booked users. What happens?**
> One transaction sets the class to `CANCELLED` and bulk-transitions reservations to `CANCELLED`, writing ledger rows and a single outbox event. Downstream consumers fan out refunds (idempotent, keyed by reservation id), notifications, and optionally auto-rebook suggestions for the same slot. Refunds must be idempotent and retried with backoff, because the payment provider will fail some of them and double-refunding is as bad as not refunding.

**Q29. Support needs to add one extra person beyond capacity (the instructor said it's fine).**
> An explicit admin path that *raises `capacity`*, never one that bypasses the invariant. `UPDATE class_inventory SET capacity = capacity + 1` with an audit record of who approved it and why. The invariant stays true by construction, the auditor stays quiet, and there's a paper trail. The moment you add a "skip the check" flag, you've lost the ability to trust your own monitoring.

**Q30. Can a user book a class that's already started?**
> No — `AND state = 'OPEN'` plus a `booking_closes` check in the conditional UPDATE handles it atomically; there's no window where the class closes between the check and the write, because they're the same statement. Closing itself is driven by a scheduled job flipping `state`, with the timestamp predicate as the backstop in case the job is late.

**Q31. Recurring classes — a user books "every Monday for 8 weeks."**
> Expand into 8 independent class instances and book each. Then the key product question: all-or-nothing, or best-effort? All-or-nothing across 8 classes spanning multiple shards is a distributed transaction — I'd implement it as a **saga**: acquire holds on all 8, and if any fails, release the rest. Holds make the compensation natural because they already expire on their own if the saga crashes mid-way. I'd push hard for best-effort as the product behavior, because "7 of 8 booked" is usually better for the user than "nothing booked."

**Q32. How do you prevent one user from hoarding seats?**
> The partial unique index already caps them at one seat per class. For broader abuse — bots grabbing seats across many classes to resell — layer on per-user rate limits at enrollment-open, a cap on concurrent active holds, device attestation in the waiting room, and a cancellation-rate reputation score that pushes repeat offenders to the back of the admission queue.

**Q33. Last one: if you could only keep three things from this design, what are they?**
> The atomic conditional update, because it's the only thing that actually makes over-booking impossible. The mandatory idempotency key, because without it every network timeout is a potential double-booking. And the invariant auditor, because prevention is a hypothesis and the auditor is the test — it's what tells me whether the first two are actually working in production.

---

## Appendix — the whiteboard cheat sheet

```
INVARIANT:  active_reservations(class) <= capacity(class)       ← circle this

WRITE PATH
  [Redis Lua DECRBY]  → <0 ? 409 fast-reject (98% of herd)
          ↓ ok
  BEGIN
    UPDATE class_inventory SET booked_count=booked_count+1, version=version+1
     WHERE class_id=? AND state='OPEN' AND booked_count+1 <= capacity
    -- rows=0 → ROLLBACK + Redis INCR + 409 CLASS_FULL
    INSERT reservation (...)        -- uq_idem → 200 replay
                                    -- uq_active_user_class → 409 ALREADY_RESERVED
    INSERT seat_ledger (+1)
    INSERT outbox (ReservationCreated)
  COMMIT                            -- single shard, no 2PC
          ↓ CDC
  Kafka → [cache update] [SSE fanout] [waitlist] [notify] [analytics]

READ PATH
  client ETag poll ─► CDN(5s) ─► GW single-flight ─► Redis ─► replicas / OpenSearch
  + SSE push from CDC   (sub-second, advisory, carries `asOf` + `version`)

SAFETY NET
  Reaper (lazy + eager)  ·  Auditor (counter vs COUNT(*) vs ledger)  ·  CHECK constraint
  overbooking_violations_total > 0  →  PAGE

KEY TRADE-OFFS
  CP on writes (503 > over-book)   |   AP on reads (stale count is fine)
  shard by class_id → single-shard txn, no 2PC
  Redis = gate, not truth → wrong in either direction is still safe
  v1 without Redis gate / counter sharding / log-writer → add on measurement
```
