package com.thecupboardapp.cupboard;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    private final String TAG = "SignInActivity";
    private FirebaseAuth mAuth;

    private String mEmail;
    private String mPassword;

    private EditText mEmailEditText;
    private EditText mPasswordEditText;
    private Button mSignInButton;
    private Button mCreateAccountButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        mAuth = FirebaseAuth.getInstance();

        mEmailEditText = (EditText) findViewById(R.id.sign_in_email);
        mPasswordEditText = (EditText) findViewById(R.id.sign_in_password);
        mSignInButton = (Button) findViewById(R.id.button_sign_in);
        mCreateAccountButton = (Button) findViewById(R.id.button_create_account);

        mEmailEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // nope
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mEmail = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // not gonna happen
            }
        });

        mPasswordEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // nope again
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                mPassword = s.toString();
            }

            @Override
            public void afterTextChanged(Editable s) {
                // uh uh
            }
        });

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(verifyEmail(mEmail)) signIn();
                else Toast.makeText(SignInActivity.this, "Please enter a valid email address",
                        Toast.LENGTH_SHORT).show();
            }
        });

        mCreateAccountButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "mcreateacc clicked");
            }
        });
    }

    public static Intent newIntent(Context packageContext) {
        Intent intent = new Intent(packageContext, SignInActivity.class);
        return intent;
    }

    public static boolean verifyEmail(String email){
        if (email.contains(Character.toString('@'))
                && email.contains(Character.toString('.'))) return true;
        else return false;
    }

    private void signIn() {

        // Error catch
        if (mEmail == null || mPassword == null) {
            Toast.makeText(SignInActivity.this, "Please enter all fields",
                    Toast.LENGTH_SHORT).show();
            return;
        }

        mAuth.signInWithEmailAndPassword(mEmail, mPassword)
            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithEmail:success");

                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        Toast.makeText(SignInActivity.this, "id: " + user.getUid(),
                                Toast.LENGTH_SHORT).show();

                        UserData.get(SignInActivity.this).updateFromFirebase(user);

//                        updateUI(user);
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithEmail:failure", task.getException());
                        Toast.makeText(SignInActivity.this, "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
    }
}
