package za.co.krankit.sorting;

import java.util.ArrayList;

public class BubbleSort implements CustomSort{

    /**
     * Takes an a list of unsorted integers and sorts them in-place
     * using the bubble sort algorithm
     *
     * @param unsortedList ArrayList<Integer>
     */
    public static void sort(ArrayList<Integer> unsortedList) {

        int size = unsortedList.size();
        int temp;
        for (int i = 0; i < size; i++) {
            for (int j = 1; j < (size - i); j++) {
                if (unsortedList.get(j - 1) > unsortedList.get(j)) {
                    //swap the elements
                    temp = unsortedList.get(j - 1);
                    unsortedList.set(j - 1, unsortedList.get(j));
                    unsortedList.set(j, temp);
                }
            }
        }
    }
}