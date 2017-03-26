package com.example.sangt.find_spots;

import android.Manifest;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.instantapps.PackageManagerWrapper;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private GoogleMap mMap;
    private Button btnAddSpot;
    private FirebaseAuth auth;

    //private ClusterManager<MyItem> mClusterManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        getPictures();
        btnAddSpot = (Button) findViewById(R.id.btn_add);
        btnAddSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Intent openCamera = new Intent(MapsActivity.this,CameraActivity.class);
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, 111);
                }


            }
        });

        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            Toast.makeText(getApplication(), auth.getCurrentUser().getEmail() + " Logged In Successfully", Toast.LENGTH_LONG).show();
        }

        mapFragment.getMapAsync(this);
    }


    private static final String[] INITIAL_PERMS = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
    };

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


        // Add a marker in Sydney and move the camera

        //mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        getPictures();
        // mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        mMap.setOnMarkerClickListener(this);


        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.requestPermissions(INITIAL_PERMS, 1337);
        }

        try {
            mMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            Log.d("D", "NO PERMISION NO PERMISSION");
        }


    }

    @Override
    public boolean onMarkerClick(Marker marker) {

        ArrayList<String> photosToSend = new ArrayList<String>();

        for (int i = 0; i < markers.size(); i++) {
            if (marker.getPosition().equals(markers.get(i).getPosition())) {
                photosToSend.add(0, photosToView.get(i));
            } else {
                if (mMap.getProjection().getVisibleRegion().latLngBounds.contains(markers.get(i).getPosition())) {
                    photosToSend.add(photosToView.get(i));
                }
            }

        }
        Intent myIntent = new Intent(MapsActivity.this, PhotoActivity.class);


        myIntent.putExtra("photos", photosToSend);


        startActivity(myIntent);
        return false;
    }

    ArrayList<String> photosToView = new ArrayList<String>();

    private void getPictures() {
        //Same idea as above: get reference to database
        FirebaseDatabase database = FirebaseDatabase.getInstance();


        DatabaseReference photosRef = database.getReference("pictures").child("CSLgZ1y7yWTTOoZRqUCbIwlnZP13");


        //Add listener to database to get values
        photosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                markers = new ArrayList<MarkerOptions>();

                for (DataSnapshot photoX : dataSnapshot.getChildren()) {
                    if (photoX.child("location") != null && photoX.child("location").child("longitude") != null && photoX.child("location").child("latitude") != null) {

                        if (photoX.child("location").child("longitude").getValue() != null && photoX.child("location").child("latitude").getValue() != null) {
                            Double longitude = (Double) photoX.child("location").child("longitude").getValue();
                            Double latitude = (Double) photoX.child("location").child("latitude").getValue();

                            photosToView.add(photoX.getKey().toString());
                            Log.d(photoX.getKey().toString(), "d");

                            LatLng tempLoc = new LatLng(latitude, longitude);

                            MarkerOptions tempMarker = new MarkerOptions().position(tempLoc);
                            markers.add(tempMarker);
                            mMap.addMarker(tempMarker);

                        }

                    }

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Error catching here
            }
        });


    }

    ArrayList<MarkerOptions> markers;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 111 && resultCode == this.RESULT_OK) {
            Intent cameraIntent = new Intent(this, CameraActivity.class);
            cameraIntent.putExtras(data.getExtras());
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            try {
                Location loc = locationManager.getLastKnownLocation(locationManager.getBestProvider(criteria, false));
                cameraIntent.putExtra("location", loc);

            } catch (SecurityException e) {
                e.printStackTrace();
            }
            startActivity(cameraIntent);
        }
    }
}
