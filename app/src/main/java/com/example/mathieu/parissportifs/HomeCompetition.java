package com.example.mathieu.parissportifs;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;


public class HomeCompetition extends Fragment implements AdapterView.OnItemClickListener {

    private ListView playersList;
    private TextView competitionNametv,textViewligue1;
    private FirebaseDatabase database;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private String uId, postKey, competitionTitle, competitionKey, strtext, championShipName;
    private PlayersListAdapter aPlayersListAdapter;


    private Query playerListQuery;
    private DatabaseReference playersRef;


    public static HomeCompetition newInstance() {
        HomeCompetition fragment = new HomeCompetition();
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        View view = inflater.inflate(R.layout.fragment_home_competition, container, false);


        strtext = ((Navigation) getActivity()).getKey();



        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        uId = user.getUid();

        playersList = (ListView) view.findViewById(R.id.playersList);
        competitionNametv = (TextView) view.findViewById(R.id.competitionName);
        textViewligue1 = (TextView) view.findViewById(R.id.textViewLigue1);

        database = FirebaseDatabase.getInstance();


        playersRef = database.getReference("Competitions")
                .child(strtext).child("membersMap");
        playerListQuery = playersRef;

        Query competitionQuery = database.getReference("Competitions")
                .child(strtext);
        competitionQuery.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CompetitionModel competitionModel = dataSnapshot.getValue(CompetitionModel.class);
                competitionTitle = competitionModel.getCompetitionName();
                competitionTitle = competitionTitle.substring(0, Math.min(competitionTitle.length(), 30));
                championShipName = competitionModel.getChamionshipName();
                competitionNametv.setText(competitionTitle);
                textViewligue1.setText(championShipName);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        aPlayersListAdapter = new PlayersListAdapter(playerListQuery.
                orderByChild("currentScore"),
                HomeCompetition.this.getActivity(), R.layout.players_items_list, strtext);

        playersList.setAdapter(aPlayersListAdapter);


        // To fix
       // updateView(playersList);

        playersList.setOnItemClickListener(this);


        return view;
    }

    public View getViewByPosition(int pos, ListView listView) {
        final int firstListItemPosition = listView.getFirstVisiblePosition();
        final int lastListItemPosition = firstListItemPosition + listView.getChildCount() - 1;

        if (pos < firstListItemPosition || pos > lastListItemPosition) {
            return listView.getAdapter().getView(pos, null, listView);
        } else {
            final int childIndex = pos - firstListItemPosition;
            return listView.getChildAt(childIndex);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

    }

    private void updateView(ListView listView) {

        int count = listView.getAdapter().getCount();

        View firstPlayer = playersList.getChildAt(playersList.getFirstVisiblePosition());
        View secondPlayer = playersList.getChildAt(playersList.getFirstVisiblePosition() - 1);
        View beforelastPlayer = playersList.getChildAt(playersList.getLastVisiblePosition() + 1);
        View lastPlayer = playersList.getChildAt(playersList.getLastVisiblePosition());

        TextView first = ((TextView) firstPlayer.findViewById(R.id.textViewChampionShipName));
        TextView last = ((TextView) lastPlayer.findViewById(R.id.textViewChampionShipName));
        TextView b4last = ((TextView) beforelastPlayer.findViewById(R.id.textViewChampionShipName));
        TextView second = ((TextView) secondPlayer.findViewById(R.id.textViewChampionShipName));


        switch (count) {

            case 1:
                break;

            case 2:
                first.setText("Zlatan");
                last.setText("Jallet");

                break;
            case 3:
                first.setText("Zlatan");
                second.setText("Aubameyang");
                last.setText("Jallet");
                break;
            case 4:
                first.setText("Zlatan");
                second.setText("Aubameyang");
                last.setText("Jallet");
                b4last.setText("Gourcuff");
                break;
            case 5:
                break;
            case 6:
                break;

        }
    }
}







