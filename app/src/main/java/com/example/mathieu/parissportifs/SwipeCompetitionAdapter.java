package com.example.mathieu.parissportifs;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.daimajia.swipe.adapters.BaseSwipeAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;




/**
 * Created by apprenti on 28/06/17.
 */

public class SwipeCompetitionAdapter extends BaseSwipeAdapter {

    private List<CompetitionModel> competitionModelList;
    private TextView competitionName;
    private TextView championshipName;
    private TextView textViewPoints;
    private Context context;
    private LayoutInflater inflater;
    private Button removeCompetition;
    private String user;
    private DatabaseReference mDatabase;

    public SwipeCompetitionAdapter(List<CompetitionModel> competitionModelList, Context context, LayoutInflater inflater, String user) {
        this.competitionModelList = competitionModelList;
        this.context = context;
        this.inflater = inflater;
        this.user = user;
    }

    @Override
    public int getSwipeLayoutResourceId(int position) {
        return R.id.swipe;
    }

    @Override
    public View generateView(int position, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.competitions_list_items, null);
    }

    @Override
    public void fillValues(final int position, View convertView) {

        competitionName = (TextView) convertView.findViewById(R.id.textViewCompetitionName);
        championshipName = (TextView) convertView.findViewById(R.id.championshipName);
        removeCompetition = (Button) convertView.findViewById(R.id.removeCompetition);
        textViewPoints = (TextView) convertView.findViewById(R.id.textViewPoints);

        competitionName.setText(competitionModelList.get(position).getCompetitionName());
        championshipName.setText(String.valueOf(competitionModelList.get(position).getChamionshipName()));
        textViewPoints.setText(String.valueOf(competitionModelList.get(position).getCompetitionScore()));



        removeCompetition.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Sortir de la compétition !")
                        .setContentText("Es-tu sûre de vouloir quitter cette competition ?")
                        .setCancelText("Non")
                        .setConfirmText("Oui")
                        .showCancelButton(true)
                        .setCancelClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                sDialog.setTitleText("Super Choix !")
                                        .setContentText("Continues et tu deviendras le Messi du pronostique")
                                        .showCancelButton(false)
                                        .setCancelClickListener(null)
                                        .setConfirmClickListener(null)
                                        .changeAlertType(SweetAlertDialog.ERROR_TYPE);
                            }
                        })
                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                            @Override
                            public void onClick(SweetAlertDialog sDialog) {
                                removeUser(competitionModelList.get(position).getCompetitionIdReedeemCode(),user);

                                sDialog.setTitleText("Supprimer !")
                                        .setContentText("Nous t'avons bien supprimer de cette compétition")
                                        .showCancelButton(false)
                                        .setCancelClickListener(null)
                                        .setConfirmText("OK")
                                        .setConfirmClickListener(new SweetAlertDialog.OnSweetClickListener() {
                                            @Override
                                            public void onClick(SweetAlertDialog sDialog) {
                                                sDialog.cancel();
                                            }
                                        })
                                        .changeAlertType(SweetAlertDialog.SUCCESS_TYPE);
                            }
                        })
                        .show();



            }
        });
    }

    @Override
    public int getCount() {
        return competitionModelList.size();
    }

    @Override
    public Object getItem(int position) {
        return competitionModelList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String getmKey (int position){
        return competitionModelList.get(position).getCompetitionIdReedeemCode();
    }

    public void removeUser(final String competitionKey, final String userId){

        mDatabase = FirebaseDatabase.getInstance().getReference(Constants.COMPET);


        mDatabase.child(competitionKey).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                CompetitionModel competitionModel = dataSnapshot.getValue(CompetitionModel.class);
                HashMap<String, UserModel> usersMap = competitionModel.getMembersMap();

                usersMap.remove(userId);

                mDatabase.child(competitionKey).setValue(competitionModel);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                throw databaseError.toException();
            }
        });

    }
}
