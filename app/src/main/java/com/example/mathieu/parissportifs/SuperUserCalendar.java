package com.example.mathieu.parissportifs;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarListener;

import static com.facebook.FacebookSdk.getApplicationContext;


public class SuperUserCalendar extends Fragment {

    private HorizontalCalendar horizontalCalendar;
    private ListView mGameListView;
    private DatabaseReference mDatabaseGameRef;
    private int gameToday;
    private DatabaseReference mDatabase;


    public static SuperUserCalendar newInstance () {
        SuperUserCalendar fragment = new SuperUserCalendar();
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_super_user_calendar, container, false);



        mGameListView = (ListView) view.findViewById(R.id.gameListSuperUser);

        /** end after 1 month from now */
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1);

        /** start before 1 month from now */
        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.MONTH, -1);

        final Calendar defaultDate = Calendar.getInstance();
        defaultDate.add(Calendar.MONTH, -1);
        defaultDate.add(Calendar.DAY_OF_WEEK, +5);

        horizontalCalendar = new HorizontalCalendar.Builder(view, R.id.superUserCalendar)
                .startDate(startDate.getTime())
                .endDate(endDate.getTime())
                .datesNumberOnScreen(5)
                .dayNameFormat("EEE")
                .dayNumberFormat("dd")
                .monthFormat("MMM")
                .showDayName(true)
                .showMonthName(true)
                .textColor(Color.LTGRAY, Color.WHITE)
                .selectedDateBackground(Color.TRANSPARENT)
                .build();

        horizontalCalendar.goToday(true);

        Button buttonNewGame = (Button) view.findViewById(R.id.buttonNewGame);
        buttonNewGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), NewGameActivity.class));
            }
        });

        mGameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                NewGame newGame = (NewGame) parent.getItemAtPosition(position);

                Intent i = new Intent(getActivity(), EnterScore.class);
                i.putExtra("newGame", newGame);
                startActivity(i);
            }
        });



        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Date date, int position) {

                changeStatus();
                DateFormat df = new SimpleDateFormat("yyMMdd");
                String reportDate = df.format(date);

                mDatabaseGameRef = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_GAMES).child(reportDate);

                GameListAdapter mGameListAdapter = new GameListAdapter(mDatabaseGameRef,getActivity(), R.layout.game_list_items); // APPELLE L'ADAPTER

                mGameListView.setAdapter(mGameListAdapter);

                mGameListAdapter.notifyDataSetChanged();

            }

        });







        return view;
    }

    public int badges(){
        DateFormat dateFormat = new SimpleDateFormat("yyMMdd");
        Date date = new Date();
        String todayDate = dateFormat.format(date);
        gameToday = 0;

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_GAMES).child(todayDate);
        mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot game : dataSnapshot.getChildren()){

                    NewGame newGame = game.getValue(NewGame.class);
                    if (newGame.getmStatus().equals("EN COURS")){
                        gameToday ++;
                    }

                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

    return gameToday;
    }

    public void changeStatus(){
        final long currentDate_long = System.currentTimeMillis();
        final Date currentDate = new Date(currentDate_long);
        DateFormat dff = new SimpleDateFormat("yyMMdd");
        String reportDate = dff.format(currentDate);

        mDatabase = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_GAMES).child(reportDate);

        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot competition : dataSnapshot.getChildren()){

                    NewGame mNewGame = competition.getValue(NewGame.class);

                    if (mNewGame.getmStatus().equals("OUVERT")){

                        long myDate = mNewGame.getmOurDate();
                        if (currentDate_long >= myDate){
                            mNewGame.setmStatus("EN COURS");
                            mDatabase.child(mNewGame.getmIdGame()).setValue(mNewGame);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
