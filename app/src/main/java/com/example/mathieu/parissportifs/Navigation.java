package com.example.mathieu.parissportifs;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

public class Navigation extends AppCompatActivity {

    private BottomBar bottomBar;
    private TextView keytv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);



        //keytv = (TextView) findViewById(R.id.keytv);

        bottomBar = (BottomBar) findViewById(R.id.bottomBar);

        bottomBar.selectTabAtPosition(1);

        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {

                Fragment selectedFragment = null;

                if (tabId == R.id.tab_homeCompetition) {
                    selectedFragment = HomeCompetition.newInstance();
                } else if (tabId == R.id.tab_competition) {
                    selectedFragment = Competition.newInstance();
                } else if (tabId == R.id.tab_challenge) {
                    selectedFragment = Challenge.newInstance();
                } else if (tabId == R.id.tab_chat) {
                    selectedFragment = Chat.newInstance();
                }

               /** Bundle bundle = getIntent().getExtras();

//Extract the data…
                String competitionKey = bundle.getString("key");

                Intent a = new Intent (Navigation.this, HomeCompetition.class);
                a.putExtra( "key", competitionKey);
                setIntent(a); */



                FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
                transaction.replace(R.id.frame_layout, selectedFragment);
                transaction.commit();
            }
        });
    }
}
