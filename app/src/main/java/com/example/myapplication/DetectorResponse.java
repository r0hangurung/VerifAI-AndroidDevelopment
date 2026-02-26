package com.example.myapplication;

import com.google.gson.annotations.SerializedName;

public class DetectorResponse {
    @SerializedName("fakePercentage")
    public double fakePercentage;

    @SerializedName("aiWords")
    public int aiWords;

    @SerializedName("textWords")
    public int textWords;

    public String getDisplayResult() {
        return (fakePercentage >= 50.0 ? "SYNTHETIC" : "HUMAN") + " [" + (int)fakePercentage + "%]";
    }

    public String getWordStats() {
        return aiWords + " AI WORDS OUT OF " + textWords + " TOTAL";
    }
}