package com.example.vikra.location;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.webkit.DownloadListener;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;



import org.w3c.dom.Text;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;


public class MainActivity extends AppCompatActivity
        implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener

{
    GoogleApiClient mGoogleApiClient;
    Location mlastLocation;
    TextView display;
    static final int REQUEST_LOCATION = 1;
    LocationRequest mLocationRequest;
    Location currentLocation;
    TextView addressdisplay;
    LocationDB database;
    CustomAdapter customAdapter;
    List<LocationDB.CheckIn> data;
    ListView listview;
    String Address;
    int isdefaultstartinglocation = 0;
    private final String accesskey = "access";
    private final String key = "Key";
    private final String key2 = "Key2";
    private final String key3 = "kkl";
    private EditText imsickofthishit;
    private static final String KKKKK = "LLLLL";
    private static final String checkinkey = "checkinkey";
    private static final String kkki = "kkki";
    //private double startlat;
   // private double startlng;
    private double metercounter;
    Bundle savedstate;
    private double startlat;
    private double startlng;

    int A;



    // ArrayList<X> Coord_entries;

    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        savedstate = savedInstanceState;
        display = (TextView) findViewById(R.id.textview1);
        addressdisplay = (TextView) findViewById(R.id.textView2);
        imsickofthishit = (EditText) findViewById(R.id.checkinname) ;
        metercounter = 0;
        A = 0;




