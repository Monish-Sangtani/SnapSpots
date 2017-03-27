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
    public DeleteButtonOnClickListener(String key){
        dataKey = key;
    }
    @Override
    public void onClick(View v) {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        Query pictureToDelete = ref.child("pictures").child("CSLgZ1y7yWTTOoZRqUCbIwlnZP13").equalTo(dataKey);

        pictureToDelete.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot pictureSnapshot: dataSnapshot.getChildren()) {
                    Log.w("Key to delete: " + pictureSnapshot.getKey().toString(), "d**************");
                    pictureSnapshot.getRef().removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
