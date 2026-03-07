package DSA_Interview_Questions.Grind150;

/**
 You are given a list of Employee objects, where each employee has fields like id, name, city, etc.

 Given a target city, write a function to return how many employees belong to that city.

 1. Now assume the list is very large. Design and implement an efficient multithreaded solution to compute the count of employees for a given city. Discuss correctness and potential performance benefits.

 2. How would you generalize this to compute the number of employees in each city?
 */
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.*;



public class EB_EmpPerCity {

    /**
     * Demo for counting employees by city using
     * single-threaded and multi-threaded approaches.
     */
    static class Employee {
        int id;
        String name;
        String city;

        Employee(int id, String name, String city) {
            this.id = id;
            this.name = name;
            this.city = city;
        }

        String getCity() {
            return city;
        }

        @Override
        public String toString() {
            return "Employee{id=" + id + ", name='" + name + "', city='" + city + "'}";
        }
    }

    /**
     * Single-threaded simple count.
     *
     * Logic:
     *  - Iterate through all employees once and increment count when city matches.
     *
     * Time Complexity: O(n), where n = number of employees.
     * Space Complexity: O(1) extra space.
     */
    static int countEmployeesInCity(List<Employee> employees, String targetCity) {
        int count = 0;
        for (Employee e : employees) {
            if (e.getCity().equals(targetCity)) {
                count++;
            }
        }
        return count;
    }

    /**
     * Precompute city -> count map (useful when you have many queries).
     *
     * Logic:
     *  - Iterate once and maintain a frequency map of city to count.
     *
     * Time Complexity: O(n), where n = number of employees.
     * Space Complexity: O(k), where k = number of distinct cities.
     */
    static Map<String, Integer> buildCityCountMap(List<Employee> employees) {
        Map<String, Integer> map = new HashMap<>();
        for (Employee e : employees) {
            map.merge(e.getCity(), 1, Integer::sum);
        }
        return map;
    }


    /**
     * Count employees in a given city using Java Streams.
     *
     * Logic:
     *  - Create a stream over the list, filter by city, count the matches.
     *
     * Time Complexity: O(n), where n = number of employees.
     * Space Complexity: O(1) extra space (ignoring stream framework overhead).
     */
    static long countEmployeesInCityStream(List<Employee> employees, String targetCity) {
        return employees.stream()
                .filter(e -> e.getCity().equals(targetCity))
                .count();
    }

    /**
     * Count employees in a given city using Java parallelStream.
     *
     * Logic:
     *  - Similar to streams version but processed in parallel by fork-join pool.
     *
     * Time Complexity: O(n) total work, ~O(n / p) wall-clock for p cores (minus overhead).
     * Space Complexity: O(1) extra per thread (ignoring framework overhead).
     */
    static long countEmployeesInCityParallelStream(List<Employee> employees, String targetCity) {
        return employees.parallelStream()
                .filter(e -> e.getCity().equals(targetCity))
                .count();
    }

    /**
     * Multithreaded count using ExecutorService.
     *
     * Logic:
     *  - Split the list into chunks.
     *  - Each thread counts matches in its chunk using a local variable.
     *  - Aggregate partial counts at the end.
     *
     * Time Complexity:
     *  - Total work: O(n)
     *  - Wall-clock time: roughly O(n / p) for p threads (minus overhead).
     *
     * Space Complexity:
     *  - O(p) extra space for partial results and task list.
     */
    static int countEmployeesInCityParallel(List<Employee> employees,
                                            String targetCity,
                                            int numThreads) throws InterruptedException {
        int n = employees.size();
        if (n == 0 || numThreads <= 1) {
            // Fall back to single-threaded
            return countEmployeesInCity(employees, targetCity);
        }

        ExecutorService pool = Executors.newFixedThreadPool(numThreads);
        List<Future<Integer>> futures = new ArrayList<>();

        int chunkSize = (n + numThreads - 1) / numThreads; // ceil(n / numThreads)

        for (int i = 0; i < n; i += chunkSize) {
            int from = i;
            int to = Math.min(i + chunkSize, n);

            Callable<Integer> task = () -> {
                int localCount = 0;
                for (int j = from; j < to; j++) {
                    Employee e = employees.get(j);
                    if (e.getCity().equals(targetCity)) {
                        localCount++;
                    }
                }
                return localCount;
            };

            futures.add(pool.submit(task));
        }

        int total = 0;
        for (Future<Integer> f : futures) {
            try {
                total += f.get();
            } catch (ExecutionException e) {
                pool.shutdownNow();
                throw new RuntimeException(e);
            }
        }

        pool.shutdown();
        return total;
    }

    public static void main(String[] args) throws Exception {
        List<Employee> employees = Arrays.asList(
                new Employee(1, "Alice", "Chennai"),
                new Employee(2, "Bob", "Bangalore"),
                new Employee(3, "Charlie", "Chennai"),
                new Employee(4, "David", "Mumbai"),
                new Employee(5, "Eve", "Chennai"),
                new Employee(6, "Frank", "Bangalore"),
                new Employee(7, "Grace", "Delhi"),
                new Employee(8, "Heidi", "Chennai")
        );

        String targetCity = "Chennai";

        System.out.println("Employees list:");
        for (Employee e : employees) {
            System.out.println("  " + e);
        }
        System.out.println();

        // 1) Single-threaded count
        int singleCount = countEmployeesInCity(employees, targetCity);
        System.out.println("Single-threaded count for " + targetCity + ": " + singleCount);

        // 2) Precomputed map approach
        Map<String, Integer> cityMap = buildCityCountMap(employees);
        System.out.println("City -> count map: " + cityMap);
        System.out.println("Count from map for " + targetCity + ": " + cityMap.getOrDefault(targetCity, 0));

        // 3) Multithreaded manual ExecutorService
        int numThreads = 3;
        int parallelCount = countEmployeesInCityParallel(employees, targetCity, numThreads);
        System.out.println("Parallel (ExecutorService) count for " + targetCity
                + " with " + numThreads + " threads: " + parallelCount);

        // 4) Java 8 Stream (sequential)
        long streamCount = countEmployeesInCityStream(employees, targetCity);
        System.out.println("Stream (sequential) count for " + targetCity + ": " + streamCount);

        // 5) Java 8 parallelStream
        long parallelStreamCount = countEmployeesInCityParallelStream(employees, targetCity);
        System.out.println("Stream (parallelStream) count for " + targetCity + ": " + parallelStreamCount);
    }

}

