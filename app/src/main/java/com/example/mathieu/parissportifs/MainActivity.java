package com.example.mathieu.parissportifs;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import static com.example.mathieu.parissportifs.Constants.TEAM;
import static com.example.mathieu.parissportifs.Constants.USER;

public class MainActivity extends AppCompatActivity implements  AdapterView.OnItemSelectedListener {



    private EditText editTextModifyPseudo;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private DatabaseReference mDatabase, checkUserScore;
    private Spinner favoriteTeamSelector;
    private String equipefavorite, favoriteTeam;
    private FirebaseUser user;
    private Button buttonGo;
    private ProgressDialog progressDialog;


    private static String email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Get Firebase auth instance
        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        mDatabase = FirebaseDatabase.getInstance().getReference().child(USER).child(user.getUid());
        // checking firebase information in progressdialog
        isOnFirebase();


        // Spinner
        favoriteTeamSelector = (Spinner) findViewById(R.id.spinner_favorite_team);
        favoriteTeamSelector.setOnItemSelectedListener(this);
        // Nickname
        editTextModifyPseudo = (EditText) findViewById(R.id.editTextModifyPseudo);
        // Button
        buttonGo = (Button) findViewById(R.id.buttonGo);
        progressDialog = new ProgressDialog(this);
        //pseudo = user.getDisplayName();
        addItemFavoriteTeamSelector();



        email= user.getEmail();

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null) {

                    UserModel userActuel = dataSnapshot.getValue(UserModel.class);
                    editTextModifyPseudo.setText(userActuel.getUserName());
                }


            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        buttonGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                conditionItent();
            }
        });







    }




    public void changeProfil() {

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String newPseudo = editTextModifyPseudo.getText().toString().trim();

        //Uri newPhoto = userImg.

        //Update Pseudo & Photo
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(newPseudo)
                .build();

        user.updateProfile(profileUpdates).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {

                }
            }
        });
    }

    public void addItemFavoriteTeamSelector() {


        List<String> ligue1List = new ArrayList<String>();
        ligue1List.add(getString(R.string.SelectTeam));
        ligue1List.add(getString(R.string.AngersSCO));
        ligue1List.add(getString(R.string.ACBastia));
        ligue1List.add(getString(R.string.GirondinsBordeaux));
        ligue1List.add(getString(R.string.Caen));
        ligue1List.add(getString(R.string.DijonFC));
        ligue1List.add(getString(R.string.EAGuingamp));
        ligue1List.add(getString(R.string.Lorient));
        ligue1List.add(getString(R.string.Lille));
        ligue1List.add(getString(R.string.Lyon));
        ligue1List.add(getString(R.string.Marseille));
        ligue1List.add(getString(R.string.Monaco));
        ligue1List.add(getString(R.string.Metz));
        ligue1List.add(getString(R.string.Montpellier));
        ligue1List.add(getString(R.string.Nancy));
        ligue1List.add(getString(R.string.Nantes));
        ligue1List.add(getString(R.string.Nice));
        ligue1List.add(getString(R.string.PSG));
        ligue1List.add(getString(R.string.Rennes));
        ligue1List.add(getString(R.string.ASSE));
        ligue1List.add(getString(R.string.TFC));

        ArrayAdapter<String> dataAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, ligue1List);
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        favoriteTeamSelector.setAdapter(dataAdapter);


    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        Toast.makeText(parent.getContext(),
                "OnItemSelectedListener : " + parent.getItemAtPosition(position).toString(),
                Toast.LENGTH_SHORT).show();
        favoriteTeam = parent.getItemAtPosition(position).toString();


    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {


    }




    public void conditionItent() {

        if (editTextModifyPseudo.getText().length() == 0 || favoriteTeam.equals("Select your Favorite Team !")) {
            Toast.makeText(MainActivity.this, favoriteTeam, Toast.LENGTH_LONG).show();
            return;
        }
        else{



            /** FirebaseDatabase database = FirebaseDatabase.getInstance();
             DatabaseReference myRef = database.getReference("Competitions");*/

            String UserId = FirebaseAuth.getInstance().getCurrentUser().getUid();

            String userName = editTextModifyPseudo.getText().toString();


            UserModel user = new UserModel(UserId, userName, null, favoriteTeam, email, null);
            mDatabase.setValue(user);

            Intent intent = new Intent(MainActivity.this, CreateOrJoinCompetition.class);
            startActivity(intent);
            finish();
        }
    }

    void isOnFirebase(){
        mDatabase.child(TEAM).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.getValue() != null){
                    Intent intent = new Intent(MainActivity.this, CreateOrJoinCompetition.class);
                    startActivity(intent);
                    finish();
                }else {
                    progressDialog.setMessage("Vérification de votre compte : en cours");
                    progressDialog.show();
                    progressDialog.cancel();

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

}









