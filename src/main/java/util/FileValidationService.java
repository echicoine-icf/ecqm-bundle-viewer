package util;

import org.json.JSONException;
import org.json.JSONObject;

public class FileValidationService {
	public static boolean isValidJSON(String jsonContent) {
		try {
			new JSONObject(jsonContent);
		} catch (JSONException e) {
			return false;
		}

		return true;
	}
}
