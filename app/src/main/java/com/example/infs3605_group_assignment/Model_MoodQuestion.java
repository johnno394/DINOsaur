package com.example.infs3605_group_assignment;

import java.util.ArrayList;

public class Model_MoodQuestion {

    private String question;
    private String option1;
    private String option2;
    private String option3;
    private String option4;



    public Model_MoodQuestion(String question, String option1, String option2, String option3, String option4){

        this.question = question;
        this.option1 = option1;
        this.option2 = option2;
        this.option3 = option3;
        this.option4 = option4;

    }


    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }


    public String getOption1() {
        return option1;
    }

    public void setOption1(String option1) {
        this.option1 = option1;
    }

    public String getOption2() {
        return option2;
    }

    public void setOption2(String option2) {
        this.option2 = option2;
    }

    public String getOption3() {
        return option3;
    }

    public void setOption3(String option3) {
        this.option3 = option3;
    }

    public String getOption4() {
        return option4;
    }

    public void setOption4(String option4) {
        this.option4 = option4;
    }


    public static ArrayList<Model_MoodQuestion> getMoodQuestions(){

        ArrayList<Model_MoodQuestion> questions = new ArrayList<>();


        //add questions here for MCQ
        questions.add(new Model_MoodQuestion("How many friends did you talk to today?", "0", "1-2", "3-5","6+" ));
        questions.add(new Model_MoodQuestion("How much outdoor time did you get today?", "None", "15 mins", "30 mins", "45 mins"));
        questions.add(new Model_MoodQuestion("Was class boring?", "Very", "A bit", "No", "Class was fun!"));
        questions.add(new Model_MoodQuestion("Were you nervous to come to school today?", "Very", "A bit", "No", "I love School!"));
        questions.add(new Model_MoodQuestion("Did you enjoy school today?", "Not at all", "Not really", "It was okay", "Yes!"));


        return questions;


    }



}
