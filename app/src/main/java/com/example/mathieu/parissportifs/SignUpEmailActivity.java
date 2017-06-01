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
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
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
import java.util.List;

public class SignUpEmailActivity extends AppCompatActivity implements View.OnClickListener, AdapterView.OnItemSelectedListener {

    private EditText inputEmail, inputPassword, verifyPassword, inputUserName;
    private ProgressBar progressBar;
    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";
    private FirebaseAuth.AuthStateListener mAuthListener;
    private ProgressDialog progress;
    private TextView mStatusTextView, mDetailTextView;
    private FirebaseDatabase database;
    private DatabaseReference mDatabase;
    private FirebaseDatabase userDatabase;
    private Spinner favoriteTeamSelector;
    private String favoriteTeam;
    private String userPseudo;
    private UserProfileChangeRequest profileUpdates;
    private ProgressDialog progressDialog;
    private String email;
    private String password;
    private String userName;
    private UserModel mUser;
    private static String idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up_email);

        //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();



        favoriteTeamSelector = (Spinner) findViewById(R.id.spinner_favorite_team);
        favoriteTeamSelector.setOnItemSelectedListener(this);


        //EditText et ProgressBar

        inputUserName = (EditText) findViewById(R.id.eTextuserName);
        inputEmail = (EditText) findViewById(R.id.emailText);
        inputPassword = (EditText) findViewById(R.id.passwordText);
        verifyPassword = (EditText) findViewById(R.id.editTextVerifyPass);
        progressDialog = new ProgressDialog(this);



        //Button
        findViewById(R.id.buttonSignUp).setOnClickListener(this);
        findViewById(R.id.buttonBack).setOnClickListener(this);

        addItemFavoriteTeamSelector();

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

    public void addItemFavoriteTeamSelector() {


        List<String> ligue1List = new ArrayList<String>();
        ligue1List.add("Select your Favorite Team !");
        ligue1List.add("Angers SCO");
        ligue1List.add("AC Bastia");
        ligue1List.add("Girondins Bordeaux");
        ligue1List.add("Caen");
        ligue1List.add("Dijon FC");
        ligue1List.add("EA Guingamp");
        ligue1List.add("Lorient");
        ligue1List.add("Lille");
        ligue1List.add("Lyon");
        ligue1List.add("Marseille");
        ligue1List.add("Monaco");
        ligue1List.add("Metz");
        ligue1List.add("Montpellier");
        ligue1List.add("Nancy");
        ligue1List.add("Nantes");
        ligue1List.add("OGC Nice");
        ligue1List.add("PSG");
        ligue1List.add("Rennes");
        ligue1List.add("ASSE");
        ligue1List.add("TFC");


        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_spinner_item, ligue1List);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        favoriteTeamSelector.setAdapter(dataAdapter);
    }

    private void createAccount() {
        email = inputEmail.getText().toString().trim();
        password  = inputPassword.getText().toString().trim();
        userName = inputUserName .getText().toString().trim();



        if (!validateForm()) {
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



                                    }
                                }
                            });
                            mDatabase = FirebaseDatabase.getInstance().getReference("Users");
                            idUser = FirebaseAuth.getInstance().getCurrentUser().getUid();
                            mUser = new UserModel (idUser,userName,null,0,favoriteTeam);
                            mDatabase.child(idUser).setValue(mUser);

                            startActivity(new Intent(getApplicationContext(), ModifyProfile.class));
                            SignUpEmailActivity.this.finish();



                        } else{
                            //display some message here
                            //Log.e(TAG, "Sign-in Failed: " + task.getException().getMessage());
                            // If the connection keeps failing un comment this :
                            //Toast.makeText(NewAccountActivity.this,"Sign-in Failed: " + task.getException().getMessage(),Toast.LENGTH_LONG).show();

                            Toast.makeText(SignUpEmailActivity.this,"NO",Toast.LENGTH_LONG).show();
                        }
                        progressDialog.dismiss();
                    }
                });

    }

    private boolean validateForm() {
        boolean valid = true;

        String userPseudo = inputUserName.getText().toString();
        if (TextUtils.isEmpty(userPseudo)) {
            inputUserName.setError("Required.");
            valid = false;
        } else {
            inputUserName.setError(null);
        }

        String email = inputEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            inputEmail.setError("Required.");
            valid = false;
        } else {
            inputEmail.setError(null);
        }
        String verify = verifyPassword.getText().toString();
        if (TextUtils.isEmpty(verify)) {
            verifyPassword.setError("One does not simply don't check his password");
            valid = false;
        }
        if (verifyPassword == inputPassword) {
            verifyPassword.setError("Your pass does not match");
            inputPassword.setError("Your pass does not match");
            valid = false;
        } else {
            verifyPassword.setError(null);
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


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.buttonSignUp) {


            createAccount();

        }
        if (i == R.id.buttonBack) {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));

        }
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        favoriteTeam = parent.getItemAtPosition(position).toString();

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}


