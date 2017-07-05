package com.example.mathieu.parissportifs;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static com.example.mathieu.parissportifs.Constants.USER;

public class CreateOrJoinCompetition extends AppCompatActivity implements View.OnClickListener {

        public static final String REC_DATA = "REC_DATA";
    public static final String COMPETITION_ID = "competitionId";



    private ListView mCompetitionListView;
    private Query mDatabaseCompetitionRef, AdapterQuery, mDatabaseUserRef;


    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private FirebaseAuth mAuth;
    private List<CompetitionModel> competitionsList;
    private String uId;
    private UserModel userData;
    private String pass;
    private SwipeCompetitionAdapter mCompetitionResultAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_or_join_competition);

        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        uId = user.getUid();
        getUserData();
        if (uId.equals(Constants.ADMIN_USER)) {
            startActivity(new Intent(CreateOrJoinCompetition.this, SuperUserNavigation.class));
            finish();
        }
        if (user == null) {
            startActivity(new Intent(CreateOrJoinCompetition.this, LoginActivity.class));
        }

        findViewById(R.id.button_create_competition).setOnClickListener(this);
        findViewById(R.id.button_join_competition).setOnClickListener(this);

        final EditText input = new EditText(CreateOrJoinCompetition.this);

        database = FirebaseDatabase.getInstance();

        mDatabaseCompetitionRef = database.getReference("Competitions");

        mDatabaseUserRef = database.getReference("users");

        competitionsList = new ArrayList<>();
        mCompetitionListView = (ListView) findViewById(R.id.CompetitionList);
        mCompetitionResultAdapter = new SwipeCompetitionAdapter(competitionsList, CreateOrJoinCompetition.this, this.getLayoutInflater(),uId);
        mCompetitionListView.setAdapter(mCompetitionResultAdapter);

        mCompetitionListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String postKey = mCompetitionResultAdapter.getmKey(position);


                Bundle bundle = new Bundle();
                bundle.putString(COMPETITION_ID, postKey);

                Intent intent = new Intent(CreateOrJoinCompetition.this, Navigation.class);
                intent.putExtra(COMPETITION_ID, bundle);
                startActivity(intent);
            }
        });


        Query query = mDatabaseCompetitionRef;
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //progressDialog.dismiss();
                competitionsList.clear();

                for(DataSnapshot competition: dataSnapshot.getChildren()){
                    CompetitionModel competitionModel = competition.getValue(CompetitionModel.class);

                    HashMap<String,UserModel> membersMap = competitionModel.getMembersMap();

                    if (membersMap != null){
                        if(membersMap.get(uId)!= null){
                            competitionsList.add(competitionModel);
                        }
                    }
                    mCompetitionResultAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

        @Override
        public void onClick(View v) {
            int i = v.getId();
            if (i == R.id.button_create_competition) {
                startActivity(new Intent(CreateOrJoinCompetition.this, CreateCompetitionActivity.class));
            }

            if (i == R.id.button_join_competition) {


                final AlertDialog.Builder alertDialog = new AlertDialog.Builder(CreateOrJoinCompetition.this);
                        //(CreateOrJoinCompetition.this);
                alertDialog.setTitle("Rejoins tes amis !");
                alertDialog.setMessage("Rentre la clé secrète pour entrer dans le vestiaire");
                final EditText input = new EditText(CreateOrJoinCompetition.this);
                LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        LinearLayout.LayoutParams.MATCH_PARENT);
                input.setLayoutParams(lp);
                alertDialog.setView(input);

                alertDialog.setPositiveButton("GO",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {


                                addUserToCompetition(input);
                                addCompetitionToUSer(input);
                                final String competitionPassword = input.getText().toString();
                                final Query competitionQuery = mDatabaseCompetitionRef;

                                pass = "";

                            }
                        });

                alertDialog.setNegativeButton("NOPE",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });

                alertDialog.show();
            }
        }

        private void getUserData() {
            DatabaseReference database = FirebaseDatabase
                    .getInstance().getReference();
            myRef = database.child("users/" + uId);
            myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    userData = dataSnapshot.getValue(UserModel.class);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }

        private void addUserToCompetition(EditText input) {
            final String competitionPassword = input.getText().toString();
            database.getReference("Competitions").child(competitionPassword).runTransaction(new Transaction.Handler() {
                @Override
                public Transaction.Result doTransaction(MutableData mutableData) {
                    if (mutableData.getValue() == null) {
                        return Transaction.success(mutableData);
                    }
                    CompetitionModel currentCompetition = mutableData.getValue(CompetitionModel.class);

                    HashMap<String, UserModel> membersMap = currentCompetition.getMembersMap();
                    membersMap.put(userData.getUserId(), userData);
                    currentCompetition.setMembersMap(membersMap);

                    mutableData.setValue(currentCompetition);
                    return Transaction.success(mutableData);
                }

                @Override
                public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                }


            });
        }

        private void addCompetitionToUSer(EditText input) {
            final String competitionPassword = input.getText().toString();
            if (mAuth.getCurrentUser() != null) {
                database.getReference(USER).child(mAuth.getCurrentUser().getUid()).runTransaction(new Transaction.Handler() {
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
                        newHash.put(competitionPassword, 0);
                        currentUser.setUserScorePerCompetition(newHash);
                        mutableData.setValue(currentUser);
                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

                    }
                });

            }
        }



       /* @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

            //goToCompetition = mCompetitionResultAdapter.getItem(position);
            //String key = mCompetitionResultAdapter.getRef(position).getKey();
            //String postKey = competitionModel.get(position).getKey();
            //String postKey = mCompetitionResultAdapter.getItemId(position);

            String postKey = mCompetitionResultAdapter.getmKey(position);


            Bundle bundle = new Bundle();
            bundle.putString(COMPETITION_ID, postKey);

            Intent intent = new Intent(CreateOrJoinCompetition.this, Navigation.class);
            intent.putExtra(COMPETITION_ID, bundle);
            startActivity(intent);


        }*/
    }









