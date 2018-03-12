package com.thecupboardapp.cupboard;

import com.thecupboardapp.cupboard.activities.SignInActivity;

import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

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