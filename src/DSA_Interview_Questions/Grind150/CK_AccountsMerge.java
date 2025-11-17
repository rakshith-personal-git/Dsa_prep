package DSA_Interview_Questions.Grind150;

import DSA_Interview_Questions.DisjointSet.DisjointSet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

public class CK_AccountsMerge {
    /**
     * Given a list of accounts where each element accounts[i] is a list of strings, where the first element accounts[i][0] is a name,
     * and the rest of the elements are emails representing emails of the account.
     *
     * Now, we would like to merge these accounts. Two accounts definitely belong to the same person if there is some common email to both accounts.
     * Note that even if two accounts have the same name, they may belong to different people as people could have the same name.
     * A person can have any number of accounts initially, but all of their accounts definitely have the same name.
     *
     * After merging the accounts, return the accounts in the following format: the first element of each account is the name,
     * and the rest of the elements are emails in sorted order. The accounts themselves can be returned in any order.
     *
     * Example 1:
     * Input: accounts = [["John","johnsmith@mail.com","john_newyork@mail.com"],["John","johnsmith@mail.com","john00@mail.com"],["Mary","mary@mail.com"],["John","johnnybravo@mail.com"]]
     * Output: [["John","john00@mail.com","john_newyork@mail.com","johnsmith@mail.com"],["Mary","mary@mail.com"],["John","johnnybravo@mail.com"]]
     * Explanation:
     * The first and second John's are the same person as they have the common email "johnsmith@mail.com".
     * The third John and Mary are different people as none of their email addresses are used by other accounts.
     * We could return these lists in any order, for example the answer [['Mary', 'mary@mail.com'], ['John', 'johnnybravo@mail.com'],
     * ['John', 'john00@mail.com', 'john_newyork@mail.com', 'johnsmith@mail.com']] would still be accepted.
     *
     *  Example 2:
     * Input: accounts = [["Gabe","Gabe0@m.co","Gabe3@m.co","Gabe1@m.co"],["Kevin","Kevin3@m.co","Kevin5@m.co","Kevin0@m.co"],["Ethan","Ethan5@m.co","Ethan4@m.co","Ethan0@m.co"],["Hanzo","Hanzo3@m.co","Hanzo1@m.co","Hanzo0@m.co"],["Fern","Fern5@m.co","Fern1@m.co","Fern0@m.co"]]
     * Output: [["Ethan","Ethan0@m.co","Ethan4@m.co","Ethan5@m.co"],["Gabe","Gabe0@m.co","Gabe1@m.co","Gabe3@m.co"],["Hanzo","Hanzo0@m.co","Hanzo1@m.co","Hanzo3@m.co"],["Kevin","Kevin0@m.co","Kevin3@m.co","Kevin5@m.co"],["Fern","Fern0@m.co","Fern1@m.co","Fern5@m.co"]]
     *
     * Constraints:
     * 1 <= accounts.length <= 1000
     * 2 <= accounts[i].length <= 10
     * 1 <= accounts[i][j].length <= 30
     * accounts[i][0] consists of English letters.
     * accounts[i][j] (for j > 0) is a valid email.
     * */

    public static void main(String[] args) {
        // Test the function with examples
        List<List<String>> accounts1 = Arrays.asList(
                Arrays.asList("John", "johnsmith@mail.com", "john_newyork@mail.com"),
                Arrays.asList("John", "johnsmith@mail.com", "john00@mail.com"),
                Arrays.asList("Mary", "mary@mail.com"),
                Arrays.asList("John", "johnnybravo@mail.com")
        );
        List<List<String>> accounts2 = Arrays.asList(
                Arrays.asList("Gabe", "Gabe0@m.co", "Gabe3@m.co", "Gabe1@m.co"),
                Arrays.asList("Kevin", "Kevin3@m.co", "Kevin5@m.co", "Kevin0@m.co"),
                Arrays.asList("Ethan", "Ethan5@m.co", "Ethan4@m.co", "Ethan0@m.co"),
                Arrays.asList("Hanzo", "Hanzo3@m.co", "Hanzo1@m.co", "Hanzo0@m.co"),
                Arrays.asList("Fern", "Fern5@m.co", "Fern1@m.co", "Fern0@m.co")
        );

        List<List<String>> ans = accountsMerge(accounts1);
        for (List<String> account : ans) {
            System.out.println(account);
        }
        System.out.println();

        ans = accountsMergeUsingDisjoint(accounts1);
        for (List<String> account : ans) {
            System.out.println(account);
        }
        System.out.println();

        ans = accountsMergeUsingDisjointSetApproach(accounts1);
        for (List<String> account : ans) {
            System.out.println(account);
        }
    }


    // approach:
    //1. Create Graph: Construct a graph where each email is a node, and if two emails are in the same account, create an edge between them.
    //2. DFS or BFS: Traverse the graph to find connected components. For each connected component, collect all emails belonging to that component.
    //3. Merge Accounts: Merge the emails of each connected component into one account, sorting them alphabetically.
    //4. Return Result: Return the merged accounts.
    private static List<List<String>> accountsMerge(List<List<String>> accounts) {
        Map<String, String> emailToNameMap = new HashMap<>();
        Map<String, List<String>> graph = new HashMap<>();

        //creating the graph
        for(List<String> account : accounts) {
            String name = account.get(0);
            for (int i = 1; i < account.size(); i++) {
                String email = account.get(i);
                emailToNameMap.put(email, name);
                graph.putIfAbsent(email, new ArrayList<>());
                if (i == 1) {
                    continue; //skipping adding edge between name and first email since this is the 1st node for this name
                }
                graph.get(email).add(account.get(1)); // Adding edge between emails
                graph.get(account.get(1)).add(email);
            }
        }

        //DFS to find connected components
        Set<String> visited = new HashSet<>();
        List<List<String>> mergedAccounts = new ArrayList<>();
        for (String email : graph.keySet()) {
            if (!visited.contains(email)){
                List<String> component = new ArrayList<>();
                dfs(email, graph, visited, component);
                Collections.sort(component); // Sort emails within each merged account
                component.add(0, emailToNameMap.get(email)); // Adding name at the beginning
                mergedAccounts.add(component);
            }
        }
        return mergedAccounts;

    }

