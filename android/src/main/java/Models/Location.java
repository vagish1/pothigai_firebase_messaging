package Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Location {
    private String type;
    private double[] coordinates;
    private String fragmentedAddress;

    // Getter and Setter methods

    public void parseFromJSON(JSONObject jsonObject) throws JSONException {
        type = jsonObject.optString("type");
        JSONArray coordinatesArray = jsonObject.optJSONArray("coordinates");
        if (coordinatesArray != null && coordinatesArray.length() == 2) {
            coordinates = new double[]{coordinatesArray.optDouble(0), coordinatesArray.optDouble(1)};
        }
        fragmentedAddress = jsonObject.optString("fragmentedAddress");
    }

    public String getType() {
        return type;
    }

    public double[] getCoordinates() {
        return coordinates;
    }

    public String getFragmentedAddress() {
        return fragmentedAddress;
    }
}
