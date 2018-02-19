package com.thecupboardapp.cupboard;

import android.content.Context;
import android.test.UiThreadTest;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.CoreMatchers.*;
import static org.mockito.Mockito.*;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import android.content.SharedPreferences;

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