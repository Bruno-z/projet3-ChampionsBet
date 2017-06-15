package com.example.mathieu.parissportifs;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import biz.borealis.numberpicker.NumberPicker;
import biz.borealis.numberpicker.OnValueChangeListener;

public class BetGame extends AppCompatActivity implements View.OnClickListener {

    private NewGame newGame;
    private TextView homeTeam;
    private TextView awayTeam;
    private TextView hour;
    private Button buttonSaveBet;
    private NumberPicker numberPickerHome;
    private NumberPicker numberPickerAway;
    private BetGameModel betGameModel;
    private int mScoreHome;
    private int mScoreAway;
    private String mWinner;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bet_game);

        mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_BET);

        Intent i = getIntent();
        newGame = (NewGame) i.getSerializableExtra("newGame");

        homeTeam = (TextView) findViewById(R.id.textViewHomeTeam);
        awayTeam = (TextView) findViewById(R.id.textViewAwayTeam);
        hour = (TextView) findViewById(R.id.textViewHour);
        buttonSaveBet = (Button) findViewById(R.id.buttonSaveBet);
        buttonSaveBet.setOnClickListener(this);

        homeTeam.setText(newGame.getmHomeTeam());
        awayTeam.setText(newGame.getmAwayTeam());
        hour.setText(String.valueOf(newGame.getmHour())+ " : "+String.valueOf(newGame.getmMinute()));



        numberPickerHome = (NumberPicker) findViewById(R.id.numberPickerHome);
        numberPickerHome.setOnValueChangeListener(new OnValueChangeListener() {
            @Override
            public void onValueChanged(int newValue) {

                mScoreHome = newValue;

            }
        });
        numberPickerAway= (NumberPicker) findViewById(R.id.numberPickerAway);
        numberPickerAway.setOnValueChangeListener(new OnValueChangeListener() {
            @Override
            public void onValueChanged(int newValue) {

                mScoreAway = newValue;
            }
        });
    }

    private void checkWinnerBet (){

        if(mScoreAway < mScoreHome){
            mWinner = "HOME";
        } else if (mScoreAway > mScoreHome){
            mWinner = "AWAY";
        } else {
            mWinner = "NULL";
        }

    }

    public void onClick (View view) {

        if (view == buttonSaveBet){
            checkWinnerBet();
            betGameModel = new BetGameModel(newGame.getmIdGame(),newGame.getmHomeTeam(),newGame.getmAwayTeam(),mScoreHome,mScoreAway,newGame.getmMatchWeek(),mWinner);
            mDatabase.child(String.valueOf(newGame.getmMatchWeek())).child(newGame.getmIdGame()).setValue(newGame);

        }

    }
}