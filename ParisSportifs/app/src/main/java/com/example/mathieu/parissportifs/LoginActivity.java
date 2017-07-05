package com.example.mathieu.parissportifs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText inputEmail, inputPassword;
    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;
    private ProgressDialog progress;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        // set the view now
        setContentView(R.layout.activity_login);

        inputEmail = (EditText) findViewById(R.id.inputEmail);
        inputPassword = (EditText) findViewById(R.id.inputPassword);
        findViewById(R.id.buttonSignIn).setOnClickListener(this);
        findViewById(R.id.buttonResetPassword).setOnClickListener(this);
        findViewById(R.id.buttonCreateWithEmail).setOnClickListener(this);
        //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();


        FirebaseUser user = mAuth.getCurrentUser();
        if (user != null){signOut();}

        mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {

                    //startActivity(new Intent(LoginActivity.this, ScreenSlideActivity.class));
                    //LoginActivity.this.finish();
                } else{
                    return;
                }
            }
        });
    }

    private void signOut() {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signOut();
    }

    private boolean validateForm() {
        boolean valid = true;
        String email = inputEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("Required.");
            valid = false;
        } else {
            inputEmail.setError(null);
        }

        String password = inputPassword.getText().toString();
        if (TextUtils.isEmpty(password)) {
            inputPassword.setError("Required.");
            valid = false;
        } else {
            inputPassword.setError(null);
        }
        return valid;
    }

    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);

        if (!validateForm()) {
            return;
        }


        progress = ProgressDialog.show(this, "Signin' you in", "please wait ...", true);
        ;

        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                        // If sign in fails, display a message to the user. If sign in succeeds
                        // the auth state listener will be notified and logic to handle the
                        // signed in user can be handled in the listener.
                        if (!task.isSuccessful()) { // if failed
                            Log.w(TAG, "signInWithEmail:failed", task.getException());
                            Toast.makeText(LoginActivity.this, R.string.auth_failed,
                                    Toast.LENGTH_SHORT).show();
                        } else { // if success
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        }

                        progress.dismiss();
                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_em
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.buttonSignIn) {

            signIn(inputEmail.getText().toString(), inputPassword.getText().toString());
        }
       /**  if (i == R.id.buttonSignUp) {
            startActivity(new Intent(LoginActivity.this, SignUpActivity.class));
        } */
        if (i == R.id.buttonResetPassword) {
            startActivity(new Intent(getApplicationContext(), ResetPasswordActivity.class));
            ;
        }
        if (i == R.id.buttonCreateWithEmail) {
            startActivity(new Intent(getApplicationContext(), SignUpEmailActivity.class));
            ;
        }

    }
}


