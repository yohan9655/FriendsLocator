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
import android.media.Image;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.Toast;


import com.google.android.gms.common.api.GoogleApiClient;
import com.parse.FindCallback;
import com.parse.GetDataCallback;
import com.parse.LogOutCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

public class List extends AppCompatActivity {
    private ParseQuery<ParseUser> parseQuery;
    private SearchView searchView;
    private ListView listView;
    private ParseUser user;
    private LocationManager locationManager;
    private LocationListener locationListener;
    private ParseGeoPoint parseGeoPoint;
    private ParseFile parseFile;
    private CustomListVIew customListVIew;
    private String[] username;
    private Toolbar toolbar;
    private Bitmap[] bitmaps;
    //boolean flag;
    int i = 0;
    int j = 0;
    //int temp;

    public void getPhoto() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), selectedImage);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();

                bitmap.compress(Bitmap.CompressFormat.PNG, 50, stream);

                byte[] byteArray = stream.toByteArray();

                ParseFile file = new ParseFile("image.png", byteArray);

                ParseUser object = ParseUser.getCurrentUser();

                object.put("image", file);

                //object.put("username", ParseUser.getCurrentUser().getUsername());

                object.saveInBackground(new SaveCallback() {
                    @Override
                    public void done(ParseException e) {
                        if (e == null) {
                            Toast.makeText(List.this, "Uploaded", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(List.this, "Error", Toast.LENGTH_SHORT).show();
                        }
                    }
                });

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }


    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

                    Location lastknownLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                    if (lastknownLocation != null) {
                        user.put("Location", new ParseGeoPoint(lastknownLocation.getLatitude(), lastknownLocation.getLongitude()));
                        user.saveInBackground();
                    } else
                        Toast.makeText(this, "Last known location not found", Toast.LENGTH_LONG);
                    locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);
                }
            }
        }
        if(requestCode == 2) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
            {
                getPhoto();
            }
        }
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);

        //final CustomListVIew customListVIew; //= new CustomListVIew(this,username)
        toolbar = (Toolbar) findViewById(R.id.toolbar);

        toolbar.setTitle("Friend Locator");
        toolbar.inflateMenu(R.menu.share_menu);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.share) {
                    if(item.getItemId() == R.id.share) {
                        if(checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            requestPermissions(new String []{Manifest.permission.READ_EXTERNAL_STORAGE}, 2);
                        }
                        else{
                            getPhoto();
                        }
                    }
                }
                if(item.getItemId() == R.id.logout) {
                    ParseUser.logOutInBackground(new LogOutCallback() {
                        @Override
                        public void done(ParseException e) {
                            if(e == null) {
                                Toast.makeText(List.this,"Logged out", Toast.LENGTH_SHORT).show();
                                Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
                return false;
            }
        });

        user = ParseUser.getCurrentUser();

        listView = findViewById(R.id.friendList);

        searchView = findViewById(R.id.search);

        final ArrayList<String> username1 = new ArrayList<>();
        final ArrayAdapter arrayAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, username1);
        //listView.setAdapter(arrayAdapter);

        ParseQuery<ParseUser> query = ParseUser.getQuery();

        query.whereNotEqualTo("username", ParseUser.getCurrentUser().getUsername());
        query.addAscendingOrder("username");

        query.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(java.util.List<ParseUser> objects, ParseException e) {
                if (e == null) {
                    username = new String[objects.size()];
                    bitmaps = new Bitmap[objects.size()];
                    if (objects.size() > 0) {
                        for (ParseUser user : objects) {
                            //username1.add(user.getUsername());
                            username[i] = user.getUsername();
                            i++;
                            parseFile = user.getParseFile("image");
                            if (parseFile != null) {
                                final int temp = j;
                                //Toast.makeText(List.this,Integer.toString(temp),Toast.LENGTH_LONG).show();
                                j++;
                                parseFile.getDataInBackground(new GetDataCallback() {
                                    @Override
                                    public void done(byte[] data, ParseException e) {
                                        if (e == null && data != null) {
                                            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);
                                            bitmaps[temp] = bitmap;
                                            //j++;

                                        } else {
                                            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_profile);
                                            bitmaps[temp] = bitmap;
                                            //j++;
                                        }
                                       // j++;
                                    }
                                });
                            }
                            else {
                                Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_action_profile);
                                bitmaps[j] = bitmap;
                                j++;
                            }
                            //i++;
                            //j++;
                            }
                        }
                    final Handler handler = new Handler();
                    handler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            customListVIew = new CustomListVIew(List.this,username, bitmaps);
                            listView.setAdapter(customListVIew);//Do something after 100ms
                        }
                    }, 500);

                        //Toast.makeText(List.this,Integer.toString(i) +" " + Integer.toString(j), Toast.LENGTH_SHORT).show();
                        //listView.setAdapter(arrayAdapter);
                    } else {
                        e.printStackTrace();
                    }
                //listView.setAdapter(customListVIew);
                }
            });

        //customListVIew = new CustomListVIew(List.this,username, bitmaps);
        //customListVIew.notifyDataSetChanged();
        //listView.setAdapter(customListVIew);



        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                String friend = listView.getItemAtPosition(position).toString();
                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                intent.putExtra("friendName",friend);
                startActivity(intent);
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {

                customListVIew.getFilter().filter(query);
                //parseQuery = ParseQuery.getQuery(query);


                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                customListVIew.getFilter().filter(newText);
                return false;
            }
        });

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
            else
            {
                Toast.makeText(this,"Last known location not found",Toast.LENGTH_LONG);
            }
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,3000,10, locationListener);

        }

    }

}
