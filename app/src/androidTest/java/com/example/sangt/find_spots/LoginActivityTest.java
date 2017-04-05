package com.example.sangt.find_spots;

import android.support.test.rule.ActivityTestRule;
import android.test.ActivityInstrumentationTestCase;
import android.test.ActivityInstrumentationTestCase2;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by ncarr on 4/5/2017.
 */
public class LoginActivityTest extends ActivityInstrumentationTestCase {
    public LoginActivityTest(){
        super("com.exmple.sang.find_spots", LoginActivity.class);
    }

    @Rule
    public ActivityTestRule<LoginActivity> mActivityTestRule = new ActivityTestRule<LoginActivity>(LoginActivity.class);
    private LoginActivity mLoginActivity = null;
    @Before
    public void setUp() throws Exception {
        super.setUp();
        mLoginActivity = mActivityTestRule.getActivity();
    }

    @Test
    public void testCorrectLogin(){
        String email = "a@a.com";
        String passwd = "carroll1";
        FirebaseAuth auth= FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, passwd);
        assertNotNull(auth.getCurrentUser());
        assertTrue(email.equals(auth.getCurrentUser().getEmail().toString()));
        auth.signOut();
    }

    @Test
    public void testIncorrectLogin(){
        String email = "a@a.com";
        String passwd = "wrong password";
        FirebaseAuth auth = FirebaseAuth.getInstance();
        auth.signInWithEmailAndPassword(email, passwd);
        assertNull(auth.getCurrentUser());
        auth.signOut();
    }

    @After
    public void tearDown() throws Exception {
        mLoginActivity = null;
    }

}