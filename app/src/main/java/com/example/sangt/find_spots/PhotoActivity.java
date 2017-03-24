package com.example.sangt.find_spots;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.firebase.storage.*;

public class PhotoActivity extends AppCompatActivity {
    private ImageView image;
    private TextView description;
    private String url="https://firebasestorage.googleapis.com/v0/b/snapspots-14595.appspot.com/o/earth.png?alt=media&token=33b6f912-68ea-48bd-b1ec-cc000d9e813c";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);
        setTitle("View Photos");

        image = (ImageView) findViewById(R.id.current_picture);
        description = (TextView) findViewById(R.id.current_picture_description);

        Glide.with(this).load(url).into(image);

        description.setText("Image Description Text Goes Here");
    }

}
