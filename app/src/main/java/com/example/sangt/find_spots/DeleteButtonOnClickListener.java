package com.example.sangt.find_spots;

import android.util.Log;
import android.view.View;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

/**
 * Created by Owner on 3/27/2017.
 */

public class DeleteButtonOnClickListener implements View.OnClickListener {
    String dataKey;
    PhotoActivity activity;
    public DeleteButtonOnClickListener(String key, PhotoActivity activity){
        dataKey = key;
        this.activity = activity;
    }
    @Override
    public void onClick(View v) {
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference photosRef = database.getReference("pictures").child("ChiNfBGWOYRekw95RT4toABezwp2");

        photosRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot pictureSnapshot: dataSnapshot.getChildren()) {
                    if(pictureSnapshot.getKey().toString().equals(dataKey)){
                        pictureSnapshot.getRef().removeValue();
                    }
                }
                activity.finish();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
