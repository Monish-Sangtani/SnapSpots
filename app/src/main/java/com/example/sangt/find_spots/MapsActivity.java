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
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
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
import com.google.maps.android.clustering.Cluster;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

import java.io.ByteArrayOutputStream;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private Button btnAddSpot;
    private FirebaseAuth auth;
    private ClusterManager<CustomMarker> mClusterManager;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mm:ss");



    //private ClusterManager<MyItem> mClusterManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);



       // getPictures();
        btnAddSpot= (Button) findViewById(R.id.btn_add);
        btnAddSpot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(isNetworkAvailable())
                {
                    //Intent openCamera = new Intent(MapsActivity.this,CameraActivity.class);
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                        startActivityForResult(takePictureIntent, 111);
                    }
                }
                else
                {
                    Toast.makeText(getApplication(), "Must have internet connection.",Toast.LENGTH_LONG).show();
                }



            }
        });




        auth = FirebaseAuth.getInstance();
        if (auth.getCurrentUser() != null) {
            Toast.makeText(getApplication(), auth.getCurrentUser().getEmail()+" Logged In Successfully",Toast.LENGTH_LONG).show();
        }

        mapFragment.getMapAsync(this);
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    private static final String[] INITIAL_PERMS={
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
        //mMap.setOnMarkerClickListener(this);

        mClusterManager = new ClusterManager<CustomMarker>(this, mMap);

        mMap.setOnCameraIdleListener(mClusterManager);
        mMap.setOnMarkerClickListener(mClusterManager);

        //mClusterManager.onMarkerClick()



        mMap.getUiSettings().setMyLocationButtonEnabled(true);
        mMap.getUiSettings().setMapToolbarEnabled(true);


        mClusterManager.setOnClusterItemClickListener(new ClusterManager.OnClusterItemClickListener<CustomMarker>() {
            @Override
            public boolean onClusterItemClick(CustomMarker item) {

                ArrayList<Photo> photosToSend = new ArrayList<Photo>();

                for(int i=0;i<markers.size();i++)
                {
                    if(item.getPosition().equals(markers.get(i).getPosition()))
                    {
                        photosToSend.add(0,photosToView.get(i));
                    }
                    else
                    {
                        if(mMap.getProjection().getVisibleRegion().latLngBounds.contains(markers.get(i).getPosition()))
                        {
                            photosToSend.add(photosToView.get(i));
                        }
                    }

                }
                Intent myIntent = new Intent(MapsActivity.this, PhotoActivity.class);




                myIntent.putExtra("photos", photosToSend);


                startActivity(myIntent);
                return false;
            }
        });

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            this.requestPermissions(INITIAL_PERMS, 1337);
        }

        try {
             mMap.setMyLocationEnabled(true);
        } catch (SecurityException e) {
            Log.d("D","NO PERMISION NO PERMISSION" );
        }


    }

    ArrayList<Photo> photosToView = new ArrayList<Photo>();
    private void getPictures () {
        //Same idea as above: get reference to database
        final FirebaseDatabase database = FirebaseDatabase.getInstance();

        final DatabaseReference photosRef = database.getReference("pictures");


        //Add listener to database to get values
        photosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                mClusterManager.clearItems();
                photosToView = new ArrayList<Photo>();

                markers = new ArrayList<MarkerOptions>();

                for (DataSnapshot photoX: dataSnapshot.getChildren()) {

                        if(photoX.child("lon").getValue()!=null&&photoX.child("lat").getValue()!=null)
                        {
                           // Double longitude = (Double) photoX.child("lon").getValue();
                            //Double latitude = (Double) photoX.child("lat").getValue();


                            Photo photo = photoX.getValue(Photo.class);

//                            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mm:ss");
//                            Calendar myCalendar = Calendar.getInstance();
//                            Date currentDate = myCalendar.getTime();
//
//
//                            String date = dateFormat.format(myCalendar.getTime());
//                            String currentDateStr = dateFormat.format(currentDate);

//                            if(!currentDate.before(new Date(dateFormat.format(photo.getExpirationDate()))))
//                            {
//                                //delete
//                                Log.d("DELETEDDELETEDELETE","d");
//                            }
                            //Date check (if expiration date is before current date then delete Photo)
                            Calendar calendar = Calendar.getInstance();

                            try {
                                Date currentDate = calendar.getTime();
                                Date expDate = dateFormat.parse(photo.getExpirationDate());
                                if(expDate.before(currentDate)){
                                    photosRef.child(photo.getId()).removeValue();
                                }else{
                                    Log.d("photo"+" "+photo.getComment()+" "+photo.getId(),"d");


                                    photosToView.add(photo);
                                    Log.d(photoX.getKey().toString(), "d");

                                    LatLng tempLoc = new LatLng(photo.getLat(), photo.getLon());

                                    MarkerOptions tempMarker = new MarkerOptions().position(tempLoc);
                                    markers.add(tempMarker);

//                                    for(int i=0;i<1000;i++)
//                                    {
//                                        Random r = new Random();
//                                        double randomValue = -90 + (90 - -90) * r.nextDouble();
//                                        double randomValue2 = -90 + (90 - -90) * r.nextDouble();
//
//                                        LatLng t = new LatLng(randomValue, randomValue2);
//
//                                        MarkerOptions tm = new MarkerOptions().position(t);
//                                        mClusterManager.addItem(new CustomMarker(tm));
//                                        //mMap.addMarker(tm);
//
//
//                                    }
                                    mClusterManager.addItem(new CustomMarker(tempMarker));
                                }

                            } catch (ParseException e) {
                                e.printStackTrace();
                            }
                            //Remove from firebase



//                            Photo photo = new Photo(longitude,latitude,
//                                    (String) photoX.child("creationDate").getValue(),
//                                    (String) photoX.child("expirationDate").getValue(),
//                                    (String) photoX.child("comment").getValue(),
//                                    (String) photoX.child("creator").getValue());
//                            photo.setUri(photoX.child("uri").getValue().toString());
//                            photo.setId(photoX.child("id").getValue().toString());



                            //mClusterManager.addItem(markers);

                           // mMap.addMarker(tempMarker);

                        }



                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Error catching here
            }
        });



    }
ArrayList<MarkerOptions> markers ;

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        if(requestCode == 111 && resultCode == this.RESULT_OK){
            Intent cameraIntent = new Intent( this, CameraActivity.class);
            cameraIntent.putExtras(data.getExtras());
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Criteria criteria = new Criteria();
            try {
                String locationProvider=locationManager.getBestProvider(criteria, false);
                Location loc;
                if(locationProvider==null)
                {
                    loc = new Location("");//provider name is unnecessary
                    loc.setLatitude(0.0d);//your coords of course
                    loc.setLongitude(0.0d);
                }
                else
                {
                    loc = locationManager.getLastKnownLocation(locationProvider);
                }


                cameraIntent.putExtra("location", loc);

            }catch (SecurityException e){
                e.printStackTrace();
            }
            startActivity(cameraIntent);
        }
    }

}
