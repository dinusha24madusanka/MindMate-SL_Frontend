package com.dinusha.mindmate_sl.model;

import com.google.gson.annotations.SerializedName;

public class ChatResponse {
    @SerializedName("reply")
    private String reply;

    @SerializedName("stress_score")
    private int stressScore;

    public String getReply() { return reply; }
    public void setReply(String reply) { this.reply = reply; }

    public int getStressScore() { return stressScore; }
    public void setStressScore(int stressScore) { this.stressScore = stressScore; }
}