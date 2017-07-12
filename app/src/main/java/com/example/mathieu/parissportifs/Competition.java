package com.example.mathieu.parissportifs;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import cn.pedant.SweetAlert.SweetAlertDialog;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarListener;


public class Competition extends Fragment {

    private HorizontalCalendar horizontalCalendar;
    private ListView mGameListView;
    private DatabaseReference mDatabaseGameRef;
    private String mCompetitionId;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private SuperUserCalendar superUserCalendar;

    public static Competition newInstance (String competitonId) {
        Bundle bundle = new Bundle();
        bundle.putString(CreateOrJoinCompetition.COMPETITION_ID, competitonId);
        Competition fragment = new Competition();
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mCompetitionId = getArguments().getString(CreateOrJoinCompetition.COMPETITION_ID);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_competition, container, false);

        mGameListView = (ListView) view.findViewById(R.id.gameListUser);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        /** end after 1 month from now */
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);

        /** start before 1 month from now */
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);

        final Calendar defaultDate = Calendar.getInstance();
        defaultDate.add(Calendar.MONTH, -1);
        defaultDate.add(Calendar.DAY_OF_WEEK, +5);



         horizontalCalendar = new HorizontalCalendar.Builder(view, R.id.calendarViewUser)
                .startDate(startDate.getTime())
                .endDate(endDate.getTime())
                .datesNumberOnScreen(5)
                .dayNameFormat("EEE")
                .dayNumberFormat("dd")
                .monthFormat("MMM")
                .showDayName(true)
                .showMonthName(true)
                .defaultSelectedDate(defaultDate.getTime())
                .textColor(Color.LTGRAY, Color.WHITE)
                .selectedDateBackground(Color.TRANSPARENT)
                .build();


        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Date date, int position) {

                superUserCalendar = new SuperUserCalendar();
                superUserCalendar.changeStatus();
                DateFormat df = new SimpleDateFormat("yyMMdd");
                String reportDate = df.format(date);

                mDatabaseGameRef = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_GAMES).child(reportDate);

                GameListAdapter mGameListAdapter = new GameListAdapter(mDatabaseGameRef,getActivity(), R.layout.game_list_items, mCompetitionId, user.getUid()); // APPELLE L'ADAPTER

                mGameListView.setAdapter(mGameListAdapter); //FUSION LIST ET ADAPTER


                mGameListAdapter.notifyDataSetChanged();



            }
        });



        mGameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                NewGame newGame = (NewGame) parent.getItemAtPosition(position);

                if(newGame.getmStatus().equals("OUVERT")){
                    Intent i = new Intent(getActivity(), BetGame.class);
                    i.putExtra("newGame", newGame);
                    i.putExtra(CreateOrJoinCompetition.COMPETITION_ID, mCompetitionId);
                    startActivity(i);
                } else if (newGame.getmStatus().equals("TERMINE")){
                    showAlertDialog(newGame);
                } else {
                    Toast.makeText(getActivity(), "Tu ne peux plus pronostiquer cette rencontre !", Toast.LENGTH_SHORT).show();

                }
            }
        });

        horizontalCalendar.goToday(true);

        return view;
    }

    private void showAlertDialog (final NewGame newGame){
        DatabaseReference mDatabaseCompet = FirebaseDatabase.getInstance().getReference(Constants.COMPET).child(mCompetitionId);

        mDatabaseCompet.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                CompetitionModel competitionModel = dataSnapshot.getValue(CompetitionModel.class);
                HashMap<String, UserModel> membersMap = competitionModel.getMembersMap();
                UserModel userModel = membersMap.get(user.getUid());
                HashMap<String, BetGameModel> usersBets = userModel.getUsersBets();
                if (usersBets == null){
                    alertDialogNoBet();

                } else {
                    BetGameModel betGame = usersBets.get(newGame.getmIdGame());

                    if (betGame == null) {
                        alertDialogNoBet();
                    } else if (betGame.getmBetResult() == 0) {
                        alertDialogFail();
                    } else if (betGame.getmBetResult() == 1) {
                        alertDialogOnePoints();
                    } else {
                        alertDialogThreePoints();
                    }
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



    }

    private void alertDialogNoBet() {

        new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                .setTitleText("Dommage !")
                .setContentText("Tu n'as pas pronostiqué ce match !")
                .setConfirmText("OK")
                .showCancelButton(false)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {
                        sDialog.cancel();

                    }
                })
                .show();
    }

    private void alertDialogFail(){

        new SweetAlertDialog(getActivity(), SweetAlertDialog.ERROR_TYPE)
                .setTitleText("0 point !")
                .setContentText("Tu t'es trompé sur ce pronostique !")
                .setConfirmText("OK")
                .showCancelButton(false)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        sDialog.cancel();
                    }
                })
                .show();
    }

    private void alertDialogOnePoints(){

        new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("1 points !")
                .setContentText("Pas mal ! Tu as pronostiqué le bon résultat mais pas le score exact !")
                .setConfirmText("OK")
                .showCancelButton(false)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        sDialog.cancel();
                    }
                })
                .show();
    }

    private void alertDialogThreePoints(){

        new SweetAlertDialog(getActivity(), SweetAlertDialog.SUCCESS_TYPE)
                .setTitleText("3 points !")
                .setContentText("Tu as pronostiqué le résultat exact, INCROYABLE !")
                .setConfirmText("OK")
                .showCancelButton(false)
                .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                    @Override
                    public void onClick(SweetAlertDialog sDialog) {

                        sDialog.cancel();
                    }
                })
                .show();
    }


}