      database = new LocationDB(this);
        if(database.getReadableDatabase() != null)
        {
            //display.setText("This has been reached");
            data = database.getCheckIns();
            if(data == null)
            {
                display.setText("NULL");
                return;
            }
            isdefaultstartinglocation = 1;
           if(data.size() == 0)
           {
               A = 0;
           }
           else
               A = 1;
            //LocationDB.CheckIn temp = new LocationDB.CheckIn(25,25,16,"Home","Nigger2");
            //data.add(temp);

        }
        else
        {
            data = new ArrayList<>();
            //isdefaultstartinglocation = 1;
           // LocationDB.CheckIn temp = new LocationDB.CheckIn(25,25,16,"Home","Nigger");
            //data.add(temp);
            isdefaultstartinglocation = 0;
        }
        //LocationDB.CheckIn temp = new LocationDB.CheckIn(25,25,16,"Home","Nigger");
        //data.add(temp);
        customAdapter = new CustomAdapter(this,R.layout.customlistview,data);
        listview = (ListView)findViewById(R.id.listview);
        listview.setAdapter(customAdapter);
        //setUpInitalEntries();
        //Coord_entries = new ArrayList<>();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
                   // mGoogleApiClient.connect();
            //createLocationRequest();
        }
       // mGoogleApiClient.connect();

       // createLocationRequest();



    }


    void setupentry(double lat, double lng, String name)
    {
        Geocoder gecoder;
        List<Address> address = null;
        gecoder = new Geocoder(this, Locale.getDefault());
        //address = gecoder.getFromLocation(latitude, longitude, 1);
        // double lat = (mlastLocation.getLatitude());
        //double lng = (mlastLocation.getLongitude());
        try {
            address = gecoder.getFromLocation(lat, lng, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        display.setText("Longtitude: " + String.format("%.6f",lng) + " Latitude: " + String.format("%.6f",lat));
        if(address == null)
        {
            addressdisplay.setText("Can't get street address");
        }
        else
            Address = "The address is :" + address.get(0).getAddressLine(0) + ","
                    + address.get(0).getLocality() + "," + address.get(0).getAdminArea() + ","
                    + address.get(0).getCountryName() + "," + address.get(0).getPostalCode();

        long time = System.currentTimeMillis();
        LocationDB.CheckIn person = new LocationDB.CheckIn(lat,lng,time,name,Address);
        addCheckIntoDatabase(person,0);

    }

    void setUpInitalEntries()
    {
       setupentry(40.519921,-74.462384,"Location1");
        setupentry(40.522650,-74.465851,"Location2");
        setupentry(40.524435, -74.458611,"Location3");
        setupentry(40.526803,-74.462139,"Location4");
        setupentry(40.525524,-74.464346,"Location5");
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

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

            return;
        }
        createLocationRequest();

        mlastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if (mlastLocation != null) {
           currentLocation = mlastLocation;
            updateUI(currentLocation.getLatitude(),currentLocation.getLongitude());
            //setUpInitalEntries();
           // updateUI();
        } else
            display.setText("Need to reset app/phone/emulator in order for code to work");

    }

    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(100);
        mLocationRequest.setFastestInterval(100);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
       startLocationUpdates();
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


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode) {
            case REQUEST_LOCATION:
             //onCreate(savedstate);
                recreate();
                break;
        }
    }


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {

        /*
        By using the synchronized function, this only allows the UI thread to to make the change, currentlocation = location
        Since current location is used by the function as the local version of location, onLocationChanged will not update by another thread while the synchro lock is on
         */

            synchronized (location)
            {
                if(currentLocation != null) {
                    startlat = currentLocation.getLatitude();
                    startlng = currentLocation.getLongitude();
                }
                currentLocation = location;
            }

            if (location != null) {

                //currentLocation = location;
                checkAutoMaticCheckIn();
            if(A == 0) {
                setUpInitalEntries();
                A = 1;
            }
                updateUI(currentLocation.getLatitude(), currentLocation.getLongitude());
                Timer mytimer = new Timer();
                mytimer.scheduleAtFixedRate(new TimerTask() {
                                                @Override
                                                public void run() {
                                                    double lat = currentLocation.getLatitude();
                                                    double lng = currentLocation.getLongitude();
                                                    long time = System.currentTimeMillis();
                                                    String name = "automatic check in from timer";
                                                    Geocoder gecoder;
                                                    List<Address> address = null;
                                                    gecoder = new Geocoder(getApplicationContext(), Locale.getDefault());
                                                    //address = gecoder.getFromLocation(latitude, longitude, 1);
                                                    // double lat = (mlastLocation.getLatitude());
                                                    //double lng = (mlastLocation.getLongitude());
                                                    try {
                                                        address = gecoder.getFromLocation(lat, lng, 1);
                                                    } catch (IOException e) {
                                                        e.printStackTrace();
                                                    }
                                                    display.setText("Longtitude: " + String.format("%.6f",lng) + " Latitude: " + String.format("%.6f",lat));
                                                    if(address == null)
                                                    {
                                                        addressdisplay.setText("Can't get street address");
                                                    }
                                                    else
                                                        Address = "The address is :" + address.get(0).getAddressLine(0) + ","
                                                                + address.get(0).getLocality() + "," + address.get(0).getAdminArea() + ","
                                                                + address.get(0).getCountryName() + "," + address.get(0).getPostalCode();

                                                    LocationDB.CheckIn person = new LocationDB.CheckIn(lat,lng,time,name,Address);
                                                    addCheckIntoDatabase(person,0);

                                                }
                                            },
                        300000,300000);



                // updateUI();
            }

    }

    void checkAutoMaticCheckIn()
    {
        if(startlng == 0 || startlat == 0)
        {
            return;
        }
        float[] results = new float[1];
        Location.distanceBetween(currentLocation.getLatitude(), currentLocation.getLongitude(), startlat, startlng, results);
        float distanceinMeters = results[0];
        metercounter += distanceinMeters;
        Geocoder gecoder;
        List<Address> address = null;
        gecoder = new Geocoder(this, Locale.getDefault());
        //address = gecoder.getFromLocation(latitude, longitude, 1);
        // double lat = (mlastLocation.getLatitude());
        //double lng = (mlastLocation.getLongitude());
        try {
            address = gecoder.getFromLocation(currentLocation.getLatitude(), currentLocation.getLongitude(), 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        //display.setText("Longtitude: " + String.format("%.6f",lng) + " Latitude: " + String.format("%.6f",lat));
        if(address == null)
        {
            //addressdisplay.setText("Can't get street address");
        }
        else
            Address = "The address is :" + address.get(0).getAddressLine(0) + ","
                    + address.get(0).getLocality() + "," + address.get(0).getAdminArea() + ","
                    + address.get(0).getCountryName() + "," + address.get(0).getPostalCode();
        if(metercounter >= 100)
        {
            LocationDB.CheckIn newcheckin = new LocationDB.CheckIn(currentLocation.getLatitude(),currentLocation.getLongitude(),System.currentTimeMillis(),"automatic check in from distance ",Address);
            metercounter = 0;
            addCheckIntoDatabase(newcheckin,0);

        }

    }

     void updateUI(double lat, double lng){
         Geocoder gecoder;
         List<Address> address = null;
         gecoder = new Geocoder(this, Locale.getDefault());
         //address = gecoder.getFromLocation(latitude, longitude, 1);
        // double lat = (mlastLocation.getLatitude());
         //double lng = (mlastLocation.getLongitude());
         try {
             address = gecoder.getFromLocation(lat, lng, 1);
         } catch (IOException e) {
             e.printStackTrace();
         }
         display.setText("Longtitude: " + String.format("%.6f",lng) + " Latitude: " + String.format("%.6f",lat));
         if(address == null)
         {
             addressdisplay.setText("Can't get street address");
         }
         else
             Address = "The address is :" + address.get(0).getAddressLine(0) + ","
                     + address.get(0).getLocality() + "," + address.get(0).getAdminArea() + ","
                     + address.get(0).getCountryName() + "," + address.get(0).getPostalCode();
         addressdisplay.setText(Address);
     }


     public void addCheckIntoDatabase(LocationDB.CheckIn checkin, int mode )
     {
         LocationDB.CheckIn newCheckin = checkin;
         String name;
         String address;

             List<LocationDB.CheckIn> temp = database.getStartingPoints();
         if(temp != null) {
             for (int i = 0; i < temp.size(); i++) {
                 double templat = temp.get(i).returnLat();
                 double templng = temp.get(i).returnLng();
                 int geocode = (int) temp.get(i).returnepochtime();

                 double lat = currentLocation.getLatitude();
                 double lng = currentLocation.getLongitude();
                 name = temp.get(i).returnName();
                 address = temp.get(i).returnAddress();
                 float[] results = new float[1];
                 Location.distanceBetween(templat, templng, lat, lng, results);
                 float distanceinMeters = results[0];
                 if (distanceinMeters > 30) {
                     continue;
                 } else {
                     //newCheckin = new LocationDB.CheckIn(currentLocation.getLatitude(), currentLocation.getLongitude(), System.currentTimeMillis(),name.toString(), address);
                     if (mode == 1) {
                         newCheckin = new LocationDB.CheckIn(currentLocation.getLatitude(), currentLocation.getLongitude(), System.currentTimeMillis(), name, address);
                     }

                     data.add(newCheckin);
                     customAdapter.notifyDataSetChanged();
                     database.addNewCheckIn(newCheckin, 0, geocode);
                     return;
                 }

             }
         }
             if(mode == 1) {
                 newCheckin = new LocationDB.CheckIn(currentLocation.getLatitude(), currentLocation.getLongitude(), System.currentTimeMillis(), imsickofthishit.getText().toString(), Address);
             }
             //newCheckin = new LocationDB.CheckIn(currentLocation.getLatitude(),currentLocation.getLongitude(),System.currentTimeMillis(),imsickofthishit.getText().toString(), Address);
             data.add(newCheckin);
             customAdapter.notifyDataSetChanged();
             database.addNewCheckIn(newCheckin, 1,0);

         }
         //customAdapter.notifyDataSetChanged();



     public void addCheckIn(View v)
     {
         LocationDB.CheckIn newCheckin;
            String name;
         String address;

         addCheckIntoDatabase(null,1);

;
         //customAdapter.notifyDataSetChanged();


     }

     public void gotoMapView(View v)
     {
         Intent a = new Intent(this,MapsActivity.class);
         Bundle b = new Bundle();
         b.putSerializable(key,  (Serializable)data);
         b.putDouble(key2,currentLocation.getLatitude());
         b.putDouble(key3,currentLocation.getLongitude());
       //  b.putSerializable(key2, database);
          a.putExtra(accesskey,b);

         startActivity(a);



     }


}