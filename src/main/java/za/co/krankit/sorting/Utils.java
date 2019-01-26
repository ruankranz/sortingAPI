package za.co.krankit.sorting;

import java.util.ArrayList;
import java.util.regex.Pattern;

public class Utils {

    /**
     * Get the max array size that the app is allowed to process
     *
     * @return maxSize int
     */
    public static int getMaxSize() {
        int maxSize;
        // Try to parse the MAX_ARRAY_SIZE env variable
        try {
            maxSize = Integer.parseInt(System.getenv("MAX_ARRAY_SIZE"));
        } catch (NumberFormatException e) {
            // and default to 10000 if it is not set/found
            maxSize = 10000;
        }
        return maxSize;
    }

    /**
     * Get a list of integers from the request data
     *
     * @param requestData String
     * @return numbers ArrayList<Integer>
     */
    public static ArrayList<Integer> getIntegers(String requestData) {
        // Apply a regex to the request body (as a string)
        final String regularExpression = "([^\\d])+";
        // and get all the nested arrays
        Pattern pattern = Pattern.compile(regularExpression);
        String[] results = pattern.split(requestData);
        ArrayList<Integer> numbers = new ArrayList<>();
        // loop over the results and add to numbers array
        for (String result : results) {
            try {
                numbers.add(Integer.valueOf(result));
            } catch (NumberFormatException e) {
                // Catch and skip any non integers
            }
        }
        return numbers;
    }
}
