//package DSA_Interview_Questions.CompanyWisePrep.Toast.CodeReview;
//
//
///*
// * ============================================================================================
// *  MenuDataResourceFix: reference solution for MenuDataResource.java (Toast code-review round)
// * ============================================================================================
// *
// *  Issue ids in the comments ([S1], [R3], ...) match MenuDataResourceReview.md.
// *
// *  Classpath: dropwizard-hibernate, jersey (javax.ws.rs 2.1), hibernate-core 5.4,
// *  javax.persistence 2.2, javax.validation 2.0, slf4j. This IntelliJ module has no build
// *  dependencies, so the IDE shows this file in red (the original MenuDataResource.java is red too).
// *  It compiles against the real APIs. The runnable, dependency-free demo of these fixes is in
// *  MenuDataResourceFixDemo.java.
// *
// *  Request flow AFTER the fix
// *  --------------------------
// *  POST /menu-sources  {"type":"URL","location":"https://..."}          [S5] state change = POST
// *    -> auth (@RolesAllowed) + rate limit at the gateway                 [S4]
// *    -> Bean Validation on the body                                      [C4]
// *    -> bulkhead: at most N concurrent fetches, otherwise 503            [R2]
// *    -> MenuSource strategy, with NO DB connection held                  [D1][R3]
// *         URL : http/https only, public IPs only, every redirect re-checked,
// *               connect/read timeouts + total deadline, size cap, stream closed  [S2][R1][R2][R4]
// *         FILE: only inside the configured import directory              [S1]
// *    -> MenuDataService.save(): short transaction, dedupe by SHA-256     [R3][P3]
// *    -> 201 Created + Location header; failures -> 400/413/502/504       [C1][C2]
// *  GET /menu-sources?afterId=&limit=   -> JSON summaries, keyset paging, no blobs     [S3][P1][P2]
// *  GET /menu-sources/{id}/content      -> bytes, stored content type, nosniff, 404    [C3][C9]
// *
// *  Wiring (Dropwizard Application#run):
// *
// *    MenuDataDAO dao = new MenuDataDAO(hibernateBundle.getSessionFactory());
// *    // [R3] the service gets its own @UnitOfWork, so the resource method can fetch with no DB connection held
// *    MenuDataService service = new UnitOfWorkAwareProxyFactory(hibernateBundle)
// *            .create(MenuDataService.class, MenuDataDAO.class, dao);
// *    Map<SourceType, MenuSource> sources = new EnumMap<>(SourceType.class);
// *    sources.put(SourceType.URL,  new UrlMenuSource(SsrfGuard::isPublicAddress, config.getMaxMenuBytes()));
// *    sources.put(SourceType.FILE, new FileMenuSource(config.getImportDir(), config.getMaxMenuBytes()));
// *    env.jersey().register(new MenuSourceResource(sources, service, dao, config.getMaxConcurrentFetches()));
// *    env.jersey().register(new MenuFetchExceptionMapper());
// *    // [S4] env.jersey().register(new AuthDynamicFeature(...)); env.jersey().register(RolesAllowedDynamicFeature.class);
// */
//
//import java.io.ByteArrayOutputStream;
//import java.io.IOException;
//import java.io.InputStream;
//import java.math.BigInteger;
//import java.net.HttpURLConnection;
//import java.net.Inet4Address;
//import java.net.InetAddress;
//import java.net.SocketTimeoutException;
//import java.net.URI;
//import java.net.URISyntaxException;
//import java.net.UnknownHostException;
//import java.nio.file.Files;
//import java.security.MessageDigest;
//import java.security.NoSuchAlgorithmException;
//import java.time.Duration;
//import java.time.Instant;
//import java.util.Collections;
//import java.util.EnumMap;
//import java.util.List;
//import java.util.Locale;
//import java.util.Map;
//import java.util.Optional;
//import java.util.concurrent.Semaphore;
//import java.util.function.Predicate;
//
//import javax.annotation.security.RolesAllowed;
//import javax.persistence.Basic;
//import javax.persistence.Column;
//import javax.persistence.Entity;
//import javax.persistence.FetchType;
//import javax.persistence.GeneratedValue;
//import javax.persistence.GenerationType;
//import javax.persistence.Id;
//import javax.persistence.Lob;
//import javax.persistence.Table;
//import javax.persistence.UniqueConstraint;
//import javax.validation.Valid;
//import javax.validation.constraints.Max;
//import javax.validation.constraints.Min;
//import javax.validation.constraints.NotBlank;
//import javax.validation.constraints.NotNull;
//import javax.validation.constraints.Size;
//import javax.ws.rs.BadRequestException;
//import javax.ws.rs.Consumes;
//import javax.ws.rs.DefaultValue;
//import javax.ws.rs.GET;
//import javax.ws.rs.NotFoundException;
//import javax.ws.rs.POST;
//import javax.ws.rs.Path;
//import javax.ws.rs.PathParam;
//import javax.ws.rs.Produces;
//import javax.ws.rs.QueryParam;
//import javax.ws.rs.ServiceUnavailableException;
//import javax.ws.rs.core.Context;
//import javax.ws.rs.core.MediaType;
//import javax.ws.rs.core.Response;
//import javax.ws.rs.core.UriInfo;
//import javax.ws.rs.ext.ExceptionMapper;
//import javax.ws.rs.ext.Provider;
//
//import org.hibernate.SessionFactory;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import io.dropwizard.hibernate.AbstractDAO;
//import io.dropwizard.hibernate.UnitOfWork;
//
//public final class MenuDataResourceFix {
//
//    private MenuDataResourceFix() {
//    }
//
//    // =========================================================================================
//    //  1. Resource (HTTP layer only: validate, delegate, map to status codes)
//    // =========================================================================================
//
//    @Path("/menu-sources")
//    @Produces(MediaType.APPLICATION_JSON)
//    @RolesAllowed("menu-ingest")                                   // [S4] no anonymous access
//    public static class MenuSourceResource {
//
//        private static final int HTML_PAGE_SIZE = 50;
//
//        private final Map<SourceType, MenuSource> sources;
//        private final MenuDataService service;
//        private final MenuDataDAO dao;
//        private final Semaphore fetchPermits;                      // [R2] bulkhead: caps threads stuck on slow fetches
//
//        public MenuSourceResource(Map<SourceType, MenuSource> sources, MenuDataService service,
//                                  MenuDataDAO dao, int maxConcurrentFetches) {
//            this.sources = new EnumMap<>(sources);
//            this.service = service;
//            this.dao = dao;
//            this.fetchPermits = new Semaphore(maxConcurrentFetches);
//        }
//
//        /**
//         * Fetch a menu and store it. Deliberately NOT annotated with @UnitOfWork [R3]:
//         * the slow network/disk read must not hold a DB connection. Only service.save() opens a
//         * (short) transaction.
//         */
//        @POST                                                      // [S5] was GET
//        @Consumes(MediaType.APPLICATION_JSON)
//        public Response create(@NotNull @Valid CreateMenuSourceRequest request, @Context UriInfo uriInfo) {
//            MenuSource source = sources.get(request.getType());
//            if (source == null) {
//                throw new BadRequestException("unsupported source type " + request.getType());
//            }
//            if (!fetchPermits.tryAcquire()) {
//                throw new ServiceUnavailableException(5L);         // 503 + Retry-After: 5, instead of queueing forever
//            }
//            FetchedContent content;
//            try {
//                content = source.fetch(request.getLocation());     // throws MenuFetchException -> 400/413/502/504 [C1]
//            } finally {
//                fetchPermits.release();
//            }
//            MenuData saved = service.save(request.getLocation(), content);
//            URI location = uriInfo.getAbsolutePathBuilder().path(String.valueOf(saved.getId())).build();
//            return Response.created(location).entity(MenuSummary.of(saved)).build();   // [C2] 201, not "success!"
//        }
//
//        /** JSON list: no hand-built HTML, so no XSS [S3]; keyset pagination, no blobs loaded [P1]. */
//        @GET
//        @UnitOfWork(readOnly = true)                               // [P2]
//        public MenuSummaryPage list(@QueryParam("afterId") @DefaultValue("0") @Min(0) long afterId,
//                                    @QueryParam("limit") @DefaultValue("50") @Min(1) @Max(200) int limit) {
//            List<MenuSummary> items = dao.findSummaries(afterId, limit);
//            Long next = items.size() == limit ? items.get(items.size() - 1).getId() : null;
//            return new MenuSummaryPage(items, next);
//        }
//
//        /** If an HTML page must stay, this is the fixed form of the original list() [S3][C5]. */
//        @GET
//        @Produces(MediaType.TEXT_HTML)
//        @UnitOfWork(readOnly = true)
//        public String listHtml(@QueryParam("afterId") @DefaultValue("0") @Min(0) long afterId,
//                               @Context UriInfo uriInfo) {
//            StringBuilder html = new StringBuilder("<!DOCTYPE html><html><body><ul>");   // <ul>, not <list>
//            for (MenuSummary s : dao.findSummaries(afterId, HTML_PAGE_SIZE)) {
//                URI href = uriInfo.getBaseUriBuilder()                                     // absolute link, not relative "5"
//                        .path(MenuSourceResource.class).path("{id}/content").build(s.getId());
//                html.append("<li>").append(s.getId()).append(" :: <a href=\"")             // quoted attribute
//                        .append(Html.escape(href.toString())).append("\">")
//                        .append(Html.escape(s.getLocator()))                                // escape user-controlled data
//                        .append("</a></li>");
//            }
//            return html.append("</ul></body></html>").toString();
//        }
//
//        @GET
//        @Path("/{id}/content")
//        @Produces(MediaType.WILDCARD)
//        @UnitOfWork(readOnly = true)
//        public Response content(@PathParam("id") long id) {
//            MenuData data = dao.findById(id)
//                    .orElseThrow(() -> new NotFoundException("menu source " + id));   // [C3] 404, not 500
//            return Response.ok(data.getBytes(), data.getContentType())               // [C9] real content type
//                    .header("X-Content-Type-Options", "nosniff")                     // browser must not sniff it as HTML
//                    .header("Content-Disposition", "attachment")
//                    .build();
//            // With blobs in S3 [D2]: return a 302/307 to a short-lived pre-signed URL instead of streaming through us.
//        }
//    }
//
//    // =========================================================================================
//    //  2. API DTOs
//    // =========================================================================================
//
//    public enum SourceType { URL, FILE }
//
//    public static class CreateMenuSourceRequest {                     // [C4] validated before the method runs
//        @NotNull
//        private SourceType type;
//
//        @NotBlank
//        @Size(max = 2048)
//        private String location;
//
//        public SourceType getType() { return type; }
//        public void setType(SourceType type) { this.type = type; }
//        public String getLocation() { return location; }
//        public void setLocation(String location) { this.location = location; }
//    }
//
//    public static class MenuSummary {                                  // what list() needs, and nothing else
//        private final Long id;
//        private final String locator;
//        private final String contentType;
//        private final long sizeBytes;
//        private final Instant fetchedAt;
//
//        public MenuSummary(Long id, String locator, String contentType, long sizeBytes, Instant fetchedAt) {
//            this.id = id;
//            this.locator = locator;
//            this.contentType = contentType;
//            this.sizeBytes = sizeBytes;
//            this.fetchedAt = fetchedAt;
//        }
//
//        static MenuSummary of(MenuData d) {
//            return new MenuSummary(d.getId(), d.getLocator(), d.getContentType(), d.getSizeBytes(), d.getFetchedAt());
//        }
//
//        public Long getId() { return id; }
//        public String getLocator() { return locator; }
//        public String getContentType() { return contentType; }
//        public long getSizeBytes() { return sizeBytes; }
//        public Instant getFetchedAt() { return fetchedAt; }
//    }
//
//    public static class MenuSummaryPage {
//        private final List<MenuSummary> items;
//        private final Long nextAfterId;                                // null = last page
//
//        public MenuSummaryPage(List<MenuSummary> items, Long nextAfterId) {
//            this.items = Collections.unmodifiableList(items);
//            this.nextAfterId = nextAfterId;
//        }
//
//        public List<MenuSummary> getItems() { return items; }
//        public Long getNextAfterId() { return nextAfterId; }
//    }
//
//    // =========================================================================================
//    //  3. Errors: typed exception (HTTP-agnostic) + one mapper   [C1][C2][C3]
//    // =========================================================================================
//
//    public static class MenuFetchException extends RuntimeException {
//        private static final long serialVersionUID = 1L;
//
//        public enum Kind { INVALID_LOCATOR, TOO_LARGE, TIMEOUT, UPSTREAM_FAILED }
//
//        private final Kind kind;
//
//        public MenuFetchException(Kind kind, String message) {
//            this(kind, message, null);
//        }
//
//        public MenuFetchException(Kind kind, String message, Throwable cause) {
//            super(message, cause);
//            this.kind = kind;
//        }
//
//        public Kind getKind() { return kind; }
//    }
//
//    @Provider
//    public static class MenuFetchExceptionMapper implements ExceptionMapper<MenuFetchException> {
//        private static final Logger LOG = LoggerFactory.getLogger(MenuFetchExceptionMapper.class);
//
//        @Override
//        public Response toResponse(MenuFetchException e) {
//            int status;
//            switch (e.getKind()) {
//                case INVALID_LOCATOR: status = 400; break;
//                case TOO_LARGE:       status = 413; break;
//                case TIMEOUT:         status = 504; break;
//                default:              status = 502; break;
//            }
//            // Client mistakes are not server errors: no SEVERE + stack trace that an attacker can use to flood logs.
//            if (status >= 500) {
//                LOG.warn("menu fetch failed: {} {}", e.getKind(), e.getMessage(), e);
//            } else {
//                LOG.info("menu fetch rejected: {} {}", e.getKind(), e.getMessage());
//            }
//            Map<String, String> body = new java.util.LinkedHashMap<>();
//            body.put("error", e.getKind().name());
//            body.put("message", e.getMessage());
//            return Response.status(status).type(MediaType.APPLICATION_JSON).entity(body).build();
//        }
//    }
//
//    // =========================================================================================
//    //  4. Sources: Strategy pattern, replaces the static Reader + switch   [D1]
//    // =========================================================================================
//
//    public interface MenuSource {
//        FetchedContent fetch(String locator);
//    }
//
//    /** URL source: SSRF-safe [S2], no leaks [R1], timeouts [R2], bounded memory [R4]. */
//    public static class UrlMenuSource implements MenuSource {
//        private static final int MAX_REDIRECTS = 3;
//        private static final int CONNECT_TIMEOUT_MS = 2_000;
//        private static final int READ_TIMEOUT_MS = 5_000;              // max gap between two packets
//        private static final Duration TOTAL_DEADLINE = Duration.ofSeconds(15);   // slow-drip protection
//
//        private final Predicate<InetAddress> addressAllowed;            // injectable so tests can allow loopback
//        private final int maxBytes;
//
//        public UrlMenuSource(Predicate<InetAddress> addressAllowed, int maxBytes) {
//            this.addressAllowed = addressAllowed;
//            this.maxBytes = maxBytes;
//        }
//
//        @Override
//        public FetchedContent fetch(String locator) {
//            Instant deadline = Instant.now().plus(TOTAL_DEADLINE);
//            URI uri = SsrfGuard.parseHttpUri(locator);
//            for (int hop = 0; hop <= MAX_REDIRECTS; hop++) {
//                SsrfGuard.checkHost(uri, addressAllowed);               // every hop, not only the first one
//                HttpURLConnection conn = open(uri);
//                try {
//                    int status = conn.getResponseCode();
//                    if (status >= 300 && status < 400) {
//                        String next = conn.getHeaderField("Location");
//                        if (next == null) {
//                            throw new MenuFetchException(MenuFetchException.Kind.UPSTREAM_FAILED, "redirect without Location");
//                        }
//                        uri = SsrfGuard.parseHttpUri(uri.resolve(next).toString());
//                        continue;
//                    }
//                    if (status != 200) {
//                        throw new MenuFetchException(MenuFetchException.Kind.UPSTREAM_FAILED, "upstream returned " + status);
//                    }
//                    long declared = conn.getContentLengthLong();
//                    if (declared > maxBytes) {                          // cheap early reject; the header can lie or be absent
//                        throw new MenuFetchException(MenuFetchException.Kind.TOO_LARGE, "declared " + declared + " bytes");
//                    }
//                    try (InputStream in = conn.getInputStream()) {       // [R1] always closed
//                        byte[] bytes = BoundedRead.readAtMost(in, maxBytes, deadline);   // [R4] enforced while reading
//                        return FetchedContent.of(bytes, contentTypeOr(conn.getContentType()));
//                    }
//                } catch (SocketTimeoutException e) {
//                    throw new MenuFetchException(MenuFetchException.Kind.TIMEOUT, "upstream timed out", e);
//                } catch (IOException e) {
//                    throw new MenuFetchException(MenuFetchException.Kind.UPSTREAM_FAILED, "upstream I/O error", e);
//                } finally {
//                    conn.disconnect();                                  // [R1] release the socket
//                }
//            }
//            throw new MenuFetchException(MenuFetchException.Kind.UPSTREAM_FAILED, "too many redirects");
//        }
//
//        private static HttpURLConnection open(URI uri) {
//            try {
//                HttpURLConnection conn = (HttpURLConnection) uri.toURL().openConnection();
//                conn.setConnectTimeout(CONNECT_TIMEOUT_MS);              // [R2]
//                conn.setReadTimeout(READ_TIMEOUT_MS);                    // [R2]
//                conn.setInstanceFollowRedirects(false);                  // [S2] we follow and re-validate ourselves
//                conn.setRequestProperty("User-Agent", "ToastMenuIngest/1.0");
//                return conn;
//            } catch (IOException e) {
//                throw new MenuFetchException(MenuFetchException.Kind.UPSTREAM_FAILED, "cannot open connection", e);
//            }
//        }
//
//        private static String contentTypeOr(String type) {
//            return type == null ? MediaType.APPLICATION_OCTET_STREAM : type;
//        }
//    }
//
//    /** FILE source: confined to one import directory [S1]. Better still: remove it and accept uploads. */
//    public static class FileMenuSource implements MenuSource {
//        private final java.nio.file.Path baseDir;   // java.nio Path (javax.ws.rs.Path is the annotation)
//        private final int maxBytes;
//
//        public FileMenuSource(java.nio.file.Path importDir, int maxBytes) throws IOException {
//            this.baseDir = importDir.toRealPath();                     // resolve symlinks once, at startup
//            this.maxBytes = maxBytes;
//        }
//
//        @Override
//        public FetchedContent fetch(String locator) {
//            java.nio.file.Path real = PathGuard.resolveInside(baseDir, locator);
//            try (InputStream in = Files.newInputStream(real)) {
//                byte[] bytes = BoundedRead.readAtMost(in, maxBytes, null);
//                String type = Files.probeContentType(real);
//                return FetchedContent.of(bytes, type == null ? MediaType.APPLICATION_OCTET_STREAM : type);
//            } catch (IOException e) {
//                throw new MenuFetchException(MenuFetchException.Kind.INVALID_LOCATOR, "file not readable", e);
//            }
//        }
//    }
//
//    // =========================================================================================
//    //  5. Guards and helpers (pure JDK, the same code runs in MenuDataResourceFixDemo)
//    // =========================================================================================
//
//    static final class SsrfGuard {
//        private SsrfGuard() {
//        }
//
//        /** Only http/https, a host, no user-info, default ports. Rejects file:, jar:, ftp:, gopher:, ... */
//        static URI parseHttpUri(String locator) {
//            URI uri;
//            try {
//                uri = new URI(locator.trim());
//            } catch (URISyntaxException e) {
//                throw invalid("malformed URL");
//            }
//            String scheme = uri.getScheme() == null ? "" : uri.getScheme().toLowerCase(Locale.ROOT);
//            if (!scheme.equals("http") && !scheme.equals("https")) {
//                throw invalid("only http/https allowed");
//            }
//            if (uri.getHost() == null || uri.getRawUserInfo() != null) {
//                throw invalid("host required, user-info not allowed");
//            }
//            int port = uri.getPort();
//            if (port != -1 && port != 80 && port != 443) {
//                throw invalid("port not allowed");
//            }
//            return uri;
//        }
//
//        /** Resolve DNS and check EVERY address. Checking the hostname string is not enough. */
//        static void checkHost(URI uri, Predicate<InetAddress> allowed) {
//            InetAddress[] addresses;
//            try {
//                addresses = InetAddress.getAllByName(uri.getHost());
//            } catch (UnknownHostException e) {
//                throw invalid("unknown host");
//            }
//            for (InetAddress a : addresses) {
//                if (!allowed.test(a)) {
//                    throw invalid("destination not allowed");          // same message for all reasons: no network map
//                }
//            }
//        }
//
//        static boolean isPublicAddress(InetAddress a) {
//            if (a.isAnyLocalAddress()                                  // 0.0.0.0, ::
//                    || a.isLoopbackAddress()                           // 127.0.0.0/8, ::1
//                    || a.isLinkLocalAddress()                          // 169.254.0.0/16 = cloud metadata, fe80::/10
//                    || a.isSiteLocalAddress()                          // 10/8, 172.16/12, 192.168/16
//                    || a.isMulticastAddress()) {
//                return false;
//            }
//            byte[] b = a.getAddress();
//            if (a instanceof Inet4Address) {
//                int b0 = b[0] & 0xff;
//                int b1 = b[1] & 0xff;
//                return b0 != 0 && !(b0 == 100 && b1 >= 64 && b1 <= 127);   // 0/8 and 100.64/10 (carrier-grade NAT)
//            }
//            return (b[0] & 0xfe) != 0xfc;                              // fc00::/7 = IPv6 unique-local ("private")
//        }
//
//        private static MenuFetchException invalid(String msg) {
//            return new MenuFetchException(MenuFetchException.Kind.INVALID_LOCATOR, msg);
//        }
//    }
//
//    static final class PathGuard {
//        private PathGuard() {
//        }
//
//        /**
//         * normalize() removes "..", toRealPath() follows symlinks, and Path.startsWith compares
//         * whole path components ("/data/import-evil" does NOT start with "/data/import").
//         * A missing file and a forbidden file give the same error, so callers can't probe which files exist.
//         */
//        static java.nio.file.Path resolveInside(java.nio.file.Path realBaseDir, String locator) {
//            MenuFetchException denied =
//                    new MenuFetchException(MenuFetchException.Kind.INVALID_LOCATOR, "file not allowed");
//            java.nio.file.Path real;
//            try {
//                real = realBaseDir.resolve(locator).normalize().toRealPath();
//            } catch (IOException | java.nio.file.InvalidPathException e) {
//                throw denied;
//            }
//            if (!real.startsWith(realBaseDir) || !Files.isRegularFile(real)) {
//                throw denied;
//            }
//            return real;
//        }
//    }
//
//    static final class BoundedRead {
//        private BoundedRead() {
//        }
//
//        /**
//         * Reads at most maxBytes and fails fast after that, so memory per request is bounded.
//         * (One-liner alternative without the deadline: in.readNBytes(maxBytes + 1), then check the length.)
//         */
//        static byte[] readAtMost(InputStream in, int maxBytes, Instant deadline) throws IOException {
//            ByteArrayOutputStream out = new ByteArrayOutputStream(Math.min(maxBytes, 64 * 1024));
//            byte[] buf = new byte[16 * 1024];
//            int n;
//            while ((n = in.read(buf)) != -1) {
//                if (out.size() + n > maxBytes) {
//                    throw new MenuFetchException(MenuFetchException.Kind.TOO_LARGE, "more than " + maxBytes + " bytes");
//                }
//                if (deadline != null && Instant.now().isAfter(deadline)) {
//                    throw new MenuFetchException(MenuFetchException.Kind.TIMEOUT, "total deadline exceeded");
//                }
//                out.write(buf, 0, n);
//            }
//            return out.toByteArray();
//        }
//    }
//
//    static final class Html {
//        private Html() {
//        }
//
//        /** Safe for HTML text and quoted attributes. In real code: OWASP Encoder or an auto-escaping template. */
//        static String escape(String s) {
//            StringBuilder out = new StringBuilder(s.length() + 16);
//            for (int i = 0; i < s.length(); i++) {
//                char c = s.charAt(i);
//                switch (c) {
//                    case '&':  out.append("&amp;"); break;
//                    case '<':  out.append("&lt;"); break;
//                    case '>':  out.append("&gt;"); break;
//                    case '"':  out.append("&quot;"); break;
//                    case '\'': out.append("&#x27;"); break;
//                    default:   out.append(c);
//                }
//            }
//            return out.toString();
//        }
//    }
//
//    public static final class FetchedContent {
//        private final byte[] bytes;
//        private final String contentType;
//        private final String sha256;
//
//        private FetchedContent(byte[] bytes, String contentType, String sha256) {
//            this.bytes = bytes;
//            this.contentType = contentType;
//            this.sha256 = sha256;
//        }
//
//        static FetchedContent of(byte[] bytes, String contentType) {
//            return new FetchedContent(bytes, contentType, sha256Hex(bytes));
//        }
//
//        public byte[] getBytes() { return bytes; }
//        public String getContentType() { return contentType; }
//        public String getSha256() { return sha256; }
//
//        private static String sha256Hex(byte[] bytes) {
//            try {
//                byte[] digest = MessageDigest.getInstance("SHA-256").digest(bytes);
//                return String.format("%064x", new BigInteger(1, digest));
//            } catch (NoSuchAlgorithmException e) {
//                throw new IllegalStateException("SHA-256 is always available", e);
//            }
//        }
//    }
//
//    // =========================================================================================
//    //  6. Service: the only place with a write transaction   [R3][P3]
//    // =========================================================================================
//
//    /** Created through UnitOfWorkAwareProxyFactory so @UnitOfWork works outside a resource method. */
//    public static class MenuDataService {
//        private final MenuDataDAO dao;
//
//        public MenuDataService(MenuDataDAO dao) {
//            this.dao = dao;
//        }
//
//        /**
//         * Short transaction: holds a DB connection for milliseconds, after the fetch is done.
//         * Idempotent: the same content returns the existing row. Under a race, the unique constraint
//         * on CONTENT_SHA256 is the real guard (catch the violation and re-read, or use INSERT ... ON CONFLICT).
//         */
//        @UnitOfWork
//        public MenuData save(String locator, FetchedContent content) {
//            return dao.findByContentHash(content.getSha256())
//                    .orElseGet(() -> dao.create(new MenuData(locator, content, Instant.now())));
//        }
//    }
//
//    // =========================================================================================
//    //  7. DAO: typed queries, projection, keyset pagination   [P1][D4]
//    // =========================================================================================
//
//    public static class MenuDataDAO extends AbstractDAO<MenuData> {
//        public MenuDataDAO(SessionFactory factory) {
//            super(factory);
//        }
//
//        public Optional<MenuData> findById(long id) {
//            return Optional.ofNullable(get(id));
//        }
//
//        public MenuData create(MenuData menuData) {
//            return persist(menuData);
//        }
//
//        public Optional<MenuData> findByContentHash(String sha256) {
//            return currentSession()
//                    .createQuery("SELECT d FROM MenuData d WHERE d.contentSha256 = :h", MenuData.class)
//                    .setParameter("h", sha256)
//                    .uniqueResultOptional();
//        }
//
//        /**
//         * Projection: the BYTE_DATA column is never read. Keyset: "WHERE id > :afterId ORDER BY id" uses the
//         * primary-key index, unlike OFFSET, which scans and throws away every skipped row.
//         * (In a real codebase MenuSummary is a top-level class: SELECT new com.toasttab.menu.MenuSummary(...).)
//         */
//        public List<MenuSummary> findSummaries(long afterId, int limit) {
//            return currentSession()
//                    .createQuery("SELECT new " + MenuSummary.class.getName()
//                            + "(d.id, d.locator, d.contentType, d.sizeBytes, d.fetchedAt)"
//                            + " FROM MenuData d WHERE d.id > :afterId ORDER BY d.id", MenuSummary.class)
//                    .setParameter("afterId", afterId)
//                    .setMaxResults(limit)
//                    .list();
//        }
//    }
//
//    // =========================================================================================
//    //  8. Entity   [C6][C7][C8][P3]
//    // =========================================================================================
//
//    @Entity(name = "MenuData")                                     // nested class: default entity name would be "MenuDataResourceFix$MenuData"
//    @Table(name = "MENU_SOURCE",
//            uniqueConstraints = @UniqueConstraint(name = "UK_MENU_SOURCE_SHA256", columnNames = "CONTENT_SHA256"))
//    public static class MenuData {
//
//        @Id
//        @GeneratedValue(strategy = GenerationType.IDENTITY)
//        private Long id;                                           // [C8] Long: null means "not persisted yet"
//
//        @Column(name = "LOCATOR", nullable = false, length = 2048)
//        private String locator;
//
//        @Lob                                                       // [C7] without it: VARBINARY(255) -> truncation/error
//        @Basic(fetch = FetchType.LAZY)                             // only honoured with bytecode enhancement,
//        @Column(name = "BYTE_DATA", nullable = false)              //   which is why list() uses a projection
//        private byte[] bytes;
//
//        @Column(name = "CONTENT_TYPE", nullable = false)
//        private String contentType;
//
//        @Column(name = "SIZE_BYTES", nullable = false)
//        private long sizeBytes;
//
//        @Column(name = "CONTENT_SHA256", nullable = false, length = 64)
//        private String contentSha256;                              // [P3] dedupe key
//
//        @Column(name = "FETCHED_AT", nullable = false)
//        private Instant fetchedAt;
//
//        protected MenuData() {                                     // for Hibernate only
//        }
//
//        /** [C8] the only public constructor builds a complete row: no MenuData(bytes) without a locator. */
//        public MenuData(String locator, FetchedContent content, Instant fetchedAt) {
//            this.locator = locator;
//            this.bytes = content.getBytes();
//            this.contentType = content.getContentType();
//            this.sizeBytes = content.getBytes().length;
//            this.contentSha256 = content.getSha256();
//            this.fetchedAt = fetchedAt;
//        }
//
//        public Long getId() { return id; }
//        public String getLocator() { return locator; }
//        public byte[] getBytes() { return bytes; }
//        public String getContentType() { return contentType; }
//        public long getSizeBytes() { return sizeBytes; }
//        public String getContentSha256() { return contentSha256; }
//        public Instant getFetchedAt() { return fetchedAt; }
//
//        /**
//         * [C6] Identity = database id. Two transient (id == null) objects are equal only if they are the same
//         * instance. Uses getId(), not that.id, because a Hibernate proxy's fields are not populated.
//         */
//        @Override
//        public boolean equals(Object o) {
//            if (this == o) {
//                return true;
//            }
//            if (!(o instanceof MenuData)) {
//                return false;
//            }
//            MenuData that = (MenuData) o;
//            return id != null && id.equals(that.getId());
//        }
//
//        /** Constant, so the hash doesn't change when persist() assigns the id (HashSet stays consistent). */
//        @Override
//        public int hashCode() {
//            return MenuData.class.hashCode();
//        }
//
//        @Override
//        public String toString() {                                 // never print the blob
//            return "MenuData{id=" + id + ", locator='" + locator + "', size=" + sizeBytes + "}";
//        }
//    }
//}
