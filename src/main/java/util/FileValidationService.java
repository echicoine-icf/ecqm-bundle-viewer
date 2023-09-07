package util;


import org.json.JSONException;
import org.json.JSONTokener;

public class FileValidationService {
    public static boolean isValidJSON(String jsonContent) {
        try {
            new JSONTokener(jsonContent).nextValue();
            return true;
        } catch (JSONException e) {
            return false;
        }
    }
}

