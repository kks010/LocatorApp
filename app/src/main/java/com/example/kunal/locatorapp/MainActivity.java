package com.example.kunal.locatorapp;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlertDialog;
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
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.List;

public class MainActivity extends AppCompatActivity implements LocationListener {

    LocationManager lom;

    EditText locationEditText;
    Button submitButton;
    String inputLocation;

    public double userLatitude;
    public double userLongitude;

    public double locationLatitude;
    public double locationLongitude;


    @TargetApi(Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        locationEditText = (EditText) findViewById(R.id.location_edit_text);
        submitButton = (Button) findViewById(R.id.submitBtn);

        lom = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (checkSelfPermission(Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && checkSelfPermission(Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    public void requestPermissions(@NonNull String[] permissions, int requestCode)
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for Activity#requestPermissions for more details.
            return;
        }
        lom.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 10, this);


        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!locationEditText.getText().toString().equals("")){

                    inputLocation=locationEditText.getText().toString();

                    AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                    builder.setMessage("We will notify when " + inputLocation + " is in 1km range \n\nPlease make sure your Gps is enabled");
                    builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            convertAddress(inputLocation);
                            Log.d("Kunal",locationLatitude + " "+ locationLongitude);
                            startService(new Intent(getApplicationContext(), NotificationService.class));
                            onStop();
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
    public void onLocationChanged(Location location) {

        double latitude=location.getLatitude();
        double longitude=location.getLongitude();
        userLatitude=latitude;
        userLongitude=longitude;

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
        Toast.makeText(getApplicationContext(),"GPS disabled!",Toast.LENGTH_SHORT).show();

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
}
