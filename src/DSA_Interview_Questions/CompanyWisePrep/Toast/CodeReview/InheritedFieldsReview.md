# Toast Code Review — InheritedFields Cheat Sheet

**Problem:** A menu is a tree: `Menu → MenuGroup → (nested MenuGroups) → MenuItem`.
An item with `price == null` **inherits** the price of the **nearest ancestor** that has one
(item → its group → parent group → … → menu). `getPrice(menus, item)` should return that effective price.

> Use the same approach as in `MenuDataResourceReview.md`: understand intent → trace with real data → report by severity → root cause, impact, fix.

---

## 1. Open with this: it's wrong for every input (verified by running it)

`main` prints **`0.0`** for "Wings" (expected `5.00`). Calling `getPrice` for **every** item returns `0.0`, including an item that doesn't exist ("Pizza").

| Item | Expected | Actual |
|---|---|---|
| Wings | 5.00 (Appetizers) | 0.0 |
| Fries | 3.50 (own) | 0.0 |
| Burger | **11.50 (Lunch menu)** | 0.0 |
| Garden Salad | 7.50 (Salads) | 0.0 |
| Cobb Salad w/ Bacon | 9.00 (own) | 0.0 |
| Avocado / PB&J | 6.50 (Toast) | 0.0 |
| Iced Latte | 6.50 (own) | 0.0 |
| Decaf Iced Coffee | 5.00 (Iced) | 0.0 |
| Americano / Cup of Joe | 3.00 (Hot) | 0.0 |
| Coke / Ginger Ale | 2.50 (Sodas) | 0.0 |
| Pizza (not on any menu) | null / not found | 0.0 |

**Why:** In the first group ("Appetizers") `groups` is empty, so `price1` keeps its initial value `0.0`, which is **not null**. The check `price1 != null` (L71) is true, so it returns `0.0`. And it **always returns inside the first iteration**, so nothing else is ever searched.

---

## 2. How to walk it (order)

1. **Data model** (L14–48): understand the tree and the "nullable price" rule. Ask the clarifying questions in §5.
2. **Entry point** `getPrice` (L50): trace "Wings" by hand, which immediately exposes the 0.0 bug.
3. **Recursion** `parseMenuGroup` (L83): trace a nested item ("Garden Salad", "Burger").
4. **Leaf** `parseItems` (L107): matching logic.
5. **main / static data** (L122–212): test setup and the missing tests.
6. Then the cross-cutting issues: types, nulls, naming, design, performance.

---

## 3. Issues

### 🔴 Functional bugs
| # | Where | Root cause | Impact | Fix |
|---|---|---|---|---|
| 1 | L59–60, L85–86 | `price1/price2` start at `0.0` instead of `null`, so "not searched" looks like "found, price 0" | Returns **0.0 for everything** | Start with `null`, or better, return as soon as the item is found |
| 2 | L69–75 | The `return` is inside the loop and runs unconditionally after the first group | Only the **first group of the first menu** is ever searched | Return only when found; otherwise continue to the next group / menu |
| 3 | L70 | Not found → `0.0`, but L80 / L99 / L119 → `null` | Inconsistent contract; "free" and "missing" look the same | Pick one: `Optional<BigDecimal>` or a not-found exception |
| 4 | L63, L66 | **Menu-level price is never used**: `objMenuGroup.price` is passed even when null; `objMenu.price` is ignored | Burger (Entrees has no price) should be **11.50** but would get null | Effective price = `group.price ?? menu.price` |
| 5 | L87–96 | The loop **overwrites** `price1/price2` for each sibling and never stops when found | "Garden Salad" is found in Salads (7.50), then the Toast sibling overwrites it with `null` | Return as soon as a non-null result is found |
| 6 | L100 | `price1 != 0.0` used as "not found" | A legitimately **free item (0.00)** is treated as missing | Use null or Optional for "not found", never a magic value |
| 7 | L110 | `obj.name == item.name` compares **references** | Works only because the literals are interned; names from a DB, JSON or user input never match | `obj.name.equals(item.name)`, or better, match by a unique **id / GUID** |
| 8 | L62, L65, L90, L93 | `.isEmpty()` on lists that can be null; `menus` and `item` are never null-checked | NPE on real data | Treat null as empty (`nullSafe(list)`), or require non-null lists in the constructor; `Objects.requireNonNull(item)` |
| 9 | Search order | Subgroups are searched **before** the group's own items | Differs from the expected "own items first" if the same name exists at both levels | Check the group's own items, then recurse |
| 10 | Ambiguity | Matching by name: the same item can be in several menus / groups at different prices | Silently returns whichever is found first | Ask what should happen; match by id or return all matches |

