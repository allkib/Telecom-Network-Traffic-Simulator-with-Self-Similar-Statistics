/** 
 * @author: Clarence
 */

package view;

import java.util.List;

public class ErrorDisplay {
    public static void printValidationErrors(List<String> errors) {
        if (errors == null || errors.isEmpty()) return;
        System.out.println("Input Validation Errors:");
        for (String err : errors) {
            System.out.println(err);
        }
    }

    public static void printException(String context, Exception e){
        System.out.println("Error" + (context != null ? " in " + context : "") + (e != null ? ": " + e.getMessage() : ""));
    }
 }