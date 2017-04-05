package com.example.sangt.find_spots;

import android.app.Instrumentation;
import android.content.Intent;
import android.support.test.rule.ActivityTestRule;
import android.util.Log;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Calendar;

import static org.junit.Assert.*;

/**
 * Created by ncarr on 4/5/2017.
 */
public class PhotoActivityTest {
    @Rule
    public ActivityTestRule<PhotoActivity> mActivityTestRule = new ActivityTestRule<PhotoActivity>(PhotoActivity.class);
    private PhotoActivity mPhotoActivity= null;
    private ArrayList<Photo> photoList = new ArrayList<>();
    @Before
    public void setUp() throws Exception {
        for(int i=0; i<3;i++){
            photoList.add(new Photo(null, "Today","Tomorrow","Comment "+ i, "Nick Carroll"));
        }
        Intent data = new Intent();
        data.putParcelableArrayListExtra("photos",photoList);

        mPhotoActivity = mActivityTestRule.launchActivity(data);
        mPhotoActivity.setmPhotoIds(photoList);
    }

    @Test
    public void testLoad(){
        assertEquals(3, mPhotoActivity.getmPhotoIds().size());
    }

    @After
    public void tearDown() throws Exception {
        photoList = null;
        mPhotoActivity = null;
    }

}