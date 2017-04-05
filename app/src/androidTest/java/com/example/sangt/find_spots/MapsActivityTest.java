package com.example.sangt.find_spots;

import android.support.test.rule.ActivityTestRule;
import android.view.View;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by ncarr on 4/5/2017.
 */
public class MapsActivityTest {

    @Rule
    public ActivityTestRule<MapsActivity> mActivityTestRule = new ActivityTestRule<MapsActivity>(MapsActivity.class);
    private MapsActivity mMapsActivity = null;

    @Before
    public void setUp() throws Exception {
        mMapsActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testLauch(){
        View btn = mMapsActivity.findViewById(R.id.btn_add);
        View map = mMapsActivity.findViewById(R.id.map);

        assertNotNull(btn);
        assertNotNull(map);
    }

    @After
    public void tearDown() throws Exception {
        mMapsActivity = null;
    }

}