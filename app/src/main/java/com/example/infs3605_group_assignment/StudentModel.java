package com.example.infs3605_group_assignment;

import com.google.firebase.Timestamp;

import java.util.List;

public class StudentModel {

    String FirstName, LastName, Email, isTeacher, UID;
    Timestamp latest_Timestamp;
    String latest_additional_comments;
    Long latest_emotional_rating, latest_intensity_of_emotion;
    int MoodProgress;


    public StudentModel(String firstName, String lastName, String email,String UID, String isTeacher, Timestamp latest_Timestamp, String latest_additional_comments, Long latest_emotional_rating, Long latest_intensity_of_emotion, int moodProgress) {
        FirstName = firstName;
        LastName = lastName;
        Email = email;
        this.UID = UID;
        this.isTeacher = isTeacher;
        this.latest_Timestamp = latest_Timestamp;
        this.latest_additional_comments = latest_additional_comments;
        this.latest_emotional_rating = latest_emotional_rating;
        this.latest_intensity_of_emotion = latest_intensity_of_emotion;
        MoodProgress = moodProgress;
    }

    public StudentModel() {
    }

    public String getFirstName() {
        return FirstName;
    }

    public void setFirstName(String firstName) {
        FirstName = firstName;
    }

    public String getLastName() {
        return LastName;
    }

    public void setLastName(String lastName) {
        LastName = lastName;
    }

    public String getEmail() {
        return Email;
    }

    public void setEmail(String email) {
        Email = email;
    }

    public String getIsTeacher() {
        return isTeacher;
    }

    public void setIsTeacher(String isTeacher) {
        this.isTeacher = isTeacher;
    }

    public Timestamp getLatest_Timestamp() {
        return latest_Timestamp;
    }

    public void setLatest_Timestamp(Timestamp latest_Timestamp) {
        this.latest_Timestamp = latest_Timestamp;
    }

    public String getLatest_additional_comments() {
        return latest_additional_comments;
    }

    public void setLatest_additional_comments(String latest_additional_comments) {
        this.latest_additional_comments = latest_additional_comments;
    }

    public Long getLatest_emotional_rating() {
        return latest_emotional_rating;
    }

    public void setLatest_emotional_rating(Long latest_emotional_rating) {
        this.latest_emotional_rating = latest_emotional_rating;
    }

    public Long getLatest_intensity_of_emotion() {
        return latest_intensity_of_emotion;
    }

    public void setLatest_intensity_of_emotion(Long latest_intensity_of_emotion) {
        this.latest_intensity_of_emotion = latest_intensity_of_emotion;
    }

    public int getMoodProgress() {
        return MoodProgress;
    }

    public void setMoodProgress(int moodProgress) {
        MoodProgress = moodProgress;
    }

    public String getUid() {
        return UID;
    }

    public void setUid(String uid) {
        this.UID = uid;
    }
}


