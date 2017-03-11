package com.example.sangt.find_spots;

import android.graphics.Picture;
import android.os.Debug;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;

public class CameraActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

    }

    //This places the picture information in the database
    private void savePicture(Photo photo){
        //This gets a reference to our database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //This gets the reference to the specific table that you want to access
        // (In this case we are accessing the Photos table)
        DatabaseReference photosRef = database.getReference("Photos");

        //This lets you generate a unique ID for the photo (That's why you don't need to pass the
        // id in when constructing the Photo)
        String photoId = photosRef.push().getKey();
        // Assign the ID to the Photo (Not neccessary for Firebase, but nice for coding)
        photo.setId(photoId);

        //Create a HashMap that will contain the Photo that we want to put in the database
        HashMap<String, Object> tempMap = new HashMap<>();

        //Key is the Id that we generated earlier and the Value is the Photo
        tempMap.put(photoId, photo);

        //Save it to the database (This still works if there is an Item in the table that
        // has the same ID because it just overwrites it)
        photosRef.updateChildren(tempMap);
    }


    //Just shows the general idea of how I usually get things from the database.
    private void getPictures (){
        //Same idea as above: get reference to database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        //Get reference to table (can use orderBy... to get a certain ordering of the data)
        DatabaseReference photosRef = database.getReference("Photos");

        //Add listener to database to get values
        photosRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //Add code here for when data is found
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //Error catching here
            }
        });
    }
}
