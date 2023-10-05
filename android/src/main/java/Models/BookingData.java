package Models;

import org.json.JSONException;
import org.json.JSONObject;

public class BookingData {
    private TariffDetails tariffDetails;
    private Location pickupLocation;
    private Location destinationLocation;
    private String typeOfBooking;
    private String tripType;
    private String vehicleTransmissionType;
    private String vehicleType;
    private boolean active;
    private String bookingId;
    private String status;
    private long pickupDateTime;
    private int totalAmount;
    private int estimatedAmount;

    // Getter and Setter methods

    public void parseFromJSON(JSONObject jsonObject) throws JSONException {
        tariffDetails = new TariffDetails();
        tariffDetails.parseFromJSON(jsonObject.optJSONObject("tariffDetails"));

        pickupLocation = new Location();
        pickupLocation.parseFromJSON(jsonObject.optJSONObject("pickupLocation"));

        destinationLocation = new Location();
        destinationLocation.parseFromJSON(jsonObject.optJSONObject("destinationLocation"));

        typeOfBooking = jsonObject.optString("typeOfBooking");
        tripType = jsonObject.optString("tripType");
        vehicleTransmissionType = jsonObject.optString("vehicleTransmissionType");
        vehicleType = jsonObject.optString("vehicleType");
        active = jsonObject.optBoolean("active");
        bookingId = jsonObject.optString("bookingId");
        status = jsonObject.optString("status");
        pickupDateTime = jsonObject.optLong("pickupDateTime");
        totalAmount = jsonObject.optInt("totalAmount");
        estimatedAmount = jsonObject.optInt("estimatedAmount");
    }

    public TariffDetails getTariffDetails() {
        return tariffDetails;
    }

    public Location getPickupLocation() {
        return pickupLocation;
    }

    public Location getDestinationLocation() {
        return destinationLocation;
    }

    public String getTypeOfBooking() {
        return typeOfBooking;
    }

    public String getTripType() {
        return tripType;
    }

    public String getVehicleTransmissionType() {
        return vehicleTransmissionType;
    }

    public String getVehicleType() {
        return vehicleType;
    }

    public boolean isActive() {
        return active;
    }

    public String getBookingId() {
        return bookingId;
    }

    public String getStatus() {
        return status;
    }

    public long getPickupDateTime() {
        return pickupDateTime;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public int getEstimatedAmount() {
        return estimatedAmount;
    }
}
