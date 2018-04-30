package com.thecupboardapp.cupboard.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.thecupboardapp.cupboard.R;
import com.thecupboardapp.cupboard.UserData;

public class SignInActivity extends AppCompatActivity {
    private final String TAG = "SignInActivity";
    private FirebaseAuth mAuth;

    private String mEmail;
    private String mPassword;

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private Button mSignInButton;
    private Button mCreateAccountButton;

    static final int SIGN_IN_RESULT_CODE = 1;
    static final int NEW_ACCOUNT_RESULT_CODE = 2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();

        mEmail = "";
        mPassword = "";

        mEmailEditText = findViewById(R.id.sign_in_email);
        mPasswordEditText = findViewById(R.id.sign_in_password);
        mSignInButton = findViewById(R.id.button_sign_in);
        mCreateAccountButton = findViewById(R.id.button_create_account);

        mEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEmail = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPassword = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            signIn();
            }
        });

        mCreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                createAccount();
            }
        });
    }

    // Create an intent for this
    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, SignInActivity.class);
        return intent;
    }

    // Unit test?
    public static boolean verifyEmail(String email){
        if (email.contains(Character.toString('@'))
                && email.contains(Character.toString('.'))) return true;
        else return false;
    }

    // If the fields are good, they wil
    private boolean verifyFields() {
        // Check if email field or password is filled out
        if (TextUtils.isEmpty(mEmail) || TextUtils.isEmpty(mPassword)) {
            Toast.makeText(this, "Please enter all fields!", Toast.LENGTH_SHORT).show();
            return false;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(mEmail).matches()) {
            Toast.makeText(this, "Invalid email address", Toast.LENGTH_SHORT).show();
            return false;
        } else if (mPassword.length() < 8) {
            Toast.makeText(this, "Password must be 8 characters long",
                    Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void signIn() {
        // check the fields
        if (!verifyFields()) {
            return;
        }

        mAuth.signInWithEmailAndPassword(mEmail, mPassword)
            .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    UserData.get(SignInActivity.this).setReferences();
                    UserData.get(SignInActivity.this).setUpListeners();
                    setResult(SIGN_IN_RESULT_CODE);
                    finish();
                } else {
                    Toast.makeText(SignInActivity.this, task.getException().getMessage(),
                            Toast.LENGTH_SHORT).show();
                }
            });
    }

    private void createAccount(){
        // check the fields
        if (!verifyFields()) {
            return;
        }

        mAuth.createUserWithEmailAndPassword(mEmail, mPassword).addOnCompleteListener(this, task -> {
            if (task.isSuccessful()) {
                UserData.get(SignInActivity.this).setReferences();
                UserData.get(SignInActivity.this).syncWithFirebase();
                UserData.get(SignInActivity.this).setUpListeners();
                setResult(NEW_ACCOUNT_RESULT_CODE);
                finish();
            } else {
                Toast.makeText(SignInActivity.this, task.getException().getMessage(),
                        Toast.LENGTH_SHORT).show();
            }
        });
    }
}
