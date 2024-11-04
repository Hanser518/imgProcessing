package algorithm.quickSort;

public class QuickSort {

    public static void main(String[] args) {
        int[] arr = new int[20];
        for (int i = 0; i < 20; i++) {
            arr[i] = (int) (Math.random() * 100);
        }

        System.out.print("原始数组: ");
        for (int a : arr) {
            System.out.print(a + " ");
        }
        System.out.println();

        quickSort(arr, 0, arr.length - 1);

        System.out.print("排序后数组: ");
        for (int a : arr) {
            System.out.print(a + " ");
        }
        System.out.println();
    }

    public static void quickSort(int[] arr, int l, int r) {
        if (l >= r) {
            return;
        }
        int pivotIndex = partition(arr, l, r);
        quickSort(arr, l, pivotIndex - 1);
        quickSort(arr, pivotIndex + 1, r);
    }

    private static int partition(int[] arr, int l, int r) {
        int pivot = arr[r]; // 选择最右边的元素作为基准值
        int i = l - 1;
        for (int j = l; j < r; j++) {
            if (arr[j] < pivot) {
                i++;
                swap(arr, i, j);
            }
        }
        swap(arr, i + 1, r);
        return i + 1;
    }

    private static void swap(int[] arr, int i, int j) {
        int temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
