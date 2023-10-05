package Models;

import org.json.JSONException;
import org.json.JSONObject;

public class BookingDetails {
    private String message;
    private int code;
    private BookingData data;
    private int responseCode;

    // Getter and Setter methods

    public void parseFromJSON(JSONObject jsonObject) throws JSONException {
        message = jsonObject.optString("message");
        code = jsonObject.optInt("code");
        responseCode = jsonObject.optInt("responseCode");

        JSONObject dataObject = jsonObject.optJSONObject("data");
        if (dataObject != null) {
            data = new BookingData();
            data.parseFromJSON(dataObject.optJSONObject("result"));
        }
    }

    public String getMessage() {
        return message;
    }

    public int getCode() {
        return code;
    }

    public BookingData getData() {
        return data;
    }

    public int getResponseCode() {
        return responseCode;
    }
}

