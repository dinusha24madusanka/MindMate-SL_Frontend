package com.dinusha.mindmate_sl.model;

import com.google.gson.annotations.SerializedName;

public class ChatResponse {

    // =================================================
    // EXISTING FIELDS
    // =================================================

    private String reply;

    @SerializedName("stress_score")
    private int stressScore;


    // =================================================
    // HYBRID NLP FIELDS
    // =================================================

    private String intent;

    @SerializedName("intent_raw")
    private String intentRaw;

    @SerializedName("intent_confidence")
    private double intentConfidence;

    private String emotion;

    @SerializedName("emotion_confidence")
    private double emotionConfidence;

    @SerializedName("stress_probability")
    private double stressProbability;

    @SerializedName("stress_level")
    private String stressLevel;

    @SerializedName("risk_level")
    private String riskLevel;

    @SerializedName("allow_gamification")
    private boolean allowGamification;

    @SerializedName("recommended_activity")
    private String recommendedActivity;


    // =================================================
    // GETTERS
    // =================================================

    public String getReply() {
        return reply;
    }

    public int getStressScore() {
        return stressScore;
    }

    public String getIntent() {
        return intent;
    }

    public String getIntentRaw() {
        return intentRaw;
    }

    public double getIntentConfidence() {
        return intentConfidence;
    }

    public String getEmotion() {
        return emotion;
    }

    public double getEmotionConfidence() {
        return emotionConfidence;
    }

    public double getStressProbability() {
        return stressProbability;
    }

    public String getStressLevel() {
        return stressLevel;
    }

    public String getRiskLevel() {
        return riskLevel;
    }

    public boolean isAllowGamification() {
        return allowGamification;
    }

    public String getRecommendedActivity() {
        return recommendedActivity;
    }
}