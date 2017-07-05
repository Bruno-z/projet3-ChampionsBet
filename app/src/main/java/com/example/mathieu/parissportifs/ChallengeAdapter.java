package com.example.mathieu.parissportifs;

import android.app.Activity;
import android.graphics.Color;
import android.view.View;
import android.widget.TextView;

import com.google.firebase.database.Query;

/**
 * Created by apprenti on 25/06/17.
 */

public class ChallengeAdapter extends Firebaseadapter<CompetitionModel> {

    public TextView textViewPosition;
    public TextView textViewPoints;
    public TextView textViewNameCompetition;
    public String competitionId;



    public ChallengeAdapter(Query ref, Activity activity, int layout, String competitionId) {
        super(ref, CompetitionModel.class, layout, activity);

        this.competitionId = competitionId;


    }

    @Override
    protected void populateView(View view, CompetitionModel competition, int position) {

        textViewPosition = (TextView) view.findViewById(R.id.textViewPosition);
        textViewPoints = (TextView) view.findViewById(R.id.textViewChampionShipName);
        textViewNameCompetition = (TextView) view.findViewById(R.id.textViewNameCompetition);

        textViewPosition.setText(String.valueOf(getItemId(position+1)));

        textViewNameCompetition.setText(competition.getCompetitionName());
        textViewPoints.setText(String.valueOf(competition.getCompetitionScore()));

        if (competition.getCompetitionIdReedeemCode().equals(competitionId)){
            view.setBackgroundResource(R.drawable.list_highlight_background);
        } else {
            view.setBackgroundColor(Color.WHITE);
        }
    }
}
