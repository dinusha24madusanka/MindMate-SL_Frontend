package com.dinusha.mindmate_sl.model;

import com.google.gson.annotations.SerializedName;

public class ChatResponse {

    private String reply;

    @SerializedName("stress_score")
    private int stressScore;

    public String getReply() {
        return reply;
    }

    public int getStressScore() {
        return stressScore;
    }
}