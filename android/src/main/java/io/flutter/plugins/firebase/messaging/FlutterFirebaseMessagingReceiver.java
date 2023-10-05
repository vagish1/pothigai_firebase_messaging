// Copyright 2020 The Chromium Authors. All rights reserved.
// Use of this source code is governed by a BSD-style license that can be
// found in the LICENSE file.

package io.flutter.plugins.firebase.messaging;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.PixelFormat;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.JsonRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.api.Response;
import com.google.firebase.messaging.RemoteMessage;
import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;
import com.ncorti.slidetoact.SlideToActView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Objects;
import java.util.TimeZone;
import java.util.Timer;
import java.util.TimerTask;

import Models.BookingDetails;
import api.BookingDetailsRequest;

public class FlutterFirebaseMessagingReceiver extends BroadcastReceiver {
  private static final String TAG = "FLTFireMsgReceiver";
  static HashMap<String, RemoteMessage> notifications = new HashMap<>();

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

      Toast.makeText(context,"Inside If",Toast.LENGTH_LONG).show();

      SharedPreferences preferences = context.getSharedPreferences("session",Context.MODE_PRIVATE);
      String cookie = preferences.getString("cookie","");
      if(cookie.isEmpty()){
        Toast.makeText(context,"No Cookie",Toast.LENGTH_LONG).show();
        return;
      }

      BookingDetailsRequest.getBookingDetails(context, remoteMessage.getData().get("recordId"), cookie, new BookingDetailsRequest.BookingDetailsListener() {
        @Override
        public void onSuccess(BookingDetails bookingDetails) {
          // Handle the successful response here
          // Access data like: bookingDetails.getData().getBookingId(), bookingDetails.getData().getTariffDetails().getPrice(), etc.

          if(bookingDetails.getResponseCode() !=109){
            Toast.makeText(context,"Response = "+bookingDetails.getResponseCode(),Toast.LENGTH_LONG).show();

            return;
          }
          final MediaPlayer player = MediaPlayer.create(context, R.raw.ringtone);



          final  WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams(
                  ViewGroup.LayoutParams.MATCH_PARENT,
                  ViewGroup.LayoutParams.WRAP_CONTENT,
                  WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
//                          WindowManager.LayoutParams.FLAG_LAYOUT_IN_SCREEN,
                  WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL,
                  PixelFormat.TRANSPARENT);

          final  View inflater = LayoutInflater.from(context).inflate(R.layout.booking_popup, null);
          final  WindowManager manager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);


          final TextView travellingDistance = inflater.findViewById(R.id.travellingDistance);
          final TextView countDownText = inflater.findViewById(R.id.countDownText);
          final TextView duration = inflater.findViewById(R.id.textView3);
          final TextView price = inflater.findViewById(R.id.travellingPrice);
          final TextView skip = inflater.findViewById(R.id.textView);
          final TextView carType = inflater.findViewById(R.id.textView4);
          final ProgressBar countDownProgress = inflater.findViewById(R.id.progressBar2);
          countDownProgress.setMax(120);
          countDownProgress.setMax(0);

          final TextView dropOff = inflater.findViewById(R.id.dropOffText);
          final TextView pickUpAddress = inflater.findViewById(R.id.pickupAddress);
          final TextView dropOffAddress = inflater.findViewById(R.id.dropOffAddress);

          final TextView pickupDateAndTypeOfTrip = inflater.findViewById(R.id.dateOfPickup);
          final SlideToActView slideToConfirm = inflater.findViewById(R.id.slideToActView);


          travellingDistance.setText(getTypeOfBooking(bookingDetails.getData().getTypeOfBooking()));
          duration.setText(bookingDetails.getData().getTariffDetails().getNoOfDays() + " Days");

          price.setText("₹ " +bookingDetails.getData().getEstimatedAmount());

          pickUpAddress.setText(bookingDetails.getData().getPickupLocation().getFragmentedAddress());
          dropOffAddress.setText(bookingDetails.getData().getDestinationLocation().getFragmentedAddress());

          Date date = new Date(bookingDetails.getData().getPickupDateTime() * 1000);
          SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm aa", Locale.getDefault());
          formatter.setTimeZone(TimeZone.getDefault());

          pickupDateAndTypeOfTrip.setText(bookingDetails.getData().getTripType()+" "+formatter.format(date).toString());

          carType.setText("Car Type : "+bookingDetails.getData().getVehicleType()+" | "+ bookingDetails.getData().getVehicleTransmissionType());
          dropOff.setText("Drop Off");
          skip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
              Toast.makeText(context,"You have cancelled this order",Toast.LENGTH_LONG).show();
              manager.removeView(inflater);
              if(player.isPlaying()){
                player.stop();
              }

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
                }

                @Override
                public void onError(String errorMessage) {
                  slideToConfirm.resetSlider();
                  Toast.makeText(context,"We encountered an error while accepting the booking",Toast.LENGTH_LONG).show();

                  if(player.isPlaying()){
                    player.stop();
                  }
                  manager.removeView(inflater);
                }
              });
            }
          });
          final CountDownTimer timer = new CountDownTimer(120000,1000) {
            @Override
            public void onTick(long l) {
              countDownText.setText(l/1000+" Sec");
              countDownProgress.setProgress(Integer.parseInt ((l/1000)+""));

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
          player.start();
          timer.start();
          manager.addView(inflater, layoutParams);

        }

        @Override
        public void onError(String errorMessage) {
          // Handle errors here
        }
      });

    }else{
      Toast.makeText(context,"bookingStatusNotMatched",Toast.LENGTH_LONG).show();
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
    if(type.equals("localTrip")){
      return "Local Trip";
    }

    if(type.equals("longDistance")){
      return "Long Distance";
    }

    return "Outstation";

  }





}
