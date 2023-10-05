package Models;

import org.json.JSONException;
import org.json.JSONObject;

public class TariffDetails {
    private CancellationDetails cancellationDetails;
    private NightFare nightFare;
    private ExtraTime extraTime;
    private int price;
    private int returnPrice;
    private int timeInHours;
    private int noOfDays;
    private int maxDistanceInKm;
    private int foodAllowances;

    // Getter and Setter methods

    public void parseFromJSON(JSONObject jsonObject) throws JSONException {
        cancellationDetails = new CancellationDetails();
        cancellationDetails.parseFromJSON(jsonObject.optJSONObject("cancellationDetails"));

        nightFare = new NightFare();
        nightFare.parseFromJSON(jsonObject.optJSONObject("nightFare"));

        extraTime = new ExtraTime();
        extraTime.parseFromJSON(jsonObject.optJSONObject("extraTime"));

        price = jsonObject.optInt("price");
        returnPrice = jsonObject.optInt("returnPrice");
        timeInHours = jsonObject.optInt("timeInHours");
        noOfDays = jsonObject.optInt("noOfDays");
        maxDistanceInKm = jsonObject.optInt("maxDistanceInKm");
        foodAllowances = jsonObject.optInt("foodAllowances");
    }

    public CancellationDetails getCancellationDetails() {
        return cancellationDetails;
    }

    public NightFare getNightFare() {
        return nightFare;
    }

    public ExtraTime getExtraTime() {
        return extraTime;
    }

    public int getPrice() {
        return price;
    }

    public int getReturnPrice() {
        return returnPrice;
    }

    public int getTimeInHours() {
        return timeInHours;
    }

    public int getNoOfDays() {
        return noOfDays;
    }

    public int getMaxDistanceInKm() {
        return maxDistanceInKm;
    }

    public int getFoodAllowances() {
        return foodAllowances;
    }
}
