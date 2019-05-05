package com.example.hp.friendslocator;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private String username;
    private ParseUser friend;
    private LatLng location;
    private ParseGeoPoint coordinates;
    private Marker marker;
    private Timer timer;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private ParseGeoPoint parseGeoPoint;
    private ParseUser user;
    private Bitmap bitmap;

    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){

            if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                Location lastknownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if(lastknownLocation != null) {
                    user.put("Location", new ParseGeoPoint(lastknownLocation.getLatitude(), lastknownLocation.getLongitude()));
                    user.saveInBackground();
                }
                else{
                    Toast.makeText(this,"Last known location not found",Toast.LENGTH_LONG);
                }
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,0,0, locationListener);
            }
        }
    }



    public void update() {

        //marker.setIcon(bitmap);
        marker = mMap.addMarker(new MarkerOptions().position(location).title(username));
        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location,5));
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        username = intent.getStringExtra("friendName");
        user = ParseUser.getCurrentUser();


        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;
       final ParseQuery<ParseUser> query = ParseUser.getQuery();
        query.whereEqualTo("username",username);

        final Handler handler = new Handler();

        final Runnable runnable = new Runnable() {
            @Override
            public void run() {
                try {
                    friend = query.find().get(0);

                    coordinates = friend.getParseGeoPoint("Location");
                    if(coordinates != null) {
                        location = new LatLng(coordinates.getLatitude(), coordinates.getLongitude());
                        if (marker != null) {
                            //marker.remove();
                            if(marker.getPosition().latitude != location.latitude || marker.getPosition().longitude != location.longitude ) {
                                marker.remove();
                                update();
                            }
                        }
                        else
                        {
                            update();
                        }
                        handler.postDelayed(this,2000);
                    }
                    else
                    {
                        Toast.makeText(MapsActivity.this,"Location not found",Toast.LENGTH_LONG).show();
                    }

                } catch (ParseException e) {
                    Toast.makeText(MapsActivity.this,"Server Issue",Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }

            }
        };
        handler.post(runnable);

        parseGeoPoint = new ParseGeoPoint();

        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                parseGeoPoint.setLatitude(location.getLatitude());
                parseGeoPoint.setLongitude(location.getLongitude());
                user.put("Location", parseGeoPoint);
                user.saveInBackground();
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},1);
        } else {
            Location lastknownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if(lastknownLocation != null) {
                user.put("Location", new ParseGeoPoint(lastknownLocation.getLatitude(), lastknownLocation.getLongitude()));
                user.saveInBackground();
            }
            else {
                Toast.makeText(this,"Last known location not found",Toast.LENGTH_LONG);
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000,10, locationListener);

        }

    }
}