### 🟠 Correctness of types / data
- **`Double` for money:** floating-point error (`0.1 + 0.2 != 0.3`). Use `BigDecimal` or `long` cents.
- **Boxed `Double` + `==`/`!=`:** `price1 != 0.0` unboxes and can throw NPE; comparing two `Double`s with `==` compares references.
- **The `MenuItem item` parameter** is used only for its `name`; its own `price` is ignored. Clarify: should a passed-in price override?

### 🟡 Performance
- Each lookup is **O(N)** over the whole tree, and it doesn't short-circuit once found.
- Many lookups (e.g. pricing a whole order) → O(N × K). **Fix:** one DFS that builds `Map<id, effectivePrice>` (O(N)), then each lookup is O(1). Rebuild or invalidate when the menu changes.
- Recursion depth = nesting depth. Fine for menus, but mention an iterative stack if nesting is unbounded or the data is untrusted (cycles → `StackOverflowError`; use a visited set).

### 🟡 Design / clean code
- **Duplicated logic:** `getPrice`'s inner loop is a copy of `parseMenuGroup`. Treat the menu as the root and call one recursive function.
- **Misleading names:** `parseMenuGroup` / `parseItems` don't parse, they *find*. `price1`, `price2`, `objMenu`, `obj` say nothing. `curr_price` is snake_case. `String args[]` is C-style.
- **Public mutable fields** and `Arrays.asList` (fixed size but mutable) → make them `private final` with getters, and `List.copyOf`.
- **Leftovers:** wildcard and unused imports (`java.io.*`, `java.text.*`, `java.math.*`, `java.util.regex.*`), commented-out code (L56–57), inconsistent indentation in `main`.
- **Static mutable globals** (`LUNCH_MENU`, `DRINK_MENU`) used as test data. Move them into test fixtures.
- **No tests:** `main` only prints one value with no assertion. Add a parameterized JUnit test covering the table in §1, plus: not found, free item, null lists, duplicate names, a deep nest that falls back to the menu price.

---

## 4. Corrected version (verified: every row in §1 now matches "Expected")

```java
public static Double getPrice(Collection<Menu> menus, MenuItem target) {
    Objects.requireNonNull(target, "target");
    for (Menu menu : nullSafe(menus)) {
        Double price = find(menu.groups, menu.price, target.name);   // menu price is the root default
        if (price != null) return price;
    }
    return null;                                                       // not found
}

private static Double find(List<MenuGroup> groups, Double inherited, String name) {
    for (MenuGroup group : nullSafe(groups)) {
        Double effective = group.price != null ? group.price : inherited;
        for (MenuItem item : nullSafe(group.items)) {                 // own items first
            if (item.name.equals(name)) {
                return item.price != null ? item.price : effective;
            }
        }
        Double found = find(group.groups, effective, name);           // then nested groups
        if (found != null) return found;                               // short-circuit
    }
    return null;
}

private static <T> List<T> nullSafe(Collection<T> c) {
    return c == null ? List.of() : new ArrayList<>(c);
}
```

**Even better (say this out loud):** return `Optional<BigDecimal>`, match on `item.id`, and to price many items build the map once:

```java
Map<String, BigDecimal> effectivePrices(Collection<Menu> menus) {
    Map<String, BigDecimal> out = new HashMap<>();
    for (Menu m : menus) walk(m.groups, m.price, out);
    return out;
}
void walk(List<MenuGroup> groups, BigDecimal inherited, Map<String, BigDecimal> out) {
    for (MenuGroup g : groups) {
        BigDecimal eff = g.price != null ? g.price : inherited;
        for (MenuItem i : g.items) out.putIfAbsent(i.id, i.price != null ? i.price : eff);
        walk(g.groups, eff, out);
    }
}
```

---

## 5. Clarifying questions to ask the interviewer
1. If no ancestor has a price (all null), return null, 0, or throw?
2. The same item in multiple menus or groups: first match, or should the menu be passed in?
3. Is there a unique item id, or must we match by name (case-sensitive? trimmed?)?
4. Does the `price` on the passed-in `MenuItem` mean anything?
5. One lookup or many? How often does the menu change? (Decides between on-demand search and a precomputed map.)
6. Can the data contain cycles or very deep nesting?

---

## 6. 30-second summary (memorize)
1. **Always returns 0.0**: the `0.0` sentinel plus an unconditional return in the first loop iteration.
2. **Wrong inheritance**: the menu price is ignored, siblings overwrite found results, and `0.0` is treated as "not found".
3. **`==` on Strings**, NPE on null lists, `Double` for money, inconsistent null / 0.0 contract.
4. **Fix**: one recursive DFS passing the effective inherited price, returning on the first match, `equals`/id matching, `Optional<BigDecimal>`.
5. **Scale**: precompute `Map<id, price>` in O(N) for O(1) lookups. Clean up naming, duplication and imports, and add real tests.
