package com.example.sangt.find_spots;

import android.graphics.Picture;
import android.location.Location;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.*;

import java.util.ArrayList;

public class PhotoActivity extends AppCompatActivity {
    private ImageView mImage;
    private TextView mDescription;

    private String mPhotoKey;

    FirebaseUser mUser;
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    StorageReference mStorageRef;

    ArrayList<Photo> mPhotos;


    private PhotoActivity TAG = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        setTitle("View Photos");

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();


        mPhotos = new ArrayList<Photo>();

        //Same idea as above: get reference to database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //Get reference to table (can use orderBy... to get a certain ordering of the data)
        DatabaseReference photosRef = database.getReference("Photos");


        ArrayList<String> photoIds = (ArrayList<String>) getIntent().getSerializableExtra("photos");

        for (String x : photoIds) {
            Log.d("SWAG: " + x, "d");
        }

        mImage = (ImageView) findViewById(R.id.current_picture);
        mDescription = (TextView) findViewById(R.id.current_picture_description);

        getPictures();



//        Glide.with(this).load(url).into(mImage);


    }


    private void getPictures() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference photosRef = database.getReference("pictures").child("CSLgZ1y7yWTTOoZRqUCbIwlnZP13");

        //Add listener to database to get values
        photosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot photoX : dataSnapshot.getChildren()) {
                    if (photoX.child("location") != null && photoX.child("location").child("longitude") != null && photoX.child("location").child("latitude") != null) {

                        if (photoX.child("location").child("longitude").getValue() != null && photoX.child("location").child("latitude").getValue() != null) {
                            Photo photo = new Photo(null,
                                    (String) photoX.child("creationDate").getValue(),
                                    (String) photoX.child("expirationDate").getValue(),
                                    (String) photoX.child("comment").getValue(),
                                    (String) photoX.child("creator").getValue());
                            photo.setUri(photoX.child("uri").toString());
                            photo.setId(photoX.child("id").toString());

                            mPhotos.add(photo);
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

}


