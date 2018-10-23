package com.example.vikra.location;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.LocationSource;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.Date;
import java.util.List;

import static com.example.vikra.location.MainActivity.REQUEST_LOCATION;


public class NameofLocationPopupwindow extends AppCompatActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private final String key3 = "kkl";
   TextView aa;
    private final String addresskey = "addresskey";
    private final String timekey = "timekey";  private final String key2 = "Key2";
    //private final String key3 = "kkl";
    private final String accesskey = "access";


    private double currentlat;
    private double currentlng;
    String name;
    long time;
    GoogleApiClient mGoogleApiClient;
    Location mlastLocation;
    LocationRequest mLocationRequest;
    Location currentLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_popupwindow);

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);

        int width = dm.widthPixels;
        int height = dm.heightPixels;


        getWindow().setLayout((int)(width*0.5),(int)(height*0.5));
        aa = (TextView)findViewById(R.id.nameoflocation);
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
        Intent a = getIntent();
        Bundle b = a.getBundleExtra(accesskey);

        name = b.getString(addresskey);
        time = b.getLong(timekey);
        currentlat = b.getDouble(key2);
        currentlng = b.getDouble(key3);
        Date date = new Date(time);
        aa.setText("The name of the location is " + name + " Time of last check in is " + date.toString() );


        //Intent returnIntent = new Intent();
       // returnIntent.putExtra(key3,a.getText().toString());
    }



    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

            return;
        }
        createLocationRequest();

        mlastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mlastLocation != null) {
            Location locationn = mlastLocation;
            float[] results = new float[1];
            Location.distanceBetween(currentlat, currentlng, locationn.getLatitude(), locationn.getLongitude(), results);
            float distanceinMeters = results[0];
            if (distanceinMeters > 30) {
                finish();
            }


            //updateUI(currentLocation.getLatitude(),currentLocation.getLongitude());
            // updateUI();
        } else
        {

        }
        //display.setText("code not working boss");

    }


    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(100);
        mLocationRequest.setFastestInterval(100);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        startLocationUpdates();
    }

    @Override
    protected void onStart() {
        if(mGoogleApiClient != null)
            mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
    protected void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

            return;
        }
        //mGoogleApiClient.connect();
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }

    @Override
    public void onConnectionSuspended(int i) {

    }
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_LOCATION:
                //onStart();
                break;
        }
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location locationn) {

        if(locationn != null) {
            float[] results = new float[1];
            Location.distanceBetween(currentlat, currentlng, locationn.getLatitude(), locationn.getLongitude(), results);
            float distanceinMeters = results[0];
            if (distanceinMeters > 30) {
                finish();
            }
        }




            //updateUI(currentLocation.getLatitude(),currentLocation.getLongitude());
            // updateUI();
        }
    }


