package Models;

import org.json.JSONException;
import org.json.JSONObject;

public class ExtraTime {
    private double pricePerMinute;

    // Getter and Setter methods

    public void parseFromJSON(JSONObject jsonObject) throws JSONException {
        pricePerMinute = jsonObject.optDouble("pricePerMinute");
    }

    public double getPricePerMinute() {
        return pricePerMinute;
    }
}