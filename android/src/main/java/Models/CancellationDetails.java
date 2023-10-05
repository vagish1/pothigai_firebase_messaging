package Models;

import org.json.JSONException;
import org.json.JSONObject;

public class CancellationDetails {
    private int timeInMinutes;
    private int amount;

    // Getter and Setter methods

    public void parseFromJSON(JSONObject jsonObject) throws JSONException {
        timeInMinutes = jsonObject.optInt("timeInMinutes");
        amount = jsonObject.optInt("amount");
    }

    public int getTimeInMinutes() {
        return timeInMinutes;
    }

    public int getAmount() {
        return amount;
    }
}
