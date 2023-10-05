package Models;

import org.json.JSONException;
import org.json.JSONObject;

public class NightFare {
    private String nightFareStartTime;
    private String nightFareEndTime;
    private int price;

    // Getter and Setter methods

    public void parseFromJSON(JSONObject jsonObject) throws JSONException {
        nightFareStartTime = jsonObject.optString("nightFareStartTime");
        nightFareEndTime = jsonObject.optString("nightFareEndTime");
        price = jsonObject.optInt("price");
    }

    public String getNightFareStartTime() {
        return nightFareStartTime;
    }

    public String getNightFareEndTime() {
        return nightFareEndTime;
    }

    public int getPrice() {
        return price;
    }
}