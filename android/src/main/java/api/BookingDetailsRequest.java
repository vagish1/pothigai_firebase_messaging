package api;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.internal.Objects;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import Models.BookingDetails;

public class BookingDetailsRequest {
    private static final String API_URL = "https://jivi.pothigaicalldrivers.com/user/booking/details";

    private static final String ACCEPT_BOOKING = "https://jivi.pothigaicalldrivers.com/user/booking/accept";

    public static void getBookingDetails(Context context, String bookingId, String cookieValue, final BookingDetailsListener listener) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("bookingId", bookingId);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, API_URL, requestBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                // Parse the response into the BookingDetails model class

                                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                JsonElement jsonElement = JsonParser.parseString(response.toString());
                                String prettyJson = gson.toJson(jsonElement);
                                System.out.println(prettyJson);
                                BookingDetails bookingDetails = new BookingDetails();
                                bookingDetails.parseFromJSON(response);

                                listener.onSuccess(bookingDetails);
                            } catch (JSONException e) {
                                e.printStackTrace();
                                listener.onError("Error parsing JSON");
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
System.out.println(error.getMessage());
                            System.out.println(error.networkResponse);
                            listener.onError("Error in network request");
                        }


                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("cookie", cookieValue);
                    return headers;
                }
            };

            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onError("Error creating JSON request");
        }
    }

    public static void acceptBooking(Context context, String bookingId, String cookieValue, final AcceptBookingListener listener) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);

        try {
            JSONObject requestBody = new JSONObject();
            requestBody.put("bookingId", bookingId);

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, ACCEPT_BOOKING, requestBody,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {

                                Gson gson = new GsonBuilder().setPrettyPrinting().create();
                                JsonElement jsonElement = JsonParser.parseString(response.toString());
                                String prettyJson = gson.toJson(jsonElement);
                                System.out.println(prettyJson);

                                JSONObject data = response.optJSONObject("data");

                                int responseCode = data.optInt("responseCode");
                                String message = data.optString("message");

                                if(responseCode == 109 || responseCode == 125){
                                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
                                }




                                listener.onSuccess(response);
                            } catch (Exception e) {
                                e.printStackTrace();
                                
                                listener.onError(e.getMessage());
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            System.out.println(error.networkResponse);
System.out.println(error.getMessage());
                            listener.onError("Error in network request"+ error.getMessage());
                        }


                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    headers.put("cookie", cookieValue);
                    return headers;
                }
            };

            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            e.printStackTrace();
            listener.onError("Error creating JSON request"+e.getMessage());
        }
    }

    public interface BookingDetailsListener {
        void onSuccess(BookingDetails bookingDetails);
        void onError(String errorMessage);

    }

    public interface AcceptBookingListener{
        void onSuccess(JSONObject response);
        void onError(String errorMessage);
    }
}
