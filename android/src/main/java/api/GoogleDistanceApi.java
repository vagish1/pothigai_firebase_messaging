package api;

import android.content.Context;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
public class GoogleDistanceApi {
    private static final String API_URL = "https://maps.googleapis.com/maps/api/directions/json";

    public static void getDistance(Context context, double userLat, double userLng, double destLat, double destLng, final String apiKey, final DistanceListener listener) {
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        try {
            // Construct the URL with origin, destination, and API key parameters
            String url = API_URL + "?origin=" + userLat + "," + userLng + "&destination=" + destLat + "," + destLng + "&key=" + apiKey;

            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null,
                    new Response.Listener<JSONObject>() {
                        @Override
                        public void onResponse(JSONObject response) {
                            try {
                                // Parse the response into the BookingDetails model class
                                JSONArray routes = response.getJSONArray("routes");
                                JSONObject route = routes.getJSONObject(0);
                                JSONArray legs = route.getJSONArray("legs");
                                JSONObject firstLeg = legs.getJSONObject(0);
                                JSONObject distance = firstLeg.getJSONObject("distance");

                                // Retrieve distance text and value
                                String distanceText = distance.getString("text");
                                int distanceValue = distance.getInt("value");

                                // Call onSuccess method with distance details

                                listener.onSuccess(distanceText);

                            } catch (JSONException e) {
                                e.printStackTrace();
                                listener.onError("Error parsing JSON");
                            }
                        }
                    },
                    new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            listener.onError("Error in network request");
                        }
                    }) {
                @Override
                public Map<String, String> getHeaders() {
                    Map<String, String> headers = new HashMap<>();
                    // Add any headers if needed
                    return headers;
                }
            };

            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
            listener.onError("Error creating JSON request");
        }
    }

    public interface DistanceListener {
        void onSuccess(String distanceDetails);
        void onError(String errorMessage);
    }
}


