    package com.example.mathieu.parissportifs;

    import android.content.Intent;
import android.os.Bundle;
    import android.support.annotation.NonNull;
    import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

    import com.google.android.gms.tasks.OnCompleteListener;
    import com.google.android.gms.tasks.Task;
    import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.mathieu.parissportifs.Constants.COMPET;
import static com.example.mathieu.parissportifs.Constants.USER;

    public class CreateCompetitionActivity extends AppCompatActivity implements
            View.OnClickListener, AdapterView.OnItemSelectedListener {

        private static final String TAG = "CreateCompetitionActy";

        private FirebaseUser mUser;
        private FirebaseDatabase database;
        private DatabaseReference mUserRef;
        private DatabaseReference competitionRef;
        private Spinner championShipSelector;
        private ImageView frenchFlag;
        private List<String> championshipList;
        private EditText etnameCompetition;
        private DatabaseReference finalPush;
        private HashMap<String, UserModel> members;
        private int scaleVictory, scaleScore;
        private FirebaseAuth mAuth;
        private FirebaseAuth.AuthStateListener mAuthListener;

        private String competitionName, championHShipName, mGroupId, checkKey;
        private Intent intent;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_create_competition);


            mAuth = FirebaseAuth.getInstance();
            mUser = mAuth.getCurrentUser();
            database = FirebaseDatabase.getInstance();
            competitionRef = database.getReference(COMPET);
            mUserRef = database.getReference(USER).child(mUser.getUid());


            if (mAuth.getCurrentUser() == null) {
                startActivity(new Intent(getApplicationContext(), LoginActivity.class));
                finish();
            }


            championShipSelector = (Spinner) findViewById(R.id.championship_spinner);
            championShipSelector.setOnItemSelectedListener(this);

            findViewById(R.id.button_validate_mycompetition).setOnClickListener(this);

            etnameCompetition = (EditText) findViewById(R.id.eTextNameYourCompetition);

            mUserRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    UserModel userProfile = dataSnapshot.getValue(UserModel.class);

                    //ArrayList To create the Competition Object
                    members = new HashMap<String, UserModel>();
                    members.put(userProfile.getUserId(), userProfile);

                }


                @Override
                public void onCancelled(DatabaseError databaseError) {
                }
            });


            addItemOnChampionShipSelector();
        }

        public void addItemOnChampionShipSelector() {


            List<String> championshipList = new ArrayList<String>();
            championshipList.add(Constants.SELECT_CREATE_CHAMPIONSHIF);
            championshipList.add(Constants.LIGUE_1);

            ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_spinner_item, championshipList);
            dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            championShipSelector.setAdapter(dataAdapter);

        }

        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {



            championHShipName = parent.getItemAtPosition(position).toString();
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }

        @Override
        public void onClick(View v) {
            int i = v.getId();

            if (i == R.id.button_validate_mycompetition) {

                final String competitionName = etnameCompetition.getText().toString();

                if (competitionName.length() == 0) {
                    Toast.makeText(CreateCompetitionActivity.this, "Tu dois renseigner le nom de la compétition", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (championHShipName.equals(Constants.SELECT_CREATE_CHAMPIONSHIF)){
                    Toast.makeText(CreateCompetitionActivity.this, "Tu dois renseigner la compétition", Toast.LENGTH_SHORT).show();
                    return;
                }

                // Write a message to the database
                final String userId = mUser.getUid();
                String emailAdmin = mUser.getEmail();
                String nameAdmin = mUser.getDisplayName();



                final CompetitionModel userCompetition = new CompetitionModel(competitionName,
                        championHShipName, userId,members, null, emailAdmin, nameAdmin);


                final DatabaseReference pushedPostRf = competitionRef.push();
                pushedPostRf.setValue(userCompetition).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mUserRef.runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                UserModel currentUser = mutableData.getValue(UserModel.class);
                                HashMap<String, Integer> newHash = currentUser.getUserScorePerCompetition();
                                if (newHash == null){
                                    newHash = new HashMap<String, Integer>();
                                }
                                newHash.put(pushedPostRf.getKey(), 0);
                                currentUser.setUserScorePerCompetition(newHash);
                                mutableData.setValue(currentUser);
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {
                                if (databaseError != null) {
                                    Log.d(TAG, databaseError.getMessage());
                                }
                            }
                        });

                        database.getReference(COMPET).child(pushedPostRf.getKey()).child("membersMap").child(userId).runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                if (mutableData.getValue() == null) {
                                    return Transaction.success(mutableData);
                                }
                                UserModel currentUser = mutableData.getValue(UserModel.class);
                                HashMap<String, Integer> newHash = currentUser.getUserScorePerCompetition();
                                if (newHash == null) {
                                    newHash = new HashMap<String, Integer>();
                                }
                                newHash.put(pushedPostRf.getKey(), 0);
                                currentUser.setUserScorePerCompetition(newHash);
                                mutableData.setValue(currentUser);
                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                            }
                        });
                    }
                });


                competitionRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {

                        for (DataSnapshot child : dataSnapshot.getChildren()) {


                            mGroupId = child.getKey();
                            DatabaseReference checkChild = competitionRef.child(mGroupId);

                            checkChild.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {

                                    CompetitionModel competitionModel = dataSnapshot.getValue(CompetitionModel.class);
                                    if (competitionModel.getCompetitionName().equals(competitionName)){


                                        //MgroupId renvoie au child qui correspond a l'ID de la competition qui vient d'être crée par l'user
                                        userCompetition.setCompetitionIdReedeemCode(mGroupId);
                                        competitionRef.child(mGroupId).setValue(userCompetition);
                                        intent = new Intent(CreateCompetitionActivity.this,
                                                PickContactActivity.class);

                                        intent.putExtra("oui", mGroupId);

                                        //competitionRef.removeEventListener(this);
                                        startActivity(intent);


                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                        }

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

                finish();
            }
        }
    }


