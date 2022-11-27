package com.example.infs3605_group_assignment;

import com.google.firebase.Timestamp;

import java.util.Date;

public class Model_WellbeingEntry {

    private String userID;
    private Long emotional_rating;
    private Long intensity_of_emotion;
    private String additional_comments;
    private Timestamp Timestamp;

    public Model_WellbeingEntry(String userID, Long emotional_rating, Long intensity_of_emotion, String additional_comments, com.google.firebase.Timestamp timestamp) {
        this.userID = userID;
        this.emotional_rating = emotional_rating;
        this.intensity_of_emotion = intensity_of_emotion;
        this.additional_comments = additional_comments;
        Timestamp = timestamp;
    }

    private Model_WellbeingEntry() {

    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public Long getEmotional_rating() {
        return emotional_rating;
    }

    public void setEmotional_rating(Long emotional_rating) {
        this.emotional_rating = emotional_rating;
    }

    public Long getIntensity_of_emotion() {
        return intensity_of_emotion;
    }

    public void setIntensity_of_emotion(Long intensity_of_emotion) {
        this.intensity_of_emotion = intensity_of_emotion;
    }

    public String getAdditional_comments() {
        return additional_comments;
    }

    public void setAdditional_comments(String additional_comments) {
        this.additional_comments = additional_comments;
    }

    public com.google.firebase.Timestamp getTimestamp() {
        return Timestamp;
    }

    public void setTimestamp(com.google.firebase.Timestamp timestamp) {
        Timestamp = timestamp;
    }
}
