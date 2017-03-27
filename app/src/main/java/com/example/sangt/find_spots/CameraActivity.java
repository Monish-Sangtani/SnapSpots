package com.example.sangt.find_spots;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

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
import java.util.Date;
import java.util.HashMap;

public class CameraActivity extends AppCompatActivity {

    FirebaseUser mUser;
    FirebaseAuth mAuth;
    FirebaseDatabase mDatabase;
    StorageReference mStorageRef;
    Calendar myCalendar = Calendar.getInstance();
    Date currentDate = myCalendar.getTime();


    private DatePickerDialog.OnDateSetListener date;
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yy hh:mm:ss");


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        final Bundle extras = getIntent().getExtras();

        setContentView(R.layout.activity_camera);

        ImageView image = (ImageView) findViewById(R.id.photoImageView);
        Button sendButton = (Button) findViewById(R.id.sendPhotoButton);
        final TextView deletionDate = (TextView) findViewById(R.id.deletionDateView);

        final Bitmap imageBitmap = (Bitmap) extras.get("data");
        final Activity currentActivity = this;
        image.setImageBitmap(imageBitmap);

        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                imageBitmap.compress(Bitmap.CompressFormat.PNG, 100, baos);
                byte[] imageData = baos.toByteArray();
                Location loc = (Location) extras.get("location");
                addImageToDatabase(mUser.getUid(), loc, imageData);
                currentActivity.finish();
            }
        });



        date = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                myCalendar.set(Calendar.YEAR, year);
                myCalendar.set(Calendar.MONTH, month);
                myCalendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateLabel(deletionDate);

            }
        };

        deletionDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                DatePickerDialog dialog = new DatePickerDialog(findViewById(android.R.id.content).getContext(), date,
                        myCalendar.get(Calendar.YEAR), myCalendar.get(Calendar.MONTH), myCalendar.get(Calendar.DAY_OF_MONTH));
                dialog.getDatePicker().setMinDate(System.currentTimeMillis());
                dialog.setTitle("Set Deletion Date");
                dialog.show();
            }
        });


        //        Bundle extras = savedInstanceState.getExtras();


    }

    private void addImageToDatabase(String clientId, Location location, byte[] imageData){
        final DatabaseReference pictures = mDatabase.getReference("pictures");
        final String imageKey = pictures.push().getKey();
        final TextView messageView = (TextView) findViewById(R.id.messageTextView);


        String date = dateFormat.format(myCalendar.getTime());
        String currentDateStr = dateFormat.format(currentDate);
        final Photo pic = new Photo(location, currentDateStr, date, messageView.getText().toString(), clientId);
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


    private void updateLabel(TextView editText){
        SimpleDateFormat dateFormatVis = new SimpleDateFormat("MM/dd/yy");

        editText.setText("Will be deleted at the end of: " + dateFormatVis.format((myCalendar.getTime())));
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
