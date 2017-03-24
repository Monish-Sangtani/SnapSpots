package com.example.sangt.find_spots;

import android.graphics.Bitmap;
import android.graphics.Picture;
import android.location.Location;
import android.net.Uri;
import android.os.Debug;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.fitness.data.Goal;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;

import static android.R.attr.data;

public class CameraActivity extends AppCompatActivity {

    FirebaseUser mUser;
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    StorageReference mStorageRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        Bundle extras = getIntent().getExtras();

        setContentView(R.layout.activity_camera);
//        Bundle extras = savedInstanceState.getExtras();
        Bitmap imageBitmap = (Bitmap) extras.get("data");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] imageData = baos.toByteArray();
        Location loc = (Location) extras.get("location");
        addImageToDatabase(mUser.getUid(), loc, imageData);

    }

    private void addImageToDatabase(String clientId, Location location, byte[] imageData){
        final DatabaseReference pictures = mDatabase.getReference("pictures").child(clientId);
        final String imageKey = pictures.push().getKey();

        Calendar calObj = Calendar.getInstance();
        SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mm:ss");
        String date = dateFormat.format(calObj.getTime());
        final Photo pic = new Photo(location, date, "", "", clientId);
        pic.setId(imageKey);

        ByteArrayInputStream inputStream = new ByteArrayInputStream(imageData);
        StorageReference storageRef = mStorageRef.child("pictures/" + clientId + "/" + imageKey + ".png");
        UploadTask uploadTask = storageRef.putStream(inputStream);
        uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                @SuppressWarnings("VisibleForTests") Uri uri = taskSnapshot.getDownloadUrl();
                pic.setUri(uri.toString());
                HashMap<String, Object> temp = new HashMap<>();
                temp.put(imageKey, pic);
                pictures.updateChildren(temp);
//                Toast.makeText(getParentActivityIntent(), "Picture saved!", Toast.LENGTH_SHORT).show();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {

            }
        });
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
