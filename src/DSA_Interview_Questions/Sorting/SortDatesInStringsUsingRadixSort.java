package DSA_Interview_Questions.Sorting;



public class SortDatesInStringsUsingRadixSort {
    /**
     * sort the given array of strings representing dates in DDMMYYYY format
     *
     * eg:- input array => {"05121968", "17121996", "05061997", "11081972", "110819990"}*/


    public static void main(String[] args) {
        // Input array
        String[] dateStrings = {"05121968", "17121996", "05061997", "11081972", "11081999"};

        // Radix sort
        radixSort(dateStrings);

        // Print the sorted array
        printArray(dateStrings);
    }

    private static void radixSort(String[] arr) {
        // say DD_MM_YYYY
        // for DD => (no/10^6) % 100
        // for MM => (no/10^4) % 100
        // for YYYY => (no/1) % 10000

        countingSort(arr, 31, 1000000, 100); //for DD
        countingSort(arr, 12, 10000, 100); //for MM
        countingSort(arr, 2500, 1, 10000); //for YYYY

    }

    private static void countingSort(String[] arr, int range, int div, int mod) {
        int n = arr.length;
        int[] p = new int[range + 1];

        //find the frequency for each element with positionIdx value in p as the element value of nums[]
        for (int i = 0; i < n; i++) {
            p[(Integer.parseInt(arr[i])/div) % mod] = p[(Integer.parseInt(arr[i])/div) % mod] + 1;
        }

        //convert the array to prefixSum array
        for (int i = 1; i < p.length; i++) {
            p[i] = p[i-1] + p[i];
        }

        //use the prefix array to populate the ans array
        // by traversing through it in reverse
        String[] ans = new String[n];
        for (int i = n-1; i >= 0; i--) {
            int positionValue = p[(Integer.parseInt(arr[i])/div) % mod];
            ans[positionValue - 1] = arr[i];
            p[(Integer.parseInt(arr[i])/div) % mod]--;
        }

        //copy the solution to its original array
        for (int i = 0; i < n; i++) {
            arr[i] = ans[i];
        }
    }

    private static void printArray(String[] arr) {
        for (String str : arr) {
            System.out.print(str + " ");
        }
        System.out.println();
    }

    /**TC is O(N+R), SC is O(N+R), where n is the ans array and R is the prefixSum array*/
}
