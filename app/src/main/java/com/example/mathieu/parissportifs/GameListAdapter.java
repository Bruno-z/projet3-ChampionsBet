package com.example.mathieu.parissportifs;

import android.app.Activity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.Date;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by apprenti on 03/06/17.
 */

public class GameListAdapter extends Firebaseadapter <NewGame> {

    TextView homeTeam;
    TextView scoreHomeTeam, betScoreHome, betScoreAway;
    TextView scoreAwayTeam;
    TextView awayTeam;
    TextView hour;
    ImageView imageViewAway;
    ImageView imageViewHome;
    int ScoreBetAway, ScoreBetHome;
    String competitionid, userid;
    public DatabaseReference mDatabaseref;
    DatabaseReference mDatabasereferenceCompet;
    private NewGame newGame;

    public GameListAdapter(Query ref, Activity activity, int layout) {
        super(ref, NewGame.class, layout, activity);

    }


    public GameListAdapter(Query ref, Activity activity, int layout, String CompetitionId, String UserId) {
        super(ref, NewGame.class, layout, activity);

        competitionid= CompetitionId;
        userid = UserId;
    }



    @Override
    protected void populateView(View view, final NewGame mNewGame, int position) {


        String prout = mNewGame.getmReportDate();
        mDatabaseref = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_GAMES).child(mNewGame.getmReportDate()).child(mNewGame.getmIdGame());

        hour = (TextView) view.findViewById(R.id.textViewHour);
        homeTeam = (TextView) view.findViewById(R.id.textViewHomeTeam);
        awayTeam = (TextView) view.findViewById(R.id.textViewAwayTeam);
        scoreHomeTeam = (TextView) view.findViewById(R.id.textViewScoreHome);
        scoreAwayTeam = (TextView) view.findViewById(R.id.textViewScoreAway);
        betScoreHome = (TextView) view.findViewById(R.id.textViewbetScoreHome);
        betScoreAway = (TextView) view.findViewById(R.id.textViewbetScoreAway);
        imageViewAway = (ImageView) view.findViewById(R.id.imageViewAwayTeam);
        imageViewHome = (ImageView) view.findViewById(R.id.imageViewHomeTeam);


        if (mNewGame.getmStatus().equals("OUVERT")){

            if (mNewGame.getmMinute() == 0){
                hour.setText(String.valueOf(mNewGame.getmHour()) + ":0" + String.valueOf(mNewGame.getmMinute()));
            } else {
                hour.setText(String.valueOf(mNewGame.getmHour()) + ":" + String.valueOf(mNewGame.getmMinute()));
            }

        } else if (mNewGame.getmStatus().equals("EN COURS")) {

            hour.setText("MATCH EN COURS");


        } else {

            hour.setText("TERMINE");
            scoreHomeTeam.setText(String.valueOf(mNewGame.getmScoreHomeTeam()));
            scoreAwayTeam.setText(String.valueOf(mNewGame.getmScoreAwayTeam() ));
        }

        homeTeam.setText(String.valueOf(mNewGame.getmHomeTeam()));
        awayTeam.setText(String.valueOf(mNewGame.getmAwayTeam()));

        SwitchLogoModel.switchLogo(homeTeam, imageViewHome, awayTeam , imageViewAway);

        mDatabasereferenceCompet = FirebaseDatabase.getInstance().getReference(Constants.COMPET).child(competitionid);

        ValueEventListener postListener = new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                CompetitionModel competition = dataSnapshot.getValue(CompetitionModel.class);
                HashMap<String, UserModel> usersMap = competition.getMembersMap();
                UserModel myUser = usersMap.get(userid);
                HashMap<String, BetGameModel> myGame = myUser.getUsersBets();
                BetGameModel betGame = myGame.get(mNewGame.getmIdGame());
                if (betGame != null && betGame.getmHomeScore() != -1){
                    betScoreHome.setText(String.valueOf(mNewGame.getmScoreHomeTeam()));
                    betScoreAway.setText(String.valueOf(mNewGame.getmScoreAwayTeam()));
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        };
        mDatabasereferenceCompet.addValueEventListener(postListener);
        

    }
}
