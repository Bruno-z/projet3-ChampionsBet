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
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

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


        ImageButton firebase_notif = (ImageButton) view.findViewById(R.id.firebase_notif);

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
                .defaultSelectedDate(defaultDate.getTime())
                .textColor(Color.LTGRAY, Color.WHITE)
                .selectedDateBackground(Color.TRANSPARENT)
                .build();

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Date date, int position) {
                Toast.makeText(getActivity(), DateFormat.getDateInstance().format(date) + " is selected!", Toast.LENGTH_SHORT).show();

                DateFormat df = new SimpleDateFormat("yyMMdd");
                String reportDate = df.format(date);

                mDatabaseGameRef = FirebaseDatabase.getInstance().getReference(Constants.DATABASE_PATH_GAMES).child(reportDate);

                GameListAdapter mGameListAdapter = new GameListAdapter(mDatabaseGameRef,getActivity(), R.layout.game_list_items); // APPELLE L'ADAPTER

                mGameListView.setAdapter(mGameListAdapter); //FUSION LIST ET ADAPTER

                mGameListAdapter.notifyDataSetChanged();

            }

        });

        horizontalCalendar.goToday(true);

        FloatingActionButton fab = (FloatingActionButton) view.findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), NewGameActivity.class));
            }
        });

        /*firebase_notif.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.setAction(Intent.ACTION_VIEW);
                intent.addCategory(Intent.CATEGORY_BROWSABLE);
                intent.setData(Uri.parse("https://console.firebase.google.com/project/parissportifs-d74e4/notification/compose?campaignId=8388219356442312473"));
                startActivity(intent);


            }
        });*/

        mGameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {

                NewGame newGame = (NewGame) parent.getItemAtPosition(position);

                Intent i = new Intent(getActivity(), EnterScore.class);
                i.putExtra("newGame", newGame);
                startActivity(i);
            }
        });


        return view;
    }
}