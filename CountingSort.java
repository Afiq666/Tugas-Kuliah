public class CoutingSort {
    public static int findMax(int[] arr) {
        int max = arr[0];
        for (int i = 1; i < arr.length; i++) {
            if (arr[i] > max) {
                max = arr[i];
            }
        }
        return max;
    }
    
    public static void countingSort(int[] arr) {
        int n = arr.length;
        
        int max = findMax(arr);
        
        int[] count = new int[max + 1];
        
        int[] output = new int[n];
        
        for (int i = 0; i < n; i++) {
            count[arr[i]]++;
        }
        
        for (int i = max - 1; i >= 0; i--) {
            count[i] += count[i + 1];
        }
        
        for (int i = n - 1; i >= 0; i--) {
            output[count[arr[i]] - 1] = arr[i];
            count[arr[i]]--;
        }
        
        for (int i = 0; i < n; i++) {
            arr[i] = output[i];
        }
    }
    
    public static void printArray(int[] arr) {
        for (int i = 0; i < arr.length; i++) {
            System.out.print(arr[i] + " ");
        }
        System.out.println();
    }
    
    public static void main(String[] args) {
        int[] data = {45, 23, 78, 12, 67, 34, 89, 56, 90, 15};
        
        System.out.println("Data sebelum diurutkan : ");
        printArray(data);
        
        countingSort(data);        
        System.out.println("\nData setelah diurutkan (terbesar ke terkecil) : ");
        printArray(data);        
    }
}