package DSA_Interview_Questions.CompanyWisePrep.Toast;

import java.util.*;

import java.util.*;

public class ToastMenuSearch {

    // --- Data Model Classes ---

    public static class MenuItem {
        public String name;
        public Double price; // Nullable

        public MenuItem(String name, Double price) {
            this.name = name;
            this.price = price;
        }
    }

    public static class MenuGroup {
        public String name;
        public Double price; // Nullable
        public List<MenuGroup> groups;
        public List<MenuItem> items;

        public MenuGroup(String name, Double price, List<MenuItem> items, List<MenuGroup> groups) {
            this.name = name;
            this.price = price;
            this.items = items != null ? items : Collections.emptyList();
            this.groups = groups != null ? groups : Collections.emptyList();
        }
    }

    public static class Menu {
        public String name;
        public Double price; // Nullable
        public List<MenuGroup> groups;

        public Menu(String name, Double price, List<MenuGroup> groups) {
            this.name = name;
            this.price = price;
            this.groups = groups != null ? groups : Collections.emptyList();
        }
    }

    public static class ItemSearchResult {
        public String itemName;
        public Double resolvedPrice;

        public ItemSearchResult(String itemName, Double resolvedPrice) {
            this.itemName = itemName;
            this.resolvedPrice = resolvedPrice;
        }

        @Override
        public String toString() {
            return "ItemSearchResult{name='" + itemName + "', price=" + resolvedPrice + '}';
        }
    }

    // ==========================================
    // APPROACH 1: On-Demand Recursive DFS Search
    // Time: O(N), Space: O(H) call stack
    // ==========================================

    public static Optional<ItemSearchResult> findItemAndPrice(List<Menu> menus, String targetItemName) {
        if (menus == null || targetItemName == null) {
            return Optional.empty();
        }

        for (Menu menu : menus) {
            // Determine effective price starting at Menu level
            Double effectivePrice = menu.price;
            ItemSearchResult result = searchGroupList(menu.groups, effectivePrice, targetItemName);
            if (result != null) {
                return Optional.of(result);
            }
        }

        return Optional.empty(); // Not found in any menu
    }

    private static ItemSearchResult searchGroupList(List<MenuGroup> groups, Double parentPrice, String target) {
        if (groups == null) return null;

        for (MenuGroup group : groups) {
            // Inheritance: Group price overrides parent price if present
            Double effectivePrice = group.price != null ? group.price : parentPrice;

            // Check items in this group
            if (group.items != null) {
                for (MenuItem item : group.items) {
                    if (item.name.equals(target)) {
                        // Item price overrides group price if present; fallback to parent price, default 0.0
                        Double finalPrice = item.price != null ? item.price : (effectivePrice != null ? effectivePrice : 0.0);
                        return new ItemSearchResult(item.name, finalPrice);
                    }
                }
            }

            // Recursively search nested subgroups (arbitrary depth)
            ItemSearchResult nestedResult = searchGroupList(group.groups, effectivePrice, target);
            if (nestedResult != null) {
                return nestedResult;
            }
        }

        return null;
    }

    // ==========================================
    // APPROACH 2: Pre-Indexed Map Service (SSE Design)
    // Build: O(N) time & space | Lookup: O(1) time
    // ==========================================

    public static class MenuCatalogService {
        private final Map<String, Double> itemPriceIndex = new HashMap<>();

        public MenuCatalogService(List<Menu> menus) {
            if (menus != null) {
                for (Menu menu : menus) {
                    buildIndex(menu.groups, menu.price);
                }
            }
        }

        private void buildIndex(List<MenuGroup> groups, Double parentPrice) {
            if (groups == null) return;

            for (MenuGroup group : groups) {
                Double effectivePrice = group.price != null ? group.price : parentPrice;

                if (group.items != null) {
                    for (MenuItem item : group.items) {
                        Double finalPrice = item.price != null ? item.price : (effectivePrice != null ? effectivePrice : 0.0);
                        itemPriceIndex.put(item.name, finalPrice);
                    }
                }

                // Recursive pass for nested groups
                buildIndex(group.groups, effectivePrice);
            }
        }

        public ItemSearchResult getItemPrice(String itemName) {
            if (!itemPriceIndex.containsKey(itemName)) {
                return null;
            }
            return new ItemSearchResult(itemName, itemPriceIndex.get(itemName));
        }
    }

    // --- Main Method with Test Cases ---

    public static void main(String[] args) {
        MenuGroup appetizers = new MenuGroup("Appetizers", 5.00,
                Arrays.asList(new MenuItem("Wings", null), new MenuItem("Fries", 3.50)), Collections.emptyList());

        MenuGroup chefSpecials = new MenuGroup("Chef Specials", null,
                Arrays.asList(new MenuItem("Truffle Pasta", null)), Collections.emptyList());

        MenuGroup specials = new MenuGroup("Specials", null,
                Collections.emptyList(), Arrays.asList(chefSpecials));

        MenuGroup budgetItems = new MenuGroup("Budget Items", null,
                Arrays.asList(new MenuItem("Water", null)), Collections.emptyList());

        Menu lunchMenu = new Menu("Lunch Menu", 12.00, Arrays.asList(appetizers, specials));
        Menu drinksMenu = new Menu("Drinks Menu", null, Arrays.asList(budgetItems));
        List<Menu> allMenus = Arrays.asList(lunchMenu, drinksMenu);

        // System Tests
        System.out.println("--- Testing Approach 1: Recursive DFS ---");
        runTest("Test 1 (Explicit Item Price)", allMenus, "Fries", 3.50);
        runTest("Test 2 (Inherit Immediate Group Price)", allMenus, "Wings", 5.00);
        runTest("Test 3 (Inherit Top-Level Menu Price)", allMenus, "Truffle Pasta", 12.00);
        runTest("Test 4 (Default 0.0)", allMenus, "Water", 0.00);
        runTest("Test 5 (Item Not Found)", allMenus, "Burger", null);

        System.out.println("\n--- Testing Approach 2: O(1) Catalog Service ---");
        MenuCatalogService catalog = new MenuCatalogService(allMenus);
        System.out.println("O(1) Lookup 'Wings': " + catalog.getItemPrice("Wings"));
        System.out.println("O(1) Lookup 'Truffle Pasta': " + catalog.getItemPrice("Truffle Pasta"));
    }

    private static void runTest(String testName, List<Menu> menus, String targetItem, Double expectedPrice) {
        ItemSearchResult result = findItemAndPrice(menus, targetItem).orElse(null);
        if (expectedPrice == null) {
            if (result == null) {
                System.out.println(testName + " PASSED");
            } else {
                System.out.println(testName + " FAILED -> Expected null, Got: " + result);
            }
        } else {
            if (result != null && Math.abs(result.resolvedPrice - expectedPrice) < 0.0001) {
                System.out.println(testName + " PASSED (" + result + ")");
            } else {
                System.out.println(testName + " FAILED -> Expected: " + expectedPrice + ", Got: " + result);
            }
        }
    }
}