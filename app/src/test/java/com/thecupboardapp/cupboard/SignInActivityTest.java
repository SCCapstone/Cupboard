package com.thecupboardapp.cupboard;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;

import com.thecupboardapp.cupboard.activities.SignInActivity;

import static org.junit.Assert.*;

/**
 * Created by William on 1/31/2018.
 */
public class SignInActivityTest {

    @Test
    public void valid_email_returns_true_verifyEmail(){
        String email = "username@example.com";
        boolean result = SignInActivity.verifyEmail(email);
        assertTrue(result);
    }

    @Test
    public void invalid_email_returns_false_verifyEmail(){
        String email = "Billy Bob Joe";
        boolean result = SignInActivity.verifyEmail(email);
        assertFalse(result);
    }

    @Test
    public void onCreate() throws Exception {
    }

    @Test
    public void newIntent() throws Exception {
    }

}