package com.example.kunal.locatorapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
import android.app.Notification;
import android.app.NotificationManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {

    LocationManager lom;

    EditText locationEditText;
    Button submitButton;
    TextView locationTextView;
    String inputLocation;

    public double userLatitude;
    public double userLongitude;

    public double locationLatitude;
    public double locationLongitude;

    public double distance;
    Boolean notificationSent= false;


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationEditText = (EditText) findViewById(R.id.location_edit_text);
        submitButton = (Button) findViewById(R.id.submitBtn);
        locationTextView=(TextView)findViewById(R.id.text_view_loc);

        lom = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            Toast toast = Toast.makeText(this, "Permission for location access not given", Toast.LENGTH_SHORT);
            toast.show();
        } else {
            lom.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000*2, 0, this);
//            lom.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000*2, 0, this);
        }

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!locationEditText.getText().toString().equals("")){

                    inputLocation=locationEditText.getText().toString();

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("We will notify when " + inputLocation + " is in \n1 KM range \n\nMake sure your Gps is enabled");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            locationTextView.setText(inputLocation);

                            convertAddress(inputLocation);
                            Log.d("kunal", locationLatitude + " " + locationLongitude);
                            Log.d("kunal", userLatitude + " " + userLongitude);
                            startService(new Intent(getApplicationContext(), NotificationService.class));
                            minimizeApp();

                            distance=getDistanceFromLatLonInKm(userLatitude,userLongitude,locationLatitude,locationLongitude);
                            Log.d("kunal", String.valueOf(distance));

                            checkForNotification(distance,inputLocation);


                        }
                    });

                    AlertDialog alert = builder.create();
                    alert.show();
                }
                else{
                    Toast.makeText(getApplicationContext(),"Please Enter the Location",Toast.LENGTH_SHORT).show();
                }
            }

        });
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void onLocationChanged(Location location) {

        double latitude=location.getLatitude();
        double longitude=location.getLongitude();
        userLatitude=latitude;
        userLongitude=longitude;

        distance=getDistanceFromLatLonInKm(userLatitude,userLongitude,locationLatitude,locationLongitude);
        Log.d("kunal",String.valueOf(distance));

        checkForNotification(distance,inputLocation);

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(getApplicationContext(),"GPS enabled!",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getApplicationContext(),"GPS disabled! Please enable it.",Toast.LENGTH_SHORT).show();

    }

    public void convertAddress(String location){
        Geocoder geocoder= new Geocoder(getApplicationContext());
        try{
            List<Address> addressList = geocoder.getFromLocationName(location,1);
            if(addressList!=null && addressList.size()>0){
                locationLatitude=addressList.get(0).getLatitude();
                locationLongitude=addressList.get(0).getLongitude();
            }
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public  double getDistanceFromLatLonInKm(double lat1,double lon1, double lat2, double lon2) {
        int r = 6371; // Radius of the earth in km
        double dLat = deg2rad(lat2-lat1);  // deg2rad below
        double dLon = deg2rad(lon2-lon1);
        double a =
                Math.sin(dLat/2) * Math.sin(dLat/2) +
                        Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) *
                                Math.sin(dLon/2) * Math.sin(dLon/2)
                ;
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double d = r * c; // Distance in km
        return d;
    }

    public double deg2rad(double deg) {
        return deg * (Math.PI/180);
    }

    public void minimizeApp(){
        Intent startMain = new Intent(Intent.ACTION_MAIN);
        startMain.addCategory(Intent.CATEGORY_HOME);
        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(startMain);
    }

    public void checkForNotification(double distance,String inputLocation){
        if(distance<=1.00 && notificationSent==false){
            NotificationCompat.Builder builder= new NotificationCompat.Builder(getApplicationContext());
            builder.setSmallIcon(R.mipmap.ic_launcher);
            builder.setContentTitle("Yippee!!");
            builder.setContentText("Within 1 KM of " + inputLocation );

            Notification notification = builder.build();
            NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);
            notification.defaults |= Notification.DEFAULT_VIBRATE;
            notification.defaults |= Notification.DEFAULT_SOUND;

            nm.notify(1,notification);
            notificationSent=true;
        }
    }

}
