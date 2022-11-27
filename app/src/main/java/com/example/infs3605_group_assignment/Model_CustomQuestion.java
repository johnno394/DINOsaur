package com.example.infs3605_group_assignment;

import com.google.firebase.Timestamp;

public class Model_CustomQuestion {

    private String Short_Title, Question, Teacher_ID;
    private Timestamp Post_Date, Finish_Date;

    public Model_CustomQuestion(String short_Title, String question, String teacher_ID, Timestamp post_Date, Timestamp Finish_Date) {
        Short_Title = short_Title;
        Question = question;
        Teacher_ID = teacher_ID;
        Post_Date = post_Date;
        this.Finish_Date = Finish_Date;
    }

    public Model_CustomQuestion() {

    }

    public String getShort_Title() {
        return Short_Title;
    }

    public void setShort_Title(String short_Title) {
        Short_Title = short_Title;
    }

    public String getQuestion() {
        return Question;
    }

    public void setQuestion(String question) {
        Question = question;
    }

    public String getTeacher_ID() {
        return Teacher_ID;
    }

    public void setTeacher_ID(String teacher_ID) {
        Teacher_ID = teacher_ID;
    }

    public Timestamp getPost_Date() {
        return Post_Date;
    }

    public void setPost_Date(Timestamp post_Date) {
        Post_Date = post_Date;
    }

    public Timestamp getFinish_Date() {
        return Finish_Date;
    }

    public void setFinish_Date(Timestamp Finish_Date) {
        this.Finish_Date = Finish_Date;
    }
}
