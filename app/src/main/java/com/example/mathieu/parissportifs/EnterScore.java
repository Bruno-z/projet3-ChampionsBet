package com.example.mathieu.parissportifs;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;

import biz.kasual.materialnumberpicker.MaterialNumberPicker;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.example.mathieu.parissportifs.Constants.WINNER_AWAY;
import static com.example.mathieu.parissportifs.Constants.WINNER_HOME;
import static com.example.mathieu.parissportifs.Constants.WINNER_NULL;

public class EnterScore extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "enterScore";

    private TextView textViewDate;
    private TextView hour;
    private TextView homeTeam;
    private TextView awayTeam;
    private TextView homeScore;
    private TextView awayScore;
    private Button buttonUpload;
    private MaterialNumberPicker numberPickerHome;
    private MaterialNumberPicker numberPickerAway;
    private NewGame newGame;
    private DatabaseReference mDatabaseGame;
    private DatabaseReference mDatabaseUser;
    private DatabaseReference mDatabaseCompet;
    private DatabaseReference currentCompetitionRef;
    private String date_firebase;
    private DateFormat dff;
    private String uploadId;
    private int compareScoreHome;
    private int compareScoreAway;
    private Button buttonChangeTimeTable;
    private int mYear;
    private int mMonth;
    private int mDay;
    private int mHour;
    private int mMinute;
    private  Calendar dateCalendar;
    private int date_of_month;
    private int month;
    private int years;
    private Calendar timeCalendar;
    private Date date_time_object;
    private String winner = "O";
    private String status_open = "OUVERT";
    private GregorianCalendar ourDate;
    private String update_date_firebase;
    private DatabaseReference currentUserRef;
    private HashMap<String, UserModel> membersMap;
    private CompetitionModel currentCompetition;
    private AlertDialog alertDialog;
    private long ourDateLong;
    private String hourGame;
    private Button removeCompetition;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enter_score);

        mDatabaseGame = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_GAMES);
        mDatabaseUser = FirebaseDatabase.getInstance().getReference(Constants.USER);
        mDatabaseCompet = FirebaseDatabase.getInstance().getReference(Constants.COMPET);


        textViewDate = (TextView) findViewById(R.id.textViewDate);
        homeTeam = (TextView) findViewById(R.id.textViewHomeTeam);
        awayTeam = (TextView) findViewById(R.id.textViewAwayTeam);
        homeScore = (TextView) findViewById(R.id.textViewHomeScore);
        homeScore.setOnClickListener(this);
        awayScore = (TextView) findViewById(R.id.textViewAwayScore);
        awayScore.setOnClickListener(this);
        buttonUpload = (Button) findViewById(R.id.buttonUpload);
        buttonUpload.setOnClickListener(this);
        buttonChangeTimeTable = (Button) findViewById(R.id.buttonChangeHoraire);
        buttonChangeTimeTable.setOnClickListener(this);
        removeCompetition = (Button) findViewById(R.id.buttonRemoveGame);
        removeCompetition.setOnClickListener(this);



        numberPickerHome = new MaterialNumberPicker.Builder(this)
                .minValue(0)
                .maxValue(10)
                .defaultValue(0)
                .backgroundColor(Color.WHITE)
                .separatorColor(Color.TRANSPARENT)
                .textColor(Color.BLACK)
                .textSize(20)
                .enableFocusability(false)
                .wrapSelectorWheel(true)
                .build();

        numberPickerAway = new MaterialNumberPicker.Builder(this)
                .minValue(0)
                .maxValue(10)
                .defaultValue(0)
                .backgroundColor(Color.WHITE)
                .separatorColor(Color.TRANSPARENT)
                .textColor(Color.BLACK)
                .textSize(20)
                .enableFocusability(false)
                .wrapSelectorWheel(true)
                .build();

        Intent i = getIntent();
        newGame = (NewGame) i.getSerializableExtra("newGame");

        DateFormat df = new SimpleDateFormat("dd.MM.yy");
        String reportDate = df.format(newGame.getmDate());

        if (newGame.getmMinute() < 10){
            hourGame = String.valueOf(newGame.getmHour())+" : 0"+String.valueOf(newGame.getmMinute());
        } else {
            hourGame = String.valueOf(newGame.getmHour())+" : "+String.valueOf(newGame.getmMinute());
        }

        textViewDate.setText(reportDate + " - " + hourGame);
        homeTeam.setText(newGame.getmHomeTeam());
        awayTeam.setText(newGame.getmAwayTeam());

        if(newGame.getmScoreHomeTeam() == -1){
            homeScore.setText("");
            awayScore.setText("");
        } else {
            homeScore.setText(String.valueOf(newGame.getmScoreHomeTeam()));
            awayScore.setText(String.valueOf(newGame.getmScoreAwayTeam()));
        }


    }

    private void changeScoreHomeTeam(){

        new AlertDialog.Builder(this)
                .setTitle("Entrer le score de " + newGame.getmHomeTeam())
                .setView(numberPickerHome)
                .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        homeScore.setText(Integer.toString(numberPickerHome.getValue()));
                        newGame.setmScoreHomeTeam(numberPickerHome.getValue());
                        changeScoreAwayTeam();

                    }
                }).show();

    }

    private void changeScoreAwayTeam(){

        new AlertDialog.Builder(this)
                .setTitle("Entrer le score de " + newGame.getmAwayTeam())
                .setView(numberPickerAway)
                .setPositiveButton(getString(android.R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        awayScore.setText(Integer.toString(numberPickerAway.getValue()));
                        newGame.setmScoreAwayTeam(numberPickerAway.getValue());


                        int scoreHome = numberPickerHome.getValue();
                        int scoreAway = numberPickerAway.getValue();

                        String prout = newGame.getmHomeTeam()+ " " + String.valueOf(scoreHome) + "  -  " + String.valueOf(scoreAway) + " " + newGame.getmAwayTeam();


                        new SweetAlertDialog(EnterScore.this, SweetAlertDialog.WARNING_TYPE)
                                .setTitleText("Es-tu sûre du résultat de la rencontre ?")
                                .setContentText(prout)
                                .setCancelText("Non")
                                .setConfirmText("Oui")
                                .showCancelButton(true)
                                .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {
                                        sDialog.setTitleText("C'est noté !")
                                                .setContentText("Tu peux modifier le score !")
                                                .showCancelButton(false)
                                                .setCancelClickListener(null)
                                                .setConfirmText("OK")
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sDialog) {

                                                        sDialog.cancel();
                                                        finish();
                                                    }
                                                })
                                                .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                                    }
                                })
                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                    @Override
                                    public void onClick(SweetAlertDialog sDialog) {



                                        sDialog.setTitleText("Le score est enregistré")
                                                .setContentText("Le score est bien pris en compte, nous allons calculer le meilleur parieur !")
                                                .showCancelButton(false)
                                                .setCancelClickListener(null)
                                                .setConfirmText("OK")
                                                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                                    @Override
                                                    public void onClick(SweetAlertDialog sDialog) {
                                                        checkWinnerGame();
                                                        checkCompetitionBet();
                                                        sDialog.cancel();
                                                        finish();
                                                    }
                                                })
                                                .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                                    }
                                })
                                .show();


                    }
                })
                .show();

    }
    private void checkWinnerGame(){

        compareScoreAway = newGame.getmScoreAwayTeam();
        compareScoreHome = newGame.getmScoreHomeTeam();
        if (compareScoreHome > compareScoreAway){
            newGame.setmWinner(WINNER_HOME);
        } else if (compareScoreAway > compareScoreHome) {
            newGame.setmWinner(WINNER_AWAY);
        } else {
            newGame.setmWinner(WINNER_NULL);
        }
        uploadId = newGame.getmIdGame();
        dff = new SimpleDateFormat("yyMMdd");
        date_firebase = dff.format(newGame.getmDate());
        newGame.setmStatus("TERMINE");
        mDatabaseGame.child(date_firebase).child(uploadId).setValue(newGame);

    }

    private void updateDate(){
        // Get Current Date
        dateCalendar = Calendar.getInstance();
        mYear = dateCalendar.get(Calendar.YEAR);
        mMonth = dateCalendar.get(Calendar.MONTH);
        mDay = dateCalendar.get(Calendar.DAY_OF_MONTH);

        final DatePickerDialog datePickerDialog = new DatePickerDialog(EnterScore.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        date_of_month = view.getDayOfMonth();
                        month = view.getMonth();
                        years = view.getYear();
                        String displayMonth = String.valueOf(view.getMonth()+1);

                        String date_time = date_of_month + "/" + displayMonth + "/" + years;
                        date_time_object = new Date(years, month, date_of_month);
                        textViewDate.setText(date_time);

                        updateTime();
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void updateTime(){
        // Get Current Time
        timeCalendar = Calendar.getInstance();
        mHour = timeCalendar.get(Calendar.HOUR_OF_DAY);
        mMinute = timeCalendar.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(EnterScore.this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        mHour = hourOfDay;
                        mMinute = minute;

                        updateGame();

                    }
                }, mHour, mMinute, false);
        timePickerDialog.show();



    }

    private void updateGame (){
        dff = new SimpleDateFormat("yyMMdd");
        update_date_firebase = dff.format(date_time_object);
        ourDate = new GregorianCalendar(years, month, date_of_month, mHour,mMinute);

        ourDateLong = ourDate.getTimeInMillis();
        NewGame newGameTimeTable = new NewGame(uploadId, newGame.getmHomeTeam(), newGame.getmAwayTeam(), newGame.getmScoreHomeTeam(), newGame.getmScoreAwayTeam(), date_time_object.getTime(), mHour, mMinute, newGame.getmMatchWeek(), ourDateLong, update_date_firebase, status_open, winner);
        mDatabaseGame.child(update_date_firebase).child(uploadId).setValue(newGameTimeTable);
        finish();
    }

    public void onClick (View v) {

        if (v == buttonUpload) {
            changeScoreHomeTeam();

        }
        if (v == buttonChangeTimeTable){
            if (newGame != null) {
                uploadId = newGame.getmIdGame();
                dff = new SimpleDateFormat("yyMMdd");
                date_firebase = dff.format(newGame.getmDate());
                mDatabaseGame.child(date_firebase).child(uploadId).removeValue();

                updateDate();
            }
        }

        if ( v == removeCompetition) {

            new SweetAlertDialog(EnterScore.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Supprimer la rencontre !")
                    .setContentText("Es-tu sûre de vouloir supprimer la rencontre ?")
                    .setCancelText("Non")
                    .setConfirmText("Oui")
                    .showCancelButton(true)
                    .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {

                            sDialog.setTitleText("C'est noté !")
                                    .setContentText("Les utilisateurs pourront voir ce match !")
                                    .showCancelButton(false)
                                    .setCancelClickListener(null)
                                    .setConfirmClickListener(null)
                                    .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                        }
                    })
                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                        @Override
                        public void onClick(SweetAlertDialog sDialog) {

                            uploadId = newGame.getmIdGame();
                            dff = new SimpleDateFormat("yyMMdd");
                            date_firebase = dff.format(newGame.getmDate());
                            mDatabaseGame.child(date_firebase).child(uploadId).removeValue();

                            sDialog.setTitleText("Supprimer !")
                                    .setContentText("Nous avons bien supprimer la rencontre !")
                                    .showCancelButton(false)
                                    .setCancelClickListener(null)
                                    .setConfirmText("OK")
                                    .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                        @Override
                                        public void onClick(SweetAlertDialog sDialog) {
                                            sDialog.cancel();
                                            finish();
                                        }
                                    })
                                    .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                        }
                    })
                    .show();
        }
    }

    private void checkCompetitionBet(){
        mDatabaseCompet.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot competition : dataSnapshot.getChildren()){

                    updateCompetition(competition.getKey());
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    private void updateCompetition(final String competitionKey){
        mDatabaseCompet.child(competitionKey).runTransaction(new Transaction.Handler() {
            @Override
            public Transaction.Result doTransaction(MutableData mutableData) {
                if (mutableData.getValue() == null) {
                    return Transaction.success(mutableData);
                }
                CompetitionModel competitionModel = mutableData.getValue(CompetitionModel.class);
                HashMap<String, UserModel> usersMap = competitionModel.getMembersMap();
                int scoreCompetition = 0;
                for (UserModel user : usersMap.values()) {
                    UserModel updateUser = updateUser(user, competitionKey);
                    competitionModel.getMembersMap().put(user.getUserId(), updateUser);
                    if (updateUser.getUserScorePerCompetition().containsKey(competitionKey)) {
                        scoreCompetition = scoreCompetition + updateUser.getUserScorePerCompetition().get(competitionKey);
                    }

                }

                competitionModel.setCompetitionScore(scoreCompetition/usersMap.size());
                mutableData.setValue(competitionModel);
                return Transaction.success(mutableData);
            }
            @Override
            public void onComplete(DatabaseError databaseError, boolean b, DataSnapshot dataSnapshot) {

            }
        });
    }


    private UserModel updateUser(UserModel userModel, String competiitionID){



        HashMap<String, BetGameModel> betsList = userModel.getUsersBets();
        if (betsList == null){
            return userModel;
        }
        if (betsList.containsKey(uploadId)){
            BetGameModel currentBet = betsList.get(uploadId);
            int score = 0;
            if (currentBet.getmHomeScore() == compareScoreHome && currentBet.getmAwayScore() == compareScoreAway) {
                score = 3;
            } else if(currentBet.getmWinner().equals(newGame.getmWinner())){
                score = 1;
            }
            currentBet.setmBetResult(score);
            HashMap<String, Integer> updateScorePerCompetition = userModel.getUserScorePerCompetition();
            if (updateScorePerCompetition == null ){
                updateScorePerCompetition = new HashMap<>();
                updateScorePerCompetition.put(competiitionID,0);
            }
            int newScore = updateScorePerCompetition.get(competiitionID) + score;
            updateScorePerCompetition.put(competiitionID, newScore);
            userModel.setCurrentScore(newScore);
            userModel.setUserScorePerCompetition(updateScorePerCompetition);

        }

        return userModel;


    }




}