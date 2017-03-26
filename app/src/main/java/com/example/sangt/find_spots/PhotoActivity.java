package com.example.sangt.find_spots;

import android.graphics.Picture;
import android.location.Location;
import android.net.Uri;
import android.nfc.Tag;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

    private ViewFlipper mVF;

    private String mPhotoKey;

    FirebaseUser mUser;
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    StorageReference mStorageRef;

    ArrayList<Photo> mPhotos;
    ArrayList<String> mPhotoIds;

    private PhotoActivity TAG = this;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        setTitle("View Photos");

        mVF = (ViewFlipper) findViewById(R.id.photo_view_flipper);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();


        mPhotos = new ArrayList<Photo>();

        //Same idea as above: get reference to database
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        //Get reference to table (can use orderBy... to get a certain ordering of the data)
        DatabaseReference photosRef = database.getReference("Photos");


        mPhotoIds = (ArrayList<String>) getIntent().getSerializableExtra("photos");

        for (String x : mPhotoIds) {
            Log.d("SWAG: " + x, "d");
        }

//        mImage = (ImageView) findViewById(R.id.current_picture);
//        mDescription = (TextView) findViewById(R.id.current_picture_description);

        getPictures();



//        Glide.with(this).load(url).into(mImage);


    }


    private void getPictures() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference photosRef = database.getReference("pictures").child("CSLgZ1y7yWTTOoZRqUCbIwlnZP13");

        photosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot photoX : dataSnapshot.getChildren()) {
                    Log.w("CHILD ID: "+photoX.child("id").getValue(), "A*******");

                    if(mPhotoIds.contains(photoX.child("id").getValue().toString())){
                        Photo photo = new Photo(null,
                                (String) photoX.child("creationDate").getValue(),
                                (String) photoX.child("expirationDate").getValue(),
                                (String) photoX.child("comment").getValue(),
                                (String) photoX.child("creator").getValue());
                        photo.setUri(photoX.child("uri").getValue().toString());
                        Log.w(photo.getUri(), "d*****");
                        photo.setId(photoX.child("id").toString());

                        mPhotos.add(photo);
                    }
                }
                mVF.removeAllViews();
                for(Photo p : mPhotos){

                    LinearLayout ll = new LinearLayout(TAG);
                    ll.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,LinearLayout.LayoutParams.MATCH_PARENT));
                    ll.setOrientation(LinearLayout.VERTICAL);

                    ImageView newImageView = new ImageView(TAG);
                    newImageView.setLayoutParams( new LinearLayoutCompat.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                    ll.addView(newImageView);
                    Glide.with(TAG).load(Uri.parse(p.getUri())).into(newImageView);

                    TextView description = new TextView(TAG);
                    description.setLayoutParams( new LinearLayoutCompat.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                    ll.addView(description);
                    description.setText(p.getComment());

                    mVF.addView(ll);
                    
                }
                mVF.setFlipInterval(300);
                mVF.startFlipping();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Error catching here
            }
        });


    }

}


