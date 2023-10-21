package Models;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Location {
    private String type;
    private double[] coordinates;
    private String fragmentedAddress;

    public void parseFromJSON(JSONObject jsonObject) throws JSONException {
        type = jsonObject.optString("type");

        JSONObject locationObject = jsonObject.optJSONObject("location");
        if (locationObject != null) {
            JSONArray coordinatesArray = locationObject.optJSONArray("coordinates");
            if (coordinatesArray != null && coordinatesArray.length() == 2) {
                coordinates = new double[2];
                coordinates[0] = coordinatesArray.optDouble(0, 0.0);
                coordinates[1] = coordinatesArray.optDouble(1, 0.0);
            }
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
