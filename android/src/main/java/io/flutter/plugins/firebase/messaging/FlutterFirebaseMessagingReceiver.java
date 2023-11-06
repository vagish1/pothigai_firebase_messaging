// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package io.flutter.plugins.firebase.messaging;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.google.firebase.messaging.RemoteMessage;
import com.ncorti.slidetoact.SlideToActView;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import Models.BookingDetails;
import antonkozyriatskyi.circularprogressindicator.CircularProgressIndicator;
import api.BookingDetailsRequest;

public class FlutterFirebaseMessagingReceiver extends BroadcastReceiver {
  private static final String TAG = "FLTFireMsgReceiver";
  static HashMap<String, RemoteMessage> notifications = new HashMap<>();
  private static double RADIUS_OF_EARTH = 6371;

  @Override
  public void onReceive(Context context, Intent intent) {
    Log.d(TAG, "broadcast received for message");
    if (ContextHolder.getApplicationContext() == null) {
      ContextHolder.setApplicationContext(context.getApplicationContext());
    }

    if (intent.getExtras() == null) {
      Log.d(
              TAG,
              "broadcast received but intent contained no extras to process RemoteMessage. Operation cancelled.");
      return;
    }

    RemoteMessage remoteMessage = new RemoteMessage(intent.getExtras());

    // Store the RemoteMessage if the message contains a notification payload.
    if (remoteMessage.getNotification() != null) {
      notifications.put(remoteMessage.getMessageId(), remoteMessage);
      FlutterFirebaseMessagingStore.getInstance().storeFirebaseMessage(remoteMessage);
    }

    if(remoteMessage.getData().get("subject").equals("bookingStatusPending")){



      SharedPreferences preferences = context.getSharedPreferences("session",Context.MODE_PRIVATE);
      String cookie = preferences.getString("cookie","");
      if(cookie.isEmpty()){

        return;
      }

      BookingDetailsRequest.getBookingDetails(context, remoteMessage.getData().get("recordId"), cookie, new BookingDetailsRequest.BookingDetailsListener() {
        @SuppressLint("SetTextI18n")
        @Override
        public void onSuccess(BookingDetails bookingDetails) {

          Log.d("Response", bookingDetails.toString());
          final MediaPlayer player = MediaPlayer.create(context, R.raw.ringtone);
          player.setLooping(true);

          final  WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                  ViewGroup.LayoutParams.MATCH_PARENT,
                  ViewGroup.LayoutParams.WRAP_CONTENT,
                  WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
//                          WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                  WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                  PixelFormat.TRANSPARENT);


          layoutParams.width = WindowManager.LayoutParams.MATCH_PARENT;
          layoutParams.height = WindowManager.LayoutParams.MATCH_PARENT;
          layoutParams.gravity = Gravity.BOTTOM | Gravity.CENTER;
          final  View inflater = LayoutInflater.from(context).inflate(R.layout.booking_popup, null);
          final  WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);


          final TextView travellingDistance = inflater.findViewById(R.id.travellingDistance);

          final TextView duration = inflater.findViewById(R.id.textView3);
          final TextView price = inflater.findViewById(R.id.travellingPrice);
          final TextView skip = inflater.findViewById(R.id.textView);
          final TextView carType = inflater.findViewById(R.id.textView4);
          final CircularProgressIndicator countDownProgress = inflater.findViewById(R.id.progressBar2);
          countDownProgress.setMaxProgress(120);


          final TextView dropOff = inflater.findViewById(R.id.dropOffText);
          final TextView pickUpAddress = inflater.findViewById(R.id.pickupAddress);
          final TextView dropOffAddress = inflater.findViewById(R.id.dropOffAddress);

          final TextView pickupDateAndTypeOfTrip = inflater.findViewById(R.id.dateOfPickup);
          final SlideToActView slideToConfirm = inflater.findViewById(R.id.slideToActView);


          travellingDistance.setText(getTypeOfBooking(bookingDetails.getData().getTypeOfBooking()));
          duration.setText(getDuration(bookingDetails.getData().getTypeOfBooking(),bookingDetails.getData().getTariffDetails().getNoOfDays(), bookingDetails.getData().getTariffDetails().getTimeInHours()));

          price.setText("₹ " +bookingDetails.getData().getEstimatedAmount());
          try{
            System.out.println(bookingDetails.getData().getPickupLocation().getCoordinates()[1]);
            System.out.println(bookingDetails.getData().getPickupLocation().getCoordinates()[0]);
            System.out.println(bookingDetails.getData().getDestinationLocation().getCoordinates()[1]);
            System.out.println(bookingDetails.getData().getDestinationLocation().getCoordinates()[1]);
          }catch (NullPointerException e){
            System.out.println(e.getMessage());
            e.printStackTrace();
          }

          pickUpAddress.setText(bookingDetails.getData().getPickupLocation().getFragmentedAddress());
          dropOffAddress.setText(bookingDetails.getData().getDestinationLocation().getFragmentedAddress());

          if(bookingDetails.getData().getPickupDateTime() ==0){



            pickupDateAndTypeOfTrip.setText(bookingDetails.getData().getTripType()+" , Please proceed to the customer's location immediately.");
            pickupDateAndTypeOfTrip.setTypeface(Typeface.DEFAULT_BOLD);

          }else{
            Date date = new Date(bookingDetails.getData().getPickupDateTime() * 1000);
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm aa", Locale.getDefault());
            formatter.setTimeZone(TimeZone.getDefault());


            pickupDateAndTypeOfTrip.setText(bookingDetails.getData().getTripType()+" : "+ formatter.format(date));
          }



          carType.setText("Car Type : "+bookingDetails.getData().getVehicleType()+" | "+ bookingDetails.getData().getVehicleTransmissionType());
          dropOff.setText("Drop Off" + " | "+calculateDistance(bookingDetails.getData().getPickupLocation().getCoordinates()[1],bookingDetails.getData().getPickupLocation().getCoordinates()[0],bookingDetails.getData().getDestinationLocation().getCoordinates()[1],bookingDetails.getData().getDestinationLocation().getCoordinates()[0])+" Km");
          final CountDownTimer timer = new CountDownTimer(120000,1000) {
            @Override
            public void onTick(long l) {

              countDownProgress.setProgress(l/1000,120);

            }

            @Override
            public void onFinish() {


              Toast.makeText(context,"Sorry but you haven't accepted booking within time limit",Toast.LENGTH_LONG).show();

              if(player.isPlaying()){
                player.stop();
              }
              manager.removeView(inflater);


            }
          };
          skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Toast.makeText(context,"You have cancelled this order",Toast.LENGTH_LONG).show();
              manager.removeView(inflater);
              if(player.isPlaying()){
                player.stop();
              }
              timer.cancel();

            }
          });

          slideToConfirm.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(@NonNull SlideToActView slideToActView) {
              //Todo : call accept Booking api
              BookingDetailsRequest.acceptBooking(context, remoteMessage.getData().get("recordId"), cookie, new BookingDetailsRequest.AcceptBookingListener() {
                @Override
                public void onSuccess() {
                  Toast.makeText(context,"Thanks for accepting booking, check booking details inside app",Toast.LENGTH_LONG).show();
                  manager.removeView(inflater);

                  if(player.isPlaying()){
                    player.stop();
                  }
                  timer.cancel();

                }

                @Override
                public void onError(String errorMessage) {
                  slideToConfirm.resetSlider();
                  Toast.makeText(context,"We encountered an error while accepting the booking",Toast.LENGTH_LONG).show();

                  if(player.isPlaying()){
                    player.stop();
                  }
                  manager.removeView(inflater);
                  timer.cancel();

                }
              });
            }
          });

          player.start();
          timer.start();
          manager.addView(inflater, layoutParams);

        }

        @Override
        public void onError(String errorMessage) {
          // Handle errors here
          Toast.makeText(context, errorMessage, Toast.LENGTH_SHORT).show();
        }
      });

    }
    //  |-> ---------------------
    //      App in Foreground
    //   ------------------------
    if (FlutterFirebaseMessagingUtils.isApplicationForeground(context)) {
      FlutterFirebaseRemoteMessageLiveData.getInstance().postRemoteMessage(remoteMessage);
      return;
    }

    //  |-> ---------------------
    //    App in Background/Quit
    //   ------------------------


    Intent onBackgroundMessageIntent =
            new Intent(context, FlutterFirebaseMessagingBackgroundService.class);
    onBackgroundMessageIntent.putExtra(
            FlutterFirebaseMessagingUtils.EXTRA_REMOTE_MESSAGE, remoteMessage);
    FlutterFirebaseMessagingBackgroundService.enqueueMessageProcessing(
            context, onBackgroundMessageIntent);
  }

  private static String getTypeOfBooking(String type){
    if(type.equals("cityLimit")){
      return "Local Trip";
    }

    if(type.equals("outskirt")){
      return "Long Distance";
    }

    return "Outstation";

  }

  private static  String getDuration(String type, int duration, int timeInHours){
    if(Objects.equals(type.toLowerCase(), "outstation")){
        return duration+" Days";
    }
    return timeInHours+" Hours";
  }


  public static double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
    // Convert latitude and longitude from degrees to radians
    double dLat = Math.toRadians(lat2 - lat1);
    double dLon = Math.toRadians(lon2 - lon1);
    double a = Math.pow(Math.sin(dLat / 2), 2) + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) * Math.pow(Math.sin(dLon / 2), 2);
    double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
    double distance = RADIUS_OF_EARTH * c;
    DecimalFormat df = new DecimalFormat("#.#");
    distance = Double.parseDouble(df.format(distance));

    return Math.round( distance);
  }
}
