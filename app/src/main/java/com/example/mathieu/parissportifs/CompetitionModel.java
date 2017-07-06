package com.example.mathieu.parissportifs;



import android.support.annotation.Nullable;

import com.example.mathieu.parissportifs.UserModel;


import java.util.HashMap;
/**
 Created by mathieu on 22/05/17. */
public class CompetitionModel {
    private String competitionName;
    private String chamionshipName;
    private String userAdmin;
    private HashMap<String, UserModel> membersMap;
    private int competitionScore;
    private String competitionIdReedeemCode;
    private String emailAdmin;
    private String nameAdmin;



    private CompetitionModel() {}

    public CompetitionModel(String competitionName, String chamionshipName, String userAdmin, HashMap<String, UserModel> userList,  @Nullable String competitionIdReedeemCode, String emailAdmin, String nomAdmin) {
        this.competitionName = competitionName;
        this.chamionshipName = chamionshipName;
        this.userAdmin = userAdmin;
        this.membersMap = userList;
        this.competitionIdReedeemCode = competitionIdReedeemCode;
        this.emailAdmin = emailAdmin;
        this.nameAdmin = nomAdmin;

    }



    public String getCompetitionName() {
        return competitionName;
    }

    public void setCompetitionName(String competitionName) {
        this.competitionName = competitionName;
    }

    public String getChamionshipName() {
        return chamionshipName;
    }

    public void setChamionshipName(String chamionshipName) {
        this.chamionshipName = chamionshipName;
    }

    public String getUserAdmin() {
        return userAdmin;
    }

    public void setUserAdmin(String userAdmin) {
        this.userAdmin = userAdmin;
    }

    public HashMap<String, UserModel> getMembersMap() {
        return membersMap;
    }

    public void setMembersMap(HashMap<String, UserModel> membersMap) {
        this.membersMap = membersMap;
    }

    public int getCompetitionScore() {
        return competitionScore;
    }

    public void setCompetitionScore(int competitionScore) {
        this.competitionScore = competitionScore;
    }

    public String getCompetitionIdReedeemCode() {
        return competitionIdReedeemCode;
    }

    public void setCompetitionIdReedeemCode(String competitionIdReedeemCode) {
        this.competitionIdReedeemCode = competitionIdReedeemCode;
    }

    public String getEmailAdmin() {
        return emailAdmin;
    }

    public void setEmailAdmin(String emailAdmin) {
        this.emailAdmin = emailAdmin;
    }

    public String getNameAdmin() {
        return nameAdmin;
    }

    public void setNameAdmin(String nameAdmin) {
        this.nameAdmin = nameAdmin;
    }
}
