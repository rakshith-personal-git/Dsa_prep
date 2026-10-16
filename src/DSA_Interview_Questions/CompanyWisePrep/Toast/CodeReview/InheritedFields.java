package DSA_Interview_Questions.CompanyWisePrep.Toast.CodeReview;

import static java.util.Arrays.asList;
import static java.util.Collections.emptyList;

import java.io.*;
import java.util.*;
import java.text.*;
import java.math.*;
import java.util.regex.*;

public class InheritedFields {

    static class Menu {
        public String name;
        public List<MenuGroup> groups;
        public Double price; // nullable

        public Menu(String name,  Double price, List<MenuGroup> groups) {
            this.name = name;
            this.price = price;
            this.groups = groups;
        }
    }

    static class MenuGroup {
        public String name;
        public List<MenuGroup> groups;
        public List<MenuItem> items;
        public Double price; // nullable

        public MenuGroup(String name, Double price, List<MenuItem> items, List<MenuGroup> groups) {
            this.name = name;
            this.price = price;
            this.items = items;
            this.groups = groups;
        }
    }

    static class MenuItem {
        public String name;
        public Double price; // nullable

        public MenuItem(String name, Double price) {
            this.name = name;
            this.price = price;
        }
    }

    public static Double getPrice(Collection<Menu> menus, MenuItem item) {
        
        for(Menu objMenu: menus) {
            
            for(MenuGroup objMenuGroup: objMenu.groups){
                
                // List<MenuGroup> groups;
                // List<MenuItem> items;
                
                Double price1 = 0.0;
                Double price2 = 0.0;
                
                if(!objMenuGroup.groups.isEmpty()) {
                    price1 = parseMenuGroup(objMenuGroup.groups, objMenuGroup.price, item);
                }
                if(!objMenuGroup.items.isEmpty()) {
                    price2 = parseItems(objMenuGroup.items, objMenuGroup.price, item);
                }
                
                if(price1 == null && price2 == null) {
                    return 0.0;
                } else if(price1 != null){
                    return price1;
                } else {
                    return price2;
                }
            }
            
        }
        
        return null;
    }
    
    public static Double parseMenuGroup(List<MenuGroup> menugroups, Double price, MenuItem item) {
        
        Double price1 = 0.0;
        Double price2 = 0.0;
        for (MenuGroup obj: menugroups) {
            
            Double curr_price = obj.price==null ? price : obj.price;
            if(!obj.groups.isEmpty()) {
                    price1 = parseMenuGroup(obj.groups, curr_price, item);
            }
            if(!obj.items.isEmpty()) {
                price2 = parseItems(obj.items, curr_price, item);
            }
        }
        
        if(price1 == null && price2 == null) {
            return null;
        } else if(price1 != null && price1 != 0.0){
            return price1;
        } else {
            return price2;
        }
    }
    
    public static Double parseItems(List<MenuItem> items, Double price, MenuItem item) {
        
        for(MenuItem obj: items) {
            if (obj.name == item.name) {
                if(obj.price == null) {
                    return price;
                } else {
                    return obj.price;
                }
            }
        }
        
        return null;
    }
    
    static final Menu LUNCH_MENU, DRINK_MENU;

    public static void main(String args[] ) {
    
    List<Menu> menuList = new ArrayList<>();
    menuList.add(LUNCH_MENU);
    menuList.add(DRINK_MENU);
    
    MenuItem test = new MenuItem("Wings", null);
    
    System.out.println(getPrice(menuList, test));
       
    }


    static {
        // Initialize LUNCH_MENU + DRINK_MENU in a block so that it's collapsible in IDE
        LUNCH_MENU = new Menu(
            "Lunch",
            11.50,
            asList(
                new MenuGroup(
                    "Appetizers",
                    5.00,
                    asList(new MenuItem("Wings", null), new MenuItem("Fries", 3.50)),
                    emptyList()
                ),
                new MenuGroup(
                    "Entrees",
                    null,
                    asList(new MenuItem("Burger", null)),
                    asList(
                        new MenuGroup(
                            "Salads",
                            7.50,
                            asList(
                                new MenuItem("Garden Salad", null),
                                new MenuItem("Cobb Salad w/ Bacon", 9.00)),
                            emptyList()
                        ),
                        new MenuGroup(
                            "Toast",
                            6.50,
                            asList(
                                new MenuItem("Avocado", null),
                                new MenuItem("PB&J", null)),
                            emptyList()
                        )
                    )
                )
            )
        );
        
        DRINK_MENU = new Menu(
            "Drinks",
            5.00,
            asList(
                new MenuGroup(
                    "Coffees",
                    null,
                    emptyList(),
                    asList(
                        new MenuGroup(
                            "Iced",
                            5.00,
                            asList(
                                new MenuItem("Iced Latte", 6.50),
                                new MenuItem("Decaf Iced Coffee", null)),
                            emptyList()
                        ),
                        new MenuGroup(
                            "Hot",
                            3.00,
                            asList(
                                new MenuItem("Americano", null),
                                new MenuItem("Cup of Joe", null)
                            ),
                            emptyList()
                        )
                    )
                ),
                new MenuGroup(
                    "Sodas",
                    2.50,
                    asList(new MenuItem("Coke", null), new MenuItem("Sprite", null),
                            new MenuItem("Ginger Ale", null)),
                    emptyList()
                )
            )
        );
    }
}