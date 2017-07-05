package com.example.mathieu.parissportifs;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.Query;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;


public class PlayersListAdapter extends Firebaseadapter<UserModel> {

    private TextView playerName;
    private TextView playerRank;
    private TextView playerScore;
    private TextView nickname;
    private int scoreValue;
    private String CompetitionKey;
    private CircleImageView playerPic;
    private StorageReference mStorageRef;
    private Context context;



    public PlayersListAdapter(Query ref, Activity activity, int layout) {

        super(ref, UserModel.class, layout, activity);

    }

    public PlayersListAdapter(Query ref, Activity activity, int layout, String CompetKey) {

        super(ref, UserModel.class, layout, activity);

        this.CompetitionKey = CompetKey;

    }


    @Override
    protected void populateView(View v, UserModel model, int position) {

        context = v.getContext().getApplicationContext();

        playerName = (TextView) v.findViewById(R.id.textViewPlayerName);
        playerRank = (TextView) v.findViewById(R.id.textViewChampionShipName);
        playerScore = (TextView) v.findViewById(R.id.textViewPoints);
        nickname = (TextView) v.findViewById(R.id.textViewNickName);
        playerPic = (CircleImageView) v.findViewById(R.id.playerPic);


        downloadPicture(model,playerPic);
        playerRank.setText(String.valueOf(position+1));
        playerName.setText(model.getUserName());

        if (model.getUserScorePerCompetition() == null) {
            playerScore.setText(String.valueOf(0));
        } else {
            HashMap<String, Integer> playerScoreMap = model.getUserScorePerCompetition();
            Integer value = playerScoreMap.get(CompetitionKey);
            if(value == null){
                playerScore.setText(String.valueOf(0));
            } else {
                playerScore.setText(String.valueOf(value));
            }


        }

        int i = getCount();

        if (i >= 2 && i <= 4) {
            if(position == 0){
                nickname.setText(R.string.first);
            } else if (position == i - 1){
                nickname.setText(R.string.last);
            }
        } else if (i > 3){
            if (position == 0){
                nickname.setText(R.string.first);
            } else if (position == 1){
                nickname.setText(R.string.second);
            } else if (position == i-2){
                nickname.setText(R.string.before_last);
            } else if (position == i-1){
                nickname.setText(R.string.last);
            }
        }

    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {


        return super.getView(i, view, viewGroup);
    }

    private void downloadPicture (UserModel userModel, final CircleImageView circleAvatar) {
        mStorageRef = FirebaseStorage.getInstance().getReference("users_avatar");

        StorageReference userPicture = mStorageRef.child(userModel.getUserId()+"_avatar");

            userPicture.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    Glide.with(context)
                            .load(uri)
                            .into(circleAvatar);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                }
            });

    }
}

