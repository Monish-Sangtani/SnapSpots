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
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Button;
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
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.*;

import java.util.ArrayList;

public class PhotoActivity extends AppCompatActivity {

    private ViewFlipper mVF;

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

        getPictures();

    }


    private void getPictures() {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference photosRef = database.getReference("pictures").child("ChiNfBGWOYRekw95RT4toABezwp2");

        photosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot photoX : dataSnapshot.getChildren()) {

                    if(mPhotoIds.contains(photoX.child("id").getValue().toString())){
                        Photo photo = new Photo(null,
                                (String) photoX.child("creationDate").getValue(),
                                (String) photoX.child("expirationDate").getValue(),
                                (String) photoX.child("comment").getValue(),
                                (String) photoX.child("creator").getValue());
                        photo.setUri(photoX.child("uri").getValue().toString());
                        photo.setId(photoX.child("id").getValue().toString());

                        mPhotos.add(photo);
                    }
                }
                mVF.removeAllViews();
                int i = 1;
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

                    if(mUser.getUid() == p.getCreator() || mUser.getEmail().equals("test@test.com")){
                        Button btnDelete = new Button(TAG);
                        btnDelete.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT));
                        btnDelete.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.BOTTOM | Gravity.LEFT);
                        btnDelete.setText("Delete");
                        btnDelete.setOnClickListener(new DeleteButtonOnClickListener(p.getId(), TAG));
                        ll.addView(btnDelete);
                    }

                    TextView photoNumber = new TextView(TAG);
                    photoNumber.setLayoutParams(new LinearLayoutCompat.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,LinearLayout.LayoutParams.WRAP_CONTENT));
                    photoNumber.setGravity(Gravity.RIGHT | Gravity.BOTTOM);
                    photoNumber.setText(i + "/" + mPhotos.size());
                    ll.addView(photoNumber);
                    i++;
                    mVF.addView(ll);

                }
                mVF.setOnTouchListener(new View.OnTouchListener() {
                    @Override
                    public boolean onTouch(View v, MotionEvent event) {

                        float downX=0;
                        switch (event.getAction()){
                            case MotionEvent.ACTION_DOWN:
                            {
                                // store the X value when the user's finger was pressed down
                                downX = event.getX();
                                break;
                            }
                            case MotionEvent.ACTION_UP: {
                                // Get the X value when the user released his/her finger
                                float currentX = event.getX();

                                // going backwards: pushing stuff to the right
                                if (downX < currentX) {
                                    mVF.showPrevious();
                                }
                                // going forwards: pushing stuff to the left
                                if (downX > currentX) {
                                    // Flip!
                                    mVF.showNext();
                                }
                                break;
                            }
                        }
                        return true;
                    }
                });
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Error catching here
            }
        });


    }

}


