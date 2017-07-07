package com.example.mathieu.parissportifs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SignUpEmailActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText inputEmail, inputPassword, verifyPassword;
    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase;
    private String favoriteTeam;
    private ProgressDialog progressDialog;
    private String email;
    private String password;
    private String userName;
    private String verify;
    private UserModel mUser;
    private static String idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_email);

        //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();


        //EditText et ProgressBar
        inputEmail = (EditText) findViewById(R.id.emailText);
        inputPassword = (EditText) findViewById(R.id.passwordText);
        verifyPassword = (EditText) findViewById(R.id.editTextVerifyPass);
        progressDialog = new ProgressDialog(this);



        //Button
        findViewById(R.id.buttonSignUp).setOnClickListener(this);



        // [START auth_state_listener]
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());


                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // [START_EXCLUDE]
                // [END_EXCLUDE]
            }
        };
        // [END auth_state_listener]
    }


    private void createAccount() {
        email = inputEmail.getText().toString().trim();
        password  = inputPassword.getText().toString().trim();
        verify = verifyPassword.getText().toString().trim();
        password = inputPassword.getText().toString().trim();


        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("Required.");
            return;
        }

        if (TextUtils.isEmpty(password)) {
            inputPassword.setError("Required.");
            return;
        }

        if (TextUtils.isEmpty(verify)) {
            verifyPassword.setError("One does not simply don't check his password");
            return;
        }
        if (!password.equals(verify)) {
            verifyPassword.setError("Your pass does not match");
            inputPassword.setError("Your pass does not match");
            return;
        }

        progressDialog.setMessage("Wait");
        progressDialog.show();

        // [START sign_in_with_email]
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {

                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //checking if success
                        if(task.isSuccessful()){
                            FirebaseUser user = mAuth.getCurrentUser();

                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                    .setDisplayName(userName)
                                    .build();

                            user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if(task.isSuccessful()){
                                        Log.d("TAG", "User profile update.");
                                        SignUpEmailActivity.this.finish();
                                        startActivity(new Intent(getApplicationContext(), MainActivity.class));


                                    }
                                }
                            });
                            mDatabase = FirebaseDatabase.getInstance().getReference("users");
                            idUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            mUser = new UserModel (idUser, userName, null, favoriteTeam, email, null,0);
                            mDatabase.child(idUser).setValue(mUser);


                        } else{

                            Toast.makeText(SignUpEmailActivity.this,"NO",Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.buttonSignUp) {

            createAccount();

        }

    }
}


