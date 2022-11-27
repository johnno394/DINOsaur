package com.example.infs3605_group_assignment;

import com.google.firebase.Timestamp;

public class Model_CustomAnswers {

    private String Student_Answer;
    private String Student_ID;
    private Timestamp Submission_Time;
    private Boolean Has_Submitted;

    public Model_CustomAnswers(String student_Answer, String student_ID, Timestamp submission_Time, Boolean has_Submitted) {
        Student_Answer = student_Answer;
        Student_ID = student_ID;
        Submission_Time = submission_Time;
        Has_Submitted = has_Submitted;
    }

    public Model_CustomAnswers() {

    }

    public String getStudent_Answer() {
        return Student_Answer;
    }

    public void setStudent_Answer(String student_Answer) {
        Student_Answer = student_Answer;
    }

    public String getStudent_ID() {
        return Student_ID;
    }

    public void setStudent_ID(String student_ID) {
        Student_ID = student_ID;
    }

    public Timestamp getSubmission_Time() {
        return Submission_Time;
    }

    public void setSubmission_Time(Timestamp submission_Time) {
        Submission_Time = submission_Time;
    }

    public Boolean getHas_Submitted() {
        return Has_Submitted;
    }

    public void setHas_Submitted(Boolean has_Submitted) {
        Has_Submitted = has_Submitted;
    }
}