    private static void dfs(String email, Map<String, List<String>> graph, Set<String> visited, List<String> component) {
        if (visited.contains(email)) {
            return;
        }
        visited.add(email);
        component.add(email);
        List<String> neighbours = graph.getOrDefault(email, new ArrayList<>());
        for (String neighbour: neighbours){
            dfs(neighbour, graph, visited, component);
        }
    }

    //Time Complexity (TC):
    //Graph Construction: Constructing the graph involves iterating through each account and adding edges between emails, which takes O(N * M) time, where N is the number of accounts and M is the average number of emails per account.
    //DFS Traversal: Traversing the graph to find connected components using DFS takes O(V + E) time, where V is the number of vertices (emails) and E is the number of edges in the graph. In the worst case, every email could be connected to every other email, so E could be as large as O(N * M^2).
    //Sorting: Sorting the emails within each connected component takes O(M log M) time for each component, where M is the number of emails in that component.
    //Overall, the time complexity can be approximated as O(N * M^2 + M log M), where N is the number of accounts and M is the average number of emails per account.
    //
    //Space Complexity (SC):
    //Graph and Maps: The space required to store the graph and maps is proportional to the number of emails and accounts, so it's O(N * M).
    //Visited Set and Components: The space required for the visited set and each connected component during DFS traversal is also proportional to the number of emails and accounts, so it's also O(N * M).
    //Overall, the space complexity is O(N * M).



//    -------------------------------
    //this is using disjoint set class....
    private static List<List<String>> accountsMergeUsingDisjoint(List<List<String>> accounts) {
        int n = accounts.size();
        DisjointSet ds = new DisjointSet(n);
        HashMap<String, Integer> mapMailNode = new HashMap<>();

        for (int i = 0; i < n; i++) {
            for (int j = 1; j < accounts.get(i).size(); j++) {
                String mail = accounts.get(i).get(j);
                if (!mapMailNode.containsKey(mail)) {
                    mapMailNode.put(mail, i);
                } else {
                    ds.unionBySize(i, mapMailNode.get(mail));
                }
            }
        }

        ArrayList<String>[] mergedMail = new ArrayList[n];

        for (int i = 0; i < n; i++){
            mergedMail[i] = new ArrayList<>();
        }
        for (Map.Entry<String, Integer> it : mapMailNode.entrySet()) {
            String mail = it.getKey();
            int node = ds.findUPar(it.getValue());
            mergedMail[node].add(mail);
        }

        List<List<String>> ans = new ArrayList<>();

        for (int i = 0; i < n; i++) {
            if (mergedMail[i].size() == 0) continue;
            Collections.sort(mergedMail[i]);
            List<String> temp = new ArrayList<>();
            temp.add(accounts.get(i).get(0));
            for (String it : mergedMail[i]) {
                temp.add(it);
            }
            ans.add(temp);
        }
        return ans;
    }

//    ---------------------------------------------------
    //this is using disjoint set approach, but not the class already created in the project
    private static List<List<String>> accountsMergeUsingDisjointSetApproach(List<List<String>> accounts) {
        Map<String, String> parent = new HashMap<>();  // email -> parent email
        Map<String, String> name = new HashMap<>();    // email -> name
        Map<String, TreeSet<String>> unions = new HashMap<>(); // parent email -> set of emails

        // Initialize the union-find structure
        for (List<String> account : accounts) {
            String accountName = account.get(0);
            for (int i = 1; i < account.size(); i++) {
                String email = account.get(i);
                parent.put(email, email);  // Initially, each email is its own parent
                name.put(email, accountName);  // Map email to the person's name
            }
        }

        // Union all emails in the same account
        for (List<String> account : accounts) {
            String firstEmail = account.get(1);
            for (int i = 2; i < account.size(); i++) {
                union(parent, firstEmail, account.get(i));  // Union all emails in the same account
            }
        }

        // Find the root for each email and group them
        for (List<String> account : accounts) {
            String firstEmail = find(parent, account.get(1));
            if (!unions.containsKey(firstEmail)) {
                unions.put(firstEmail, new TreeSet<>());
            }
            for (int i = 1; i < account.size(); i++) {
                unions.get(firstEmail).add(account.get(i));  // Add email to the correct group
            }
        }

        // Prepare the final result
        List<List<String>> result = new ArrayList<>();
        for (String root : unions.keySet()) {
            List<String> emails = new ArrayList<>(unions.get(root));
            emails.add(0, name.get(root));  // Add the name at the beginning
            result.add(emails);
        }

        return result;
    }

    // Find operation with path compression
    private static String find(Map<String, String> parent, String s) {
        if (!s.equals(parent.get(s))) {
            parent.put(s, find(parent, parent.get(s)));  // Path compression
        }
        return parent.get(s);
    }

    // Union operation
    private static void union(Map<String, String> parent, String s1, String s2) {
        String root1 = find(parent, s1);
        String root2 = find(parent, s2);
        if (!root1.equals(root2)) {
            parent.put(root1, root2);  // Union by making one root point to the other
        }
    }
}
