package com.example.vikra.location;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.RelativeLayout;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.jar.Attributes;

import static com.example.vikra.location.MainActivity.REQUEST_LOCATION;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener
{

    private GoogleMap mMap;
    private MapView Maps;
    SupportMapFragment mapFragment;
    private final String key = "Key";
    private final String key2 = "Key2";
    private final String key3 = "kkl";
    private final String addresskey = "addresskey";
    private final String timekey = "timekey";
    private final String accesskey = "access";
    private String nameofnewmarker = "";
    private Marker currentMarker;
    int u = 0;
    private EditText ll;
    GoogleApiClient mGoogleApiClient;
    Location mlastLocation;
    LocationRequest mLocationRequest;
    Location currentLocation;
    private List<LocationDB.CheckIn> bluemarkers;
    private double currentlat;
    private double currentlng;
    private double lastcheckinlat;
    private double lastcheckinlng;

    private List<LocationDB.CheckIn> data = null;
    View mapView;
    private  LocationDB database;
    private int Geocode;
    private List<MarkerGeocode> yuk;
    private static final String checkinkey = "checkinkey";
    private static final String kkki = "kkki";
    public MapsActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
         mapView = mapFragment.getView();
        mapFragment.getMapAsync(this);
        Intent a = getIntent();
        Bundle b = a.getBundleExtra(accesskey);
        data = (ArrayList< LocationDB.CheckIn>)b.getSerializable(key);
        currentlat = b.getDouble(key2);
        currentlng = b.getDouble(key3);
        database = new LocationDB(this.getApplicationContext());
        ll = (EditText)findViewById(R.id.markername);
        yuk = new ArrayList<MarkerGeocode>();
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
            // mGoogleApiClient.connect();
            //createLocationRequest();
        }
        //database = (LocationDB)b.getSerializable(key2);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */

    class MarkerGeocode
    {
        private Marker marker;
        private int Geocode;
        public MarkerGeocode(Marker marker, int Geocode)
        {
            this.marker = marker;
            this.Geocode = Geocode;
        }

        public Marker getMarker() {
            return marker;
        }

        public int getGeocode() {
            return Geocode;
        }
    }




    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        setupBlueMarkers();

        // Add a marker in Sydney and move the camera
        //LatLng sydney = new LatLng(-34, 151);
        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

        }
        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);
        LatLng x = new LatLng(currentlat,currentlng);
        CameraUpdate location = CameraUpdateFactory.newLatLng(
                x);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(x,12));
        //mMap.animateCamera(location);
        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {


                return false;
            }
        });
        setupRedMarkers();
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                nameofnewmarker = ll.getText().toString();
                //marker.setTitle(nameofnewmarker);
                Geocoder geocoder;
                // Marker marker = currentMarker;
                LatLng coords = latLng;
                int sp = 3;


                String name = "jjjjj";
                List<Address> address = null;
                String Address = "";
                geocoder = new Geocoder(getApplicationContext());
                try {
                    address = geocoder.getFromLocation(coords.latitude,coords.longitude,1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(address == null) {
                    Address = "The address is :" + address.get(0).getAddressLine(0) + ","
                            + address.get(0).getLocality() + "," + address.get(0).getAdminArea() + ","
                            + address.get(0).getCountryName() + "," + address.get(0).getPostalCode();
                }
                nameofnewmarker = ll.getText().toString();
                LocationDB.CheckIn a = new LocationDB.CheckIn(coords.latitude,coords.longitude,System.currentTimeMillis(),nameofnewmarker,Address);
                Geocode =   database.addNewCheckIn(a,3,-1);

                //marker.setTitle(nameofnewmarker);
               // marker.showInfoWindow();
                //marker.setDraggable(false);
                Marker y = mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .draggable(true)
                        .title(nameofnewmarker)
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));
                MarkerGeocode newmarkergeocode = new MarkerGeocode(y,Geocode);
                y.showInfoWindow();
                yuk.add(newmarkergeocode);


            }
        });
        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if(marker.isInfoWindowShown())
                {
                    marker.hideInfoWindow();
                }
                else
                marker.showInfoWindow();
                return true;
            }
        });



        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {
                marker.setDraggable(true);





            }

            @Override
            public void onMarkerDrag(Marker marker) {
                //mMap.moveCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                CameraUpdate location = CameraUpdateFactory.newLatLng(
                        marker.getPosition());
                mMap.animateCamera(location);
            }

            @Override
            public void onMarkerDragEnd(Marker marker) {


                String name = marker.getTitle();
                LatLng latlng = marker.getPosition();
                double yu = latlng.latitude;
                double yui = latlng.longitude;
                Geocoder geocoder;
                // Marker marker = currentMarker;
                //LatLng coords = latLng;
                //int sp = 3;


                //String name = "jjjjj";
                List<Address> address = null;
                String Address = "";
                geocoder = new Geocoder(getApplicationContext());
                try {
                    address = geocoder.getFromLocation(yu,yui,1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(address == null) {
                    Address = "The address is :" + address.get(0).getAddressLine(0) + ","
                            + address.get(0).getLocality() + "," + address.get(0).getAdminArea() + ","
                            + address.get(0).getCountryName() + "," + address.get(0).getPostalCode();
                }


                database.update(name,yu,yui,System.currentTimeMillis(),Address);

                /*
                Geocoder geocoder;
               // Marker marker = currentMarker;
                LatLng coords = marker.getPosition();
                int sp = 3;


                String name = "jjjjj";
                List<Address> address = null;
                String Address = "";
                geocoder = new Geocoder(getApplicationContext());
                try {
                    address = geocoder.getFromLocation(coords.latitude,coords.longitude,1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if(address == null) {
                    Address = "The address is :" + address.get(0).getAddressLine(0) + ","
                            + address.get(0).getLocality() + "," + address.get(0).getAdminArea() + ","
                            + address.get(0).getCountryName() + "," + address.get(0).getPostalCode();
                }
                nameofnewmarker = ll.getText().toString();
                LocationDB.CheckIn a = new LocationDB.CheckIn(coords.latitude,coords.longitude,System.currentTimeMillis(),nameofnewmarker,Address);
                database.addNewCheckIn(a,3,-1);

                marker.setTitle(nameofnewmarker);
                marker.showInfoWindow();
                marker.setDraggable(false);
                */



            }
        });




    }





    void setupRedMarkers()
    {
        if(data == null)
        {
            return;
        }
        for(int i = 0; i < data.size(); i++)
        {
            LatLng temp = new LatLng(data.get(i).returnLat(),data.get(i).returnLng());
            //MarkerOptions tempp = new MarkerOptions();
            //tempp.position(temp);

           Marker newmarker = mMap.addMarker(new MarkerOptions().position(temp));
            newmarker.setDraggable(false);
            newmarker.setTitle(data.get(i).returnName());
            newmarker.showInfoWindow();

        }
    }

    void setupBlueMarkers()
    {
       bluemarkers =  database.getFalseCheckIns();
        List<LocationDB.CheckIn> data = bluemarkers;
        for(int i = 0; i < data.size(); i++)
        {
            LatLng temp = new LatLng(data.get(i).returnLat(),data.get(i).returnLng());
            //MarkerOptions tempp = new MarkerOptions();
            //tempp.position(temp);

            Marker newmarker = mMap.addMarker(new MarkerOptions().position(temp));
            newmarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
            newmarker.setDraggable(false);
            newmarker.setTitle(data.get(i).returnName());
            newmarker.showInfoWindow();

        }
    }



    public void backtoMainScreen(View v)
    {

        Intent a = new Intent(this,MainActivity.class);
        startActivity(a);

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
            Location locationn = mlastLocation;
            //LatLng x = new LatLng(locationn.getLatitude(),locationn.getLongitude());
            //CameraUpdate location = CameraUpdateFactory.newLatLng(x);
           // mMap.animateCamera(CameraUpdateFactory.zoomBy(12));

            //mMap.animateCamera(location);
            currentLocation = locationn;
            database.getStartingPoints();
            List<LocationDB.CheckIn> temp = database.getStartingPoints();
            for(int i = 0; i < temp.size(); i++) {
                double templat = temp.get(i).returnLat();
                double templng = temp.get(i).returnLng();
                int geocode = (int) temp.get(i).returnepochtime();

                double lat = currentLocation.getLatitude();
                double lng = currentLocation.getLongitude();
                String name = temp.get(i).returnName();
                String  address = temp.get(i).returnAddress();
                float[] results = new float[1];
                Location.distanceBetween(templat, templng, lat, lng, results);
                float distanceinMeters = results[0];
                if (distanceinMeters > 30) {

                }
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
                //onStart();
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
    public void onLocationChanged(Location locationn) {

        if(locationn != null)
        {
            LatLng x = new LatLng(locationn.getLatitude(),locationn.getLongitude());
            CameraUpdate location = CameraUpdateFactory.newLatLng(
                   x);

            //mMap.animateCamera(location);
           // mMap.animateCamera(CameraUpdateFactory.zoomBy(12));
            currentLocation = locationn;
            //database.getFalseCheckIns();
            List<LocationDB.CheckIn> temp = database.getFalseCheckIns();//StartingPoints();
            for(int i = 0; i < temp.size(); i++) {
                double templat = temp.get(i).returnLat();
                double templng = temp.get(i).returnLng();
                int geocode = (int) temp.get(i).returnepochtime();

                double lat = currentLocation.getLatitude();
                double lng = currentLocation.getLongitude();
                String name = temp.get(i).returnName();
                String  address = temp.get(i).returnAddress();
                float[] results = new float[1];
                Location.distanceBetween(templat, templng, lat, lng, results);
                float distanceinMeters = results[0];
                if (distanceinMeters <= 30) {
                    Intent a = new Intent(this, NameofLocationPopupwindow.class);
                    Bundle b = new Bundle();
                    b.putString(addresskey,name);
                    b.putLong(timekey,temp.get(i).returnepochtime());
                    b.putDouble(key2,lat);
                    b.putDouble(key3,lng);
                    a.putExtra(accesskey,b);
                    database.update(name,templat,templng,System.currentTimeMillis(),address);
                    startActivity(a);
                    break;

                }
                else
                    continue;
            }



            //updateUI(currentLocation.getLatitude(),currentLocation.getLongitude());
            // updateUI();
        }
    }
}
