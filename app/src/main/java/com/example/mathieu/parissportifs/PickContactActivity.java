package com.example.mathieu.parissportifs;

import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class PickContactActivity extends ListActivity implements View.OnClickListener {


    static final int PICK_CONTACT_REQUEST = 1;  // The request code

    private ListView contactListView;
    private ArrayList<String> listItems;
    private ArrayAdapter<String> adapter;
    private String[] contactArray;
    private DatabaseReference myRef;
    private FirebaseDatabase database;
    private DatabaseReference competitionRef;
    private FirebaseDatabase competitionDatabase;
    private String CompetitionId;
    private String CompareCompetId;
    public final static String COMPET = "compet";



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pick_contact);



        contactListView = (ListView) findViewById(android.R.id.list);

        Intent intent = getIntent();
        CompetitionId= intent.getStringExtra("oui");




        //LIST OF ARRAY STRINGS WHICH WILL SERVE AS LIST ITEMS
        listItems = new ArrayList<>();

        //DEFINING A STRING ADAPTER WHICH WILL HANDLE THE DATA OF THE LISTVIEW




        adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1,
                listItems);
        setListAdapter(adapter);

        findViewById(R.id.buttonfinishpickcontacts).setOnClickListener(this);
        findViewById(R.id.pickcontact).setOnClickListener(this);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == PICK_CONTACT_REQUEST){
            if(resultCode == RESULT_OK){
                Uri contactData = data.getData();
                Cursor cursor = getContentResolver().query(contactData, null, null, null, null);
                cursor.moveToFirst();

                String number = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.CommonDataKinds.Phone.NUMBER));
                String name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));

                //contactName.setText(name);
                listItems.add(number);
                String[] contactArray = listItems.toArray(new String[listItems.size()]);
                adapter.notifyDataSetChanged();
                //contactEmail.setText(email);
            }
        }
    }

    public void getCompetitionIdCode() {

        competitionRef = database.getReference("Competitions");

        competitionRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {


                CompetitionModel model = dataSnapshot.getValue(CompetitionModel.class);

                 CompareCompetId = model.getCompetitionIdReedeemCode();




            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }





    private void sendSMS(String message, String[] ArrContact) {




        for (int i = 0; i < ArrContact.length; i++) {

            String phoneNumber = ArrContact[i];
            SmsManager sms = SmsManager.getDefault();
            sms.sendTextMessage(phoneNumber, null, message, null, null);
        }
    }




    @Override
    public void onClick(View v) {
        int i = v.getId();

        if (i == R.id.pickcontact){

            Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType(ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE);
            startActivityForResult(intent, 1);
        }
        if (i == R.id.buttonfinishpickcontacts){

            String[] contactArray = listItems.toArray(new String[listItems.size()]);
            String message = "Rejoins-moi sur ChampionsBet. Voici le code de ma compétition : " + CompetitionId;
            sendSMS(message, contactArray);
            startActivity(new Intent(PickContactActivity.this, CreateOrJoinCompetition.class));

        }

    }


}
